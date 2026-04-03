package com.example.verseflow

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.verseflow.ui.components.ArtworkReactiveBackdrop
import com.example.verseflow.ui.components.MiniPlayerBar
import com.example.verseflow.ui.navigation.VerseFlowDestination
import com.example.verseflow.ui.navigation.VerseFlowNavHost
import com.example.verseflow.ui.theme.VerseFlowTheme
import com.example.verseflow.model.LibraryTab
import kotlinx.coroutines.launch

@Composable
fun VerseFlowApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: VerseFlowViewModel = viewModel(
        factory = VerseFlowViewModel.factory(application),
    )
    val audioPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    var hasRequestedAudioPermission by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onAudioPermissionChanged(granted)
    }
    val deleteRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        val pendingSongId = viewModel.songActionUiState.pendingSystemDeleteSongId
        if (pendingSongId != null) {
            viewModel.onDeviceDeleteResult(
                songId = pendingSongId,
                deleted = result.resultCode == Activity.RESULT_OK,
            )
        } else {
            viewModel.completePendingDeleteLaunch()
        }
    }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val uiState = viewModel.uiState
    val songActionUiState = viewModel.songActionUiState
    val currentSong = uiState.playback.currentSong
    val activePalette = currentSong?.palette ?: uiState.featuredAlbums.firstOrNull()?.palette
    val topLevelRoutes = remember {
        setOf(
            VerseFlowDestination.Home.route,
            VerseFlowDestination.Library.route,
            VerseFlowDestination.PlayQueue.route,
            VerseFlowDestination.Search.route,
            VerseFlowDestination.Settings.route,
        )
    }
    val showTopLevelChrome = currentRoute in topLevelRoutes
    val miniPlayerHiddenRoutes = remember {
        setOf(
            VerseFlowDestination.Splash.route,
            VerseFlowDestination.NowPlaying.route,
            VerseFlowDestination.Lyrics.route,
        )
    }
    val showMiniPlayer = currentRoute !in miniPlayerHiddenRoutes
    val lifecycleOwner = LocalLifecycleOwner.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner, audioPermission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    audioPermission,
                ) == PackageManager.PERMISSION_GRANTED
                viewModel.onAudioPermissionChanged(granted)
                if (!granted && !hasRequestedAudioPermission) {
                    hasRequestedAudioPermission = true
                    permissionLauncher.launch(audioPermission)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val requestAudioPermission = {
        val granted = ContextCompat.checkSelfPermission(
            context,
            audioPermission,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            viewModel.onAudioPermissionChanged(true)
            viewModel.refreshDeviceLibrary()
        } else {
            hasRequestedAudioPermission = true
            permissionLauncher.launch(audioPermission)
        }
    }

    LaunchedEffect(songActionUiState.pendingSystemDeleteSongId) {
        val songId = songActionUiState.pendingSystemDeleteSongId ?: return@LaunchedEffect
        val song = uiState.songsById[songId]
        val mediaUri = song?.mediaUri?.takeIf(String::isNotBlank) ?: run {
            viewModel.onDeviceDeleteResult(songId, deleted = false)
            return@LaunchedEffect
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching {
                val deleteRequest = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(Uri.parse(mediaUri)),
                )
                deleteRequestLauncher.launch(
                    IntentSenderRequest.Builder(deleteRequest.intentSender).build(),
                )
            }.onFailure {
                viewModel.onDeviceDeleteResult(songId, deleted = false)
            }
        } else {
            viewModel.performLegacyDeviceDelete(songId)
        }
    }

    VerseFlowTheme(preset = uiState.profile.settings.themePreset) {
        songActionUiState.noticeTitle?.let { title ->
            AlertDialog(
                onDismissRequest = viewModel::dismissSongActionNotice,
                title = { Text(title) },
                text = {
                    Text(songActionUiState.noticeMessage.orEmpty())
                },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissSongActionNotice) {
                        Text("Close")
                    }
                },
            )
        }

        songActionUiState.removeFromVerseFlowSongId
            ?.let(uiState.songsById::get)
            ?.let { song ->
                AlertDialog(
                    onDismissRequest = viewModel::dismissRemoveFromVerseFlow,
                    title = { Text("Remove from VerseFlow") },
                    text = {
                        Text(
                            "Hide \"${song.title}\" from VerseFlow? This only removes it from the app. The file stays on your phone.",
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = viewModel::confirmRemoveFromVerseFlow) {
                            Text("Remove")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::dismissRemoveFromVerseFlow) {
                            Text("Cancel")
                        }
                    },
                )
            }

        songActionUiState.deleteFromDeviceSongId
            ?.let(uiState.songsById::get)
            ?.let { song ->
                AlertDialog(
                    onDismissRequest = viewModel::dismissDeleteFromDevice,
                    title = { Text("Delete from device") },
                    text = {
                        Text(
                            "Delete \"${song.title}\" from your phone storage? This cannot be undone from VerseFlow.",
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = viewModel::confirmDeleteFromDevice) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::dismissDeleteFromDevice) {
                            Text("Cancel")
                        }
                    },
                )
            }

        songActionUiState.editMusicInfoSongId
            ?.let(uiState.songsById::get)
            ?.let { song ->
                val currentArtistName = uiState.artistsById[song.artistId]?.name.orEmpty()
                val currentAlbumTitle = uiState.albumsById[song.albumId]?.title.orEmpty()
                var editedTitle by rememberSaveable(song.id) { mutableStateOf(song.title) }
                var editedArtist by rememberSaveable(song.id) { mutableStateOf(currentArtistName) }
                var editedAlbum by rememberSaveable(song.id) { mutableStateOf(currentAlbumTitle) }
                var editedGenre by rememberSaveable(song.id) { mutableStateOf(song.genre.orEmpty()) }

                AlertDialog(
                    onDismissRequest = viewModel::dismissEditMusicInfo,
                    title = { Text("Edit Music Info") },
                    text = {
                        Column {
                            TextField(
                                value = editedTitle,
                                onValueChange = { editedTitle = it },
                                label = { Text("Song title") },
                            )
                            TextField(
                                value = editedArtist,
                                onValueChange = { editedArtist = it },
                                label = { Text("Artist") },
                            )
                            TextField(
                                value = editedAlbum,
                                onValueChange = { editedAlbum = it },
                                label = { Text("Album") },
                            )
                            TextField(
                                value = editedGenre,
                                onValueChange = { editedGenre = it },
                                label = { Text("Genre") },
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            enabled = editedTitle.trim().isNotBlank() &&
                                editedArtist.trim().isNotBlank() &&
                                editedAlbum.trim().isNotBlank(),
                            onClick = {
                                viewModel.saveEditedMusicInfo(
                                    songId = song.id,
                                    title = editedTitle,
                                    artistName = editedArtist,
                                    albumTitle = editedAlbum,
                                    genre = editedGenre,
                                )
                            },
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::dismissEditMusicInfo) {
                            Text("Cancel")
                        }
                    },
                )
            }

        val drawerItems = remember(
            currentRoute,
            uiState.selectedLibraryTab,
        ) {
            listOf(
                DrawerDestination(
                    label = "Home",
                    icon = Icons.Rounded.Home,
                    isSelected = currentRoute == VerseFlowDestination.Home.route,
                ) {
                    navController.navigateToDrawerDestination(VerseFlowDestination.Home.route)
                },
                DrawerDestination(
                    label = "Play Queue",
                    icon = Icons.AutoMirrored.Rounded.QueueMusic,
                    isSelected = currentRoute == VerseFlowDestination.PlayQueue.route,
                ) {
                    navController.navigateToDrawerDestination(VerseFlowDestination.PlayQueue.route)
                },
                DrawerDestination(
                    label = "Playlists",
                    icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                    isSelected = currentRoute == VerseFlowDestination.Library.route &&
                        uiState.selectedLibraryTab == LibraryTab.Playlists,
                ) {
                    viewModel.selectLibraryTab(LibraryTab.Playlists)
                    navController.navigateToDrawerDestination(VerseFlowDestination.Library.route)
                },
                DrawerDestination(
                    label = "Artists",
                    icon = Icons.Rounded.Person,
                    isSelected = currentRoute == VerseFlowDestination.Library.route &&
                        uiState.selectedLibraryTab == LibraryTab.Artists,
                ) {
                    viewModel.selectLibraryTab(LibraryTab.Artists)
                    navController.navigateToDrawerDestination(VerseFlowDestination.Library.route)
                },
                DrawerDestination(
                    label = "Albums",
                    icon = Icons.Rounded.Album,
                    isSelected = currentRoute == VerseFlowDestination.Library.route &&
                        uiState.selectedLibraryTab == LibraryTab.Albums,
                ) {
                    viewModel.selectLibraryTab(LibraryTab.Albums)
                    navController.navigateToDrawerDestination(VerseFlowDestination.Library.route)
                },
                DrawerDestination(
                    label = "Songs",
                    icon = Icons.Rounded.LibraryMusic,
                    isSelected = currentRoute == VerseFlowDestination.Library.route &&
                        uiState.selectedLibraryTab == LibraryTab.Songs,
                ) {
                    viewModel.selectLibraryTab(LibraryTab.Songs)
                    navController.navigateToDrawerDestination(VerseFlowDestination.Library.route)
                },
                DrawerDestination(
                    label = "Folders",
                    icon = Icons.Rounded.Folder,
                    isSelected = currentRoute == VerseFlowDestination.Library.route &&
                        uiState.selectedLibraryTab == LibraryTab.Folders,
                ) {
                    viewModel.selectLibraryTab(LibraryTab.Folders)
                    navController.navigateToDrawerDestination(VerseFlowDestination.Library.route)
                },
                DrawerDestination(
                    label = "Genres",
                    icon = Icons.Rounded.Category,
                    isSelected = currentRoute == VerseFlowDestination.Library.route &&
                        uiState.selectedLibraryTab == LibraryTab.Genres,
                ) {
                    viewModel.selectLibraryTab(LibraryTab.Genres)
                    navController.navigateToDrawerDestination(VerseFlowDestination.Library.route)
                },
                DrawerDestination(
                    label = "Settings",
                    icon = Icons.Rounded.Settings,
                    isSelected = currentRoute == VerseFlowDestination.Settings.route,
                ) {
                    navController.navigateToDrawerDestination(VerseFlowDestination.Settings.route)
                },
            )
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = showTopLevelChrome,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    drawerContentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    ) {
                        Text(
                            text = "VerseFlow",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Move through your library from one focused drawer instead of a crowded bottom rail.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                        )
                        drawerItems.forEach { destination ->
                            NavigationDrawerItem(
                                label = { Text(destination.label) },
                                selected = destination.isSelected,
                                icon = { Icon(destination.icon, contentDescription = destination.label) },
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.close()
                                        destination.onClick()
                                    }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                ),
                            )
                        }
                    }
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                activePalette?.let {
                    ArtworkReactiveBackdrop(
                        palette = it,
                        artworkUri = currentSong?.artworkUri,
                        fallbackMediaUri = currentSong?.mediaUri,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (showMiniPlayer) {
                            currentSong?.let { song ->
                                MiniPlayerBar(
                                    song = song,
                                    artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                                    progress = if (song.durationMs == 0L) {
                                        0f
                                    } else {
                                        uiState.playback.positionMs.toFloat() / song.durationMs.toFloat()
                                    },
                                    isPlaying = uiState.playback.isPlaying,
                                    onPlayPause = viewModel::togglePlayPause,
                                    onPrevious = viewModel::skipPrevious,
                                    onNext = viewModel::skipNext,
                                    onClick = { navController.navigate(VerseFlowDestination.NowPlaying.route) },
                                )
                            }
                        }
                    },
                ) { innerPadding ->
                    VerseFlowNavHost(
                        navController = navController,
                        uiState = uiState,
                        viewModel = viewModel,
                        onRequestAudioPermission = requestAudioPermission,
                        onOpenDrawer = {
                            if (showTopLevelChrome) {
                                coroutineScope.launch { drawerState.open() }
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

private data class DrawerDestination(
    val label: String,
    val icon: ImageVector,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)

private fun androidx.navigation.NavHostController.navigateToDrawerDestination(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(VerseFlowDestination.Home.route) {
            saveState = true
        }
    }
}
