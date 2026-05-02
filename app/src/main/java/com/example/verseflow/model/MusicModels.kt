package com.example.verseflow.model

import androidx.compose.ui.graphics.Color

enum class LibraryTab(val title: String) {
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists"),
    Playlists("Playlists"),
    Folders("Folders"),
    Genres("Genres"),
}

enum class LibrarySort(val label: String) {
    Recent("Recent"),
    Title("A-Z"),
    Duration("Length"),
}

enum class LibraryFilter(val label: String) {
    All("All"),
    Favorites("Favorites"),
    Downloaded("Downloaded"),
}

enum class RepeatMode {
    Off,
    All,
    One,
}

enum class LyricsDisplayMode(val label: String) {
    Synced("Synced"),
    Plain("Plain"),
}

enum class ThemePreset(val label: String) {
    Nebula("Nebula Dark"),
    Eclipse("Eclipse OLED"),
    Crimson("Crimson Velvet"),
    Solar("Solar Gold"),
    Cobalt("Cobalt Luxe"),
    Arctic("Arctic Light"),
    Rose("Rose Studio"),
    Mint("Mint Daybreak"),
    Amber("Amber Paper"),
    Mono("Mono Mist"),
}

enum class MusicCatalogSource {
    Demo,
    Device,
}

enum class SongSource {
    Mock,
    Local,
}

enum class LyricsLoadState {
    Idle,
    Loading,
    Ready,
    Unavailable,
}

data class AccentPalette(
    val background: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val glow: Color,
)

data class LyricLine(
    val timestampMs: Long,
    val text: String,
)

data class LyricsSearchCandidate(
    val id: String,
    val title: String,
    val artistName: String,
    val albumTitle: String?,
    val durationMs: Long?,
    val sourceLabel: String,
    val attribution: String,
    val syncedLyrics: List<LyricLine> = emptyList(),
    val plainLyrics: List<String> = emptyList(),
    val matchScore: Double = 0.0,
) {
    val hasSyncedLyrics: Boolean
        get() = syncedLyrics.isNotEmpty()

    val hasPlainLyrics: Boolean
        get() = plainLyrics.isNotEmpty()
}

data class Artist(
    val id: String,
    val name: String,
    val genre: String,
    val monthlyListeners: String,
    val bio: String,
    val heroPalette: AccentPalette,
    val albumIds: List<String>,
    val topTrackIds: List<String>,
    val photoUri: String? = null,
    val trackCount: Int = 0,
    val relatedArtistIds: List<String>,
)

data class PlayHistoryEntry(
    val songId: String,
    val title: String,
    val artistName: String,
    val albumTitle: String,
    val listenedMs: Long,
    val playedAtMs: Long,
    val artworkUri: String? = null,
    val fallbackMediaUri: String? = null,
)

data class ArtistLookupUiState(
    val artistId: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
)

data class ArtistSearchCandidate(
    val pageTitle: String,
    val description: String,
)

data class ManualArtistSearchUiState(
    val artistId: String? = null,
    val query: String = "",
    val isVisible: Boolean = false,
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val results: List<ArtistSearchCandidate> = emptyList(),
    val message: String? = null,
)

data class Album(
    val id: String,
    val title: String,
    val artistId: String,
    val year: Int,
    val label: String,
    val description: String,
    val palette: AccentPalette,
    val trackIds: List<String>,
    val artworkUri: String? = null,
)

data class Song(
    val id: String,
    val title: String,
    val artistId: String,
    val albumId: String,
    val durationMs: Long,
    val genre: String? = null,
    val mood: String,
    val palette: AccentPalette,
    val lyrics: List<LyricLine>,
    val plainLyrics: List<String> = lyrics.map(LyricLine::text),
    val lyricsAttribution: String? = null,
    val isDownloaded: Boolean = false,
    val artworkUri: String? = null,
    val mediaUri: String? = null,
    val artistCredits: List<String> = emptyList(),
    val folderName: String? = null,
    val folderPath: String? = null,
    val source: SongSource = SongSource.Mock,
)

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val curator: String,
    val followers: String,
    val palette: AccentPalette,
    val trackIds: List<String>,
    val artworkUri: String? = null,
    val isUserCreated: Boolean = false,
)

data class UserSettings(
    val themePreset: ThemePreset = ThemePreset.Nebula,
    val autoplay: Boolean = true,
    val immersiveMotion: Boolean = true,
    val showSyncedLyricsByDefault: Boolean = true,
    val downloadOnWifiOnly: Boolean = true,
    val explicitContent: Boolean = false,
    val language: String = "English",
    val useTestArtwork: Boolean = false,
)

data class UserProfile(
    val name: String,
    val handle: String,
    val membershipTier: String,
    val avatarPalette: AccentPalette,
    val settings: UserSettings,
) {
    val displayName: String
        get() = name.trim().ifBlank { "Music Lover" }
}

data class PlaybackUiState(
    val queue: List<Song> = emptyList(),
    val canonicalQueue: List<Song> = emptyList(),
    val currentIndex: Int = 0,
    val currentSongId: String? = null,
    val positionMs: Long = 0L,
    val isPlaying: Boolean = false,
    val isShuffled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.All,
    val likedSongIds: Set<String> = emptySet(),
    val lyricsDisplayMode: LyricsDisplayMode = LyricsDisplayMode.Synced,
    val isQueueSheetVisible: Boolean = false,
) {
    val currentSong: Song?
        get() = currentSongId?.let { id -> queue.firstOrNull { it.id == id } } ?: queue.getOrNull(currentIndex)
}

data class ManualLyricsSearchUiState(
    val songId: String? = null,
    val queryTitle: String = "",
    val queryArtist: String = "",
    val isVisible: Boolean = false,
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val results: List<LyricsSearchCandidate> = emptyList(),
)

data class VerseFlowUiState(
    val profile: UserProfile,
    val songs: List<Song>,
    val songsById: Map<String, Song>,
    val albums: List<Album>,
    val albumsById: Map<String, Album>,
    val artists: List<Artist>,
    val artistsById: Map<String, Artist>,
    val playlists: List<Playlist>,
    val playlistsById: Map<String, Playlist>,
    val featuredAlbums: List<Album>,
    val recentlyPlayed: List<Song>,
    val trendingSongs: List<Song>,
    val favoritePlaylists: List<Playlist>,
    val playHistoryEntries: List<PlayHistoryEntry> = emptyList(),
    val recentSearches: List<String>,
    val trendingCategories: List<String>,
    val selectedLibraryTab: LibraryTab = LibraryTab.Songs,
    val selectedLibrarySort: LibrarySort = LibrarySort.Recent,
    val selectedLibraryFilter: LibraryFilter = LibraryFilter.All,
    val libraryQuery: String = "",
    val searchQuery: String = "",
    val audioPermissionGranted: Boolean = false,
    val hasScannedDeviceAudio: Boolean = false,
    val isScanningDeviceAudio: Boolean = false,
    val catalogSource: MusicCatalogSource = MusicCatalogSource.Demo,
    val lyricsStatusBySongId: Map<String, LyricsLoadState> = emptyMap(),
    val playQueueSongIds: List<String> = emptyList(),
    val playback: PlaybackUiState = PlaybackUiState(),
    val artistLookup: ArtistLookupUiState = ArtistLookupUiState(),
    val manualArtistSearch: ManualArtistSearchUiState = ManualArtistSearchUiState(),
    val manualLyricsSearch: ManualLyricsSearchUiState = ManualLyricsSearchUiState(),
)
