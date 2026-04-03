package com.example.verseflow.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.verseflow.data.MockMusicRepository
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.LibraryFilter
import com.example.verseflow.model.LibrarySort
import com.example.verseflow.model.LibraryTab
import com.example.verseflow.model.LyricsDisplayMode
import com.example.verseflow.model.MusicCatalogSource
import com.example.verseflow.model.PlaybackUiState
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.RepeatMode
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.screens.album.AlbumDetailScreen
import com.example.verseflow.ui.screens.artist.ArtistDetailScreen
import com.example.verseflow.ui.screens.home.HomeScreen
import com.example.verseflow.ui.screens.library.LibraryScreen
import com.example.verseflow.ui.screens.lyrics.LyricsScreen
import com.example.verseflow.ui.screens.player.NowPlayingScreen
import com.example.verseflow.ui.screens.playlist.PlaylistDetailScreen
import com.example.verseflow.ui.screens.search.SearchScreen
import com.example.verseflow.ui.screens.settings.SettingsScreen
import com.example.verseflow.ui.screens.splash.SplashScreen
import com.example.verseflow.ui.theme.VerseFlowTheme

private object VerseFlowPreviewData {
    private val repository = MockMusicRepository()
    private val songs = repository.songs()
    private val songsById = songs.associateBy(Song::id)
    private val albums = repository.albums()
    private val albumsById = albums.associateBy(Album::id)
    private val artists = repository.artists()
    private val artistsById = artists.associateBy(Artist::id)
    private val playlists = repository.playlists()
    private val playlistsById = playlists.associateBy(Playlist::id)
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

    fun library(): VerseFlowUiState = baseState.copy(
        selectedLibraryTab = LibraryTab.Albums,
        selectedLibrarySort = LibrarySort.Title,
        selectedLibraryFilter = LibraryFilter.All,
        libraryQuery = "Glass",
    )

    fun search(): VerseFlowUiState = baseState.copy(searchQuery = "Nova")

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

    fun playlist(): Playlist = favoritePlaylists.first()

    fun artist(): Artist = artists.first()

    fun album(): Album = albums.first()

    fun settings(): VerseFlowUiState = baseState
}

@Preview(
    name = "VerseFlow Screen",
    device = Devices.PIXEL_7_PRO,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
private annotation class VerseFlowPreview

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

@VerseFlowPreview
@Composable
private fun SplashScreenPreview() {
    PreviewFrame {
        SplashScreen(onFinished = {})
    }
}

@VerseFlowPreview
@Composable
private fun HomeScreenPreview() {
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

@VerseFlowPreview
@Composable
private fun LibraryScreenPreview() {
    val state = VerseFlowPreviewData.library()
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

@VerseFlowPreview
@Composable
private fun SearchScreenPreview() {
    val state = VerseFlowPreviewData.search()
    PreviewFrame {
        SearchScreen(
            uiState = state,
            onOpenDrawer = {},
            onQueryChange = {},
            onUseRecentSearch = {},
            onSongClick = {},
            onAlbumClick = {},
            onArtistClick = {},
            onPlaylistClick = {},
            onRemoveFromVerseFlow = {},
            onAddSongToPlaylist = { _, _ -> },
            onAddSongToPlayQueue = {},
            onToggleSongLike = {},
            onDeleteFromStorage = {},
            onEditMusicInfo = {},
        )
    }
}

@VerseFlowPreview
@Composable
private fun NowPlayingScreenPreview() {
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

@VerseFlowPreview
@Composable
private fun LyricsScreenPreview() {
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

@VerseFlowPreview
@Composable
private fun AlbumDetailScreenPreview() {
    val state = VerseFlowPreviewData.home()
    PreviewFrame {
        AlbumDetailScreen(
            album = VerseFlowPreviewData.album(),
            uiState = state,
            onBack = {},
            onPlayAll = {},
            onShuffle = {},
            onSongClick = {},
            onAlbumClick = {},
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

@VerseFlowPreview
@Composable
private fun PlaylistDetailScreenPreview() {
    val state = VerseFlowPreviewData.home()
    PreviewFrame {
        PlaylistDetailScreen(
            playlist = VerseFlowPreviewData.playlist(),
            uiState = state,
            onBack = {},
            onPlayAll = {},
            onShuffle = {},
            onSongClick = {},
            onAlbumClick = {},
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

@VerseFlowPreview
@Composable
private fun ArtistDetailScreenPreview() {
    val state = VerseFlowPreviewData.home()
    PreviewFrame {
        ArtistDetailScreen(
            artist = VerseFlowPreviewData.artist(),
            uiState = state,
            onBack = {},
            onPlayTopTracks = {},
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

@VerseFlowPreview
@Composable
private fun SettingsScreenPreview() {
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
        )
    }
}
