package com.example.verseflow.ui.screens.lyrics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.LyricsLoadState
import com.example.verseflow.model.LyricsDisplayMode
import com.example.verseflow.model.LyricsSearchCandidate
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.ArtworkReactiveBackdrop
import com.example.verseflow.ui.components.EmptyStatePanel
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.LyricsLineChip
import com.example.verseflow.ui.components.PlaybackProgress
import com.example.verseflow.ui.components.VerseFilterChip
import com.example.verseflow.ui.car.rememberCarModeArtworkUri
import com.example.verseflow.ui.car.rememberIsCarLandscapeMode
import kotlinx.coroutines.launch

@Composable
fun LyricsScreen(
    uiState: VerseFlowUiState,
    onBack: () -> Unit,
    onModeSelected: (LyricsDisplayMode) -> Unit,
    onSeek: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onNowPlayingRequested: () -> Unit,
    onManualSearchRequested: () -> Unit,
    onManualSearchDismissed: () -> Unit,
    onManualSearchTitleChange: (String) -> Unit,
    onManualSearchArtistChange: (String) -> Unit,
    onManualSearchExecute: () -> Unit,
    onManualCandidateSelected: (LyricsSearchCandidate) -> Unit,
) {
    val song = uiState.playback.currentSong
    if (song == null) {
        EmptyStatePanel(
            title = "No lyrics yet",
            body = "Play a song to open the live lyric experience.",
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            shape = RectangleShape,
        )
        return
    }

    if (uiState.manualLyricsSearch.isVisible) {
        ManualLyricsSearchSheet(
            searchState = uiState.manualLyricsSearch,
            onDismiss = onManualSearchDismissed,
            onTitleChange = onManualSearchTitleChange,
            onArtistChange = onManualSearchArtistChange,
            onSearch = onManualSearchExecute,
            onCandidateSelected = onManualCandidateSelected,
        )
    }

    val syncedLyrics = song.lyrics
    val plainLyrics = song.plainLyrics.ifEmpty { syncedLyrics.map { it.text } }
    val lyricsStatus = uiState.lyricsStatusBySongId[song.id] ?: LyricsLoadState.Idle
    val activeIndex = syncedLyrics.indexOfLast { it.timestampMs <= uiState.playback.positionMs }.coerceAtLeast(0)
    val activeLine = syncedLyrics.getOrNull(activeIndex)?.text
    val isCarLandscapeMode = rememberIsCarLandscapeMode()
    val carArtworkUri = rememberCarModeArtworkUri(uiState.profile.settings.useTestArtwork)
    val effectiveArtworkUri = carArtworkUri ?: song.artworkUri
    val effectiveFallbackMediaUri = if (carArtworkUri != null) null else song.mediaUri
    val listState = rememberLazyListState()
    val showingSyncedLyrics = uiState.playback.lyricsDisplayMode == LyricsDisplayMode.Synced && syncedLyrics.isNotEmpty()
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    var followLiveLyrics by rememberSaveable(song.id, uiState.playback.lyricsDisplayMode) { mutableStateOf(true) }
    var autoScrolling by rememberSaveable(song.id, uiState.playback.lyricsDisplayMode) { mutableStateOf(false) }

    DisposableEffect(view, song.id) {
        val previousKeepScreenOn = view.keepScreenOn
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = previousKeepScreenOn
        }
    }

    LaunchedEffect(song.id, uiState.playback.lyricsDisplayMode) {
        followLiveLyrics = true
        autoScrolling = true
        listState.scrollToItem(0)
        autoScrolling = false
    }

    LaunchedEffect(listState.isScrollInProgress, showingSyncedLyrics, song.id) {
        if (showingSyncedLyrics && listState.isScrollInProgress && !autoScrolling) {
            followLiveLyrics = false
        }
    }

    LaunchedEffect(activeIndex, followLiveLyrics, uiState.playback.lyricsDisplayMode, song.id) {
        if (showingSyncedLyrics && followLiveLyrics) {
            autoScrolling = true
            listState.animateScrollToItem((activeIndex - 2).coerceAtLeast(0))
            autoScrolling = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(song.id) {
                var drag = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (drag > 120f) onNowPlayingRequested()
                        drag = 0f
                    },
                    onHorizontalDrag = { _, amount -> drag += amount },
                )
            },
    ) {
        ArtworkReactiveBackdrop(
            palette = song.palette,
            artworkUri = effectiveArtworkUri,
            fallbackMediaUri = effectiveFallbackMediaUri,
            modifier = Modifier.fillMaxSize(),
        )
        if (isCarLandscapeMode) {
            CarLyricsLayout(
                uiState = uiState,
                songTitle = song.title,
                artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                artworkUriOverride = carArtworkUri,
                activeLine = activeLine,
                plainLyrics = plainLyrics,
                lyricsStatus = lyricsStatus,
                onBack = onBack,
                onSeek = onSeek,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onModeSelected = onModeSelected,
                onManualSearchRequested = onManualSearchRequested,
            )
            return
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlowIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                )
                Text(
                    text = "Real-Time Lyrics",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AlbumArtwork(
                    title = song.title,
                    subtitle = uiState.artistsById[song.artistId]?.name.orEmpty(),
                    palette = song.palette,
                    artworkUri = song.artworkUri,
                    fallbackMediaUri = song.mediaUri,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(52.dp),
                    shape = RectangleShape,
                )
            }
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RectangleShape,
                surfaceAlpha = 0.90f,
                surfaceVariantAlpha = 0.84f,
                borderAlpha = 0.14f,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                )
                                Text(
                                    text = uiState.artistsById[song.artistId]?.name.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            song.lyricsAttribution?.let { attribution ->
                                Text(
                                    text = attribution,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            LyricsDisplayMode.entries.forEach { mode ->
                                VerseFilterChip(
                                    label = mode.label,
                                    selected = mode == uiState.playback.lyricsDisplayMode,
                                    onClick = { onModeSelected(mode) },
                                )
                            }
                            VerseFilterChip(
                                label = "Search lyrics",
                                selected = uiState.manualLyricsSearch.isVisible,
                                onClick = onManualSearchRequested,
                            )
                            if (showingSyncedLyrics && !followLiveLyrics) {
                                VerseFilterChip(
                                    label = "Jump live",
                                    selected = false,
                                    onClick = {
                                        followLiveLyrics = true
                                        coroutineScope.launch {
                                            autoScrolling = true
                                            listState.animateScrollToItem((activeIndex - 2).coerceAtLeast(0))
                                            autoScrolling = false
                                        }
                                    },
                                )
                            }
                        }
                        if (uiState.playback.lyricsDisplayMode == LyricsDisplayMode.Synced && syncedLyrics.isEmpty() && plainLyrics.isNotEmpty()) {
                            Text(
                                text = "Only plain lyrics were found for this track, so timing is unavailable.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.30f),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        if (lyricsStatus == LyricsLoadState.Loading && syncedLyrics.isEmpty() && plainLyrics.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    text = "Fetching live lyrics for this song...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 18.dp),
                                )
                                Text(
                                    text = "VerseFlow is matching this local file with online lyric timing.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }
                        } else if (showingSyncedLyrics) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 48.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                itemsIndexed(syncedLyrics, key = { _, item -> item.timestampMs }) { index, line ->
                                    LyricsLineChip(
                                        text = line.text,
                                        active = index == activeIndex,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RectangleShape,
                                        showContainer = false,
                                    )
                                }
                            }
                        } else if (plainLyrics.isNotEmpty()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 40.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                            ) {
                                itemsIndexed(plainLyrics, key = { index, line -> "$index-$line" }) { _, line ->
                                    LyricsLineChip(
                                        text = line,
                                        active = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RectangleShape,
                                        showContainer = false,
                                    )
                                }
                            }
                        } else {
                            EmptyStatePanel(
                                title = if (lyricsStatus == LyricsLoadState.Unavailable) {
                                    "No lyrics found"
                                } else {
                                    "No synced lyrics available"
                                },
                                body = if (lyricsStatus == LyricsLoadState.Unavailable) {
                                    "VerseFlow couldn't find a reliable lyrics match for this track from the current lyric sources."
                                } else {
                                    "This local track can play from your device, but VerseFlow doesn't have timed lyric data for it yet."
                                },
                                modifier = Modifier.fillMaxSize(),
                                shape = RectangleShape,
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
            )
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp),
                shape = RectangleShape,
                surfaceAlpha = 0.94f,
                surfaceVariantAlpha = 0.88f,
                borderAlpha = 0.16f,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    PlaybackProgress(
                        positionMs = uiState.playback.positionMs,
                        durationMs = song.durationMs,
                        onSeek = onSeek,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            GlowIconButton(
                                icon = Icons.Rounded.SkipPrevious,
                                contentDescription = "Previous",
                                onClick = onPrevious,
                            )
                            GlowIconButton(
                                icon = if (uiState.playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "Play pause",
                                onClick = onPlayPause,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary,
                            )
                            GlowIconButton(
                                icon = Icons.Rounded.SkipNext,
                                contentDescription = "Next",
                                onClick = onNext,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarLyricsLayout(
    uiState: VerseFlowUiState,
    songTitle: String,
    artistName: String,
    artworkUriOverride: String?,
    activeLine: String?,
    plainLyrics: List<String>,
    lyricsStatus: LyricsLoadState,
    onBack: () -> Unit,
    onSeek: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onModeSelected: (LyricsDisplayMode) -> Unit,
    onManualSearchRequested: () -> Unit,
) {
    val currentSong = uiState.playback.currentSong ?: return
    val lineToShow = activeLine
        ?: plainLyrics.firstOrNull()
        ?: if (lyricsStatus == LyricsLoadState.Unavailable) {
            "No lyrics found for this song yet."
        } else {
            "Lyrics will appear here once VerseFlow matches this track."
        }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight()
                .padding(horizontal = 26.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlowIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )
                    Text(
                        text = "Lyrics",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = songTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = artistName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = lineToShow,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    LyricsDisplayMode.entries.forEach { mode ->
                        VerseFilterChip(
                            label = mode.label,
                            selected = mode == uiState.playback.lyricsDisplayMode,
                            onClick = { onModeSelected(mode) },
                        )
                    }
                    VerseFilterChip(
                        label = "Search lyrics",
                        selected = false,
                        onClick = onManualSearchRequested,
                    )
                }
                PlaybackProgress(
                    positionMs = uiState.playback.positionMs,
                    durationMs = currentSong.durationMs,
                    onSeek = onSeek,
                )
            }
        }
        GlassPanel(
            modifier = Modifier
                .weight(0.38f)
                .fillMaxHeight(),
            shape = RectangleShape,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    AlbumArtwork(
                        title = songTitle,
                        subtitle = artistName,
                        palette = currentSong.palette,
                        artworkUri = artworkUriOverride ?: currentSong.artworkUri,
                        fallbackMediaUri = if (artworkUriOverride != null) null else currentSong.mediaUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        shape = RectangleShape,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlowIconButton(
                        icon = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        onClick = onPrevious,
                    )
                    GlowIconButton(
                        icon = if (uiState.playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play pause",
                        onClick = onPlayPause,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                    GlowIconButton(
                        icon = Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        onClick = onNext,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualLyricsSearchSheet(
    searchState: com.example.verseflow.model.ManualLyricsSearchUiState,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onSearch: () -> Unit,
    onCandidateSelected: (LyricsSearchCandidate) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Search Lyrics Manually",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                TextButton(onClick = onSearch) {
                    Text("Search")
                }
            }
            Text(
                text = "Edit the song title or artist, then pick the exact lyric match you want VerseFlow to use.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextField(
                value = searchState.queryTitle,
                onValueChange = onTitleChange,
                singleLine = true,
                label = { Text("Song title") },
                modifier = Modifier.fillMaxWidth(),
            )
            TextField(
                value = searchState.queryArtist,
                onValueChange = onArtistChange,
                singleLine = true,
                label = { Text("Artist") },
                modifier = Modifier.fillMaxWidth(),
            )
            when {
                searchState.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                        Text(
                            text = "Searching lyric sources...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                searchState.results.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = searchState.results,
                            key = LyricsSearchCandidate::id,
                        ) { candidate ->
                            ManualLyricsCandidateCard(
                                candidate = candidate,
                                onClick = { onCandidateSelected(candidate) },
                            )
                        }
                    }
                }

                searchState.hasSearched -> {
                    GlassPanel(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        surfaceAlpha = 0.46f,
                        surfaceVariantAlpha = 0.12f,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "No strong matches found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Try simplifying the title, removing version labels, or searching with a different artist spelling.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManualLyricsCandidateCard(
    candidate: LyricsSearchCandidate,
    onClick: () -> Unit,
) {
    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RectangleShape,
        surfaceAlpha = 0.46f,
        surfaceVariantAlpha = 0.12f,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = candidate.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = candidate.artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = if (candidate.hasSyncedLyrics) "SYNCED" else "PLAIN",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (candidate.hasSyncedLyrics) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            Text(
                text = listOfNotNull(candidate.albumTitle, candidate.sourceLabel).joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            TextButton(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Use this lyric match")
            }
        }
    }
}
