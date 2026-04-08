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

        val payload = buildPayload(
            syncedLyrics = syncedLyrics,
            plainLyrics = plainLyrics,
            attribution = attribution,
        ) ?: return

        runCatching {
            preferences.edit().putString(key, payload).apply()
        }.onFailure {
            preferences.edit().remove(key).apply()
        }
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

    private fun buildPayload(
        syncedLyrics: List<LyricLine>,
        plainLyrics: List<String>,
        attribution: String?,
    ): String? {
        val sanitizedAttribution = attribution?.takeIf(String::isNotBlank)?.take(MAX_ATTRIBUTION_LENGTH)
        val trimmedSyncedLyrics = syncedLyrics.take(MAX_SYNCED_LINES).map { line ->
            line.copy(text = line.text.take(MAX_LINE_LENGTH))
        }
        val trimmedPlainLyrics = plainLyrics.take(MAX_PLAIN_LINES).map { line ->
            line.take(MAX_LINE_LENGTH)
        }

        val payload = JSONObject().apply {
            put("synced", JSONArray().apply {
                trimmedSyncedLyrics.forEach { line ->
                    put(
                        JSONObject().apply {
                            put("timestampMs", line.timestampMs)
                            put("text", line.text)
                        },
                    )
                }
            })
            put("plain", JSONArray().apply {
                trimmedPlainLyrics.forEach(::put)
            })
            put("attribution", sanitizedAttribution)
        }.toString()

        if (payload.length <= MAX_PAYLOAD_LENGTH) return payload

        val fallbackPayload = JSONObject().apply {
            put("synced", JSONArray())
            put("plain", JSONArray().apply {
                trimmedPlainLyrics.take(FALLBACK_PLAIN_LINES).forEach(::put)
            })
            put("attribution", sanitizedAttribution)
        }.toString()

        return fallbackPayload.takeIf { it.length <= MAX_PAYLOAD_LENGTH }
    }

    private companion object {
        const val MAX_SYNCED_LINES = 400
        const val MAX_PLAIN_LINES = 400
        const val FALLBACK_PLAIN_LINES = 180
        const val MAX_LINE_LENGTH = 220
        const val MAX_ATTRIBUTION_LENGTH = 160
        const val MAX_PAYLOAD_LENGTH = 48_000
    }
}
