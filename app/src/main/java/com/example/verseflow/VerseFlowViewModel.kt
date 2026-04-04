package com.example.verseflow

import android.Manifest
import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.verseflow.data.DeviceAudioCatalog
import com.example.verseflow.data.DeviceAudioStoreLoader
import com.example.verseflow.data.LibraryCustomizationStore
import com.example.verseflow.data.LrcLibLyricsRepository
import com.example.verseflow.data.LyricsCacheStore
import com.example.verseflow.data.LocalLyricsMetadataResolver
import com.example.verseflow.data.LyricsLookupResult
import com.example.verseflow.data.MockMusicRepository
import com.example.verseflow.data.MusicRepository
import com.example.verseflow.data.PlaybackSessionStore
import com.example.verseflow.data.SavedPlaybackSession
import com.example.verseflow.data.SongMetadataOverride
import com.example.verseflow.data.UserPreferencesStore
import com.example.verseflow.data.preferredArtistQuery
import com.example.verseflow.data.preferredTitleQuery
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.LibraryFilter
import com.example.verseflow.model.LibrarySort
import com.example.verseflow.model.LibraryTab
import com.example.verseflow.model.LyricsLoadState
import com.example.verseflow.model.LyricsDisplayMode
import com.example.verseflow.model.LyricsSearchCandidate
import com.example.verseflow.model.ManualLyricsSearchUiState
import com.example.verseflow.model.MusicCatalogSource
import com.example.verseflow.model.PlaybackUiState
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.RepeatMode
import com.example.verseflow.model.Song
import com.example.verseflow.model.SongSource
import com.example.verseflow.model.ThemePreset
import com.example.verseflow.model.UserSettings
import com.example.verseflow.model.VerseFlowUiState
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class SongActionUiState(
    val removeFromVerseFlowSongId: String? = null,
    val deleteFromDeviceSongId: String? = null,
    val editMusicInfoSongId: String? = null,
    val pendingSystemDeleteSongId: String? = null,
    val noticeTitle: String? = null,
    val noticeMessage: String? = null,
)

class VerseFlowViewModel(
    application: Application,
    private val repository: MusicRepository = MockMusicRepository(),
) : AndroidViewModel(application) {

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VerseFlowViewModel(application)
            }
        }
    }

    private val deviceAudioLoader = DeviceAudioStoreLoader(application.applicationContext)
    private val lyricsRepository = LrcLibLyricsRepository()
    private val lyricsCacheStore = LyricsCacheStore(application.applicationContext)
    private val playbackSessionStore = PlaybackSessionStore(application.applicationContext)
    private val libraryCustomizationStore = LibraryCustomizationStore(application.applicationContext)
    private val userPreferencesStore = UserPreferencesStore(application.applicationContext)
    private var player: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val defaultProfile = userPreferencesStore.load(repository.profile())
    private val persistedLibraryCustomizations = libraryCustomizationStore.load()
    private val initialAudioPermissionGranted = application.hasAudioPermission()
    private val emptyCatalog = emptyCatalogData()
    private var activeCatalog = emptyCatalog
    private var pendingPlaybackSession: SavedPlaybackSession? = playbackSessionStore.load()
    private var hiddenSongIds: Set<String> = persistedLibraryCustomizations.hiddenSongIds
    private var songMetadataOverrides: Map<String, SongMetadataOverride> = persistedLibraryCustomizations.songMetadataOverrides
    private var customPlaylists: List<Playlist> = emptyList()
    private var playlistTrackOverrides: Map<String, List<String>> = emptyMap()
    private var removedPlaylistIds: Set<String> = emptySet()

    var uiState by mutableStateOf(
        buildStateFromCatalog(
            catalog = activeCatalog.applyLibraryCustomizations(hiddenSongIds, songMetadataOverrides),
            previousState = null,
            audioPermissionGranted = initialAudioPermissionGranted,
            hasScannedDeviceAudio = false,
            isScanningDeviceAudio = false,
            catalogSource = MusicCatalogSource.Device,
            playback = initialPlaybackFor(
                songs = activeCatalog.applyLibraryCustomizations(hiddenSongIds, songMetadataOverrides).songs,
                syncedLyricsDefault = defaultProfile.settings.showSyncedLyricsByDefault,
                shouldAutoplay = false,
            ),
        ),
    )
        private set

    var songActionUiState by mutableStateOf(SongActionUiState())
        private set

    private var playbackTicker: Job? = null
    private val lyricsJobs = mutableMapOf<String, Job>()
    private val lyricsUpgradeAttempts = mutableSetOf<String>()
    private var manualLyricsSearchJob: Job? = null

    init {
        initializeMediaController()
        startPlaybackTicker()
        if (initialAudioPermissionGranted) {
            refreshDeviceLibrary()
        }
    }

    private fun initializeMediaController() {
        val context = getApplication<Application>().applicationContext
        val sessionToken = SessionToken(
            context,
            ComponentName(context, VerseFlowPlaybackService::class.java),
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync().also { future ->
            future.addListener(
                {
                    val controller = runCatching { future.get() }.getOrNull() ?: return@addListener
                    player = controller
                    controller.repeatMode = uiState.playback.repeatMode.toPlayerRepeatMode()
                    controller.addListener(
                        object : Player.Listener {
                            override fun onEvents(player: Player, events: Player.Events) {
                                if (
                                    events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                                    events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                                    events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) ||
                                    events.contains(Player.EVENT_IS_PLAYING_CHANGED) ||
                                    events.contains(Player.EVENT_POSITION_DISCONTINUITY)
                                ) {
                                    syncFromPlayer()
                                }
                            }
                        },
                    )
                    if (usesLocalPlayback(uiState.playback) && uiState.playback.queue.isNotEmpty()) {
                        synchronizePlayerQueue(
                            playback = uiState.playback,
                            startPositionMs = uiState.playback.positionMs,
                            playWhenReady = uiState.playback.isPlaying,
                        )
                    }
                },
                ContextCompat.getMainExecutor(context),
            )
        }
    }

    fun onAudioPermissionChanged(granted: Boolean) {
        if (granted) {
            if (uiState.audioPermissionGranted && uiState.hasScannedDeviceAudio) return
            uiState = uiState.copy(audioPermissionGranted = true)
            refreshDeviceLibrary()
            return
        }

        if (!uiState.audioPermissionGranted && !uiState.hasScannedDeviceAudio) {
            return
        }

        cancelLyricsRequests()
        stopLocalPlayback()
        activeCatalog = emptyCatalog
        val effectiveCatalog = activeCatalog.applyLibraryCustomizations(hiddenSongIds, songMetadataOverrides)
        uiState = buildStateFromCatalog(
            catalog = effectiveCatalog,
            previousState = uiState,
            audioPermissionGranted = false,
            hasScannedDeviceAudio = false,
            isScanningDeviceAudio = false,
            catalogSource = MusicCatalogSource.Device,
            playback = initialPlaybackFor(
                songs = effectiveCatalog.songs,
                syncedLyricsDefault = uiState.profile.settings.showSyncedLyricsByDefault,
                shouldAutoplay = false,
                likedSongIds = uiState.playback.likedSongIds,
            ),
        )
    }

    fun refreshDeviceLibrary() {
        if (!uiState.audioPermissionGranted || uiState.isScanningDeviceAudio) return

        viewModelScope.launch {
            uiState = uiState.copy(isScanningDeviceAudio = true)
            val deviceCatalog = deviceAudioLoader.load()
            stopLocalPlayback()
            activeCatalog = deviceCatalog.toCatalogData()
            val effectiveCatalog = activeCatalog.applyLibraryCustomizations(hiddenSongIds, songMetadataOverrides)
            val localPlayback = restoredPlaybackFor(
                songs = effectiveCatalog.songs,
                syncedLyricsDefault = uiState.profile.settings.showSyncedLyricsByDefault,
                likedSongIds = uiState.playback.likedSongIds,
            )
            uiState = buildStateFromCatalog(
                catalog = effectiveCatalog,
                previousState = uiState,
                audioPermissionGranted = true,
                hasScannedDeviceAudio = true,
                isScanningDeviceAudio = false,
                catalogSource = MusicCatalogSource.Device,
                playback = localPlayback,
            )
            synchronizePlayerQueue(
                playback = localPlayback,
                startPositionMs = localPlayback.positionMs,
                playWhenReady = false,
            )
            localPlayback.currentSong?.let { ensureLyricsForSong(it.id) }
            pendingPlaybackSession = null
        }
    }

    private fun startPlaybackTicker() {
        playbackTicker?.cancel()
        playbackTicker = viewModelScope.launch {
            while (isActive) {
                delay(450L)
                val playback = uiState.playback
                val currentSong = playback.currentSong ?: continue

                if (usesLocalPlayback(playback)) {
                    syncFromPlayer()
                    continue
                }

                if (!playback.isPlaying) continue
                val nextPosition = playback.positionMs + 450L
                if (nextPosition >= currentSong.durationMs) {
                    onMockTrackCompleted()
                } else {
                    updatePlayback(playback.copy(positionMs = nextPosition))
                }
            }
        }
    }

    private fun onMockTrackCompleted() {
        val playback = uiState.playback
        when {
            playback.repeatMode == RepeatMode.One -> updatePlayback(playback.copy(positionMs = 0L))
            playback.currentIndex < playback.queue.lastIndex -> {
                updatePlayback(playback.copy(currentIndex = playback.currentIndex + 1, positionMs = 0L))
            }
            playback.repeatMode == RepeatMode.All && playback.queue.isNotEmpty() -> {
                updatePlayback(playback.copy(currentIndex = 0, positionMs = 0L))
            }
            else -> updatePlayback(playback.copy(isPlaying = false))
        }
    }

    private fun syncFromPlayer() {
        val playback = uiState.playback
        val controller = player ?: return
        if (!usesLocalPlayback(playback) || playback.queue.isEmpty()) return

        val index = controller.currentMediaItemIndex
            .takeIf { it in playback.queue.indices }
            ?: playback.currentIndex

        updatePlayback(
            playback.copy(
                currentIndex = index,
                positionMs = controller.currentPosition.coerceAtLeast(0L),
                isPlaying = controller.isPlaying,
            ),
        )
    }

    private fun usesLocalPlayback(playback: PlaybackUiState = uiState.playback): Boolean =
        playback.currentSong?.source == SongSource.Local && playback.queue.all { it.mediaUri != null }

    private fun synchronizePlayerQueue(
        playback: PlaybackUiState,
        startPositionMs: Long = playback.positionMs,
        playWhenReady: Boolean = playback.isPlaying,
    ) {
        val controller = player
        if (controller == null) return

        if (!usesLocalPlayback(playback)) {
            stopLocalPlayback()
            return
        }

        val mediaItems = playback.queue.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id)
                .setUri(song.mediaUri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(uiState.artistsById[song.artistId]?.name.orEmpty())
                        .setAlbumTitle(uiState.albumsById[song.albumId]?.title.orEmpty())
                        .setArtworkUri(song.artworkUri?.let(Uri::parse))
                        .build(),
                )
                .build()
        }
        controller.setMediaItems(
            mediaItems,
            playback.currentIndex.coerceIn(0, mediaItems.lastIndex),
            startPositionMs.coerceAtLeast(0L),
        )
        controller.repeatMode = playback.repeatMode.toPlayerRepeatMode()
        controller.prepare()
        controller.playWhenReady = playWhenReady
    }

    private fun stopLocalPlayback() {
        val controller = player ?: return
        controller.playWhenReady = false
        controller.stop()
        controller.clearMediaItems()
    }

    private fun updatePlayback(playback: PlaybackUiState) {
        val previousSongId = uiState.playback.currentSong?.id
        val nextSongId = playback.currentSong?.id
        val shouldResetManualSearch = previousSongId != nextSongId
        if (shouldResetManualSearch) {
            manualLyricsSearchJob?.cancel()
            manualLyricsSearchJob = null
        }
        uiState = uiState.copy(
            playback = playback,
            manualLyricsSearch = if (shouldResetManualSearch) {
                ManualLyricsSearchUiState()
            } else {
                uiState.manualLyricsSearch
            },
        )
        val currentSongId = uiState.playback.currentSong?.id
        if (currentSongId != null && currentSongId != previousSongId) {
            ensureLyricsForSong(currentSongId)
        }
        persistPlaybackSession()
    }

    private fun ensureLyricsForSong(songId: String) {
        val song = uiState.songsById[songId] ?: return
        if (song.source != SongSource.Local) return
        if (lyricsJobs[songId]?.isActive == true) return

        if (song.lyrics.isEmpty() && song.plainLyrics.isEmpty()) {
            lyricsCacheStore.load(song.mediaUri)?.let { cachedLyrics ->
                applySongUpdate(songId) { existing ->
                    existing.copy(
                        lyrics = cachedLyrics.syncedLyrics,
                        plainLyrics = if (cachedLyrics.plainLyrics.isNotEmpty()) {
                            cachedLyrics.plainLyrics
                        } else {
                            cachedLyrics.syncedLyrics.map { it.text }
                        },
                        lyricsAttribution = cachedLyrics.attribution,
                    )
                }
                updateLyricsStatus(songId, LyricsLoadState.Ready)
                return
            }
        }

        val currentStatus = uiState.lyricsStatusBySongId[songId]
        val hasSyncedLyrics = song.lyrics.isNotEmpty()
        val hasPlainLyrics = song.plainLyrics.isNotEmpty()
        if (hasSyncedLyrics || hasPlainLyrics) {
            if (hasSyncedLyrics) {
                lyricsUpgradeAttempts.remove(songId)
            }
            if (!hasSyncedLyrics && hasPlainLyrics && lyricsUpgradeAttempts.add(songId)) {
                updateLyricsStatus(songId, LyricsLoadState.Loading)
            } else {
                if (currentStatus != LyricsLoadState.Ready) {
                    updateLyricsStatus(songId, LyricsLoadState.Ready)
                }
                return
            }
        }
        if (currentStatus == LyricsLoadState.Loading || currentStatus == LyricsLoadState.Unavailable) {
            if (!(hasPlainLyrics && !hasSyncedLyrics && lyricsUpgradeAttempts.contains(songId))) {
                return
            }
        }

        if (!hasSyncedLyrics && !hasPlainLyrics) {
            updateLyricsStatus(songId, LyricsLoadState.Loading)
        } else if (currentStatus != LyricsLoadState.Loading) {
            updateLyricsStatus(songId, LyricsLoadState.Loading)
        }
        val artistName = uiState.artistsById[song.artistId]?.name.orEmpty()
        val albumTitle = uiState.albumsById[song.albumId]?.title
        lyricsJobs[songId] = viewModelScope.launch {
            val embeddedPlainLyrics = if (!hasSyncedLyrics && !hasPlainLyrics) {
                LocalLyricsMetadataResolver.loadPlainLyrics(
                    context = getApplication<Application>().applicationContext,
                    mediaUri = song.mediaUri,
                )
            } else {
                emptyList()
            }
            if (embeddedPlainLyrics.isNotEmpty()) {
                applySongUpdate(songId) { existing ->
                    existing.copy(
                        plainLyrics = embeddedPlainLyrics,
                        lyricsAttribution = "Embedded lyrics from local file",
                    )
                }
                lyricsCacheStore.save(
                    mediaUri = song.mediaUri,
                    syncedLyrics = emptyList(),
                    plainLyrics = embeddedPlainLyrics,
                    attribution = "Embedded lyrics from local file",
                )
            }

            when (
                val result = lyricsRepository.lookup(
                    title = song.title,
                    artistName = artistName,
                    albumTitle = albumTitle,
                    durationMs = song.durationMs,
                )
            ) {
                is LyricsLookupResult.Found -> {
                    applySongUpdate(songId) { existing ->
                        existing.copy(
                            lyrics = result.syncedLyrics,
                            plainLyrics = result.plainLyrics,
                            lyricsAttribution = result.attribution,
                        )
                    }
                    lyricsCacheStore.save(
                        mediaUri = song.mediaUri,
                        syncedLyrics = result.syncedLyrics,
                        plainLyrics = result.plainLyrics,
                        attribution = result.attribution,
                    )
                    if (result.syncedLyrics.isNotEmpty()) {
                        lyricsUpgradeAttempts.remove(songId)
                    }
                    updateLyricsStatus(songId, LyricsLoadState.Ready)
                }

                LyricsLookupResult.NotFound -> {
                    if (embeddedPlainLyrics.isNotEmpty()) {
                        updateLyricsStatus(songId, LyricsLoadState.Ready)
                    } else {
                        applySongUpdate(songId) { existing ->
                            if (existing.source != SongSource.Local) {
                                existing
                            } else {
                                existing.copy(
                                    lyrics = emptyList(),
                                    plainLyrics = emptyList(),
                                    lyricsAttribution = null,
                                )
                            }
                        }
                        updateLyricsStatus(songId, LyricsLoadState.Unavailable)
                    }
                    lyricsUpgradeAttempts.remove(songId)
                }
            }
            lyricsJobs.remove(songId)
        }
    }

    private fun applySongUpdate(
        songId: String,
        transform: (Song) -> Song,
    ) {
        val currentSong = uiState.songsById[songId] ?: return
        val updatedSong = transform(currentSong)
        if (updatedSong == currentSong) return
        val baseSong = activeCatalog.songs.firstOrNull { it.id == songId }
        if (baseSong != null) {
            activeCatalog = activeCatalog.replaceSong(
                updatedSong.copy(
                    artistId = baseSong.artistId,
                    albumId = baseSong.albumId,
                ),
            )
        }

        fun List<Song>.replaceSong(): List<Song> = map { song ->
            if (song.id == songId) updatedSong else song
        }

        uiState = uiState.copy(
            songs = uiState.songs.replaceSong(),
            songsById = uiState.songsById + (songId to updatedSong),
            recentlyPlayed = uiState.recentlyPlayed.replaceSong(),
            trendingSongs = uiState.trendingSongs.replaceSong(),
            playback = uiState.playback.copy(
                queue = uiState.playback.queue.replaceSong(),
                canonicalQueue = uiState.playback.canonicalQueue.replaceSong(),
            ),
        )
    }

    private fun updateLyricsStatus(
        songId: String,
        status: LyricsLoadState,
    ) {
        uiState = uiState.copy(
            lyricsStatusBySongId = uiState.lyricsStatusBySongId + (songId to status),
        )
    }

    private fun cancelLyricsRequests() {
        lyricsJobs.values.forEach(Job::cancel)
        lyricsJobs.clear()
    }

    fun requestRemoveFromVerseFlow(songId: String) {
        if (uiState.songsById[songId] == null) return
        songActionUiState = songActionUiState.copy(removeFromVerseFlowSongId = songId)
    }

    fun dismissRemoveFromVerseFlow() {
        songActionUiState = songActionUiState.copy(removeFromVerseFlowSongId = null)
    }

    fun confirmRemoveFromVerseFlow() {
        val songId = songActionUiState.removeFromVerseFlowSongId ?: return
        hiddenSongIds = hiddenSongIds + songId
        libraryCustomizationStore.saveHiddenSongIds(hiddenSongIds)
        dismissRemoveFromVerseFlow()
        rebuildStateFromActiveCatalog()
    }

    fun requestDeleteFromDevice(songId: String) {
        val song = uiState.songsById[songId] ?: return
        if (song.source != SongSource.Local || song.mediaUri.isNullOrBlank()) {
            showSongActionNotice(
                title = "Delete unavailable",
                message = "Device deletion is only available for local songs imported from this phone.",
            )
            return
        }
        songActionUiState = songActionUiState.copy(deleteFromDeviceSongId = songId)
    }

    fun dismissDeleteFromDevice() {
        songActionUiState = songActionUiState.copy(deleteFromDeviceSongId = null)
    }

    fun confirmDeleteFromDevice() {
        val songId = songActionUiState.deleteFromDeviceSongId ?: return
        songActionUiState = songActionUiState.copy(
            deleteFromDeviceSongId = null,
            pendingSystemDeleteSongId = songId,
        )
    }

    fun dismissSongActionNotice() {
        songActionUiState = songActionUiState.copy(
            noticeTitle = null,
            noticeMessage = null,
        )
    }

    fun completePendingDeleteLaunch() {
        if (songActionUiState.pendingSystemDeleteSongId == null) return
        songActionUiState = songActionUiState.copy(pendingSystemDeleteSongId = null)
    }

    fun performLegacyDeviceDelete(songId: String) {
        val song = uiState.songsById[songId] ?: run {
            completePendingDeleteLaunch()
            return
        }
        val mediaUri = song.mediaUri?.let(Uri::parse) ?: run {
            completePendingDeleteLaunch()
            showSongActionNotice(
                title = "Delete unavailable",
                message = "This track does not expose a deletable media file on the device.",
            )
            return
        }
        val deleted = runCatching {
            getApplication<Application>().contentResolver.delete(mediaUri, null, null) > 0
        }.getOrDefault(false)
        onDeviceDeleteResult(songId, deleted)
    }

    fun onDeviceDeleteResult(
        songId: String,
        deleted: Boolean,
    ) {
        completePendingDeleteLaunch()
        if (!deleted) {
            showSongActionNotice(
                title = "Delete cancelled",
                message = "VerseFlow kept the song in your library because Android did not confirm the delete request.",
            )
            return
        }

        val deletedSong = activeCatalog.songs.firstOrNull { it.id == songId } ?: uiState.songsById[songId]
        hiddenSongIds = hiddenSongIds - songId
        songMetadataOverrides = songMetadataOverrides - songId
        libraryCustomizationStore.saveHiddenSongIds(hiddenSongIds)
        libraryCustomizationStore.saveSongMetadataOverrides(songMetadataOverrides)
        lyricsCacheStore.remove(deletedSong?.mediaUri)
        activeCatalog = activeCatalog.removeSong(songId)
        rebuildStateFromActiveCatalog()

        if (uiState.catalogSource == MusicCatalogSource.Device && uiState.audioPermissionGranted) {
            refreshDeviceLibrary()
        }
    }

    fun requestEditMusicInfo(songId: String) {
        if (uiState.songsById[songId] == null) return
        songActionUiState = songActionUiState.copy(editMusicInfoSongId = songId)
    }

    fun dismissEditMusicInfo() {
        songActionUiState = songActionUiState.copy(editMusicInfoSongId = null)
    }

    fun saveEditedMusicInfo(
        songId: String,
        title: String,
        artistName: String,
        albumTitle: String,
        genre: String,
    ) {
        val baseSong = activeCatalog.songs.firstOrNull { it.id == songId } ?: return
        val baseArtistName = activeCatalog.artists.firstOrNull { it.id == baseSong.artistId }?.name.orEmpty()
        val baseAlbumTitle = activeCatalog.albums.firstOrNull { it.id == baseSong.albumId }?.title.orEmpty()

        val sanitizedOverride = SongMetadataOverride(
            title = title.trim().takeIf { it.isNotBlank() && !it.equals(baseSong.title, ignoreCase = false) },
            artistName = artistName.trim().takeIf { it.isNotBlank() && !it.equals(baseArtistName, ignoreCase = false) },
            albumTitle = albumTitle.trim().takeIf { it.isNotBlank() && !it.equals(baseAlbumTitle, ignoreCase = false) },
            genre = genre.trim().takeIf { candidate ->
                candidate.isNotBlank() && candidate != (baseSong.genre ?: "")
            },
        )

        songMetadataOverrides = if (sanitizedOverride.isEmpty()) {
            songMetadataOverrides - songId
        } else {
            songMetadataOverrides + (songId to sanitizedOverride)
        }
        libraryCustomizationStore.saveSongMetadataOverrides(songMetadataOverrides)
        dismissEditMusicInfo()
        rebuildStateFromActiveCatalog()
    }

    private fun showSongActionNotice(
        title: String,
        message: String,
    ) {
        songActionUiState = songActionUiState.copy(
            noticeTitle = title,
            noticeMessage = message,
        )
    }

    private fun rebuildStateFromActiveCatalog() {
        val previousState = uiState
        val effectiveCatalog = activeCatalog.applyLibraryCustomizations(hiddenSongIds, songMetadataOverrides)
        val previousPlayback = previousState.playback
        val rebuiltPlayback = reconcilePlayback(
            previousPlayback = previousPlayback,
            songs = effectiveCatalog.songs,
            syncedLyricsDefault = previousState.profile.settings.showSyncedLyricsByDefault,
        )
        val shouldResynchronizePlayer =
            previousPlayback.queue.map(Song::id) != rebuiltPlayback.queue.map(Song::id) ||
                previousPlayback.currentSong?.id != rebuiltPlayback.currentSong?.id ||
                previousPlayback.currentIndex != rebuiltPlayback.currentIndex ||
                usesLocalPlayback(previousPlayback) != usesLocalPlayback(rebuiltPlayback)

        uiState = buildStateFromCatalog(
            catalog = effectiveCatalog,
            previousState = previousState,
            audioPermissionGranted = previousState.audioPermissionGranted,
            hasScannedDeviceAudio = previousState.hasScannedDeviceAudio,
            isScanningDeviceAudio = previousState.isScanningDeviceAudio,
            catalogSource = previousState.catalogSource,
            playback = rebuiltPlayback,
        )

        if (shouldResynchronizePlayer) {
            synchronizePlayerQueue(
                playback = uiState.playback,
                startPositionMs = uiState.playback.positionMs,
                playWhenReady = uiState.playback.isPlaying,
            )
        }
        persistPlaybackSession()
    }

    private fun reconcilePlayback(
        previousPlayback: PlaybackUiState,
        songs: List<Song>,
        syncedLyricsDefault: Boolean,
    ): PlaybackUiState {
        if (songs.isEmpty()) {
            return initialPlaybackFor(
                songs = emptyList(),
                syncedLyricsDefault = syncedLyricsDefault,
                shouldAutoplay = false,
                likedSongIds = emptySet(),
            )
        }

        val songsById = songs.associateBy(Song::id)
        val canonicalQueue = previousPlayback.canonicalQueue.mapNotNull { songsById[it.id] }.ifEmpty { songs }
        val queue = previousPlayback.queue.mapNotNull { songsById[it.id] }.ifEmpty { canonicalQueue }
        val previousSongId = previousPlayback.currentSong?.id
        val currentSong = songsById[previousSongId]
            ?: queue.getOrNull(previousPlayback.currentIndex)
            ?: queue.firstOrNull()
            ?: canonicalQueue.firstOrNull()
            ?: songs.firstOrNull()
        val currentSongId = currentSong?.id ?: return initialPlaybackFor(
            songs = emptyList(),
            syncedLyricsDefault = syncedLyricsDefault,
            shouldAutoplay = false,
            likedSongIds = emptySet(),
        )
        val currentIndex = queue.indexOfFirst { it.id == currentSongId }.coerceAtLeast(0)
        val currentQueueSong = queue.getOrNull(currentIndex) ?: queue.first()
        val keptLikedSongIds = previousPlayback.likedSongIds.filterTo(mutableSetOf()) { it in songsById }

        return previousPlayback.copy(
            queue = queue,
            canonicalQueue = canonicalQueue,
            currentIndex = currentIndex,
            positionMs = previousPlayback.positionMs.coerceIn(0L, currentQueueSong.durationMs),
            isPlaying = previousPlayback.isPlaying && previousSongId == currentSongId,
            likedSongIds = keptLikedSongIds,
        )
    }

    fun selectLibraryTab(tab: LibraryTab) {
        uiState = uiState.copy(selectedLibraryTab = tab)
    }

    fun updateLibraryQuery(query: String) {
        uiState = uiState.copy(libraryQuery = query)
    }

    fun selectLibrarySort(sort: LibrarySort) {
        uiState = uiState.copy(selectedLibrarySort = sort)
    }

    fun selectLibraryFilter(filter: LibraryFilter) {
        uiState = uiState.copy(selectedLibraryFilter = filter)
    }

    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun recallSearch(query: String) {
        persistSearch(query)
        uiState = uiState.copy(searchQuery = query)
    }

    fun playCurrentLibrarySong(songId: String) {
        val queueSongIds = currentLibrarySongs().map(Song::id)
        playSong(songId = songId, queueSongIds = queueSongIds)
    }

    fun shuffleAllSongs() {
        val queueSongIds = currentLibrarySongs().map(Song::id)
        val startSongId = queueSongIds.randomOrNull() ?: return
        playSong(songId = startSongId, queueSongIds = queueSongIds, shuffled = true)
    }

    fun addSongToPlayQueue(songId: String) {
        appendToPlayQueue(listOf(songId))
    }

    fun addAlbumToPlayQueue(albumId: String) {
        val album = uiState.albumsById[albumId] ?: return
        appendToPlayQueue(album.trackIds)
    }

    fun playQueuedSong(songId: String) {
        val queueSongIds = uiState.playQueueSongIds.filter(uiState.songsById::containsKey)
        if (queueSongIds.isEmpty() || songId !in queueSongIds) return
        playSong(songId = songId, queueSongIds = queueSongIds)
    }

    fun addSongToPlaylist(
        playlistId: String,
        songId: String,
    ) {
        addTrackIdsToPlaylist(playlistId, listOf(songId))
    }

    fun addAlbumToPlaylist(
        playlistId: String,
        albumId: String,
    ) {
        val album = uiState.albumsById[albumId] ?: return
        addTrackIdsToPlaylist(playlistId, album.trackIds)
    }

    fun deletePlaylist(playlistId: String) {
        if (uiState.playlistsById[playlistId] == null) return
        removedPlaylistIds = removedPlaylistIds + playlistId
        customPlaylists = customPlaylists.filterNot { it.id == playlistId }
        playlistTrackOverrides = playlistTrackOverrides - playlistId
        val updatedPlaylists = uiState.playlists.filterNot { it.id == playlistId }
        uiState = uiState.copy(
            playlists = updatedPlaylists,
            playlistsById = updatedPlaylists.associateBy(Playlist::id),
            favoritePlaylists = uiState.favoritePlaylists.filterNot { it.id == playlistId },
        )
    }

    private fun addTrackIdsToPlaylist(
        playlistId: String,
        trackIds: List<String>,
    ) {
        val playlist = uiState.playlistsById[playlistId] ?: return
        val validTrackIds = trackIds.filter(uiState.songsById::containsKey)
        if (validTrackIds.isEmpty()) return

        val updatedTrackIds = (playlist.trackIds + validTrackIds).distinct()
        if (updatedTrackIds == playlist.trackIds) return

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            followers = playlist.updatedTrackCountLabel(updatedTrackIds.size),
        )

        playlistTrackOverrides = playlistTrackOverrides + (playlistId to updatedTrackIds)
        if (playlist.isUserCreated) {
            customPlaylists = customPlaylists.map { existing ->
                if (existing.id == playlistId) updatedPlaylist else existing
            }
        }

        fun List<Playlist>.replacePlaylist(): List<Playlist> = map { existing ->
            if (existing.id == playlistId) updatedPlaylist else existing
        }

        uiState = uiState.copy(
            playlists = uiState.playlists.replacePlaylist(),
            playlistsById = uiState.playlistsById + (playlistId to updatedPlaylist),
            favoritePlaylists = uiState.favoritePlaylists.replacePlaylist(),
        )
    }

    fun playSong(
        songId: String,
        queueSongIds: List<String>? = null,
        shuffled: Boolean = false,
        repeatModeOverride: RepeatMode? = null,
    ) {
        val canonicalQueue = queueSongIds
            ?.mapNotNull(uiState.songsById::get)
            ?.distinctBy(Song::id)
            ?.takeIf { it.isNotEmpty() }
            ?: uiState.songs

        val requestedSong = uiState.songsById[songId] ?: return
        val queue = if (shuffled) {
            listOf(requestedSong) + canonicalQueue.filterNot { it.id == songId }.shuffled()
        } else {
            canonicalQueue
        }
        val currentIndex = queue.indexOfFirst { it.id == songId }.coerceAtLeast(0)
        val newPlayback = uiState.playback.copy(
            queue = queue,
            canonicalQueue = canonicalQueue,
            currentIndex = currentIndex,
            positionMs = 0L,
            isPlaying = true,
            isShuffled = shuffled,
            repeatMode = repeatModeOverride ?: uiState.playback.repeatMode,
        )

        updatePlayback(newPlayback)
        synchronizePlayerQueue(newPlayback, startPositionMs = 0L, playWhenReady = true)
    }

    fun playAlbum(albumId: String, shuffled: Boolean = false) {
        val album = uiState.albumsById[albumId] ?: return
        val startSongId = album.trackIds.firstOrNull() ?: return
        playSong(
            songId = startSongId,
            queueSongIds = album.trackIds,
            shuffled = shuffled,
            repeatModeOverride = RepeatMode.All,
        )
    }

    fun playAlbumTrack(
        albumId: String,
        songId: String,
    ) {
        val album = uiState.albumsById[albumId] ?: return
        if (songId !in album.trackIds) return
        playSong(
            songId = songId,
            queueSongIds = album.trackIds,
            repeatModeOverride = RepeatMode.All,
        )
    }

    fun playPlaylist(playlistId: String, shuffled: Boolean = false) {
        val playlist = uiState.playlistsById[playlistId] ?: return
        val startSongId = playlist.trackIds.firstOrNull() ?: return
        playSong(songId = startSongId, queueSongIds = playlist.trackIds, shuffled = shuffled)
    }

    fun playArtistTopTracks(artistId: String) {
        val artist = uiState.artistsById[artistId] ?: return
        val startSongId = artist.topTrackIds.firstOrNull() ?: return
        playSong(songId = startSongId, queueSongIds = artist.topTrackIds)
    }

    fun togglePlayPause() {
        val controller = player
        if (usesLocalPlayback()) {
            if (controller == null) return
            if (controller.isPlaying) controller.pause() else controller.play()
            syncFromPlayer()
            return
        }
        updatePlayback(uiState.playback.copy(isPlaying = !uiState.playback.isPlaying))
    }

    fun skipNext() {
        val playback = uiState.playback
        if (playback.queue.isEmpty()) return

        val controller = player
        if (usesLocalPlayback()) {
            if (controller == null) return
            if (controller.hasNextMediaItem()) {
                controller.seekToNextMediaItem()
            } else if (playback.repeatMode == RepeatMode.All) {
                controller.seekToDefaultPosition(0)
            }
            if (!controller.isPlaying) controller.play()
            syncFromPlayer()
            return
        }

        when {
            playback.currentIndex < playback.queue.lastIndex -> {
                updatePlayback(playback.copy(currentIndex = playback.currentIndex + 1, positionMs = 0L))
            }
            playback.repeatMode == RepeatMode.All -> {
                updatePlayback(playback.copy(currentIndex = 0, positionMs = 0L))
            }
        }
    }

    fun skipPrevious() {
        val playback = uiState.playback
        if (playback.queue.isEmpty()) return

        val controller = player
        if (usesLocalPlayback()) {
            if (controller == null) return
            if (controller.currentPosition > 5_000L) {
                controller.seekTo(0L)
            } else if (controller.hasPreviousMediaItem()) {
                controller.seekToPreviousMediaItem()
            } else if (playback.repeatMode == RepeatMode.All) {
                controller.seekToDefaultPosition(playback.queue.lastIndex)
            } else {
                controller.seekTo(0L)
            }
            syncFromPlayer()
            return
        }

        if (playback.positionMs > 5_000L) {
            updatePlayback(playback.copy(positionMs = 0L))
            return
        }

        when {
            playback.currentIndex > 0 -> {
                updatePlayback(playback.copy(currentIndex = playback.currentIndex - 1, positionMs = 0L))
            }
            playback.repeatMode == RepeatMode.All -> {
                updatePlayback(playback.copy(currentIndex = playback.queue.lastIndex, positionMs = 0L))
            }
        }
    }

    fun seekTo(positionMs: Long) {
        val currentSong = uiState.playback.currentSong ?: return
        val clamped = positionMs.coerceIn(0L, currentSong.durationMs)

        val controller = player
        if (usesLocalPlayback()) {
            if (controller == null) return
            controller.seekTo(clamped)
            syncFromPlayer()
            return
        }

        updatePlayback(uiState.playback.copy(positionMs = clamped))
    }

    fun toggleShuffle() {
        val playback = uiState.playback
        val currentSong = playback.currentSong ?: return
        val canonicalQueue = playback.canonicalQueue.ifEmpty { playback.queue }
        val currentPosition = playback.positionMs

        val newPlayback = if (playback.isShuffled) {
            val restoredIndex = canonicalQueue.indexOfFirst { it.id == currentSong.id }.coerceAtLeast(0)
            playback.copy(
                queue = canonicalQueue,
                canonicalQueue = canonicalQueue,
                currentIndex = restoredIndex,
                isShuffled = false,
            )
        } else {
            val shuffledQueue = listOf(currentSong) + canonicalQueue.filterNot { it.id == currentSong.id }.shuffled()
            playback.copy(
                queue = shuffledQueue,
                canonicalQueue = canonicalQueue,
                currentIndex = 0,
                isShuffled = true,
            )
        }

        updatePlayback(newPlayback)
        synchronizePlayerQueue(newPlayback, startPositionMs = currentPosition, playWhenReady = newPlayback.isPlaying)
    }

    fun cycleRepeatMode() {
        val nextMode = when (uiState.playback.repeatMode) {
            RepeatMode.Off -> RepeatMode.All
            RepeatMode.All -> RepeatMode.One
            RepeatMode.One -> RepeatMode.Off
        }
        updatePlayback(uiState.playback.copy(repeatMode = nextMode))
        player?.repeatMode = nextMode.toPlayerRepeatMode()
    }

    fun toggleCurrentLike() {
        val currentSongId = uiState.playback.currentSong?.id ?: return
        toggleSongLike(currentSongId)
    }

    fun toggleSongLike(songId: String) {
        if (uiState.songsById[songId] == null) return
        val liked = uiState.playback.likedSongIds.toMutableSet()
        if (!liked.add(songId)) {
            liked.remove(songId)
        }
        updatePlayback(uiState.playback.copy(likedSongIds = liked))
    }

    fun setLyricsDisplayMode(mode: LyricsDisplayMode) {
        updatePlayback(uiState.playback.copy(lyricsDisplayMode = mode))
    }

    fun openManualLyricsSearch() {
        val song = uiState.playback.currentSong ?: return
        val artistName = uiState.artistsById[song.artistId]?.name.orEmpty()
        uiState = uiState.copy(
            manualLyricsSearch = ManualLyricsSearchUiState(
                songId = song.id,
                queryTitle = preferredTitleQuery(song.title),
                queryArtist = preferredArtistQuery(artistName),
                isVisible = true,
            ),
        )
        searchManualLyricsCandidates()
    }

    fun dismissManualLyricsSearch() {
        manualLyricsSearchJob?.cancel()
        manualLyricsSearchJob = null
        uiState = uiState.copy(manualLyricsSearch = ManualLyricsSearchUiState())
    }

    fun updateManualLyricsSearchTitle(title: String) {
        uiState = uiState.copy(
            manualLyricsSearch = uiState.manualLyricsSearch.copy(queryTitle = title),
        )
    }

    fun updateManualLyricsSearchArtist(artist: String) {
        uiState = uiState.copy(
            manualLyricsSearch = uiState.manualLyricsSearch.copy(queryArtist = artist),
        )
    }

    fun searchManualLyricsCandidates() {
        val song = uiState.playback.currentSong ?: return
        val searchState = uiState.manualLyricsSearch
        if (!searchState.isVisible) return

        val title = searchState.queryTitle.trim()
        val artist = searchState.queryArtist.trim()
        if (title.isBlank() || artist.isBlank()) {
            uiState = uiState.copy(
                manualLyricsSearch = searchState.copy(
                    isLoading = false,
                    hasSearched = true,
                    results = emptyList(),
                ),
            )
            return
        }

        manualLyricsSearchJob?.cancel()
        uiState = uiState.copy(
            manualLyricsSearch = searchState.copy(
                songId = song.id,
                isLoading = true,
                hasSearched = true,
                results = emptyList(),
            ),
        )

        val albumTitle = uiState.albumsById[song.albumId]?.title
        manualLyricsSearchJob = viewModelScope.launch {
            val results = lyricsRepository.searchCandidates(
                title = title,
                artistName = artist,
                albumTitle = albumTitle,
                durationMs = song.durationMs,
            )
            val latestSearchState = uiState.manualLyricsSearch
            if (latestSearchState.songId != song.id || !latestSearchState.isVisible) return@launch
            uiState = uiState.copy(
                manualLyricsSearch = latestSearchState.copy(
                    isLoading = false,
                    hasSearched = true,
                    results = results,
                ),
            )
            manualLyricsSearchJob = null
        }
    }

    fun applyManualLyricsCandidate(candidate: LyricsSearchCandidate) {
        val songId = uiState.playback.currentSong?.id ?: return
        val currentSong = uiState.songsById[songId] ?: return
        manualLyricsSearchJob?.cancel()
        manualLyricsSearchJob = null
        lyricsJobs[songId]?.cancel()
        lyricsJobs.remove(songId)

        applySongUpdate(songId) { existing ->
            existing.copy(
                lyrics = candidate.syncedLyrics,
                plainLyrics = if (candidate.plainLyrics.isNotEmpty()) {
                    candidate.plainLyrics
                } else {
                    candidate.syncedLyrics.map { it.text }
                },
                lyricsAttribution = candidate.attribution,
            )
        }
        lyricsUpgradeAttempts.remove(songId)
        lyricsCacheStore.save(
            mediaUri = currentSong.mediaUri,
            syncedLyrics = candidate.syncedLyrics,
            plainLyrics = if (candidate.plainLyrics.isNotEmpty()) {
                candidate.plainLyrics
            } else {
                candidate.syncedLyrics.map { it.text }
            },
            attribution = candidate.attribution,
        )
        updateLyricsStatus(songId, LyricsLoadState.Ready)
        uiState = uiState.copy(
            playback = uiState.playback.copy(
                lyricsDisplayMode = if (candidate.hasSyncedLyrics) {
                    LyricsDisplayMode.Synced
                } else {
                    LyricsDisplayMode.Plain
                },
            ),
            manualLyricsSearch = ManualLyricsSearchUiState(),
        )
    }

    fun setQueueSheetVisible(visible: Boolean) {
        updatePlayback(uiState.playback.copy(isQueueSheetVisible = visible))
    }

    fun updateThemePreset(preset: ThemePreset) {
        updateSettings { copy(themePreset = preset) }
    }

    fun updateAutoplay(enabled: Boolean) {
        updateSettings { copy(autoplay = enabled) }
    }

    fun updateImmersiveMotion(enabled: Boolean) {
        updateSettings { copy(immersiveMotion = enabled) }
    }

    fun updateSyncedLyrics(enabled: Boolean) {
        updateSettings { copy(showSyncedLyricsByDefault = enabled) }
        updatePlayback(
            uiState.playback.copy(
                lyricsDisplayMode = if (enabled) LyricsDisplayMode.Synced else LyricsDisplayMode.Plain,
            ),
        )
    }

    fun updateWifiDownloads(enabled: Boolean) {
        updateSettings { copy(downloadOnWifiOnly = enabled) }
    }

    fun updateExplicitContent(enabled: Boolean) {
        updateSettings { copy(explicitContent = enabled) }
    }

    fun updateLanguage(language: String) {
        updateSettings { copy(language = language) }
    }

    fun updateProfileName(name: String) {
        val sanitizedName = name.replace("\n", " ").take(40)
        val generatedHandle = sanitizedName
            .trim()
            .lowercase()
            .replace(Regex("""[^a-z0-9]+"""), "")
            .take(20)
            .ifBlank { "musiclover" }

        uiState = uiState.copy(
            profile = uiState.profile.copy(
                name = sanitizedName,
                handle = "@$generatedHandle",
            ),
        )
        userPreferencesStore.saveProfileName(
            name = uiState.profile.name,
            handle = uiState.profile.handle,
        )
    }

    private fun updateSettings(transform: UserSettings.() -> UserSettings) {
        uiState = uiState.copy(profile = uiState.profile.copy(settings = uiState.profile.settings.transform()))
        userPreferencesStore.saveSettings(uiState.profile.settings)
    }

    private fun persistSearch(query: String) {
        if (query.isBlank()) return
        val updatedSearches = buildList {
            add(query)
            addAll(uiState.recentSearches.filterNot { it.equals(query, ignoreCase = true) })
        }.take(6)
        uiState = uiState.copy(recentSearches = updatedSearches)
    }

    private fun currentLibrarySongs(): List<Song> {
        return uiState.songs
            .sortedBy { it.title.lowercase() }
    }

    fun createPlaylist(
        title: String,
        description: String,
    ) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) return

        val playlist = Playlist(
            id = "user_playlist_${System.currentTimeMillis()}",
            title = trimmedTitle,
            description = description.trim().ifBlank { "Created in your VerseFlow library." },
            curator = uiState.profile.displayName,
            followers = "0 tracks",
            palette = uiState.playback.currentSong?.palette ?: uiState.profile.avatarPalette,
            trackIds = emptyList(),
            artworkUri = uiState.playback.currentSong?.artworkUri,
            isUserCreated = true,
        )

        customPlaylists = (listOf(playlist) + customPlaylists).distinctBy(Playlist::id)
        val mergedPlaylists = mergePlaylists(
            basePlaylists = uiState.playlists.filterNot(Playlist::isUserCreated),
            availableSongIds = uiState.songsById.keys,
        )
        uiState = uiState.copy(
            playlists = mergedPlaylists,
            playlistsById = mergedPlaylists.associateBy(Playlist::id),
        )
    }

    private fun restoredPlaybackFor(
        songs: List<Song>,
        syncedLyricsDefault: Boolean,
        likedSongIds: Set<String>,
    ): PlaybackUiState {
        val session = pendingPlaybackSession
        if (session == null) {
            return initialPlaybackFor(
                songs = songs,
                syncedLyricsDefault = syncedLyricsDefault,
                shouldAutoplay = false,
                likedSongIds = likedSongIds,
            )
        }

        val songsById = songs.associateBy(Song::id)
        val songsByMediaUri = songs.associateBy { it.mediaUri.orEmpty() }
        val restoredQueue = session.queueSongIds
            .mapNotNull(songsById::get)
            .ifEmpty {
                session.queueSongMediaUris.mapNotNull { mediaUri ->
                    songsByMediaUri[mediaUri]
                }
            }
            .distinctBy(Song::id)

        val restoredSong = session.currentSongId?.let(songsById::get)
            ?: session.currentSongMediaUri?.let { songsByMediaUri[it] }
            ?: restoredQueue.getOrNull(session.currentIndex)
            ?: restoredQueue.firstOrNull()

        if (restoredSong == null) {
            return initialPlaybackFor(
                songs = songs,
                syncedLyricsDefault = syncedLyricsDefault,
                shouldAutoplay = false,
                likedSongIds = likedSongIds,
            )
        }

        val queue = if (restoredQueue.isEmpty()) listOf(restoredSong) else restoredQueue
        val currentIndex = queue.indexOfFirst { it.id == restoredSong.id }.coerceAtLeast(0)
        val currentQueueSong = queue[currentIndex]

        return PlaybackUiState(
            queue = queue,
            canonicalQueue = queue,
            currentIndex = currentIndex,
            positionMs = session.positionMs.coerceIn(0L, currentQueueSong.durationMs),
            isPlaying = false,
            isShuffled = session.isShuffled,
            repeatMode = session.repeatMode,
            likedSongIds = likedSongIds,
            lyricsDisplayMode = session.lyricsDisplayMode.takeIf {
                it == LyricsDisplayMode.Plain || currentQueueSong.lyrics.isNotEmpty() || currentQueueSong.plainLyrics.isNotEmpty()
            } ?: if (syncedLyricsDefault) LyricsDisplayMode.Synced else LyricsDisplayMode.Plain,
        )
    }

    private fun persistPlaybackSession() {
        val playback = uiState.playback
        val currentSong = playback.currentSong
        if (currentSong?.source != SongSource.Local || currentSong.mediaUri.isNullOrBlank()) {
            playbackSessionStore.clear()
            return
        }

        val queueSongIds = playback.queue.map(Song::id)
        val queueSongMediaUris = playback.queue.mapNotNull { song ->
            song.mediaUri?.takeIf(String::isNotBlank)
        }
        if (queueSongIds.isEmpty() && queueSongMediaUris.isEmpty()) {
            playbackSessionStore.clear()
            return
        }

        playbackSessionStore.save(
            SavedPlaybackSession(
                currentSongId = currentSong.id,
                currentSongMediaUri = currentSong.mediaUri,
                queueSongIds = queueSongIds,
                queueSongMediaUris = queueSongMediaUris,
                currentIndex = playback.currentIndex,
                positionMs = playback.positionMs,
                repeatMode = playback.repeatMode,
                isShuffled = playback.isShuffled,
                lyricsDisplayMode = playback.lyricsDisplayMode,
            ),
        )
    }

    override fun onCleared() {
        playbackTicker?.cancel()
        cancelLyricsRequests()
        manualLyricsSearchJob?.cancel()
        persistPlaybackSession()
        controllerFuture?.let(MediaController::releaseFuture)
        player = null
        super.onCleared()
    }

    private fun buildStateFromCatalog(
        catalog: CatalogData,
        previousState: VerseFlowUiState?,
        audioPermissionGranted: Boolean,
        hasScannedDeviceAudio: Boolean,
        isScanningDeviceAudio: Boolean,
        catalogSource: MusicCatalogSource,
        playback: PlaybackUiState,
    ): VerseFlowUiState {
        val songsById = catalog.songs.associateBy(Song::id)
        val albumsById = catalog.albums.associateBy(Album::id)
        val artistsById = catalog.artists.associateBy(Artist::id)
        val mergedPlaylists = mergePlaylists(
            basePlaylists = catalog.playlists,
            availableSongIds = songsById.keys,
        )
        val playlistsById = mergedPlaylists.associateBy(Playlist::id)
        val lyricsStatusBySongId = catalog.songs.associate { song ->
            val previousStatus = previousState?.lyricsStatusBySongId?.get(song.id)
            song.id to when {
                song.lyrics.isNotEmpty() || song.plainLyrics.isNotEmpty() -> LyricsLoadState.Ready
                previousStatus == LyricsLoadState.Loading -> LyricsLoadState.Loading
                previousStatus == LyricsLoadState.Unavailable -> LyricsLoadState.Unavailable
                else -> LyricsLoadState.Idle
            }
        }

        return VerseFlowUiState(
            profile = previousState?.profile ?: defaultProfile,
            songs = catalog.songs,
            songsById = songsById,
            albums = catalog.albums,
            albumsById = albumsById,
            artists = catalog.artists,
            artistsById = artistsById,
            playlists = mergedPlaylists,
            playlistsById = playlistsById,
            featuredAlbums = catalog.featuredAlbums,
            recentlyPlayed = catalog.recentlyPlayed,
            trendingSongs = catalog.trendingSongs,
            favoritePlaylists = catalog.favoritePlaylists.mapNotNull { favoritePlaylist ->
                playlistsById[favoritePlaylist.id]
            },
            recentSearches = previousState?.recentSearches
                ?: if (catalogSource == MusicCatalogSource.Demo) repository.recentSearches() else emptyList(),
            trendingCategories = catalog.trendingCategories,
            selectedLibraryTab = previousState?.selectedLibraryTab ?: LibraryTab.Songs,
            selectedLibrarySort = previousState?.selectedLibrarySort ?: LibrarySort.Recent,
            selectedLibraryFilter = previousState?.selectedLibraryFilter ?: LibraryFilter.All,
            libraryQuery = previousState?.libraryQuery.orEmpty(),
            searchQuery = previousState?.searchQuery.orEmpty(),
            audioPermissionGranted = audioPermissionGranted,
            hasScannedDeviceAudio = hasScannedDeviceAudio,
            isScanningDeviceAudio = isScanningDeviceAudio,
            catalogSource = catalogSource,
            lyricsStatusBySongId = lyricsStatusBySongId,
            playQueueSongIds = previousState?.playQueueSongIds.orEmpty().filter(songsById::containsKey),
            playback = playback,
            manualLyricsSearch = ManualLyricsSearchUiState(),
        )
    }

    private fun mergePlaylists(
        basePlaylists: List<Playlist>,
        availableSongIds: Set<String>,
    ): List<Playlist> =
        (customPlaylists + basePlaylists)
            .distinctBy(Playlist::id)
            .filterNot { it.id in removedPlaylistIds }
            .map { playlist ->
                val overriddenTrackIds = playlistTrackOverrides[playlist.id]
                if (overriddenTrackIds == null) {
                    val validTrackIds = playlist.trackIds.filter { it in availableSongIds }
                    playlist.copy(
                        trackIds = validTrackIds,
                        followers = playlist.updatedTrackCountLabel(validTrackIds.size),
                    )
                } else {
                    val validTrackIds = overriddenTrackIds.filter { it in availableSongIds }
                    playlist.copy(
                        trackIds = validTrackIds,
                        followers = playlist.updatedTrackCountLabel(validTrackIds.size),
                    )
                }
            }

    private fun initialPlaybackFor(
        songs: List<Song>,
        syncedLyricsDefault: Boolean = defaultProfile.settings.showSyncedLyricsByDefault,
        shouldAutoplay: Boolean,
        likedSongIds: Set<String> = setOf("song_midnight_circuit", "song_velvet_static"),
    ): PlaybackUiState {
        val queue = songs
        return PlaybackUiState(
            queue = queue,
            canonicalQueue = queue,
            currentIndex = 0,
            positionMs = if (shouldAutoplay && queue.firstOrNull()?.source == SongSource.Mock) 18_000L else 0L,
            isPlaying = shouldAutoplay,
            isShuffled = false,
            repeatMode = RepeatMode.All,
            likedSongIds = likedSongIds,
            lyricsDisplayMode = if (syncedLyricsDefault) LyricsDisplayMode.Synced else LyricsDisplayMode.Plain,
        )
    }

    private fun appendToPlayQueue(trackIds: List<String>) {
        val validTrackIds = trackIds.filter(uiState.songsById::containsKey)
        if (validTrackIds.isEmpty()) return
        uiState = uiState.copy(
            playQueueSongIds = (uiState.playQueueSongIds + validTrackIds).distinct(),
        )
    }
}

private fun Playlist.updatedTrackCountLabel(trackCount: Int): String {
    return if (followers.matches(Regex("""\d+\s+tracks""", RegexOption.IGNORE_CASE))) {
        "$trackCount tracks"
    } else {
        followers
    }
}

private data class CatalogData(
    val songs: List<Song>,
    val albums: List<Album>,
    val artists: List<Artist>,
    val playlists: List<Playlist>,
    val featuredAlbums: List<Album>,
    val recentlyPlayed: List<Song>,
    val trendingSongs: List<Song>,
    val favoritePlaylists: List<Playlist>,
    val trendingCategories: List<String>,
)

private fun MusicRepository.toCatalog(): CatalogData {
    val songs = songs()
    val songsById = songs.associateBy(Song::id)
    val albums = albums()
    val albumsById = albums.associateBy(Album::id)
    val playlists = playlists()
    val playlistsById = playlists.associateBy(Playlist::id)
    return CatalogData(
        songs = songs,
        albums = albums,
        artists = artists(),
        playlists = playlists,
        featuredAlbums = featuredAlbumIds().mapNotNull(albumsById::get),
        recentlyPlayed = recentlyPlayedIds().mapNotNull(songsById::get),
        trendingSongs = trendingSongIds().mapNotNull(songsById::get),
        favoritePlaylists = favoritePlaylistIds().mapNotNull(playlistsById::get),
        trendingCategories = trendingCategories(),
    )
}

private fun DeviceAudioCatalog.toCatalogData(): CatalogData = CatalogData(
    songs = songs,
    albums = albums,
    artists = artists,
    playlists = playlists,
    featuredAlbums = albums.take(3),
    recentlyPlayed = songs.take(6),
    trendingSongs = songs.take(8),
    favoritePlaylists = playlists.take(3),
    trendingCategories = if (songs.isEmpty()) {
        emptyList()
    } else {
        listOf(
            "On Device",
            "Recently Added",
            "Offline",
            "Local Albums",
            "Artist Mix",
        )
    },
)

private fun emptyCatalogData(): CatalogData = CatalogData(
    songs = emptyList(),
    albums = emptyList(),
    artists = emptyList(),
    playlists = emptyList(),
    featuredAlbums = emptyList(),
    recentlyPlayed = emptyList(),
    trendingSongs = emptyList(),
    favoritePlaylists = emptyList(),
    trendingCategories = emptyList(),
)

private fun Application.hasAudioPermission(): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

private fun CatalogData.replaceSong(updatedSong: Song): CatalogData {
    fun List<Song>.replaceSong(): List<Song> = map { song ->
        if (song.id == updatedSong.id) updatedSong else song
    }

    return copy(
        songs = songs.replaceSong(),
        recentlyPlayed = recentlyPlayed.replaceSong(),
        trendingSongs = trendingSongs.replaceSong(),
    )
}

private fun CatalogData.removeSong(songId: String): CatalogData = copy(
    songs = songs.filterNot { it.id == songId },
    playlists = playlists.map { playlist ->
        playlist.copy(trackIds = playlist.trackIds.filterNot { it == songId })
    },
    recentlyPlayed = recentlyPlayed.filterNot { it.id == songId },
    trendingSongs = trendingSongs.filterNot { it.id == songId },
    favoritePlaylists = favoritePlaylists.map { playlist ->
        playlist.copy(trackIds = playlist.trackIds.filterNot { it == songId })
    },
)

private data class EffectiveSongSeed(
    val baseSong: Song,
    val title: String,
    val artistName: String,
    val albumTitle: String,
    val genre: String?,
)

private fun CatalogData.applyLibraryCustomizations(
    hiddenSongIds: Set<String>,
    songMetadataOverrides: Map<String, SongMetadataOverride>,
): CatalogData {
    val baseArtistsById = artists.associateBy(Artist::id)
    val baseAlbumsById = albums.associateBy(Album::id)
    val visibleSeeds = songs
        .filterNot { it.id in hiddenSongIds }
        .map { song ->
            val override = songMetadataOverrides[song.id]
            EffectiveSongSeed(
                baseSong = song,
                title = override?.title ?: song.title,
                artistName = override?.artistName ?: baseArtistsById[song.artistId]?.name.orEmpty().ifBlank { "Unknown Artist" },
                albumTitle = override?.albumTitle ?: baseAlbumsById[song.albumId]?.title.orEmpty().ifBlank { "Singles" },
                genre = override?.genre ?: song.genre,
            )
        }

    if (visibleSeeds.isEmpty()) {
        return copy(
            songs = emptyList(),
            albums = emptyList(),
            artists = emptyList(),
            playlists = playlists.filter(Playlist::isUserCreated).map { playlist ->
                playlist.copy(trackIds = emptyList(), followers = playlist.updatedTrackCountLabel(0))
            },
            featuredAlbums = emptyList(),
            recentlyPlayed = emptyList(),
            trendingSongs = emptyList(),
            favoritePlaylists = favoritePlaylists.filter(Playlist::isUserCreated).map { playlist ->
                playlist.copy(trackIds = emptyList(), followers = playlist.updatedTrackCountLabel(0))
            },
        )
    }

    fun stableId(seed: String): String = seed.hashCode().absoluteValue.toString()
    fun artistKey(artistName: String): String = artistName.trim().lowercase()
    fun albumKey(artistName: String, albumTitle: String): String = "${artistKey(artistName)}::${albumTitle.trim().lowercase()}"

    val artistNameByKey = visibleSeeds
        .map { it.artistName }
        .distinctBy(::artistKey)
        .associateBy(::artistKey)
    val artistIdByKey = artistNameByKey.keys.associateWith { key -> "custom_artist_${stableId(key)}" }
    val albumInfoByKey = visibleSeeds
        .map { it.artistName to it.albumTitle }
        .distinctBy { (artistName, albumTitle) -> albumKey(artistName, albumTitle) }
        .associateBy(
            keySelector = { (artistName, albumTitle) -> albumKey(artistName, albumTitle) },
            valueTransform = { (_, albumTitle) -> albumTitle },
        )
    val albumIdByKey = albumInfoByKey.keys.associateWith { key -> "custom_album_${stableId(key)}" }

    val effectiveSongs = visibleSeeds.map { seed ->
        val effectiveArtistId = artistIdByKey.getValue(artistKey(seed.artistName))
        val effectiveAlbumId = albumIdByKey.getValue(albumKey(seed.artistName, seed.albumTitle))
        seed.baseSong.copy(
            title = seed.title,
            artistId = effectiveArtistId,
            albumId = effectiveAlbumId,
            genre = seed.genre,
        )
    }
    val effectiveSongsById = effectiveSongs.associateBy(Song::id)
    val visibleSeedsBySongId = visibleSeeds.associateBy { it.baseSong.id }

    val albums = visibleSeeds
        .groupBy { albumKey(it.artistName, it.albumTitle) }
        .map { (key, seeds) ->
            val firstSeed = seeds.first()
            val firstSong = effectiveSongsById.getValue(firstSeed.baseSong.id)
            val representativeBaseAlbum = baseAlbumsById[firstSeed.baseSong.albumId]
            val effectiveArtistId = artistIdByKey.getValue(artistKey(firstSeed.artistName))
            val trackIds = seeds.map { it.baseSong.id }.filter(effectiveSongsById::containsKey)
            Album(
                id = albumIdByKey.getValue(key),
                title = firstSeed.albumTitle,
                artistId = effectiveArtistId,
                year = representativeBaseAlbum?.year ?: 2026,
                label = representativeBaseAlbum?.label ?: if (firstSong.source == SongSource.Local) {
                    "On-device library"
                } else {
                    "VerseFlow"
                },
                description = representativeBaseAlbum?.description
                    ?: "${trackIds.size} tracks available in VerseFlow",
                palette = representativeBaseAlbum?.palette ?: firstSong.palette,
                trackIds = trackIds,
                artworkUri = representativeBaseAlbum?.artworkUri ?: firstSong.artworkUri,
            )
        }
        .sortedBy { it.title.lowercase() }
    val albumsById = albums.associateBy(Album::id)

    val artists = visibleSeeds
        .groupBy { artistKey(it.artistName) }
        .map { (key, seeds) ->
            val firstSeed = seeds.first()
            val effectiveArtistId = artistIdByKey.getValue(key)
            val representativeBaseArtist = baseArtistsById[firstSeed.baseSong.artistId]
            val effectiveArtistSongs = seeds.mapNotNull { seed -> effectiveSongsById[seed.baseSong.id] }
            Artist(
                id = effectiveArtistId,
                name = artistNameByKey.getValue(key),
                genre = effectiveArtistSongs
                    .mapNotNull(Song::genre)
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key
                    ?: representativeBaseArtist?.genre
                    ?: "Library",
                monthlyListeners = representativeBaseArtist?.monthlyListeners
                    ?: "${effectiveArtistSongs.size} saved tracks",
                bio = representativeBaseArtist?.bio
                    ?: "Available in your VerseFlow library with app-level metadata overrides applied.",
                heroPalette = representativeBaseArtist?.heroPalette ?: effectiveArtistSongs.first().palette,
                albumIds = albums.filter { it.artistId == effectiveArtistId }.map(Album::id),
                topTrackIds = effectiveArtistSongs.take(5).map(Song::id),
                relatedArtistIds = emptyList(),
            )
        }
        .sortedBy { it.name.lowercase() }
    val artistsById = artists.associateBy(Artist::id)
    val relatedArtistIdsById = artists.associate { artist ->
        artist.id to artists
            .asSequence()
            .filterNot { it.id == artist.id }
            .sortedBy { it.name.lowercase() }
            .take(2)
            .map(Artist::id)
            .toList()
    }
    val finalizedArtists = artists.map { artist ->
        artist.copy(relatedArtistIds = relatedArtistIdsById[artist.id].orEmpty())
    }

    val effectivePlaylists = playlists
        .map { playlist ->
            val filteredTrackIds = playlist.trackIds.filter(effectiveSongsById::containsKey)
            playlist.copy(
                trackIds = filteredTrackIds,
                followers = playlist.updatedTrackCountLabel(filteredTrackIds.size),
                artworkUri = filteredTrackIds.firstNotNullOfOrNull { trackId ->
                    effectiveSongsById[trackId]?.artworkUri
                } ?: playlist.artworkUri,
            )
        }
        .filter { it.isUserCreated || it.trackIds.isNotEmpty() }
    val effectivePlaylistsById = effectivePlaylists.associateBy(Playlist::id)

    val effectiveAlbumIdByBaseAlbumId = visibleSeeds
        .groupBy { it.baseSong.albumId }
        .mapValues { (_, seeds) ->
            val first = seeds.first()
            albumIdByKey.getValue(albumKey(first.artistName, first.albumTitle))
        }

    return CatalogData(
        songs = effectiveSongs,
        albums = albums,
        artists = finalizedArtists,
        playlists = effectivePlaylists,
        featuredAlbums = featuredAlbums.mapNotNull { featuredAlbum ->
            effectiveAlbumIdByBaseAlbumId[featuredAlbum.id]?.let(albumsById::get)
        }.distinctBy(Album::id),
        recentlyPlayed = recentlyPlayed.mapNotNull { effectiveSongsById[it.id] }.distinctBy(Song::id),
        trendingSongs = trendingSongs.mapNotNull { effectiveSongsById[it.id] }.distinctBy(Song::id),
        favoritePlaylists = favoritePlaylists.mapNotNull { favoritePlaylist ->
            effectivePlaylistsById[favoritePlaylist.id]
        }.filter { it.isUserCreated || it.trackIds.isNotEmpty() },
        trendingCategories = trendingCategories,
    )
}

private fun RepeatMode.toPlayerRepeatMode(): Int = when (this) {
    RepeatMode.Off -> Player.REPEAT_MODE_OFF
    RepeatMode.All -> Player.REPEAT_MODE_ALL
    RepeatMode.One -> Player.REPEAT_MODE_ONE
}
