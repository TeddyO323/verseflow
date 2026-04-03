package com.example.verseflow.ui.screens.album

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.SectionHeader
import com.example.verseflow.ui.components.SongListItem
import com.example.verseflow.ui.components.SongOverflowMenu
import com.example.verseflow.ui.components.formatDuration

@Composable
fun AlbumDetailScreen(
    album: Album,
    uiState: VerseFlowUiState,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddSongToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddSongToPlayQueue: (songId: String) -> Unit,
    onToggleSongLike: (songId: String) -> Unit,
    onDeleteFromStorage: (songId: String) -> Unit,
    onEditMusicInfo: (songId: String) -> Unit,
) {
    val artistName = uiState.artistsById[album.artistId]?.name.orEmpty()
    val songs = album.trackIds.mapNotNull(uiState.songsById::get)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    text = "Album",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                GlowIconButton(
                    icon = Icons.Rounded.PlayArrow,
                    contentDescription = "Play album",
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
                surfaceAlpha = 0.52f,
                surfaceVariantAlpha = 0.16f,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AlbumArtwork(
                        title = album.title,
                        subtitle = artistName,
                        palette = album.palette,
                        artworkUri = album.artworkUri,
                        modifier = Modifier.size(164.dp),
                        shape = RectangleShape,
                        showOverlay = false,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = album.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = artistName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${songs.size} songs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "${album.year} • ${album.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
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
                surfaceAlpha = 0.46f,
                surfaceVariantAlpha = 0.12f,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Album controls",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlowIconButton(
                            icon = Icons.Rounded.Shuffle,
                            contentDescription = "Shuffle album",
                            onClick = onShuffle,
                        )
                        GlowIconButton(
                            icon = Icons.Rounded.PlayArrow,
                            contentDescription = "Play album",
                            onClick = onPlayAll,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
        item {
            SectionHeader(
                title = "Songs",
                subtitle = "Play any track and stay inside this album queue",
            )
        }
        items(songs, key = { it.id }) { song ->
            SongListItem(
                song = song,
                artistName = artistName,
                supportingText = formatDuration(song.durationMs),
                onClick = { onSongClick(song) },
                shape = RectangleShape,
                surfaceAlpha = 0f,
                surfaceVariantAlpha = 0f,
                borderAlpha = 0f,
                shadowElevation = 0.dp,
                itemPadding = 8.dp,
                artworkSize = 54.dp,
                itemSpacing = 8.dp,
                artworkShape = RectangleShape,
                showArtworkOverlay = false,
                trailingContent = {
                    SongOverflowMenu(
                        song = song,
                        artistName = artistName,
                        albumTitle = album.title,
                        playlists = uiState.playlists,
                        isFavorite = song.id in uiState.playback.likedSongIds,
                        onRemoveFromVerseFlow = onRemoveFromVerseFlow,
                        onAddToPlaylist = onAddSongToPlaylist,
                        onAddToPlayQueue = onAddSongToPlayQueue,
                        onToggleFavorite = onToggleSongLike,
                        onOpenArtist = {
                            uiState.artistsById[song.artistId]?.let(onArtistClick)
                        },
                        onOpenAlbum = { onAlbumClick(album) },
                        onDeleteFromStorage = onDeleteFromStorage,
                        onEditMusicInfo = onEditMusicInfo,
                    )
                },
            )
        }
    }
}
