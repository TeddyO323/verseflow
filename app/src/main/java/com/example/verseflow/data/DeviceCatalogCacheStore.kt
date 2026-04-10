package com.example.verseflow.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.verseflow.model.AccentPalette
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.SongSource
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DeviceCatalogCacheStore(
    context: Context,
) {
    private val cacheFile = File(context.filesDir, "device_catalog_cache.json")

    fun load(): DeviceAudioCatalog? = runCatching {
        if (!cacheFile.exists()) return null
        val root = JSONObject(cacheFile.readText())
        DeviceAudioCatalog(
            songs = root.optJSONArray("songs").orEmpty().mapObjects(::songFromJson),
            albums = root.optJSONArray("albums").orEmpty().mapObjects(::albumFromJson),
            artists = root.optJSONArray("artists").orEmpty().mapObjects(::artistFromJson),
            playlists = root.optJSONArray("playlists").orEmpty().mapObjects(::playlistFromJson),
        )
    }.getOrNull()

    fun save(catalog: DeviceAudioCatalog) {
        runCatching {
            val root = JSONObject()
                .put("songs", JSONArray().apply { catalog.songs.forEach { put(it.toJson()) } })
                .put("albums", JSONArray().apply { catalog.albums.forEach { put(it.toJson()) } })
                .put("artists", JSONArray().apply { catalog.artists.forEach { put(it.toJson()) } })
                .put("playlists", JSONArray().apply { catalog.playlists.forEach { put(it.toJson()) } })
            cacheFile.writeText(root.toString())
        }
    }

    private fun Song.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("artistId", artistId)
        .put("albumId", albumId)
        .put("durationMs", durationMs)
        .put("genre", genre)
        .put("mood", mood)
        .put("palette", palette.toJson())
        .put("plainLyrics", JSONArray().apply { plainLyrics.forEach(::put) })
        .put("lyricsAttribution", lyricsAttribution)
        .put("isDownloaded", isDownloaded)
        .put("artworkUri", artworkUri)
        .put("mediaUri", mediaUri)
        .put("artistCredits", JSONArray().apply { artistCredits.forEach(::put) })
        .put("folderName", folderName)
        .put("folderPath", folderPath)
        .put("source", source.name)

    private fun Album.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("artistId", artistId)
        .put("year", year)
        .put("label", label)
        .put("description", description)
        .put("palette", palette.toJson())
        .put("trackIds", JSONArray().apply { trackIds.forEach(::put) })
        .put("artworkUri", artworkUri)

    private fun Artist.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("genre", genre)
        .put("monthlyListeners", monthlyListeners)
        .put("bio", bio)
        .put("heroPalette", heroPalette.toJson())
        .put("albumIds", JSONArray().apply { albumIds.forEach(::put) })
        .put("topTrackIds", JSONArray().apply { topTrackIds.forEach(::put) })
        .put("photoUri", photoUri)
        .put("trackCount", trackCount)
        .put("relatedArtistIds", JSONArray().apply { relatedArtistIds.forEach(::put) })

    private fun Playlist.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("description", description)
        .put("curator", curator)
        .put("followers", followers)
        .put("palette", palette.toJson())
        .put("trackIds", JSONArray().apply { trackIds.forEach(::put) })
        .put("artworkUri", artworkUri)
        .put("isUserCreated", isUserCreated)

    private fun AccentPalette.toJson(): JSONObject = JSONObject()
        .put("background", background.toArgb())
        .put("primary", primary.toArgb())
        .put("secondary", secondary.toArgb())
        .put("tertiary", tertiary.toArgb())
        .put("glow", glow.toArgb())

    private fun songFromJson(json: JSONObject): Song = Song(
        id = json.optString("id"),
        title = json.optString("title"),
        artistId = json.optString("artistId"),
        albumId = json.optString("albumId"),
        durationMs = json.optLong("durationMs"),
        genre = json.optString("genre").takeIf(String::isNotBlank),
        mood = json.optString("mood"),
        palette = json.optJSONObject("palette")?.toPalette() ?: fallbackPalette(),
        lyrics = emptyList(),
        plainLyrics = json.optJSONArray("plainLyrics").orEmpty().mapStrings(),
        lyricsAttribution = json.optString("lyricsAttribution").takeIf(String::isNotBlank),
        isDownloaded = json.optBoolean("isDownloaded", true),
        artworkUri = json.optString("artworkUri").takeIf(String::isNotBlank),
        mediaUri = json.optString("mediaUri").takeIf(String::isNotBlank),
        artistCredits = json.optJSONArray("artistCredits").orEmpty().mapStrings(),
        folderName = json.optString("folderName").takeIf(String::isNotBlank),
        folderPath = json.optString("folderPath").takeIf(String::isNotBlank),
        source = SongSource.entries.firstOrNull { it.name == json.optString("source") } ?: SongSource.Local,
    )

    private fun albumFromJson(json: JSONObject): Album = Album(
        id = json.optString("id"),
        title = json.optString("title"),
        artistId = json.optString("artistId"),
        year = json.optInt("year"),
        label = json.optString("label"),
        description = json.optString("description"),
        palette = json.optJSONObject("palette")?.toPalette() ?: fallbackPalette(),
        trackIds = json.optJSONArray("trackIds").orEmpty().mapStrings(),
        artworkUri = json.optString("artworkUri").takeIf(String::isNotBlank),
    )

    private fun artistFromJson(json: JSONObject): Artist = Artist(
        id = json.optString("id"),
        name = json.optString("name"),
        genre = json.optString("genre"),
        monthlyListeners = json.optString("monthlyListeners"),
        bio = json.optString("bio"),
        heroPalette = json.optJSONObject("heroPalette")?.toPalette() ?: fallbackPalette(),
        albumIds = json.optJSONArray("albumIds").orEmpty().mapStrings(),
        topTrackIds = json.optJSONArray("topTrackIds").orEmpty().mapStrings(),
        photoUri = json.optString("photoUri").takeIf(String::isNotBlank),
        trackCount = json.optInt("trackCount"),
        relatedArtistIds = json.optJSONArray("relatedArtistIds").orEmpty().mapStrings(),
    )

    private fun playlistFromJson(json: JSONObject): Playlist = Playlist(
        id = json.optString("id"),
        title = json.optString("title"),
        description = json.optString("description"),
        curator = json.optString("curator"),
        followers = json.optString("followers"),
        palette = json.optJSONObject("palette")?.toPalette() ?: fallbackPalette(),
        trackIds = json.optJSONArray("trackIds").orEmpty().mapStrings(),
        artworkUri = json.optString("artworkUri").takeIf(String::isNotBlank),
        isUserCreated = json.optBoolean("isUserCreated"),
    )

    private fun JSONObject.toPalette(): AccentPalette = AccentPalette(
        background = Color(optInt("background")),
        primary = Color(optInt("primary")),
        secondary = Color(optInt("secondary")),
        tertiary = Color(optInt("tertiary")),
        glow = Color(optInt("glow")),
    )

    private fun fallbackPalette(): AccentPalette = AccentPalette(
        background = Color(0xFF080B11),
        primary = Color(0xFF6A8CFF),
        secondary = Color(0xFF8AF5FF),
        tertiary = Color(0xFFB7C5FF),
        glow = Color(0xFF9FAFFF),
    )

    private fun JSONArray.mapStrings(): List<String> = buildList {
        for (index in 0 until length()) {
            optString(index).takeIf(String::isNotBlank)?.let(::add)
        }
    }

    private fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> = buildList {
        for (index in 0 until length()) {
            optJSONObject(index)?.let { add(transform(it)) }
        }
    }

    private fun JSONArray?.orEmpty(): JSONArray = this ?: JSONArray()
}
