package com.example.verseflow.ui.screens.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.AccentPalette
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.LibraryTab
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.AlbumCard
import com.example.verseflow.ui.components.DeviceLibraryStatusCard
import com.example.verseflow.ui.components.EmptyStatePanel
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.PlaylistCard
import com.example.verseflow.ui.components.SongListItem
import com.example.verseflow.ui.components.SongOverflowMenu
import com.example.verseflow.ui.components.formatDuration

@Composable
fun LibraryScreen(
    uiState: VerseFlowUiState,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onLibraryTabChange: (LibraryTab) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onShuffleAllSongs: () -> Unit,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddSongToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddSongToPlayQueue: (songId: String) -> Unit,
    onToggleSongLike: (songId: String) -> Unit,
    onDeleteFromStorage: (songId: String) -> Unit,
    onEditMusicInfo: (songId: String) -> Unit,
    onCreatePlaylist: (String, String) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onRequestAudioPermission: () -> Unit,
) {
    val alphabeticalSongs = remember(uiState.songs) {
        uiState.songs.sortedBy { it.title.lowercase() }
    }
    val alphabeticalAlbums = remember(uiState.albums) {
        uiState.albums.sortedBy { it.title.lowercase() }
    }
    val alphabeticalArtists = remember(uiState.artists) {
        uiState.artists.sortedBy { it.name.lowercase() }
    }
    val visiblePlaylists = remember(uiState.playlists) {
        uiState.playlists.sortedWith(
            compareByDescending<Playlist> { it.isUserCreated }
                .thenBy { it.title.lowercase() },
        )
    }
    val genres = remember(uiState.songs, uiState.artistsById) {
        uiState.songs
            .groupBy {
                it.genre
                    ?: uiState.artistsById[it.artistId]
                        ?.genre
                        ?.takeUnless { genre -> genre.equals("Local Library", ignoreCase = true) }
                    ?: "Unclassified"
            }
            .map { (name, songs) ->
                val firstSong = songs.first()
                GenreSummary(
                    name = name,
                    songCount = songs.size,
                    palette = firstSong.palette,
                    artworkUri = firstSong.artworkUri,
                    fallbackMediaUri = firstSong.mediaUri,
                )
            }
            .sortedBy { it.name.lowercase() }
    }
    val folders = remember(uiState.songs) {
        uiState.songs
            .filter { !it.folderName.isNullOrBlank() }
            .groupBy { it.folderPath ?: it.folderName.orEmpty() }
            .mapNotNull { (path, songs) ->
                val firstSong = songs.firstOrNull() ?: return@mapNotNull null
                val folderName = firstSong.folderName ?: return@mapNotNull null
                FolderSummary(
                    name = folderName,
                    path = path,
                    songCount = songs.size,
                    artistCount = songs.map(Song::artistId).distinct().size,
                    songs = songs.sortedBy { it.title.lowercase() },
                    palette = firstSong.palette,
                    artworkUri = firstSong.artworkUri,
                    fallbackMediaUri = firstSong.mediaUri,
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    val songsListState = rememberLazyListState()
    val albumsListState = rememberLazyListState()
    val artistsListState = rememberLazyListState()
    val playlistsListState = rememberLazyListState()
    val foldersListState = rememberLazyListState()
    val genresListState = rememberLazyListState()

    val showDeviceStatusCard by remember(
        uiState.selectedLibraryTab,
        songsListState,
        albumsListState,
        artistsListState,
        playlistsListState,
        foldersListState,
        genresListState,
    ) {
        derivedStateOf {
            val activeState = when (uiState.selectedLibraryTab) {
                LibraryTab.Songs -> songsListState
                LibraryTab.Albums -> albumsListState
                LibraryTab.Artists -> artistsListState
                LibraryTab.Playlists -> playlistsListState
                LibraryTab.Folders -> foldersListState
                LibraryTab.Genres -> genresListState
            }
            activeState.firstVisibleItemIndex == 0 && activeState.firstVisibleItemScrollOffset < 12
        }
    }

    var showCreatePlaylistDialog by rememberSaveable { mutableStateOf(false) }
    var playlistTitle by rememberSaveable { mutableStateOf("") }
    var playlistDescription by rememberSaveable { mutableStateOf("") }
    var selectedFolderPath by rememberSaveable { mutableStateOf<String?>(null) }
    var showDisplayModeMenu by rememberSaveable { mutableStateOf(false) }
    var albumGridView by rememberSaveable { mutableStateOf(true) }
    var genreGridView by rememberSaveable { mutableStateOf(true) }

    val selectedFolder = remember(selectedFolderPath, folders) {
        folders.firstOrNull { it.path == selectedFolderPath }
    }

    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreatePlaylistDialog = false
                playlistTitle = ""
                playlistDescription = ""
            },
            title = { Text("Create Playlist") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = playlistTitle,
                        onValueChange = { playlistTitle = it },
                        singleLine = true,
                        placeholder = { Text("Playlist name") },
                    )
                    TextField(
                        value = playlistDescription,
                        onValueChange = { playlistDescription = it },
                        minLines = 2,
                        maxLines = 3,
                        placeholder = { Text("Short description") },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = playlistTitle.trim().isNotBlank(),
                    onClick = {
                        onCreatePlaylist(playlistTitle, playlistDescription)
                        showCreatePlaylistDialog = false
                        playlistTitle = ""
                        playlistDescription = ""
                    },
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreatePlaylistDialog = false
                        playlistTitle = ""
                        playlistDescription = ""
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    if (selectedFolder != null) {
        AlertDialog(
            onDismissRequest = { selectedFolderPath = null },
            title = { Text(selectedFolder.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = selectedFolder.path,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        items(selectedFolder.songs, key = { it.id }) { song ->
                            SongListItem(
                                song = song,
                                artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                                supportingText = formatDuration(song.durationMs),
                                onClick = {
                                    selectedFolderPath = null
                                    onSongClick(song)
                                },
                                shape = RectangleShape,
                                surfaceAlpha = 0f,
                                surfaceVariantAlpha = 0f,
                                borderAlpha = 0f,
                                shadowElevation = 0.dp,
                                itemPadding = 6.dp,
                                artworkSize = 52.dp,
                                itemSpacing = 6.dp,
                                artworkShape = RectangleShape,
                                showArtworkOverlay = false,
                                trailingContent = {
                                    SongOverflowMenu(
                                        song = song,
                                        artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                                        albumTitle = uiState.albumsById[song.albumId]?.title.orEmpty(),
                                        playlists = uiState.playlists,
                                        isFavorite = song.id in uiState.playback.likedSongIds,
                                        onRemoveFromVerseFlow = onRemoveFromVerseFlow,
                                        onAddToPlaylist = onAddSongToPlaylist,
                                        onAddToPlayQueue = onAddSongToPlayQueue,
                                        onToggleFavorite = onToggleSongLike,
                                        onOpenArtist = {
                                            uiState.artistsById[song.artistId]?.let(onArtistClick)
                                        },
                                        onOpenAlbum = {
                                            uiState.albumsById[song.albumId]?.let(onAlbumClick)
                                        },
                                        onDeleteFromStorage = onDeleteFromStorage,
                                        onEditMusicInfo = onEditMusicInfo,
                                    )
                                },
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedFolderPath = null }) {
                    Text("Close")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlowIconButton(
                    icon = Icons.Rounded.Menu,
                    contentDescription = "Open navigation",
                    onClick = onOpenDrawer,
                )
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "Library",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Every saved song, album, artist, playlist, folder, and genre.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onOpenSearch) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search library",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (uiState.selectedLibraryTab == LibraryTab.Albums || uiState.selectedLibraryTab == LibraryTab.Genres) {
                        Box {
                            IconButton(onClick = { showDisplayModeMenu = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = "Display options",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            DropdownMenu(
                                expanded = showDisplayModeMenu,
                                onDismissRequest = { showDisplayModeMenu = false },
                            ) {
                                val isGridView = if (uiState.selectedLibraryTab == LibraryTab.Albums) {
                                    albumGridView
                                } else {
                                    genreGridView
                                }
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (isGridView) "Show list" else "Show grid",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
                                        )
                                    },
                                    onClick = {
                                        if (uiState.selectedLibraryTab == LibraryTab.Albums) {
                                            albumGridView = !albumGridView
                                        } else {
                                            genreGridView = !genreGridView
                                        }
                                        showDisplayModeMenu = false
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showDeviceStatusCard,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            DeviceLibraryStatusCard(
                audioPermissionGranted = uiState.audioPermissionGranted,
                hasScannedDeviceAudio = uiState.hasScannedDeviceAudio,
                isScanningDeviceAudio = uiState.isScanningDeviceAudio,
                catalogSource = uiState.catalogSource,
                songCount = uiState.songs.size,
                onAction = onRequestAudioPermission,
                shape = RectangleShape,
                surfaceAlpha = 0.52f,
                surfaceVariantAlpha = 0.14f,
            )
        }

        PrimaryScrollableTabRow(
            selectedTabIndex = uiState.selectedLibraryTab.ordinal,
            edgePadding = 20.dp,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
            contentColor = MaterialTheme.colorScheme.secondary,
            indicator = {},
        ) {
            LibraryTab.entries.forEach { tab ->
                Tab(
                    selected = tab == uiState.selectedLibraryTab,
                    onClick = { onLibraryTabChange(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            color = if (tab == uiState.selectedLibraryTab) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    },
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            AnimatedContent(
                targetState = uiState.selectedLibraryTab,
                label = "libraryTab",
            ) { tab ->
                when (tab) {
                    LibraryTab.Songs -> {
                        if (alphabeticalSongs.isEmpty()) {
                            EmptyStatePanel(
                                title = "No songs yet",
                                body = "Import music from your device to populate this library.",
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        } else {
                            LazyColumn(
                                state = songsListState,
                                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(1.dp),
                            ) {
                                item {
                                    GlassPanel(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = onShuffleAllSongs),
                                        shape = RectangleShape,
                                        surfaceAlpha = 0.44f,
                                        surfaceVariantAlpha = 0.10f,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = "Shuffle all songs",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Icon(
                                                imageVector = Icons.Rounded.Shuffle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                            )
                                        }
                                    }
                                }
                                items(alphabeticalSongs, key = { it.id }) { song ->
                                    SongListItem(
                                        song = song,
                                        artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                                        supportingText = formatDuration(song.durationMs),
                                        onClick = { onSongClick(song) },
                                        shape = RectangleShape,
                                        surfaceAlpha = 0f,
                                        surfaceVariantAlpha = 0f,
                                        borderAlpha = 0f,
                                        shadowElevation = 0.dp,
                                        itemPadding = 5.dp,
                                        artworkSize = 50.dp,
                                        itemSpacing = 6.dp,
                                        artworkShape = RectangleShape,
                                        showArtworkOverlay = false,
                                        trailingContent = {
                                            SongOverflowMenu(
                                                song = song,
                                                artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                                                albumTitle = uiState.albumsById[song.albumId]?.title.orEmpty(),
                                                playlists = uiState.playlists,
                                                isFavorite = song.id in uiState.playback.likedSongIds,
                                                onRemoveFromVerseFlow = onRemoveFromVerseFlow,
                                                onAddToPlaylist = onAddSongToPlaylist,
                                                onAddToPlayQueue = onAddSongToPlayQueue,
                                                onToggleFavorite = onToggleSongLike,
                                                onOpenArtist = {
                                                    uiState.artistsById[song.artistId]?.let(onArtistClick)
                                                },
                                                onOpenAlbum = {
                                                    uiState.albumsById[song.albumId]?.let(onAlbumClick)
                                                },
                                                onDeleteFromStorage = onDeleteFromStorage,
                                                onEditMusicInfo = onEditMusicInfo,
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }

                    LibraryTab.Albums -> {
                        if (albumGridView) {
                            LazyColumn(
                                state = albumsListState,
                                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(alphabeticalAlbums, key = { it.id }) { album ->
                                    AlbumCard(
                                        album = album,
                                        artistName = uiState.artistsById[album.artistId]?.name.orEmpty(),
                                        onClick = { onAlbumClick(album) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RectangleShape,
                                        fixedWidth = null,
                                        surfaceAlpha = 0.48f,
                                        surfaceVariantAlpha = 0.12f,
                                        topArtworkBleed = true,
                                        artworkHeight = 208.dp,
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                state = albumsListState,
                                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(1.dp),
                            ) {
                                items(alphabeticalAlbums, key = { it.id }) { album ->
                                    LibraryAlbumListItem(
                                        album = album,
                                        artistName = uiState.artistsById[album.artistId]?.name.orEmpty(),
                                        onClick = { onAlbumClick(album) },
                                    )
                                }
                            }
                        }
                    }

                    LibraryTab.Artists -> {
                        LazyColumn(
                            state = artistsListState,
                            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(alphabeticalArtists.chunked(2), key = { row -> row.joinToString { it.id } }) { rowArtists ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    rowArtists.forEach { artist ->
                                        LibraryArtistCard(
                                            artist = artist,
                                            songCount = uiState.songs.count { it.artistId == artist.id },
                                            onClick = { onArtistClick(artist) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    if (rowArtists.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    LibraryTab.Playlists -> {
                        LazyColumn(
                            state = playlistsListState,
                            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(visiblePlaylists, key = { it.id }) { playlist ->
                                Box {
                                    PlaylistCard(
                                        playlist = playlist,
                                        onClick = { onPlaylistClick(playlist) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RectangleShape,
                                        fixedWidth = null,
                                        surfaceAlpha = 0.48f,
                                        surfaceVariantAlpha = 0.12f,
                                        topArtworkBleed = true,
                                        artworkHeight = 208.dp,
                                    )
                                    IconButton(
                                        onClick = { onDeletePlaylist(playlist.id) },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 6.dp, bottom = 6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Delete playlist",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    LibraryTab.Folders -> {
                        if (folders.isEmpty()) {
                            EmptyStatePanel(
                                title = "No folders available",
                                body = "Folders appear when local songs expose path information from your device library.",
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        } else {
                            LazyColumn(
                                state = foldersListState,
                                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(folders, key = { it.path }) { folder ->
                                    LibraryFolderCard(
                                        summary = folder,
                                        onClick = { selectedFolderPath = folder.path },
                                    )
                                }
                            }
                        }
                    }

                    LibraryTab.Genres -> {
                        if (genreGridView) {
                            LazyColumn(
                                state = genresListState,
                                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(genres, key = { it.name }) { genre ->
                                    LibraryGenreCard(summary = genre)
                                }
                            }
                        } else {
                            LazyColumn(
                                state = genresListState,
                                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(1.dp),
                            ) {
                                items(genres, key = { it.name }) { genre ->
                                    LibraryGenreListItem(summary = genre)
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.selectedLibraryTab == LibraryTab.Playlists) {
                FloatingActionButton(
                    onClick = { showCreatePlaylistDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Create playlist",
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryArtistCard(
    artist: Artist,
    songCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassPanel(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RectangleShape,
        surfaceAlpha = 0.48f,
        surfaceVariantAlpha = 0.12f,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                artist.heroPalette.primary,
                                artist.heroPalette.secondary,
                                artist.heroPalette.tertiary,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = artist.name.take(2).uppercase(),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White.copy(alpha = 0.86f),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = artist.genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$songCount songs in library",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LibraryAlbumListItem(
    album: Album,
    artistName: String,
    onClick: () -> Unit,
) {
    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RectangleShape,
        surfaceAlpha = 0f,
        surfaceVariantAlpha = 0f,
        borderAlpha = 0f,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AlbumArtwork(
                title = album.title,
                subtitle = artistName,
                palette = album.palette,
                artworkUri = album.artworkUri,
                modifier = Modifier.size(54.dp),
                shape = RectangleShape,
                borderColor = Color.Transparent,
                showOverlay = false,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LibraryGenreCard(
    summary: GenreSummary,
) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        surfaceAlpha = 0.48f,
        surfaceVariantAlpha = 0.12f,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            AlbumArtwork(
                title = summary.name,
                subtitle = "Genre",
                palette = summary.palette,
                artworkUri = summary.artworkUri,
                fallbackMediaUri = summary.fallbackMediaUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp),
                shape = RectangleShape,
                borderColor = Color.Transparent,
                showOverlay = false,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = summary.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Genre collection",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = "${summary.songCount} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LibraryGenreListItem(
    summary: GenreSummary,
) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        surfaceAlpha = 0f,
        surfaceVariantAlpha = 0f,
        borderAlpha = 0f,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AlbumArtwork(
                title = summary.name,
                subtitle = "Genre",
                palette = summary.palette,
                artworkUri = summary.artworkUri,
                fallbackMediaUri = summary.fallbackMediaUri,
                modifier = Modifier.size(54.dp),
                shape = RectangleShape,
                borderColor = Color.Transparent,
                showOverlay = false,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = summary.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${summary.songCount} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LibraryFolderCard(
    summary: FolderSummary,
    onClick: () -> Unit,
) {
    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RectangleShape,
        surfaceAlpha = 0.48f,
        surfaceVariantAlpha = 0.12f,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            AlbumArtwork(
                title = summary.name,
                subtitle = "Folder",
                palette = summary.palette,
                artworkUri = summary.artworkUri,
                fallbackMediaUri = summary.fallbackMediaUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp),
                shape = RectangleShape,
                borderColor = Color.Transparent,
                showOverlay = false,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = summary.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = summary.path,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${summary.songCount} songs • ${summary.artistCount} artists",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private data class GenreSummary(
    val name: String,
    val songCount: Int,
    val palette: AccentPalette,
    val artworkUri: String?,
    val fallbackMediaUri: String?,
)

private data class FolderSummary(
    val name: String,
    val path: String,
    val songCount: Int,
    val artistCount: Int,
    val songs: List<Song>,
    val palette: AccentPalette,
    val artworkUri: String?,
    val fallbackMediaUri: String?,
)
