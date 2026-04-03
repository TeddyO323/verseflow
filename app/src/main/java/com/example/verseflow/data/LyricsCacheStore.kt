package com.example.verseflow.data

import android.content.Context
import com.example.verseflow.model.LyricLine
import org.json.JSONArray
import org.json.JSONObject

data class CachedLyrics(
    val syncedLyrics: List<LyricLine>,
    val plainLyrics: List<String>,
    val attribution: String?,
)

class LyricsCacheStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("verseflow_lyrics_cache", Context.MODE_PRIVATE)

    fun load(mediaUri: String?): CachedLyrics? {
        val key = mediaUri?.trim()?.takeIf(String::isNotEmpty) ?: return null
        val payload = preferences.getString(key, null) ?: return null
        val json = runCatching { JSONObject(payload) }.getOrNull() ?: return null

        val syncedLyrics = json.optJSONArray("synced")
            ?.let(::parseSyncedLyrics)
            .orEmpty()
        val plainLyrics = json.optJSONArray("plain")
            ?.let(::parsePlainLyrics)
            .orEmpty()
        val attribution = json.optString("attribution").takeIf(String::isNotBlank)

        if (syncedLyrics.isEmpty() && plainLyrics.isEmpty()) return null
        return CachedLyrics(
            syncedLyrics = syncedLyrics,
            plainLyrics = plainLyrics,
            attribution = attribution,
        )
    }

    fun save(
        mediaUri: String?,
        syncedLyrics: List<LyricLine>,
        plainLyrics: List<String>,
        attribution: String?,
    ) {
        val key = mediaUri?.trim()?.takeIf(String::isNotEmpty) ?: return
        if (syncedLyrics.isEmpty() && plainLyrics.isEmpty()) return

        val payload = JSONObject().apply {
            put("synced", JSONArray().apply {
                syncedLyrics.forEach { line ->
                    put(
                        JSONObject().apply {
                            put("timestampMs", line.timestampMs)
                            put("text", line.text)
                        },
                    )
                }
            })
            put("plain", JSONArray().apply {
                plainLyrics.forEach(::put)
            })
            put("attribution", attribution)
        }

        preferences.edit().putString(key, payload.toString()).apply()
    }

    fun remove(mediaUri: String?) {
        val key = mediaUri?.trim()?.takeIf(String::isNotEmpty) ?: return
        preferences.edit().remove(key).apply()
    }

    private fun parseSyncedLyrics(array: JSONArray): List<LyricLine> = buildList {
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val timestampMs = item.optLong("timestampMs", Long.MIN_VALUE)
            val text = item.optString("text")
            if (timestampMs == Long.MIN_VALUE || text.isBlank()) continue
            add(LyricLine(timestampMs = timestampMs, text = text))
        }
    }

    private fun parsePlainLyrics(array: JSONArray): List<String> = buildList {
        for (index in 0 until array.length()) {
            array.optString(index)
                .takeIf(String::isNotBlank)
                ?.let(::add)
        }
    }
}
