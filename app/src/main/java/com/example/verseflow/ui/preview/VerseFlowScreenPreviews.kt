package com.example.verseflow.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.verseflow.data.MockMusicRepository
import com.example.verseflow.model.LibraryFilter
import com.example.verseflow.model.LibrarySort
import com.example.verseflow.model.LibraryTab
import com.example.verseflow.model.LyricsDisplayMode
import com.example.verseflow.model.MusicCatalogSource
import com.example.verseflow.model.PlaybackUiState
import com.example.verseflow.model.RepeatMode
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.screens.artist.ArtistDetailScreen
import com.example.verseflow.ui.screens.home.HomeScreen
import com.example.verseflow.ui.screens.library.LibraryScreen
import com.example.verseflow.ui.screens.lyrics.LyricsScreen
import com.example.verseflow.ui.screens.player.NowPlayingScreen
import com.example.verseflow.ui.screens.settings.SettingsScreen
import com.example.verseflow.ui.theme.VerseFlowTheme

private object VerseFlowPreviewData {
    private val repository = MockMusicRepository()
    private val songs = repository.songs()
    private val songsById = songs.associateBy(Song::id)
    private val albums = repository.albums()
    private val albumsById = albums.associateBy { it.id }
    private val artists = repository.artists()
    private val artistsById = artists.associateBy { it.id }
    private val playlists = repository.playlists()
    private val playlistsById = playlists.associateBy { it.id }
    private val featuredAlbums = repository.featuredAlbumIds().mapNotNull(albumsById::get)
    private val recentlyPlayed = repository.recentlyPlayedIds().mapNotNull(songsById::get)
    private val trendingSongs = repository.trendingSongIds().mapNotNull(songsById::get)
    private val favoritePlaylists = repository.favoritePlaylistIds().mapNotNull(playlistsById::get)

    private val basePlayback = PlaybackUiState(
        queue = trendingSongs,
        canonicalQueue = trendingSongs,
        currentIndex = 1,
        positionMs = 102_000L,
        isPlaying = true,
        isShuffled = false,
        repeatMode = RepeatMode.All,
        likedSongIds = setOf("song_midnight_circuit", "song_velvet_static"),
        lyricsDisplayMode = LyricsDisplayMode.Synced,
        isQueueSheetVisible = false,
    )

    private val baseState = VerseFlowUiState(
        profile = repository.profile(),
        songs = songs,
        songsById = songsById,
        albums = albums,
        albumsById = albumsById,
        artists = artists,
        artistsById = artistsById,
        playlists = playlists,
        playlistsById = playlistsById,
        featuredAlbums = featuredAlbums,
        recentlyPlayed = recentlyPlayed,
        trendingSongs = trendingSongs,
        favoritePlaylists = favoritePlaylists,
        recentSearches = repository.recentSearches(),
        trendingCategories = repository.trendingCategories(),
        audioPermissionGranted = true,
        hasScannedDeviceAudio = true,
        catalogSource = MusicCatalogSource.Demo,
        playQueueSongIds = listOf(trendingSongs[0].id, trendingSongs[1].id),
        playback = basePlayback,
    )

    fun home(): VerseFlowUiState = baseState

    fun artist() = artists.first()

    fun libraryAlbums(): VerseFlowUiState = baseState.copy(
        selectedLibraryTab = LibraryTab.Albums,
        selectedLibrarySort = LibrarySort.Title,
        selectedLibraryFilter = LibraryFilter.All,
        libraryQuery = "Glass",
    )

    fun librarySongs(): VerseFlowUiState = baseState.copy(
        selectedLibraryTab = LibraryTab.Songs,
        selectedLibrarySort = LibrarySort.Title,
        selectedLibraryFilter = LibraryFilter.All,
        libraryQuery = "",
    )

    fun nowPlaying(): VerseFlowUiState = baseState.copy(
        playback = basePlayback.copy(
            currentIndex = 0,
            positionMs = 74_000L,
            repeatMode = RepeatMode.One,
            isShuffled = true,
        ),
    )

    fun lyrics(): VerseFlowUiState = baseState.copy(
        playback = basePlayback.copy(
            currentIndex = 0,
            positionMs = 126_000L,
            lyricsDisplayMode = LyricsDisplayMode.Synced,
        ),
    )

    fun settings(): VerseFlowUiState = baseState

    fun carNowPlayingWithTestArtwork(): VerseFlowUiState = nowPlaying().copy(
        profile = nowPlaying().profile.copy(
            settings = nowPlaying().profile.settings.copy(useTestArtwork = true),
        ),
    )

    fun carLyricsWithTestArtwork(): VerseFlowUiState = lyrics().copy(
        profile = lyrics().profile.copy(
            settings = lyrics().profile.settings.copy(useTestArtwork = true),
        ),
    )

    fun carLibraryWithTestArtwork(): VerseFlowUiState = libraryAlbums().copy(
        profile = libraryAlbums().profile.copy(
            settings = libraryAlbums().profile.settings.copy(useTestArtwork = true),
        ),
    )
}

@Preview(
    name = "VerseFlow Car Screen",
    widthDp = 1024,
    heightDp = 600,
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
private annotation class VerseFlowCarPreview

@Composable
private fun PreviewFrame(
    content: @Composable () -> Unit,
) {
    VerseFlowTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
        ) {
            content()
        }
    }
}

@VerseFlowCarPreview
@Composable
private fun CarArtistSearchFlowPreview() {
    val state = VerseFlowPreviewData.home().copy(
        manualArtistSearch = com.example.verseflow.model.ManualArtistSearchUiState(
            artistId = VerseFlowPreviewData.artist().id,
            query = "Akon",
            isVisible = true,
            hasSearched = true,
            results = listOf(
                com.example.verseflow.model.ArtistSearchCandidate(
                    pageTitle = "Akon",
                    description = "Senegalese-American singer, songwriter, rapper, record producer, and entrepreneur.",
                ),
                com.example.verseflow.model.ArtistSearchCandidate(
                    pageTitle = "Akon City",
                    description = "Planned city in Senegal announced by singer Akon.",
                ),
            ),
        ),
    )
    PreviewFrame {
        ArtistDetailScreen(
            artist = VerseFlowPreviewData.artist(),
            uiState = state,
            onBack = {},
            onPlayTopTracks = {},
            onSearchArtistInfo = {},
            onOpenManualArtistSearch = {},
            onDismissArtistLookupMessage = {},
            onDismissManualArtistSearch = {},
            onManualArtistSearchQueryChange = {},
            onManualArtistSearchExecute = {},
            onManualArtistCandidateSelected = {},
            onAlbumClick = {},
            onSongClick = {},
            onArtistClick = {},
            onRemoveFromVerseFlow = {},
            onAddSongToPlaylist = { _, _ -> },
            onAddSongToPlayQueue = {},
            onToggleSongLike = {},
            onDeleteFromStorage = {},
            onEditMusicInfo = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarHomeScreenPreview() {
    val state = VerseFlowPreviewData.home()
    PreviewFrame {
        HomeScreen(
            uiState = state,
            onAlbumClick = {},
            onSongClick = {},
            onPlaylistClick = {},
            onArtistClick = {},
            onAddAlbumToPlaylist = { _, _ -> },
            onAddAlbumToPlayQueue = {},
            onOpenDrawer = {},
            onOpenSearch = {},
            onRequestAudioPermission = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarLibraryAlbumsScreenPreview() {
    val state = VerseFlowPreviewData.libraryAlbums()
    PreviewFrame {
        LibraryScreen(
            uiState = state,
            onSongClick = {},
            onAlbumClick = {},
            onArtistClick = {},
            onPlaylistClick = {},
            onLibraryTabChange = {},
            onOpenDrawer = {},
            onOpenSearch = {},
            onShuffleAllSongs = {},
            onRemoveFromVerseFlow = {},
            onAddSongToPlaylist = { _, _ -> },
            onAddSongToPlayQueue = {},
            onToggleSongLike = {},
            onDeleteFromStorage = {},
            onEditMusicInfo = {},
            onCreatePlaylist = { _, _ -> },
            onDeletePlaylist = {},
            onRequestAudioPermission = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarLibrarySongsScreenPreview() {
    val state = VerseFlowPreviewData.librarySongs()
    PreviewFrame {
        LibraryScreen(
            uiState = state,
            onSongClick = {},
            onAlbumClick = {},
            onArtistClick = {},
            onPlaylistClick = {},
            onLibraryTabChange = {},
            onOpenDrawer = {},
            onOpenSearch = {},
            onShuffleAllSongs = {},
            onRemoveFromVerseFlow = {},
            onAddSongToPlaylist = { _, _ -> },
            onAddSongToPlayQueue = {},
            onToggleSongLike = {},
            onDeleteFromStorage = {},
            onEditMusicInfo = {},
            onCreatePlaylist = { _, _ -> },
            onDeletePlaylist = {},
            onRequestAudioPermission = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarLibraryAlbumsTestArtworkPreview() {
    val state = VerseFlowPreviewData.carLibraryWithTestArtwork()
    PreviewFrame {
        LibraryScreen(
            uiState = state,
            onSongClick = {},
            onAlbumClick = {},
            onArtistClick = {},
            onPlaylistClick = {},
            onLibraryTabChange = {},
            onOpenDrawer = {},
            onOpenSearch = {},
            onShuffleAllSongs = {},
            onRemoveFromVerseFlow = {},
            onAddSongToPlaylist = { _, _ -> },
            onAddSongToPlayQueue = {},
            onToggleSongLike = {},
            onDeleteFromStorage = {},
            onEditMusicInfo = {},
            onCreatePlaylist = { _, _ -> },
            onDeletePlaylist = {},
            onRequestAudioPermission = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarNowPlayingScreenPreview() {
    val state = VerseFlowPreviewData.nowPlaying()
    PreviewFrame {
        NowPlayingScreen(
            uiState = state,
            onBack = {},
            onPlayPause = {},
            onNext = {},
            onPrevious = {},
            onSeek = {},
            onToggleShuffle = {},
            onCycleRepeat = {},
            onToggleLike = {},
            onQueueVisibilityChange = {},
            onQueueSongSelected = {},
            onSearchRequested = {},
            onRemoveFromVerseFlow = {},
            onAddSongToPlaylist = { _, _ -> },
            onAddSongToPlayQueue = {},
            onToggleSongLike = {},
            onDeleteFromStorage = {},
            onEditMusicInfo = {},
            onArtistRequested = {},
            onAlbumRequested = {},
            onLyricsRequested = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarNowPlayingScreenTestArtworkPreview() {
    val state = VerseFlowPreviewData.carNowPlayingWithTestArtwork()
    PreviewFrame {
        NowPlayingScreen(
            uiState = state,
            onBack = {},
            onPlayPause = {},
            onNext = {},
            onPrevious = {},
            onSeek = {},
            onToggleShuffle = {},
            onCycleRepeat = {},
            onToggleLike = {},
            onQueueVisibilityChange = {},
            onQueueSongSelected = {},
            onSearchRequested = {},
            onRemoveFromVerseFlow = {},
            onAddSongToPlaylist = { _, _ -> },
            onAddSongToPlayQueue = {},
            onToggleSongLike = {},
            onDeleteFromStorage = {},
            onEditMusicInfo = {},
            onArtistRequested = {},
            onAlbumRequested = {},
            onLyricsRequested = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarLyricsScreenPreview() {
    val state = VerseFlowPreviewData.lyrics()
    PreviewFrame {
        LyricsScreen(
            uiState = state,
            onBack = {},
            onModeSelected = {},
            onSeek = {},
            onPlayPause = {},
            onNext = {},
            onPrevious = {},
            onNowPlayingRequested = {},
            onManualSearchRequested = {},
            onManualSearchDismissed = {},
            onManualSearchTitleChange = {},
            onManualSearchArtistChange = {},
            onManualSearchExecute = {},
            onManualCandidateSelected = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarLyricsScreenTestArtworkPreview() {
    val state = VerseFlowPreviewData.carLyricsWithTestArtwork()
    PreviewFrame {
        LyricsScreen(
            uiState = state,
            onBack = {},
            onModeSelected = {},
            onSeek = {},
            onPlayPause = {},
            onNext = {},
            onPrevious = {},
            onNowPlayingRequested = {},
            onManualSearchRequested = {},
            onManualSearchDismissed = {},
            onManualSearchTitleChange = {},
            onManualSearchArtistChange = {},
            onManualSearchExecute = {},
            onManualCandidateSelected = {},
        )
    }
}

@VerseFlowCarPreview
@Composable
private fun CarSettingsScreenPreview() {
    val state = VerseFlowPreviewData.settings()
    PreviewFrame {
        SettingsScreen(
            uiState = state,
            onOpenDrawer = {},
            onOpenSearch = {},
            onProfileNameChange = {},
            onThemeSelected = {},
            onAutoplayChange = {},
            onImmersiveMotionChange = {},
            onSyncedLyricsChange = {},
            onWifiDownloadsChange = {},
            onExplicitContentChange = {},
            onLanguageSelected = {},
            onUseTestArtworkChange = {},
        )
    }
}
