package com.example.verseflow.data

import android.content.Context
import com.example.verseflow.model.ThemePreset
import com.example.verseflow.model.UserProfile
import com.example.verseflow.model.UserSettings

class UserPreferencesStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("verseflow_user_preferences", Context.MODE_PRIVATE)

    fun load(defaultProfile: UserProfile): UserProfile {
        val storedName = preferences.getString(KEY_NAME, defaultProfile.name).orEmpty()
        val storedHandle = preferences.getString(KEY_HANDLE, defaultProfile.handle).orEmpty()

        return defaultProfile.copy(
            name = storedName,
            handle = storedHandle.ifBlank { defaultProfile.handle },
            settings = loadSettings(defaultProfile.settings),
        )
    }

    fun saveProfileName(
        name: String,
        handle: String,
    ) {
        preferences.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_HANDLE, handle)
            .apply()
    }

    fun saveSettings(settings: UserSettings) {
        preferences.edit()
            .putString(KEY_THEME_PRESET, settings.themePreset.name)
            .putBoolean(KEY_AUTOPLAY, settings.autoplay)
            .putBoolean(KEY_IMMERSIVE_MOTION, settings.immersiveMotion)
            .putBoolean(KEY_SYNCED_LYRICS, settings.showSyncedLyricsByDefault)
            .putBoolean(KEY_WIFI_DOWNLOADS, settings.downloadOnWifiOnly)
            .putBoolean(KEY_EXPLICIT_CONTENT, settings.explicitContent)
            .putString(KEY_LANGUAGE, settings.language)
            .putBoolean(KEY_TEST_ARTWORK, settings.useTestArtwork)
            .apply()
    }

    private fun loadSettings(defaultSettings: UserSettings): UserSettings {
        val storedThemePreset = preferences.getString(KEY_THEME_PRESET, defaultSettings.themePreset.name)
        val themePreset = ThemePreset.entries.firstOrNull { it.name == storedThemePreset } ?: defaultSettings.themePreset

        return defaultSettings.copy(
            themePreset = themePreset,
            autoplay = preferences.getBoolean(KEY_AUTOPLAY, defaultSettings.autoplay),
            immersiveMotion = preferences.getBoolean(KEY_IMMERSIVE_MOTION, defaultSettings.immersiveMotion),
            showSyncedLyricsByDefault = preferences.getBoolean(KEY_SYNCED_LYRICS, defaultSettings.showSyncedLyricsByDefault),
            downloadOnWifiOnly = preferences.getBoolean(KEY_WIFI_DOWNLOADS, defaultSettings.downloadOnWifiOnly),
            explicitContent = preferences.getBoolean(KEY_EXPLICIT_CONTENT, defaultSettings.explicitContent),
            language = preferences.getString(KEY_LANGUAGE, defaultSettings.language).orEmpty().ifBlank {
                defaultSettings.language
            },
            useTestArtwork = preferences.getBoolean(KEY_TEST_ARTWORK, defaultSettings.useTestArtwork),
        )
    }

    private companion object {
        const val KEY_NAME = "profile_name"
        const val KEY_HANDLE = "profile_handle"
        const val KEY_THEME_PRESET = "theme_preset"
        const val KEY_AUTOPLAY = "autoplay"
        const val KEY_IMMERSIVE_MOTION = "immersive_motion"
        const val KEY_SYNCED_LYRICS = "synced_lyrics"
        const val KEY_WIFI_DOWNLOADS = "wifi_downloads"
        const val KEY_EXPLICIT_CONTENT = "explicit_content"
        const val KEY_LANGUAGE = "language"
        const val KEY_TEST_ARTWORK = "use_test_artwork"
    }
}
