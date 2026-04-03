package com.example.verseflow.ui.screens.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.SongListItem
import com.example.verseflow.ui.components.SongOverflowMenu
import com.example.verseflow.ui.components.formatDuration

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    uiState: VerseFlowUiState,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddSongToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddSongToPlayQueue: (songId: String) -> Unit,
    onToggleSongLike: (songId: String) -> Unit,
    onDeleteFromStorage: (songId: String) -> Unit,
    onEditMusicInfo: (songId: String) -> Unit,
) {
    val songs = remember(playlist.trackIds, uiState.songsById) {
        playlist.trackIds.mapNotNull(uiState.songsById::get)
    }
    val totalDurationMs = remember(songs) { songs.sumOf(Song::durationMs) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(top = 20.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlowIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                )
                Text(
                    text = "Playlist",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                GlowIconButton(
                    icon = Icons.Rounded.PlayArrow,
                    contentDescription = "Play all",
                    onClick = onPlayAll,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                surfaceAlpha = 0.50f,
                surfaceVariantAlpha = 0.12f,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    PlaylistArtworkCollage(
                        playlist = playlist,
                        songs = songs,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(296.dp),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = playlist.title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${songs.size} songs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = formatDuration(totalDurationMs),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Tracks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlowIconButton(
                            icon = Icons.Rounded.Shuffle,
                            contentDescription = "Shuffle",
                            onClick = onShuffle,
                        )
                        GlowIconButton(
                            icon = Icons.Rounded.PlayArrow,
                            contentDescription = "Play all",
                            onClick = onPlayAll,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
        items(songs, key = { it.id }) { song ->
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
                itemPadding = 2.dp,
                artworkSize = 46.dp,
                itemSpacing = 3.dp,
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
                        onOpenArtist = { onArtistClick(song.artistId) },
                        onOpenAlbum = { onAlbumClick(song.albumId) },
                        onDeleteFromStorage = onDeleteFromStorage,
                        onEditMusicInfo = onEditMusicInfo,
                    )
                },
            )
        }
    }
}

@Composable
private fun PlaylistArtworkCollage(
    playlist: Playlist,
    songs: List<Song>,
    modifier: Modifier = Modifier,
) {
    val artworkSongs = remember(playlist.id, songs) {
        songs
            .distinctBy { it.artworkUri ?: it.albumId }
            .sortedBy { "${playlist.id}:${it.id}".hashCode() }
            .take(4)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        repeat(2) { rowIndex ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                repeat(2) { columnIndex ->
                    val index = rowIndex * 2 + columnIndex
                    val song = artworkSongs.getOrNull(index)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        AlbumArtwork(
                            title = song?.title ?: playlist.title,
                            subtitle = song?.let { "Track" } ?: playlist.curator,
                            palette = song?.palette ?: playlist.palette,
                            artworkUri = song?.artworkUri,
                            fallbackMediaUri = song?.mediaUri,
                            modifier = Modifier.fillMaxSize(),
                            shape = RectangleShape,
                            borderColor = Color.Transparent,
                            showOverlay = false,
                        )
                    }
                }
            }
        }
    }
}
