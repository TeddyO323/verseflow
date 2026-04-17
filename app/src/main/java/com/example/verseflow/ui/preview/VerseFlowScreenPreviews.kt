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
import com.example.verseflow.ui.car.ProvideCarTestArtwork
import com.example.verseflow.ui.screens.home.HomeScreen
import com.example.verseflow.ui.screens.library.LibraryScreen
import com.example.verseflow.ui.screens.lyrics.LyricsScreen
import com.example.verseflow.ui.screens.player.NowPlayingScreen
import com.example.verseflow.ui.screens.settings.SettingsScreen
import com.example.verseflow.ui.theme.VerseFlowTheme

private object CarPreviewData {
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
        currentIndex = 0,
        positionMs = 74_000L,
        isPlaying = true,
        isShuffled = true,
        repeatMode = RepeatMode.One,
        likedSongIds = setOf("song_midnight_circuit", "song_velvet_static"),
        lyricsDisplayMode = LyricsDisplayMode.Synced,
        isQueueSheetVisible = false,
    )

    private val baseState = VerseFlowUiState(
        profile = repository.profile().copy(
            settings = repository.profile().settings.copy(useTestArtwork = true),
        ),
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

    fun librarySongs(): VerseFlowUiState = baseState.copy(
        selectedLibraryTab = LibraryTab.Songs,
        selectedLibrarySort = LibrarySort.Title,
        selectedLibraryFilter = LibraryFilter.All,
    )

    fun libraryAlbums(): VerseFlowUiState = baseState.copy(
        selectedLibraryTab = LibraryTab.Albums,
        selectedLibrarySort = LibrarySort.Title,
        selectedLibraryFilter = LibraryFilter.All,
    )

    fun libraryArtists(): VerseFlowUiState = baseState.copy(
        selectedLibraryTab = LibraryTab.Artists,
        selectedLibrarySort = LibrarySort.Title,
        selectedLibraryFilter = LibraryFilter.All,
    )

    fun settings(): VerseFlowUiState = baseState

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
}

@Preview(
    name = "VerseFlow Car",
    device = "spec:width=1024dp,height=600dp,dpi=240",
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
private annotation class VerseFlowCarPreview

@Composable
private fun CarPreviewFrame(
    content: @Composable () -> Unit,
) {
    VerseFlowTheme {
        ProvideCarTestArtwork(enabled = true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
            ) {
                content()
            }
        }
    }
}

@VerseFlowCarPreview
@Composable
private fun CarHomeScreenPreview() {
    CarPreviewFrame {
        HomeScreen(
            uiState = CarPreviewData.home(),
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
private fun CarLibrarySongsScreenPreview() {
    CarPreviewFrame {
        LibraryScreen(
            uiState = CarPreviewData.librarySongs(),
            onSongClick = {},
            onFolderSongClick = { _, _ -> },
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
private fun CarLibraryAlbumsScreenPreview() {
    CarPreviewFrame {
        LibraryScreen(
            uiState = CarPreviewData.libraryAlbums(),
            onSongClick = {},
            onFolderSongClick = { _, _ -> },
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
private fun CarLibraryArtistsScreenPreview() {
    CarPreviewFrame {
        LibraryScreen(
            uiState = CarPreviewData.libraryArtists(),
            onSongClick = {},
            onFolderSongClick = { _, _ -> },
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
    CarPreviewFrame {
        NowPlayingScreen(
            uiState = CarPreviewData.nowPlaying(),
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
    CarPreviewFrame {
        LyricsScreen(
            uiState = CarPreviewData.lyrics(),
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
    CarPreviewFrame {
        SettingsScreen(
            uiState = CarPreviewData.settings(),
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
