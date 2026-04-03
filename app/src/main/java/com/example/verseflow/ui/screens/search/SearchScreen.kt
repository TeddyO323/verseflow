package com.example.verseflow.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.EmptyStatePanel
import com.example.verseflow.ui.components.PlaylistCard
import com.example.verseflow.ui.components.SectionHeader
import com.example.verseflow.ui.components.SongListItem
import com.example.verseflow.ui.components.SongOverflowMenu
import com.example.verseflow.ui.components.VerseFilterChip
import com.example.verseflow.ui.components.VerseFlowSearchBar
import com.example.verseflow.ui.components.formatDuration
import com.example.verseflow.ui.components.AlbumCard
import com.example.verseflow.ui.components.ArtistCard
import com.example.verseflow.ui.components.GlowIconButton

@Composable
fun SearchScreen(
    uiState: VerseFlowUiState,
    onOpenDrawer: () -> Unit,
    onQueryChange: (String) -> Unit,
    onUseRecentSearch: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddSongToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddSongToPlayQueue: (songId: String) -> Unit,
    onToggleSongLike: (songId: String) -> Unit,
    onDeleteFromStorage: (songId: String) -> Unit,
    onEditMusicInfo: (songId: String) -> Unit,
) {
    val query = uiState.searchQuery.trim()
    val songs = uiState.songs.filter {
        query.isBlank() || it.title.contains(query, ignoreCase = true) ||
            uiState.artistsById[it.artistId]?.name.orEmpty().contains(query, ignoreCase = true)
    }
    val albums = uiState.albums.filter {
        query.isBlank() || it.title.contains(query, ignoreCase = true) ||
            uiState.artistsById[it.artistId]?.name.orEmpty().contains(query, ignoreCase = true)
    }
    val artists = uiState.artists.filter {
        query.isBlank() || it.name.contains(query, ignoreCase = true) || it.genre.contains(query, ignoreCase = true)
    }
    val playlists = uiState.playlists.filter {
        query.isBlank() || it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 150.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            text = "Search",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Query songs, artists, moods, and future playlists.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                VerseFlowSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onQueryChange,
                    placeholder = "Search the signal",
                )
            }
        }

        if (query.isBlank()) {
            item {
                SectionHeader(
                    title = "Recent Searches",
                    subtitle = "Jump back into your latest discoveries",
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.recentSearches.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            rowItems.forEach { search ->
                                VerseFilterChip(
                                    label = search,
                                    selected = false,
                                    onClick = { onUseRecentSearch(search) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
            item {
                SectionHeader(
                    title = "Trending Categories",
                    subtitle = "Curated modes for every kind of motion",
                )
            }
            items(uiState.trendingCategories, key = { it }) { category ->
                VerseFilterChip(
                    label = category,
                    selected = false,
                    onClick = { onUseRecentSearch(category) },
                )
            }
        } else if (songs.isEmpty() && albums.isEmpty() && artists.isEmpty() && playlists.isEmpty()) {
            item {
                EmptyStatePanel(
                    title = "No results for \"$query\"",
                    body = "Try a broader genre, artist, or playlist mood to widen the orbit.",
                )
            }
        } else {
            if (songs.isNotEmpty()) {
                item {
                    SectionHeader(title = "Songs")
                }
                items(songs.take(5), key = { it.id }) { song ->
                    SongListItem(
                        song = song,
                        artistName = uiState.artistsById[song.artistId]?.name.orEmpty(),
                        supportingText = "${song.mood} • ${formatDuration(song.durationMs)}",
                        onClick = { onSongClick(song) },
                        surfaceAlpha = 0f,
                        surfaceVariantAlpha = 0f,
                        borderAlpha = 0f,
                        shadowElevation = 0.dp,
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
            if (albums.isNotEmpty()) {
                item {
                    SectionHeader(title = "Albums")
                }
                items(albums.take(3), key = { it.id }) { album ->
                    AlbumCard(
                        album = album,
                        artistName = uiState.artistsById[album.artistId]?.name.orEmpty(),
                        onClick = { onAlbumClick(album) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (artists.isNotEmpty()) {
                item {
                    SectionHeader(title = "Artists")
                }
                items(artists.take(4), key = { it.id }) { artist ->
                    ArtistCard(
                        artist = artist,
                        onClick = { onArtistClick(artist) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (playlists.isNotEmpty()) {
                item {
                    SectionHeader(title = "Playlists")
                }
                items(playlists.take(3), key = { it.id }) { playlist ->
                    PlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
