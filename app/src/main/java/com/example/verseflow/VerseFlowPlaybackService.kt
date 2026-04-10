package com.example.verseflow

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.example.verseflow.data.CachedLyrics
import com.example.verseflow.data.DeviceAudioCatalog
import com.example.verseflow.data.DeviceAudioStoreLoader
import com.example.verseflow.data.LocalLyricsMetadataResolver
import com.example.verseflow.data.LyricsCacheStore
import com.example.verseflow.data.PlaybackSessionStore
import com.example.verseflow.data.SavedPlaybackSession
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.RepeatMode
import com.example.verseflow.model.Song
import com.example.verseflow.model.SongSource
import com.google.common.util.concurrent.Futures.immediateFuture
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class VerseFlowPlaybackService : MediaLibraryService() {

    private lateinit var player: ExoPlayer
    private var librarySession: MediaLibrarySession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var lyricsRefreshJob: Job? = null
    private var lastLyricsSnapshotKey: String? = null
    private var preservedSessionForTaskRemoval = false

    private val deviceAudioLoader by lazy { DeviceAudioStoreLoader(applicationContext) }
    private val lyricsCacheStore by lazy { LyricsCacheStore(applicationContext) }
    private val playbackSessionStore by lazy { PlaybackSessionStore(applicationContext) }

    @Volatile
    private var cachedCatalog: DeviceAudioCatalog? = null

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            setHandleAudioBecomingNoisy(true)
            addListener(
                object : Player.Listener {
                    override fun onEvents(player: Player, events: Player.Events) {
                        if (
                            events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                            events.contains(Player.EVENT_POSITION_DISCONTINUITY) ||
                            events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) ||
                            events.contains(Player.EVENT_REPEAT_MODE_CHANGED) ||
                            events.contains(Player.EVENT_TIMELINE_CHANGED)
                        ) {
                            persistPlaybackSession()
                            notifyLyricsChanged()
                            refreshLyricsTicker()
                        }
                        if (
                            events.contains(Player.EVENT_IS_PLAYING_CHANGED) ||
                            events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)
                        ) {
                            refreshLyricsTicker()
                        }
                    }
                },
            )
        }

        restorePlaybackSession()

        librarySession = MediaLibrarySession.Builder(
            this,
            player,
            VerseFlowLibraryCallback(),
        ).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = librarySession

    override fun onTaskRemoved(rootIntent: android.content.Intent?) {
        persistPlaybackSession()
        preservedSessionForTaskRemoval = true
        player.pause()
        player.stop()
        stopSelf()
    }

    override fun onDestroy() {
        lyricsRefreshJob?.cancel()
        serviceScope.cancel()
        if (!preservedSessionForTaskRemoval) {
            persistPlaybackSession()
        }
        librarySession?.release()
        librarySession = null
        player.release()
        super.onDestroy()
    }

    private inner class VerseFlowLibraryCallback : MediaLibrarySession.Callback {
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = if (hasAudioPermission()) {
                browsableItem(
                    mediaId = ROOT_ID,
                    title = "VerseFlow",
                    subtitle = "Browse your local music library",
                )
            } else {
                browsableItem(
                    mediaId = ROOT_ID,
                    title = "VerseFlow needs media access",
                    subtitle = "Open the phone app and grant audio permission",
                )
            }
            return immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<com.google.common.collect.ImmutableList<MediaItem>>> {
            val items = childrenFor(parentId)
            val pagedItems = paginate(items, page, pageSize)
            return immediateFuture(LibraryResult.ofItemList(pagedItems, params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val item = itemFor(mediaId)
                ?: return immediateFuture(
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE),
                )
            return immediateFuture(LibraryResult.ofItem(item, null))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): ListenableFuture<MutableList<MediaItem>> {
            val catalog = loadCatalog()
            val resolvedItems = mediaItems.mapNotNull { requested ->
                resolvePlayableItem(requested, catalog)
            }
            return immediateFuture(
                if (resolvedItems.isNotEmpty()) {
                    resolvedItems.toMutableList()
                } else {
                    mediaItems
                },
            )
        }
    }

    private fun childrenFor(parentId: String): List<MediaItem> {
        if (!hasAudioPermission()) {
            return listOf(
                infoItem(
                    mediaId = "$ROOT_ID:no_permission",
                    title = "Audio permission required",
                    subtitle = "Open VerseFlow on your phone and allow music access",
                ),
            )
        }

        val catalog = loadCatalog() ?: return emptyList()
        return when (parentId) {
            ROOT_ID -> rootChildren(catalog)
            RECENT_ID -> catalog.songs.take(6).map(::songItem)
            ARTISTS_ID -> catalog.artists.sortedBy { it.name.lowercase() }.map(::artistItem)
            ALBUMS_ID -> catalog.albums.sortedBy { it.title.lowercase() }.map(::albumItem)
            PLAYLISTS_ID -> catalog.playlists.sortedBy { it.title.lowercase() }.map(::playlistItem)
            SONGS_ID -> catalog.songs.sortedBy { it.title.lowercase() }.map(::songItem)
            LYRICS_ID -> lyricsExcerptItems(catalog)
            else -> when {
                parentId.startsWith(ARTIST_PREFIX) -> {
                    val artistId = parentId.removePrefix(ARTIST_PREFIX)
                    val artist = catalog.artists.firstOrNull { it.id == artistId } ?: return emptyList()
                    artist.topTrackIds.mapNotNull { trackId ->
                        catalog.songs.firstOrNull { it.id == trackId }
                    }.map(::songItem)
                }
                parentId.startsWith(ALBUM_PREFIX) -> {
                    val albumId = parentId.removePrefix(ALBUM_PREFIX)
                    val album = catalog.albums.firstOrNull { it.id == albumId } ?: return emptyList()
                    album.trackIds.mapNotNull { trackId ->
                        catalog.songs.firstOrNull { it.id == trackId }
                    }.map(::songItem)
                }
                parentId.startsWith(PLAYLIST_PREFIX) -> {
                    val playlistId = parentId.removePrefix(PLAYLIST_PREFIX)
                    val playlist = catalog.playlists.firstOrNull { it.id == playlistId } ?: return emptyList()
                    playlist.trackIds.mapNotNull { trackId ->
                        catalog.songs.firstOrNull { it.id == trackId }
                    }.map(::songItem)
                }
                else -> emptyList()
            }
        }
    }

    private fun itemFor(mediaId: String): MediaItem? {
        if (!hasAudioPermission()) {
            return infoItem(
                mediaId = "$ROOT_ID:no_permission",
                title = "Audio permission required",
                subtitle = "Open VerseFlow on your phone and allow music access",
            )
        }

        val catalog = loadCatalog() ?: return null
        return when (mediaId) {
            ROOT_ID -> browsableItem(ROOT_ID, "VerseFlow", "Browse your local music library")
            RECENT_ID -> browsableItem(RECENT_ID, "Recently played", "Jump back into recent tracks")
            ARTISTS_ID -> browsableItem(ARTISTS_ID, "Artists", "${catalog.artists.size} artists")
            ALBUMS_ID -> browsableItem(ALBUMS_ID, "Albums", "${catalog.albums.size} albums")
            PLAYLISTS_ID -> browsableItem(PLAYLISTS_ID, "Playlists", "${catalog.playlists.size} playlists")
            SONGS_ID -> browsableItem(SONGS_ID, "Songs", "${catalog.songs.size} songs")
            LYRICS_ID -> browsableItem(LYRICS_ID, "Lyrics excerpt", "Show the current song and a few lyric lines")
            else -> when {
                mediaId.startsWith(ARTIST_PREFIX) -> {
                    val artistId = mediaId.removePrefix(ARTIST_PREFIX)
                    catalog.artists.firstOrNull { it.id == artistId }?.let(::artistItem)
                }
                mediaId.startsWith(ALBUM_PREFIX) -> {
                    val albumId = mediaId.removePrefix(ALBUM_PREFIX)
                    catalog.albums.firstOrNull { it.id == albumId }?.let(::albumItem)
                }
                mediaId.startsWith(PLAYLIST_PREFIX) -> {
                    val playlistId = mediaId.removePrefix(PLAYLIST_PREFIX)
                    catalog.playlists.firstOrNull { it.id == playlistId }?.let(::playlistItem)
                }
                else -> catalog.songs.firstOrNull { it.id == mediaId }?.let(::songItem)
            }
        }
    }

    private fun resolvePlayableItem(
        requested: MediaItem,
        catalog: DeviceAudioCatalog?,
    ): MediaItem? {
        if (requested.localConfiguration != null) return requested
        val songs = catalog?.songs.orEmpty()
        return songs.firstOrNull { it.id == requested.mediaId }?.let(::songItem)
    }

    private fun rootChildren(catalog: DeviceAudioCatalog): List<MediaItem> = listOf(
        browsableItem(RECENT_ID, "Recently played", "Your latest device tracks"),
        browsableItem(ARTISTS_ID, "Artists", "${catalog.artists.size} artists"),
        browsableItem(ALBUMS_ID, "Albums", "${catalog.albums.size} albums"),
        browsableItem(PLAYLISTS_ID, "Playlists", "${catalog.playlists.size} playlists"),
        browsableItem(SONGS_ID, "Songs", "${catalog.songs.size} songs"),
        browsableItem(LYRICS_ID, "Lyrics excerpt", "Current track plus a short lyrics preview"),
    )

    private fun songItem(song: Song): MediaItem {
        val catalog = cachedCatalog
        val artistName = catalog?.artists?.firstOrNull { it.id == song.artistId }?.name.orEmpty()
        val albumTitle = catalog?.albums?.firstOrNull { it.id == song.albumId }?.title.orEmpty()
        return MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(song.mediaUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(artistName)
                    .setAlbumTitle(albumTitle)
                    .setArtworkUri(song.artworkUri?.let(Uri::parse))
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .build(),
            )
            .build()
    }

    private fun artistItem(artist: Artist): MediaItem =
        browsableItem(
            mediaId = "$ARTIST_PREFIX${artist.id}",
            title = artist.name,
            subtitle = "${artist.topTrackIds.size} top tracks",
        )

    private fun albumItem(album: Album): MediaItem {
        val artistName = cachedCatalog?.artists?.firstOrNull { it.id == album.artistId }?.name.orEmpty()
        return browsableItem(
            mediaId = "$ALBUM_PREFIX${album.id}",
            title = album.title,
            subtitle = if (artistName.isBlank()) "${album.trackIds.size} tracks" else "$artistName • ${album.trackIds.size} tracks",
        )
    }

    private fun playlistItem(playlist: Playlist): MediaItem =
        browsableItem(
            mediaId = "$PLAYLIST_PREFIX${playlist.id}",
            title = playlist.title,
            subtitle = "${playlist.trackIds.size} tracks",
        )

    private fun lyricsExcerptItems(catalog: DeviceAudioCatalog): List<MediaItem> {
        val currentSong = currentSong(catalog)
        if (currentSong == null) {
            return listOf(
                infoItem(
                    mediaId = "$LYRICS_ID:empty",
                    title = "Nothing is playing",
                    subtitle = "Start a song to see a lyrics excerpt here",
                ),
            )
        }

        val artistName = catalog.artists.firstOrNull { it.id == currentSong.artistId }?.name.orEmpty()
        val header = infoItem(
            mediaId = "$LYRICS_ID:current",
            title = currentSong.title,
            subtitle = artistName.ifBlank { "Current track" },
        )
        val excerptLines = loadLyricsExcerpt(currentSong)
        if (excerptLines.isEmpty()) {
            return listOf(
                header,
                infoItem(
                    mediaId = "$LYRICS_ID:none",
                    title = "No lyrics available yet",
                    subtitle = "Open the phone lyrics screen to fetch or choose lyrics for this song",
                ),
            )
        }

        val liveLine = excerptLines.firstOrNull().orEmpty()
        return listOf(
            header,
            infoItem(
                mediaId = "$LYRICS_ID:line:0",
                title = liveLine.take(MAX_LYRICS_LINE_LENGTH),
                subtitle = "Live lyric",
            ),
        )
    }

    private fun loadLyricsExcerpt(song: Song): List<String> {
        val cachedLyrics = lyricsCacheStore.load(song.mediaUri)
        val cachedLines = cachedLyrics.toExcerptLines(player.currentPosition.coerceAtLeast(0L))
        if (cachedLines.isNotEmpty()) return cachedLines

        if (song.source != SongSource.Local || song.mediaUri.isNullOrBlank()) return emptyList()
        return runBlocking {
            LocalLyricsMetadataResolver.loadPlainLyrics(
                context = applicationContext,
                mediaUri = song.mediaUri,
            )
        }.filter { it.isNotBlank() }
    }

    private fun CachedLyrics?.toExcerptLines(positionMs: Long): List<String> {
        if (this == null) return emptyList()
        val syncedLines = syncedLyrics.filter { it.text.isNotBlank() }
        if (syncedLines.isNotEmpty()) {
            val activeIndex = syncedLines.indexOfLast { it.timestampMs <= positionMs }
                .takeIf { it >= 0 }
                ?: 0
            return listOf("Now: ${syncedLines[activeIndex].text}")
        }
        return plainLyrics.firstOrNull(String::isNotBlank)?.let(::listOf).orEmpty()
    }

    private fun refreshLyricsTicker() {
        lyricsRefreshJob?.cancel()
        if (!player.isPlaying) return
        lyricsRefreshJob = serviceScope.launch {
            while (isActive && player.isPlaying) {
                notifyLyricsChanged()
                delay(LYRICS_REFRESH_INTERVAL_MS)
            }
        }
    }

    private fun notifyLyricsChanged() {
        val catalog = cachedCatalog ?: return
        val currentSong = currentSong(catalog)
        val snapshotKey = buildLyricsSnapshotKey(currentSong)
        if (snapshotKey == lastLyricsSnapshotKey) return
        lastLyricsSnapshotKey = snapshotKey
        librarySession?.notifyChildrenChanged(
            LYRICS_ID,
            lyricsExcerptItems(catalog).size,
            null,
        )
    }

    private fun buildLyricsSnapshotKey(song: Song?): String {
        if (song == null) return "empty"
        val cachedLyrics = lyricsCacheStore.load(song.mediaUri)
        val syncedLines = cachedLyrics?.syncedLyrics.orEmpty().filter { it.text.isNotBlank() }
        val activeLineIndex = if (syncedLines.isNotEmpty()) {
            syncedLines.indexOfLast { it.timestampMs <= player.currentPosition.coerceAtLeast(0L) }
                .takeIf { it >= 0 }
                ?: 0
        } else {
            -1
        }
        return "${song.id}:$activeLineIndex:${player.isPlaying}"
    }

    private fun currentSong(catalog: DeviceAudioCatalog): Song? {
        val currentMediaId = player.currentMediaItem?.mediaId
        if (!currentMediaId.isNullOrBlank()) {
            catalog.songs.firstOrNull { it.id == currentMediaId }?.let { return it }
        }

        val savedSession = playbackSessionStore.load()
        return savedSession?.currentSongId?.let { currentSongId ->
            catalog.songs.firstOrNull { it.id == currentSongId }
        } ?: savedSession?.currentSongMediaUri?.let { currentSongUri ->
            catalog.songs.firstOrNull { it.mediaUri == currentSongUri }
        }
    }

    private fun loadCatalog(): DeviceAudioCatalog? {
        if (!hasAudioPermission()) return null
        cachedCatalog?.let { cached ->
            if (cached.songs.isNotEmpty()) return cached
        }
        val loadedCatalog = runBlocking { deviceAudioLoader.load() }
        cachedCatalog = loadedCatalog
        return loadedCatalog
    }

    private fun restorePlaybackSession() {
        val savedSession = playbackSessionStore.load() ?: return
        val catalog = loadCatalog() ?: return
        val songsById = catalog.songs.associateBy(Song::id)
        val songsByMediaUri = catalog.songs.associateBy { it.mediaUri.orEmpty() }
        val resolvedQueue = savedSession.queueSongIds
            .mapNotNull(songsById::get)
            .ifEmpty {
                savedSession.queueSongMediaUris.mapNotNull { mediaUri ->
                    songsByMediaUri[mediaUri]
                }
            }
            .distinctBy(Song::id)
        if (resolvedQueue.isEmpty()) return

        val currentSong = savedSession.currentSongId?.let(songsById::get)
            ?: savedSession.currentSongMediaUri?.let { songsByMediaUri[it] }
            ?: resolvedQueue.getOrNull(savedSession.currentIndex)
            ?: resolvedQueue.firstOrNull()
            ?: return

        val startIndex = resolvedQueue.indexOfFirst { it.id == currentSong.id }.coerceAtLeast(0)
        val mediaItems = resolvedQueue.map(::songItem)
        player.setMediaItems(
            mediaItems,
            startIndex,
            savedSession.positionMs.coerceAtLeast(0L),
        )
        player.repeatMode = savedSession.repeatMode.toPlayerRepeatMode()
        player.prepare()
    }

    private fun persistPlaybackSession() {
        val currentItem = player.currentMediaItem ?: run {
            playbackSessionStore.clear()
            return
        }
        val queueIds = (0 until player.mediaItemCount).mapNotNull { index ->
            player.getMediaItemAt(index).mediaId.takeIf(String::isNotBlank)
        }
        val queueUris = (0 until player.mediaItemCount).mapNotNull { index ->
            player.getMediaItemAt(index).localConfiguration?.uri?.toString()?.takeIf(String::isNotBlank)
        }
        if (queueIds.isEmpty() && queueUris.isEmpty()) {
            playbackSessionStore.clear()
            return
        }

        playbackSessionStore.save(
            SavedPlaybackSession(
                currentSongId = currentItem.mediaId.takeIf(String::isNotBlank),
                currentSongMediaUri = currentItem.localConfiguration?.uri?.toString(),
                queueSongIds = queueIds,
                queueSongMediaUris = queueUris,
                currentIndex = player.currentMediaItemIndex.coerceAtLeast(0),
                positionMs = player.currentPosition.coerceAtLeast(0L),
                repeatMode = player.repeatMode.toVerseFlowRepeatMode(),
                isShuffled = player.shuffleModeEnabled,
                lyricsDisplayMode = com.example.verseflow.model.LyricsDisplayMode.Plain,
            ),
        )
    }

    private fun hasAudioPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun paginate(
        items: List<MediaItem>,
        page: Int,
        pageSize: Int,
    ): List<MediaItem> {
        if (items.isEmpty()) return emptyList()
        if (page < 0 || pageSize <= 0) return items
        val fromIndex = page * pageSize
        if (fromIndex >= items.size) return emptyList()
        val toIndex = (fromIndex + pageSize).coerceAtMost(items.size)
        return items.subList(fromIndex, toIndex)
    }

    private fun browsableItem(
        mediaId: String,
        title: String,
        subtitle: String?,
    ): MediaItem = MediaItem.Builder()
        .setMediaId(mediaId)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setIsBrowsable(true)
                .setIsPlayable(false)
                .build(),
        )
        .build()

    private fun infoItem(
        mediaId: String,
        title: String,
        subtitle: String?,
    ): MediaItem = MediaItem.Builder()
        .setMediaId(mediaId)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setIsBrowsable(false)
                .setIsPlayable(false)
                .build(),
        )
        .build()

    private fun Int.toVerseFlowRepeatMode(): RepeatMode = when (this) {
        Player.REPEAT_MODE_ONE -> RepeatMode.One
        Player.REPEAT_MODE_ALL -> RepeatMode.All
        else -> RepeatMode.Off
    }

    private fun RepeatMode.toPlayerRepeatMode(): Int = when (this) {
        RepeatMode.Off -> Player.REPEAT_MODE_OFF
        RepeatMode.All -> Player.REPEAT_MODE_ALL
        RepeatMode.One -> Player.REPEAT_MODE_ONE
    }

    private companion object {
        const val ROOT_ID = "root"
        const val RECENT_ID = "recent"
        const val ARTISTS_ID = "artists"
        const val ALBUMS_ID = "albums"
        const val PLAYLISTS_ID = "playlists"
        const val SONGS_ID = "songs"
        const val LYRICS_ID = "lyrics_excerpt"

        const val ARTIST_PREFIX = "artist:"
        const val ALBUM_PREFIX = "album:"
        const val PLAYLIST_PREFIX = "playlist:"

        const val MAX_LYRICS_LINE_LENGTH = 120
        const val LYRICS_REFRESH_INTERVAL_MS = 1_500L
    }
}
