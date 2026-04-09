package com.example.verseflow.data

import android.content.Context

data class ArtistProfileOverride(
    val bio: String? = null,
    val photoUri: String? = null,
) {
    fun isEmpty(): Boolean = bio.isNullOrBlank() && photoUri.isNullOrBlank()
}

class ArtistProfileOverrideStore(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("verseflow_artist_overrides", Context.MODE_PRIVATE)

    fun load(): Map<String, ArtistProfileOverride> {
        val keys = preferences.getStringSet(KEY_KNOWN_KEYS, emptySet()).orEmpty()
        return buildMap {
            keys.forEach { key ->
                val override = ArtistProfileOverride(
                    bio = preferences.getString("${key}_bio", null)?.takeIf(String::isNotBlank),
                    photoUri = preferences.getString("${key}_photo_uri", null)?.takeIf(String::isNotBlank),
                )
                if (!override.isEmpty()) {
                    put(key, override)
                }
            }
        }
    }

    fun saveArtist(name: String, override: ArtistProfileOverride) {
        val key = artistOverrideKey(name)
        val knownKeys = preferences.getStringSet(KEY_KNOWN_KEYS, emptySet()).orEmpty().toMutableSet()
        val edit = preferences.edit()
        if (override.isEmpty()) {
            knownKeys.remove(key)
            edit.remove("${key}_bio")
            edit.remove("${key}_photo_uri")
        } else {
            knownKeys.add(key)
            edit.putString("${key}_bio", override.bio?.take(MAX_BIO_LENGTH))
            edit.putString("${key}_photo_uri", override.photoUri?.take(MAX_URI_LENGTH))
        }
        edit.putStringSet(KEY_KNOWN_KEYS, knownKeys).apply()
    }

    companion object {
        private const val KEY_KNOWN_KEYS = "artist_override_keys"
        private const val MAX_BIO_LENGTH = 1_800
        private const val MAX_URI_LENGTH = 1_024

        fun artistOverrideKey(name: String): String = name.trim().lowercase()
    }
}
