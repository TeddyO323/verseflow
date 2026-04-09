package com.example.verseflow.data

import android.content.ContentUris
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import com.example.verseflow.model.AccentPalette
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.SongSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.absoluteValue

data class DeviceAudioCatalog(
    val songs: List<Song>,
    val albums: List<Album>,
    val artists: List<Artist>,
    val playlists: List<Playlist>,
)

class DeviceAudioStoreLoader(
    private val context: Context,
) {
    suspend fun load(): DeviceAudioCatalog = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val genreByAudioId = loadGenreMap(resolver)
        val folderColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.MediaColumns.RELATIVE_PATH
        } else {
            MediaStore.MediaColumns.DATA
        }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.YEAR,
            folderColumn,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        val rows = mutableListOf<DeviceSongRow>()
        resolver.query(mediaUri, projection, selection, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val folderPathColumn = cursor.getColumnIndex(folderColumn)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = sanitizeMetadata(cursor.getString(titleColumn), fallback = "Untitled Track")
                val artistName = sanitizeMetadata(cursor.getString(artistColumn), fallback = "Unknown Artist")
                val artistCredits = buildArtistCredits(artistName, title)
                val primaryArtistName = artistCredits.firstOrNull() ?: artistName
                val albumTitle = sanitizeMetadata(cursor.getString(albumColumn), fallback = "Singles")
                val albumMediaId = cursor.getLong(albumIdColumn).takeIf { it > 0L }
                val durationMs = cursor.getLong(durationColumn).coerceAtLeast(0L)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val year = cursor.getInt(yearColumn).takeIf { it > 0 }
                val uri = ContentUris.withAppendedId(mediaUri, id).toString()
                val artworkUri = albumArtUri(albumMediaId) ?: uri
                val folderInfo = resolveFolderInfo(
                    rawFolderValue = if (folderPathColumn >= 0) cursor.getString(folderPathColumn) else null,
                    isRelativePath = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q,
                )

                rows += DeviceSongRow(
                    id = id,
                    title = title,
                    artistName = primaryArtistName,
                    artistCredits = artistCredits,
                    albumTitle = albumTitle,
                    genre = genreByAudioId[id],
                    albumMediaId = albumMediaId,
                    durationMs = durationMs,
                    dateAdded = dateAdded,
                    year = year,
                    artworkUri = artworkUri,
                    mediaUri = uri,
                    folderName = folderInfo?.name,
                    folderPath = folderInfo?.path,
                )
            }
        }

        buildCatalog(rows)
    }

    private fun buildCatalog(rows: List<DeviceSongRow>): DeviceAudioCatalog {
        if (rows.isEmpty()) {
            return DeviceAudioCatalog(
                songs = emptyList(),
                albums = emptyList(),
                artists = emptyList(),
                playlists = emptyList(),
            )
        }

        val artistIdsByName = rows
            .flatMap { it.artistCredits.ifEmpty { listOf(it.artistName) } }
            .distinct()
            .associateWith { name -> "device_artist_${stableId(name)}" }

        val albumIdsByKey = rows
            .map { "${it.albumTitle}::${it.artistName}" }
            .distinct()
            .associateWith { key -> "device_album_${stableId(key)}" }

        val songs = rows.map { row ->
            val artistId = artistIdsByName.getValue(row.artistName)
            val albumId = albumIdsByKey.getValue("${row.albumTitle}::${row.artistName}")
            Song(
                id = "device_song_${row.id}",
                title = row.title,
                artistId = artistId,
                albumId = albumId,
                durationMs = row.durationMs,
                genre = row.genre,
                mood = deriveMood(row.title, row.artistName),
                palette = paletteFor("${row.title}${row.artistName}${row.albumTitle}"),
                lyrics = emptyList(),
                isDownloaded = true,
                artworkUri = row.artworkUri,
                mediaUri = row.mediaUri,
                artistCredits = row.artistCredits.ifEmpty { listOf(row.artistName) },
                folderName = row.folderName,
                folderPath = row.folderPath,
                source = SongSource.Local,
            )
        }
        val songsByAlbumId = songs.groupBy(Song::albumId)
        val songsByArtistId = artistIdsByName.entries.associate { (artistName, artistId) ->
            artistId to songs.filter { song -> artistName in song.artistCredits }
        }

        val albums = rows
            .groupBy { "${it.albumTitle}::${it.artistName}" }
            .map { (key, albumRows) ->
                val first = albumRows.first()
                val artistId = artistIdsByName.getValue(first.artistName)
                val albumId = albumIdsByKey.getValue(key)
                Album(
                    id = albumId,
                    title = first.albumTitle,
                    artistId = artistId,
                    year = albumRows.mapNotNull(DeviceSongRow::year).maxOrNull() ?: 2026,
                    label = "On-device library",
                    description = "${albumRows.size} tracks imported from this phone",
                    palette = paletteFor(key),
                    trackIds = songsByAlbumId[albumId].orEmpty().map(Song::id),
                    artworkUri = albumRows.firstNotNullOfOrNull(DeviceSongRow::artworkUri),
                )
            }
            .sortedByDescending(Album::year)

        val artistNames = artistIdsByName.keys.toList()
        val artists = artistNames.map { artistName ->
            val artistId = artistIdsByName.getValue(artistName)
            val artistSongs = songsByArtistId[artistId].orEmpty()
            val related = artistNames
                .filterNot { it == artistName }
                .sortedBy { stableId("$artistName::$it") }
                .take(2)
                .map(artistIdsByName::getValue)

            Artist(
                id = artistId,
                name = artistName,
                genre = artistSongs
                    .mapNotNull(Song::genre)
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key
                    ?: "Local Library",
                monthlyListeners = "${artistSongs.size} saved tracks",
                bio = "Imported from your device for on-phone playback testing in VerseFlow.",
                heroPalette = paletteFor(artistName),
                albumIds = albums.filter { it.artistId == artistId }.map(Album::id),
                topTrackIds = artistSongs.take(5).map(Song::id),
                trackCount = artistSongs.size,
                relatedArtistIds = related,
            )
        }

        val recentlyAdded = songs.take(18)
        val longestTracks = songs.sortedByDescending(Song::durationMs).take(18)
        val nightRun = songs
            .sortedBy { stableId(it.title + it.artistId) }
            .take(18)

        val playlists = listOf(
            generatedPlaylist(
                id = "device_playlist_recent",
                title = "Recently Added",
                description = "Fresh imports from this phone's local music library.",
                curator = "This Device",
                followers = "${recentlyAdded.size} tracks",
                seed = "device_recent",
                songs = recentlyAdded,
            ),
            generatedPlaylist(
                id = "device_playlist_longplay",
                title = "Long Play",
                description = "The longest tracks currently available on this device.",
                curator = "This Device",
                followers = "${longestTracks.size} tracks",
                seed = "device_long",
                songs = longestTracks,
            ),
            generatedPlaylist(
                id = "device_playlist_nightrun",
                title = "Night Run",
                description = "A mixed local set assembled for sleek test-drive playback.",
                curator = "VerseFlow Auto Mix",
                followers = "${nightRun.size} tracks",
                seed = "device_mix",
                songs = nightRun,
            ),
        ).filter { it.trackIds.isNotEmpty() }

        return DeviceAudioCatalog(
            songs = songs,
            albums = albums,
            artists = artists,
            playlists = playlists,
        )
    }

    private fun generatedPlaylist(
        id: String,
        title: String,
        description: String,
        curator: String,
        followers: String,
        seed: String,
        songs: List<Song>,
    ) = Playlist(
        id = id,
        title = title,
        description = description,
        curator = curator,
        followers = followers,
        palette = paletteFor(seed),
        trackIds = songs.map(Song::id),
        artworkUri = songs.firstNotNullOfOrNull(Song::artworkUri),
    )

    private fun sanitizeMetadata(raw: String?, fallback: String): String {
        val cleaned = raw
            ?.trim()
            ?.takeIf { it.isNotBlank() && !it.equals("<unknown>", ignoreCase = true) }
            ?: fallback
        return cleaned.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun loadGenreMap(resolver: ContentResolver): Map<Long, String> {
        val genres = mutableMapOf<Long, String>()
        val genreProjection = arrayOf(
            MediaStore.Audio.Genres._ID,
            MediaStore.Audio.Genres.NAME,
        )

        resolver.query(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            genreProjection,
            null,
            null,
            null,
        )?.use { genreCursor ->
            val genreIdColumn = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)
            val genreNameColumn = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

            while (genreCursor.moveToNext()) {
                val genreId = genreCursor.getLong(genreIdColumn)
                val genreName = sanitizeMetadata(
                    genreCursor.getString(genreNameColumn),
                    fallback = "Unclassified",
                )
                val membersUri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId)
                resolver.query(
                    membersUri,
                    arrayOf(MediaStore.Audio.Genres.Members.AUDIO_ID),
                    null,
                    null,
                    null,
                )?.use { memberCursor ->
                    val audioIdColumn = memberCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.AUDIO_ID)
                    while (memberCursor.moveToNext()) {
                        val audioId = memberCursor.getLong(audioIdColumn)
                        genres.putIfAbsent(audioId, genreName)
                    }
                }
            }
        }
        return genres
    }

    private fun stableId(seed: String): String = seed.hashCode().absoluteValue.toString()

    private fun albumArtUri(albumMediaId: Long?): String? {
        if (albumMediaId == null) return null
        return ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumMediaId,
        ).toString()
    }

    private fun deriveMood(title: String, artistName: String): String {
        val options = listOf("On-device glow", "Pocket session", "Offline pulse", "Late night local")
        return options[(title.length + artistName.length) % options.size]
    }

    private fun resolveFolderInfo(
        rawFolderValue: String?,
        isRelativePath: Boolean,
    ): FolderInfo? {
        val cleaned = rawFolderValue
            ?.trim()
            ?.trim('/')
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return if (isRelativePath) {
            val segments = cleaned.split('/').filter { it.isNotBlank() }
            if (segments.isEmpty()) {
                null
            } else {
                FolderInfo(
                    name = segments.last(),
                    path = segments.joinToString("/"),
                )
            }
        } else {
            val normalized = cleaned.replace('\\', '/')
            val path = normalized.substringBeforeLast('/', missingDelimiterValue = normalized)
            val name = path.substringAfterLast('/', missingDelimiterValue = path)
            if (name.isBlank()) {
                null
            } else {
                FolderInfo(
                    name = name,
                    path = path,
                )
            }
        }
    }

    private fun paletteFor(seed: String): AccentPalette {
        val hash = seed.hashCode().absoluteValue
        val hue = (hash % 360).toFloat()
        val secondaryHue = (hue + 38f) % 360f
        val tertiaryHue = (hue + 290f) % 360f
        return AccentPalette(
            background = hsv(hue, 0.56f, 0.14f),
            primary = hsv(hue, 0.68f, 0.92f),
            secondary = hsv(secondaryHue, 0.58f, 0.95f),
            tertiary = hsv(tertiaryHue, 0.48f, 0.96f),
            glow = hsv((hue + 18f) % 360f, 0.40f, 0.98f),
        )
    }

    private fun hsv(hue: Float, saturation: Float, value: Float): Color =
        Color.hsv(hue, saturation.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
}

private data class DeviceSongRow(
    val id: Long,
    val title: String,
    val artistName: String,
    val artistCredits: List<String>,
    val albumTitle: String,
    val genre: String?,
    val albumMediaId: Long?,
    val durationMs: Long,
    val dateAdded: Long,
    val year: Int?,
    val artworkUri: String?,
    val mediaUri: String,
    val folderName: String?,
    val folderPath: String?,
)

private data class FolderInfo(
    val name: String,
    val path: String,
)
