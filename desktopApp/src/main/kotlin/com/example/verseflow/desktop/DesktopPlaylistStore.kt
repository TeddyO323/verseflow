package com.example.verseflow.desktop

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.prefs.Preferences

data class DesktopUserPlaylist(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val trackPaths: List<String> = emptyList(),
)

class DesktopPlaylistStore {
    private val preferences = Preferences.userRoot().node("com/example/verseflow/desktop")

    fun loadPlaylists(): List<DesktopUserPlaylist> {
        val raw = preferences.get(KEY_USER_PLAYLISTS, null)?.trim().orEmpty()
        if (raw.isBlank()) return emptyList()

        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length())
                .mapNotNull(array::optJSONObject)
                .mapNotNull { payload ->
                    val id = payload.optString("id").ifBlank { UUID.randomUUID().toString() }
                    val title = payload.optString("title").trim()
                    if (title.isBlank()) return@mapNotNull null
                    DesktopUserPlaylist(
                        id = id,
                        title = title,
                        description = payload.optString("description").trim(),
                        trackPaths = (payload.optJSONArray("trackPaths") ?: JSONArray())
                            .let { trackArray ->
                                (0 until trackArray.length())
                                    .map { index -> trackArray.optString(index).trim() }
                                    .filter(String::isNotBlank)
                            },
                    )
                }
        }.getOrDefault(emptyList())
    }

    fun savePlaylists(playlists: List<DesktopUserPlaylist>) {
        val payload = JSONArray(
            playlists.map { playlist ->
                JSONObject()
                    .put("id", playlist.id)
                    .put("title", playlist.title)
                    .put("description", playlist.description)
                    .put("trackPaths", JSONArray(playlist.trackPaths))
            },
        )
        preferences.put(KEY_USER_PLAYLISTS, payload.toString())
    }

    private companion object {
        const val KEY_USER_PLAYLISTS = "user_playlists_json"
    }
}
