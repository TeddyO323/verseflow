package com.example.verseflow.ui.screens.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.ArtworkReactiveBackdrop
import com.example.verseflow.ui.components.EmptyStatePanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.PlaybackControls
import com.example.verseflow.ui.components.PlaybackProgress
import com.example.verseflow.ui.components.PlaybackQueueSheet
import com.example.verseflow.ui.components.SongOverflowMenu
import com.example.verseflow.ui.components.WaveVisualizer
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingScreen(
    uiState: VerseFlowUiState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleLike: () -> Unit,
    onQueueVisibilityChange: (Boolean) -> Unit,
    onQueueSongSelected: (Song) -> Unit,
    onSearchRequested: () -> Unit,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddSongToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddSongToPlayQueue: (songId: String) -> Unit,
    onToggleSongLike: (songId: String) -> Unit,
    onDeleteFromStorage: (songId: String) -> Unit,
    onEditMusicInfo: (songId: String) -> Unit,
    onArtistRequested: (String) -> Unit,
    onAlbumRequested: (String) -> Unit,
    onLyricsRequested: () -> Unit,
) {
    val song = uiState.playback.currentSong
    val artistName = song?.let { uiState.artistsById[it.artistId]?.name.orEmpty() }.orEmpty()
    val albumTitle = song?.let { uiState.albumsById[it.albumId]?.title.orEmpty() }.orEmpty()
    if (song == null) {
        EmptyStatePanel(
            title = "Nothing is playing",
            body = "Start a track from Home, Library, or Search to enter the immersive player.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        return
    }

    if (uiState.playback.isQueueSheetVisible) {
        PlaybackQueueSheet(
            songs = uiState.playback.queue,
            currentSongId = song.id,
            artistsById = uiState.artistsById,
            onDismiss = { onQueueVisibilityChange(false) },
            onSongSelected = {
                onQueueSongSelected(it)
                onQueueVisibilityChange(false)
            },
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        ArtworkReactiveBackdrop(
            palette = song.palette,
            artworkUri = song.artworkUri,
            fallbackMediaUri = song.mediaUri,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            AnimatedContent(
                targetState = song.id,
                label = "nowPlayingSong",
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(470.dp)
                            .pointerInput(song.id) {
                                var totalDragX = 0f
                                var totalDragY = 0f
                                detectDragGestures(
                                    onDragCancel = {
                                        totalDragX = 0f
                                        totalDragY = 0f
                                    },
                                    onDragEnd = {
                                        when {
                                            totalDragY < -140f && abs(totalDragY) > abs(totalDragX) -> onLyricsRequested()
                                            totalDragX < -140f && abs(totalDragX) > abs(totalDragY) -> onNext()
                                            totalDragX > 140f && abs(totalDragX) > abs(totalDragY) -> onPrevious()
                                        }
                                        totalDragX = 0f
                                        totalDragY = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        totalDragX += dragAmount.x
                                        totalDragY += dragAmount.y
                                    },
                                )
                            },
                    ) {
                        AlbumArtwork(
                            title = song.title,
                            subtitle = artistName,
                            palette = song.palette,
                            artworkUri = song.artworkUri,
                            fallbackMediaUri = song.mediaUri,
                            modifier = Modifier.fillMaxSize(),
                            shape = RectangleShape,
                            borderColor = Color.Transparent,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(start = 24.dp, end = 0.dp, top = 20.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            GlowIconButton(
                                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                onClick = onBack,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(onClick = onSearchRequested) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                GlowIconButton(
                                    icon = Icons.AutoMirrored.Rounded.QueueMusic,
                                    contentDescription = "Queue",
                                    onClick = { onQueueVisibilityChange(true) },
                                )
                                SongOverflowMenu(
                                    song = song,
                                    artistName = artistName,
                                    albumTitle = albumTitle,
                                    playlists = uiState.playlists,
                                    isFavorite = song.id in uiState.playback.likedSongIds,
                                    onRemoveFromVerseFlow = onRemoveFromVerseFlow,
                                    onAddToPlaylist = onAddSongToPlaylist,
                                    onAddToPlayQueue = onAddSongToPlayQueue,
                                    onToggleFavorite = onToggleSongLike,
                                    onOpenArtist = { onArtistRequested(song.artistId) },
                                    onOpenAlbum = { onAlbumRequested(song.albumId) },
                                    onDeleteFromStorage = onDeleteFromStorage,
                                    onEditMusicInfo = onEditMusicInfo,
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .basicMarquee(
                                    iterations = Int.MAX_VALUE,
                                    initialDelayMillis = 600,
                                    repeatDelayMillis = 900,
                                ),
                        )
                        Text(
                            text = artistName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = albumTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        WaveVisualizer(
                            isPlaying = uiState.playback.isPlaying,
                            modifier = Modifier
                                .height(32.dp)
                                .size(width = 48.dp, height = 32.dp),
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                PlaybackProgress(
                    positionMs = uiState.playback.positionMs,
                    durationMs = song.durationMs,
                    onSeek = onSeek,
                )
                PlaybackControls(
                    isPlaying = uiState.playback.isPlaying,
                    isShuffled = uiState.playback.isShuffled,
                    repeatMode = uiState.playback.repeatMode,
                    isLiked = song.id in uiState.playback.likedSongIds,
                    onShuffle = onToggleShuffle,
                    onPrevious = onPrevious,
                    onPlayPause = onPlayPause,
                    onNext = onNext,
                    onRepeat = onCycleRepeat,
                    onLike = onToggleLike,
                    onQueue = { onQueueVisibilityChange(true) },
                )
            }
        }
    }
}
