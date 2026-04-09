package com.example.verseflow.data

import android.content.Context
import com.example.verseflow.model.PlayHistoryEntry
import org.json.JSONArray
import org.json.JSONObject

class PlayHistoryStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("verseflow_play_history", Context.MODE_PRIVATE)

    fun load(): List<PlayHistoryEntry> {
        val payload = preferences.getString(KEY_HISTORY, null).orEmpty()
        if (payload.isBlank()) return emptyList()
        val array = runCatching { JSONArray(payload) }.getOrNull() ?: return emptyList()
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val songId = item.optString("songId").takeIf(String::isNotBlank) ?: continue
                add(
                    PlayHistoryEntry(
                        songId = songId,
                        title = item.optString("title"),
                        artistName = item.optString("artistName"),
                        albumTitle = item.optString("albumTitle"),
                        listenedMs = item.optLong("listenedMs"),
                        playedAtMs = item.optLong("playedAtMs"),
                        artworkUri = item.optString("artworkUri").takeIf(String::isNotBlank),
                        fallbackMediaUri = item.optString("fallbackMediaUri").takeIf(String::isNotBlank),
                    ),
                )
            }
        }.sortedByDescending(PlayHistoryEntry::playedAtMs)
    }

    fun save(entries: List<PlayHistoryEntry>) {
        val sanitizedEntries = entries
            .asSequence()
            .map { entry ->
                entry.copy(
                    songId = entry.songId.take(MAX_VALUE_LENGTH),
                    title = entry.title.take(MAX_TEXT_LENGTH),
                    artistName = entry.artistName.take(MAX_TEXT_LENGTH),
                    albumTitle = entry.albumTitle.take(MAX_TEXT_LENGTH),
                    artworkUri = entry.artworkUri?.take(MAX_URI_LENGTH),
                    fallbackMediaUri = entry.fallbackMediaUri?.take(MAX_URI_LENGTH),
                    listenedMs = entry.listenedMs.coerceAtLeast(0L),
                )
            }
            .take(MAX_ENTRIES)
            .toList()

        val payload = JSONArray().apply {
            sanitizedEntries.forEach { entry ->
                put(
                    JSONObject().apply {
                        put("songId", entry.songId)
                        put("title", entry.title)
                        put("artistName", entry.artistName)
                        put("albumTitle", entry.albumTitle)
                        put("listenedMs", entry.listenedMs)
                        put("playedAtMs", entry.playedAtMs)
                        put("artworkUri", entry.artworkUri)
                        put("fallbackMediaUri", entry.fallbackMediaUri)
                    },
                )
            }
        }.toString()

        if (payload.length > MAX_PAYLOAD_LENGTH) {
            preferences.edit().remove(KEY_HISTORY).apply()
            return
        }

        preferences.edit()
            .putString(KEY_HISTORY, payload)
            .apply()
    }

    private companion object {
        const val KEY_HISTORY = "play_history"
        const val MAX_ENTRIES = 240
        const val MAX_TEXT_LENGTH = 180
        const val MAX_VALUE_LENGTH = 256
        const val MAX_URI_LENGTH = 1_024
        const val MAX_PAYLOAD_LENGTH = 60_000
    }
}
