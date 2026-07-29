package com.example.verseflow.ui.screens.lyrics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.LyricsLoadState
import com.example.verseflow.model.LyricsDisplayMode
import com.example.verseflow.model.LyricsSearchCandidate
import com.example.verseflow.model.LyricLine
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AuroraBackdrop
import com.example.verseflow.ui.components.EmptyStatePanel
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.LyricsLineChip
import com.example.verseflow.ui.car.rememberCarModeArtworkUri
import com.example.verseflow.ui.car.rememberIsCarLandscapeMode

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
    val isCarLandscapeMode = rememberIsCarLandscapeMode()
    val carArtworkUri = rememberCarModeArtworkUri(uiState.profile.settings.useTestArtwork)
    val showingSyncedLyrics = uiState.playback.lyricsDisplayMode == LyricsDisplayMode.Synced && syncedLyrics.isNotEmpty()
    val view = LocalView.current
    val visibleSyncedLines = visibleLyricLines(
        syncedLyrics = syncedLyrics,
        activeIndex = activeIndex,
    )

    DisposableEffect(view, song.id) {
        val previousKeepScreenOn = view.keepScreenOn
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = previousKeepScreenOn
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
        AuroraBackdrop(
            palette = song.palette,
            modifier = Modifier.fillMaxSize(),
        )
        if (isCarLandscapeMode) {
            CarLyricsLayout(
                uiState = uiState,
                songTitle = song.title,
                artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                artworkUriOverride = carArtworkUri,
                activeIndex = activeIndex,
                visibleSyncedLines = visibleSyncedLines,
                plainLyrics = plainLyrics,
                lyricsStatus = lyricsStatus,
                onBack = onBack,
                onSeek = onSeek,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onManualSearchRequested = onManualSearchRequested,
            )
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlowIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                PhoneLyricsContent(
                    showingSyncedLyrics = showingSyncedLyrics,
                    visibleSyncedLines = visibleSyncedLines,
                    activeIndex = activeIndex,
                    plainLyrics = plainLyrics,
                    lyricsStatus = lyricsStatus,
                    onManualSearchRequested = onManualSearchRequested,
                )
            }

            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RectangleShape,
                surfaceAlpha = 0.94f,
                surfaceVariantAlpha = 0.88f,
                borderAlpha = 0.16f,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        GlowIconButton(
                            icon = Icons.Rounded.SkipPrevious,
                            contentDescription = "Previous",
                            onClick = onPrevious,
                            modifier = Modifier.size(36.dp),
                        )
                        GlowIconButton(
                            icon = if (uiState.playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play / Pause",
                            onClick = onPlayPause,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp),
                        )
                        GlowIconButton(
                            icon = Icons.Rounded.SkipNext,
                            contentDescription = "Next",
                            onClick = onNext,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    LinearProgressIndicator(
                        progress = {
                            val d = song.durationMs.coerceAtLeast(1L)
                            (uiState.playback.positionMs.toFloat() / d.toFloat()).coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f),
                    )
                }
            }
        }
    }
}

private fun visibleLyricLines(
    syncedLyrics: List<LyricLine>,
    activeIndex: Int,
): List<IndexedValue<String>> {
    if (syncedLyrics.isEmpty()) return emptyList()
    val safeActiveIndex = activeIndex.coerceIn(0, syncedLyrics.lastIndex)
    val startIndex = (safeActiveIndex - 1).coerceAtLeast(0)
    return syncedLyrics
        .drop(startIndex)
        .take(3)
        .mapIndexed { offset, line -> IndexedValue(startIndex + offset, line.text) }
}

@Composable
private fun PhoneLyricsContent(
    showingSyncedLyrics: Boolean,
    visibleSyncedLines: List<IndexedValue<String>>,
    activeIndex: Int,
    plainLyrics: List<String>,
    lyricsStatus: LyricsLoadState,
    onManualSearchRequested: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            lyricsStatus == LyricsLoadState.Loading && visibleSyncedLines.isEmpty() && plainLyrics.isEmpty() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    Text(
                        text = "Finding lyrics...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            showingSyncedLyrics && visibleSyncedLines.isNotEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(26.dp),
                ) {
                    visibleSyncedLines.forEach { line ->
                        LyricsLineChip(
                            text = line.value,
                            active = line.index == activeIndex,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            showContainer = false,
                        )
                    }
                }
            }

            plainLyrics.isNotEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(26.dp),
                ) {
                    plainLyrics.take(3).forEachIndexed { index, line ->
                        LyricsLineChip(
                            text = line,
                            active = index == 0,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            showContainer = false,
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Text(
                        text = if (lyricsStatus == LyricsLoadState.Unavailable) {
                            "No lyrics found"
                        } else {
                            "Lyrics will appear here"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    TextButton(onClick = onManualSearchRequested) {
                        Text("Search lyrics")
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
    activeIndex: Int,
    visibleSyncedLines: List<IndexedValue<String>>,
    plainLyrics: List<String>,
    lyricsStatus: LyricsLoadState,
    onBack: () -> Unit,
    onSeek: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onManualSearchRequested: () -> Unit,
) {
    val currentSong = uiState.playback.currentSong ?: return
    val placeholderLine = plainLyrics.firstOrNull()
        ?: if (lyricsStatus == LyricsLoadState.Unavailable) {
            "No lyrics found for this song yet."
        } else {
            "Lyrics will appear here once VerseFlow matches this track."
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            GlowIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (visibleSyncedLines.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    visibleSyncedLines.forEach { line ->
                        LyricsLineChip(
                            text = line.value,
                            active = line.index == activeIndex,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            showContainer = false,
                        )
                    }
                }
            } else {
                Text(
                    text = placeholderLine,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlowIconButton(
                    icon = Icons.Rounded.SkipPrevious,
                    contentDescription = "Previous",
                    onClick = onPrevious,
                )
                Spacer(modifier = Modifier.width(18.dp))
                GlowIconButton(
                    icon = if (uiState.playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play / Pause",
                    onClick = onPlayPause,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(18.dp))
                GlowIconButton(
                    icon = Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    onClick = onNext,
                )
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
