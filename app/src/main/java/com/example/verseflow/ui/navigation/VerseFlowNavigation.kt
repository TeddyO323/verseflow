package com.example.verseflow.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.verseflow.VerseFlowViewModel
import com.example.verseflow.ui.screens.album.AlbumDetailScreen
import com.example.verseflow.ui.screens.artist.ArtistDetailScreen
import com.example.verseflow.ui.screens.home.HomeScreen
import com.example.verseflow.ui.screens.history.PlayHistoryScreen
import com.example.verseflow.ui.screens.library.LibraryScreen
import com.example.verseflow.ui.screens.lyrics.LyricsScreen
import com.example.verseflow.ui.screens.player.NowPlayingScreen
import com.example.verseflow.ui.screens.playlist.PlaylistDetailScreen
import com.example.verseflow.ui.screens.search.SearchScreen
import com.example.verseflow.ui.screens.settings.SettingsScreen
import com.example.verseflow.ui.screens.splash.SplashScreen
import com.example.verseflow.ui.screens.queue.PlayQueueScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ManageSearch
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Settings
import com.example.verseflow.model.LibraryTab
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.car.rememberIsCarLandscapeMode

sealed class VerseFlowDestination(val route: String) {
    data object Splash : VerseFlowDestination("splash")
    data object Home : VerseFlowDestination("home")
    data object Library : VerseFlowDestination("library")
    data object PlayQueue : VerseFlowDestination("play_queue")
    data object PlayHistory : VerseFlowDestination("play_history")
    data object Search : VerseFlowDestination("search")
    data object NowPlaying : VerseFlowDestination("now_playing")
    data object Lyrics : VerseFlowDestination("lyrics")
    data object Settings : VerseFlowDestination("settings")
    data object AlbumDetail : VerseFlowDestination("album/{albumId}") {
        fun createRoute(albumId: String) = "album/$albumId"
    }
    data object PlaylistDetail : VerseFlowDestination("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    data object ArtistDetail : VerseFlowDestination("artist/{artistId}") {
        fun createRoute(artistId: String) = "artist/$artistId"
    }
}

data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val topLevelDestinations = listOf(
    TopLevelDestination(VerseFlowDestination.Home.route, "Home", Icons.Rounded.Home),
    TopLevelDestination(VerseFlowDestination.PlayHistory.route, "History", Icons.Rounded.History),
    TopLevelDestination(VerseFlowDestination.Library.route, "Library", Icons.Rounded.LibraryMusic),
    TopLevelDestination(VerseFlowDestination.PlayQueue.route, "Play Queue", Icons.AutoMirrored.Rounded.QueueMusic),
    TopLevelDestination(VerseFlowDestination.Search.route, "Search", Icons.AutoMirrored.Rounded.ManageSearch),
    TopLevelDestination(VerseFlowDestination.Settings.route, "Settings", Icons.Rounded.Settings),
)

@Composable
fun VerseFlowNavHost(
    navController: NavHostController,
    uiState: VerseFlowUiState,
    viewModel: VerseFlowViewModel,
    onRequestAudioPermission: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCarLandscapeMode = rememberIsCarLandscapeMode()
    val openAlbumRoute: (String) -> Unit = remember(navController, isCarLandscapeMode) {
        { albumId ->
            if (!isCarLandscapeMode) {
                navController.navigate(VerseFlowDestination.AlbumDetail.createRoute(albumId))
            }
        }
    }
    val openArtistRoute: (String) -> Unit = remember(navController, isCarLandscapeMode) {
        { artistId ->
            if (!isCarLandscapeMode) {
                navController.navigate(VerseFlowDestination.ArtistDetail.createRoute(artistId))
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = VerseFlowDestination.Splash.route,
        modifier = modifier,
        enterTransition = {
            fadeIn() + slideInHorizontally(initialOffsetX = { it / 5 })
        },
        exitTransition = {
            fadeOut() + slideOutHorizontally(targetOffsetX = { -it / 8 })
        },
        popEnterTransition = {
            fadeIn() + slideInHorizontally(initialOffsetX = { -it / 5 })
        },
        popExitTransition = {
            fadeOut() + slideOutHorizontally(targetOffsetX = { it / 8 })
        },
    ) {
        composable(VerseFlowDestination.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(VerseFlowDestination.Home.route) {
                        popUpTo(VerseFlowDestination.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(VerseFlowDestination.Home.route) {
            HomeScreen(
                uiState = uiState,
                onAlbumClick = {
                    openAlbumRoute(it.id)
                },
                onSongClick = {
                    viewModel.playSong(it.id)
                },
                onPlaylistClick = {
                    navController.navigate(VerseFlowDestination.PlaylistDetail.createRoute(it.id))
                },
                onArtistClick = {
                    openArtistRoute(it.id)
                },
                onAddAlbumToPlaylist = viewModel::addAlbumToPlaylist,
                onAddAlbumToPlayQueue = { album -> viewModel.addAlbumToPlayQueue(album.id) },
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = { navController.navigate(VerseFlowDestination.Search.route) },
                onRequestAudioPermission = onRequestAudioPermission,
            )
        }
        composable(VerseFlowDestination.Library.route) {
            LibraryScreen(
                uiState = uiState,
                onSongClick = {
                    viewModel.playCurrentLibrarySong(it.id)
                },
                onAlbumClick = {
                    openAlbumRoute(it.id)
                },
                onArtistClick = { openArtistRoute(it.id) },
                onPlaylistClick = { navController.navigate(VerseFlowDestination.PlaylistDetail.createRoute(it.id)) },
                onLibraryTabChange = viewModel::selectLibraryTab,
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = { navController.navigate(VerseFlowDestination.Search.route) },
                onShuffleAllSongs = viewModel::shuffleAllSongs,
                onRemoveFromVerseFlow = viewModel::requestRemoveFromVerseFlow,
                onAddSongToPlaylist = viewModel::addSongToPlaylist,
                onAddSongToPlayQueue = viewModel::addSongToPlayQueue,
                onToggleSongLike = viewModel::toggleSongLike,
                onDeleteFromStorage = viewModel::requestDeleteFromDevice,
                onEditMusicInfo = viewModel::requestEditMusicInfo,
                onCreatePlaylist = viewModel::createPlaylist,
                onDeletePlaylist = viewModel::deletePlaylist,
                onRequestAudioPermission = onRequestAudioPermission,
            )
        }
        composable(VerseFlowDestination.PlayHistory.route) {
            PlayHistoryScreen(
                uiState = uiState,
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = { navController.navigate(VerseFlowDestination.Search.route) },
                onSongClick = { viewModel.playSong(it.id) },
                onClearHistory = viewModel::clearPlayHistory,
            )
        }
        composable(VerseFlowDestination.PlayQueue.route) {
            PlayQueueScreen(
                uiState = uiState,
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = { navController.navigate(VerseFlowDestination.Search.route) },
                onSongClick = {
                    viewModel.playQueuedSong(it.id)
                },
                onArtistClick = { artistId ->
                    openArtistRoute(artistId)
                },
                onAlbumClick = { albumId ->
                    openAlbumRoute(albumId)
                },
                onRemoveFromVerseFlow = viewModel::requestRemoveFromVerseFlow,
                onAddSongToPlaylist = viewModel::addSongToPlaylist,
                onAddSongToPlayQueue = viewModel::addSongToPlayQueue,
                onToggleSongLike = viewModel::toggleSongLike,
                onDeleteFromStorage = viewModel::requestDeleteFromDevice,
                onEditMusicInfo = viewModel::requestEditMusicInfo,
            )
        }
        composable(VerseFlowDestination.Search.route) {
            SearchScreen(
                uiState = uiState,
                onOpenDrawer = onOpenDrawer,
                onQueryChange = viewModel::updateSearchQuery,
                onUseRecentSearch = viewModel::recallSearch,
                onSongClick = {
                    viewModel.playSong(it.id)
                    viewModel.recallSearch(uiState.searchQuery.ifBlank { it.title })
                },
                onAlbumClick = {
                    openAlbumRoute(it.id)
                },
                onArtistClick = {
                    viewModel.recallSearch(it.name)
                    openArtistRoute(it.id)
                },
                onPlaylistClick = {
                    viewModel.recallSearch(it.title)
                    navController.navigate(VerseFlowDestination.PlaylistDetail.createRoute(it.id))
                },
                onRemoveFromVerseFlow = viewModel::requestRemoveFromVerseFlow,
                onAddSongToPlaylist = viewModel::addSongToPlaylist,
                onAddSongToPlayQueue = viewModel::addSongToPlayQueue,
                onToggleSongLike = viewModel::toggleSongLike,
                onDeleteFromStorage = viewModel::requestDeleteFromDevice,
                onEditMusicInfo = viewModel::requestEditMusicInfo,
            )
        }
        composable(VerseFlowDestination.NowPlaying.route) {
            NowPlayingScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onPlayPause = viewModel::togglePlayPause,
                onNext = viewModel::skipNext,
                onPrevious = viewModel::skipPrevious,
                onSeek = viewModel::seekTo,
                onToggleShuffle = viewModel::toggleShuffle,
                onCycleRepeat = viewModel::cycleRepeatMode,
                onToggleLike = viewModel::toggleCurrentLike,
                onQueueVisibilityChange = viewModel::setQueueSheetVisible,
                onQueueSongSelected = {
                    viewModel.playSong(it.id, uiState.playback.canonicalQueue.map { song -> song.id })
                },
                onSearchRequested = { navController.navigate(VerseFlowDestination.Search.route) },
                onRemoveFromVerseFlow = viewModel::requestRemoveFromVerseFlow,
                onAddSongToPlaylist = viewModel::addSongToPlaylist,
                onAddSongToPlayQueue = viewModel::addSongToPlayQueue,
                onToggleSongLike = viewModel::toggleSongLike,
                onDeleteFromStorage = viewModel::requestDeleteFromDevice,
                onEditMusicInfo = viewModel::requestEditMusicInfo,
                onArtistRequested = { artistId ->
                    openArtistRoute(artistId)
                },
                onAlbumRequested = { albumId ->
                    openAlbumRoute(albumId)
                },
                onLyricsRequested = { navController.navigate(VerseFlowDestination.Lyrics.route) },
            )
        }
        composable(
            route = VerseFlowDestination.AlbumDetail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val album = uiState.albumsById[backStackEntry.arguments?.getString("albumId")]
            album?.let {
                AlbumDetailScreen(
                    album = it,
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onPlayAll = {
                        viewModel.playAlbum(it.id, shuffled = false)
                        navController.navigate(VerseFlowDestination.NowPlaying.route)
                    },
                    onShuffle = {
                        viewModel.playAlbum(it.id, shuffled = true)
                        navController.navigate(VerseFlowDestination.NowPlaying.route)
                    },
                    onSongClick = { song ->
                        viewModel.playAlbumTrack(it.id, song.id)
                    },
                    onAlbumClick = { album ->
                        openAlbumRoute(album.id)
                    },
                    onArtistClick = { artist ->
                        openArtistRoute(artist.id)
                    },
                    onRemoveFromVerseFlow = viewModel::requestRemoveFromVerseFlow,
                    onAddSongToPlaylist = viewModel::addSongToPlaylist,
                    onAddSongToPlayQueue = viewModel::addSongToPlayQueue,
                    onToggleSongLike = viewModel::toggleSongLike,
                    onDeleteFromStorage = viewModel::requestDeleteFromDevice,
                    onEditMusicInfo = viewModel::requestEditMusicInfo,
                )
            }
        }
        composable(VerseFlowDestination.Lyrics.route) {
            LyricsScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onModeSelected = viewModel::setLyricsDisplayMode,
                onSeek = viewModel::seekTo,
                onPlayPause = viewModel::togglePlayPause,
                onNext = viewModel::skipNext,
                onPrevious = viewModel::skipPrevious,
                onNowPlayingRequested = { navController.popBackStack() },
                onManualSearchRequested = viewModel::openManualLyricsSearch,
                onManualSearchDismissed = viewModel::dismissManualLyricsSearch,
                onManualSearchTitleChange = viewModel::updateManualLyricsSearchTitle,
                onManualSearchArtistChange = viewModel::updateManualLyricsSearchArtist,
                onManualSearchExecute = viewModel::searchManualLyricsCandidates,
                onManualCandidateSelected = viewModel::applyManualLyricsCandidate,
            )
        }
        composable(
            route = VerseFlowDestination.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val playlist = uiState.playlistsById[backStackEntry.arguments?.getString("playlistId")]
            playlist?.let {
                PlaylistDetailScreen(
                    playlist = it,
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onPlayAll = {
                        viewModel.playPlaylist(it.id, shuffled = false)
                        navController.navigate(VerseFlowDestination.NowPlaying.route)
                    },
                    onShuffle = {
                        viewModel.playPlaylist(it.id, shuffled = true)
                        navController.navigate(VerseFlowDestination.NowPlaying.route)
                    },
                    onSongClick = { song ->
                        viewModel.playSong(song.id, queueSongIds = it.trackIds)
                    },
                    onAlbumClick = { albumId ->
                        openAlbumRoute(albumId)
                    },
                    onArtistClick = { artistId ->
                        openArtistRoute(artistId)
                    },
                    onRemoveFromVerseFlow = viewModel::requestRemoveFromVerseFlow,
                    onAddSongToPlaylist = viewModel::addSongToPlaylist,
                    onAddSongToPlayQueue = viewModel::addSongToPlayQueue,
                    onToggleSongLike = viewModel::toggleSongLike,
                    onDeleteFromStorage = viewModel::requestDeleteFromDevice,
                    onEditMusicInfo = viewModel::requestEditMusicInfo,
                )
            }
        }
        composable(
            route = VerseFlowDestination.ArtistDetail.route,
            arguments = listOf(navArgument("artistId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId")
            val artist = uiState.artistsById[artistId]
            artist?.let {
                ArtistDetailScreen(
                    artist = it,
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onPlayTopTracks = {
                        viewModel.playArtistTopTracks(it.id)
                        navController.navigate(VerseFlowDestination.NowPlaying.route)
                    },
                    onSearchArtistInfo = { artistId ->
                        viewModel.searchArtistInfo(artistId)
                    },
                    onOpenManualArtistSearch = viewModel::openManualArtistSearch,
                    onDismissArtistLookupMessage = viewModel::dismissArtistLookupMessage,
                    onDismissManualArtistSearch = viewModel::dismissManualArtistSearch,
                    onManualArtistSearchQueryChange = viewModel::updateManualArtistSearchQuery,
                    onManualArtistSearchExecute = viewModel::searchManualArtistCandidates,
                    onManualArtistCandidateSelected = viewModel::applyManualArtistCandidate,
                    onAlbumClick = { album ->
                        openAlbumRoute(album.id)
                    },
                    onSongClick = { song ->
                        viewModel.playSong(song.id, queueSongIds = it.topTrackIds)
                    },
                    onArtistClick = { relatedArtist ->
                        openArtistRoute(relatedArtist.id)
                    },
                    onRemoveFromVerseFlow = viewModel::requestRemoveFromVerseFlow,
                    onAddSongToPlaylist = viewModel::addSongToPlaylist,
                    onAddSongToPlayQueue = viewModel::addSongToPlayQueue,
                    onToggleSongLike = viewModel::toggleSongLike,
                    onDeleteFromStorage = viewModel::requestDeleteFromDevice,
                    onEditMusicInfo = viewModel::requestEditMusicInfo,
                )
            }
        }
        composable(VerseFlowDestination.Settings.route) {
            SettingsScreen(
                uiState = uiState,
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = { navController.navigate(VerseFlowDestination.Search.route) },
                onProfileNameChange = viewModel::updateProfileName,
                onThemeSelected = viewModel::updateThemePreset,
                onAutoplayChange = viewModel::updateAutoplay,
                onImmersiveMotionChange = viewModel::updateImmersiveMotion,
                onSyncedLyricsChange = viewModel::updateSyncedLyrics,
                onWifiDownloadsChange = viewModel::updateWifiDownloads,
                onExplicitContentChange = viewModel::updateExplicitContent,
                onLanguageSelected = viewModel::updateLanguage,
                onUseTestArtworkChange = viewModel::updateUseTestArtwork,
            )
        }
    }
}
