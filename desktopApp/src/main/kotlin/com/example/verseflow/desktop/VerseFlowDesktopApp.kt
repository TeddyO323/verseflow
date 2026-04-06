package com.example.verseflow.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.ViewList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.awt.datatransfer.DataFlavor
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.swing.BorderFactory
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerMoveFilter
import kotlin.math.roundToLong
import kotlin.random.Random

private val InkBlack = Color(0xFF040611)
private val DeepSpace = Color(0xFF070B16)
private val VerseBlue = Color(0xFF0000FF)
private val NebulaBlue = Color(0xFF6B88FF)
private val AuroraCyan = Color(0xFF66F2FF)
private val FrostWhite = Color(0xFFF5F7FF)
private val MutedLavender = Color(0xFFB5BDD6)
private val SurfaceGlass = Color(0xFF0B0E17)

private val VerseFlowDesktopColors = darkColorScheme(
    primary = VerseBlue,
    secondary = AuroraCyan,
    tertiary = NebulaBlue,
    background = InkBlack,
    surface = DeepSpace,
    surfaceVariant = Color(0xFF12172B),
    onPrimary = FrostWhite,
    onBackground = FrostWhite,
    onSurface = FrostWhite,
    onSurfaceVariant = MutedLavender,
)

@Suppress("UnusedParameter")
private fun RoundedCornerShape(radius: Dp): Shape = RectangleShape

private enum class DesktopSection(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val showInSidebar: Boolean = true,
) {
    Home("Home", Icons.Rounded.Home),
    Library("Library", Icons.Rounded.LibraryMusic, showInSidebar = false),
    Search("Search", Icons.Rounded.Search, showInSidebar = false),
    PlayQueue("Play Queue", Icons.AutoMirrored.Rounded.QueueMusic),
    PlayHistory("Play History", Icons.Rounded.History),
    NowPlaying("Now Playing", Icons.Rounded.Album, showInSidebar = false),
    Lyrics("Lyrics", Icons.Rounded.Lyrics, showInSidebar = false),
    Settings("Settings", Icons.Rounded.Settings, showInSidebar = false),
    PlaylistDetail("Playlist", Icons.AutoMirrored.Rounded.QueueMusic, showInSidebar = false),
    AlbumDetail("Album", Icons.Rounded.Album, showInSidebar = false),
    ArtistDetail("Artist", Icons.Rounded.LibraryMusic, showInSidebar = false),
}

private enum class DesktopLibraryTab(val title: String) {
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists"),
    Favourites("Favourites"),
    Playlists("Playlists"),
    Genres("Genres"),
}

private enum class DesktopCollectionViewMode {
    List,
    Grid,
}

private enum class DesktopAlbumSortMode(val label: String) {
    DateAdded("Date added"),
    Alphabetical("Alphabetical"),
    MostSongs("Most songs"),
}

private enum class DesktopArtistDetailTab(val title: String) {
    TopTracks("Top tracks"),
    Albums("Albums"),
    Features("Features"),
    About("About"),
}

private data class DesktopArtistConnectionSummary(
    val name: String,
    val sharedTrackCount: Int,
    val sharedAlbumCount: Int,
    val genres: List<String>,
    val palette: List<Color>,
)

private data class DesktopPlaylistSummary(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val supporting: String,
    val tracks: List<DesktopTrack>,
    val palette: List<Color>,
    val artworkBytes: ByteArray? = null,
    val isUserCreated: Boolean = false,
    val isSystemPlaylist: Boolean = false,
)

private data class DesktopGenreSummary(
    val title: String,
    val trackCount: Int,
    val palette: List<Color>,
    val artworkBytes: ByteArray? = null,
)

private data class DesktopFocusPanel(
    val title: String,
    val subtitle: String,
    val body: String,
    val palette: List<Color>,
    val artworkBytes: ByteArray? = null,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
)

private data class DesktopSidebarItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit,
)

private data class DesktopHistorySummaryCard(
    val title: String,
    val value: String,
    val supporting: String,
)

private data class DesktopMoodRailItem(
    val title: String,
    val subtitle: String,
    val supporting: String,
    val tracks: List<DesktopTrack>,
    val palette: List<Color>,
    val artworkBytes: ByteArray? = null,
)

private data class DesktopListeningScene(
    val title: String,
    val subtitle: String,
    val supporting: String,
    val tracks: List<DesktopTrack>,
    val palette: List<Color>,
    val artworkBytes: ByteArray? = null,
)

private data class DesktopPlayHistoryDaySection(
    val date: LocalDate,
    val entries: List<DesktopPlayHistoryEntry>,
)

private data class DesktopTrackMenuModel(
    val favoriteTrackPaths: Set<String>,
    val userPlaylists: List<DesktopPlaylistSummary>,
    val onAddToQueue: (DesktopTrack) -> Unit,
    val onToggleFavorite: (DesktopTrack) -> Unit,
    val onAddToPlaylist: (String, DesktopTrack) -> Unit,
    val onCreatePlaylistWithTrack: (DesktopTrack) -> Unit,
    val onOpenArtist: (DesktopTrack) -> Unit,
    val onOpenAlbum: (DesktopTrack) -> Unit,
    val onHideTrack: (DesktopTrack) -> Unit,
    val onDeleteTrack: (DesktopTrack) -> Unit,
    val onEditTrack: (DesktopTrack) -> Unit,
)

private fun displayGenreLabel(genre: String): String =
    genre
        .trim()
        .takeIf(String::isNotEmpty)
        ?.takeUnless { it.equals("Unclassified", ignoreCase = true) }
        ?: "No genre tag"

private fun displayGenreLabels(genres: List<String>): String =
    genres
        .map(::displayGenreLabel)
        .distinct()
        .joinToString(" • ")
        .ifBlank { "No genre tag" }

@Composable
fun VerseFlowDesktopApp() {
    val appStore = remember { DesktopAppStore() }
    val libraryStore = remember { DesktopLibraryStore() }
    val playlistStore = remember { DesktopPlaylistStore() }
    val desktopThemes = remember {
        listOf(
            DesktopThemePreset("Nebula Dark", "Original cinematic dark theme"),
            DesktopThemePreset("Eclipse OLED", "High-contrast near-black desktop mode"),
            DesktopThemePreset("Aurora Glow", "Richer accents for screenshots and motion"),
            DesktopThemePreset("Cobalt Luxe", "Blue-forward premium look for desktop"),
        )
    }
    val storedSettings = remember(desktopThemes) { appStore.loadSettings(desktopThemes.last().name) }
    val lyricsCacheStore = remember { DesktopLyricsCacheStore() }
    val coroutineScope = rememberCoroutineScope()
    val playbackController = remember { DesktopPlaybackController() }
    val playbackState by playbackController.state.collectAsState()
    var libraryState by remember {
        mutableStateOf(
            DesktopLibraryUiState(
                sourcePaths = libraryStore.loadLibraryPaths(),
            ),
        )
    }
    var section by remember { mutableStateOf(DesktopSection.Home) }
    var libraryTab by remember { mutableStateOf(DesktopLibraryTab.Songs) }
    var isSidebarCollapsed by remember { mutableStateOf(libraryStore.loadSidebarCollapsed()) }
    var selectedPlaylistId by remember { mutableStateOf<String?>(null) }
    var selectedAlbumKey by remember { mutableStateOf<String?>(null) }
    var selectedArtistName by remember { mutableStateOf<String?>(null) }
    var currentTrackId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var recentSearches by remember { mutableStateOf(appStore.loadRecentSearches()) }
    var artistSpotlightOrder by remember { mutableStateOf(appStore.loadArtistSpotlightOrder()) }
    var albumsViewMode by remember { mutableStateOf(DesktopCollectionViewMode.List) }
    var artistsViewMode by remember { mutableStateOf(DesktopCollectionViewMode.List) }
    var albumsSortMode by remember { mutableStateOf(DesktopAlbumSortMode.DateAdded) }
    var albumsGridScrolled by remember { mutableStateOf(false) }
    var homeScrolled by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf(storedSettings.displayName) }
    var selectedTheme by remember { mutableStateOf(storedSettings.selectedTheme) }
    var isShuffleEnabled by remember { mutableStateOf(storedSettings.isShuffleEnabled) }
    var isRepeatEnabled by remember { mutableStateOf(storedSettings.isRepeatEnabled) }
    var autoRescanEnabled by remember { mutableStateOf(storedSettings.autoRescanEnabled) }
    var musixmatchApiKey by remember { mutableStateOf(storedSettings.musixmatchApiKey) }
    var lyricsStatuses by remember { mutableStateOf<Map<String, DesktopLyricsLoadState>>(emptyMap()) }
    var playHistoryEntries by remember { mutableStateOf(appStore.loadPlayHistory()) }
    var recentTrackIds by remember { mutableStateOf(playHistoryEntries.toRecentTrackIds()) }
    var playCounts by remember { mutableStateOf(playHistoryEntries.toPlayCounts()) }
    var userPlaylists by remember { mutableStateOf(playlistStore.loadPlaylists()) }
    var favoriteTrackPaths by remember { mutableStateOf(libraryStore.loadFavoriteTrackPaths()) }
    var hiddenTrackPaths by remember { mutableStateOf(appStore.loadHiddenTrackPaths()) }
    var trackOverrides by remember { mutableStateOf(appStore.loadTrackOverrides()) }
    var artistProfileOverrides by remember { mutableStateOf(appStore.loadArtistProfileOverrides()) }
    var pendingPlaybackSession by remember { mutableStateOf(appStore.loadPlaybackSession()) }
    var queueTrackPaths by remember { mutableStateOf<List<String>>(emptyList()) }
    var queueLabel by remember { mutableStateOf("All Songs") }
    var activeHistoryTrackId by remember { mutableStateOf<String?>(null) }
    var activeHistoryTrackPath by remember { mutableStateOf<String?>(null) }
    var activeHistoryAccumulatedMs by remember { mutableStateOf(0L) }
    var activeHistoryLastPositionMs by remember { mutableStateOf(0L) }
    var editingTrack by remember { mutableStateOf<DesktopTrack?>(null) }
    var deletingTrack by remember { mutableStateOf<DesktopTrack?>(null) }
    var manualLyricsTrack by remember { mutableStateOf<DesktopTrack?>(null) }
    val lyricsRepository = remember { DesktopLyricsRepository(apiKeyProvider = { musixmatchApiKey }) }
    val tracks = libraryState.tracks
    val activeQueue = remember(queueTrackPaths, tracks) {
        val restoredQueue = queueTrackPaths
            .mapNotNull { path -> tracks.firstOrNull { it.path == path } }
            .distinctBy(DesktopTrack::id)
        when {
            restoredQueue.isNotEmpty() -> restoredQueue
            tracks.isNotEmpty() -> tracks
            else -> emptyList()
        }
    }
    val currentTrack = tracks.firstOrNull { it.id == currentTrackId } ?: tracks.firstOrNull()
    val currentDurationMs = playbackState.durationMs.takeIf { it > 0 } ?: currentTrack?.durationMs ?: 0L
    val allAlbums = remember(tracks) { summarizeAlbums(tracks) }
    val allArtists = remember(tracks, artistProfileOverrides) {
        summarizeArtists(tracks).map { artist ->
            artist.copy(
                artworkBytes = loadDesktopImageBytes(artistProfileOverrides[artist.name]?.photoPath),
            )
        }
    }
    val featuredAlbums = remember(allAlbums) { allAlbums.take(4) }
    val recentTracks = remember(recentTrackIds, tracks) {
        recentTrackIds.mapNotNull { id -> tracks.firstOrNull { it.id == id } }.take(8)
    }
    val trendingTracks = remember(tracks, playCounts) {
        if (tracks.isEmpty()) {
            emptyList()
        } else {
            tracks
                .sortedWith(
                    compareByDescending<DesktopTrack> { playCounts[it.id] ?: 0 }
                        .thenBy { it.title.lowercase() },
                )
                .take(8)
        }
    }
    val rankedArtists = remember(tracks, playCounts, allArtists) {
        if (playCounts.isEmpty()) {
            allArtists
        } else {
            allArtists.sortedWith(
                compareByDescending<DesktopArtistSummary> { artist ->
                    tracks.filter { artist.name in it.artistCredits }.sumOf { playCounts[it.id] ?: 0 }
                }.thenBy { it.name.lowercase() },
            )
        }
    }
    val featuredArtists = remember(rankedArtists, artistSpotlightOrder, allArtists) {
        val orderedArtists = artistSpotlightOrder
            .mapNotNull { artistName -> allArtists.firstOrNull { it.name == artistName } }
        (orderedArtists + rankedArtists.filterNot { ranked -> orderedArtists.any { it.name == ranked.name } })
            .take(6)
    }
    val smartPlaylists = remember(tracks, recentTrackIds, playCounts) {
        buildDesktopSmartPlaylists(
            tracks = tracks,
            recentTrackIds = recentTrackIds,
            playCounts = playCounts,
        )
    }
    val userPlaylistSummaries = remember(userPlaylists, tracks) {
        buildDesktopUserPlaylists(
            playlists = userPlaylists,
            tracks = tracks,
        )
    }
    val editablePlaylists = remember(userPlaylistSummaries) {
        userPlaylistSummaries.sortedBy { it.title.lowercase() }
    }
    val favoritesPlaylist = remember(favoriteTrackPaths, tracks) {
        buildDesktopFavoritesPlaylist(
            favoriteTrackPaths = favoriteTrackPaths,
            tracks = tracks,
        )
    }
    val allPlaylists = remember(favoritesPlaylist, userPlaylistSummaries, smartPlaylists) {
        listOf(favoritesPlaylist) + userPlaylistSummaries + smartPlaylists
    }
    val favoritePlaylists = remember(allPlaylists) { allPlaylists.take(4) }
    val featuredGenres = remember(tracks) { summarizeGenres(tracks).take(6) }
    val libraryGenres = remember(tracks) { summarizeDesktopGenres(tracks) }
    val selectedPlaylist = remember(selectedPlaylistId, allPlaylists) {
        allPlaylists.firstOrNull { it.id == selectedPlaylistId }
    }
    val selectedAlbum = remember(selectedAlbumKey, allAlbums) {
        allAlbums.firstOrNull { desktopAlbumKey(it.artist, it.title) == selectedAlbumKey }
    }
    val selectedArtist = remember(selectedArtistName, allArtists) {
        allArtists.firstOrNull { it.name == selectedArtistName }
    }
    val selectedArtistProfileOverride = remember(selectedArtist?.name, artistProfileOverrides) {
        selectedArtist?.name?.let { artistProfileOverrides[it] }
    }
    val relatedArtists = remember(selectedArtist?.name, allArtists, tracks) {
        val activeArtist = selectedArtist ?: return@remember emptyList()
        val activeTracks = tracks.filter { activeArtist.name in it.artistCredits }
        val activeGenres = activeTracks.map { it.genre.lowercase() }.toSet()
        allArtists
            .mapNotNull { candidate ->
                if (candidate.name == activeArtist.name) {
                    null
                } else {
                    val collaborationOverlap = activeTracks.count { candidate.name in it.artistCredits }
                    val genreOverlap = candidate.genres.count { it.lowercase() in activeGenres }
                    val score = (collaborationOverlap * 10) + genreOverlap
                    if (score > 0) candidate to score else null
                }
            }
            .sortedWith(
                compareByDescending<Pair<DesktopArtistSummary, Int>> { it.second }
                    .thenBy { it.first.name.lowercase() },
            )
            .map { it.first }
            .take(8)
    }
    val collaboratorConnections = remember(selectedArtist?.name, tracks) {
        val activeArtist = selectedArtist ?: return@remember emptyList()
        tracks
            .filter { activeArtist.name in it.artistCredits }
            .flatMap { track ->
                track.artistCredits
                    .filterNot { collaboratorName -> collaboratorName == activeArtist.name }
                    .distinct()
                    .map { collaboratorName -> collaboratorName to track }
            }
            .groupBy(
                keySelector = { (collaboratorName, _) -> collaboratorName },
                valueTransform = { (_, track) -> track },
            )
            .map { (collaboratorName, collaboratorTracks) ->
                val distinctTracks = collaboratorTracks.distinctBy(DesktopTrack::id)
                DesktopArtistConnectionSummary(
                    name = collaboratorName,
                    sharedTrackCount = distinctTracks.size,
                    sharedAlbumCount = distinctTracks
                        .map { track -> desktopAlbumKey(track.albumArtist, track.album) }
                        .distinct()
                        .size,
                    genres = distinctTracks
                        .map(DesktopTrack::genre)
                        .filter(String::isNotBlank)
                        .distinct()
                        .sorted(),
                    palette = distinctTracks.firstOrNull()?.palette ?: listOf(
                        Color(0xFF0F1330),
                        VerseBlue,
                        AuroraCyan,
                    ),
                )
            }
            .sortedWith(
                compareByDescending<DesktopArtistConnectionSummary> { it.sharedTrackCount }
                    .thenBy { it.name.lowercase() },
            )
            .take(10)
    }

    fun rememberSearch(query: String) {
        val normalized = query.trim()
        if (normalized.isBlank()) return
        val nextSearches = (listOf(normalized) + recentSearches.filterNot { it.equals(normalized, ignoreCase = true) })
            .take(10)
        recentSearches = nextSearches
        appStore.saveRecentSearches(nextSearches)
    }

    fun applyTrackCustomization(track: DesktopTrack): DesktopTrack {
        val override = trackOverrides[track.path] ?: return track
        val updatedTitle = override.title?.trim().orEmpty().ifBlank { track.title }
        val updatedArtist = override.artist?.trim().orEmpty().ifBlank { track.artist }
        val updatedAlbum = override.album?.trim().orEmpty().ifBlank { track.album }
        val updatedGenre = override.genre?.trim().orEmpty().ifBlank { track.genre }
        return track.copy(
            title = updatedTitle,
            artist = updatedArtist,
            artistCredits = buildDesktopArtistCredits(updatedArtist, updatedTitle).ifEmpty { listOf(updatedArtist) },
            album = updatedAlbum,
            genre = updatedGenre,
            mood = updatedGenre.takeIf { it.isNotBlank() && !it.equals("Unclassified", ignoreCase = true) } ?: track.mood,
        )
    }

    fun buildPlaybackSessionSnapshot(positionMs: Long = playbackState.positionMs): DesktopPlaybackSessionSnapshot? {
        val activeTrack = currentTrack ?: return null
        return DesktopPlaybackSessionSnapshot(
            currentTrackPath = activeTrack.path,
            queueTrackPaths = activeQueue.map(DesktopTrack::path),
            queueLabel = queueLabel,
            positionMs = positionMs.coerceAtLeast(0L),
        )
    }

    fun restorePlaybackSession(snapshot: DesktopPlaybackSessionSnapshot?, scannedTracks: List<DesktopTrack>): Boolean {
        val activeSnapshot = snapshot ?: return false
        val restoredTrack = activeSnapshot.currentTrackPath
            ?.let { path -> scannedTracks.firstOrNull { it.path == path } }
            ?: return false
        val restoredQueue = activeSnapshot.queueTrackPaths
            .mapNotNull { path -> scannedTracks.firstOrNull { it.path == path } }
            .distinctBy(DesktopTrack::id)
        queueTrackPaths = if (restoredQueue.isNotEmpty()) {
            restoredQueue.map(DesktopTrack::path)
        } else {
            scannedTracks.map(DesktopTrack::path)
        }
        queueLabel = activeSnapshot.queueLabel.ifBlank { "All Songs" }
        currentTrackId = restoredTrack.id
        playbackController.loadTrack(
            track = restoredTrack,
            autoPlay = false,
            startPositionMs = activeSnapshot.positionMs,
        )
        pendingPlaybackSession = null
        return true
    }

    fun normalizedLibraryPaths(paths: List<Path>): List<Path> =
        paths
            .mapNotNull { path -> runCatching { path.toAbsolutePath().normalize() }.getOrNull() }
            .filter(Files::exists)
            .distinct()

    fun clearLibrarySources() {
        libraryStore.saveLibraryPaths(emptyList())
        libraryState = DesktopLibraryUiState(sourcePaths = emptyList())
        lyricsStatuses = emptyMap()
        currentTrackId = null
        queueTrackPaths = emptyList()
        queueLabel = "All Songs"
        pendingPlaybackSession = null
        appStore.savePlaybackSession(null)
        playbackController.stopPlayback()
    }

    DisposableEffect(playbackController) {
        onDispose {
            val trackPath = activeHistoryTrackPath
            val listenedMs = activeHistoryAccumulatedMs
            if (trackPath != null && listenedMs >= 1_000L) {
                tracks.firstOrNull { it.path == trackPath }?.let { track ->
                    val nextHistory = (listOf(
                        DesktopPlayHistoryEntry(
                            trackPath = track.path,
                            title = track.title,
                            artist = track.artist,
                            album = track.album,
                            listenedMs = listenedMs,
                            playedAtMs = System.currentTimeMillis(),
                        ),
                    ) + playHistoryEntries).take(2500)
                    appStore.savePlayHistory(nextHistory)
                }
            }
            playbackController.dispose()
        }
    }

    fun scanFolders(paths: List<Path>) {
        coroutineScope.launch {
            val normalizedPaths = normalizedLibraryPaths(paths)
            if (normalizedPaths.isEmpty()) {
                clearLibrarySources()
                return@launch
            }
            val sessionToRestore = buildPlaybackSessionSnapshot() ?: pendingPlaybackSession
            libraryState = libraryState.copy(
                sourcePaths = normalizedPaths.map(Path::toString),
                isScanning = true,
                errorMessage = null,
            )
            libraryStore.saveLibraryPaths(normalizedPaths.map(Path::toString))
            val scannedTracks = runCatching { scanDesktopLibrary(normalizedPaths) }.getOrElse { error ->
                libraryState = libraryState.copy(
                    isScanning = false,
                    errorMessage = error.message ?: "VerseFlow could not scan those folders.",
                )
                return@launch
            }
            val visibleTracks = scannedTracks
                .map(::applyTrackCustomization)
                .filterNot { track -> track.path in hiddenTrackPaths }
            libraryState = libraryState.copy(
                tracks = visibleTracks,
                isScanning = false,
                errorMessage = null,
            )
            lyricsStatuses = visibleTracks.associate { track ->
                track.id to if (track.lyrics.isNotEmpty() || track.plainLyrics.isNotEmpty()) {
                    DesktopLyricsLoadState.Ready
                } else {
                    DesktopLyricsLoadState.Idle
                }
            }
            val visibleTrackIds = visibleTracks.map(DesktopTrack::id).toSet()
            recentTrackIds = playHistoryEntries.toRecentTrackIds().filter { it in visibleTrackIds }
            playCounts = playHistoryEntries.toPlayCounts().filterKeys { it in visibleTrackIds }
            favoriteTrackPaths = favoriteTrackPaths.filter { favoritePath -> visibleTracks.any { it.path == favoritePath } }
            libraryStore.saveFavoriteTrackPaths(favoriteTrackPaths)
            queueTrackPaths = queueTrackPaths.filter { queuedPath -> visibleTracks.any { it.path == queuedPath } }
            if (queueTrackPaths.isEmpty() && visibleTracks.isNotEmpty()) {
                queueTrackPaths = visibleTracks.map(DesktopTrack::path)
                queueLabel = "All Songs"
            }
            if (!restorePlaybackSession(sessionToRestore, visibleTracks)) {
                currentTrackId = currentTrackId?.takeIf { id -> visibleTracks.any { it.id == id } }
                    ?: visibleTracks.firstOrNull()?.id
                playbackController.stopPlayback()
            }
        }
    }

    fun scanFolder(path: Path) = scanFolders(listOf(path))

    fun addLibraryFolders(paths: List<Path>) {
        val mergedPaths = normalizedLibraryPaths(
            libraryState.sourcePaths.map(Path::of) + paths,
        )
        scanFolders(mergedPaths)
    }

    fun removeLibraryFolder(path: String) {
        val remainingPaths = libraryState.sourcePaths
            .filterNot { it == path }
            .map(Path::of)
        if (remainingPaths.isEmpty()) {
            clearLibrarySources()
        } else {
            scanFolders(remainingPaths)
        }
    }

    fun appendPlayHistoryEntry(track: DesktopTrack, listenedMs: Long, playedAtMs: Long = System.currentTimeMillis()) {
        val normalizedListenedMs = listenedMs.coerceAtLeast(0L)
        if (normalizedListenedMs < 1_000L) return
        val nextHistory = (listOf(
            DesktopPlayHistoryEntry(
                trackPath = track.path,
                title = track.title,
                artist = track.artist,
                album = track.album,
                listenedMs = normalizedListenedMs,
                playedAtMs = playedAtMs,
            ),
        ) + playHistoryEntries).take(2500)
        playHistoryEntries = nextHistory
        appStore.savePlayHistory(nextHistory)
        recentTrackIds = nextHistory.toRecentTrackIds()
        playCounts = nextHistory.toPlayCounts()
    }

    fun flushActiveListeningHistory() {
        val trackPath = activeHistoryTrackPath ?: return
        val listenedMs = activeHistoryAccumulatedMs
        tracks.firstOrNull { it.path == trackPath }?.let { track ->
            appendPlayHistoryEntry(track, listenedMs = listenedMs)
        }
        activeHistoryTrackId = null
        activeHistoryTrackPath = null
        activeHistoryAccumulatedMs = 0L
        activeHistoryLastPositionMs = 0L
    }

    fun clearPlayHistory() {
        playHistoryEntries = emptyList()
        appStore.savePlayHistory(emptyList())
        recentTrackIds = emptyList()
        playCounts = emptyMap()
        activeHistoryAccumulatedMs = 0L
        activeHistoryLastPositionMs = playbackState.positionMs.coerceAtLeast(0L)
    }

    fun playTrack(
        track: DesktopTrack,
        queue: List<DesktopTrack> = tracks,
        label: String = "All Songs",
    ) {
        val normalizedQueue = queue.distinctBy(DesktopTrack::id).ifEmpty { listOf(track) }
        queueTrackPaths = normalizedQueue.map(DesktopTrack::path)
        queueLabel = label
        currentTrackId = track.id
        playbackController.loadTrack(track, autoPlay = true)
    }

    fun updateTrackLyrics(trackId: String, payload: DesktopLyricsPayload) {
        libraryState = libraryState.copy(
            tracks = libraryState.tracks.map { track ->
                if (track.id == trackId) {
                    track.copy(
                        lyrics = payload.syncedLyrics,
                        plainLyrics = payload.plainLyrics,
                        lyricsAttribution = payload.attribution,
                    )
                } else {
                    track
                }
            },
        )
    }

    fun toggleFavoriteTrack(track: DesktopTrack) {
        val nextFavorites = if (track.path in favoriteTrackPaths) {
            favoriteTrackPaths.filterNot { it == track.path }
        } else {
            listOf(track.path) + favoriteTrackPaths
        }
        favoriteTrackPaths = nextFavorites
        libraryStore.saveFavoriteTrackPaths(nextFavorites)
    }

    fun saveTrackMetadataOverride(
        track: DesktopTrack,
        title: String,
        artist: String,
        album: String,
        genre: String,
    ) {
        val override = DesktopTrackMetadataOverride(
            title = title.trim().takeIf(String::isNotBlank),
            artist = artist.trim().takeIf(String::isNotBlank),
            album = album.trim().takeIf(String::isNotBlank),
            genre = genre.trim().takeIf(String::isNotBlank),
        )
        trackOverrides = trackOverrides + (track.path to override)
        appStore.saveTrackOverrides(trackOverrides)
        libraryState = libraryState.copy(
            tracks = libraryState.tracks.map { candidate ->
                if (candidate.path == track.path) applyTrackCustomization(candidate) else candidate
            },
        )
        editingTrack = null
    }

    fun applyManualLyrics(track: DesktopTrack, payload: DesktopLyricsPayload) {
        updateTrackLyrics(track.id, payload)
        lyricsStatuses = lyricsStatuses + (track.id to DesktopLyricsLoadState.Ready)
        coroutineScope.launch {
            lyricsCacheStore.save(track.path, payload)
        }
        manualLyricsTrack = null
    }

    fun updateArtistProfile(
        artistName: String,
        photoPath: String? = artistProfileOverrides[artistName]?.photoPath,
        about: String? = artistProfileOverrides[artistName]?.about,
    ) {
        val nextOverride = DesktopArtistProfileOverride(
            photoPath = photoPath?.trim().takeUnless { it.isNullOrEmpty() },
            about = about?.trim().takeUnless { it.isNullOrEmpty() },
        )
        artistProfileOverrides = if (nextOverride.photoPath == null && nextOverride.about == null) {
            artistProfileOverrides - artistName
        } else {
            artistProfileOverrides + (artistName to nextOverride)
        }
        appStore.saveArtistProfileOverrides(artistProfileOverrides)
    }

    fun persistUserPlaylists(nextPlaylists: List<DesktopUserPlaylist>) {
        userPlaylists = nextPlaylists
        playlistStore.savePlaylists(nextPlaylists)
    }

    fun createUserPlaylist() {
        val nextPlaylist = DesktopUserPlaylist(
            title = "New Playlist",
            description = "Made in VerseFlow Desktop",
        )
        persistUserPlaylists(listOf(nextPlaylist) + userPlaylists)
        selectedPlaylistId = nextPlaylist.id
        section = DesktopSection.PlaylistDetail
    }

    fun updateUserPlaylist(
        playlistId: String,
        transform: (DesktopUserPlaylist) -> DesktopUserPlaylist,
    ) {
        persistUserPlaylists(
            userPlaylists.map { playlist ->
                if (playlist.id == playlistId) transform(playlist) else playlist
            },
        )
    }

    fun deleteUserPlaylist(playlistId: String) {
        if (playlistId == FavoritesPlaylistId) return
        persistUserPlaylists(userPlaylists.filterNot { it.id == playlistId })
        if (selectedPlaylistId == playlistId) {
            selectedPlaylistId = null
            libraryTab = DesktopLibraryTab.Playlists
            section = DesktopSection.Library
        }
    }

    fun hideTrackFromVerseFlow(track: DesktopTrack) {
        hiddenTrackPaths = hiddenTrackPaths + track.path
        appStore.saveHiddenTrackPaths(hiddenTrackPaths)
        libraryState = libraryState.copy(tracks = libraryState.tracks.filterNot { it.path == track.path })
        favoriteTrackPaths = favoriteTrackPaths.filterNot { it == track.path }
        libraryStore.saveFavoriteTrackPaths(favoriteTrackPaths)
        queueTrackPaths = queueTrackPaths.filterNot { it == track.path }
        persistUserPlaylists(
            userPlaylists.map { playlist ->
                playlist.copy(trackPaths = playlist.trackPaths.filterNot { it == track.path })
            },
        )
        if (currentTrackId == track.id) {
            playbackController.stopPlayback()
            currentTrackId = libraryState.tracks.firstOrNull { it.path != track.path }?.id
        }
    }

    fun addTrackToUserPlaylist(playlistId: String, track: DesktopTrack) {
        updateUserPlaylist(playlistId) { playlist ->
            if (track.path in playlist.trackPaths) {
                playlist
            } else {
                playlist.copy(trackPaths = playlist.trackPaths + track.path)
            }
        }
    }

    fun createPlaylistWithTrack(track: DesktopTrack) {
        val nextPlaylist = DesktopUserPlaylist(
            title = track.title.take(42).ifBlank { "New Playlist" },
            description = "Created from ${track.title}",
            trackPaths = listOf(track.path),
        )
        persistUserPlaylists(listOf(nextPlaylist) + userPlaylists)
        selectedPlaylistId = nextPlaylist.id
        section = DesktopSection.PlaylistDetail
    }

    fun addTrackToQueue(track: DesktopTrack) {
        queueTrackPaths = (activeQueue.map(DesktopTrack::path) + track.path)
        queueLabel = "Play Queue"
    }

    fun openTrackArtist(track: DesktopTrack) {
        selectedArtistName = track.artistCredits.firstOrNull() ?: track.artist
        section = DesktopSection.ArtistDetail
    }

    fun openTrackAlbum(track: DesktopTrack) {
        selectedAlbumKey = desktopAlbumKey(track.albumArtist, track.album)
        section = DesktopSection.AlbumDetail
    }

    fun requestTrackDeletion(track: DesktopTrack) {
        deletingTrack = track
    }

    fun requestTrackEdit(track: DesktopTrack) {
        editingTrack = track
    }

    fun deleteTrackFromDevice(track: DesktopTrack) {
        runCatching { Files.deleteIfExists(Path.of(track.path)) }
            .onSuccess {
                deletingTrack = null
                hiddenTrackPaths = hiddenTrackPaths - track.path
                appStore.saveHiddenTrackPaths(hiddenTrackPaths)
                trackOverrides = trackOverrides - track.path
                appStore.saveTrackOverrides(trackOverrides)
                if (libraryState.sourcePaths.isEmpty()) {
                    clearLibrarySources()
                } else {
                    scanFolders(libraryState.sourcePaths.map(Path::of))
                }
            }
            .onFailure { error ->
                deletingTrack = null
                libraryState = libraryState.copy(
                    errorMessage = error.message ?: "VerseFlow could not delete that file from disk.",
                )
            }
    }

    val trackMenuModel = remember(editablePlaylists, favoriteTrackPaths) {
        DesktopTrackMenuModel(
            favoriteTrackPaths = favoriteTrackPaths.toSet(),
            userPlaylists = editablePlaylists,
            onAddToQueue = ::addTrackToQueue,
            onToggleFavorite = ::toggleFavoriteTrack,
            onAddToPlaylist = ::addTrackToUserPlaylist,
            onCreatePlaylistWithTrack = ::createPlaylistWithTrack,
            onOpenArtist = ::openTrackArtist,
            onOpenAlbum = ::openTrackAlbum,
            onHideTrack = ::hideTrackFromVerseFlow,
            onDeleteTrack = ::requestTrackDeletion,
            onEditTrack = ::requestTrackEdit,
        )
    }

    fun stepToTrack(offset: Int) {
        if (activeQueue.isEmpty()) return
        val currentIndex = activeQueue.indexOfFirst { it.id == currentTrackId }.let { index ->
            if (index < 0) 0 else index
        }
        val nextTrack = when {
            offset > 0 && isShuffleEnabled && activeQueue.size > 1 -> {
                val candidateIndexes = activeQueue.indices.filterNot { it == currentIndex }
                activeQueue[candidateIndexes.random(Random.Default)]
            }
            else -> {
                val requestedIndex = currentIndex + offset
                val resolvedIndex = when {
                    requestedIndex < 0 -> if (isRepeatEnabled) activeQueue.lastIndex else 0
                    requestedIndex > activeQueue.lastIndex -> if (isRepeatEnabled) 0 else activeQueue.lastIndex
                    else -> requestedIndex
                }
                activeQueue[resolvedIndex]
            }
        }
        currentTrackId = nextTrack.id
        playbackController.loadTrack(nextTrack, autoPlay = true)
    }

    LaunchedEffect(Unit) {
        libraryState.sourcePaths
            .takeIf { it.isNotEmpty() }
            ?.map(Path::of)
            ?.let(::scanFolders)
    }

    LaunchedEffect(displayName, selectedTheme, isShuffleEnabled, isRepeatEnabled, autoRescanEnabled, musixmatchApiKey) {
        appStore.saveSettings(
            DesktopSettingsSnapshot(
                displayName = displayName,
                selectedTheme = selectedTheme,
                isShuffleEnabled = isShuffleEnabled,
                isRepeatEnabled = isRepeatEnabled,
                autoRescanEnabled = autoRescanEnabled,
                musixmatchApiKey = musixmatchApiKey,
            ),
        )
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            buildPlaybackSessionSnapshot(
                positionMs = ((playbackState.positionMs / 2_000L) * 2_000L).coerceAtLeast(0L),
            )
        }
            .distinctUntilChanged()
            .collect { snapshot ->
                appStore.savePlaybackSession(snapshot)
            }
    }

    LaunchedEffect(playbackController, tracks) {
        snapshotFlow { Triple(playbackState.trackId, playbackState.isPlaying, playbackState.positionMs) }
            .collect { (trackId, isPlaying, positionMs) ->
                if (trackId == null) {
                    flushActiveListeningHistory()
                    return@collect
                }

                val track = tracks.firstOrNull { it.id == trackId }
                if (track == null) {
                    flushActiveListeningHistory()
                    return@collect
                }

                if (activeHistoryTrackId != trackId) {
                    flushActiveListeningHistory()
                    activeHistoryTrackId = trackId
                    activeHistoryTrackPath = track.path
                    activeHistoryAccumulatedMs = 0L
                    activeHistoryLastPositionMs = positionMs.coerceAtLeast(0L)
                    return@collect
                }

                val safePositionMs = positionMs.coerceAtLeast(0L)
                if (isPlaying) {
                    val delta = safePositionMs - activeHistoryLastPositionMs
                    if (delta in 1L..5_000L) {
                        activeHistoryAccumulatedMs += delta
                    }
                    activeHistoryLastPositionMs = safePositionMs
                } else {
                    flushActiveListeningHistory()
                }
            }
    }

    val chooseFolder = {
        val selectedFolders = chooseDesktopFolders(
            initialDirectory = libraryState.rootPath?.let(Path::of),
        )
        if (selectedFolders.isNotEmpty()) {
            scanFolders(selectedFolders)
        }
    }

    LaunchedEffect(playbackController, tracks) {
        playbackController.events.collect { event ->
            when (event) {
                DesktopPlaybackEvent.TrackCompleted -> {
                    if (activeQueue.isNotEmpty()) {
                        val currentIndex = activeQueue.indexOfFirst { it.id == currentTrackId }.let { index ->
                            if (index < 0) 0 else index
                        }
                        val nextTrack = when {
                            isShuffleEnabled && activeQueue.size > 1 -> {
                                val candidateIndexes = activeQueue.indices.filterNot { it == currentIndex }
                                activeQueue[candidateIndexes.random(Random.Default)]
                            }
                            currentIndex < activeQueue.lastIndex -> activeQueue[currentIndex + 1]
                            isRepeatEnabled -> activeQueue.first()
                            else -> null
                        }
                        if (nextTrack != null) {
                            currentTrackId = nextTrack.id
                            playbackController.loadTrack(nextTrack, autoPlay = true)
                        }
                    }
                }
            }
        }
    }

    fun openLibraryTab(tab: DesktopLibraryTab) {
        libraryTab = tab
        section = DesktopSection.Library
    }

    val sidebarItems = listOf(
        DesktopSidebarItem(
            title = "Home",
            icon = Icons.Rounded.Home,
            selected = section == DesktopSection.Home,
            onClick = { section = DesktopSection.Home },
        ),
        DesktopSidebarItem(
            title = "Play Queue",
            icon = Icons.AutoMirrored.Rounded.QueueMusic,
            selected = section == DesktopSection.PlayQueue,
            onClick = { section = DesktopSection.PlayQueue },
        ),
        DesktopSidebarItem(
            title = "Play History",
            icon = Icons.Rounded.History,
            selected = section == DesktopSection.PlayHistory,
            onClick = { section = DesktopSection.PlayHistory },
        ),
        DesktopSidebarItem(
            title = "Favourites",
            icon = Icons.Rounded.Favorite,
            selected = (section == DesktopSection.Library && libraryTab == DesktopLibraryTab.Favourites) ||
                (section == DesktopSection.PlaylistDetail && selectedPlaylistId == FavoritesPlaylistId),
            onClick = { openLibraryTab(DesktopLibraryTab.Favourites) },
        ),
        DesktopSidebarItem(
            title = "Playlists",
            icon = Icons.AutoMirrored.Rounded.QueueMusic,
            selected = (section == DesktopSection.Library && libraryTab == DesktopLibraryTab.Playlists) ||
                (section == DesktopSection.PlaylistDetail && selectedPlaylistId != FavoritesPlaylistId),
            onClick = { openLibraryTab(DesktopLibraryTab.Playlists) },
        ),
        DesktopSidebarItem(
            title = "Artists",
            icon = Icons.Rounded.LibraryMusic,
            selected = (section == DesktopSection.Library && libraryTab == DesktopLibraryTab.Artists) || section == DesktopSection.ArtistDetail,
            onClick = { openLibraryTab(DesktopLibraryTab.Artists) },
        ),
        DesktopSidebarItem(
            title = "Albums",
            icon = Icons.Rounded.Album,
            selected = (section == DesktopSection.Library && libraryTab == DesktopLibraryTab.Albums) || section == DesktopSection.AlbumDetail,
            onClick = { openLibraryTab(DesktopLibraryTab.Albums) },
        ),
        DesktopSidebarItem(
            title = "Songs",
            icon = Icons.Rounded.GraphicEq,
            selected = section == DesktopSection.Library && libraryTab == DesktopLibraryTab.Songs,
            onClick = { openLibraryTab(DesktopLibraryTab.Songs) },
        ),
        DesktopSidebarItem(
            title = "Genres",
            icon = Icons.Rounded.Lyrics,
            selected = section == DesktopSection.Library && libraryTab == DesktopLibraryTab.Genres,
            onClick = { openLibraryTab(DesktopLibraryTab.Genres) },
        ),
        DesktopSidebarItem(
            title = "Settings",
            icon = Icons.Rounded.Settings,
            selected = section == DesktopSection.Settings,
            onClick = { section = DesktopSection.Settings },
        ),
    )

    LaunchedEffect(currentTrack?.id) {
        val activeTrack = currentTrack ?: return@LaunchedEffect
        if (activeTrack.lyrics.isNotEmpty() || activeTrack.plainLyrics.isNotEmpty()) {
            lyricsStatuses = lyricsStatuses + (activeTrack.id to DesktopLyricsLoadState.Ready)
            return@LaunchedEffect
        }
        if (lyricsStatuses[activeTrack.id] == DesktopLyricsLoadState.Loading ||
            lyricsStatuses[activeTrack.id] == DesktopLyricsLoadState.Unavailable
        ) {
            return@LaunchedEffect
        }
        lyricsCacheStore.load(activeTrack.path)?.let { cachedLyrics ->
            updateTrackLyrics(activeTrack.id, cachedLyrics)
            lyricsStatuses = lyricsStatuses + (activeTrack.id to DesktopLyricsLoadState.Ready)
            return@LaunchedEffect
        }

        lyricsStatuses = lyricsStatuses + (activeTrack.id to DesktopLyricsLoadState.Loading)
        val payload = lyricsRepository.lookup(activeTrack)
        if (payload != null) {
            updateTrackLyrics(activeTrack.id, payload)
            lyricsCacheStore.save(activeTrack.path, payload)
            lyricsStatuses = lyricsStatuses + (activeTrack.id to DesktopLyricsLoadState.Ready)
        } else {
            lyricsStatuses = lyricsStatuses + (activeTrack.id to DesktopLyricsLoadState.Unavailable)
        }
    }

    MaterialTheme(colorScheme = VerseFlowDesktopColors) {
        val isLibrarySection = section == DesktopSection.Library
        val isHomeSection = section == DesktopSection.Home
        val isSearchSection = section == DesktopSection.Search
        val isPlayQueueSection = section == DesktopSection.PlayQueue
        val isPlayHistorySection = section == DesktopSection.PlayHistory
        val isSettingsSection = section == DesktopSection.Settings
        val isAlbumDetailSection = section == DesktopSection.AlbumDetail
        val isArtistDetailSection = section == DesktopSection.ArtistDetail
        val isPlaylistDetailSection = section == DesktopSection.PlaylistDetail
        val isPageOwnedChromeSection =
            isLibrarySection ||
                isHomeSection ||
                isSearchSection ||
                isPlayQueueSection ||
                isPlayHistorySection ||
                isSettingsSection ||
                isArtistDetailSection
        val isEdgeToEdgeSection =
            isPageOwnedChromeSection ||
                isAlbumDetailSection ||
                isPlaylistDetailSection
        val contentStartPadding by animateDpAsState(
            targetValue = if (isEdgeToEdgeSection) 0.dp else 24.dp,
            label = "desktopContentStartPadding",
        )
        val contentEndPadding by animateDpAsState(
            targetValue = if (isEdgeToEdgeSection) 0.dp else 24.dp,
            label = "desktopContentEndPadding",
        )
        val contentBottomPadding by animateDpAsState(
            targetValue = if (isEdgeToEdgeSection) 0.dp else 24.dp,
            label = "desktopContentBottomPadding",
        )
        val contentTopPadding by animateDpAsState(
            targetValue = if (isEdgeToEdgeSection) 0.dp else 24.dp,
            label = "desktopContentTopPadding",
        )
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            DesktopAppBackdrop(track = currentTrack)
            Row(modifier = Modifier.fillMaxSize()) {
                DesktopSidebar(
                    items = sidebarItems,
                    collapsed = isSidebarCollapsed,
                    onToggleCollapsed = {
                        val nextState = !isSidebarCollapsed
                        isSidebarCollapsed = nextState
                        libraryStore.saveSidebarCollapsed(nextState)
                    },
                    queueLabel = queueLabel,
                    modifier = Modifier.width(if (isSidebarCollapsed) 96.dp else 250.dp),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(
                            start = contentStartPadding,
                            end = contentEndPadding,
                            bottom = contentBottomPadding,
                            top = contentTopPadding,
                        ),
                    verticalArrangement = Arrangement.spacedBy(if (isPageOwnedChromeSection) 0.dp else 18.dp),
                ) {
                    if (!isPageOwnedChromeSection) {
                        DesktopTopBar(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            onOpenSearch = {
                                if (searchQuery.isNotBlank()) rememberSearch(searchQuery)
                                section = DesktopSection.Search
                            },
                            libraryPaths = libraryState.sourcePaths,
                            trackCount = tracks.size,
                            isScanning = libraryState.isScanning,
                            onChooseFolder = chooseFolder,
                            compactMode = false,
                            compactTitle = null,
                            onRescan = {
                                libraryState.sourcePaths
                                    .takeIf { it.isNotEmpty() }
                                    ?.map(Path::of)
                                    ?.let(::scanFolders)
                            },
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        AnimatedContent(targetState = section, label = "desktopSection") { activeSection ->
                            when (activeSection) {
                                DesktopSection.Home -> DesktopHome(
                                    name = displayName,
                                    tracks = tracks,
                                    currentTrack = currentTrack,
                                    albums = featuredAlbums,
                                    recentTracks = recentTracks,
                                    trendingTracks = trendingTracks,
                                    playlists = favoritePlaylists,
                                    artists = featuredArtists,
                                    artistSpotlightOrder = artistSpotlightOrder,
                                    genres = featuredGenres,
                                    searchQuery = searchQuery,
                                    onSearchChange = { searchQuery = it },
                                    onOpenSearch = {
                                        if (searchQuery.isNotBlank()) rememberSearch(searchQuery)
                                        section = DesktopSection.Search
                                    },
                                    libraryPaths = libraryState.sourcePaths,
                                    trackCount = tracks.size,
                                    isScanning = libraryState.isScanning,
                                    libraryRootPath = libraryState.rootPath,
                                    errorMessage = libraryState.errorMessage,
                                    onChooseFolder = chooseFolder,
                                    onRescan = {
                                        libraryState.sourcePaths
                                            .takeIf { it.isNotEmpty() }
                                            ?.map(Path::of)
                                            ?.let(::scanFolders)
                                    },
                                    onHomeScrolledChange = { homeScrolled = it },
                                    onOpenNowPlaying = { section = DesktopSection.NowPlaying },
                                    onOpenLyrics = { section = DesktopSection.Lyrics },
                                    onOpenAlbum = { album ->
                                        selectedAlbumKey = desktopAlbumKey(album.artist, album.title)
                                        section = DesktopSection.AlbumDetail
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks, label = "All Songs")
                                        section = DesktopSection.NowPlaying
                                    },
                                    onPlayCollection = { collection, label ->
                                        collection.firstOrNull()?.let { playTrack(it, queue = collection, label = label) }
                                        section = DesktopSection.NowPlaying
                                    },
                                    onArtistSpotlightOrderChange = { orderedArtists ->
                                        artistSpotlightOrder = orderedArtists
                                        appStore.saveArtistSpotlightOrder(orderedArtists)
                                    },
                                    onOpenArtist = { artist ->
                                        selectedArtistName = artist.name
                                        section = DesktopSection.ArtistDetail
                                    },
                                    onOpenPlaylist = { playlist ->
                                        selectedPlaylistId = playlist.id
                                        section = DesktopSection.PlaylistDetail
                                    },
                                )

                                DesktopSection.Library -> DesktopLibrary(
                                    tracks = tracks,
                                    albums = allAlbums,
                                    artists = allArtists,
                                    playlists = allPlaylists,
                                    genres = libraryGenres,
                                    selectedTab = libraryTab,
                                    onTabSelect = { libraryTab = it },
                                    searchQuery = searchQuery,
                                        onSearchChange = { searchQuery = it },
                                        onOpenSearch = {
                                            if (searchQuery.isNotBlank()) rememberSearch(searchQuery)
                                            section = DesktopSection.Search
                                        },
                                        libraryPaths = libraryState.sourcePaths,
                                        trackCount = tracks.size,
                                        currentTrack = currentTrack,
                                        isScanning = libraryState.isScanning,
                                    errorMessage = libraryState.errorMessage,
                                    albumsViewMode = albumsViewMode,
                                    artistsViewMode = artistsViewMode,
                                    albumsSortMode = albumsSortMode,
                                    onAlbumsSortModeChange = { albumsSortMode = it },
                                    onAlbumsViewModeChange = { albumsViewMode = it },
                                    onArtistsViewModeChange = { artistsViewMode = it },
                                    onAlbumsGridScrolledChange = { albumsGridScrolled = it },
                                    onChooseFolder = chooseFolder,
                                    onRescan = {
                                            libraryState.sourcePaths
                                                .takeIf { it.isNotEmpty() }
                                                ?.map(Path::of)
                                                ?.let(::scanFolders)
                                        },
                                        onSelectTrack = { track, queue, label ->
                                            playTrack(track, queue = queue, label = label)
                                    },
                                    onPlayCollection = { collection ->
                                        collection.firstOrNull()?.let { playTrack(it, queue = collection, label = "Selected Collection") }
                                        section = DesktopSection.NowPlaying
                                    },
                                    onOpenPlaylist = { playlist ->
                                        selectedPlaylistId = playlist.id
                                        section = DesktopSection.PlaylistDetail
                                    },
                                    onOpenAlbum = { album ->
                                        selectedAlbumKey = desktopAlbumKey(album.artist, album.title)
                                        section = DesktopSection.AlbumDetail
                                    },
                                    onOpenArtist = { artist ->
                                        selectedArtistName = artist.name
                                        section = DesktopSection.ArtistDetail
                                    },
                                    onCreatePlaylist = ::createUserPlaylist,
                                    trackMenu = trackMenuModel,
                                )

                                DesktopSection.Search -> DesktopSearch(
                                    query = searchQuery,
                                    recentSearches = recentSearches,
                                    tracks = tracks,
                                    albums = allAlbums,
                                    artists = allArtists,
                                    playlists = allPlaylists,
                                    genres = libraryGenres,
                                    currentTrack = currentTrack,
                                    trackMenu = trackMenuModel,
                                    libraryPaths = libraryState.sourcePaths,
                                    trackCount = tracks.size,
                                    isScanning = libraryState.isScanning,
                                    onChooseFolder = chooseFolder,
                                    onRescan = {
                                        libraryState.sourcePaths
                                            .takeIf { it.isNotEmpty() }
                                            ?.map(Path::of)
                                            ?.let(::scanFolders)
                                    },
                                    onQueryChange = {
                                        searchQuery = it
                                        if (it.isNotBlank()) rememberSearch(it)
                                    },
                                    onUseRecentSearch = {
                                        searchQuery = it
                                        rememberSearch(it)
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks, label = "Search Results")
                                        rememberSearch(track.title)
                                    },
                                    onOpenAlbum = { album ->
                                        selectedAlbumKey = desktopAlbumKey(album.artist, album.title)
                                        section = DesktopSection.AlbumDetail
                                        rememberSearch(album.title)
                                    },
                                    onOpenArtist = { artist ->
                                        selectedArtistName = artist.name
                                        section = DesktopSection.ArtistDetail
                                        rememberSearch(artist.name)
                                    },
                                    onOpenPlaylist = { playlist ->
                                        selectedPlaylistId = playlist.id
                                        section = DesktopSection.PlaylistDetail
                                        rememberSearch(playlist.title)
                                    },
                                    onPlayGenre = { genre ->
                                        val genreTracks = tracks.filter { it.genre.equals(genre.title, ignoreCase = true) }
                                        genreTracks.firstOrNull()?.let { playTrack(it, queue = genreTracks, label = genre.title) }
                                        rememberSearch(genre.title)
                                    },
                                )

                                DesktopSection.PlayQueue -> DesktopPlayQueue(
                                    searchQuery = searchQuery,
                                    onSearchChange = { searchQuery = it },
                                    onOpenSearch = {
                                        if (searchQuery.isNotBlank()) rememberSearch(searchQuery)
                                        section = DesktopSection.Search
                                    },
                                    libraryPaths = libraryState.sourcePaths,
                                    trackCount = tracks.size,
                                    isScanning = libraryState.isScanning,
                                    onChooseFolder = chooseFolder,
                                    onRescan = {
                                        libraryState.sourcePaths
                                            .takeIf { it.isNotEmpty() }
                                            ?.map(Path::of)
                                            ?.let(::scanFolders)
                                    },
                                    queueLabel = queueLabel,
                                    tracks = activeQueue,
                                    currentTrack = currentTrack,
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = activeQueue, label = queueLabel)
                                    },
                                    trackMenu = trackMenuModel,
                                )

                                DesktopSection.PlayHistory -> DesktopPlayHistory(
                                    searchQuery = searchQuery,
                                    onSearchChange = { searchQuery = it },
                                    onOpenSearch = {
                                        if (searchQuery.isNotBlank()) rememberSearch(searchQuery)
                                        section = DesktopSection.Search
                                    },
                                    libraryPaths = libraryState.sourcePaths,
                                    trackCount = tracks.size,
                                    isScanning = libraryState.isScanning,
                                    onChooseFolder = chooseFolder,
                                    onRescan = {
                                        libraryState.sourcePaths
                                            .takeIf { it.isNotEmpty() }
                                            ?.map(Path::of)
                                            ?.let(::scanFolders)
                                    },
                                    historyEntries = playHistoryEntries,
                                    tracks = tracks,
                                    currentTrack = currentTrack,
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks, label = "Play History")
                                    },
                                    onClearHistory = ::clearPlayHistory,
                                    trackMenu = trackMenuModel,
                                )

                                DesktopSection.NowPlaying -> DesktopNowPlaying(
                                    track = currentTrack,
                                    isPlaying = playbackState.isPlaying,
                                    progress = if (currentDurationMs == 0L || currentTrack == null) 0f else playbackState.positionMs.toFloat() / currentDurationMs.toFloat(),
                                    positionMs = playbackState.positionMs,
                                    errorMessage = playbackState.errorMessage,
                                    isFavorite = currentTrack?.path in favoriteTrackPaths,
                                    isShuffleEnabled = isShuffleEnabled,
                                    isRepeatEnabled = isRepeatEnabled,
                                    onPrevious = { stepToTrack(-1) },
                                    onPlayPause = {
                                        val activeTrack = currentTrack
                                        if (activeTrack != null) {
                                            if (playbackState.trackId != activeTrack.id) {
                                                playTrack(activeTrack, queue = activeQueue, label = queueLabel)
                                            } else {
                                                playbackController.togglePlayPause()
                                            }
                                        }
                                    },
                                    onNext = { stepToTrack(1) },
                                    onSeekTo = { targetMs -> playbackController.seekTo(targetMs) },
                                    onToggleFavorite = { activeTrack ->
                                        toggleFavoriteTrack(activeTrack)
                                    },
                                    onToggleShuffle = { isShuffleEnabled = !isShuffleEnabled },
                                    onToggleRepeat = { isRepeatEnabled = !isRepeatEnabled },
                                    onOpenLyrics = { section = DesktopSection.Lyrics },
                                    onChooseFolder = chooseFolder,
                                )

                                DesktopSection.Lyrics -> DesktopLyrics(
                                    track = currentTrack,
                                    progressMs = playbackState.positionMs,
                                    lyricsStatus = currentTrack?.id?.let { lyricsStatuses[it] } ?: DesktopLyricsLoadState.Idle,
                                    onSeekTo = { targetMs -> playbackController.seekTo(targetMs) },
                                    onBackToPlayer = { section = DesktopSection.NowPlaying },
                                    onOpenManualSearch = {
                                        currentTrack?.let { track -> manualLyricsTrack = track }
                                    },
                                    onChooseFolder = chooseFolder,
                                )

                                DesktopSection.Settings -> DesktopSettings(
                                    searchQuery = searchQuery,
                                    onSearchChange = { searchQuery = it },
                                    onOpenSearch = {
                                        if (searchQuery.isNotBlank()) rememberSearch(searchQuery)
                                        section = DesktopSection.Search
                                    },
                                    displayName = displayName,
                                    onDisplayNameChange = { displayName = it },
                                    selectedTheme = selectedTheme,
                                    themes = desktopThemes,
                                    onThemeSelect = { selectedTheme = it },
                                    autoRescanEnabled = autoRescanEnabled,
                                    onAutoRescanChange = { autoRescanEnabled = it },
                                    musixmatchApiKey = musixmatchApiKey,
                                    onMusixmatchApiKeyChange = { musixmatchApiKey = it },
                                    libraryPaths = libraryState.sourcePaths,
                                    trackCount = tracks.size,
                                    isScanning = libraryState.isScanning,
                                    onAddFolders = {
                                        val selectedFolders = chooseDesktopFolders(
                                            initialDirectory = libraryState.rootPath?.let(Path::of),
                                        )
                                        if (selectedFolders.isNotEmpty()) {
                                            addLibraryFolders(selectedFolders)
                                        }
                                    },
                                    onFoldersDropped = ::addLibraryFolders,
                                    onRemoveFolder = ::removeLibraryFolder,
                                    onRescanLibrary = {
                                        libraryState.sourcePaths
                                            .takeIf { it.isNotEmpty() }
                                            ?.map(Path::of)
                                            ?.let(::scanFolders)
                                    },
                                )

                                DesktopSection.PlaylistDetail -> DesktopPlaylistDetail(
                                    playlist = selectedPlaylist,
                                    allTracks = tracks,
                                    currentTrack = currentTrack,
                                    onBack = {
                                        libraryTab = DesktopLibraryTab.Playlists
                                        section = DesktopSection.Library
                                    },
                                    onPlayPlaylist = { playlist ->
                                        playlist.tracks.firstOrNull()?.let { playTrack(it, queue = playlist.tracks, label = playlist.title) }
                                        section = DesktopSection.NowPlaying
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(
                                            track,
                                            queue = selectedPlaylist?.tracks ?: activeQueue,
                                            label = selectedPlaylist?.title ?: queueLabel,
                                        )
                                        section = DesktopSection.NowPlaying
                                    },
                                    onUpdateTitle = { playlistId, title ->
                                        updateUserPlaylist(playlistId) { playlist ->
                                            playlist.copy(title = title.ifBlank { "New Playlist" })
                                        }
                                    },
                                    onUpdateDescription = { playlistId, description ->
                                        updateUserPlaylist(playlistId) { playlist ->
                                            playlist.copy(description = description)
                                        }
                                    },
                                    onAddTrack = { playlistId, track ->
                                        updateUserPlaylist(playlistId) { playlist ->
                                            if (track.path in playlist.trackPaths) playlist else playlist.copy(trackPaths = playlist.trackPaths + track.path)
                                        }
                                    },
                                    onRemoveTrack = { playlistId, track ->
                                        updateUserPlaylist(playlistId) { playlist ->
                                            playlist.copy(trackPaths = playlist.trackPaths.filterNot { it == track.path })
                                        }
                                    },
                                    onDeletePlaylist = ::deleteUserPlaylist,
                                    trackMenu = trackMenuModel,
                                )

                                DesktopSection.AlbumDetail -> DesktopAlbumDetail(
                                    album = selectedAlbum,
                                    tracks = selectedAlbum?.let { album ->
                                        tracks.filter { it.albumArtist == album.artist && it.album == album.title }
                                    }.orEmpty(),
                                    currentTrack = currentTrack,
                                    onBack = {
                                        libraryTab = DesktopLibraryTab.Albums
                                        section = DesktopSection.Library
                                    },
                                    onOpenArtist = { artistName ->
                                        selectedArtistName = artistName
                                        section = DesktopSection.ArtistDetail
                                    },
                                    onPlayAlbum = { albumTracks ->
                                        albumTracks.firstOrNull()?.let { playTrack(it, queue = albumTracks, label = selectedAlbum?.title ?: "Album") }
                                        section = DesktopSection.NowPlaying
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks.filter { it.albumArtist == selectedAlbum?.artist && it.album == selectedAlbum?.title }, label = selectedAlbum?.title ?: "Album")
                                        section = DesktopSection.NowPlaying
                                    },
                                    trackMenu = trackMenuModel,
                                )

                                DesktopSection.ArtistDetail -> DesktopArtistDetail(
                                    artist = selectedArtist,
                                    artistProfileOverride = selectedArtistProfileOverride,
                                    tracks = selectedArtist?.let { artist ->
                                        tracks.filter { artist.name in it.artistCredits }
                                    }.orEmpty(),
                                    albums = selectedArtist?.let { artist ->
                                        allAlbums.filter { album ->
                                            tracks.any { track ->
                                                track.album == album.title &&
                                                    track.albumArtist == album.artist &&
                                                    artist.name in track.artistCredits
                                            }
                                        }
                                    }.orEmpty(),
                                    relatedArtists = relatedArtists,
                                    collaboratorConnections = collaboratorConnections,
                                    currentTrack = currentTrack,
                                    searchQuery = searchQuery,
                                    onSearchChange = { searchQuery = it },
                                    onOpenSearch = {
                                        if (searchQuery.isNotBlank()) rememberSearch(searchQuery)
                                        section = DesktopSection.Search
                                    },
                                    libraryPaths = libraryState.sourcePaths,
                                    trackCount = tracks.size,
                                    isScanning = libraryState.isScanning,
                                    onChooseFolder = chooseFolder,
                                    onRescan = {
                                        libraryState.sourcePaths
                                            .takeIf { it.isNotEmpty() }
                                            ?.map(Path::of)
                                            ?.let(::scanFolders)
                                    },
                                    onBack = {
                                        libraryTab = DesktopLibraryTab.Artists
                                        section = DesktopSection.Library
                                    },
                                    onOpenAlbum = { album ->
                                        selectedAlbumKey = desktopAlbumKey(album.artist, album.title)
                                        section = DesktopSection.AlbumDetail
                                    },
                                    onOpenRelatedArtist = { relatedArtist ->
                                        selectedArtistName = relatedArtist.name
                                        section = DesktopSection.ArtistDetail
                                    },
                                    onOpenArtistByName = { artistName ->
                                        selectedArtistName = artistName
                                        section = DesktopSection.ArtistDetail
                                    },
                                    onPlayArtist = { artistTracks ->
                                        artistTracks.firstOrNull()?.let { playTrack(it, queue = artistTracks, label = selectedArtist?.name ?: "Artist") }
                                        section = DesktopSection.NowPlaying
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks.filter { selectedArtist?.name in it.artistCredits }, label = selectedArtist?.name ?: "Artist")
                                        section = DesktopSection.NowPlaying
                                    },
                                    onUpdateAbout = { artistName, about ->
                                        updateArtistProfile(artistName = artistName, about = about)
                                    },
                                    onChooseArtistPhoto = { artistName ->
                                        chooseDesktopArtistImageFile()?.let { photoPath ->
                                            updateArtistProfile(artistName = artistName, photoPath = photoPath.toString())
                                        }
                                    },
                                    onClearArtistPhoto = { artistName ->
                                        updateArtistProfile(artistName = artistName, photoPath = null)
                                    },
                                    trackMenu = trackMenuModel,
                                )
                            }
                        }
                    }
                    if (currentTrack != null && section != DesktopSection.NowPlaying) {
                        DesktopMiniPlayer(
                            track = currentTrack,
                            isPlaying = playbackState.isPlaying,
                            progress = if (currentDurationMs == 0L) 0f else playbackState.positionMs.toFloat() / currentDurationMs.toFloat(),
                            positionMs = playbackState.positionMs,
                            onOpenNowPlaying = { section = DesktopSection.NowPlaying },
                            onPrevious = { stepToTrack(-1) },
                            onPlayPause = {
                                if (playbackState.trackId != currentTrack.id) {
                                    playTrack(currentTrack, queue = activeQueue, label = queueLabel)
                                } else {
                                    playbackController.togglePlayPause()
                                }
                            },
                            onNext = { stepToTrack(1) },
                        )
                    }
                }
            }
            editingTrack?.let { track ->
                DesktopTrackEditDialog(
                    track = track,
                    onDismiss = { editingTrack = null },
                    onSave = { title, artist, album, genre ->
                        saveTrackMetadataOverride(track, title, artist, album, genre)
                    },
                )
            }
            deletingTrack?.let { track ->
                AlertDialog(
                    onDismissRequest = { deletingTrack = null },
                    title = { Text("Delete from device", fontFamily = FontFamily.SansSerif) },
                    text = {
                        Text(
                            "Delete ${track.title} by ${track.artist} from this Mac? This removes the real file from storage.",
                            fontFamily = FontFamily.SansSerif,
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { deleteTrackFromDevice(track) }) {
                            Text("Delete", fontFamily = FontFamily.SansSerif, color = Color(0xFFFF8E8E))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deletingTrack = null }) {
                            Text("Cancel", fontFamily = FontFamily.SansSerif)
                        }
                    },
                )
            }
            manualLyricsTrack?.let { track ->
                DesktopManualLyricsDialog(
                    track = track,
                    repository = lyricsRepository,
                    onDismiss = { manualLyricsTrack = null },
                    onApply = { payload -> applyManualLyrics(track, payload) },
                )
            }
        }
    }
}

@Composable
private fun DesktopSidebar(
    items: List<DesktopSidebarItem>,
    collapsed: Boolean,
    onToggleCollapsed: () -> Unit,
    queueLabel: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxHeight(),
        color = Color.Black,
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (collapsed) 8.dp else 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!collapsed) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = "VerseFlow",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                        )
                        Text(
                            text = "macOS player",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
                }
                IconButton(onClick = onToggleCollapsed) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = if (collapsed) "Expand sidebar" else "Collapse sidebar",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Spacer(modifier = Modifier.height(if (collapsed) 0.dp else 2.dp))
            items.forEach { item ->
                val itemColor by animateColorAsState(
                    targetValue = if (item.selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "sidebarItem",
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (item.selected) VerseBlue.copy(alpha = 0.16f) else Color.Transparent)
                        .clickable(onClick = item.onClick)
                        .padding(horizontal = if (collapsed) 8.dp else 10.dp, vertical = 8.dp),
                    horizontalArrangement = if (collapsed) Arrangement.Center else Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = itemColor,
                    )
                    if (!collapsed) {
                        Text(
                            text = item.title,
                            color = itemColor,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (!collapsed) {
                Text(
                    text = "Queue: $queueLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun DesktopTopBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    libraryPaths: List<String>,
    trackCount: Int,
    isScanning: Boolean,
    onChooseFolder: () -> Unit,
    compactMode: Boolean,
    compactTitle: String?,
    compactActions: @Composable RowScope.() -> Unit = {},
    onRescan: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(220)),
    ) {
        if (compactMode) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                shape = RectangleShape,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = compactTitle.orEmpty(),
                        color = FrostWhite,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = compactActions,
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                        )
                    },
                    label = { Text("Search your Mac library") },
                )
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Open search page",
                        tint = VerseBlue,
                    )
                }
                Surface(
                    color = Color(0xFF11182A),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                        Text(
                            text = "Mac shell ready",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
                }
                PrimaryChip(
                    label = if (libraryPaths.isEmpty()) {
                        "Choose Folders"
                    } else if (isScanning) {
                        "Scanning..."
                    } else {
                        "Rescan"
                    },
                    onClick = if (libraryPaths.isEmpty()) onChooseFolder else onRescan,
                )
                Surface(
                    color = DeepSpace,
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(
                        text = when {
                            libraryPaths.isEmpty() -> "No folders selected"
                            libraryPaths.size == 1 -> libraryPaths.first().substringAfterLast('/').ifBlank { "$trackCount tracks" }
                            else -> "${libraryPaths.size} folders • $trackCount tracks"
                        },
                        color = FrostWhite,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.SansSerif,
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopSearch(
    query: String,
    recentSearches: List<String>,
    tracks: List<DesktopTrack>,
    albums: List<DesktopAlbumSummary>,
    artists: List<DesktopArtistSummary>,
    playlists: List<DesktopPlaylistSummary>,
    genres: List<DesktopGenreSummary>,
    currentTrack: DesktopTrack?,
    trackMenu: DesktopTrackMenuModel,
    libraryPaths: List<String>,
    trackCount: Int,
    isScanning: Boolean,
    onChooseFolder: () -> Unit,
    onRescan: () -> Unit,
    onQueryChange: (String) -> Unit,
    onUseRecentSearch: (String) -> Unit,
    onPlayTrack: (DesktopTrack) -> Unit,
    onOpenAlbum: (DesktopAlbumSummary) -> Unit,
    onOpenArtist: (DesktopArtistSummary) -> Unit,
    onOpenPlaylist: (DesktopPlaylistSummary) -> Unit,
    onPlayGenre: (DesktopGenreSummary) -> Unit,
) {
    val searchListState = rememberLazyListState()
    val isSearchChromeCollapsed = searchListState.firstVisibleItemIndex > 0 || searchListState.firstVisibleItemScrollOffset > 8
    val filteredTracks = remember(query, tracks) {
        tracks.filter { track ->
            query.isBlank() ||
                track.title.contains(query, ignoreCase = true) ||
                track.artist.contains(query, ignoreCase = true) ||
                track.album.contains(query, ignoreCase = true)
        }.take(10)
    }
    val filteredAlbums = remember(query, albums) {
        albums.filter { album ->
            query.isBlank() ||
                album.title.contains(query, ignoreCase = true) ||
                album.artist.contains(query, ignoreCase = true) ||
                album.genre.contains(query, ignoreCase = true)
        }.take(8)
    }
    val filteredArtists = remember(query, artists) {
        artists.filter { artist ->
            query.isBlank() ||
                artist.name.contains(query, ignoreCase = true) ||
                artist.genres.any { genre -> genre.contains(query, ignoreCase = true) }
        }.take(8)
    }
    val filteredPlaylists = remember(query, playlists) {
        playlists.filter { playlist ->
            query.isBlank() ||
                playlist.title.contains(query, ignoreCase = true) ||
                playlist.subtitle.contains(query, ignoreCase = true) ||
                playlist.supporting.contains(query, ignoreCase = true)
        }.take(8)
    }
    val filteredGenres = remember(query, genres) {
        genres.filter { genre ->
            query.isBlank() || genre.title.contains(query, ignoreCase = true)
        }.take(8)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isSearchChromeCollapsed) 0.dp else 14.dp),
    ) {
        DesktopTopBar(
            searchQuery = query,
            onSearchChange = onQueryChange,
            onOpenSearch = {},
            libraryPaths = libraryPaths,
            trackCount = trackCount,
            isScanning = isScanning,
            onChooseFolder = onChooseFolder,
            compactMode = isSearchChromeCollapsed,
            compactTitle = "Search",
            compactActions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search library",
                        tint = VerseBlue,
                    )
                }
            },
            onRescan = onRescan,
        )
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = SurfaceGlass,
            shape = RectangleShape,
        ) {
            LazyColumn(
                state = searchListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (query.isBlank()) {
                    if (recentSearches.isNotEmpty()) {
                        item { SectionLabel("Recent searches") }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(recentSearches) { recentSearch ->
                                    SecondaryChip(label = recentSearch, onClick = { onUseRecentSearch(recentSearch) })
                                }
                            }
                        }
                    }
                    item { SectionLabel("Explore genres") }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(genres.take(10)) { genre ->
                                DesktopFeatureCard(
                                    title = genre.title,
                                    subtitle = "Genre",
                                    supporting = "${genre.trackCount} songs",
                                    palette = genre.palette,
                                    artworkBytes = genre.artworkBytes,
                                    onClick = { onPlayGenre(genre) },
                                )
                            }
                        }
                    }
                } else if (
                    filteredTracks.isEmpty() &&
                    filteredAlbums.isEmpty() &&
                    filteredArtists.isEmpty() &&
                    filteredPlaylists.isEmpty() &&
                    filteredGenres.isEmpty()
                ) {
                    item {
                        EmptyDesktopPanel(
                            title = "No results",
                            body = "VerseFlow could not find any songs, albums, artists, playlists, or genres that match \"$query\".",
                        )
                    }
                } else {
                    if (filteredTracks.isNotEmpty()) {
                        item { SectionLabel("Songs") }
                        items(filteredTracks) { track ->
                            TrackRow(
                                track = track,
                                selected = track.id == currentTrack?.id,
                                trackMenu = trackMenu,
                                onClick = { onPlayTrack(track) },
                            )
                        }
                    }
                    if (filteredAlbums.isNotEmpty()) {
                        item { SectionLabel("Albums") }
                        items(filteredAlbums) { album ->
                            DesktopCollectionRow(
                                title = album.title,
                                subtitle = album.artist,
                                supporting = "${album.trackCount} songs • ${album.genre}",
                                palette = album.palette,
                                artworkBytes = album.artworkBytes,
                                onClick = { onOpenAlbum(album) },
                            )
                        }
                    }
                    if (filteredArtists.isNotEmpty()) {
                        item { SectionLabel("Artists") }
                        items(filteredArtists) { artist ->
                            DesktopCollectionRow(
                                title = artist.name,
                                subtitle = displayGenreLabel(artist.genres.firstOrNull().orEmpty()),
                                supporting = "${artist.trackCount} songs in library",
                                palette = artist.palette,
                                artworkBytes = artist.artworkBytes,
                                artworkLabel = "",
                                onClick = { onOpenArtist(artist) },
                            )
                        }
                    }
                    if (filteredPlaylists.isNotEmpty()) {
                        item { SectionLabel("Playlists") }
                        items(filteredPlaylists) { playlist ->
                            DesktopCollectionRow(
                                title = playlist.title,
                                subtitle = playlist.subtitle,
                                supporting = playlist.supporting,
                                palette = playlist.palette,
                                artworkBytes = playlist.artworkBytes,
                                onClick = { onOpenPlaylist(playlist) },
                            )
                        }
                    }
                    if (filteredGenres.isNotEmpty()) {
                        item { SectionLabel("Genres") }
                        items(filteredGenres) { genre ->
                            DesktopCollectionRow(
                                title = genre.title,
                                subtitle = "Genre",
                                supporting = "${genre.trackCount} songs in library",
                                palette = genre.palette,
                                artworkBytes = genre.artworkBytes,
                                onClick = { onPlayGenre(genre) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopHome(
    name: String,
    tracks: List<DesktopTrack>,
    currentTrack: DesktopTrack?,
    albums: List<DesktopAlbumSummary>,
    recentTracks: List<DesktopTrack>,
    trendingTracks: List<DesktopTrack>,
    playlists: List<DesktopPlaylistSummary>,
    artists: List<DesktopArtistSummary>,
    artistSpotlightOrder: List<String>,
    genres: List<Pair<String, Int>>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    libraryPaths: List<String>,
    trackCount: Int,
    isScanning: Boolean,
    libraryRootPath: String?,
    errorMessage: String?,
    onChooseFolder: () -> Unit,
    onRescan: () -> Unit,
    onHomeScrolledChange: (Boolean) -> Unit,
    onOpenNowPlaying: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenAlbum: (DesktopAlbumSummary) -> Unit,
    onPlayTrack: (DesktopTrack) -> Unit,
    onPlayCollection: (List<DesktopTrack>, String) -> Unit,
    onArtistSpotlightOrderChange: (List<String>) -> Unit,
    onOpenArtist: (DesktopArtistSummary) -> Unit,
    onOpenPlaylist: (DesktopPlaylistSummary) -> Unit,
) {
    val homeListState = rememberLazyListState()
    val homeCardSpacing = 2.dp
    val isHomeChromeCollapsed = homeListState.firstVisibleItemIndex > 0 || homeListState.firstVisibleItemScrollOffset > 8
    var weatherSummary by remember { mutableStateOf<String?>(null) }
    var showArtistSpotlightEditor by remember { mutableStateOf(false) }
    var spotlightDraft by remember { mutableStateOf<List<String>>(emptyList()) }
    val continueListeningTracks = remember(currentTrack, recentTracks) {
        (listOfNotNull(currentTrack) + recentTracks)
            .distinctBy(DesktopTrack::id)
            .take(8)
    }
    val orderedArtists = remember(artists, artistSpotlightOrder) {
        orderDesktopArtistSpotlights(
            artists = artists,
            artistSpotlightOrder = artistSpotlightOrder,
        )
    }
    val moodRailItems = remember(tracks, recentTracks, genres, currentTrack) {
        buildDesktopMoodRail(
            tracks = tracks,
            currentTrack = currentTrack,
            recentTracks = recentTracks,
            featuredGenres = genres,
        )
    }
    val listeningScene = remember(tracks, currentTrack, recentTracks, genres, weatherSummary) {
        buildDesktopListeningScene(
            tracks = tracks,
            currentTrack = currentTrack,
            recentTracks = recentTracks,
            featuredGenres = genres,
            weatherSummary = weatherSummary,
        )
    }

    LaunchedEffect(homeListState) {
        snapshotFlow { isHomeChromeCollapsed }
            .distinctUntilChanged()
            .collect { scrolled -> onHomeScrolledChange(scrolled) }
    }

    LaunchedEffect(Unit) {
        weatherSummary = loadDesktopWeatherSummary()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isHomeChromeCollapsed) 0.dp else 14.dp),
    ) {
        DesktopTopBar(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            onOpenSearch = onOpenSearch,
            libraryPaths = libraryPaths,
            trackCount = trackCount,
            isScanning = isScanning,
            onChooseFolder = onChooseFolder,
            compactMode = isHomeChromeCollapsed,
            compactTitle = "Home",
            compactActions = {
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search library",
                        tint = VerseBlue,
                    )
                }
            },
            onRescan = onRescan,
        )
        LazyColumn(
            state = homeListState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                if (tracks.isEmpty()) {
                    HeroPanel(
                        title = "Welcome, $name",
                        subtitle = when {
                            isScanning -> "VerseFlow is scanning your Mac music folders and preparing your desktop library."
                            errorMessage != null -> errorMessage
                            libraryRootPath == null -> "Choose one or more music folders on your Mac to import local songs into VerseFlow Desktop."
                            else -> "No supported audio files were found in the selected folders yet. Pick another folder or add music files."
                        },
                        ctaPrimary = "Choose Folders",
                        ctaSecondary = "Open Lyrics",
                        onPrimary = onChooseFolder,
                        onSecondary = onOpenLyrics,
                    )
                } else {
                    HeroPanel(
                        title = "${desktopGreetingForNow()}, $name",
                        subtitle = "Your Mac library is now inside VerseFlow. Home highlights where to resume, what fits the moment, and who is shaping your rotation right now.",
                        ctaPrimary = "Open Player",
                        ctaSecondary = "View Lyrics",
                        onPrimary = onOpenNowPlaying,
                        onSecondary = onOpenLyrics,
                    )
                }
            }
            if (tracks.isNotEmpty()) {
                listeningScene?.let { scene ->
                    item {
                        DesktopListeningSceneCard(
                            scene = scene,
                            weatherSummary = weatherSummary,
                            onPlay = { onPlayCollection(scene.tracks, scene.title) },
                        )
                    }
                }
                if (continueListeningTracks.isNotEmpty()) {
                    item {
                        HomeSectionHeader(
                            title = "Continue listening",
                            actionLabel = currentTrack?.let { "Resume current" },
                            onAction = currentTrack?.let { { onPlayTrack(it) } },
                        )
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                            items(continueListeningTracks) { track ->
                                DesktopFeatureCard(
                                    title = track.title,
                                    subtitle = track.artist,
                                    supporting = "${track.album} • ${formatDuration(track.durationMs)}",
                                    palette = track.palette,
                                    artworkBytes = track.artworkBytes,
                                    badgeText = if (currentTrack?.id == track.id) "LIVE" else null,
                                    badgeHighlighted = currentTrack?.id == track.id,
                                    onClick = { onPlayTrack(track) },
                                )
                            }
                        }
                    }
                }
                if (moodRailItems.isNotEmpty()) {
                    item {
                        SectionLabel("Mood rail")
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                            items(moodRailItems) { moodItem ->
                                DesktopFeatureCard(
                                    title = moodItem.title,
                                    subtitle = moodItem.subtitle,
                                    supporting = moodItem.supporting,
                                    palette = moodItem.palette,
                                    artworkBytes = moodItem.artworkBytes,
                                    onClick = { onPlayCollection(moodItem.tracks, moodItem.title) },
                                )
                            }
                        }
                    }
                }
                item {
                    SectionLabel("Featured albums")
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                        items(albums) { album ->
                            DesktopFeatureCard(
                                title = album.title,
                                subtitle = album.artist,
                                supporting = "${album.trackCount} songs • ${album.genre}",
                                palette = album.palette,
                                artworkBytes = album.artworkBytes,
                                onClick = { onOpenAlbum(album) },
                            )
                        }
                    }
                }
                if (recentTracks.isNotEmpty()) {
                    item {
                        SectionLabel("Recently played")
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                            items(recentTracks) { track ->
                                DesktopFeatureCard(
                                    title = track.title,
                                    subtitle = track.artist,
                                    supporting = "${track.album} • ${formatDuration(track.durationMs)}",
                                    palette = track.palette,
                                    artworkBytes = track.artworkBytes,
                                    onClick = { onPlayTrack(track) },
                                )
                            }
                        }
                    }
                }
                if (trendingTracks.isNotEmpty()) {
                    item {
                        SectionLabel("Trending tracks")
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                            items(trendingTracks) { track ->
                                DesktopFeatureCard(
                                    title = track.title,
                                    subtitle = track.artist,
                                    supporting = "${track.album} • ${track.genre}",
                                    palette = track.palette,
                                    artworkBytes = track.artworkBytes,
                                    onClick = { onPlayTrack(track) },
                                )
                            }
                        }
                    }
                }
                if (playlists.isNotEmpty()) {
                    item {
                        SectionLabel("Favorite playlists")
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                            items(playlists) { playlist ->
                                DesktopFeatureCard(
                                    title = playlist.title,
                                    subtitle = playlist.subtitle,
                                    supporting = playlist.supporting,
                                    palette = playlist.palette,
                                    artworkBytes = playlist.artworkBytes,
                                    badgeText = if (playlist.id == FavoritesPlaylistId) "${playlist.tracks.size}" else null,
                                    badgeIcon = if (playlist.id == FavoritesPlaylistId) Icons.Rounded.Favorite else null,
                                    badgeHighlighted = playlist.id == FavoritesPlaylistId,
                                    onClick = { onOpenPlaylist(playlist) },
                                )
                            }
                        }
                    }
                }
                if (artists.isNotEmpty()) {
                    item {
                        HomeSectionHeader(
                            title = "Artists in rotation",
                            actionLabel = "Reorder",
                            onAction = {
                                spotlightDraft = orderedArtists.map(DesktopArtistSummary::name)
                                showArtistSpotlightEditor = true
                            },
                        )
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                            items(orderedArtists) { artist ->
                                DesktopFeatureCard(
                                    title = artist.name,
                                    subtitle = displayGenreLabel(artist.genres.firstOrNull().orEmpty()),
                                    supporting = "${artist.trackCount} songs in library",
                                    palette = artist.palette,
                                    artworkBytes = artist.artworkBytes,
                                    artworkLabel = "",
                                    onClick = { onOpenArtist(artist) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showArtistSpotlightEditor) {
        AlertDialog(
            onDismissRequest = { showArtistSpotlightEditor = false },
            title = { Text("Reorder artist spotlight", fontFamily = FontFamily.SansSerif) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    spotlightDraft.forEachIndexed { index, artistName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = artistName,
                                color = FrostWhite,
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.weight(1f),
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = {
                                        if (index > 0) {
                                            spotlightDraft = spotlightDraft.toMutableList().also { draft ->
                                                val item = draft.removeAt(index)
                                                draft.add(index - 1, item)
                                            }
                                        }
                                    },
                                ) {
                                    Text("Up", fontFamily = FontFamily.SansSerif)
                                }
                                TextButton(
                                    onClick = {
                                        if (index < spotlightDraft.lastIndex) {
                                            spotlightDraft = spotlightDraft.toMutableList().also { draft ->
                                                val item = draft.removeAt(index)
                                                draft.add(index + 1, item)
                                            }
                                        }
                                    },
                                ) {
                                    Text("Down", fontFamily = FontFamily.SansSerif)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onArtistSpotlightOrderChange(spotlightDraft)
                        showArtistSpotlightEditor = false
                    },
                ) {
                    Text("Save", fontFamily = FontFamily.SansSerif)
                }
            },
            dismissButton = {
                TextButton(onClick = { showArtistSpotlightEditor = false }) {
                    Text("Cancel", fontFamily = FontFamily.SansSerif)
                }
            },
        )
    }
}

@Composable
private fun DesktopLibrary(
    tracks: List<DesktopTrack>,
    albums: List<DesktopAlbumSummary>,
    artists: List<DesktopArtistSummary>,
    playlists: List<DesktopPlaylistSummary>,
    genres: List<DesktopGenreSummary>,
    selectedTab: DesktopLibraryTab,
    onTabSelect: (DesktopLibraryTab) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    libraryPaths: List<String>,
    trackCount: Int,
    currentTrack: DesktopTrack?,
    isScanning: Boolean,
    errorMessage: String?,
    albumsViewMode: DesktopCollectionViewMode,
    artistsViewMode: DesktopCollectionViewMode,
    albumsSortMode: DesktopAlbumSortMode,
    onAlbumsSortModeChange: (DesktopAlbumSortMode) -> Unit,
    onAlbumsViewModeChange: (DesktopCollectionViewMode) -> Unit,
    onArtistsViewModeChange: (DesktopCollectionViewMode) -> Unit,
    onAlbumsGridScrolledChange: (Boolean) -> Unit,
    onChooseFolder: () -> Unit,
    onRescan: () -> Unit,
    onSelectTrack: (DesktopTrack, List<DesktopTrack>, String) -> Unit,
    onPlayCollection: (List<DesktopTrack>) -> Unit,
    onOpenPlaylist: (DesktopPlaylistSummary) -> Unit,
    onOpenAlbum: (DesktopAlbumSummary) -> Unit,
    onOpenArtist: (DesktopArtistSummary) -> Unit,
    onCreatePlaylist: () -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    val albumTrackMap = remember(tracks) { tracks.groupBy { desktopAlbumKey(it.albumArtist, it.album) } }
    val genreTrackMap = remember(tracks) { tracks.groupBy { it.genre.ifBlank { "Unclassified" } } }
    val favoritesPlaylist = remember(playlists) {
        playlists.firstOrNull { it.id == FavoritesPlaylistId }
    }
    val albumGridState = rememberLazyGridState()
    val artistsGridState = rememberLazyGridState()
    val songsListState = rememberLazyListState()
    val albumsListState = rememberLazyListState()
    val artistsListState = rememberLazyListState()
    val favouritesListState = rememberLazyListState()
    val playlistsListState = rememberLazyListState()
    val genresListState = rememberLazyListState()

    val filteredTracks = remember(searchQuery, tracks) {
        val matchingTracks = if (searchQuery.isBlank()) {
            tracks
        } else {
            tracks.filter { track ->
                track.title.contains(searchQuery, ignoreCase = true) ||
                    track.artist.contains(searchQuery, ignoreCase = true) ||
                    track.album.contains(searchQuery, ignoreCase = true)
            }
        }
        matchingTracks.sortedWith(
            compareBy<DesktopTrack>(
                { it.title.lowercase() },
                { it.artist.lowercase() },
                { it.album.lowercase() },
            ),
        )
    }
    val filteredAlbums = remember(searchQuery, albums, albumsSortMode) {
        val matchingAlbums = if (searchQuery.isBlank()) {
            albums
        } else {
            albums.filter { album ->
                album.title.contains(searchQuery, ignoreCase = true) ||
                    album.artist.contains(searchQuery, ignoreCase = true) ||
                    album.genre.contains(searchQuery, ignoreCase = true)
            }
        }
        when (albumsSortMode) {
            DesktopAlbumSortMode.DateAdded -> matchingAlbums.sortedWith(
                compareByDescending<DesktopAlbumSummary> { it.newestAddedAtMs }
                    .thenBy { it.title.lowercase() },
            )
            DesktopAlbumSortMode.Alphabetical -> matchingAlbums.sortedBy { it.title.lowercase() }
            DesktopAlbumSortMode.MostSongs -> matchingAlbums.sortedWith(
                compareByDescending<DesktopAlbumSummary> { it.trackCount }
                    .thenBy { it.title.lowercase() },
            )
        }
    }
    val filteredArtists = remember(searchQuery, artists) {
        if (searchQuery.isBlank()) {
            artists
        } else {
            artists.filter { artist ->
                artist.name.contains(searchQuery, ignoreCase = true) ||
                    artist.genres.any { genre -> genre.contains(searchQuery, ignoreCase = true) }
            }
        }
    }
    val filteredPlaylists = remember(searchQuery, playlists) {
        val matchingPlaylists = if (searchQuery.isBlank()) {
            playlists
        } else {
            playlists.filter { playlist ->
                playlist.title.contains(searchQuery, ignoreCase = true) ||
                    playlist.subtitle.contains(searchQuery, ignoreCase = true) ||
                    playlist.supporting.contains(searchQuery, ignoreCase = true)
            }
        }
        matchingPlaylists.sortedWith(
            compareBy<DesktopPlaylistSummary>(
                { !it.isSystemPlaylist },
                { !it.isUserCreated },
                { it.title.lowercase() },
            ),
        )
    }
    val filteredFavoriteTracks = remember(searchQuery, favoritesPlaylist?.tracks) {
        val sourceTracks = favoritesPlaylist?.tracks.orEmpty()
        val matchingTracks = if (searchQuery.isBlank()) {
            sourceTracks
        } else {
            sourceTracks.filter { track ->
                track.title.contains(searchQuery, ignoreCase = true) ||
                    track.artist.contains(searchQuery, ignoreCase = true) ||
                    track.album.contains(searchQuery, ignoreCase = true)
            }
        }
        matchingTracks.sortedWith(
            compareBy<DesktopTrack>(
                { it.title.lowercase() },
                { it.artist.lowercase() },
                { it.album.lowercase() },
            ),
        )
    }
    val filteredGenres = remember(searchQuery, genres) {
        if (searchQuery.isBlank()) {
            genres
        } else {
            genres.filter { genre ->
                genre.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val isLibraryChromeCollapsed = when (selectedTab) {
        DesktopLibraryTab.Songs -> songsListState.firstVisibleItemIndex > 0 || songsListState.firstVisibleItemScrollOffset > 8
        DesktopLibraryTab.Albums -> if (albumsViewMode == DesktopCollectionViewMode.Grid) {
            albumGridState.firstVisibleItemIndex > 0 || albumGridState.firstVisibleItemScrollOffset > 8
        } else {
            albumsListState.firstVisibleItemIndex > 0 || albumsListState.firstVisibleItemScrollOffset > 8
        }
        DesktopLibraryTab.Artists -> if (artistsViewMode == DesktopCollectionViewMode.Grid) {
            artistsGridState.firstVisibleItemIndex > 0 || artistsGridState.firstVisibleItemScrollOffset > 8
        } else {
            artistsListState.firstVisibleItemIndex > 0 || artistsListState.firstVisibleItemScrollOffset > 8
        }
        DesktopLibraryTab.Favourites -> favouritesListState.firstVisibleItemIndex > 0 || favouritesListState.firstVisibleItemScrollOffset > 8
        DesktopLibraryTab.Playlists -> playlistsListState.firstVisibleItemIndex > 0 || playlistsListState.firstVisibleItemScrollOffset > 8
        DesktopLibraryTab.Genres -> genresListState.firstVisibleItemIndex > 0 || genresListState.firstVisibleItemScrollOffset > 8
    }

    LaunchedEffect(selectedTab, albumsViewMode, artistsViewMode) {
        onAlbumsGridScrolledChange(false)
    }

    LaunchedEffect(selectedTab, albumsViewMode, artistsViewMode, songsListState, albumsListState, albumGridState, artistsGridState, artistsListState, favouritesListState, playlistsListState, genresListState) {
        snapshotFlow { isLibraryChromeCollapsed }
            .distinctUntilChanged()
            .collect { scrolled -> onAlbumsGridScrolledChange(scrolled) }
    }

    val focusPanel = remember(
        selectedTab,
        albumsViewMode,
        isLibraryChromeCollapsed,
        currentTrack,
        filteredTracks,
        filteredAlbums,
        filteredArtists,
        filteredFavoriteTracks,
        filteredPlaylists,
        filteredGenres,
        albumTrackMap,
        genreTrackMap,
    ) {
        when (selectedTab) {
            DesktopLibraryTab.Songs -> {
                val focusTrack = currentTrack ?: filteredTracks.firstOrNull()
                DesktopFocusPanel(
                    title = focusTrack?.title ?: "Nothing selected",
                    subtitle = focusTrack?.let { "${it.artist} • ${it.album}" } ?: "Pick a song from the Songs tab to focus it here.",
                    body = focusTrack?.let {
                        "${it.genre} • ${formatDuration(it.durationMs)} • local file"
                    } ?: "VerseFlow keeps the current song visible here while you browse the rest of the desktop library.",
                    palette = focusTrack?.palette ?: listOf(VerseBlue, AuroraCyan),
                    artworkBytes = focusTrack?.artworkBytes,
                    actionLabel = focusTrack?.let { "Play song" },
                    onAction = focusTrack?.let { { onPlayCollection(listOf(it)) } },
                )
            }
            DesktopLibraryTab.Albums -> {
                if (albumsViewMode == DesktopCollectionViewMode.Grid) {
                    null
                } else {
                    val album = filteredAlbums.firstOrNull()
                    DesktopFocusPanel(
                        title = album?.title ?: "No albums found",
                        subtitle = album?.artist ?: "Try a different search or rescan another folder.",
                        body = album?.let {
                            "${it.trackCount} songs • ${formatDuration(it.durationMs)} • ${it.genre}"
                        } ?: "Albums are grouped from your imported local tags just like the Android app flow.",
                        palette = album?.palette ?: listOf(VerseBlue, AuroraCyan),
                        artworkBytes = album?.artworkBytes,
                        actionLabel = album?.let { "Open album" },
                        onAction = album?.let { { onOpenAlbum(it) } },
                    )
                }
            }
            DesktopLibraryTab.Artists -> {
                if (artistsViewMode == DesktopCollectionViewMode.Grid) {
                    null
                } else {
                    val artist = filteredArtists.firstOrNull()
                    DesktopFocusPanel(
                        title = artist?.name ?: "No artists found",
                        subtitle = artist?.genres?.let(::displayGenreLabels) ?: "Local library artist",
                        body = artist?.let {
                            "${it.trackCount} songs ready for playback from this artist."
                        } ?: "Artist groups come straight from the imported tag metadata.",
                        palette = artist?.palette ?: listOf(VerseBlue, AuroraCyan),
                        artworkBytes = artist?.artworkBytes,
                        actionLabel = artist?.let { "Open artist" },
                        onAction = artist?.let { { onOpenArtist(it) } },
                    )
                }
            }
            DesktopLibraryTab.Favourites -> {
                DesktopFocusPanel(
                    title = favoritesPlaylist?.title ?: "Favourites",
                    subtitle = favoritesPlaylist?.subtitle ?: "Your liked songs live here.",
                    body = if (filteredFavoriteTracks.isEmpty()) {
                        "Like songs anywhere in VerseFlow and they will appear in Favourites automatically."
                    } else {
                        "${filteredFavoriteTracks.size} liked songs ready for playback."
                    },
                    palette = favoritesPlaylist?.palette ?: listOf(VerseBlue, AuroraCyan),
                    artworkBytes = favoritesPlaylist?.artworkBytes,
                    actionLabel = filteredFavoriteTracks.takeIf { it.isNotEmpty() }?.let { "Play favourites" },
                    onAction = filteredFavoriteTracks.takeIf { it.isNotEmpty() }?.let { tracks ->
                        { onPlayCollection(tracks) }
                    },
                )
            }
            DesktopLibraryTab.Playlists -> {
                val playlist = filteredPlaylists.firstOrNull()
                DesktopFocusPanel(
                    title = playlist?.title ?: "No playlists found",
                    subtitle = playlist?.subtitle ?: "Create your own playlists or open a smart mix.",
                    body = playlist?.supporting ?: "Desktop playlists now support your own saved mixes alongside the smart sets built from listening behavior.",
                    palette = playlist?.palette ?: listOf(VerseBlue, AuroraCyan),
                    artworkBytes = playlist?.artworkBytes,
                    actionLabel = playlist?.let { "Open playlist" },
                    onAction = playlist?.let { { onOpenPlaylist(it) } },
                )
            }
            DesktopLibraryTab.Genres -> {
                val genre = filteredGenres.firstOrNull()
                val actionTracks = genre?.let { genreTrackMap[it.title].orEmpty() }
                DesktopFocusPanel(
                    title = genre?.title ?: "No genres found",
                    subtitle = genre?.let { "${it.trackCount} songs in library" } ?: "Import more tagged files for richer genre browsing.",
                    body = genre?.let {
                        "Genre views are assembled from the metadata inside your local music files."
                    } ?: "Genre sections depend on the tags already embedded in your music files.",
                    palette = genre?.palette ?: listOf(VerseBlue, AuroraCyan),
                    artworkBytes = genre?.artworkBytes,
                    actionLabel = genre?.let { "Play genre" },
                    onAction = actionTracks?.let { { onPlayCollection(it) } },
                )
            }
        }
    }

    val showFocusPanel = focusPanel != null
    val libraryChromeVisible = !isLibraryChromeCollapsed

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isLibraryChromeCollapsed) 0.dp else 14.dp),
    ) {
        DesktopTopBar(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            onOpenSearch = onOpenSearch,
            libraryPaths = libraryPaths,
            trackCount = trackCount,
            isScanning = isScanning,
            onChooseFolder = onChooseFolder,
            compactMode = isLibraryChromeCollapsed,
            compactTitle = selectedTab.title,
            compactActions = {
                if (selectedTab == DesktopLibraryTab.Albums) {
                    DesktopAlbumsHeaderActions(
                        sortMode = albumsSortMode,
                        onSortModeChange = onAlbumsSortModeChange,
                        viewMode = albumsViewMode,
                        onToggleViewMode = {
                            onAlbumsViewModeChange(
                                if (albumsViewMode == DesktopCollectionViewMode.List) {
                                    DesktopCollectionViewMode.Grid
                                } else {
                                    DesktopCollectionViewMode.List
                                },
                            )
                        },
                    )
                } else if (selectedTab == DesktopLibraryTab.Artists) {
                    DesktopArtistsHeaderActions(
                        viewMode = artistsViewMode,
                        onToggleViewMode = {
                            onArtistsViewModeChange(
                                if (artistsViewMode == DesktopCollectionViewMode.List) {
                                    DesktopCollectionViewMode.Grid
                                } else {
                                    DesktopCollectionViewMode.List
                                },
                            )
                        },
                    )
                }
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search library",
                        tint = VerseBlue,
                    )
                }
            },
            onRescan = onRescan,
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Card(
                modifier = Modifier.weight(if (showFocusPanel) 1.2f else 1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
                shape = RectangleShape,
            ) {
                val libraryCardPadding by animateDpAsState(
                    targetValue = if (
                        (selectedTab == DesktopLibraryTab.Albums && albumsViewMode == DesktopCollectionViewMode.Grid) ||
                        (selectedTab == DesktopLibraryTab.Artists && artistsViewMode == DesktopCollectionViewMode.Grid)
                    ) 0.dp else 22.dp,
                    label = "libraryCardPadding",
                )
                val libraryCardTopPadding by animateDpAsState(
                    targetValue = if (isLibraryChromeCollapsed) 0.dp else libraryCardPadding,
                    label = "libraryCardTopPadding",
                )
                Column(
                    modifier = Modifier.fillMaxSize().padding(
                        start = libraryCardPadding,
                        end = libraryCardPadding,
                        top = libraryCardTopPadding,
                        bottom = libraryCardPadding,
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                AnimatedVisibility(
                    visible = libraryChromeVisible,
                    enter = fadeIn(animationSpec = tween(220)) + expandVertically(animationSpec = tween(260)),
                    exit = fadeOut(animationSpec = tween(160)) + shrinkVertically(animationSpec = tween(220)),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SectionLabel("Library")
                            if (selectedTab == DesktopLibraryTab.Albums) {
                                DesktopAlbumsHeaderActions(
                                    sortMode = albumsSortMode,
                                    onSortModeChange = onAlbumsSortModeChange,
                                    viewMode = albumsViewMode,
                                    onToggleViewMode = {
                                        onAlbumsViewModeChange(
                                            if (albumsViewMode == DesktopCollectionViewMode.List) {
                                                DesktopCollectionViewMode.Grid
                                            } else {
                                                DesktopCollectionViewMode.List
                                            },
                                        )
                                    },
                                )
                            } else if (selectedTab == DesktopLibraryTab.Artists) {
                                DesktopArtistsHeaderActions(
                                    viewMode = artistsViewMode,
                                    onToggleViewMode = {
                                        onArtistsViewModeChange(
                                            if (artistsViewMode == DesktopCollectionViewMode.List) {
                                                DesktopCollectionViewMode.Grid
                                            } else {
                                                DesktopCollectionViewMode.List
                                            },
                                        )
                                    },
                                )
                            }
                        }
                        LibraryTabBar(
                            selectedTab = selectedTab,
                            onTabSelect = onTabSelect,
                        )
                    }
                }
                AnimatedVisibility(
                    visible = libraryChromeVisible && selectedTab == DesktopLibraryTab.Playlists,
                    enter = fadeIn(animationSpec = tween(220)) + expandVertically(animationSpec = tween(260)),
                    exit = fadeOut(animationSpec = tween(160)) + shrinkVertically(animationSpec = tween(220)),
                ) {
                    PrimaryChip(label = "New Playlist", onClick = onCreatePlaylist)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                if (tracks.isEmpty()) {
                    EmptyDesktopPanel(
                        title = if (isScanning) "Scanning your Mac library" else "No songs yet",
                        body = errorMessage ?: if (isScanning) {
                            "VerseFlow is importing local files from your selected folders."
                        } else {
                            "Choose one or more folders on your Mac to start building the desktop library."
                        },
                        actionLabel = "Choose Folders",
                        onAction = onChooseFolder,
                    )
                } else {
                    when (selectedTab) {
                        DesktopLibraryTab.Songs -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = songsListState,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(filteredTracks) { track ->
                                    TrackRow(
                                        track = track,
                                        selected = track.id == currentTrack?.id,
                                        trackMenu = trackMenu,
                                        onClick = { onSelectTrack(track, filteredTracks, "Songs") },
                                    )
                                }
                            }
                        }
                        DesktopLibraryTab.Albums -> {
                            if (albumsViewMode == DesktopCollectionViewMode.Grid) {
                                LazyVerticalGrid(
                                    modifier = Modifier.fillMaxSize(),
                                    state = albumGridState,
                                    columns = GridCells.Adaptive(minSize = 132.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    items(filteredAlbums, key = { "${it.artist}::${it.title}" }) { album ->
                                        DesktopAlbumGridTile(
                                            album = album,
                                            selected = currentTrack?.album == album.title && currentTrack.albumArtist == album.artist,
                                            onClick = { onOpenAlbum(album) },
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    state = albumsListState,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    items(filteredAlbums) { album ->
                                        DesktopCollectionRow(
                                            title = album.title,
                                            subtitle = album.artist,
                                            supporting = "${album.trackCount} songs • ${album.genre}",
                                            palette = album.palette,
                                            artworkBytes = album.artworkBytes,
                                            onClick = { onOpenAlbum(album) },
                                        )
                                    }
                                }
                            }
                        }
                        DesktopLibraryTab.Artists -> {
                            if (artistsViewMode == DesktopCollectionViewMode.Grid) {
                                LazyVerticalGrid(
                                    modifier = Modifier.fillMaxSize(),
                                    state = artistsGridState,
                                    columns = GridCells.Adaptive(minSize = 132.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    items(filteredArtists, key = { it.name }) { artist ->
                                        DesktopArtistGridTile(
                                            artist = artist,
                                            selected = currentTrack?.artistCredits?.contains(artist.name) == true,
                                            onClick = { onOpenArtist(artist) },
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize(), state = artistsListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(filteredArtists) { artist ->
                                        DesktopCollectionRow(
                                            title = artist.name,
                                            subtitle = displayGenreLabel(artist.genres.firstOrNull().orEmpty()),
                                            supporting = "${artist.trackCount} songs in library",
                                            palette = artist.palette,
                                            artworkBytes = artist.artworkBytes,
                                            artworkLabel = "",
                                            onClick = { onOpenArtist(artist) },
                                        )
                                    }
                                }
                            }
                        }
                        DesktopLibraryTab.Favourites -> {
                            if (filteredFavoriteTracks.isEmpty()) {
                                EmptyDesktopPanel(
                                    title = "No liked songs yet",
                                    body = "Tap the heart icon on a song in Now Playing and it will show up here automatically.",
                                )
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize(), state = favouritesListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(filteredFavoriteTracks) { track ->
                                        TrackRow(
                                            track = track,
                                            selected = track.id == currentTrack?.id,
                                            trackMenu = trackMenu,
                                            onClick = { onSelectTrack(track, filteredFavoriteTracks, "Favourites") },
                                        )
                                    }
                                }
                            }
                        }
                        DesktopLibraryTab.Playlists -> LazyColumn(modifier = Modifier.fillMaxSize(), state = playlistsListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filteredPlaylists) { playlist ->
                                DesktopCollectionRow(
                                    title = playlist.title,
                                    subtitle = playlist.subtitle,
                                    supporting = playlist.supporting,
                                    palette = playlist.palette,
                                    artworkBytes = playlist.artworkBytes,
                                    badgeText = if (playlist.id == FavoritesPlaylistId) "${playlist.tracks.size}" else null,
                                    badgeIcon = if (playlist.id == FavoritesPlaylistId) Icons.Rounded.Favorite else null,
                                    badgeHighlighted = playlist.id == FavoritesPlaylistId,
                                    onClick = { onOpenPlaylist(playlist) },
                                )
                            }
                        }
                        DesktopLibraryTab.Genres -> LazyColumn(modifier = Modifier.fillMaxSize(), state = genresListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filteredGenres) { genre ->
                                DesktopCollectionRow(
                                    title = genre.title,
                                    subtitle = "Genre",
                                    supporting = "${genre.trackCount} songs in library",
                                    palette = genre.palette,
                                    artworkBytes = genre.artworkBytes,
                                    onClick = { onPlayCollection(genreTrackMap[genre.title].orEmpty()) },
                                )
                            }
                        }
                    }
                }
                }
            }
            }
            if (focusPanel != null) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF171C28)),
                )
                Card(
                    modifier = Modifier.weight(0.9f).fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF090B12)),
                    shape = RectangleShape,
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        SectionLabel("Current focus")
                        if (focusPanel.title == "Nothing selected" && focusPanel.onAction == null) {
                            EmptyDesktopPanel(
                                title = "Nothing selected",
                                body = "Pick a song from the left and VerseFlow will focus it here for desktop playback and lyrics.",
                            )
                        } else {
                            DesktopArtworkPanel(
                                palette = focusPanel.palette,
                                artworkBytes = focusPanel.artworkBytes,
                                label = focusPanel.title.take(1),
                                modifier = Modifier.fillMaxWidth().height(260.dp),
                            )
                            Text(
                                text = focusPanel.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = focusPanel.subtitle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = focusPanel.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (focusPanel.actionLabel != null && focusPanel.onAction != null) {
                                PrimaryChip(
                                    label = focusPanel.actionLabel,
                                    onClick = focusPanel.onAction,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopPlayQueue(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    libraryPaths: List<String>,
    trackCount: Int,
    isScanning: Boolean,
    onChooseFolder: () -> Unit,
    onRescan: () -> Unit,
    queueLabel: String,
    tracks: List<DesktopTrack>,
    currentTrack: DesktopTrack?,
    onPlayTrack: (DesktopTrack) -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    val queueListState = rememberLazyListState()
    val isQueueChromeCollapsed = queueListState.firstVisibleItemIndex > 0 || queueListState.firstVisibleItemScrollOffset > 8

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isQueueChromeCollapsed) 0.dp else 14.dp),
    ) {
        DesktopTopBar(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            onOpenSearch = onOpenSearch,
            libraryPaths = libraryPaths,
            trackCount = trackCount,
            isScanning = isScanning,
            onChooseFolder = onChooseFolder,
            compactMode = isQueueChromeCollapsed,
            compactTitle = "Play Queue",
            compactActions = {
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search library",
                        tint = VerseBlue,
                    )
                }
            },
            onRescan = onRescan,
        )
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = SurfaceGlass,
            shape = RectangleShape,
        ) {
            LazyColumn(
                state = queueListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    AnimatedVisibility(
                        visible = !isQueueChromeCollapsed,
                        enter = fadeIn(animationSpec = tween(220)) + expandVertically(animationSpec = tween(260)),
                        exit = fadeOut(animationSpec = tween(160)) + shrinkVertically(animationSpec = tween(220)),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SectionLabel("Play Queue")
                            Text(
                                text = queueLabel,
                                style = MaterialTheme.typography.headlineMedium,
                                color = FrostWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = if (tracks.isEmpty()) {
                                    "No tracks are queued yet."
                                } else {
                                    "${tracks.size} songs lined up for playback."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedLavender,
                            )
                        }
                    }
                }
                if (tracks.isEmpty()) {
                    item {
                        EmptyDesktopPanel(
                            title = "Queue is empty",
                            body = "Play something from Home, Library, an album, an artist, or a playlist and it will appear here.",
                        )
                    }
                } else {
                    itemsIndexed(tracks) { index, track ->
                        TrackRow(
                            track = track,
                            selected = track.id == currentTrack?.id,
                            trackMenu = trackMenu,
                            onClick = { onPlayTrack(track) },
                            indexLabel = "${index + 1}",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopPlayHistory(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    libraryPaths: List<String>,
    trackCount: Int,
    isScanning: Boolean,
    onChooseFolder: () -> Unit,
    onRescan: () -> Unit,
    historyEntries: List<DesktopPlayHistoryEntry>,
    tracks: List<DesktopTrack>,
    currentTrack: DesktopTrack?,
    onPlayTrack: (DesktopTrack) -> Unit,
    onClearHistory: () -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    val historyListState = rememberLazyListState()
    val isHistoryChromeCollapsed = historyListState.firstVisibleItemIndex > 0 || historyListState.firstVisibleItemScrollOffset > 8
    val trackByPath = remember(tracks) { tracks.associateBy(DesktopTrack::path) }
    val groupedHistory = remember(historyEntries) { historyEntries.toHistoryDaySections() }
    val historyCards = remember(historyEntries) { historyEntries.toHistorySummaryCards() }
    val topArtists = remember(historyEntries) { historyEntries.topHistoryArtists() }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isHistoryChromeCollapsed) 0.dp else 14.dp),
    ) {
        DesktopTopBar(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            onOpenSearch = onOpenSearch,
            libraryPaths = libraryPaths,
            trackCount = trackCount,
            isScanning = isScanning,
            onChooseFolder = onChooseFolder,
            compactMode = isHistoryChromeCollapsed,
            compactTitle = "Play History",
            compactActions = {
                if (historyEntries.isNotEmpty()) {
                    IconButton(onClick = { showClearHistoryDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Clear play history",
                            tint = VerseBlue,
                        )
                    }
                }
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search library",
                        tint = VerseBlue,
                    )
                }
            },
            onRescan = onRescan,
        )
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = SurfaceGlass,
            shape = RectangleShape,
        ) {
            LazyColumn(
                state = historyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    AnimatedVisibility(
                        visible = !isHistoryChromeCollapsed,
                        enter = fadeIn(animationSpec = tween(220)) + expandVertically(animationSpec = tween(260)),
                        exit = fadeOut(animationSpec = tween(160)) + shrinkVertically(animationSpec = tween(220)),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SectionLabel("Play History")
                                if (historyEntries.isNotEmpty()) {
                                    IconButton(onClick = { showClearHistoryDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = "Clear play history",
                                            tint = VerseBlue,
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Songs you listened to, grouped by day with listening totals and top artists.",
                                color = MutedLavender,
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }
                    }
                }

                if (historyEntries.isEmpty()) {
                    item {
                        EmptyDesktopPanel(
                            title = "No listening history yet",
                            body = "Play a song in VerseFlow Desktop and it will start showing up here by date.",
                        )
                    }
                } else {
                    item {
                        DesktopHistorySummaryGrid(cards = historyCards)
                    }
                    if (topArtists.isNotEmpty()) {
                        item {
                            SectionLabel("Top artists")
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(topArtists) { (artistName, plays) ->
                                    DesktopHistoryArtistCard(
                                        artistName = artistName,
                                        plays = plays,
                                    )
                                }
                            }
                        }
                    }
                    items(groupedHistory) { section ->
                        DesktopHistoryDayCard(
                            section = section,
                            trackByPath = trackByPath,
                            currentTrack = currentTrack,
                            onPlayTrack = onPlayTrack,
                            trackMenu = trackMenu,
                        )
                    }
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear play history", fontFamily = FontFamily.SansSerif) },
            text = {
                Text(
                    "Delete the current listening history from VerseFlow Desktop? This clears the timeline and resets the history stats.",
                    fontFamily = FontFamily.SansSerif,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showClearHistoryDialog = false
                    onClearHistory()
                }) {
                    Text("Clear", fontFamily = FontFamily.SansSerif, color = Color(0xFFFF8E8E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", fontFamily = FontFamily.SansSerif)
                }
            },
        )
    }
}

@Composable
private fun DesktopHistorySummaryGrid(
    cards: List<DesktopHistorySummaryCard>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        cards.chunked(2).forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowCards.forEach { card ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF11151F),
                        shape = RectangleShape,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = card.title,
                                color = MutedLavender,
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                text = card.value,
                                color = FrostWhite,
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                text = card.supporting,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.SansSerif,
                            )
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
private fun DesktopHistoryArtistCard(
    artistName: String,
    plays: Int,
) {
    Surface(
        color = Color(0xFF101727),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = artistName,
                color = FrostWhite,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.SansSerif,
            )
            Text(
                text = if (plays == 1) "1 play" else "$plays plays",
                color = VerseBlue,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }
}

@Composable
private fun DesktopHistoryDayCard(
    section: DesktopPlayHistoryDaySection,
    trackByPath: Map<String, DesktopTrack>,
    currentTrack: DesktopTrack?,
    onPlayTrack: (DesktopTrack) -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    val dayEntries = section.entries
    val totalDurationMs = dayEntries.sumOf(DesktopPlayHistoryEntry::listenedMs)
    val albumCount = dayEntries.map(DesktopPlayHistoryEntry::album).distinct().size
    val artistCount = dayEntries.map(DesktopPlayHistoryEntry::artist).distinct().size

    Surface(
        color = Color(0xFF0F131D),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = formatHistoryDayTitle(section.date),
                        color = FrostWhite,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.SansSerif,
                    )
                    Text(
                        text = "${dayEntries.size} plays • ${formatDurationLong(totalDurationMs)} • $albumCount albums • $artistCount artists",
                        color = MutedLavender,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.SansSerif,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                dayEntries.forEach { entry ->
                    DesktopHistoryTrackRow(
                        entry = entry,
                        liveTrack = trackByPath[entry.trackPath],
                        selected = currentTrack?.path == entry.trackPath,
                        onPlayTrack = onPlayTrack,
                        trackMenu = trackMenu,
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopHistoryTrackRow(
    entry: DesktopPlayHistoryEntry,
    liveTrack: DesktopTrack?,
    selected: Boolean,
    onPlayTrack: (DesktopTrack) -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    Surface(
        color = if (selected) VerseBlue.copy(alpha = 0.12f) else Color.Transparent,
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth().let { base ->
            if (liveTrack != null) base.clickable { onPlayTrack(liveTrack) } else base
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DesktopArtworkThumb(
                artworkBytes = liveTrack?.artworkBytes,
                palette = liveTrack?.palette ?: listOf(VerseBlue, AuroraCyan),
                label = entry.album.take(1),
                modifier = Modifier.size(42.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = entry.title,
                    color = FrostWhite,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
                Text(
                    text = "${entry.artist} • ${entry.album}",
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
            }
            Text(
                text = formatHistoryEntryTime(entry.playedAtMs),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.SansSerif,
            )
            if (liveTrack != null) {
                DesktopTrackOverflowMenu(
                    track = liveTrack,
                    menuModel = trackMenu,
                )
            }
        }
    }
}

@Composable
private fun DesktopNowPlaying(
    track: DesktopTrack?,
    isPlaying: Boolean,
    progress: Float,
    positionMs: Long,
    errorMessage: String?,
    isFavorite: Boolean,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: Boolean,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleFavorite: (DesktopTrack) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onOpenLyrics: () -> Unit,
    onChooseFolder: () -> Unit,
) {
    if (track == null) {
        EmptyDesktopPanel(
            title = "No track loaded",
            body = "Choose one or more folders, import songs, then select one from Library to start building the Mac playback flow.",
            actionLabel = "Choose Folders",
            onAction = onChooseFolder,
        )
        return
    }
    var isSeeking by remember(track.id) { mutableStateOf(false) }
    var seekPositionMs by remember(track.id) { mutableFloatStateOf(positionMs.toFloat()) }
    val durationValue = track.durationMs.coerceAtLeast(1L).toFloat()

    LaunchedEffect(track.id, positionMs) {
        if (!isSeeking) {
            seekPositionMs = positionMs.coerceIn(0L, track.durationMs).toFloat()
        }
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        AlbumArtHero(
            track = track,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        Card(
            modifier = Modifier.weight(0.95f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
            shape = RoundedCornerShape(30.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = track.album,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                errorMessage?.let { playbackError ->
                    Text(
                        text = playbackError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFF8E8E),
                    )
                }
                Slider(
                    value = if (isSeeking) seekPositionMs else positionMs.coerceIn(0L, track.durationMs).toFloat(),
                    onValueChange = { nextValue ->
                        isSeeking = true
                        seekPositionMs = nextValue.coerceIn(0f, durationValue)
                    },
                    onValueChangeFinished = {
                        onSeekTo(seekPositionMs.roundToLong())
                        isSeeking = false
                    },
                    valueRange = 0f..durationValue,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = VerseBlue,
                        activeTrackColor = VerseBlue,
                        inactiveTrackColor = Color.White.copy(alpha = 0.12f),
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        formatDuration(
                            if (isSeeking) seekPositionMs.roundToLong() else positionMs,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(formatDuration(track.durationMs), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DesktopPlayerActionChip(
                            icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            label = if (isFavorite) "Favorite" else "Like",
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            selected = isFavorite,
                            onClick = { onToggleFavorite(track) },
                        )
                        DesktopPlayerActionChip(
                            icon = Icons.Rounded.Lyrics,
                            label = "Lyrics",
                            contentDescription = "Open lyrics",
                            selected = false,
                            emphasize = true,
                            onClick = onOpenLyrics,
                        )
                        DesktopPlayerActionChip(
                            icon = Icons.Rounded.Repeat,
                            label = "Repeat",
                            contentDescription = if (isRepeatEnabled) "Disable repeat" else "Enable repeat",
                            selected = isRepeatEnabled,
                            onClick = onToggleRepeat,
                        )
                        DesktopPlayerActionChip(
                            icon = Icons.Rounded.Shuffle,
                            label = "Shuffle",
                            contentDescription = if (isShuffleEnabled) "Disable shuffle" else "Enable shuffle",
                            selected = isShuffleEnabled,
                            onClick = onToggleShuffle,
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onPrevious) {
                            Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous", tint = FrostWhite)
                        }
                        IconButton(onClick = onPlayPause, modifier = Modifier.size(72.dp)) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayArrow,
                                contentDescription = "Play pause",
                                tint = VerseBlue,
                                modifier = Modifier.size(64.dp),
                            )
                        }
                        IconButton(onClick = onNext) {
                            Icon(Icons.Rounded.SkipNext, contentDescription = "Next", tint = FrostWhite)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopPlaylistDetail(
    playlist: DesktopPlaylistSummary?,
    allTracks: List<DesktopTrack>,
    currentTrack: DesktopTrack?,
    onBack: () -> Unit,
    onPlayPlaylist: (DesktopPlaylistSummary) -> Unit,
    onPlayTrack: (DesktopTrack) -> Unit,
    onUpdateTitle: (String, String) -> Unit,
    onUpdateDescription: (String, String) -> Unit,
    onAddTrack: (String, DesktopTrack) -> Unit,
    onRemoveTrack: (String, DesktopTrack) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    if (playlist == null) {
        EmptyDesktopPanel(
            title = "No playlist selected",
            body = "Open a playlist from Home or Library to see its full desktop detail page.",
        )
        return
    }

    var trackSearch by remember(playlist.id) { mutableStateOf("") }
    val availableTracks = remember(playlist.id, trackSearch, allTracks, playlist.tracks) {
        allTracks
            .filterNot { candidate -> playlist.tracks.any { it.path == candidate.path } }
            .filter { candidate ->
                trackSearch.isBlank() ||
                    candidate.title.contains(trackSearch, ignoreCase = true) ||
                    candidate.artist.contains(trackSearch, ignoreCase = true) ||
                    candidate.album.contains(trackSearch, ignoreCase = true)
            }
            .take(12)
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Card(
            modifier = Modifier.weight(0.95f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090B12)),
            shape = RectangleShape,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        if (playlist.isUserCreated) {
                            OutlinedTextField(
                                value = playlist.title,
                                onValueChange = { onUpdateTitle(playlist.id, it) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("Playlist name") },
                            )
                            OutlinedTextField(
                                value = playlist.description,
                                onValueChange = { onUpdateDescription(playlist.id, it) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("Description") },
                            )
                        } else {
                            Text(
                                text = playlist.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = FrostWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = playlist.subtitle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (playlist.isSystemPlaylist) {
                                Text(
                                    text = "System playlist",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = VerseBlue,
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (playlist.isUserCreated) {
                            SecondaryChip(label = "Delete", onClick = { onDeletePlaylist(playlist.id) })
                        }
                        SecondaryChip(label = "Back", onClick = onBack)
                    }
                }

                PlaylistArtworkCollage(
                    tracks = playlist.tracks,
                    palette = playlist.palette,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                )

                Text(
                    text = playlist.supporting,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedLavender,
                )
                if (playlist.tracks.isNotEmpty()) {
                    PrimaryChip(label = "Play playlist", onClick = { onPlayPlaylist(playlist) })
                }
                if (playlist.isUserCreated) {
                    SectionLabel("Add songs")
                    OutlinedTextField(
                        value = trackSearch,
                        onValueChange = { trackSearch = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Search library") },
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(availableTracks) { track ->
                            DesktopCollectionRow(
                                title = track.title,
                                subtitle = track.artist,
                                supporting = "${track.album} • ${formatDuration(track.durationMs)}",
                                palette = track.palette,
                                artworkBytes = track.artworkBytes,
                                onClick = { onAddTrack(playlist.id, track) },
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color(0xFF171C28)),
        )

        Card(
            modifier = Modifier.weight(1.15f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
            shape = RectangleShape,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SectionLabel("Tracks")
                if (playlist.tracks.isEmpty()) {
                    EmptyDesktopPanel(
                        title = "No songs in this playlist yet",
                        body = if (playlist.id == FavoritesPlaylistId) {
                            "Like a song anywhere in VerseFlow and it will appear in Favorites automatically."
                        } else {
                            "Add songs from your library to start filling this playlist."
                        },
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(playlist.tracks) { track ->
                            if (playlist.isUserCreated) {
                                DesktopPlaylistTrackRow(
                                    track = track,
                                    selected = track.id == currentTrack?.id,
                                    onClick = { onPlayTrack(track) },
                                    onRemove = { onRemoveTrack(playlist.id, track) },
                                    trackMenu = trackMenu,
                                )
                            } else {
                                TrackRow(
                                    track = track,
                                    selected = track.id == currentTrack?.id,
                                    trackMenu = trackMenu,
                                    onClick = { onPlayTrack(track) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopAlbumDetail(
    album: DesktopAlbumSummary?,
    tracks: List<DesktopTrack>,
    currentTrack: DesktopTrack?,
    onBack: () -> Unit,
    onOpenArtist: (String) -> Unit,
    onPlayAlbum: (List<DesktopTrack>) -> Unit,
    onPlayTrack: (DesktopTrack) -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    if (album == null) {
        EmptyDesktopPanel(
            title = "No album selected",
            body = "Open an album from Home or Library to see its desktop detail page.",
        )
        return
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Card(
            modifier = Modifier.weight(0.9f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090B12)),
            shape = RectangleShape,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    SectionLabel("Album")
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SecondaryChip(label = "Back", onClick = onBack)
                    }
                }
                DesktopArtworkPanel(
                    palette = album.palette,
                    artworkBytes = album.artworkBytes,
                    label = album.title.take(1),
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                )
                Text(album.title, style = MaterialTheme.typography.headlineLarge, color = FrostWhite)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(splitDesktopArtists(album.artist)) { artistName ->
                        SecondaryChip(label = artistName, onClick = { onOpenArtist(artistName) })
                    }
                }
                Text(
                    text = "${album.trackCount} songs • ${formatDuration(album.durationMs)} • ${album.genre}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedLavender,
                )
                PrimaryChip(label = "Play album", onClick = { onPlayAlbum(tracks) })
            }
        }
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color(0xFF171C28)),
        )

        Card(
            modifier = Modifier.weight(1.1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
            shape = RectangleShape,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SectionLabel("Tracks")
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(tracks) { track ->
                        TrackRow(
                            track = track,
                            selected = track.id == currentTrack?.id,
                            trackMenu = trackMenu,
                            onClick = { onPlayTrack(track) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopArtistDetail(
    artist: DesktopArtistSummary?,
    artistProfileOverride: DesktopArtistProfileOverride?,
    tracks: List<DesktopTrack>,
    albums: List<DesktopAlbumSummary>,
    relatedArtists: List<DesktopArtistSummary>,
    collaboratorConnections: List<DesktopArtistConnectionSummary>,
    currentTrack: DesktopTrack?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    libraryPaths: List<String>,
    trackCount: Int,
    isScanning: Boolean,
    onChooseFolder: () -> Unit,
    onRescan: () -> Unit,
    onBack: () -> Unit,
    onOpenAlbum: (DesktopAlbumSummary) -> Unit,
    onOpenRelatedArtist: (DesktopArtistSummary) -> Unit,
    onOpenArtistByName: (String) -> Unit,
    onPlayArtist: (List<DesktopTrack>) -> Unit,
    onPlayTrack: (DesktopTrack) -> Unit,
    onUpdateAbout: (String, String) -> Unit,
    onChooseArtistPhoto: (String) -> Unit,
    onClearArtistPhoto: (String) -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    if (artist == null) {
        EmptyDesktopPanel(
            title = "No artist selected",
            body = "Open an artist from Home or Library to see the full desktop artist page.",
        )
        return
    }

    val artistContentState = rememberLazyListState()
    val artistAbout = artistProfileOverride?.about.orEmpty()
    var selectedTab by remember(artist.name) { mutableStateOf(DesktopArtistDetailTab.TopTracks) }
    val featureTracks = remember(artist.name, tracks) {
        tracks.filter { track ->
            artist.name in track.artistCredits &&
                track.artistCredits.firstOrNull() != artist.name
        }
    }
    val isArtistChromeCollapsed =
        artistContentState.firstVisibleItemIndex > 0 || artistContentState.firstVisibleItemScrollOffset > 8

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isArtistChromeCollapsed) 0.dp else 14.dp),
    ) {
        DesktopTopBar(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            onOpenSearch = onOpenSearch,
            libraryPaths = libraryPaths,
            trackCount = trackCount,
            isScanning = isScanning,
            onChooseFolder = onChooseFolder,
            compactMode = isArtistChromeCollapsed,
            compactTitle = artist.name,
            compactActions = {
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search library",
                        tint = VerseBlue,
                    )
                }
            },
            onRescan = onRescan,
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Surface(
                modifier = Modifier.weight(0.92f).fillMaxHeight(),
                color = Color(0xFF090B12),
                shape = RectangleShape,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    AnimatedVisibility(
                        visible = !isArtistChromeCollapsed,
                        enter = fadeIn(animationSpec = tween(220)) + expandVertically(animationSpec = tween(260)),
                        exit = fadeOut(animationSpec = tween(160)) + shrinkVertically(animationSpec = tween(220)),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SectionLabel("Artist")
                            SecondaryChip(label = "Back", onClick = onBack)
                        }
                    }
                    DesktopArtworkPanel(
                        palette = artist.palette,
                        artworkBytes = artist.artworkBytes,
                        label = "",
                        modifier = Modifier.fillMaxWidth().height(280.dp),
                    )
                    Text(artist.name, style = MaterialTheme.typography.headlineLarge, color = FrostWhite)
                    Text(
                        text = "${artist.trackCount} songs • ${displayGenreLabels(artist.genres)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MutedLavender,
                    )
                    Text(
                        text = "${albums.size} albums • ${featureTracks.size} features • ${collaboratorConnections.size} collaborators",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedLavender,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PrimaryChip(label = "Play artist", onClick = { onPlayArtist(tracks) })
                        SecondaryChip(label = "Change photo", onClick = { onChooseArtistPhoto(artist.name) })
                        if (artist.artworkBytes != null || !artistProfileOverride?.photoPath.isNullOrBlank()) {
                            SecondaryChip(label = "Clear photo", onClick = { onClearArtistPhoto(artist.name) })
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF171C28)),
            )
            Surface(
                modifier = Modifier.weight(1.08f).fillMaxHeight(),
                color = SurfaceGlass,
                shape = RectangleShape,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AnimatedVisibility(
                        visible = !isArtistChromeCollapsed,
                        enter = fadeIn(animationSpec = tween(220)) + expandVertically(animationSpec = tween(260)),
                        exit = fadeOut(animationSpec = tween(160)) + shrinkVertically(animationSpec = tween(220)),
                    ) {
                        SectionLabel(artist.name)
                    }
                    ArtistDetailTabRow(
                        selectedTab = selectedTab,
                        onTabChange = { selectedTab = it },
                    )
                    LazyColumn(
                        state = artistContentState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        when (selectedTab) {
                            DesktopArtistDetailTab.TopTracks -> {
                                if (tracks.isEmpty()) {
                                    item {
                                        EmptyDesktopPanel(
                                            title = "No tracks for this artist",
                                            body = "VerseFlow could not find tracks linked to this artist in your Mac library.",
                                        )
                                    }
                                } else {
                                    itemsIndexed(tracks) { index, track ->
                                        TrackRow(
                                            track = track,
                                            selected = track.id == currentTrack?.id,
                                            indexLabel = (index + 1).toString(),
                                            trackMenu = trackMenu,
                                            onClick = { onPlayTrack(track) },
                                        )
                                    }
                                }
                            }

                            DesktopArtistDetailTab.Albums -> {
                                if (albums.isEmpty()) {
                                    item {
                                        EmptyDesktopPanel(
                                            title = "No albums yet",
                                            body = "This artist does not have album-grouped tracks in your current Mac library scan.",
                                        )
                                    }
                                } else {
                                    items(albums) { album ->
                                        DesktopCollectionRow(
                                            title = album.title,
                                            subtitle = album.artist,
                                            supporting = "${album.trackCount} songs • ${album.genre}",
                                            palette = album.palette,
                                            artworkBytes = album.artworkBytes,
                                            onClick = { onOpenAlbum(album) },
                                        )
                                    }
                                }
                            }

                            DesktopArtistDetailTab.Features -> {
                                if (featureTracks.isEmpty()) {
                                    item {
                                        EmptyDesktopPanel(
                                            title = "No feature appearances",
                                            body = "VerseFlow has not found this artist as a featured guest on other tracks in your Mac library yet.",
                                        )
                                    }
                                } else {
                                    itemsIndexed(featureTracks) { index, track ->
                                        TrackRow(
                                            track = track,
                                            selected = track.id == currentTrack?.id,
                                            indexLabel = (index + 1).toString(),
                                            trackMenu = trackMenu,
                                            onClick = { onPlayTrack(track) },
                                        )
                                    }
                                }
                            }

                            DesktopArtistDetailTab.About -> {
                                item {
                                    SectionLabel("About")
                                }
                                item {
                                    OutlinedTextField(
                                        value = artistAbout,
                                        onValueChange = { onUpdateAbout(artist.name, it) },
                                        modifier = Modifier.fillMaxWidth().height(132.dp),
                                        label = { Text("Artist notes") },
                                    )
                                }
                                if (relatedArtists.isNotEmpty()) {
                                    item {
                                        SectionLabel("Related artists")
                                    }
                                    items(relatedArtists) { relatedArtist ->
                                        DesktopCollectionRow(
                                            title = relatedArtist.name,
                                            subtitle = displayGenreLabel(relatedArtist.genres.firstOrNull().orEmpty()),
                                            supporting = "${relatedArtist.trackCount} songs in library",
                                            palette = relatedArtist.palette,
                                            artworkBytes = relatedArtist.artworkBytes,
                                            onClick = { onOpenRelatedArtist(relatedArtist) },
                                        )
                                    }
                                }
                                item {
                                    SectionLabel("Collaborator map")
                                }
                                if (collaboratorConnections.isEmpty()) {
                                    item {
                                        EmptyDesktopPanel(
                                            title = "No collaborator links yet",
                                            body = "VerseFlow has not found shared track credits with other artists for this profile yet.",
                                        )
                                    }
                                } else {
                                    items(collaboratorConnections) { collaborator ->
                                        DesktopCollaboratorRow(
                                            collaborator = collaborator,
                                            maxSharedTrackCount = collaboratorConnections.maxOf { it.sharedTrackCount },
                                            onClick = { onOpenArtistByName(collaborator.name) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistDetailTabRow(
    selectedTab: DesktopArtistDetailTab,
    onTabChange: (DesktopArtistDetailTab) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(DesktopArtistDetailTab.entries) { tab ->
            if (tab == selectedTab) {
                PrimaryChip(label = tab.title, onClick = { onTabChange(tab) })
            } else {
                SecondaryChip(label = tab.title, onClick = { onTabChange(tab) })
            }
        }
    }
}

@Composable
private fun DesktopCollaboratorRow(
    collaborator: DesktopArtistConnectionSummary,
    maxSharedTrackCount: Int,
    onClick: () -> Unit,
) {
    val fillFraction = if (maxSharedTrackCount <= 0) 0f else {
        (collaborator.sharedTrackCount.toFloat() / maxSharedTrackCount.toFloat()).coerceIn(0f, 1f)
    }

    Surface(
        color = Color(0xFF0D1018),
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = collaborator.name,
                        color = FrostWhite,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.SansSerif,
                    )
                    Text(
                        text = "${collaborator.sharedTrackCount} shared tracks • ${collaborator.sharedAlbumCount} albums",
                        color = MutedLavender,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.SansSerif,
                    )
                }
                Text(
                    text = displayGenreLabels(collaborator.genres),
                    color = MutedLavender,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color.White.copy(alpha = 0.08f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fillFraction)
                        .fillMaxHeight()
                        .background(Brush.horizontalGradient(collaborator.palette)),
                )
            }
        }
    }
}

@Composable
private fun DesktopPlaylistTrackRow(
    track: DesktopTrack,
    selected: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    trackMenu: DesktopTrackMenuModel? = null,
) {
    Surface(
        color = if (selected) VerseBlue.copy(alpha = 0.12f) else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DesktopArtworkThumb(
                artworkBytes = track.artworkBytes,
                palette = track.palette,
                label = track.album.take(1),
                modifier = Modifier.size(48.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(track.title, color = FrostWhite, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${track.artist} • ${track.album}", color = MutedLavender, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            SecondaryChip(label = "Remove", onClick = onRemove)
            trackMenu?.let { model ->
                DesktopTrackOverflowMenu(
                    track = track,
                    menuModel = model,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopMiniPlayer(
    track: DesktopTrack,
    isPlaying: Boolean,
    progress: Float,
    positionMs: Long,
    onOpenNowPlaying: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = VerseBlue,
                trackColor = Color.White.copy(alpha = 0.08f),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenNowPlaying)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DesktopArtworkThumb(
                    artworkBytes = track.artworkBytes,
                    palette = track.palette,
                    label = track.album.take(1),
                    modifier = Modifier.size(52.dp),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = track.title,
                        color = FrostWhite,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${track.artist} • ${formatDuration(positionMs)} / ${formatDuration(track.durationMs)}",
                        color = MutedLavender,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onPrevious) {
                        Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous", tint = FrostWhite)
                    }
                    IconButton(onClick = onPlayPause) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayArrow,
                            contentDescription = "Play pause",
                            tint = VerseBlue,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                    IconButton(onClick = onNext) {
                        Icon(Icons.Rounded.SkipNext, contentDescription = "Next", tint = FrostWhite)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopLyrics(
    track: DesktopTrack?,
    progressMs: Long,
    lyricsStatus: DesktopLyricsLoadState,
    onSeekTo: (Long) -> Unit,
    onBackToPlayer: () -> Unit,
    onOpenManualSearch: () -> Unit,
    onChooseFolder: () -> Unit,
) {
    if (track == null) {
        EmptyDesktopPanel(
            title = "No lyrics source yet",
            body = "Import one or more music folders and select a track first. The desktop lyrics pipeline comes right after local playback.",
            actionLabel = "Choose Folders",
            onAction = onChooseFolder,
        )
        return
    }
    val lyricsListState = rememberLazyListState()
    val activeLyricIndex = remember(track.id, progressMs, track.lyrics) {
        track.lyrics.indexOfLast { line -> progressMs >= line.timestampMs }
            .let { index -> if (index < 0) 0 else index }
    }

    LaunchedEffect(track.id, activeLyricIndex) {
        if (track.lyrics.isNotEmpty()) {
            val targetIndex = (activeLyricIndex - 2).coerceAtLeast(0)
            lyricsListState.animateScrollToItem(targetIndex)
        }
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        shape = RoundedCornerShape(30.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(track.title, style = MaterialTheme.typography.headlineLarge, color = FrostWhite)
                    Text("${track.artist} • ${track.album}", style = MaterialTheme.typography.bodyLarge, color = MutedLavender)
                    track.lyricsAttribution?.let { attribution ->
                        Text(attribution, style = MaterialTheme.typography.bodyMedium, color = VerseBlue)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryChip(label = "Search lyrics", onClick = onOpenManualSearch)
                    Surface(
                        color = VerseBlue.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable(onClick = onBackToPlayer),
                    ) {
                        Text(
                            text = "Back to player",
                            color = VerseBlue,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }
            when {
                lyricsStatus == DesktopLyricsLoadState.Loading -> {
                    EmptyDesktopPanel(
                        title = "Fetching lyrics",
                        body = "VerseFlow is searching local metadata and online lyric sources for this track.",
                    )
                }
                track.lyrics.isNotEmpty() -> {
                    LazyColumn(
                        state = lyricsListState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsIndexed(track.lyrics) { index, line ->
                            val isActive = index == activeLyricIndex
                            Surface(
                                color = if (isActive) VerseBlue.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { onSeekTo(line.timestampMs) },
                            ) {
                                Text(
                                    text = line.text,
                                    color = if (isActive) FrostWhite else MutedLavender,
                                    fontSize = if (isActive) 22.sp else 18.sp,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 16.dp),
                                )
                            }
                        }
                    }
                }
                track.plainLyrics.isNotEmpty() -> {
                    EmptyDesktopPanel(
                        title = "Plain lyrics only",
                        body = "Timing data is unavailable for this track, but VerseFlow found readable lyrics.",
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(track.plainLyrics) { line ->
                            Surface(
                                color = Color.White.copy(alpha = 0.04f),
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Text(
                                    text = line,
                                    color = MutedLavender,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 16.dp),
                                )
                            }
                        }
                    }
                }
                lyricsStatus == DesktopLyricsLoadState.Unavailable -> {
                    EmptyDesktopPanel(
                        title = "No lyrics found",
                        body = "VerseFlow could not find a reliable lyrics match for this Mac track from the current local and online sources.",
                    )
                }
                else -> {
                    EmptyDesktopPanel(
                        title = "Lyrics desktop pass is next",
                        body = "Pick a track and VerseFlow will search for lyrics automatically as the Mac playback flow grows.",
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopTrackEditDialog(
    track: DesktopTrack,
    onDismiss: () -> Unit,
    onSave: (title: String, artist: String, album: String, genre: String) -> Unit,
) {
    var title by remember(track.path) { mutableStateOf(track.title) }
    var artist by remember(track.path) { mutableStateOf(track.artist) }
    var album by remember(track.path) { mutableStateOf(track.album) }
    var genre by remember(track.path) { mutableStateOf(track.genre) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Music Info", fontFamily = FontFamily.SansSerif) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    label = { Text("Song title") },
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    singleLine = true,
                    label = { Text("Artist") },
                )
                OutlinedTextField(
                    value = album,
                    onValueChange = { album = it },
                    singleLine = true,
                    label = { Text("Album") },
                )
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    singleLine = true,
                    label = { Text("Genre") },
                )
                Text(
                    "These edits are VerseFlow-only overrides for the Mac app. The original audio file tags stay untouched.",
                    fontFamily = FontFamily.SansSerif,
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(title, artist, album, genre)
                },
            ) {
                Text("Save", fontFamily = FontFamily.SansSerif)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = FontFamily.SansSerif)
            }
        },
    )
}

@Composable
private fun DesktopManualLyricsDialog(
    track: DesktopTrack,
    repository: DesktopLyricsRepository,
    onDismiss: () -> Unit,
    onApply: (DesktopLyricsPayload) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val dialogState = rememberDialogState(size = DpSize(920.dp, 760.dp))
    val scrollState = rememberScrollState()
    var title by remember(track.path) { mutableStateOf(track.title) }
    var artist by remember(track.path) { mutableStateOf(track.artistCredits.firstOrNull() ?: track.artist) }
    var album by remember(track.path) { mutableStateOf(track.album) }
    var pastedLyrics by remember(track.path) { mutableStateOf("") }
    var isPasteMode by remember(track.path) { mutableStateOf(false) }
    var isSearching by remember(track.path) { mutableStateOf(false) }
    var searchError by remember(track.path) { mutableStateOf<String?>(null) }
    var results by remember(track.path) { mutableStateOf<List<DesktopLyricsSearchCandidate>>(emptyList()) }

    DialogWindow(
        onCloseRequest = onDismiss,
        title = "Search lyrics",
        state = dialogState,
        resizable = true,
    ) {
        Surface(
            color = DeepSpace,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(30.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Search lyrics", color = FrostWhite, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Search manually and save the selected lyrics for this track on your Mac.",
                            color = MutedLavender,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Close", fontFamily = FontFamily.SansSerif)
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Song title") },
                    )
                    OutlinedTextField(
                        value = artist,
                        onValueChange = { artist = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Artist") },
                    )
                    OutlinedTextField(
                        value = album,
                        onValueChange = { album = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Album") },
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PrimaryChip(
                            label = if (isSearching) "Searching..." else "Search now",
                            onClick = {
                                if (!isSearching) {
                                    coroutineScope.launch {
                                        isSearching = true
                                        searchError = null
                                        results = repository.searchCandidates(
                                            title = title,
                                            artistName = artist,
                                            albumTitle = album.takeIf(String::isNotBlank),
                                            durationMs = track.durationMs,
                                        )
                                        if (results.isEmpty()) {
                                            searchError = "No reliable lyrics matches were found for that search."
                                        }
                                        isSearching = false
                                    }
                                }
                            },
                        )
                        Text(
                            "Results are saved locally once you apply one.",
                            color = MutedLavender,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SecondaryChip(
                            label = "Search on web",
                            onClick = { openLyricsWebSearch(title = title, artist = artist) },
                        )
                        SecondaryChip(
                            label = if (isPasteMode) "Hide paste box" else "Paste lyrics",
                            onClick = { isPasteMode = !isPasteMode },
                        )
                        SecondaryChip(
                            label = "Import LRC/TXT",
                            onClick = {
                                chooseDesktopLyricsFile()?.let { path ->
                                    val importedPayload = runCatching {
                                        Files.readString(path)
                                    }.getOrNull()?.let { rawText ->
                                        desktopLyricsPayloadFromRawText(
                                            rawText = rawText,
                                            attribution = "Imported lyrics file",
                                        )
                                    }

                                    if (importedPayload != null) {
                                        onApply(importedPayload)
                                    } else {
                                        searchError = "VerseFlow could not read lyrics from that file."
                                    }
                                }
                            },
                        )
                    }
                    if (isPasteMode) {
                        OutlinedTextField(
                            value = pastedLyrics,
                            onValueChange = { pastedLyrics = it },
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            label = { Text("Paste lyrics or LRC content") },
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PrimaryChip(
                                label = "Apply pasted lyrics",
                                onClick = {
                                    val pastedPayload = desktopLyricsPayloadFromRawText(
                                        rawText = pastedLyrics,
                                        attribution = "Pasted lyrics",
                                    )
                                    if (pastedPayload != null) {
                                        onApply(pastedPayload)
                                    } else {
                                        searchError = "VerseFlow could not parse those pasted lyrics."
                                    }
                                },
                            )
                            Text(
                                "You can paste plain lyrics or timed LRC lyrics here.",
                                color = MutedLavender,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    searchError?.let { message ->
                        Text(
                            message,
                            color = Color(0xFFFFA5A5),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    if (results.isEmpty() && !isSearching) {
                        EmptyDesktopPanel(
                            title = "No manual results yet",
                            body = "Search with a cleaner song title or a primary artist name if your local tags include featured artists.",
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            results.forEach { candidate ->
                                Surface(
                                    color = DeepSpace,
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.fillMaxWidth().clickable { onApply(candidate.payload) },
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                candidate.title,
                                                color = FrostWhite,
                                                style = MaterialTheme.typography.titleMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Text(
                                                "${candidate.typeLabel} • ${candidate.source}",
                                                color = if (candidate.payload.syncedLyrics.isNotEmpty()) VerseBlue else AuroraCyan,
                                                style = MaterialTheme.typography.labelLarge,
                                            )
                                        }
                                        Text(
                                            listOfNotNull(candidate.artist, candidate.album).joinToString(" • "),
                                            color = MutedLavender,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            candidate.payload.plainLyrics.firstOrNull().orEmpty().ifBlank {
                                                candidate.payload.syncedLyrics.firstOrNull()?.text.orEmpty()
                                            },
                                            color = MutedLavender,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopSettings(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    selectedTheme: String,
    themes: List<DesktopThemePreset>,
    onThemeSelect: (String) -> Unit,
    autoRescanEnabled: Boolean,
    onAutoRescanChange: (Boolean) -> Unit,
    musixmatchApiKey: String,
    onMusixmatchApiKeyChange: (String) -> Unit,
    libraryPaths: List<String>,
    trackCount: Int,
    isScanning: Boolean,
    onAddFolders: () -> Unit,
    onFoldersDropped: (List<Path>) -> Unit,
    onRemoveFolder: (String) -> Unit,
    onRescanLibrary: () -> Unit,
) {
    val settingsScrollState = rememberScrollState()
    val isSettingsChromeCollapsed = settingsScrollState.value > 8

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (isSettingsChromeCollapsed) 0.dp else 14.dp),
    ) {
        DesktopTopBar(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            onOpenSearch = onOpenSearch,
            libraryPaths = libraryPaths,
            trackCount = trackCount,
            isScanning = isScanning,
            onChooseFolder = onAddFolders,
            compactMode = isSettingsChromeCollapsed,
            compactTitle = "Settings",
            compactActions = {
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search library",
                        tint = VerseBlue,
                    )
                }
            },
            onRescan = onRescanLibrary,
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(settingsScrollState),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
                shape = RectangleShape,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    SectionLabel("Profile")
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = onDisplayNameChange,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Display name") },
                    )
                    Text(
                        text = "This desktop pass keeps the same personalization direction as Android while we prepare proper shared settings and persistence.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SectionLabel("Library behavior")
                    Surface(
                        color = if (autoRescanEnabled) Color(0xFF101A38) else DeepSpace,
                        shape = RectangleShape,
                        modifier = Modifier.fillMaxWidth().clickable { onAutoRescanChange(!autoRescanEnabled) },
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                if (autoRescanEnabled) "Auto-rescan enabled" else "Auto-rescan disabled",
                                color = FrostWhite,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                "When enabled, VerseFlow refreshes your Mac library again after opening the app and after you change the watched folders.",
                                color = MutedLavender,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    SectionLabel("Lyrics source")
                    OutlinedTextField(
                        value = musixmatchApiKey,
                        onValueChange = onMusixmatchApiKeyChange,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Musixmatch API key (optional)") },
                    )
                    Text(
                        text = "VerseFlow uses LRCLIB and lyrics.ovh by default. Add your Musixmatch key here to enable it as a third lyrics source on Mac.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedLavender,
                    )
                }
            }
            Card(
                modifier = Modifier.weight(1.05f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
                shape = RectangleShape,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    SectionLabel("Theme presets")
                    themes.forEach { preset ->
                        val selected = preset.name == selectedTheme
                        Surface(
                            color = if (selected) Color(0xFF101A38) else DeepSpace,
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxWidth().clickable { onThemeSelect(preset.name) },
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(preset.name, color = FrostWhite, style = MaterialTheme.typography.titleMedium)
                                Text(preset.description, color = MutedLavender, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            Card(
                modifier = Modifier.weight(1.2f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
                shape = RectangleShape,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    SectionLabel("Music folders")
                    Text(
                        text = "VerseFlow can scan one or many folders on your Mac. Add folders here, remove them later, or drag folders in from Finder.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedLavender,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PrimaryChip(
                            label = "Add folders",
                            onClick = onAddFolders,
                        )
                        SecondaryChip(
                            label = if (isScanning) "Scanning..." else "Rescan",
                            onClick = onRescanLibrary,
                        )
                    }
                    DesktopFolderDropZone(onFoldersDropped = onFoldersDropped)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        if (libraryPaths.isEmpty()) {
                            Surface(
                                color = DeepSpace,
                                shape = RectangleShape,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = "No folders selected yet. Add one or more music folders to build the Mac library.",
                                    color = MutedLavender,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        } else {
                            libraryPaths.forEach { path ->
                                Surface(
                                    color = DeepSpace,
                                    shape = RectangleShape,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = path.substringAfterLast('/').ifBlank { path },
                                                color = FrostWhite,
                                                style = MaterialTheme.typography.titleMedium,
                                            )
                                            Text(
                                                text = path,
                                                color = MutedLavender,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                        TextButton(onClick = { onRemoveFolder(path) }) {
                                            Text("Remove")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopFolderDropZone(
    onFoldersDropped: (List<Path>) -> Unit,
) {
    SwingPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp),
        factory = {
            JPanel(BorderLayout()).apply {
                isOpaque = false
                border = BorderFactory.createDashedBorder(java.awt.Color(0, 0, 255, 160), 2f, 6f)
                add(
                    JLabel(
                        "<html><div style='text-align:center;color:#F5F7FF;font-family:sans-serif;'>" +
                            "Drag and drop one or more folders here<br/>" +
                            "<span style='color:#B5BDD6;font-size:11px;'>VerseFlow will add them to your Mac library sources.</span>" +
                            "</div></html>",
                        SwingConstants.CENTER,
                    ),
                    BorderLayout.CENTER,
                )
                dropTarget = object : DropTarget() {
                    override fun drop(event: DropTargetDropEvent) {
                        try {
                            event.acceptDrop(DnDConstants.ACTION_COPY)
                            val droppedPaths = (event.transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<*>)
                                .orEmpty()
                                .mapNotNull { item ->
                                    when (item) {
                                        is java.io.File -> item.toPath()
                                        else -> null
                                    }
                                }
                                .filter(Files::isDirectory)
                            if (droppedPaths.isNotEmpty()) {
                                onFoldersDropped(droppedPaths)
                                event.dropComplete(true)
                            } else {
                                event.dropComplete(false)
                            }
                        } catch (_: Exception) {
                            event.dropComplete(false)
                        }
                    }
                }
            }
        },
        update = {},
    )
}

@Composable
private fun HeroPanel(
    title: String,
    subtitle: String,
    ctaPrimary: String,
    ctaSecondary: String,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        shape = RoundedCornerShape(34.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    color = FrostWhite,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedLavender,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryChip(label = ctaPrimary, onClick = onPrimary)
                    SecondaryChip(label = ctaSecondary, onClick = onSecondary)
                }
            }
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                VerseBlue.copy(alpha = 0.22f),
                                AuroraCyan.copy(alpha = 0.16f),
                                Color.White.copy(alpha = 0.04f),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = FrostWhite,
                    modifier = Modifier.size(84.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeSectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SectionLabel(title)
        if (actionLabel != null && onAction != null) {
            SecondaryChip(label = actionLabel, onClick = onAction)
        }
    }
}

@Composable
private fun DesktopListeningSceneCard(
    scene: DesktopListeningScene,
    weatherSummary: String?,
    onPlay: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        shape = RectangleShape,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DesktopArtworkPanel(
                palette = scene.palette,
                artworkBytes = scene.artworkBytes,
                label = "",
                modifier = Modifier.width(240.dp).height(196.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Listening scene",
                    color = VerseBlue,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.SansSerif,
                )
                Text(
                    text = scene.title,
                    color = FrostWhite,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = scene.subtitle,
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = listOfNotNull(weatherSummary?.takeIf { it.isNotBlank() }, scene.supporting)
                        .joinToString(" • "),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.SansSerif,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryChip(label = "Play scene", onClick = onPlay)
                }
            }
        }
    }
}

@Composable
private fun DesktopFeatureCard(
    title: String,
    subtitle: String,
    supporting: String,
    palette: List<Color>,
    artworkBytes: ByteArray? = null,
    artworkLabel: String = title.take(1),
    badgeText: String? = null,
    badgeIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    badgeHighlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            val artwork = rememberArtworkBitmap(artworkBytes)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Brush.linearGradient(palette)),
            ) {
                if (artwork != null) {
                    Image(
                        bitmap = artwork,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = artworkLabel,
                        color = FrostWhite,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (badgeText != null || badgeIcon != null) {
                    Surface(
                        color = if (badgeHighlighted) VerseBlue.copy(alpha = 0.92f) else Color.Black.copy(alpha = 0.42f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            badgeIcon?.let { icon ->
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = FrostWhite,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                            badgeText?.let { text ->
                                Text(
                                    text = text,
                                    color = FrostWhite,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.SansSerif,
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(title, color = FrostWhite, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(supporting, color = MutedLavender, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun DesktopMetricCard(
    title: String,
    status: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceGlass),
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, color = FrostWhite, style = MaterialTheme.typography.titleMedium)
            Text(status, color = VerseBlue, style = MaterialTheme.typography.labelLarge)
            Text(body, color = MutedLavender, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun LibraryTabBar(
    selectedTab: DesktopLibraryTab,
    onTabSelect: (DesktopLibraryTab) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(DesktopLibraryTab.entries) { tab ->
            Surface(
                color = if (tab == selectedTab) Color(0xFF101A38) else DeepSpace,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.clickable { onTabSelect(tab) },
            ) {
                Text(
                    text = tab.title,
                    color = if (tab == selectedTab) VerseBlue else FrostWhite,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    fontFamily = FontFamily.SansSerif,
                )
            }
        }
    }
}

@Composable
private fun DesktopCollectionRow(
    title: String,
    subtitle: String,
    supporting: String,
    palette: List<Color>,
    artworkBytes: ByteArray?,
    artworkLabel: String = title.take(1),
    badgeText: String? = null,
    badgeIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    badgeHighlighted: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        color = if (badgeHighlighted) Color(0xFF101A38) else DeepSpace,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DesktopArtworkThumb(
                artworkBytes = artworkBytes,
                palette = palette,
                label = artworkLabel,
                modifier = Modifier.size(52.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    color = FrostWhite,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = supporting,
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (badgeText != null || badgeIcon != null) {
                Surface(
                    color = if (badgeHighlighted) VerseBlue else Color(0xFF151A28),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        badgeIcon?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (badgeHighlighted) VerseBlue else FrostWhite,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        badgeText?.let { text ->
                            Text(
                                text = text,
                                color = if (badgeHighlighted) VerseBlue else FrostWhite,
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopAlbumsHeaderActions(
    sortMode: DesktopAlbumSortMode,
    onSortModeChange: (DesktopAlbumSortMode) -> Unit,
    viewMode: DesktopCollectionViewMode,
    onToggleViewMode: () -> Unit,
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            IconButton(onClick = { sortMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Rounded.Sort,
                    contentDescription = "Sort albums",
                    tint = MutedLavender,
                )
            }
            DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false },
            ) {
                DesktopAlbumSortMode.entries.forEach { candidate ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                candidate.label,
                                fontFamily = FontFamily.SansSerif,
                                color = if (candidate == sortMode) VerseBlue else FrostWhite,
                            )
                        },
                        onClick = {
                            onSortModeChange(candidate)
                            sortMenuExpanded = false
                        },
                    )
                }
            }
        }
        IconButton(onClick = onToggleViewMode) {
            Icon(
                imageVector = if (viewMode == DesktopCollectionViewMode.List) Icons.Rounded.GridView else Icons.Rounded.ViewList,
                contentDescription = if (viewMode == DesktopCollectionViewMode.List) "Show album grid" else "Show album list",
                tint = MutedLavender,
            )
        }
    }
}

@Composable
private fun DesktopArtistsHeaderActions(
    viewMode: DesktopCollectionViewMode,
    onToggleViewMode: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onToggleViewMode) {
            Icon(
                imageVector = if (viewMode == DesktopCollectionViewMode.List) Icons.Rounded.GridView else Icons.Rounded.ViewList,
                contentDescription = if (viewMode == DesktopCollectionViewMode.List) "Show artist grid" else "Show artist list",
                tint = MutedLavender,
            )
        }
    }
}

@Composable
private fun TrackRow(
    track: DesktopTrack,
    selected: Boolean,
    indexLabel: String? = null,
    trackMenu: DesktopTrackMenuModel? = null,
    onClick: () -> Unit,
) {
    Surface(
        color = if (selected) VerseBlue.copy(alpha = 0.12f) else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (indexLabel != null) {
                Text(
                    text = indexLabel,
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(18.dp),
                )
            }
            DesktopArtworkThumb(
                artworkBytes = track.artworkBytes,
                palette = track.palette,
                label = track.album.take(1),
                modifier = Modifier.size(48.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(track.title, color = FrostWhite, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${track.artist} • ${track.album}", color = MutedLavender, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(formatDuration(track.durationMs), color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodySmall)
            trackMenu?.let { model ->
                DesktopTrackOverflowMenu(
                    track = track,
                    menuModel = model,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DesktopAlbumGridTile(
    album: DesktopAlbumSummary,
    selected: Boolean,
    onClick: () -> Unit,
) {
    var isHovered by remember(album.title, album.artist) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerMoveFilter(
                onEnter = {
                    isHovered = true
                    false
                },
                onExit = {
                    isHovered = false
                    false
                },
            )
            .clickable(onClick = onClick),
    ) {
        DesktopArtworkPanel(
            palette = album.palette,
            artworkBytes = album.artworkBytes,
            label = album.title.take(1),
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp),
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(VerseBlue.copy(alpha = 0.12f)),
            )
        }
        if (isHovered || selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.62f)),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = album.title,
                    color = FrostWhite,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
                Text(
                    text = album.artist,
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
                Text(
                    text = "${album.trackCount} songs",
                    color = MutedLavender,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
            }
        }
    }
}

@Composable
private fun DesktopTrackOverflowMenu(
    track: DesktopTrack,
    menuModel: DesktopTrackMenuModel,
) {
    var expanded by remember(track.id) { mutableStateOf(false) }
    var showingPlaylistTargets by remember(track.id) { mutableStateOf(false) }
    val isFavorite = track.path in menuModel.favoriteTrackPaths

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "Song actions",
                tint = MutedLavender,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                showingPlaylistTargets = false
            },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = track.title,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                onClick = {},
                enabled = false,
            )
            if (showingPlaylistTargets) {
                DropdownMenuItem(
                    text = { Text("Back", fontFamily = FontFamily.SansSerif) },
                    onClick = { showingPlaylistTargets = false },
                )
                menuModel.userPlaylists.forEach { playlist ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                playlist.title,
                                fontFamily = FontFamily.SansSerif,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        onClick = {
                            menuModel.onAddToPlaylist(playlist.id, track)
                            expanded = false
                            showingPlaylistTargets = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("New playlist", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        menuModel.onCreatePlaylistWithTrack(track)
                        expanded = false
                        showingPlaylistTargets = false
                    },
                )
            } else {
                DropdownMenuItem(
                    text = { Text("Add to playlist", fontFamily = FontFamily.SansSerif) },
                    onClick = { showingPlaylistTargets = true },
                )
                DropdownMenuItem(
                    text = { Text("Add to play queue", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        menuModel.onAddToQueue(track)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            if (isFavorite) "Remove from favourites" else "Add to favourites",
                            fontFamily = FontFamily.SansSerif,
                        )
                    },
                    onClick = {
                        menuModel.onToggleFavorite(track)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Artist", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        menuModel.onOpenArtist(track)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Album", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        menuModel.onOpenAlbum(track)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Edit Music Info", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        menuModel.onEditTrack(track)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Remove from VerseFlow", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        menuModel.onHideTrack(track)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Delete from device", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        menuModel.onDeleteTrack(track)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DesktopArtistGridTile(
    artist: DesktopArtistSummary,
    selected: Boolean,
    onClick: () -> Unit,
) {
    var isHovered by remember(artist.name) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerMoveFilter(
                onEnter = {
                    isHovered = true
                    false
                },
                onExit = {
                    isHovered = false
                    false
                },
            )
            .clickable(onClick = onClick),
    ) {
        DesktopArtworkPanel(
            palette = artist.palette,
            artworkBytes = artist.artworkBytes,
            label = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp),
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(VerseBlue.copy(alpha = 0.12f)),
            )
        }
        if (isHovered || selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.62f)),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = artist.name,
                    color = FrostWhite,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
                Text(
                    text = "${artist.trackCount} songs",
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.SansSerif,
                )
            }
        }
    }
}

@Composable
private fun DesktopArtworkPanel(
    palette: List<Color>,
    artworkBytes: ByteArray?,
    label: String,
    modifier: Modifier = Modifier,
) {
    val artwork = rememberArtworkBitmap(artworkBytes)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.linearGradient(palette)),
        contentAlignment = Alignment.Center,
    ) {
        if (artwork != null) {
            Image(
                bitmap = artwork,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = label,
                color = FrostWhite,
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DesktopAppBackdrop(track: DesktopTrack?) {
    val palette = track?.palette ?: listOf(InkBlack, Color(0xFF060B1B), Color(0xFF0A1226))
    val artwork = rememberArtworkBitmap(track?.artworkBytes)
    val baseTone = palette.firstOrNull() ?: InkBlack
    val midTone = palette.getOrNull(1) ?: Color(0xFF08101F)
    val highlightTone = palette.lastOrNull() ?: AuroraCyan

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        baseTone.copy(alpha = 0.48f),
                        midTone.copy(alpha = 0.28f),
                        InkBlack,
                    ),
                ),
            ),
    ) {
        if (artwork != null) {
            Image(
                bitmap = artwork,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.16f,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            highlightTone.copy(alpha = 0.22f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            highlightTone.copy(alpha = 0.08f),
                            Color.Transparent,
                            baseTone.copy(alpha = 0.12f),
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            InkBlack.copy(alpha = 0.12f),
                            DeepSpace.copy(alpha = 0.28f),
                            InkBlack.copy(alpha = 0.72f),
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun AlbumArtHero(
    track: DesktopTrack,
    modifier: Modifier = Modifier,
) {
    DesktopArtworkPanel(
        palette = track.palette,
        artworkBytes = track.artworkBytes,
        label = track.album.take(1),
        modifier = modifier,
    )
}

@Composable
private fun DesktopPlayerActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    contentDescription: String,
    selected: Boolean,
    emphasize: Boolean = false,
    onClick: () -> Unit,
) {
    val backgroundColor = when {
        selected -> VerseBlue.copy(alpha = 0.22f)
        emphasize -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
        else -> Color.White.copy(alpha = 0.05f)
    }
    val foregroundColor = when {
        selected -> VerseBlue
        emphasize -> MaterialTheme.colorScheme.secondary
        else -> FrostWhite
    }
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .height(46.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = foregroundColor,
            )
            Text(
                text = label,
                color = foregroundColor,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }
}

@Composable
private fun DesktopArtworkThumb(
    artworkBytes: ByteArray?,
    palette: List<Color>,
    label: String,
    modifier: Modifier = Modifier,
) {
    val artwork = rememberArtworkBitmap(artworkBytes)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(palette)),
    ) {
        if (artwork != null) {
            Image(
                bitmap = artwork,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    color = FrostWhite,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun PlaylistArtworkCollage(
    tracks: List<DesktopTrack>,
    palette: List<Color>,
    modifier: Modifier = Modifier,
) {
    val tiles = remember(tracks) { tracks.take(4).ifEmpty { emptyList() } }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(palette)),
    ) {
        if (tiles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "V",
                    color = FrostWhite,
                    fontSize = 104.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                repeat(2) { rowIndex ->
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        repeat(2) { columnIndex ->
                            val tileIndex = (rowIndex * 2) + columnIndex
                            val track = tiles.getOrNull(tileIndex)
                            if (track != null) {
                                DesktopArtworkPanel(
                                    palette = track.palette,
                                    artworkBytes = track.artworkBytes,
                                    label = track.album.take(1),
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color.White.copy(alpha = 0.04f)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberArtworkBitmap(artworkBytes: ByteArray?): ImageBitmap? =
    remember(artworkBytes) {
        artworkBytes?.takeIf(ByteArray::isNotEmpty)?.let { encoded ->
            runCatching { SkiaImage.makeFromEncoded(encoded).toComposeImageBitmap() }.getOrNull()
        }
    }

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = FrostWhite,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun EmptyDesktopPanel(
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(title, color = FrostWhite, style = MaterialTheme.typography.headlineSmall)
            Text(body, color = MutedLavender, style = MaterialTheme.typography.bodyLarge)
            if (actionLabel != null && onAction != null) {
                PrimaryChip(label = actionLabel, onClick = onAction)
            }
        }
    }
}

@Composable
private fun PrimaryChip(
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        color = VerseBlue.copy(alpha = 0.18f),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            color = VerseBlue,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontFamily = FontFamily.SansSerif,
        )
    }
}

@Composable
private fun SecondaryChip(
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            color = FrostWhite,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontFamily = FontFamily.SansSerif,
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}

private fun formatDurationLong(durationMs: Long): String {
    val totalSeconds = (durationMs / 1_000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L
    return when {
        hours > 0L -> "${hours}h ${minutes}m ${seconds}s"
        minutes > 0L -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}

private fun List<DesktopPlayHistoryEntry>.toRecentTrackIds(): List<String> =
    sortedByDescending(DesktopPlayHistoryEntry::playedAtMs)
        .map { stableId(it.trackPath) }
        .distinct()
        .take(24)

private fun List<DesktopPlayHistoryEntry>.toPlayCounts(): Map<String, Int> =
    groupingBy { stableId(it.trackPath) }.eachCount()

private fun List<DesktopPlayHistoryEntry>.toHistorySummaryCards(): List<DesktopHistorySummaryCard> {
    val totalPlays = size
    val uniqueSongs = map(DesktopPlayHistoryEntry::trackPath).distinct().size
    val uniqueAlbums = map { "${it.artist}::${it.album}" }.distinct().size
    val uniqueArtists = map(DesktopPlayHistoryEntry::artist).distinct().size
    val totalDurationMs = sumOf(DesktopPlayHistoryEntry::listenedMs)

    return listOf(
        DesktopHistorySummaryCard("Song plays", totalPlays.toString(), "$uniqueSongs unique songs"),
        DesktopHistorySummaryCard("Hours played", formatDurationLong(totalDurationMs), "Total listening time"),
        DesktopHistorySummaryCard("Albums played", uniqueAlbums.toString(), "Distinct albums heard"),
        DesktopHistorySummaryCard("Artists played", uniqueArtists.toString(), "Distinct artists heard"),
    )
}

private fun List<DesktopPlayHistoryEntry>.topHistoryArtists(limit: Int = 6): List<Pair<String, Int>> =
    groupingBy(DesktopPlayHistoryEntry::artist)
        .eachCount()
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key.lowercase() })
        .take(limit)
        .map { it.key to it.value }

private fun desktopGreetingForNow(now: LocalTime = LocalTime.now()): String =
    when (now.hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Good night"
    }

private fun orderDesktopArtistSpotlights(
    artists: List<DesktopArtistSummary>,
    artistSpotlightOrder: List<String>,
): List<DesktopArtistSummary> {
    val orderedArtists = artistSpotlightOrder
        .mapNotNull { artistName -> artists.firstOrNull { it.name == artistName } }
    return orderedArtists + artists.filterNot { candidate -> orderedArtists.any { it.name == candidate.name } }
}

private fun buildDesktopMoodRail(
    tracks: List<DesktopTrack>,
    currentTrack: DesktopTrack?,
    recentTracks: List<DesktopTrack>,
    featuredGenres: List<Pair<String, Int>>,
): List<DesktopMoodRailItem> {
    if (tracks.isEmpty()) return emptyList()
    val now = LocalTime.now()
    val timeMood = when (now.hour) {
        in 5..10 -> Triple("Sunrise lift", "Warm start", "Ease into the day")
        in 11..15 -> Triple("Midday focus", "Locked in", "Keep the pace steady")
        in 16..19 -> Triple("Golden hour", "Smooth unwind", "Shift into the evening")
        else -> Triple("After hours", "Night pulse", "Lean into darker textures")
    }
    val topGenre = featuredGenres.firstOrNull()?.first?.takeIf { it.isNotBlank() }
    val currentArtist = currentTrack?.artistCredits?.firstOrNull()
    val fromCurrentArtist = currentArtist?.let { artistName ->
        tracks.filter { artistName in it.artistCredits }
    }.orEmpty()
    val fromTopGenre = topGenre?.let { genreName ->
        tracks.filter { it.genre.equals(genreName, ignoreCase = true) }
    }.orEmpty()

    return buildList {
        add(
            DesktopMoodRailItem(
                title = timeMood.first,
                subtitle = timeMood.second,
                supporting = timeMood.third,
                tracks = (recentTracks.ifEmpty { tracks }).take(16),
                palette = (recentTracks.firstOrNull() ?: tracks.first()).palette,
                artworkBytes = recentTracks.firstOrNull()?.artworkBytes ?: tracks.firstOrNull()?.artworkBytes,
            ),
        )
        if (fromTopGenre.isNotEmpty()) {
            add(
                DesktopMoodRailItem(
                    title = topGenre ?: "Genre pulse",
                    subtitle = "Genre pulse",
                    supporting = "${fromTopGenre.size} songs ready now",
                    tracks = fromTopGenre.take(20),
                    palette = fromTopGenre.first().palette,
                    artworkBytes = fromTopGenre.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
                ),
            )
        }
        if (fromCurrentArtist.isNotEmpty()) {
            add(
                DesktopMoodRailItem(
                    title = currentArtist ?: "Artist focus",
                    subtitle = "Artist focus",
                    supporting = "Stay with the artist you just touched",
                    tracks = fromCurrentArtist.take(20),
                    palette = fromCurrentArtist.first().palette,
                    artworkBytes = fromCurrentArtist.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
                ),
            )
        }
        if (recentTracks.isNotEmpty()) {
            add(
                DesktopMoodRailItem(
                    title = "Recent energy",
                    subtitle = "Keep the thread",
                    supporting = "${recentTracks.size} songs from your latest run",
                    tracks = recentTracks.take(20),
                    palette = recentTracks.first().palette,
                    artworkBytes = recentTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
                ),
            )
        }
    }.distinctBy { it.title }
}

private fun buildDesktopListeningScene(
    tracks: List<DesktopTrack>,
    currentTrack: DesktopTrack?,
    recentTracks: List<DesktopTrack>,
    featuredGenres: List<Pair<String, Int>>,
    weatherSummary: String?,
): DesktopListeningScene? {
    if (tracks.isEmpty()) return null
    val now = LocalTime.now()
    val weather = weatherSummary.orEmpty().lowercase()
    val sceneTracks = (listOfNotNull(currentTrack) + recentTracks).distinctBy(DesktopTrack::id).ifEmpty { tracks.take(12) }
    val dominantGenre = featuredGenres.firstOrNull()?.first?.takeIf { it.isNotBlank() }
    val weatherLead = when {
        "rain" in weather || "storm" in weather || "drizzle" in weather -> "Rain-soaked"
        "sun" in weather || "clear" in weather -> "Sunlit"
        "cloud" in weather || "mist" in weather || "fog" in weather -> "Cloudline"
        "wind" in weather -> "Wind-run"
        else -> null
    }
    val timeLead = when (now.hour) {
        in 5..10 -> "Morning"
        in 11..15 -> "Midday"
        in 16..19 -> "Twilight"
        else -> "After-hours"
    }
    val tail = when {
        dominantGenre != null -> dominantGenre
        currentTrack != null -> currentTrack.artist
        else -> "scene"
    }
    return DesktopListeningScene(
        title = listOfNotNull(weatherLead, timeLead, tail.takeIf { it.isNotBlank() }).joinToString(" "),
        subtitle = when {
            currentTrack != null -> "Built around ${currentTrack.title} and the path your listening has been taking."
            recentTracks.isNotEmpty() -> "Built from the songs you have been touching lately."
            else -> "Built from the strongest signals in your local library."
        },
        supporting = listOfNotNull(
            weatherSummary?.takeIf { it.isNotBlank() },
            dominantGenre?.let { "Rooted in $it" },
            sceneTracks.firstOrNull()?.artist?.let { "Leaning toward $it" },
        ).joinToString(" • "),
        tracks = sceneTracks.take(20),
        palette = sceneTracks.first().palette,
        artworkBytes = sceneTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
    )
}

private suspend fun loadDesktopWeatherSummary(): String? = withContext(Dispatchers.IO) {
    runCatching {
        URL("https://wttr.in/?format=3")
            .readText()
            .trim()
            .substringAfter(':', "")
            .trim()
            .takeIf { it.isNotBlank() }
    }.getOrNull()
}

private fun List<DesktopPlayHistoryEntry>.toHistoryDaySections(): List<DesktopPlayHistoryDaySection> =
    groupBy { entry ->
        Instant.ofEpochMilli(entry.playedAtMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
        .entries
        .sortedByDescending { it.key }
        .map { (date, entries) ->
            DesktopPlayHistoryDaySection(
                date = date,
                entries = entries.sortedByDescending(DesktopPlayHistoryEntry::playedAtMs),
            )
        }

private fun formatHistoryDayTitle(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
    }
}

private fun formatHistoryEntryTime(playedAtMs: Long): String =
    Instant.ofEpochMilli(playedAtMs)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("h:mm a"))

private fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor

private fun buildDesktopSmartPlaylists(
    tracks: List<DesktopTrack>,
    recentTrackIds: List<String>,
    playCounts: Map<String, Int>,
): List<DesktopPlaylistSummary> {
    if (tracks.isEmpty()) return emptyList()

    val recentTracks = recentTrackIds.mapNotNull { id -> tracks.firstOrNull { it.id == id } }
    val mostPlayedTracks = tracks
        .sortedWith(
            compareByDescending<DesktopTrack> { playCounts[it.id] ?: 0 }
                .thenBy { it.title.lowercase() },
        )
        .take(12)
    val topGenreTracks = tracks
        .groupBy { it.genre.ifBlank { "Unclassified" } }
        .maxByOrNull { it.value.size }
        ?.value
        .orEmpty()
    val leadArtistTracks = tracks
        .groupBy(DesktopTrack::artist)
        .maxByOrNull { (_, groupedTracks) ->
            groupedTracks.sumOf { playCounts[it.id] ?: 0 }.coerceAtLeast(groupedTracks.size)
        }
        ?.value
        .orEmpty()

    val candidates = buildList {
        if (recentTracks.isNotEmpty()) {
            add(
                DesktopPlaylistSummary(
                    id = "smart_recent_replay",
                    title = "Recent Replay",
                    subtitle = "Your latest desktop spins",
                    description = "A smart mix built from the songs you played most recently on your Mac.",
                    supporting = "${recentTracks.size} songs • ${formatDuration(recentTracks.sumOf(DesktopTrack::durationMs))}",
                    tracks = recentTracks.take(12),
                    palette = recentTracks.first().palette,
                    artworkBytes = recentTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
                ),
            )
        }
        if (mostPlayedTracks.isNotEmpty()) {
            add(
                DesktopPlaylistSummary(
                    id = "smart_top_rotation",
                    title = "Top Rotation",
                    subtitle = "Your most-played local picks",
                    description = "A smart mix based on the songs you return to the most.",
                    supporting = "${mostPlayedTracks.size} songs • ${formatDuration(mostPlayedTracks.sumOf(DesktopTrack::durationMs))}",
                    tracks = mostPlayedTracks,
                    palette = mostPlayedTracks.first().palette,
                    artworkBytes = mostPlayedTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
                ),
            )
        }
        if (topGenreTracks.isNotEmpty()) {
            add(
                DesktopPlaylistSummary(
                    id = "smart_genre_${topGenreTracks.first().genre.lowercase().replace(" ", "_")}",
                    title = "${topGenreTracks.first().genre} Pulse",
                    subtitle = "Built from your library metadata",
                    description = "A smart mix grouped from the strongest genre tags in your local library.",
                    supporting = "${topGenreTracks.size} songs • ${formatDuration(topGenreTracks.sumOf(DesktopTrack::durationMs))}",
                    tracks = topGenreTracks.take(12),
                    palette = topGenreTracks.first().palette,
                    artworkBytes = topGenreTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
                ),
            )
        }
        if (leadArtistTracks.isNotEmpty()) {
            add(
                DesktopPlaylistSummary(
                    id = "smart_artist_${leadArtistTracks.first().artist.lowercase().replace(" ", "_")}",
                    title = "${leadArtistTracks.first().artist} Essentials",
                    subtitle = "Artist-focused quick mix",
                    description = "A smart playlist centered around one of your most-played local artists.",
                    supporting = "${leadArtistTracks.size} songs • ${formatDuration(leadArtistTracks.sumOf(DesktopTrack::durationMs))}",
                    tracks = leadArtistTracks.take(12),
                    palette = leadArtistTracks.first().palette,
                    artworkBytes = leadArtistTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
                ),
            )
        }
    }

    return candidates
        .distinctBy { it.title }
        .filter { it.tracks.isNotEmpty() }
}

private fun buildDesktopFavoritesPlaylist(
    favoriteTrackPaths: List<String>,
    tracks: List<DesktopTrack>,
): DesktopPlaylistSummary {
    val favoriteTracks = favoriteTrackPaths
        .mapNotNull { path -> tracks.firstOrNull { it.path == path } }
        .distinctBy(DesktopTrack::id)

    return DesktopPlaylistSummary(
        id = FavoritesPlaylistId,
        title = "Favourites",
        subtitle = if (favoriteTracks.isEmpty()) {
            "Your liked songs will appear here"
        } else {
            "Built automatically from your likes"
        },
        description = "A permanent VerseFlow playlist that collects every song you like on the Mac app.",
        supporting = "${favoriteTracks.size} songs • ${formatDuration(favoriteTracks.sumOf(DesktopTrack::durationMs))}",
        tracks = favoriteTracks,
        palette = favoriteTracks.firstOrNull()?.palette ?: listOf(Color(0xFF1A0B16), VerseBlue, AuroraCyan),
        artworkBytes = favoriteTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
        isSystemPlaylist = true,
    )
}

private fun buildDesktopUserPlaylists(
    playlists: List<DesktopUserPlaylist>,
    tracks: List<DesktopTrack>,
): List<DesktopPlaylistSummary> =
    playlists.mapNotNull { playlist ->
        val playlistTracks = playlist.trackPaths.mapNotNull { path ->
            tracks.firstOrNull { it.path == path }
        }
        DesktopPlaylistSummary(
            id = playlist.id,
            title = playlist.title,
            subtitle = if (playlist.description.isBlank()) "User playlist" else playlist.description,
            description = playlist.description,
            supporting = "${playlistTracks.size} songs • ${formatDuration(playlistTracks.sumOf(DesktopTrack::durationMs))}",
            tracks = playlistTracks,
            palette = playlistTracks.firstOrNull()?.palette ?: listOf(VerseBlue, AuroraCyan),
            artworkBytes = playlistTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
            isUserCreated = true,
        )
    }

private const val FavoritesPlaylistId = "system_favourites"

private fun summarizeDesktopGenres(tracks: List<DesktopTrack>): List<DesktopGenreSummary> =
    tracks
        .groupBy { it.genre.ifBlank { "Unclassified" } }
        .map { (genre, groupedTracks) ->
            DesktopGenreSummary(
                title = genre,
                trackCount = groupedTracks.size,
                palette = groupedTracks.first().palette,
                artworkBytes = groupedTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
            )
        }
        .sortedWith(
            compareByDescending<DesktopGenreSummary> { it.trackCount }
                .thenBy { it.title.lowercase() },
        )

private fun chooseDesktopFolders(initialDirectory: Path? = null): List<Path> {
    val chooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        dialogTitle = "Choose your music folders"
        isAcceptAllFileFilterUsed = false
        isMultiSelectionEnabled = true
        initialDirectory
            ?.takeIf(Files::exists)
            ?.toFile()
            ?.let { currentDirectory = it }
    }
    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFiles
            ?.map { file -> file.toPath() }
            ?.ifEmpty { chooser.selectedFile?.toPath()?.let(::listOf).orEmpty() }
            .orEmpty()
    } else {
        emptyList()
    }
}

private fun chooseDesktopLyricsFile(): Path? {
    val chooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        dialogTitle = "Import lyrics file"
        isAcceptAllFileFilterUsed = true
        fileFilter = javax.swing.filechooser.FileNameExtensionFilter("Lyrics files", "lrc", "txt")
    }
    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile?.toPath()
    } else {
        null
    }
}

private fun chooseDesktopArtistImageFile(): Path? {
    val chooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        dialogTitle = "Choose artist photo"
        isAcceptAllFileFilterUsed = true
        fileFilter = javax.swing.filechooser.FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "webp")
    }
    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile?.toPath()
    } else {
        null
    }
}

private fun loadDesktopImageBytes(path: String?): ByteArray? =
    path
        ?.trim()
        ?.takeIf(String::isNotEmpty)
        ?.let { imagePath ->
            runCatching {
                val filePath = Path.of(imagePath)
                if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                    Files.readAllBytes(filePath)
                } else {
                    null
                }
            }.getOrNull()
        }

private fun openLyricsWebSearch(title: String, artist: String) {
    val query = listOf(title.trim(), artist.trim(), "lyrics")
        .filter(String::isNotBlank)
        .joinToString(" ")
    runCatching {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(
                URI(
                    "https://www.google.com/search?q=" +
                        java.net.URLEncoder.encode(query, Charsets.UTF_8),
                ),
            )
        }
    }
}
