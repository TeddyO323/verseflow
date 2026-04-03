package com.example.verseflow.ui.screens.artist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Song
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.AlbumCard
import com.example.verseflow.ui.components.ArtistCard
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.SectionHeader
import com.example.verseflow.ui.components.SongListItem
import com.example.verseflow.ui.components.SongOverflowMenu
import com.example.verseflow.ui.components.formatDuration

@Composable
fun ArtistDetailScreen(
    artist: Artist,
    uiState: VerseFlowUiState,
    onBack: () -> Unit,
    onPlayTopTracks: () -> Unit,
    onAlbumClick: (Album) -> Unit,
    onSongClick: (Song) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddSongToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddSongToPlayQueue: (songId: String) -> Unit,
    onToggleSongLike: (songId: String) -> Unit,
    onDeleteFromStorage: (songId: String) -> Unit,
    onEditMusicInfo: (songId: String) -> Unit,
) {
    val albums = artist.albumIds.mapNotNull(uiState.albumsById::get)
    val topTracks = artist.topTrackIds.mapNotNull(uiState.songsById::get)
    val relatedArtists = artist.relatedArtistIds.mapNotNull(uiState.artistsById::get)
    val heroArtworkUri = albums.firstNotNullOfOrNull(Album::artworkUri)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
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
                    text = "Artist",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                GlowIconButton(
                    icon = Icons.Rounded.PlayArrow,
                    contentDescription = "Play top tracks",
                    onClick = onPlayTopTracks,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    AlbumArtwork(
                        title = artist.name,
                        subtitle = artist.genre,
                        palette = artist.heroPalette,
                        artworkUri = heroArtworkUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .height(260.dp),
                    )
                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = artist.monthlyListeners,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = artist.bio,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item {
            SectionHeader(
                title = "Top Tracks",
                subtitle = "Most replayed in the VerseFlow mock universe",
            )
        }
        items(topTracks, key = { it.id }) { track ->
            SongListItem(
                song = track,
                artistName = artist.name,
                supportingText = formatDuration(track.durationMs),
                onClick = { onSongClick(track) },
                surfaceAlpha = 0f,
                surfaceVariantAlpha = 0f,
                borderAlpha = 0f,
                shadowElevation = 0.dp,
                trailingContent = {
                    SongOverflowMenu(
                        song = track,
                        artistName = artist.name,
                        albumTitle = uiState.albumsById[track.albumId]?.title.orEmpty(),
                        playlists = uiState.playlists,
                        isFavorite = track.id in uiState.playback.likedSongIds,
                        onRemoveFromVerseFlow = onRemoveFromVerseFlow,
                        onAddToPlaylist = onAddSongToPlaylist,
                        onAddToPlayQueue = onAddSongToPlayQueue,
                        onToggleFavorite = onToggleSongLike,
                        onOpenArtist = { onArtistClick(artist) },
                        onOpenAlbum = {
                            uiState.albumsById[track.albumId]?.let(onAlbumClick)
                        },
                        onDeleteFromStorage = onDeleteFromStorage,
                        onEditMusicInfo = onEditMusicInfo,
                    )
                },
            )
        }
        item {
            SectionHeader(
                title = "Albums",
                subtitle = "Immersive releases with dynamic artwork",
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(albums, key = { it.id }) { album ->
                    AlbumCard(
                        album = album,
                        artistName = artist.name,
                        onClick = { onAlbumClick(album) },
                    )
                }
            }
        }
        item {
            SectionHeader(
                title = "About",
                subtitle = "Narrative, influence, and sonic identity",
            )
        }
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = artist.bio,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp),
                )
            }
        }
        item {
            SectionHeader(
                title = "Related Artists",
                subtitle = "More creators sharing the same future-facing energy",
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(relatedArtists, key = { it.id }) { relatedArtist ->
                    ArtistCard(
                        artist = relatedArtist,
                        onClick = { onArtistClick(relatedArtist) },
                    )
                }
            }
        }
    }
}
