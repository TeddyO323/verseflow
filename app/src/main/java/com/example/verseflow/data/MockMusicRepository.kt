package com.example.verseflow.data

import androidx.compose.ui.graphics.Color
import com.example.verseflow.model.AccentPalette
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.LyricLine
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.ThemePreset
import com.example.verseflow.model.UserProfile
import com.example.verseflow.model.UserSettings

class MockMusicRepository : MusicRepository {

    private val neonPulse = AccentPalette(
        background = Color(0xFF070B16),
        primary = Color(0xFF6A8CFF),
        secondary = Color(0xFF8AF5FF),
        tertiary = Color(0xFFFF69C7),
        glow = Color(0xFFB18CFF),
    )
    private val solarEcho = AccentPalette(
        background = Color(0xFF0C0816),
        primary = Color(0xFFFF8D6B),
        secondary = Color(0xFFFFC66D),
        tertiary = Color(0xFFE96BFF),
        glow = Color(0xFFFF8F70),
    )
    private val auroraGlass = AccentPalette(
        background = Color(0xFF04121A),
        primary = Color(0xFF66F0E7),
        secondary = Color(0xFF3BA0FF),
        tertiary = Color(0xFF8EE1FF),
        glow = Color(0xFF5DF9D9),
    )
    private val velvetSignal = AccentPalette(
        background = Color(0xFF120713),
        primary = Color(0xFFF26CC2),
        secondary = Color(0xFF8E63FF),
        tertiary = Color(0xFFFFB7E8),
        glow = Color(0xFFF982D4),
    )
    private val emberDrive = AccentPalette(
        background = Color(0xFF140B08),
        primary = Color(0xFFFF9365),
        secondary = Color(0xFFFFD76C),
        tertiary = Color(0xFFFFB59F),
        glow = Color(0xFFFFA779),
    )

    private fun lyrics(vararg lines: Pair<Long, String>) = lines.map { LyricLine(it.first, it.second) }

    private val songs = listOf(
        Song(
            id = "song_midnight_circuit",
            title = "Midnight Circuit",
            artistId = "artist_nova_rey",
            albumId = "album_afterglow_protocol",
            durationMs = 214_000L,
            mood = "Neon drive",
            palette = neonPulse,
            isDownloaded = true,
            lyrics = lyrics(
                0L to "Streetlights shimmer on the glass like code in motion",
                12_000L to "I hear the skyline breathing through the radio",
                28_000L to "Every signal bends your name into the dark",
                45_000L to "You pull me closer than the midnight sparks",
                63_000L to "We run the circuit till the silence glows",
                82_000L to "Chrome reflections folding into aftershows",
                102_000L to "If the city breaks, we dance inside the seam",
                125_000L to "Turning static into something we can dream",
                149_000L to "Hold the pulse, hold the pulse, don't let it fade",
                176_000L to "We're the light the night machines have made",
            ),
        ),
        Song(
            id = "song_solar_bloom",
            title = "Solar Bloom",
            artistId = "artist_nova_rey",
            albumId = "album_afterglow_protocol",
            durationMs = 198_000L,
            mood = "Cinematic lift",
            palette = solarEcho,
            lyrics = lyrics(
                0L to "Morning spills in fragments through a violet frame",
                14_000L to "You turn the room electric when you say my name",
                30_000L to "Every quiet corner catches fire in gold",
                48_000L to "We make a future out of what we hold",
                67_000L to "Bloom in the static, bright on the edge",
                87_000L to "One more orbit, one more promise, one more ledge",
                110_000L to "When the daylight cuts across the haze",
                131_000L to "I still see your shadow setting suns ablaze",
                154_000L to "Solar bloom, don't leave me waiting in the blue",
                177_000L to "Every horizon opens when I move with you",
            ),
        ),
        Song(
            id = "song_echoes_in_glass",
            title = "Echoes in Glass",
            artistId = "artist_lyra_vale",
            albumId = "album_glass_horizon",
            durationMs = 226_000L,
            mood = "Liquid ether",
            palette = auroraGlass,
            isDownloaded = true,
            lyrics = lyrics(
                0L to "Silver rain sketches constellations on the train",
                13_000L to "I taste your silhouette in every windowpane",
                29_000L to "The city folds like crystal under pressure and light",
                47_000L to "Your voice keeps breaking open all the blue in the night",
                68_000L to "Echoes in glass, glittering slow",
                89_000L to "All the things we never said are starting to show",
                111_000L to "If I touch the surface will the memory bend",
                136_000L to "Or will it bloom and pull the dark apart again",
                162_000L to "Stay in the shimmer, don't dissolve away",
                188_000L to "I'm still hearing you in every fractured ray",
            ),
        ),
        Song(
            id = "song_gravity_skin",
            title = "Gravity Skin",
            artistId = "artist_lyra_vale",
            albumId = "album_glass_horizon",
            durationMs = 207_000L,
            mood = "Slow pulse",
            palette = velvetSignal,
            lyrics = lyrics(
                0L to "Soft magnetic pressure under ultraviolet skies",
                15_000L to "You hold the room in orbit with your half-lit eyes",
                32_000L to "Every heartbeat lands with cinematic weight",
                50_000L to "Like we're suspended at the edge of what we make",
                70_000L to "Gravity skin, pull me in",
                91_000L to "Let the silence ripple where the sparks have been",
                115_000L to "Even the dark starts to glow from within",
                139_000L to "When your touch writes voltage under gravity skin",
                165_000L to "Keep the world outside the velvet noise",
                187_000L to "We're all signal when we lose the choice",
            ),
        ),
        Song(
            id = "song_neon_tide",
            title = "Neon Tide",
            artistId = "artist_orion_lane",
            albumId = "album_night_drive_syntax",
            durationMs = 222_000L,
            mood = "Night cruise",
            palette = emberDrive,
            lyrics = lyrics(
                0L to "Brake lights swimming through the rain like laser flares",
                14_000L to "Your laughter cuts a silver path through midnight air",
                31_000L to "We keep the ocean in the speakers turned up high",
                49_000L to "A tidal synth beneath a restless city sky",
                69_000L to "Neon tide, carry us past the exit signs",
                91_000L to "Every mile is writing constellations in your lines",
                114_000L to "Leave the static in the rearview glow",
                138_000L to "We were born for roads that only after-hours know",
                165_000L to "If dawn appears, we'll paint it with the bass",
                191_000L to "A little faster till the dark forgets our names",
            ),
        ),
        Song(
            id = "song_velvet_static",
            title = "Velvet Static",
            artistId = "artist_sora_prism",
            albumId = "album_echo_bloom",
            durationMs = 236_000L,
            mood = "Dream pop haze",
            palette = velvetSignal,
            isDownloaded = true,
            lyrics = lyrics(
                0L to "You arrive like color in a monochrome room",
                13_000L to "Low frequency thunder wrapped in perfume",
                29_000L to "All my sharp edges blur into the beat",
                48_000L to "When your shadow and the subline meet",
                68_000L to "Velvet static on my skin tonight",
                88_000L to "Every whispered signal turning fluorescent white",
                111_000L to "Stay suspended where the chorus bends",
                137_000L to "I don't want this gravity to end",
                164_000L to "If the whole room flickers, let it bloom",
                191_000L to "We'll be the last bright sound inside the gloom",
            ),
        ),
        Song(
            id = "song_halo_over_concrete",
            title = "Halo Over Concrete",
            artistId = "artist_atlas_bloom",
            albumId = "album_celestial_static",
            durationMs = 212_000L,
            mood = "Epic ascent",
            palette = auroraGlass,
            lyrics = lyrics(
                0L to "Warm engines hum beneath a pale electric dawn",
                15_000L to "The skyline opens like a page we've drawn upon",
                33_000L to "I feel the city lift beneath our racing feet",
                52_000L to "A golden pressure pushing sunlight through the street",
                72_000L to "Halo over concrete, rise with me",
                94_000L to "Every rooftop is a door the day can see",
                118_000L to "We don't need permission for a brighter scene",
                143_000L to "Just a pulse, a promise, and a little velocity",
                168_000L to "When the morning turns the edges clean",
                190_000L to "We're the echo of the dream beneath the beams",
            ),
        ),
    )

    private val albums = listOf(
        Album(
            id = "album_afterglow_protocol",
            title = "Afterglow Protocol",
            artistId = "artist_nova_rey",
            year = 2026,
            label = "Aether Labs",
            description = "A widescreen synth-pop record built for high-rise reflections and impossible sunsets.",
            palette = neonPulse,
            trackIds = listOf("song_midnight_circuit", "song_solar_bloom"),
        ),
        Album(
            id = "album_glass_horizon",
            title = "Glass Horizon",
            artistId = "artist_lyra_vale",
            year = 2025,
            label = "Silica House",
            description = "Liquid electronica with fragile vocals, prism textures, and luminous low-end.",
            palette = auroraGlass,
            trackIds = listOf("song_echoes_in_glass", "song_gravity_skin"),
        ),
        Album(
            id = "album_night_drive_syntax",
            title = "Night Drive Syntax",
            artistId = "artist_orion_lane",
            year = 2026,
            label = "North Lane Audio",
            description = "A cinematic freeway EP with warm analog bass and rain-slicked drums.",
            palette = emberDrive,
            trackIds = listOf("song_neon_tide"),
        ),
        Album(
            id = "album_echo_bloom",
            title = "Echo Bloom",
            artistId = "artist_sora_prism",
            year = 2024,
            label = "Prism Source",
            description = "Dream-pop coded for velvet club lighting and soft-focus heartbreak.",
            palette = velvetSignal,
            trackIds = listOf("song_velvet_static"),
        ),
        Album(
            id = "album_celestial_static",
            title = "Celestial Static",
            artistId = "artist_atlas_bloom",
            year = 2026,
            label = "Morning Orbit",
            description = "Future soul for first light, rooftop air, and starting over at full volume.",
            palette = solarEcho,
            trackIds = listOf("song_halo_over_concrete"),
        ),
    )

    private val artists = listOf(
        Artist(
            id = "artist_nova_rey",
            name = "Nova Rey",
            genre = "Cinematic Synth-Pop",
            monthlyListeners = "4.8M monthly listeners",
            bio = "Nova Rey writes future-facing pop with wide-screen pads, vapor trails of guitar, and hooks that feel engineered for skyline windows.",
            heroPalette = neonPulse,
            albumIds = listOf("album_afterglow_protocol"),
            topTrackIds = listOf("song_midnight_circuit", "song_solar_bloom"),
            relatedArtistIds = listOf("artist_lyra_vale", "artist_orion_lane"),
        ),
        Artist(
            id = "artist_lyra_vale",
            name = "Lyra Vale",
            genre = "Glass Electronica",
            monthlyListeners = "3.1M monthly listeners",
            bio = "Lyra Vale layers fragile vocals over crystalline rhythms and aquatic synth design, creating songs that feel like light passing through architecture.",
            heroPalette = auroraGlass,
            albumIds = listOf("album_glass_horizon"),
            topTrackIds = listOf("song_echoes_in_glass", "song_gravity_skin"),
            relatedArtistIds = listOf("artist_sora_prism", "artist_nova_rey"),
        ),
        Artist(
            id = "artist_orion_lane",
            name = "Orion Lane",
            genre = "Midnight Electronica",
            monthlyListeners = "2.6M monthly listeners",
            bio = "Orion Lane makes motion-driven electronic records inspired by rain-slick asphalt, underpasses, and the poetry of long drives after midnight.",
            heroPalette = emberDrive,
            albumIds = listOf("album_night_drive_syntax"),
            topTrackIds = listOf("song_neon_tide"),
            relatedArtistIds = listOf("artist_nova_rey", "artist_atlas_bloom"),
        ),
        Artist(
            id = "artist_sora_prism",
            name = "Sora Prism",
            genre = "Future Dream Pop",
            monthlyListeners = "5.2M monthly listeners",
            bio = "Sora Prism bends dream-pop into something immersive and tactile, with neon gradients, soft percussion, and intimate hooks.",
            heroPalette = velvetSignal,
            albumIds = listOf("album_echo_bloom"),
            topTrackIds = listOf("song_velvet_static"),
            relatedArtistIds = listOf("artist_lyra_vale", "artist_nova_rey"),
        ),
        Artist(
            id = "artist_atlas_bloom",
            name = "Atlas Bloom",
            genre = "Future Soul",
            monthlyListeners = "1.9M monthly listeners",
            bio = "Atlas Bloom blends warm soul progressions with sleek electronic production for songs that feel hopeful, expansive, and built for morning motion.",
            heroPalette = solarEcho,
            albumIds = listOf("album_celestial_static"),
            topTrackIds = listOf("song_halo_over_concrete"),
            relatedArtistIds = listOf("artist_orion_lane", "artist_nova_rey"),
        ),
    )

    private val playlists = listOf(
        Playlist(
            id = "playlist_skyline_after_hours",
            title = "Skyline After Hours",
            description = "Late-night synth reflections, velvet bass, and pulse-lit hooks.",
            curator = "VerseFlow Editorial",
            followers = "1.2M saves",
            palette = neonPulse,
            trackIds = listOf(
                "song_midnight_circuit",
                "song_neon_tide",
                "song_echoes_in_glass",
                "song_velvet_static",
            ),
        ),
        Playlist(
            id = "playlist_luminous_focus",
            title = "Luminous Focus",
            description = "Soft kinetic tracks for deep work, night coding, and clean concentration.",
            curator = "Ari N.",
            followers = "846K saves",
            palette = auroraGlass,
            trackIds = listOf(
                "song_echoes_in_glass",
                "song_halo_over_concrete",
                "song_solar_bloom",
                "song_gravity_skin",
            ),
        ),
        Playlist(
            id = "playlist_velvet_hearts",
            title = "Velvet Hearts",
            description = "Romantic future-pop with beautiful breakdowns and luminous choruses.",
            curator = "VerseFlow Studio",
            followers = "642K saves",
            palette = velvetSignal,
            trackIds = listOf(
                "song_velvet_static",
                "song_gravity_skin",
                "song_midnight_circuit",
                "song_solar_bloom",
            ),
        ),
        Playlist(
            id = "playlist_orbit_run",
            title = "Orbit Run",
            description = "Momentum records for city runs, metro rides, and sunrise resets.",
            curator = "Coach Mira",
            followers = "512K saves",
            palette = emberDrive,
            trackIds = listOf(
                "song_neon_tide",
                "song_halo_over_concrete",
                "song_solar_bloom",
                "song_midnight_circuit",
            ),
        ),
    )

    private val profile = UserProfile(
        name = "",
        handle = "@musiclover",
        membershipTier = "VerseFlow Nova",
        avatarPalette = neonPulse,
        settings = UserSettings(
            themePreset = ThemePreset.Nebula,
            autoplay = true,
            immersiveMotion = true,
            showSyncedLyricsByDefault = true,
            downloadOnWifiOnly = true,
            explicitContent = false,
            language = "English",
        ),
    )

    override fun profile(): UserProfile = profile

    override fun songs(): List<Song> = songs

    override fun albums(): List<Album> = albums

    override fun artists(): List<Artist> = artists

    override fun playlists(): List<Playlist> = playlists

    override fun featuredAlbumIds(): List<String> = listOf(
        "album_afterglow_protocol",
        "album_glass_horizon",
        "album_night_drive_syntax",
    )

    override fun recentlyPlayedIds(): List<String> = listOf(
        "song_midnight_circuit",
        "song_velvet_static",
        "song_echoes_in_glass",
        "song_neon_tide",
    )

    override fun trendingSongIds(): List<String> = listOf(
        "song_solar_bloom",
        "song_midnight_circuit",
        "song_halo_over_concrete",
        "song_echoes_in_glass",
        "song_velvet_static",
    )

    override fun favoritePlaylistIds(): List<String> = listOf(
        "playlist_skyline_after_hours",
        "playlist_luminous_focus",
        "playlist_velvet_hearts",
    )

    override fun recentSearches(): List<String> = listOf(
        "Nova Rey",
        "Midnight Circuit",
        "Skyline After Hours",
        "Glass Horizon",
    )

    override fun trendingCategories(): List<String> = listOf(
        "Neo Soul",
        "Midnight Drive",
        "Cinematic Pop",
        "Future R&B",
        "Lyric Focus",
        "Dream Electronica",
    )
}
