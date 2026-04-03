package com.example.verseflow.data

import android.content.Context
import org.json.JSONObject

data class SongMetadataOverride(
    val title: String? = null,
    val artistName: String? = null,
    val albumTitle: String? = null,
    val genre: String? = null,
) {
    fun isEmpty(): Boolean = title == null && artistName == null && albumTitle == null && genre == null
}

data class LibraryCustomizations(
    val hiddenSongIds: Set<String> = emptySet(),
    val songMetadataOverrides: Map<String, SongMetadataOverride> = emptyMap(),
)

class LibraryCustomizationStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("verseflow_library_customizations", Context.MODE_PRIVATE)

    fun load(): LibraryCustomizations {
        val hiddenSongIds = preferences.getStringSet(KEY_HIDDEN_SONG_IDS, emptySet()).orEmpty()
        val overridesPayload = preferences.getString(KEY_SONG_OVERRIDES, null).orEmpty()
        val songMetadataOverrides = parseOverrides(overridesPayload)
        return LibraryCustomizations(
            hiddenSongIds = hiddenSongIds,
            songMetadataOverrides = songMetadataOverrides,
        )
    }

    fun saveHiddenSongIds(songIds: Set<String>) {
        preferences.edit()
            .putStringSet(KEY_HIDDEN_SONG_IDS, songIds)
            .apply()
    }

    fun saveSongMetadataOverrides(overrides: Map<String, SongMetadataOverride>) {
        val payload = JSONObject().apply {
            overrides.forEach { (songId, override) ->
                if (!override.isEmpty()) {
                    put(
                        songId,
                        JSONObject().apply {
                            put("title", override.title)
                            put("artistName", override.artistName)
                            put("albumTitle", override.albumTitle)
                            put("genre", override.genre)
                        },
                    )
                }
            }
        }

        preferences.edit()
            .putString(KEY_SONG_OVERRIDES, payload.toString())
            .apply()
    }

    private fun parseOverrides(payload: String): Map<String, SongMetadataOverride> {
        if (payload.isBlank()) return emptyMap()
        val root = runCatching { JSONObject(payload) }.getOrNull() ?: return emptyMap()
        return buildMap {
            root.keys().forEach { songId ->
                val item = root.optJSONObject(songId) ?: return@forEach
                val override = SongMetadataOverride(
                    title = item.optString("title").takeIf(String::isNotBlank),
                    artistName = item.optString("artistName").takeIf(String::isNotBlank),
                    albumTitle = item.optString("albumTitle").takeIf(String::isNotBlank),
                    genre = item.optString("genre").takeIf(String::isNotBlank),
                )
                if (!override.isEmpty()) {
                    put(songId, override)
                }
            }
        }
    }

    private companion object {
        const val KEY_HIDDEN_SONG_IDS = "hidden_song_ids"
        const val KEY_SONG_OVERRIDES = "song_metadata_overrides"
    }
}
