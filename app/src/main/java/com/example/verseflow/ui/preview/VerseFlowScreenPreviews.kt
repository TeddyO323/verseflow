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

    fun withTestArtwork(state: VerseFlowUiState): VerseFlowUiState = state.copy(
        profile = state.profile.copy(
            settings = state.profile.settings.copy(useTestArtwork = true),
        ),
    )

    fun artistSearch(): VerseFlowUiState = baseState.copy(
        manualArtistSearch = com.example.verseflow.model.ManualArtistSearchUiState(
            artistId = artist().id,
            query = "Akon",
            isVisible = true,
            hasSearched = true,
            results = listOf(
                com.example.verseflow.model.ArtistSearchCandidate(
                    pageTitle = "Akon",
                    description = "Senegalese-American singer, songwriter, rapper, producer, and entrepreneur.",
                ),
                com.example.verseflow.model.ArtistSearchCandidate(
                    pageTitle = "Akon City",
                    description = "Planned city in Senegal announced by singer Akon.",
                ),
            ),
        ),
    )
}

@Preview(
    name = "VerseFlow Phone",
    device = "spec:width=412dp,height=915dp,dpi=420",
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
private annotation class VerseFlowPhonePreview

@Composable
private fun PreviewFrame(
    useTestArtwork: Boolean = false,
    content: @Composable () -> Unit,
) {
    VerseFlowTheme {
        ProvideCarTestArtwork(enabled = useTestArtwork) {
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

@VerseFlowPhonePreview
@Composable
private fun PhoneHomeScreenPreview() {
    PreviewFrame {
        HomeScreen(
            uiState = VerseFlowPreviewData.home(),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneHomeScreenTestArtworkPreview() {
    PreviewFrame(useTestArtwork = true) {
        HomeScreen(
            uiState = VerseFlowPreviewData.withTestArtwork(VerseFlowPreviewData.home()),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneLibrarySongsPreview() {
    PreviewFrame {
        LibraryScreen(
            uiState = VerseFlowPreviewData.librarySongs(),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneLibraryAlbumsPreview() {
    PreviewFrame {
        LibraryScreen(
            uiState = VerseFlowPreviewData.libraryAlbums(),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneLibraryAlbumsTestArtworkPreview() {
    PreviewFrame(useTestArtwork = true) {
        LibraryScreen(
            uiState = VerseFlowPreviewData.withTestArtwork(VerseFlowPreviewData.libraryAlbums()),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneNowPlayingPreview() {
    PreviewFrame {
        NowPlayingScreen(
            uiState = VerseFlowPreviewData.nowPlaying(),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneNowPlayingTestArtworkPreview() {
    PreviewFrame(useTestArtwork = true) {
        NowPlayingScreen(
            uiState = VerseFlowPreviewData.withTestArtwork(VerseFlowPreviewData.nowPlaying()),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneLyricsPreview() {
    PreviewFrame {
        LyricsScreen(
            uiState = VerseFlowPreviewData.lyrics(),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneLyricsTestArtworkPreview() {
    PreviewFrame(useTestArtwork = true) {
        LyricsScreen(
            uiState = VerseFlowPreviewData.withTestArtwork(VerseFlowPreviewData.lyrics()),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneSettingsPreview() {
    PreviewFrame {
        SettingsScreen(
            uiState = VerseFlowPreviewData.settings(),
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

@VerseFlowPhonePreview
@Composable
private fun PhoneArtistSearchPreview() {
    PreviewFrame {
        ArtistDetailScreen(
            artist = VerseFlowPreviewData.artist(),
            uiState = VerseFlowPreviewData.artistSearch(),
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
