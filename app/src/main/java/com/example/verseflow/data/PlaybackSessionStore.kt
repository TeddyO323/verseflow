package com.example.verseflow.data

import android.content.Context
import com.example.verseflow.model.LyricsDisplayMode
import com.example.verseflow.model.RepeatMode
import org.json.JSONArray
import org.json.JSONObject

data class SavedPlaybackSession(
    val currentSongId: String?,
    val currentSongMediaUri: String?,
    val queueSongIds: List<String>,
    val queueSongMediaUris: List<String>,
    val currentIndex: Int,
    val positionMs: Long,
    val repeatMode: RepeatMode,
    val isShuffled: Boolean,
    val lyricsDisplayMode: LyricsDisplayMode,
)

class PlaybackSessionStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("verseflow_playback_session", Context.MODE_PRIVATE)

    fun load(): SavedPlaybackSession? {
        val payload = preferences.getString(KEY_SESSION, null) ?: return null
        val json = runCatching { JSONObject(payload) }.getOrNull() ?: return null
        val queueSongIds = json.optJSONArray("queueSongIds")?.toStringList().orEmpty()
        val queueSongMediaUris = json.optJSONArray("queueSongMediaUris")?.toStringList().orEmpty()
        if (queueSongIds.isEmpty() && queueSongMediaUris.isEmpty()) return null

        return SavedPlaybackSession(
            currentSongId = json.optString("currentSongId").takeIf(String::isNotBlank),
            currentSongMediaUri = json.optString("currentSongMediaUri").takeIf(String::isNotBlank),
            queueSongIds = queueSongIds,
            queueSongMediaUris = queueSongMediaUris,
            currentIndex = json.optInt("currentIndex", 0).coerceAtLeast(0),
            positionMs = json.optLong("positionMs", 0L).coerceAtLeast(0L),
            repeatMode = RepeatMode.entries.firstOrNull { it.name == json.optString("repeatMode") } ?: RepeatMode.All,
            isShuffled = json.optBoolean("isShuffled", false),
            lyricsDisplayMode = LyricsDisplayMode.entries.firstOrNull {
                it.name == json.optString("lyricsDisplayMode")
            } ?: LyricsDisplayMode.Synced,
        )
    }

    fun save(session: SavedPlaybackSession) {
        val payload = JSONObject().apply {
            put("currentSongId", session.currentSongId)
            put("currentSongMediaUri", session.currentSongMediaUri)
            put("currentIndex", session.currentIndex)
            put("positionMs", session.positionMs)
            put("repeatMode", session.repeatMode.name)
            put("isShuffled", session.isShuffled)
            put("lyricsDisplayMode", session.lyricsDisplayMode.name)
            put("queueSongIds", JSONArray().apply {
                session.queueSongIds.forEach(::put)
            })
            put("queueSongMediaUris", JSONArray().apply {
                session.queueSongMediaUris.forEach(::put)
            })
        }
        preferences.edit().putString(KEY_SESSION, payload.toString()).apply()
    }

    fun clear() {
        preferences.edit().remove(KEY_SESSION).apply()
    }

    private fun JSONArray.toStringList(): List<String> = buildList {
        for (index in 0 until length()) {
            optString(index)
                .takeIf(String::isNotBlank)
                ?.let(::add)
        }
    }

    private companion object {
        const val KEY_SESSION = "playback_session"
    }
}
