package com.example.verseflow.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.ArtistCard
import com.example.verseflow.ui.components.DeviceLibraryStatusCard
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.PlaylistCard
import com.example.verseflow.ui.components.SectionHeader
import com.example.verseflow.ui.components.formatDuration
import java.util.Calendar

@Composable
fun HomeScreen(
    uiState: VerseFlowUiState,
    onAlbumClick: (Album) -> Unit,
    onSongClick: (Song) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onAddAlbumToPlaylist: (playlistId: String, albumId: String) -> Unit,
    onAddAlbumToPlayQueue: (Album) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onRequestAudioPermission: () -> Unit,
) {
    val recentlyPlayedAlbums = remember(uiState.recentlyPlayed, uiState.albumsById) {
        uiState.recentlyPlayed
            .mapNotNull { uiState.albumsById[it.albumId] }
            .distinctBy(Album::id)
    }
    var playlistPickerAlbum by remember { mutableStateOf<Album?>(null) }
    val availablePlaylists = remember(uiState.playlists) {
        uiState.playlists.sortedBy { it.title.lowercase() }
    }

    if (playlistPickerAlbum != null) {
        val album = playlistPickerAlbum!!
        AlertDialog(
            onDismissRequest = { playlistPickerAlbum = null },
            title = { Text("Add to playlist") },
            text = {
                if (availablePlaylists.isEmpty()) {
                    Text(
                        text = "No playlists are available yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(availablePlaylists, key = { it.id }) { playlist ->
                            TextButton(
                                onClick = {
                                    onAddAlbumToPlaylist(playlist.id, album.id)
                                    playlistPickerAlbum = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = playlist.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = playlist.curator,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { playlistPickerAlbum = null }) {
                    Text("Close")
                }
            },
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(top = 22.dp, bottom = 180.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HomeHeader(
                name = uiState.profile.displayName,
                tier = uiState.profile.membershipTier,
                onOpenDrawer = onOpenDrawer,
                onOpenSearch = onOpenSearch,
            )
        }
        item {
            DeviceLibraryStatusCard(
                audioPermissionGranted = uiState.audioPermissionGranted,
                hasScannedDeviceAudio = uiState.hasScannedDeviceAudio,
                isScanningDeviceAudio = uiState.isScanningDeviceAudio,
                catalogSource = uiState.catalogSource,
                songCount = uiState.songs.size,
                onAction = onRequestAudioPermission,
                shape = RectangleShape,
                surfaceAlpha = 0.58f,
                surfaceVariantAlpha = 0.20f,
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(
                        title = "Featured Orbit",
                        subtitle = "Cinematic albums built for full-screen listening",
                    )
                }
                FeaturedCarousel(
                    albums = uiState.featuredAlbums,
                    onAlbumClick = onAlbumClick,
                )
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(
                        title = "Recently Played",
                        subtitle = "Albums connected to the songs you touched most recently",
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                ) {
                    items(recentlyPlayedAlbums, key = { it.id }) { album ->
                        RecentlyPlayedAlbumCard(
                            album = album,
                            artistName = uiState.artistsById[album.artistId]?.name.orEmpty(),
                            onClick = { onAlbumClick(album) },
                            onAddToPlaylist = { playlistPickerAlbum = album },
                            onAddToPlayQueue = { onAddAlbumToPlayQueue(album) },
                            onOpenArtist = {
                                uiState.artistsById[album.artistId]?.let(onArtistClick)
                            },
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(
                        title = "Trending Tracks",
                        subtitle = "Signal peaks from your current wave",
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                ) {
                    items(uiState.trendingSongs.take(5), key = { it.id }) { song ->
                        SongFeatureCard(
                            song = song,
                            artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                            supportingText = "${song.mood} • ${formatDuration(song.durationMs)}",
                            onClick = { onSongClick(song) },
                        )
                    }
                } 
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(
                        title = "Favorite Playlists",
                        subtitle = "Curated worlds ready to launch",
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                ) {
                    items(uiState.favoritePlaylists, key = { it.id }) { playlist ->
                        PlaylistCard(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist) },
                            shape = RectangleShape,
                            surfaceAlpha = 0.56f,
                            surfaceVariantAlpha = 0.18f,
                            topArtworkBleed = true,
                            artworkHeight = 188.dp,
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(
                        title = "Artists in Rotation",
                        subtitle = "Stay close to the voices behind the glow",
                    )
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.artists.take(4), key = { it.id }) { artist ->
                        ArtistCard(
                            artist = artist,
                            onClick = { onArtistClick(artist) },
                            shape = RectangleShape,
                            surfaceAlpha = 0.56f,
                            surfaceVariantAlpha = 0.18f,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SongFeatureCard(
    song: Song,
    artistName: String,
    supportingText: String,
    onClick: () -> Unit,
) {
    GlassPanel(
        modifier = Modifier
            .size(width = 250.dp, height = 292.dp)
            .clickable(onClick = onClick),
        shape = RectangleShape,
        surfaceAlpha = 0.56f,
        surfaceVariantAlpha = 0.18f,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            AlbumArtwork(
                title = song.title,
                subtitle = artistName,
                palette = song.palette,
                artworkUri = song.artworkUri,
                fallbackMediaUri = song.mediaUri,
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
                    text = song.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = supportingText,
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
private fun RecentlyPlayedAlbumCard(
    album: Album,
    artistName: String,
    onClick: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onAddToPlayQueue: () -> Unit,
    onOpenArtist: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    GlassPanel(
        modifier = Modifier
            .size(width = 250.dp, height = 292.dp)
            .clickable(onClick = onClick),
        shape = RectangleShape,
        surfaceAlpha = 0.56f,
        surfaceVariantAlpha = 0.18f,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            AlbumArtwork(
                title = album.title,
                subtitle = artistName,
                palette = album.palette,
                artworkUri = album.artworkUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp),
                shape = RectangleShape,
                borderColor = Color.Transparent,
                showOverlay = false,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${album.trackIds.size} songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Album options",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("View", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                            onClick = {
                                menuExpanded = false
                                onClick()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Add to playlist", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                            onClick = {
                                menuExpanded = false
                                onAddToPlaylist()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Add to play queue", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                            onClick = {
                                menuExpanded = false
                                onAddToPlayQueue()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Artist", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                            onClick = {
                                menuExpanded = false
                                onOpenArtist()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    name: String,
    tier: String,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlowIconButton(
                icon = Icons.Rounded.Menu,
                contentDescription = "Open navigation",
                onClick = onOpenDrawer,
            )
            IconButton(onClick = onOpenSearch) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            surfaceAlpha = 0.58f,
            surfaceVariantAlpha = 0.20f,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${greetingForCurrentTime()}, $name",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Step back into your futuristic listening room.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = tier,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}

private fun greetingForCurrentTime(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Good night"
    }
}

@Composable
private fun FeaturedCarousel(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { albums.size })
    if (albums.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(end = 10.dp),
            pageSpacing = 8.dp,
        ) { page ->
            val album = albums[page]
            val trackCountLabel = if (album.label.contains("library", ignoreCase = true)) {
                "${album.trackIds.size} songs imported"
            } else {
                "${album.trackIds.size} songs in album"
            }
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onAlbumClick(album) }),
                shape = RectangleShape,
                surfaceAlpha = 0.56f,
                surfaceVariantAlpha = 0.18f,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    AlbumArtwork(
                        title = album.title,
                        subtitle = album.label,
                        palette = album.palette,
                        artworkUri = album.artworkUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
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
                            text = album.title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = album.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = trackCountLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(albums.size) { index ->
                val selected = pagerState.currentPage == index
                Spacer(
                    modifier = Modifier
                        .height(6.dp)
                        .weight(if (selected) 1.6f else 1f)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(100),
                        ),
                )
            }
        }
    }
}
