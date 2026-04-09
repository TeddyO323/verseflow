package com.example.verseflow.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.PlayHistoryEntry
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.EmptyStatePanel
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.SectionHeader
import com.example.verseflow.ui.components.formatDuration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PlayHistoryScreen(
    uiState: VerseFlowUiState,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onSongClick: (Song) -> Unit,
    onClearHistory: () -> Unit,
) {
    val groupedHistory = remember(uiState.playHistoryEntries) { uiState.playHistoryEntries.groupedByDay() }
    val historyCards = remember(uiState.playHistoryEntries) { uiState.playHistoryEntries.toSummaryCards() }
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlowIconButton(
                icon = Icons.Rounded.History,
                contentDescription = "Play history",
                onClick = onOpenDrawer,
            )
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Play History",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Songs you actually listened to, grouped by day.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (uiState.playHistoryEntries.isNotEmpty()) {
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Clear play history",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (uiState.playHistoryEntries.isEmpty()) {
                item {
                    EmptyStatePanel(
                        title = "No listening history yet",
                        body = "Play a song in VerseFlow and it will start showing up here.",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        shape = RectangleShape,
                    )
                }
            } else {
                item {
                    HistorySummaryGrid(
                        cards = historyCards,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
                items(groupedHistory, key = { it.date.toEpochDay() }) { section ->
                    HistoryDayCard(
                        section = section,
                        songsById = uiState.songsById,
                        onSongClick = onSongClick,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear play history") },
            text = { Text("Delete the current listening history from VerseFlow?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        onClearHistory()
                    },
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun HistorySummaryGrid(
    cards: List<HistorySummaryCard>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        cards.chunked(2).forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowCards.forEach { card ->
                    GlassPanel(
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape,
                        surfaceAlpha = 0.48f,
                        surfaceVariantAlpha = 0.12f,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(card.title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(card.value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                            Text(card.supporting, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                if (rowCards.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HistoryDayCard(
    section: HistoryDaySection,
    songsById: Map<String, Song>,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalDurationMs = section.entries.sumOf(PlayHistoryEntry::listenedMs)
    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        surfaceAlpha = 0.48f,
        surfaceVariantAlpha = 0.12f,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionHeader(
                title = formatHistoryDay(section.date),
                subtitle = "${section.entries.size} plays • ${formatDuration(totalDurationMs)} listened",
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                section.entries.forEach { entry ->
                    HistoryTrackRow(
                        entry = entry,
                        liveSong = songsById[entry.songId],
                        onSongClick = onSongClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryTrackRow(
    entry: PlayHistoryEntry,
    liveSong: Song?,
    onSongClick: (Song) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = liveSong != null) { liveSong?.let(onSongClick) }
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AlbumArtwork(
            title = entry.title,
            subtitle = entry.artistName,
            palette = liveSong?.palette ?: fallbackHistoryPalette,
            artworkUri = entry.artworkUri,
            fallbackMediaUri = entry.fallbackMediaUri,
            modifier = Modifier.size(54.dp),
            shape = RectangleShape,
            borderColor = Color.Transparent,
            showOverlay = false,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(entry.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${entry.artistName} • ${entry.albumTitle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(
            text = formatDuration(entry.listenedMs),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

private data class HistoryDaySection(
    val date: LocalDate,
    val entries: List<PlayHistoryEntry>,
)

private data class HistorySummaryCard(
    val title: String,
    val value: String,
    val supporting: String,
)

private fun List<PlayHistoryEntry>.groupedByDay(): List<HistoryDaySection> =
    groupBy { Instant.ofEpochMilli(it.playedAtMs).atZone(ZoneId.systemDefault()).toLocalDate() }
        .entries
        .sortedByDescending { it.key }
        .map { (date, entries) ->
            HistoryDaySection(
                date = date,
                entries = entries.sortedByDescending(PlayHistoryEntry::playedAtMs),
            )
        }

private fun List<PlayHistoryEntry>.toSummaryCards(): List<HistorySummaryCard> {
    val totalPlays = size
    val uniqueSongs = map(PlayHistoryEntry::songId).distinct().size
    val uniqueAlbums = map { "${it.artistName}::${it.albumTitle}" }.distinct().size
    val totalDurationMs = sumOf(PlayHistoryEntry::listenedMs)
    return listOf(
        HistorySummaryCard("Song plays", totalPlays.toString(), "$uniqueSongs unique songs"),
        HistorySummaryCard("Hours played", formatDuration(totalDurationMs), "Total listening time"),
        HistorySummaryCard("Albums played", uniqueAlbums.toString(), "Distinct albums heard"),
        HistorySummaryCard("Artists played", map(PlayHistoryEntry::artistName).distinct().size.toString(), "Distinct artists heard"),
    )
}

private fun formatHistoryDay(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, d MMM"))
    }
}

private val fallbackHistoryPalette = com.example.verseflow.model.AccentPalette(
    background = Color(0xFF080B11),
    primary = Color(0xFF6A8CFF),
    secondary = Color(0xFF8AF5FF),
    tertiary = Color(0xFFB7C5FF),
    glow = Color(0xFF9FAFFF),
)
