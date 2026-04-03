package com.example.verseflow.ui.screens.queue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.EmptyStatePanel
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.SongListItem
import com.example.verseflow.ui.components.SongOverflowMenu
import com.example.verseflow.ui.components.formatDuration

@Composable
fun PlayQueueScreen(
    uiState: VerseFlowUiState,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongClick: (Song) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddSongToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddSongToPlayQueue: (songId: String) -> Unit,
    onToggleSongLike: (songId: String) -> Unit,
    onDeleteFromStorage: (songId: String) -> Unit,
    onEditMusicInfo: (songId: String) -> Unit,
) {
    val queue = uiState.playQueueSongIds.mapNotNull(uiState.songsById::get)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlowIconButton(
                icon = Icons.Rounded.Menu,
                contentDescription = "Open navigation",
                onClick = onOpenDrawer,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Play Queue",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Songs you deliberately added from the app menus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onOpenSearch) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        if (queue.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                EmptyStatePanel(
                    title = "Queue is empty",
                    body = "Use the three-dot menu on song cards and choose Add to play queue.",
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                )
            }
        } else {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                surfaceAlpha = 0.46f,
                surfaceVariantAlpha = 0.12f,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    items(queue, key = { it.id }) { song ->
                        SongListItem(
                            song = song,
                            artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                            supportingText = if (song.id == uiState.playback.currentSong?.id) {
                                "Playing now"
                            } else {
                                formatDuration(song.durationMs)
                            },
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
        }
    }
}
