package com.example.verseflow.desktop

import org.json.JSONArray
import org.json.JSONObject
import java.util.prefs.Preferences

data class DesktopSettingsSnapshot(
    val displayName: String = "Music Lover",
    val selectedTheme: String,
    val isShuffleEnabled: Boolean = false,
    val isRepeatEnabled: Boolean = true,
    val autoRescanEnabled: Boolean = false,
    val musixmatchApiKey: String = "",
)

data class DesktopPlaybackSessionSnapshot(
    val currentTrackPath: String? = null,
    val queueTrackPaths: List<String> = emptyList(),
    val queueLabel: String = "All Songs",
    val positionMs: Long = 0L,
)

data class DesktopTrackMetadataOverride(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val genre: String? = null,
)

data class DesktopArtistProfileOverride(
    val photoPath: String? = null,
    val about: String? = null,
)

data class DesktopPlayHistoryEntry(
    val trackPath: String,
    val title: String,
    val artist: String,
    val album: String,
    val listenedMs: Long,
    val playedAtMs: Long,
)

class DesktopAppStore {
    private val preferences = Preferences.userRoot().node("com/example/verseflow/desktop")

    fun loadSettings(defaultTheme: String): DesktopSettingsSnapshot =
        DesktopSettingsSnapshot(
            displayName = preferences.get(KEY_DISPLAY_NAME, null)?.trim().orEmpty().ifBlank { "Music Lover" },
            selectedTheme = preferences.get(KEY_SELECTED_THEME, null)?.trim().orEmpty().ifBlank { defaultTheme },
            isShuffleEnabled = preferences.getBoolean(KEY_SHUFFLE_ENABLED, false),
            isRepeatEnabled = preferences.getBoolean(KEY_REPEAT_ENABLED, true),
            autoRescanEnabled = preferences.getBoolean(KEY_AUTO_RESCAN_ENABLED, false),
            musixmatchApiKey = preferences.get(KEY_MUSIXMATCH_API_KEY, null)?.trim().orEmpty(),
        )

    fun saveSettings(settings: DesktopSettingsSnapshot) {
        preferences.put(KEY_DISPLAY_NAME, settings.displayName.ifBlank { "Music Lover" })
        preferences.put(KEY_SELECTED_THEME, settings.selectedTheme)
        preferences.putBoolean(KEY_SHUFFLE_ENABLED, settings.isShuffleEnabled)
        preferences.putBoolean(KEY_REPEAT_ENABLED, settings.isRepeatEnabled)
        preferences.putBoolean(KEY_AUTO_RESCAN_ENABLED, settings.autoRescanEnabled)
        if (settings.musixmatchApiKey.isBlank()) {
            preferences.remove(KEY_MUSIXMATCH_API_KEY)
        } else {
            preferences.put(KEY_MUSIXMATCH_API_KEY, settings.musixmatchApiKey)
        }
    }

    fun loadPlaybackSession(): DesktopPlaybackSessionSnapshot? {
        val raw = preferences.get(KEY_PLAYBACK_SESSION, null)?.trim().orEmpty()
        if (raw.isBlank()) return null
        return runCatching {
            val payload = JSONObject(raw)
            DesktopPlaybackSessionSnapshot(
                currentTrackPath = payload.optString("currentTrackPath").ifBlank { null },
                queueTrackPaths = (payload.optJSONArray("queueTrackPaths") ?: JSONArray())
                    .let { queueArray ->
                        (0 until queueArray.length())
                            .map { index -> queueArray.optString(index).trim() }
                            .filter(String::isNotBlank)
                    },
                queueLabel = payload.optString("queueLabel").ifBlank { "All Songs" },
                positionMs = payload.optLong("positionMs").coerceAtLeast(0L),
            )
        }.getOrNull()
    }

    fun savePlaybackSession(snapshot: DesktopPlaybackSessionSnapshot?) {
        if (snapshot == null || snapshot.currentTrackPath.isNullOrBlank()) {
            preferences.remove(KEY_PLAYBACK_SESSION)
            return
        }
        val payload = JSONObject()
            .put("currentTrackPath", snapshot.currentTrackPath)
            .put("queueTrackPaths", JSONArray(snapshot.queueTrackPaths))
            .put("queueLabel", snapshot.queueLabel)
            .put("positionMs", snapshot.positionMs.coerceAtLeast(0L))
        preferences.put(KEY_PLAYBACK_SESSION, payload.toString())
    }

    fun loadRecentSearches(): List<String> =
        preferences.get(KEY_RECENT_SEARCHES, null)?.trim().orEmpty()
            .takeIf(String::isNotBlank)
            ?.let { raw ->
                runCatching {
                    val payload = JSONArray(raw)
                    (0 until payload.length())
                        .map { index -> payload.optString(index).trim() }
                        .filter(String::isNotBlank)
                }.getOrDefault(emptyList())
            }
            ?: emptyList()

    fun saveRecentSearches(searches: List<String>) {
        if (searches.isEmpty()) {
            preferences.remove(KEY_RECENT_SEARCHES)
            return
        }
        preferences.put(KEY_RECENT_SEARCHES, JSONArray(searches.distinct().take(MAX_RECENT_SEARCHES)).toString())
    }

    fun loadArtistSpotlightOrder(): List<String> =
        preferences.get(KEY_ARTIST_SPOTLIGHT_ORDER, null)?.trim().orEmpty()
            .takeIf(String::isNotBlank)
            ?.let { raw ->
                runCatching {
                    val payload = JSONArray(raw)
                    (0 until payload.length())
                        .map { index -> payload.optString(index).trim() }
                        .filter(String::isNotBlank)
                }.getOrDefault(emptyList())
            }
            ?: emptyList()

    fun saveArtistSpotlightOrder(artists: List<String>) {
        if (artists.isEmpty()) {
            preferences.remove(KEY_ARTIST_SPOTLIGHT_ORDER)
            return
        }
        preferences.put(KEY_ARTIST_SPOTLIGHT_ORDER, JSONArray(artists.distinct()).toString())
    }

    fun loadHiddenTrackPaths(): Set<String> =
        preferences.get(KEY_HIDDEN_TRACKS, null)?.trim().orEmpty()
            .takeIf(String::isNotBlank)
            ?.let { raw ->
                runCatching {
                    val payload = JSONArray(raw)
                    (0 until payload.length())
                        .map { index -> payload.optString(index).trim() }
                        .filter(String::isNotBlank)
                        .toSet()
                }.getOrDefault(emptySet())
            }
            ?: emptySet()

    fun saveHiddenTrackPaths(paths: Set<String>) {
        if (paths.isEmpty()) {
            preferences.remove(KEY_HIDDEN_TRACKS)
            return
        }
        preferences.put(KEY_HIDDEN_TRACKS, JSONArray(paths.toList().sorted()).toString())
    }

    fun loadTrackOverrides(): Map<String, DesktopTrackMetadataOverride> {
        val raw = preferences.get(KEY_TRACK_OVERRIDES, null)?.trim().orEmpty()
        if (raw.isBlank()) return emptyMap()

        return runCatching {
            val payload = JSONObject(raw)
            payload.keys().asSequence()
                .mapNotNull { path ->
                    val overridePayload = payload.optJSONObject(path) ?: return@mapNotNull null
                    path to DesktopTrackMetadataOverride(
                        title = overridePayload.optString("title").ifBlank { null },
                        artist = overridePayload.optString("artist").ifBlank { null },
                        album = overridePayload.optString("album").ifBlank { null },
                        genre = overridePayload.optString("genre").ifBlank { null },
                    )
                }
                .toMap()
        }.getOrDefault(emptyMap())
    }

    fun saveTrackOverrides(overrides: Map<String, DesktopTrackMetadataOverride>) {
        if (overrides.isEmpty()) {
            preferences.remove(KEY_TRACK_OVERRIDES)
            return
        }
        val payload = JSONObject()
        overrides.toSortedMap().forEach { (path, value) ->
            payload.put(
                path,
                JSONObject()
                    .put("title", value.title.orEmpty())
                    .put("artist", value.artist.orEmpty())
                    .put("album", value.album.orEmpty())
                    .put("genre", value.genre.orEmpty()),
            )
        }
        preferences.put(KEY_TRACK_OVERRIDES, payload.toString())
    }

    fun loadArtistProfileOverrides(): Map<String, DesktopArtistProfileOverride> {
        val raw = preferences.get(KEY_ARTIST_PROFILE_OVERRIDES, null)?.trim().orEmpty()
        if (raw.isBlank()) return emptyMap()

        return runCatching {
            val payload = JSONObject(raw)
            payload.keys().asSequence()
                .mapNotNull { artistName ->
                    val overridePayload = payload.optJSONObject(artistName) ?: return@mapNotNull null
                    artistName to DesktopArtistProfileOverride(
                        photoPath = overridePayload.optString("photoPath").ifBlank { null },
                        about = overridePayload.optString("about").ifBlank { null },
                    )
                }
                .toMap()
        }.getOrDefault(emptyMap())
    }

    fun saveArtistProfileOverrides(overrides: Map<String, DesktopArtistProfileOverride>) {
        if (overrides.isEmpty()) {
            preferences.remove(KEY_ARTIST_PROFILE_OVERRIDES)
            return
        }
        val payload = JSONObject()
        overrides.toSortedMap().forEach { (artistName, value) ->
            payload.put(
                artistName,
                JSONObject()
                    .put("photoPath", value.photoPath.orEmpty())
                    .put("about", value.about.orEmpty()),
            )
        }
        preferences.put(KEY_ARTIST_PROFILE_OVERRIDES, payload.toString())
    }

    fun loadPlayHistory(): List<DesktopPlayHistoryEntry> {
        val raw = preferences.get(KEY_PLAY_HISTORY, null)?.trim().orEmpty()
        if (raw.isBlank()) return emptyList()

        return runCatching {
            val payload = JSONArray(raw)
            (0 until payload.length())
                .mapNotNull { index ->
                    val item = payload.optJSONObject(index) ?: return@mapNotNull null
                    val trackPath = item.optString("trackPath").trim()
                    if (trackPath.isBlank()) return@mapNotNull null
                    DesktopPlayHistoryEntry(
                        trackPath = trackPath,
                        title = item.optString("title").ifBlank { trackPath.substringAfterLast('/') },
                        artist = item.optString("artist").ifBlank { "Unknown Artist" },
                        album = item.optString("album").ifBlank { "Unknown Album" },
                        listenedMs = item.optLong("listenedMs").takeIf { it > 0L } ?: item.optLong("durationMs").coerceAtLeast(0L),
                        playedAtMs = item.optLong("playedAtMs").coerceAtLeast(0L),
                    )
                }
        }.getOrDefault(emptyList())
    }

    fun savePlayHistory(entries: List<DesktopPlayHistoryEntry>) {
        if (entries.isEmpty()) {
            preferences.remove(KEY_PLAY_HISTORY)
            return
        }

        val payload = JSONArray()
        entries.take(MAX_PLAY_HISTORY_ENTRIES).forEach { entry ->
            payload.put(
                JSONObject()
                    .put("trackPath", entry.trackPath)
                    .put("title", entry.title)
                    .put("artist", entry.artist)
                    .put("album", entry.album)
                    .put("listenedMs", entry.listenedMs.coerceAtLeast(0L))
                    .put("playedAtMs", entry.playedAtMs.coerceAtLeast(0L)),
            )
        }
        preferences.put(KEY_PLAY_HISTORY, payload.toString())
    }

    private companion object {
        const val KEY_DISPLAY_NAME = "desktop_display_name"
        const val KEY_SELECTED_THEME = "desktop_selected_theme"
        const val KEY_SHUFFLE_ENABLED = "desktop_shuffle_enabled"
        const val KEY_REPEAT_ENABLED = "desktop_repeat_enabled"
        const val KEY_AUTO_RESCAN_ENABLED = "desktop_auto_rescan_enabled"
        const val KEY_MUSIXMATCH_API_KEY = "desktop_musixmatch_api_key"
        const val KEY_PLAYBACK_SESSION = "desktop_playback_session_json"
        const val KEY_RECENT_SEARCHES = "desktop_recent_searches_json"
        const val KEY_ARTIST_SPOTLIGHT_ORDER = "desktop_artist_spotlight_order_json"
        const val KEY_HIDDEN_TRACKS = "desktop_hidden_track_paths_json"
        const val KEY_TRACK_OVERRIDES = "desktop_track_overrides_json"
        const val KEY_ARTIST_PROFILE_OVERRIDES = "desktop_artist_profile_overrides_json"
        const val KEY_PLAY_HISTORY = "desktop_play_history_json"
        const val MAX_RECENT_SEARCHES = 10
        const val MAX_PLAY_HISTORY_ENTRIES = 2500
    }
}
