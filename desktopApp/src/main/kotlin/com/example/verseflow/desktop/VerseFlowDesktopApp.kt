package com.example.verseflow.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Duration
import javax.swing.BorderFactory
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.jetbrains.skia.Image as SkiaImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerMoveFilter
import kotlin.math.roundToLong
import kotlin.random.Random

private val InkBlackBase = Color(0xFF040611)
private val DeepSpaceBase = Color(0xFF070B16)
private val VerseBlueBase = Color(0xFF0000FF)
private val NebulaBlueBase = Color(0xFF6B88FF)
private val AuroraCyanBase = Color(0xFF66F2FF)
private val FrostWhiteBase = Color(0xFFF5F7FF)
private val MutedLavenderBase = Color(0xFFB5BDD6)
private val SurfaceGlassBase = Color(0xFF0B0E17)

private data class DesktopThemeTokens(
    val background: Color,
    val surface: Color,
    val surfaceGlass: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
)

private fun desktopThemeTokensFrom(colorScheme: androidx.compose.material3.ColorScheme) = DesktopThemeTokens(
    background = colorScheme.background,
    surface = colorScheme.surface,
    surfaceGlass = colorScheme.surface.copy(alpha = 0.94f),
    primary = colorScheme.primary,
    secondary = colorScheme.secondary,
    tertiary = colorScheme.tertiary,
    onSurface = colorScheme.onSurface,
    onSurfaceVariant = colorScheme.onSurfaceVariant,
)

private var desktopThemeTokens by mutableStateOf(
    DesktopThemeTokens(
        background = InkBlackBase,
        surface = DeepSpaceBase,
        surfaceGlass = SurfaceGlassBase,
        primary = VerseBlueBase,
        secondary = AuroraCyanBase,
        tertiary = NebulaBlueBase,
        onSurface = FrostWhiteBase,
        onSurfaceVariant = MutedLavenderBase,
    ),
)
private var desktopThemeForArtwork by mutableStateOf("Nebula Dark")

private val InkBlack: Color get() = desktopThemeTokens.background
private val DeepSpace: Color get() = desktopThemeTokens.surface
private val SurfaceGlass: Color get() = desktopThemeTokens.surfaceGlass
private val VerseBlue: Color get() = desktopThemeTokens.primary
private val AuroraCyan: Color get() = desktopThemeTokens.secondary
private val NebulaBlue: Color get() = desktopThemeTokens.tertiary
private val FrostWhite: Color get() = desktopThemeTokens.onSurface
private val MutedLavender: Color get() = desktopThemeTokens.onSurfaceVariant

private fun verseFlowDesktopTypography(): Typography {
    val default = Typography()
    val sharedFontFamily = FontFamily.SansSerif
    return Typography(
        displayLarge = default.displayLarge.copy(fontFamily = sharedFontFamily),
        displayMedium = default.displayMedium.copy(fontFamily = sharedFontFamily),
        displaySmall = default.displaySmall.copy(fontFamily = sharedFontFamily),
        headlineLarge = default.headlineLarge.copy(fontFamily = sharedFontFamily),
        headlineMedium = default.headlineMedium.copy(fontFamily = sharedFontFamily),
        headlineSmall = default.headlineSmall.copy(fontFamily = sharedFontFamily),
        titleLarge = default.titleLarge.copy(fontFamily = sharedFontFamily),
        titleMedium = default.titleMedium.copy(fontFamily = sharedFontFamily),
        titleSmall = default.titleSmall.copy(fontFamily = sharedFontFamily),
        bodyLarge = default.bodyLarge.copy(fontFamily = sharedFontFamily),
        bodyMedium = default.bodyMedium.copy(fontFamily = sharedFontFamily),
        bodySmall = default.bodySmall.copy(fontFamily = sharedFontFamily),
        labelLarge = default.labelLarge.copy(fontFamily = sharedFontFamily),
        labelMedium = default.labelMedium.copy(fontFamily = sharedFontFamily),
        labelSmall = default.labelSmall.copy(fontFamily = sharedFontFamily),
    )
}

private val VerseFlowDesktopColors = darkColorScheme(
    primary = VerseBlueBase,
    secondary = AuroraCyanBase,
    tertiary = NebulaBlueBase,
    background = InkBlackBase,
    surface = DeepSpaceBase,
    surfaceVariant = Color(0xFF12172B),
    onPrimary = FrostWhiteBase,
    onBackground = FrostWhiteBase,
    onSurface = FrostWhiteBase,
    onSurfaceVariant = MutedLavenderBase,
)

private fun desktopThemeNameCompatibility(selectedTheme: String): String =
    when (selectedTheme) {
        "Aurora Glow" -> "Crimson Velvet"
        else -> selectedTheme
    }

private fun isDesktopMonochromeTheme(themeName: String): Boolean =
    when (desktopThemeNameCompatibility(themeName)) {
        "Black & White", "White & Black" -> true
        else -> false
    }

private fun desktopArtworkColorFilter(): ColorFilter? =
    if (isDesktopMonochromeTheme(desktopThemeForArtwork)) {
        ColorFilter.colorMatrix(
            ColorMatrix().apply { setToSaturation(0f) },
        )
    } else {
        null
    }

private fun desktopColorSchemeForTheme(themeName: String) = when (desktopThemeNameCompatibility(themeName)) {
    "Eclipse OLED" -> darkColorScheme(
        primary = Color(0xFF8BB8FF),
        secondary = Color(0xFF8CF6FF),
        tertiary = Color(0xFF6C8DFF),
        background = Color(0xFF010204),
        surface = Color(0xFF05070B),
        surfaceVariant = Color(0xFF0D1118),
        onPrimary = FrostWhiteBase,
        onBackground = FrostWhiteBase,
        onSurface = FrostWhiteBase,
        onSurfaceVariant = Color(0xFFB2BCCB),
    )
    "Crimson Velvet" -> darkColorScheme(
        primary = Color(0xFFFF5A6B),
        secondary = Color(0xFFFFA0B4),
        tertiary = Color(0xFFFF7D86),
        background = Color(0xFF12050A),
        surface = Color(0xFF190911),
        surfaceVariant = Color(0xFF2B111B),
        onPrimary = FrostWhiteBase,
        onBackground = FrostWhiteBase,
        onSurface = FrostWhiteBase,
        onSurfaceVariant = Color(0xFFD7BBC3),
    )
    "Solar Gold" -> darkColorScheme(
        primary = Color(0xFFFFC548),
        secondary = Color(0xFFFFE08A),
        tertiary = Color(0xFFFF9C36),
        background = Color(0xFF120B02),
        surface = Color(0xFF1A1104),
        surfaceVariant = Color(0xFF2D1A05),
        onPrimary = Color(0xFF281600),
        onBackground = Color(0xFFFFF6E3),
        onSurface = Color(0xFFFFF6E3),
        onSurfaceVariant = Color(0xFFE0C89A),
    )
    "Cobalt Luxe" -> darkColorScheme(
        primary = Color(0xFF5E8DFF),
        secondary = Color(0xFF7AE7FF),
        tertiary = Color(0xFF9C7DFF),
        background = Color(0xFF061024),
        surface = Color(0xFF0B1630),
        surfaceVariant = Color(0xFF13244A),
        onPrimary = FrostWhiteBase,
        onBackground = FrostWhiteBase,
        onSurface = FrostWhiteBase,
        onSurfaceVariant = Color(0xFFBAC7E5),
    )
    "Arctic Light" -> lightColorScheme(
        primary = Color(0xFF3E7BFF),
        secondary = Color(0xFF2EA8C9),
        tertiary = Color(0xFF7F8FE8),
        background = Color(0xFFF4FAFF),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE6F1FB),
        onPrimary = FrostWhiteBase,
        onBackground = Color(0xFF102235),
        onSurface = Color(0xFF102235),
        onSurfaceVariant = Color(0xFF5B7185),
    )
    "Rose Studio" -> lightColorScheme(
        primary = Color(0xFFE05C86),
        secondary = Color(0xFFB86D7A),
        tertiary = Color(0xFFFF9AB5),
        background = Color(0xFFFFF7FA),
        surface = Color(0xFFFFFCFD),
        surfaceVariant = Color(0xFFFBE7EE),
        onPrimary = FrostWhiteBase,
        onBackground = Color(0xFF3B1D2A),
        onSurface = Color(0xFF3B1D2A),
        onSurfaceVariant = Color(0xFF7D6670),
    )
    "Mint Daybreak" -> lightColorScheme(
        primary = Color(0xFF2D9D78),
        secondary = Color(0xFF57B89A),
        tertiary = Color(0xFF8BD9BF),
        background = Color(0xFFF4FFF9),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE1F6EC),
        onPrimary = FrostWhiteBase,
        onBackground = Color(0xFF16342B),
        onSurface = Color(0xFF16342B),
        onSurfaceVariant = Color(0xFF5D7A70),
    )
    "Amber Paper" -> lightColorScheme(
        primary = Color(0xFFD6891F),
        secondary = Color(0xFFB76A14),
        tertiary = Color(0xFFFFC16A),
        background = Color(0xFFFFFBF2),
        surface = Color(0xFFFFFEFA),
        surfaceVariant = Color(0xFFF5E7CA),
        onPrimary = FrostWhiteBase,
        onBackground = Color(0xFF3F2A09),
        onSurface = Color(0xFF3F2A09),
        onSurfaceVariant = Color(0xFF7C6850),
    )
    "Mono Mist" -> lightColorScheme(
        primary = Color(0xFF48505A),
        secondary = Color(0xFF6C727C),
        tertiary = Color(0xFF9DA3AD),
        background = Color(0xFFF6F7F8),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE8EAED),
        onPrimary = FrostWhiteBase,
        onBackground = Color(0xFF1F2328),
        onSurface = Color(0xFF1F2328),
        onSurfaceVariant = Color(0xFF646C76),
    )
    "Black & White" -> darkColorScheme(
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFD9D9D9),
        tertiary = Color(0xFF9A9A9A),
        background = Color(0xFF000000),
        surface = Color(0xFF050505),
        surfaceVariant = Color(0xFF121212),
        onPrimary = Color(0xFF000000),
        onBackground = Color(0xFFFFFFFF),
        onSurface = Color(0xFFFFFFFF),
        onSurfaceVariant = Color(0xFFBEBEBE),
    )
    "White & Black" -> lightColorScheme(
        primary = Color(0xFF000000),
        secondary = Color(0xFF303030),
        tertiary = Color(0xFF767676),
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF8F8F8),
        surfaceVariant = Color(0xFFE7E7E7),
        onPrimary = Color(0xFFFFFFFF),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        onSurfaceVariant = Color(0xFF5F5F5F),
    )
    else -> VerseFlowDesktopColors
}

private fun desktopColorLuminance(color: Color): Float =
    (0.2126f * color.red) + (0.7152f * color.green) + (0.0722f * color.blue)

private fun desktopImmersiveColorScheme(
    palette: List<Color>,
    spotlight: Boolean,
): androidx.compose.material3.ColorScheme {
    val base = palette.getOrElse(0) { VerseBlueBase }
    val support = palette.getOrElse(1) { AuroraCyanBase }
    val accent = palette.getOrElse(2) { NebulaBlueBase }
    val isLight = desktopColorLuminance(base) > 0.52f
    val primaryNeutral = if (isLight) Color(0xFF1C2128) else Color(0xFFE7EDF8)
    val secondaryNeutral = if (isLight) Color(0xFF46515F) else Color(0xFFC9D3E2)

    return if (isLight) {
        val primaryTone = lerp(
            lerp(accent, Color.Black, if (spotlight) 0.25f else 0.42f),
            primaryNeutral,
            if (spotlight) 0.12f else 0.34f,
        )
        val secondaryTone = lerp(
            lerp(support, Color.Black, if (spotlight) 0.20f else 0.34f),
            secondaryNeutral,
            if (spotlight) 0.10f else 0.30f,
        )
        lightColorScheme(
            primary = primaryTone,
            secondary = secondaryTone,
            tertiary = lerp(base, accent, if (spotlight) 0.40f else 0.26f),
            background = lerp(base, Color.White, if (spotlight) 0.90f else 0.96f),
            surface = lerp(base, Color.White, if (spotlight) 0.94f else 0.98f),
            surfaceVariant = lerp(lerp(base, support, 0.35f), Color.White, if (spotlight) 0.82f else 0.91f),
            onPrimary = Color.White,
            onBackground = Color(0xFF101216),
            onSurface = Color(0xFF101216),
            onSurfaceVariant = Color(0xFF505763),
        )
    } else {
        val primaryTone = lerp(
            lerp(accent, Color.White, if (spotlight) 0.18f else 0.34f),
            primaryNeutral,
            if (spotlight) 0.08f else 0.26f,
        )
        val secondaryTone = lerp(
            lerp(support, Color.White, if (spotlight) 0.12f else 0.28f),
            secondaryNeutral,
            if (spotlight) 0.08f else 0.24f,
        )
        darkColorScheme(
            primary = primaryTone,
            secondary = secondaryTone,
            tertiary = lerp(base, accent, if (spotlight) 0.45f else 0.28f),
            background = lerp(base, Color.Black, if (spotlight) 0.80f else 0.90f),
            surface = lerp(base, Color.Black, if (spotlight) 0.72f else 0.84f),
            surfaceVariant = lerp(lerp(base, support, 0.30f), Color.Black, if (spotlight) 0.60f else 0.76f),
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFD2D8E3),
        )
    }
}

private const val MIN_WIKIPEDIA_ARTIST_SCORE = 20

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

private enum class DesktopAlbumDetailTab(val title: String) {
    Tracks("Tracks"),
    Info("Info"),
    Stats("Stats"),
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

private data class DesktopHistoryHeatmapCell(
    val date: LocalDate,
    val intensity: Int,
    val playCount: Int,
)

private data class DesktopHistoryAlbumRecap(
    val artist: String,
    val album: String,
    val plays: Int,
    val listenedMs: Long,
)

private data class DesktopHistoryTimePattern(
    val label: String,
    val plays: Int,
    val listenedMs: Long,
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

private data class DesktopArtistLookupUiState(
    val artistName: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

private data class DesktopAlbumLookupUiState(
    val albumKey: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

private data class DesktopAlbumManualSearchUiState(
    val albumKey: String? = null,
    val query: String = "",
    val candidates: List<DesktopArtistLookupCandidate> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
)

private data class DesktopArtistManualSearchUiState(
    val artistName: String? = null,
    val query: String = "",
    val candidates: List<DesktopArtistLookupCandidate> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
)

private data class DesktopFetchedArtistProfile(
    val bio: String?,
    val imageUrl: String?,
    val sourcePageTitle: String,
)

private data class DesktopFetchedAlbumProfile(
    val bio: String?,
    val releaseDate: String?,
    val genre: String?,
    val sourcePageTitle: String,
    val totalTrackCount: Int?,
    val trackTitles: List<String>,
)

private data class DesktopTrackLookupUiState(
    val trackPath: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

private data class DesktopTrackManualSearchUiState(
    val trackPath: String? = null,
    val query: String = "",
    val candidates: List<DesktopArtistLookupCandidate> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
)

private data class DesktopFetchedTrackProfile(
    val title: String?,
    val artist: String?,
    val album: String?,
    val releaseDate: String?,
    val genre: String?,
    val sourcePageTitle: String,
)

private data class DesktopAlbumStats(
    val playCount: Int,
    val listenedMs: Long,
    val lastPlayedAtMs: Long?,
    val firstAddedAtMs: Long?,
    val newestAddedAtMs: Long?,
    val trackCount: Int,
    val totalDurationMs: Long,
)

private data class DesktopNavigationSnapshot(
    val section: DesktopSection,
    val libraryTab: DesktopLibraryTab,
    val selectedPlaylistId: String?,
    val selectedAlbumKey: String?,
    val selectedArtistName: String?,
)

private enum class DesktopPreviewScenario(val label: String) {
    Home("Home"),
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists"),
    Playlist("Playlist"),
    Album("Album"),
    Artist("Artist"),
    Queue("Queue"),
    History("History"),
    NowPlaying("Now Playing"),
    Lyrics("Lyrics"),
    Settings("Settings"),
}

private data class DesktopPreviewData(
    val libraryState: DesktopLibraryUiState,
    val playHistoryEntries: List<DesktopPlayHistoryEntry>,
    val favoriteTrackPaths: List<String>,
    val userPlaylists: List<DesktopUserPlaylist>,
    val artistProfileOverrides: Map<String, DesktopArtistProfileOverride>,
    val albumProfileOverrides: Map<String, DesktopAlbumProfileOverride>,
    val initialTrackId: String,
    val initialAlbumKey: String,
    val initialArtistName: String,
    val initialPlaylistId: String,
)

private data class DesktopArtistLookupCandidate(
    val pageTitle: String,
    val snippet: String,
    val score: Int,
)

private fun isMissingGenreLabel(genre: String?): Boolean =
    genre.isNullOrBlank() ||
        genre.equals("Unclassified", ignoreCase = true) ||
        genre.equals("No genre", ignoreCase = true) ||
        genre.equals("No genre tag", ignoreCase = true)

private fun normalizeGenreLabel(genre: String?): String =
    genre
        ?.trim()
        ?.takeUnless(::isMissingGenreLabel)
        ?: "No genre"

private fun displayGenreLabel(genre: String): String = normalizeGenreLabel(genre)

private fun displayGenreLabels(genres: List<String>): String =
    genres
        .map(::displayGenreLabel)
        .distinct()
        .joinToString(" • ")
        .ifBlank { "No genre" }

private fun extractReleaseYear(value: String?): Int? =
    value
        ?.let { Regex("""(19|20)\d{2}""").find(it)?.value }
        ?.toIntOrNull()

private fun extractReleaseDate(value: String?): Int? = extractReleaseYear(value)

private fun desktopLastFmApiKey(settingsValue: String): String =
    settingsValue.ifBlank {
        System.getenv("VERSEFLOW_LAST_FM_API_KEY")
            ?.trim()
            .orEmpty()
            .ifBlank {
                System.getProperty("verseflow.lastfm.apiKey")
                    ?.trim()
                    .orEmpty()
            }
    }

@Composable
fun VerseFlowDesktopApp(previewMode: Boolean = false) {
    val appStore = remember { DesktopAppStore() }
    val libraryStore = remember { DesktopLibraryStore() }
    val playlistStore = remember { DesktopPlaylistStore() }
    val previewData = remember(previewMode) {
        if (previewMode) buildDesktopPreviewData() else null
    }
    val desktopThemes = remember {
        listOf(
            DesktopThemePreset("Nebula Dark", "Original cinematic dark theme"),
            DesktopThemePreset("Eclipse OLED", "High-contrast near-black desktop mode"),
            DesktopThemePreset("Crimson Velvet", "Deep red dark theme with warmer contrast"),
            DesktopThemePreset("Solar Gold", "Golden dark theme with richer warmth"),
            DesktopThemePreset("Cobalt Luxe", "Blue-forward premium look for desktop"),
            DesktopThemePreset("Arctic Light", "Clean icy light theme for daytime use"),
            DesktopThemePreset("Rose Studio", "Soft rose light theme with warm neutrals"),
            DesktopThemePreset("Mint Daybreak", "Fresh green light theme with airy surfaces"),
            DesktopThemePreset("Amber Paper", "Warm paper-like light theme"),
            DesktopThemePreset("Mono Mist", "Neutral grayscale light theme"),
            DesktopThemePreset("Black & White", "Strict monochrome theme with grayscale artwork"),
            DesktopThemePreset("White & Black", "Inverse monochrome theme with white-led surfaces"),
            DesktopThemePreset("Immersive Flow", "Live interface tint pulled from the currently playing album art"),
        )
    }
    val storedSettings = remember(desktopThemes) { appStore.loadSettings(desktopThemes.last().name) }
    val lyricsCacheStore = remember { DesktopLyricsCacheStore() }
    val coroutineScope = rememberCoroutineScope()
    val playbackController = remember { DesktopPlaybackController() }
    val playbackState by playbackController.state.collectAsState()
    var libraryState by remember {
        mutableStateOf(
            previewData?.libraryState ?: DesktopLibraryUiState(
                sourcePaths = libraryStore.loadLibraryPaths(),
            ),
        )
    }
    var section by remember(previewMode) { mutableStateOf(DesktopSection.Home) }
    var libraryTab by remember { mutableStateOf(DesktopLibraryTab.Songs) }
    var previewScenario by remember(previewMode) { mutableStateOf(DesktopPreviewScenario.Home) }
    var nowPlayingReturnSnapshot by remember { mutableStateOf<DesktopNavigationSnapshot?>(null) }
    var isSidebarCollapsed by remember { mutableStateOf(libraryStore.loadSidebarCollapsed()) }
    var selectedPlaylistId by remember(previewMode) { mutableStateOf<String?>(previewData?.initialPlaylistId) }
    var selectedAlbumKey by remember(previewMode) { mutableStateOf<String?>(previewData?.initialAlbumKey) }
    var selectedArtistName by remember(previewMode) { mutableStateOf<String?>(previewData?.initialArtistName) }
    var currentTrackId by remember(previewMode) { mutableStateOf<String?>(previewData?.initialTrackId) }
    var searchQuery by remember { mutableStateOf("") }
    var recentSearches by remember { mutableStateOf(appStore.loadRecentSearches()) }
    var artistSpotlightOrder by remember { mutableStateOf(appStore.loadArtistSpotlightOrder()) }
    var albumsViewMode by remember { mutableStateOf(DesktopCollectionViewMode.List) }
    var artistsViewMode by remember { mutableStateOf(DesktopCollectionViewMode.List) }
    var albumsSortMode by remember { mutableStateOf(DesktopAlbumSortMode.DateAdded) }
    var albumsGridScrolled by remember { mutableStateOf(false) }
    var homeScrolled by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf(storedSettings.displayName) }
    var selectedTheme by remember { mutableStateOf(desktopThemeNameCompatibility(storedSettings.selectedTheme)) }
    var isShuffleEnabled by remember { mutableStateOf(storedSettings.isShuffleEnabled) }
    var isRepeatEnabled by remember { mutableStateOf(storedSettings.isRepeatEnabled) }
    var autoRescanEnabled by remember { mutableStateOf(storedSettings.autoRescanEnabled) }
    var musixmatchApiKey by remember { mutableStateOf(storedSettings.musixmatchApiKey) }
    var lastFmApiKey by remember { mutableStateOf(storedSettings.lastFmApiKey) }
    var lyricsStatuses by remember { mutableStateOf<Map<String, DesktopLyricsLoadState>>(emptyMap()) }
    var playHistoryEntries by remember(previewMode) { mutableStateOf(previewData?.playHistoryEntries ?: appStore.loadPlayHistory()) }
    var recentTrackIds by remember { mutableStateOf(playHistoryEntries.toRecentTrackIds()) }
    var playCounts by remember { mutableStateOf(playHistoryEntries.toPlayCounts()) }
    var userPlaylists by remember(previewMode) { mutableStateOf(previewData?.userPlaylists ?: playlistStore.loadPlaylists()) }
    var favoriteTrackPaths by remember(previewMode) { mutableStateOf(previewData?.favoriteTrackPaths ?: libraryStore.loadFavoriteTrackPaths()) }
    var hiddenTrackPaths by remember { mutableStateOf(appStore.loadHiddenTrackPaths()) }
    var trackOverrides by remember { mutableStateOf(appStore.loadTrackOverrides()) }
    var artistProfileOverrides by remember(previewMode) { mutableStateOf(previewData?.artistProfileOverrides ?: appStore.loadArtistProfileOverrides()) }
    var albumProfileOverrides by remember(previewMode) { mutableStateOf(previewData?.albumProfileOverrides ?: appStore.loadAlbumProfileOverrides()) }
    var artistLookupUiState by remember { mutableStateOf(DesktopArtistLookupUiState()) }
    var artistManualSearchUiState by remember { mutableStateOf(DesktopArtistManualSearchUiState()) }
    var albumLookupUiState by remember { mutableStateOf(DesktopAlbumLookupUiState()) }
    var albumManualSearchUiState by remember { mutableStateOf(DesktopAlbumManualSearchUiState()) }
    var trackLookupUiState by remember { mutableStateOf(DesktopTrackLookupUiState()) }
    var trackManualSearchUiState by remember { mutableStateOf(DesktopTrackManualSearchUiState()) }
    var pendingPlaybackSession by remember(previewMode) { mutableStateOf(if (previewMode) null else appStore.loadPlaybackSession()) }
    var queueTrackPaths by remember(previewMode) { mutableStateOf(previewData?.libraryState?.tracks?.map(DesktopTrack::path) ?: emptyList()) }
    var queueLabel by remember(previewMode) { mutableStateOf(if (previewMode) "Preview Queue" else "All Songs") }
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
    val featuredArtists = remember(allArtists) {
        allArtists
            .sortedWith(
                compareByDescending<DesktopArtistSummary> { it.trackCount }
                    .thenBy { it.name.lowercase() },
            )
            .take(5)
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
    val selectedAlbumProfileOverride = remember(selectedAlbum?.artist, selectedAlbum?.title, albumProfileOverrides) {
        selectedAlbum?.let { albumProfileOverrides[desktopAlbumKey(it.artist, it.title)] }
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
        val updatedGenre = normalizeGenreLabel(override.genre?.trim().orEmpty().ifBlank { track.genre })
        val updatedReleaseDate = override.releaseDate?.trim().orEmpty().ifBlank { track.releaseDate.orEmpty() }.ifBlank { null }
        return track.copy(
            title = updatedTitle,
            artist = updatedArtist,
            artistCredits = buildDesktopArtistCredits(updatedArtist, updatedTitle).ifEmpty { listOf(updatedArtist) },
            album = updatedAlbum,
            releaseDate = updatedReleaseDate,
            genre = updatedGenre,
            mood = updatedGenre.takeUnless(::isMissingGenreLabel) ?: "Local file",
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

    fun captureNavigationSnapshot(): DesktopNavigationSnapshot =
        DesktopNavigationSnapshot(
            section = section,
            libraryTab = libraryTab,
            selectedPlaylistId = selectedPlaylistId,
            selectedAlbumKey = selectedAlbumKey,
            selectedArtistName = selectedArtistName,
        )

    fun openNowPlaying() {
        if (section != DesktopSection.NowPlaying) {
            nowPlayingReturnSnapshot = captureNavigationSnapshot()
        }
        section = DesktopSection.NowPlaying
    }

    fun openPreviewScenario(scenario: DesktopPreviewScenario) {
        previewScenario = scenario
        when (scenario) {
            DesktopPreviewScenario.Home -> section = DesktopSection.Home
            DesktopPreviewScenario.Songs -> {
                libraryTab = DesktopLibraryTab.Songs
                section = DesktopSection.Library
            }
            DesktopPreviewScenario.Albums -> {
                libraryTab = DesktopLibraryTab.Albums
                section = DesktopSection.Library
            }
            DesktopPreviewScenario.Artists -> {
                libraryTab = DesktopLibraryTab.Artists
                section = DesktopSection.Library
            }
            DesktopPreviewScenario.Playlist -> {
                selectedPlaylistId = previewData?.initialPlaylistId ?: selectedPlaylistId
                section = DesktopSection.PlaylistDetail
            }
            DesktopPreviewScenario.Album -> {
                selectedAlbumKey = previewData?.initialAlbumKey ?: selectedAlbumKey
                section = DesktopSection.AlbumDetail
            }
            DesktopPreviewScenario.Artist -> {
                selectedArtistName = previewData?.initialArtistName ?: selectedArtistName
                section = DesktopSection.ArtistDetail
            }
            DesktopPreviewScenario.Queue -> section = DesktopSection.PlayQueue
            DesktopPreviewScenario.History -> section = DesktopSection.PlayHistory
            DesktopPreviewScenario.NowPlaying -> {
                currentTrackId = previewData?.initialTrackId ?: currentTrackId
                section = DesktopSection.NowPlaying
            }
            DesktopPreviewScenario.Lyrics -> {
                currentTrackId = previewData?.initialTrackId ?: currentTrackId
                section = DesktopSection.Lyrics
            }
            DesktopPreviewScenario.Settings -> section = DesktopSection.Settings
        }
    }

    fun restoreFromNowPlaying() {
        val snapshot = nowPlayingReturnSnapshot
        if (snapshot == null) {
            section = DesktopSection.Home
            return
        }
        libraryTab = snapshot.libraryTab
        selectedPlaylistId = snapshot.selectedPlaylistId
        selectedAlbumKey = snapshot.selectedAlbumKey
        selectedArtistName = snapshot.selectedArtistName
        section = snapshot.section
        nowPlayingReturnSnapshot = null
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
        releaseDate: String,
    ) {
        val override = DesktopTrackMetadataOverride(
            title = title.trim().takeIf(String::isNotBlank),
            artist = artist.trim().takeIf(String::isNotBlank),
            album = album.trim().takeIf(String::isNotBlank),
            genre = normalizeGenreLabel(genre).takeIf { !isMissingGenreLabel(it) },
            releaseDate = releaseDate.trim().takeIf(String::isNotBlank),
        )
        trackOverrides = trackOverrides + (track.path to override)
        appStore.saveTrackOverrides(trackOverrides)
        libraryState = libraryState.copy(
            tracks = libraryState.tracks.map { candidate ->
                if (candidate.path == track.path) applyTrackCustomization(candidate) else candidate
            },
        )
        editingTrack = null
        trackLookupUiState = DesktopTrackLookupUiState()
        trackManualSearchUiState = DesktopTrackManualSearchUiState()
    }

    fun inferTrackGenre(track: DesktopTrack): String? {
        val albumKey = desktopAlbumKey(track.albumArtist, track.album)
        val albumGenre = albumProfileOverrides[albumKey]?.genre?.takeUnless(::isMissingGenreLabel)
        if (albumGenre != null) return albumGenre

        val albumTrackGenre = tracks
            .filter { it.album == track.album && it.albumArtist == track.albumArtist }
            .map(DesktopTrack::genre)
            .map(::normalizeGenreLabel)
            .firstOrNull { !isMissingGenreLabel(it) }
        if (albumTrackGenre != null) return albumTrackGenre

        val artistGenre = tracks
            .filter { artistTrack -> track.artistCredits.any { it in artistTrack.artistCredits } }
            .map(DesktopTrack::genre)
            .map(::normalizeGenreLabel)
            .filterNot(::isMissingGenreLabel)
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
        return artistGenre
    }

    fun inferTrackReleaseDate(track: DesktopTrack): String? {
        val albumKey = desktopAlbumKey(track.albumArtist, track.album)
        val albumDate = albumProfileOverrides[albumKey]?.releaseDate?.takeIf(String::isNotBlank)
        if (albumDate != null) return albumDate

        return tracks
            .filter { it.album == track.album && it.albumArtist == track.albumArtist }
            .mapNotNull(DesktopTrack::releaseDate)
            .firstOrNull()
    }

    suspend fun fetchLastFmFallbackGenre(track: DesktopTrack): String? {
        val apiKey = desktopLastFmApiKey(lastFmApiKey)
        if (apiKey.isBlank()) return null
        return runCatching { fetchDesktopTrackGenreFromLastFm(track, apiKey) }.getOrNull()
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

    fun updateAlbumProfile(
        albumKey: String,
        about: String? = albumProfileOverrides[albumKey]?.about,
        releaseDate: String? = albumProfileOverrides[albumKey]?.releaseDate,
        genre: String? = albumProfileOverrides[albumKey]?.genre,
        sourcePageTitle: String? = albumProfileOverrides[albumKey]?.sourcePageTitle,
        totalTrackCount: Int? = albumProfileOverrides[albumKey]?.totalTrackCount,
        trackTitles: List<String> = albumProfileOverrides[albumKey]?.trackTitles.orEmpty(),
    ) {
        val nextOverride = DesktopAlbumProfileOverride(
            about = about?.trim().takeUnless { it.isNullOrEmpty() },
            releaseDate = releaseDate?.trim().takeUnless { it.isNullOrEmpty() },
            genre = genre?.trim().takeUnless { it.isNullOrEmpty() },
            sourcePageTitle = sourcePageTitle?.trim().takeUnless { it.isNullOrEmpty() },
            totalTrackCount = totalTrackCount?.takeIf { it > 0 },
            trackTitles = trackTitles.map(String::trim).filter(String::isNotBlank).distinct(),
        )
        albumProfileOverrides = if (
            nextOverride.about == null &&
            nextOverride.releaseDate == null &&
            nextOverride.genre == null &&
            nextOverride.sourcePageTitle == null &&
            nextOverride.totalTrackCount == null &&
            nextOverride.trackTitles.isEmpty()
        ) {
            albumProfileOverrides - albumKey
        } else {
            albumProfileOverrides + (albumKey to nextOverride)
        }
        appStore.saveAlbumProfileOverrides(albumProfileOverrides)
    }

    fun importArtistProfile(
        artistName: String,
        artistTracks: List<DesktopTrack>,
    ) {
        artistLookupUiState = DesktopArtistLookupUiState(
            artistName = artistName,
            isLoading = true,
            message = "Searching Wikipedia for $artistName...",
            isError = false,
        )
        coroutineScope.launch {
            runCatching {
                val representativeTrack = artistTracks
                    .sortedWith(
                        compareByDescending<DesktopTrack> { it.artistCredits.size == 1 }
                            .thenByDescending { it.title.length }
                            .thenBy { it.title.lowercase() },
                    )
                    .firstOrNull()
                val profile = fetchDesktopArtistProfileFromWikipedia(
                    artistName = artistName,
                    representativeTrack = representativeTrack,
                )
                val savedPhotoPath = profile.imageUrl?.let { imageUrl ->
                    saveDesktopArtistReferenceImage(artistName, imageUrl)
                }
                val mergedAbout = profile.bio?.ifBlank { null } ?: artistProfileOverrides[artistName]?.about
                val mergedPhotoPath = savedPhotoPath ?: artistProfileOverrides[artistName]?.photoPath
                updateArtistProfile(
                    artistName = artistName,
                    photoPath = mergedPhotoPath,
                    about = mergedAbout,
                )
                DesktopArtistLookupUiState(
                    artistName = artistName,
                    isLoading = false,
                    message = buildString {
                        append("Imported")
                        if (profile.bio != null) append(" bio")
                        if (profile.bio != null && savedPhotoPath != null) append(" and")
                        if (savedPhotoPath != null) append(" photo")
                        append(" from Wikipedia.")
                    }.ifBlank { "Imported artist info from Wikipedia." },
                    isError = false,
                )
            }.getOrElse { error ->
                DesktopArtistLookupUiState(
                    artistName = artistName,
                    isLoading = false,
                    message = error.message ?: "VerseFlow couldn't fetch artist info right now.",
                    isError = true,
                )
            }.also { nextState ->
                artistLookupUiState = nextState
            }
        }
    }

    fun importAlbumProfile(
        album: DesktopAlbumSummary,
        albumTracks: List<DesktopTrack>,
    ) {
        val albumKey = desktopAlbumKey(album.artist, album.title)
        albumLookupUiState = DesktopAlbumLookupUiState(
            albumKey = albumKey,
            isLoading = true,
            message = "Searching Wikipedia for ${album.title}...",
            isError = false,
        )
        coroutineScope.launch {
            runCatching {
                val profile = fetchDesktopAlbumProfileFromWikipedia(
                    album = album,
                    albumTracks = albumTracks,
                )
                updateAlbumProfile(
                    albumKey = albumKey,
                    about = profile.bio ?: albumProfileOverrides[albumKey]?.about,
                    releaseDate = profile.releaseDate ?: album.releaseDate ?: albumProfileOverrides[albumKey]?.releaseDate,
                    genre = profile.genre ?: album.genre.ifBlank { null } ?: albumProfileOverrides[albumKey]?.genre,
                    sourcePageTitle = profile.sourcePageTitle,
                    totalTrackCount = profile.totalTrackCount ?: albumProfileOverrides[albumKey]?.totalTrackCount,
                    trackTitles = profile.trackTitles.ifEmpty { albumProfileOverrides[albumKey]?.trackTitles.orEmpty() },
                )
                DesktopAlbumLookupUiState(
                    albumKey = albumKey,
                    isLoading = false,
                    message = "Imported album info from ${profile.sourcePageTitle}.",
                    isError = false,
                )
            }.getOrElse { error ->
                DesktopAlbumLookupUiState(
                    albumKey = albumKey,
                    isLoading = false,
                    message = error.message ?: "VerseFlow couldn't fetch album info right now.",
                    isError = true,
                )
            }.also { nextState ->
                albumLookupUiState = nextState
            }
        }
    }

    fun openManualAlbumSearch(
        album: DesktopAlbumSummary,
        albumTracks: List<DesktopTrack>,
    ) {
        val representativeTrack = albumTracks.firstOrNull()
        val initialQuery = listOf(album.title, album.artist, representativeTrack?.title.orEmpty(), "album")
            .filter(String::isNotBlank)
            .joinToString(" ")
        val albumKey = desktopAlbumKey(album.artist, album.title)
        albumManualSearchUiState = DesktopAlbumManualSearchUiState(
            albumKey = albumKey,
            query = initialQuery,
            isLoading = true,
            message = "Searching Wikipedia...",
        )
        coroutineScope.launch {
            val candidates = runCatching {
                val client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                searchWikipediaCandidates(
                    client = client,
                    query = initialQuery,
                    artistName = album.artist,
                    representativeTrack = representativeTrack,
                )
            }.getOrDefault(emptyList())
            albumManualSearchUiState = albumManualSearchUiState.copy(
                candidates = candidates,
                isLoading = false,
                message = if (candidates.isEmpty()) "No Wikipedia results found for that search." else null,
            )
        }
    }

    fun runManualAlbumSearch(
        album: DesktopAlbumSummary,
        query: String,
        albumTracks: List<DesktopTrack>,
    ) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return
        val representativeTrack = albumTracks.firstOrNull()
        val albumKey = desktopAlbumKey(album.artist, album.title)
        albumManualSearchUiState = albumManualSearchUiState.copy(
            albumKey = albumKey,
            query = normalizedQuery,
            isLoading = true,
            message = "Searching Wikipedia...",
            candidates = emptyList(),
        )
        coroutineScope.launch {
            val candidates = runCatching {
                val client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                searchWikipediaCandidates(
                    client = client,
                    query = normalizedQuery,
                    artistName = album.artist,
                    representativeTrack = representativeTrack,
                )
            }.getOrDefault(emptyList())
            albumManualSearchUiState = albumManualSearchUiState.copy(
                candidates = candidates,
                isLoading = false,
                message = if (candidates.isEmpty()) "No Wikipedia results found for that search." else null,
            )
        }
    }

    fun importAlbumProfileFromManualCandidate(
        album: DesktopAlbumSummary,
        albumTracks: List<DesktopTrack>,
        pageTitle: String,
    ) {
        val albumKey = desktopAlbumKey(album.artist, album.title)
        albumLookupUiState = DesktopAlbumLookupUiState(
            albumKey = albumKey,
            isLoading = true,
            message = "Importing album info from $pageTitle...",
            isError = false,
        )
        coroutineScope.launch {
            runCatching {
                val profile = fetchDesktopAlbumProfileFromWikipediaPage(
                    album = album,
                    albumTracks = albumTracks,
                    pageTitle = pageTitle,
                )
                updateAlbumProfile(
                    albumKey = albumKey,
                    about = profile.bio ?: albumProfileOverrides[albumKey]?.about,
                    releaseDate = profile.releaseDate ?: album.releaseDate ?: albumProfileOverrides[albumKey]?.releaseDate,
                    genre = profile.genre ?: album.genre.ifBlank { null } ?: albumProfileOverrides[albumKey]?.genre,
                    sourcePageTitle = profile.sourcePageTitle,
                    totalTrackCount = profile.totalTrackCount ?: albumProfileOverrides[albumKey]?.totalTrackCount,
                    trackTitles = profile.trackTitles.ifEmpty { albumProfileOverrides[albumKey]?.trackTitles.orEmpty() },
                )
                albumManualSearchUiState = DesktopAlbumManualSearchUiState()
                DesktopAlbumLookupUiState(
                    albumKey = albumKey,
                    isLoading = false,
                    message = "Imported album info from $pageTitle.",
                    isError = false,
                )
            }.getOrElse { error ->
                DesktopAlbumLookupUiState(
                    albumKey = albumKey,
                    isLoading = false,
                    message = error.message ?: "VerseFlow couldn't import that Wikipedia page.",
                    isError = true,
                )
            }.also { nextState ->
                albumLookupUiState = nextState
            }
        }
    }

    fun openManualArtistSearch(
        artistName: String,
        artistTracks: List<DesktopTrack>,
    ) {
        val representativeTrack = artistTracks
            .sortedWith(
                compareByDescending<DesktopTrack> { it.artistCredits.size == 1 }
                    .thenByDescending { it.title.length }
                    .thenBy { it.title.lowercase() },
            )
            .firstOrNull()
        val initialQuery = listOf(
            artistName,
            representativeTrack?.title.orEmpty(),
            representativeTrack
                ?.artistCredits
                ?.filterNot { it.equals(artistName, ignoreCase = true) }
                ?.firstOrNull()
                .orEmpty(),
        ).filter(String::isNotBlank).joinToString(" ")
        artistManualSearchUiState = DesktopArtistManualSearchUiState(
            artistName = artistName,
            query = initialQuery,
            isLoading = true,
            message = "Searching Wikipedia...",
        )
        coroutineScope.launch {
            val candidates = runCatching {
                val client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                searchWikipediaCandidates(
                    client = client,
                    query = initialQuery,
                    artistName = artistName,
                    representativeTrack = representativeTrack,
                )
            }.getOrDefault(emptyList())
            artistManualSearchUiState = artistManualSearchUiState.copy(
                candidates = candidates,
                isLoading = false,
                message = if (candidates.isEmpty()) "No Wikipedia results found for that search." else null,
            )
        }
    }

    fun runManualArtistSearch(
        artistName: String,
        query: String,
        artistTracks: List<DesktopTrack>,
    ) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return
        val representativeTrack = artistTracks.firstOrNull()
        artistManualSearchUiState = artistManualSearchUiState.copy(
            artistName = artistName,
            query = normalizedQuery,
            isLoading = true,
            message = "Searching Wikipedia...",
            candidates = emptyList(),
        )
        coroutineScope.launch {
            val candidates = runCatching {
                val client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                searchWikipediaCandidates(
                    client = client,
                    query = normalizedQuery,
                    artistName = artistName,
                    representativeTrack = representativeTrack,
                )
            }.getOrDefault(emptyList())
            artistManualSearchUiState = artistManualSearchUiState.copy(
                candidates = candidates,
                isLoading = false,
                message = if (candidates.isEmpty()) "No Wikipedia results found for that search." else null,
            )
        }
    }

    fun importArtistProfileFromManualCandidate(
        artistName: String,
        pageTitle: String,
    ) {
        artistLookupUiState = DesktopArtistLookupUiState(
            artistName = artistName,
            isLoading = true,
            message = "Importing artist info from $pageTitle...",
            isError = false,
        )
        coroutineScope.launch {
            runCatching {
                val client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                val payload = fetchWikipediaSummaryPayload(client, pageTitle)
                val profile = DesktopFetchedArtistProfile(
                    bio = payload.optString("extract").substringBefore("\n").trim().ifBlank { null },
                    imageUrl = payload.optJSONObject("originalimage")?.optString("source")?.ifBlank { null }
                        ?: payload.optJSONObject("thumbnail")?.optString("source")?.ifBlank { null },
                    sourcePageTitle = pageTitle,
                )
                val savedPhotoPath = profile.imageUrl?.let { saveDesktopArtistReferenceImage(artistName, it) }
                updateArtistProfile(
                    artistName = artistName,
                    photoPath = savedPhotoPath ?: artistProfileOverrides[artistName]?.photoPath,
                    about = profile.bio ?: artistProfileOverrides[artistName]?.about,
                )
                artistManualSearchUiState = DesktopArtistManualSearchUiState()
                DesktopArtistLookupUiState(
                    artistName = artistName,
                    isLoading = false,
                    message = "Imported artist info from $pageTitle.",
                    isError = false,
                )
            }.getOrElse { error ->
                DesktopArtistLookupUiState(
                    artistName = artistName,
                    isLoading = false,
                    message = error.message ?: "VerseFlow couldn't import that Wikipedia page.",
                    isError = true,
                )
            }.also { nextState ->
                artistLookupUiState = nextState
            }
        }
    }

    fun importTrackProfile(track: DesktopTrack) {
        trackLookupUiState = DesktopTrackLookupUiState(
            trackPath = track.path,
            isLoading = true,
            message = "Searching Wikipedia for ${track.title}...",
            isError = false,
        )
        coroutineScope.launch {
            runCatching {
                fetchDesktopTrackProfileFromWikipedia(track)
            }.onSuccess { profile ->
                val lastFmGenre = if (profile.genre == null) fetchLastFmFallbackGenre(track) else null
                val resolvedGenre = profile.genre ?: lastFmGenre ?: inferTrackGenre(track) ?: track.genre
                val resolvedReleaseDate = profile.releaseDate ?: inferTrackReleaseDate(track) ?: track.releaseDate.orEmpty()
                saveTrackMetadataOverride(
                    track = track,
                    title = profile.title ?: track.title,
                    artist = profile.artist ?: track.artist,
                    album = profile.album ?: track.album,
                    genre = resolvedGenre,
                    releaseDate = resolvedReleaseDate,
                )
                editingTrack = tracks.firstOrNull { it.path == track.path }?.let(::applyTrackCustomization) ?: track
                trackLookupUiState = DesktopTrackLookupUiState(
                    trackPath = track.path,
                    isLoading = false,
                    message = buildString {
                        append("Imported song info from ")
                        append(profile.sourcePageTitle)
                        append('.')
                    },
                    isError = false,
                )
            }.onFailure { error ->
                val lastFmGenre = fetchLastFmFallbackGenre(track)
                val fallbackGenre = inferTrackGenre(track)
                val fallbackReleaseDate = inferTrackReleaseDate(track)
                if (lastFmGenre != null || fallbackGenre != null || fallbackReleaseDate != null) {
                    saveTrackMetadataOverride(
                        track = track,
                        title = track.title,
                        artist = track.artist,
                        album = track.album,
                        genre = lastFmGenre ?: fallbackGenre ?: track.genre,
                        releaseDate = fallbackReleaseDate ?: track.releaseDate.orEmpty(),
                    )
                    editingTrack = tracks.firstOrNull { it.path == track.path }?.let(::applyTrackCustomization) ?: track
                    trackLookupUiState = DesktopTrackLookupUiState(
                        trackPath = track.path,
                        isLoading = false,
                        message = buildString {
                            append("No reliable Wikipedia song page was found")
                            if (lastFmGenre != null || fallbackGenre != null || fallbackReleaseDate != null) {
                                append(", so VerseFlow applied fallback metadata")
                                if ((lastFmGenre != null || fallbackGenre != null) && fallbackReleaseDate != null) append(" for genre and release year")
                                else if (lastFmGenre != null || fallbackGenre != null) append(" for genre")
                                else append(" for release year")
                            }
                            append('.')
                        },
                        isError = false,
                    )
                } else {
                    trackLookupUiState = DesktopTrackLookupUiState(
                        trackPath = track.path,
                        isLoading = false,
                        message = error.message ?: "VerseFlow couldn't import that track info.",
                        isError = true,
                    )
                }
            }
        }
    }

    fun openManualTrackSearch(track: DesktopTrack) {
        trackManualSearchUiState = DesktopTrackManualSearchUiState(
            trackPath = track.path,
            query = listOf(track.title, track.artist, "song").joinToString(" ").trim(),
            candidates = emptyList(),
            isLoading = false,
            message = null,
        )
    }

    fun runManualTrackSearch(track: DesktopTrack, query: String) {
        val normalizedQuery = query.trim()
        trackManualSearchUiState = DesktopTrackManualSearchUiState(
            trackPath = track.path,
            query = normalizedQuery,
            candidates = emptyList(),
            isLoading = true,
            message = "Searching Wikipedia...",
        )
        coroutineScope.launch {
            runCatching {
                val client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                searchWikipediaPages(client, normalizedQuery, limit = 8)
            }.onSuccess { candidates ->
                trackManualSearchUiState = DesktopTrackManualSearchUiState(
                    trackPath = track.path,
                    query = normalizedQuery,
                    candidates = candidates,
                    isLoading = false,
                    message = if (candidates.isEmpty()) "No Wikipedia results found for that search." else null,
                )
            }.onFailure { error ->
                trackManualSearchUiState = DesktopTrackManualSearchUiState(
                    trackPath = track.path,
                    query = normalizedQuery,
                    candidates = emptyList(),
                    isLoading = false,
                    message = error.message ?: "VerseFlow couldn't run that search.",
                )
            }
        }
    }

    fun importTrackProfileFromManualCandidate(track: DesktopTrack, pageTitle: String) {
        trackLookupUiState = DesktopTrackLookupUiState(
            trackPath = track.path,
            isLoading = true,
            message = "Importing info from $pageTitle...",
            isError = false,
        )
        coroutineScope.launch {
            runCatching {
                fetchDesktopTrackProfileFromWikipediaPage(track, pageTitle)
            }.onSuccess { profile ->
                val lastFmGenre = if (profile.genre == null) fetchLastFmFallbackGenre(track) else null
                val resolvedGenre = profile.genre ?: lastFmGenre ?: inferTrackGenre(track) ?: track.genre
                val resolvedReleaseDate = profile.releaseDate ?: inferTrackReleaseDate(track) ?: track.releaseDate.orEmpty()
                saveTrackMetadataOverride(
                    track = track,
                    title = profile.title ?: track.title,
                    artist = profile.artist ?: track.artist,
                    album = profile.album ?: track.album,
                    genre = resolvedGenre,
                    releaseDate = resolvedReleaseDate,
                )
                editingTrack = tracks.firstOrNull { it.path == track.path }?.let(::applyTrackCustomization) ?: track
                trackLookupUiState = DesktopTrackLookupUiState(
                    trackPath = track.path,
                    isLoading = false,
                    message = "Imported song info from $pageTitle.",
                    isError = false,
                )
                trackManualSearchUiState = DesktopTrackManualSearchUiState()
            }.onFailure { error ->
                trackLookupUiState = DesktopTrackLookupUiState(
                    trackPath = track.path,
                    isLoading = false,
                    message = error.message ?: "VerseFlow couldn't import that Wikipedia page.",
                    isError = true,
                )
            }
        }
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
        if (!previewMode) {
            libraryState.sourcePaths
                .takeIf { it.isNotEmpty() }
                ?.map(Path::of)
                ?.let(::scanFolders)
        }
    }

    LaunchedEffect(displayName, selectedTheme, isShuffleEnabled, isRepeatEnabled, autoRescanEnabled, musixmatchApiKey, lastFmApiKey) {
        if (!previewMode) {
            appStore.saveSettings(
                DesktopSettingsSnapshot(
                    displayName = displayName,
                    selectedTheme = selectedTheme,
                    isShuffleEnabled = isShuffleEnabled,
                    isRepeatEnabled = isRepeatEnabled,
                    autoRescanEnabled = autoRescanEnabled,
                    musixmatchApiKey = musixmatchApiKey,
                    lastFmApiKey = lastFmApiKey,
                ),
            )
        }
    }

    LaunchedEffect(Unit) {
        if (!previewMode) {
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

    val desktopColorScheme = remember(selectedTheme, currentTrack?.id, currentTrack?.palette, section) {
        if (desktopThemeNameCompatibility(selectedTheme) == "Immersive Flow") {
            desktopImmersiveColorScheme(
                palette = currentTrack?.palette ?: listOf(VerseBlueBase, AuroraCyanBase, NebulaBlueBase),
                spotlight = section == DesktopSection.NowPlaying,
            )
        } else {
            desktopColorSchemeForTheme(selectedTheme)
        }
    }
    val desktopTypography = remember { verseFlowDesktopTypography() }

    MaterialTheme(
        colorScheme = desktopColorScheme,
        typography = desktopTypography,
    ) {
        SideEffect {
            desktopThemeTokens = desktopThemeTokensFrom(desktopColorScheme)
            desktopThemeForArtwork = selectedTheme
        }
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(InkBlack),
                )
                if (section == DesktopSection.NowPlaying) {
                    DesktopAppBackdrop(track = currentTrack)
                }
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
                        if (previewMode) {
                            DesktopPreviewToolbar(
                                selectedScenario = previewScenario,
                                onScenarioSelect = ::openPreviewScenario,
                            )
                        }
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
                                    onOpenNowPlaying = ::openNowPlaying,
                                    onOpenLyrics = { section = DesktopSection.Lyrics },
                                    onOpenAlbum = { album ->
                                        selectedAlbumKey = desktopAlbumKey(album.artist, album.title)
                                        section = DesktopSection.AlbumDetail
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks, label = "All Songs")
                                        openNowPlaying()
                                    },
                                    onPlayCollection = { collection, label ->
                                        collection.firstOrNull()?.let { playTrack(it, queue = collection, label = label) }
                                        openNowPlaying()
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
                                        openNowPlaying()
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
                                    onBack = ::restoreFromNowPlaying,
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
                                    lastFmApiKey = lastFmApiKey,
                                    onLastFmApiKeyChange = { lastFmApiKey = it },
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
                                        openNowPlaying()
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(
                                            track,
                                            queue = selectedPlaylist?.tracks ?: activeQueue,
                                            label = selectedPlaylist?.title ?: queueLabel,
                                        )
                                        openNowPlaying()
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
                                    albumProfileOverride = selectedAlbumProfileOverride,
                                    albumLookupUiState = albumLookupUiState,
                                    albumManualSearchUiState = albumManualSearchUiState,
                                    tracks = selectedAlbum?.let { album ->
                                        tracks.filter { it.albumArtist == album.artist && it.album == album.title }
                                    }.orEmpty(),
                                    historyEntries = playHistoryEntries,
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
                                        openNowPlaying()
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks.filter { it.albumArtist == selectedAlbum?.artist && it.album == selectedAlbum?.title }, label = selectedAlbum?.title ?: "Album")
                                    },
                                    onImportAlbumProfile = { albumSummary, albumTracks ->
                                        importAlbumProfile(albumSummary, albumTracks)
                                    },
                                    onOpenManualAlbumSearch = { albumSummary, albumTracks ->
                                        openManualAlbumSearch(albumSummary, albumTracks)
                                    },
                                    onManualAlbumSearchQueryChange = { albumSummary, query ->
                                        val albumKey = desktopAlbumKey(albumSummary.artist, albumSummary.title)
                                        albumManualSearchUiState = albumManualSearchUiState.copy(albumKey = albumKey, query = query)
                                    },
                                    onRunManualAlbumSearch = { albumSummary, query, albumTracks ->
                                        runManualAlbumSearch(albumSummary, query, albumTracks)
                                    },
                                    onImportAlbumProfileFromCandidate = { albumSummary, albumTracks, pageTitle ->
                                        importAlbumProfileFromManualCandidate(albumSummary, albumTracks, pageTitle)
                                    },
                                    onDismissManualAlbumSearch = { albumManualSearchUiState = DesktopAlbumManualSearchUiState() },
                                    trackMenu = trackMenuModel,
                                )

                                DesktopSection.ArtistDetail -> DesktopArtistDetail(
                                    artist = selectedArtist,
                                    artistProfileOverride = selectedArtistProfileOverride,
                                    artistLookupUiState = artistLookupUiState,
                                    artistManualSearchUiState = artistManualSearchUiState,
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
                                        openNowPlaying()
                                    },
                                    onPlayTrack = { track ->
                                        playTrack(track, queue = tracks.filter { selectedArtist?.name in it.artistCredits }, label = selectedArtist?.name ?: "Artist")
                                    },
                                    onUpdateAbout = { artistName, about ->
                                        updateArtistProfile(artistName = artistName, about = about)
                                    },
                                    onImportArtistProfile = { artistName ->
                                        importArtistProfile(
                                            artistName = artistName,
                                            artistTracks = tracks.filter { artistName in it.artistCredits },
                                        )
                                    },
                                    onOpenManualArtistSearch = { artistName ->
                                        openManualArtistSearch(
                                            artistName = artistName,
                                            artistTracks = tracks.filter { artistName in it.artistCredits },
                                        )
                                    },
                                    onManualArtistSearchQueryChange = { artistName, query ->
                                        artistManualSearchUiState = artistManualSearchUiState.copy(
                                            artistName = artistName,
                                            query = query,
                                        )
                                    },
                                    onRunManualArtistSearch = { artistName, query ->
                                        runManualArtistSearch(
                                            artistName = artistName,
                                            query = query,
                                            artistTracks = tracks.filter { artistName in it.artistCredits },
                                        )
                                    },
                                    onImportArtistProfileFromCandidate = { artistName, pageTitle ->
                                        importArtistProfileFromManualCandidate(
                                            artistName = artistName,
                                            pageTitle = pageTitle,
                                        )
                                    },
                                    onDismissManualArtistSearch = {
                                        artistManualSearchUiState = DesktopArtistManualSearchUiState()
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
                            onOpenNowPlaying = ::openNowPlaying,
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
                    lookupState = trackLookupUiState.takeIf { it.trackPath == track.path } ?: DesktopTrackLookupUiState(),
                    manualSearchState = trackManualSearchUiState.takeIf { it.trackPath == track.path } ?: DesktopTrackManualSearchUiState(),
                    onDismiss = {
                        editingTrack = null
                        trackLookupUiState = DesktopTrackLookupUiState()
                        trackManualSearchUiState = DesktopTrackManualSearchUiState()
                    },
                    onSave = { title, artist, album, genre, releaseDate ->
                        saveTrackMetadataOverride(track, title, artist, album, genre, releaseDate)
                    },
                    onAutoSearch = { importTrackProfile(track) },
                    onOpenManualSearch = { openManualTrackSearch(track) },
                    onManualSearchQueryChange = { query ->
                        trackManualSearchUiState = trackManualSearchUiState.copy(trackPath = track.path, query = query)
                    },
                    onRunManualSearch = { query -> runManualTrackSearch(track, query) },
                    onImportFromCandidate = { pageTitle -> importTrackProfileFromManualCandidate(track, pageTitle) },
                    onDismissManualSearch = { trackManualSearchUiState = DesktopTrackManualSearchUiState() },
                )
            }
            deletingTrack?.let { track ->
                AlertDialog(
                    onDismissRequest = { deletingTrack = null },
                    title = { Text("Delete from device", fontFamily = FontFamily.SansSerif) },
                    text = {
                        Text(
                            "Delete ${track.title} by ${track.artist} from this computer? This removes the real file from storage.",
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
                            text = "Desktop player",
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
private fun DesktopPreviewToolbar(
    selectedScenario: DesktopPreviewScenario,
    onScenarioSelect: (DesktopPreviewScenario) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Preview Lab",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.SansSerif,
            )
            DesktopPreviewScenario.entries.forEach { scenario ->
                val selected = scenario == selectedScenario
                Surface(
                    color = if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.clickable { onScenarioSelect(scenario) },
                ) {
                    Text(
                        text = scenario.label,
                        color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
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
                    label = { Text("Search your desktop library") },
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
                            text = "Desktop shell ready",
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
    val continueListeningTracks = remember(currentTrack, recentTracks) {
        (listOfNotNull(currentTrack) + recentTracks)
            .distinctBy(DesktopTrack::id)
            .take(8)
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
                            isScanning -> "VerseFlow is scanning your music folders and preparing your desktop library."
                            errorMessage != null -> errorMessage
                            libraryRootPath == null -> "Choose one or more music folders to import local songs into VerseFlow Desktop."
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
                        subtitle = "Your desktop library is now inside VerseFlow. Home highlights where to resume, what fits the moment, and who is shaping your rotation right now.",
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
                        SectionLabel("Favourite artists")
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(homeCardSpacing)) {
                            items(artists) { artist ->
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
    val genreTrackMap = remember(tracks) { tracks.groupBy { normalizeGenreLabel(it.genre) } }
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
                        title = if (isScanning) "Scanning your desktop library" else "No songs yet",
                        body = errorMessage ?: if (isScanning) {
                            "VerseFlow is importing local files from your selected folders."
                        } else {
                            "Choose one or more folders to start building the desktop library."
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
    val historyRecapCards = remember(historyEntries) { historyEntries.toListeningRecapCards() }
    val topArtists = remember(historyEntries) { historyEntries.topHistoryArtists() }
    val topAlbums = remember(historyEntries) { historyEntries.topHistoryAlbums() }
    val timePatterns = remember(historyEntries) { historyEntries.toTimeOfDayPatterns() }
    val heatmap = remember(historyEntries) { historyEntries.toHistoryHeatmap() }
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
                    if (historyRecapCards.isNotEmpty()) {
                        item {
                            SectionLabel("Weekly and monthly recaps")
                        }
                        item {
                            DesktopHistorySummaryGrid(cards = historyRecapCards)
                        }
                    }
                    if (heatmap.isNotEmpty()) {
                        item {
                            SectionLabel("Listening heatmap")
                        }
                        item {
                            DesktopHistoryHeatmap(heatmap = heatmap)
                        }
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
                    if (topAlbums.isNotEmpty()) {
                        item {
                            SectionLabel("Most-played albums")
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(topAlbums) { albumRecap ->
                                    DesktopHistoryAlbumCard(albumRecap = albumRecap)
                                }
                            }
                        }
                    }
                    if (timePatterns.isNotEmpty()) {
                        item {
                            SectionLabel("Time-of-day patterns")
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(timePatterns) { pattern ->
                                    DesktopHistoryTimePatternCard(pattern = pattern)
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
                        color = MaterialTheme.colorScheme.surfaceVariant,
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
        color = MaterialTheme.colorScheme.surfaceVariant,
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
private fun DesktopHistoryAlbumCard(
    albumRecap: DesktopHistoryAlbumRecap,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = albumRecap.album,
                color = FrostWhite,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.SansSerif,
            )
            Text(
                text = albumRecap.artist,
                color = MutedLavender,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.SansSerif,
            )
            Text(
                text = "${albumRecap.plays} plays • ${formatDurationLong(albumRecap.listenedMs)}",
                color = VerseBlue,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }
}

@Composable
private fun DesktopHistoryTimePatternCard(
    pattern: DesktopHistoryTimePattern,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = pattern.label,
                color = FrostWhite,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.SansSerif,
            )
            Text(
                text = "${pattern.plays} plays",
                color = VerseBlue,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.SansSerif,
            )
            Text(
                text = formatDurationLong(pattern.listenedMs),
                color = MutedLavender,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }
}

@Composable
private fun DesktopHistoryHeatmap(
    heatmap: List<List<DesktopHistoryHeatmapCell>>,
) {
    val weekdayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val totalPlays = heatmap.flatten().sumOf(DesktopHistoryHeatmapCell::playCount)

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Each square is one day. Brighter squares mean more plays on that day over the last ${heatmap.flatten().size} days.",
                color = MutedLavender,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.SansSerif,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$totalPlays plays logged",
                    color = FrostWhite,
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = FontFamily.SansSerif,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Less",
                        color = MutedLavender,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.SansSerif,
                    )
                    listOf(0, 1, 2, 3, 4).forEach { intensity ->
                        Surface(
                            color = historyHeatColor(intensity),
                            shape = RectangleShape,
                            modifier = Modifier.size(width = 18.dp, height = 12.dp),
                        ) {}
                    }
                    Text(
                        text = "More",
                        color = MutedLavender,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.SansSerif,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.width(34.dp),
                ) {
                    weekdayLabels.forEach { label ->
                        Box(
                            modifier = Modifier.height(18.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = label,
                                color = MutedLavender,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    heatmap.forEach { week ->
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            week.forEach { cell ->
                                Surface(
                                    color = historyHeatColor(cell.intensity),
                                    shape = RectangleShape,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp),
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun historyHeatColor(intensity: Int): Color =
    if (isDesktopMonochromeTheme(desktopThemeForArtwork)) {
        when (intensity) {
            0 -> MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
            1 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
            2 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f)
            3 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
        }
    } else {
        when (intensity) {
            0 -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            1 -> VerseBlue.copy(alpha = 0.28f)
            2 -> VerseBlue.copy(alpha = 0.46f)
            3 -> AuroraCyan.copy(alpha = 0.56f)
            else -> AuroraCyan.copy(alpha = 0.78f)
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
        color = MaterialTheme.colorScheme.surfaceVariant,
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
    onBack: () -> Unit,
    onChooseFolder: () -> Unit,
) {
    if (track == null) {
        EmptyDesktopPanel(
            title = "No track loaded",
            body = "Choose one or more folders, import songs, then select one from Library to start building the desktop playback flow.",
            actionLabel = "Choose Folders",
            onAction = onChooseFolder,
        )
        return
    }
    var isSeeking by remember(track.id) { mutableStateOf(false) }
    var seekPositionMs by remember(track.id) { mutableFloatStateOf(positionMs.toFloat()) }
    val durationValue = track.durationMs.coerceAtLeast(1L).toFloat()
    val immersiveNowPlaying = desktopThemeNameCompatibility(desktopThemeForArtwork) == "Immersive Flow"
    val playerAccent = if (immersiveNowPlaying) {
        lerp(
            track.palette.getOrElse(2) { track.palette.getOrElse(0) { VerseBlue } },
            track.palette.getOrElse(0) { VerseBlue },
            0.28f,
        )
    } else {
        VerseBlue
    }
    val playerAccentStrong = if (immersiveNowPlaying) lerp(playerAccent, Color.White, 0.06f) else playerAccent
    val playerAccentSoft = if (immersiveNowPlaying) playerAccent.copy(alpha = 0.24f) else playerAccent.copy(alpha = 0.22f)
    val playerAccentSoftEmphasis = if (immersiveNowPlaying) playerAccent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
    val playerInactiveTrack = if (immersiveNowPlaying) playerAccent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.12f)
    val playerSecondary = if (immersiveNowPlaying) lerp(playerAccent, MaterialTheme.colorScheme.secondary, 0.35f) else MaterialTheme.colorScheme.secondary

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
        Column(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight()
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                SecondaryChip(label = "Back", onClick = onBack)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = track.title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = track.artist,
                style = MaterialTheme.typography.headlineSmall,
                color = playerSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = track.album,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            errorMessage?.let { playbackError ->
                Text(
                    text = playbackError,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFF8E8E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
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
                    thumbColor = playerAccentStrong,
                    activeTrackColor = playerAccentStrong,
                    inactiveTrackColor = playerInactiveTrack,
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
                        activeColor = playerAccentStrong,
                        activeBackgroundColor = playerAccentSoft,
                        emphasisColor = playerSecondary,
                        emphasisBackgroundColor = playerAccentSoftEmphasis,
                        onClick = { onToggleFavorite(track) },
                    )
                    DesktopPlayerActionChip(
                        icon = Icons.Rounded.Lyrics,
                        label = "Lyrics",
                        contentDescription = "Open lyrics",
                        selected = false,
                        emphasize = true,
                        activeColor = playerAccentStrong,
                        activeBackgroundColor = playerAccentSoft,
                        emphasisColor = playerSecondary,
                        emphasisBackgroundColor = playerAccentSoftEmphasis,
                        onClick = onOpenLyrics,
                    )
                    DesktopPlayerActionChip(
                        icon = Icons.Rounded.Repeat,
                        label = "Repeat",
                        contentDescription = if (isRepeatEnabled) "Disable repeat" else "Enable repeat",
                        selected = isRepeatEnabled,
                        activeColor = playerAccentStrong,
                        activeBackgroundColor = playerAccentSoft,
                        emphasisColor = playerSecondary,
                        emphasisBackgroundColor = playerAccentSoftEmphasis,
                        onClick = onToggleRepeat,
                    )
                    DesktopPlayerActionChip(
                        icon = Icons.Rounded.Shuffle,
                        label = "Shuffle",
                        contentDescription = if (isShuffleEnabled) "Disable shuffle" else "Enable shuffle",
                        selected = isShuffleEnabled,
                        activeColor = playerAccentStrong,
                        activeBackgroundColor = playerAccentSoft,
                        emphasisColor = playerSecondary,
                        emphasisBackgroundColor = playerAccentSoftEmphasis,
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
                        Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous", tint = playerAccentStrong)
                    }
                    IconButton(onClick = onPlayPause, modifier = Modifier.size(72.dp)) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayArrow,
                            contentDescription = "Play pause",
                            tint = playerAccentStrong,
                            modifier = Modifier.size(64.dp),
                        )
                    }
                    IconButton(onClick = onNext) {
                        Icon(Icons.Rounded.SkipNext, contentDescription = "Next", tint = playerAccentStrong)
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
    albumProfileOverride: DesktopAlbumProfileOverride?,
    albumLookupUiState: DesktopAlbumLookupUiState,
    albumManualSearchUiState: DesktopAlbumManualSearchUiState,
    tracks: List<DesktopTrack>,
    historyEntries: List<DesktopPlayHistoryEntry>,
    currentTrack: DesktopTrack?,
    onBack: () -> Unit,
    onOpenArtist: (String) -> Unit,
    onPlayAlbum: (List<DesktopTrack>) -> Unit,
    onPlayTrack: (DesktopTrack) -> Unit,
    onImportAlbumProfile: (DesktopAlbumSummary, List<DesktopTrack>) -> Unit,
    onOpenManualAlbumSearch: (DesktopAlbumSummary, List<DesktopTrack>) -> Unit,
    onManualAlbumSearchQueryChange: (DesktopAlbumSummary, String) -> Unit,
    onRunManualAlbumSearch: (DesktopAlbumSummary, String, List<DesktopTrack>) -> Unit,
    onImportAlbumProfileFromCandidate: (DesktopAlbumSummary, List<DesktopTrack>, String) -> Unit,
    onDismissManualAlbumSearch: () -> Unit,
    trackMenu: DesktopTrackMenuModel,
) {
    if (album == null) {
        EmptyDesktopPanel(
            title = "No album selected",
            body = "Open an album from Home or Library to see its desktop detail page.",
        )
        return
    }

    val albumKey = remember(album.artist, album.title) { desktopAlbumKey(album.artist, album.title) }
    val lookupState = albumLookupUiState.takeIf { it.albumKey == albumKey }
    val manualSearchState = albumManualSearchUiState.takeIf { it.albumKey == albumKey }
    var selectedTab by remember(albumKey) { mutableStateOf(DesktopAlbumDetailTab.Tracks) }
    val stats = remember(albumKey, tracks, historyEntries) {
        val matchingHistory = historyEntries.filter { it.artist == album.artist && it.album == album.title }
        DesktopAlbumStats(
            playCount = matchingHistory.size,
            listenedMs = matchingHistory.sumOf(DesktopPlayHistoryEntry::listenedMs),
            lastPlayedAtMs = matchingHistory.maxOfOrNull(DesktopPlayHistoryEntry::playedAtMs),
            firstAddedAtMs = tracks.minOfOrNull(DesktopTrack::addedAtMs)?.takeIf { it > 0L },
            newestAddedAtMs = tracks.maxOfOrNull(DesktopTrack::addedAtMs)?.takeIf { it > 0L },
            trackCount = tracks.size,
            totalDurationMs = tracks.sumOf(DesktopTrack::durationMs),
        )
    }
    val displayReleaseDate = albumProfileOverride?.releaseDate ?: album.releaseDate
    val displayGenre = albumProfileOverride?.genre ?: album.genre
    val displayAbout = albumProfileOverride?.about
    val importedTrackTitles = albumProfileOverride?.trackTitles.orEmpty()
    val localTrackTitleKeys = remember(tracks) { tracks.map { normalizeAlbumTrackTitleForMatch(it.title) }.toSet() }
    val ownedImportedTrackCount = remember(importedTrackTitles, localTrackTitleKeys) {
        importedTrackTitles.count { normalizeAlbumTrackTitleForMatch(it) in localTrackTitleKeys }
    }
    val trackCoverageLabel = remember(stats.trackCount, albumProfileOverride?.totalTrackCount, ownedImportedTrackCount, importedTrackTitles) {
        when {
            albumProfileOverride?.totalTrackCount != null -> "${ownedImportedTrackCount}/${albumProfileOverride.totalTrackCount}"
            importedTrackTitles.isNotEmpty() -> "${ownedImportedTrackCount}/${importedTrackTitles.size}"
            else -> stats.trackCount.toString()
        }
    }
    val infoRows = remember(album, displayGenre, displayReleaseDate, stats, albumProfileOverride, trackCoverageLabel) {
        listOfNotNull(
            "Album artist" to album.artist,
            "Release date" to displayReleaseDate,
            "Genre" to displayGenre.takeUnless(::isMissingGenreLabel),
            "Tracks" to trackCoverageLabel,
            "Runtime" to formatDuration(stats.totalDurationMs),
            albumProfileOverride?.sourcePageTitle?.let { "Source" to it },
        )
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Surface(
            modifier = Modifier.weight(0.9f).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
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
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentScale = ContentScale.Fit,
                )
                Text(album.title, style = MaterialTheme.typography.headlineLarge, color = FrostWhite)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(splitDesktopArtists(album.artist)) { artistName ->
                        SecondaryChip(label = artistName, onClick = { onOpenArtist(artistName) })
                    }
                }
                Text(
                    text = "${album.trackCount} songs • ${formatDuration(album.durationMs)} • ${displayGenreLabel(album.genre)}",
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
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        )

        Box(
            modifier = Modifier.weight(1.1f).fillMaxHeight(),
        ) {
            DesktopBlurredArtworkBackdrop(
                artworkBytes = album.artworkBytes,
                palette = album.palette,
                modifier = Modifier.fillMaxSize(),
            )
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = SurfaceGlass.copy(alpha = 0.88f),
                shape = RectangleShape,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SectionLabel(selectedTab.title)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DesktopAlbumDetailTab.entries.forEach { tab ->
                                Surface(
                                    color = if (selectedTab == tab) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.44f),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.clickable { selectedTab = tab },
                                ) {
                                    Text(
                                        text = tab.title,
                                        color = if (selectedTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }
                            }
                        }
                    }
                    when (selectedTab) {
                        DesktopAlbumDetailTab.Tracks -> {
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

                        DesktopAlbumDetailTab.Info -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        PrimaryChip(
                                            label = if (lookupState?.isLoading == true) "Searching..." else "Search album info",
                                            onClick = { onImportAlbumProfile(album, tracks) },
                                        )
                                        SecondaryChip(
                                            label = "Manual search",
                                            onClick = { onOpenManualAlbumSearch(album, tracks) },
                                        )
                                        lookupState?.message?.let { message ->
                                            Text(
                                                text = message,
                                                color = if (lookupState.isError) Color(0xFFFFA5A5) else MutedLavender,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        }
                                    }
                                }
                                item {
                                    DesktopInfoBlock(
                                        title = "About",
                                        body = displayAbout ?: "Search Wikipedia to import a short album overview. Local metadata like release date and genre will still appear below when available.",
                                    )
                                }
                                item {
                                    DesktopInfoGrid(rows = infoRows)
                                }
                                if (importedTrackTitles.isNotEmpty()) {
                                    item {
                                        DesktopAlbumTrackCoverageBlock(
                                            trackTitles = importedTrackTitles,
                                            localTrackTitleKeys = localTrackTitleKeys,
                                        )
                                    }
                                }
                            }
                        }

                        DesktopAlbumDetailTab.Stats -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                item {
                                    DesktopInfoGrid(
                                        rows = listOf(
                                            "Play count" to stats.playCount.toString(),
                                            "Hours listened" to formatDurationLong(stats.listenedMs),
                                            "Last played" to stats.lastPlayedAtMs?.let(::formatDesktopDateTime).orEmpty().ifBlank { "Not yet" },
                                            "First added" to stats.firstAddedAtMs?.let(::formatDesktopDateTime).orEmpty().ifBlank { "Unknown" },
                                            "Last added" to stats.newestAddedAtMs?.let(::formatDesktopDateTime).orEmpty().ifBlank { "Unknown" },
                                            "Average track length" to tracks.takeIf { it.isNotEmpty() }?.let { formatDuration(stats.totalDurationMs / it.size) }.orEmpty().ifBlank { "0:00" },
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (manualSearchState != null) {
        AlertDialog(
            onDismissRequest = onDismissManualAlbumSearch,
            title = { Text("Manual album search", fontFamily = FontFamily.SansSerif) },
            text = {
                val manualSearchScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 560.dp)
                        .verticalScroll(manualSearchScrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = manualSearchState.query,
                        onValueChange = { onManualAlbumSearchQueryChange(album, it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search Wikipedia") },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PrimaryChip(
                            label = if (manualSearchState.isLoading) "Searching..." else "Search",
                            onClick = { onRunManualAlbumSearch(album, manualSearchState.query, tracks) },
                        )
                    }
                    manualSearchState.message?.let { message ->
                        Text(
                            text = message,
                            color = MutedLavender,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        manualSearchState.candidates.forEach { candidate ->
                            Surface(
                                color = Color(0xFF101520),
                                shape = RoundedCornerShape(18.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = candidate.pageTitle,
                                        color = FrostWhite,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    if (candidate.snippet.isNotBlank()) {
                                        Text(
                                            text = candidate.snippet,
                                            color = MutedLavender,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                    SecondaryChip(
                                        label = "Use this result",
                                        onClick = {
                                            onImportAlbumProfileFromCandidate(album, tracks, candidate.pageTitle)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissManualAlbumSearch) {
                    Text("Close", fontFamily = FontFamily.SansSerif)
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopArtistDetail(
    artist: DesktopArtistSummary?,
    artistProfileOverride: DesktopArtistProfileOverride?,
    artistLookupUiState: DesktopArtistLookupUiState,
    artistManualSearchUiState: DesktopArtistManualSearchUiState,
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
    onImportArtistProfile: (String) -> Unit,
    onOpenManualArtistSearch: (String) -> Unit,
    onManualArtistSearchQueryChange: (String, String) -> Unit,
    onRunManualArtistSearch: (String, String) -> Unit,
    onImportArtistProfileFromCandidate: (String, String) -> Unit,
    onDismissManualArtistSearch: () -> Unit,
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
    val artistLookupMessage = artistLookupUiState
        .takeIf { it.artistName == artist.name }
        ?.message
    val manualSearchState = artistManualSearchUiState.takeIf { it.artistName == artist.name }
    val isArtistLookupRunning = artistLookupUiState.artistName == artist.name && artistLookupUiState.isLoading
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
                        SecondaryChip(
                            label = if (isArtistLookupRunning) "Searching..." else "Search artist info",
                            onClick = { onImportArtistProfile(artist.name) },
                        )
                        SecondaryChip(
                            label = "Manual search",
                            onClick = { onOpenManualArtistSearch(artist.name) },
                        )
                        SecondaryChip(label = "Change photo", onClick = { onChooseArtistPhoto(artist.name) })
                        if (artist.artworkBytes != null || !artistProfileOverride?.photoPath.isNullOrBlank()) {
                            SecondaryChip(label = "Clear photo", onClick = { onClearArtistPhoto(artist.name) })
                        }
                    }
                    artistLookupMessage?.let { message ->
                        Text(
                            text = message,
                            color = if (artistLookupUiState.isError) Color(0xFFFF8D8D) else MutedLavender,
                            style = MaterialTheme.typography.bodyMedium,
                        )
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
                                            body = "VerseFlow could not find tracks linked to this artist in your desktop library.",
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
                                            body = "This artist does not have album-grouped tracks in your current desktop library scan.",
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
                                            body = "VerseFlow has not found this artist as a featured guest on other tracks in your desktop library yet.",
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
                                if (artistAbout.isNotBlank()) {
                                    item {
                                        Surface(
                                            color = Color(0xFF101520),
                                            shape = RoundedCornerShape(22.dp),
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                            ) {
                                                Text(
                                                    text = "Artist bio",
                                                    color = FrostWhite,
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                                Text(
                                                    text = artistAbout,
                                                    color = MutedLavender,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    OutlinedTextField(
                                        value = artistAbout,
                                        onValueChange = { onUpdateAbout(artist.name, it) },
                                        modifier = Modifier.fillMaxWidth().height(132.dp),
                                        label = { Text("Artist notes") },
                                    )
                                }
                                if (artistLookupMessage != null) {
                                    item {
                                        Text(
                                            text = artistLookupMessage,
                                            color = if (artistLookupUiState.isError) Color(0xFFFF8D8D) else MutedLavender,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
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

    if (manualSearchState != null) {
        AlertDialog(
            onDismissRequest = onDismissManualArtistSearch,
            title = { Text("Manual artist search", fontFamily = FontFamily.SansSerif) },
            text = {
                val manualSearchScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 560.dp)
                        .verticalScroll(manualSearchScrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = manualSearchState.query,
                        onValueChange = { onManualArtistSearchQueryChange(artist.name, it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search Wikipedia") },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PrimaryChip(
                            label = if (manualSearchState.isLoading) "Searching..." else "Search",
                            onClick = { onRunManualArtistSearch(artist.name, manualSearchState.query) },
                        )
                    }
                    manualSearchState.message?.let { message ->
                        Text(
                            text = message,
                            color = MutedLavender,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        manualSearchState.candidates.forEach { candidate ->
                            Surface(
                                color = Color(0xFF101520),
                                shape = RoundedCornerShape(18.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = candidate.pageTitle,
                                        color = FrostWhite,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    if (candidate.snippet.isNotBlank()) {
                                        Text(
                                            text = candidate.snippet,
                                            color = MutedLavender,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                    SecondaryChip(
                                        label = "Use this result",
                                        onClick = {
                                            onImportArtistProfileFromCandidate(artist.name, candidate.pageTitle)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissManualArtistSearch) {
                    Text("Close", fontFamily = FontFamily.SansSerif)
                }
            },
        )
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
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        itemsIndexed(track.lyrics) { index, line ->
                            val isActive = index == activeLyricIndex
                            Text(
                                text = line.text,
                                color = if (isActive) FrostWhite else MutedLavender,
                                fontSize = if (isActive) 22.sp else 18.sp,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSeekTo(line.timestampMs) }
                                    .padding(horizontal = 18.dp, vertical = 10.dp),
                            )
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
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(track.plainLyrics) { line ->
                            Text(
                                text = line,
                                color = MutedLavender,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 10.dp),
                            )
                        }
                    }
                }
                lyricsStatus == DesktopLyricsLoadState.Unavailable -> {
                    EmptyDesktopPanel(
                        title = "No lyrics found",
                        body = "VerseFlow could not find a reliable lyrics match for this desktop track from the current local and online sources.",
                    )
                }
                else -> {
                    EmptyDesktopPanel(
                        title = "Lyrics desktop pass is next",
                        body = "Pick a track and VerseFlow will search for lyrics automatically as the desktop playback flow grows.",
                    )
                }
            }
        }
    }
}

@Composable
private fun DesktopTrackEditDialog(
    track: DesktopTrack,
    lookupState: DesktopTrackLookupUiState,
    manualSearchState: DesktopTrackManualSearchUiState,
    onDismiss: () -> Unit,
    onSave: (title: String, artist: String, album: String, genre: String, releaseDate: String) -> Unit,
    onAutoSearch: () -> Unit,
    onOpenManualSearch: () -> Unit,
    onManualSearchQueryChange: (String) -> Unit,
    onRunManualSearch: (String) -> Unit,
    onImportFromCandidate: (String) -> Unit,
    onDismissManualSearch: () -> Unit,
) {
    var title by remember(track.path) { mutableStateOf(track.title) }
    var artist by remember(track.path) { mutableStateOf(track.artist) }
    var album by remember(track.path) { mutableStateOf(track.album) }
    var genre by remember(track.path) { mutableStateOf(displayGenreLabel(track.genre)) }
    var releaseDate by remember(track.path) { mutableStateOf(track.releaseDate.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Music Info", fontFamily = FontFamily.SansSerif) },
        text = {
            val dialogScroll = rememberScrollState()
            Column(
                modifier = Modifier.heightIn(max = 620.dp).verticalScroll(dialogScroll),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    PrimaryChip(
                        label = if (lookupState.isLoading) "Searching..." else "Search song info",
                        onClick = onAutoSearch,
                    )
                    SecondaryChip(label = "Manual search", onClick = onOpenManualSearch)
                }
                lookupState.message?.let { message ->
                    Text(
                        text = message,
                        color = if (lookupState.isError) Color(0xFFFFA5A5) else MutedLavender,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
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
                OutlinedTextField(
                    value = releaseDate,
                    onValueChange = { releaseDate = it },
                    singleLine = true,
                    label = { Text("Release year") },
                )
                if (manualSearchState.trackPath == track.path) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.56f),
                        shape = RectangleShape,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = manualSearchState.query,
                                    onValueChange = onManualSearchQueryChange,
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    label = { Text("Search Wikipedia") },
                                )
                                SecondaryChip(
                                    label = if (manualSearchState.isLoading) "Searching..." else "Search",
                                    onClick = { onRunManualSearch(manualSearchState.query) },
                                )
                            }
                            manualSearchState.message?.let { message ->
                                Text(message, color = MutedLavender, style = MaterialTheme.typography.bodySmall)
                            }
                            manualSearchState.candidates.forEach { candidate ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.48f),
                                    shape = RectangleShape,
                                    modifier = Modifier.fillMaxWidth().clickable { onImportFromCandidate(candidate.pageTitle) },
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text(candidate.pageTitle, color = FrostWhite, style = MaterialTheme.typography.titleSmall)
                                        Text(candidate.snippet.ifBlank { "Wikipedia result" }, color = MutedLavender, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            TextButton(onClick = onDismissManualSearch) {
                                Text("Close manual search", fontFamily = FontFamily.SansSerif)
                            }
                        }
                    }
                }
                Text(
                    "These edits are VerseFlow-only overrides for the desktop app. The original audio file tags stay untouched.",
                    fontFamily = FontFamily.SansSerif,
                    color = MutedLavender,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(title, artist, album, genre, releaseDate)
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
                            "Search manually and save the selected lyrics for this track on your desktop.",
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
    lastFmApiKey: String,
    onLastFmApiKeyChange: (String) -> Unit,
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
                                "When enabled, VerseFlow refreshes your desktop library again after opening the app and after you change the watched folders.",
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
                        text = "VerseFlow uses LRCLIB and lyrics.ovh by default. Add your Musixmatch key here to enable it as a third lyrics source on desktop.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedLavender,
                    )
                    SectionLabel("Song metadata fallback")
                    OutlinedTextField(
                        value = lastFmApiKey,
                        onValueChange = onLastFmApiKeyChange,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Last.fm API key (optional)") },
                    )
                    Text(
                        text = "VerseFlow can use Last.fm as a fallback source for song genres when Wikipedia does not have a usable page.",
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
                        text = "VerseFlow can scan one or many folders on your computer. Add folders here, remove them later, or drag folders in from your file manager.",
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
                                    text = "No folders selected yet. Add one or more music folders to build the desktop library.",
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
                            "<span style='color:#B5BDD6;font-size:11px;'>VerseFlow will add them to your desktop library sources.</span>" +
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
                        colorFilter = desktopArtworkColorFilter(),
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
                        DesktopBadgeContent(
                            badgeText = badgeText,
                            badgeIcon = badgeIcon,
                            badgeHighlighted = badgeHighlighted,
                        )
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
private fun DesktopBadgeContent(
    badgeText: String?,
    badgeIcon: androidx.compose.ui.graphics.vector.ImageVector?,
    badgeHighlighted: Boolean,
) {
    val isLiveBadge = badgeHighlighted && badgeText == "LIVE" && badgeIcon == null
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "desktopLiveBadge")
    val pulseScale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "desktopLivePulseScale",
    )
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "desktopLivePulseAlpha",
    )
    val dotAlpha by transition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 560),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "desktopLiveDotAlpha",
    )

    Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLiveBadge) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .alpha(pulseAlpha)
                        .background(FrostWhite, CircleShape),
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(dotAlpha)
                        .background(FrostWhite, CircleShape),
                )
            }
        }
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
    contentScale: ContentScale = ContentScale.Crop,
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
                contentScale = contentScale,
                colorFilter = desktopArtworkColorFilter(),
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
private fun DesktopBlurredArtworkBackdrop(
    artworkBytes: ByteArray?,
    palette: List<Color>,
    modifier: Modifier = Modifier,
) {
    val artwork = rememberArtworkBitmap(artworkBytes)
    val baseTone = palette.firstOrNull() ?: DeepSpace
    val midTone = palette.getOrNull(1) ?: baseTone
    val highlightTone = palette.lastOrNull() ?: VerseBlue
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    listOf(
                        baseTone.copy(alpha = 0.64f),
                        midTone.copy(alpha = 0.46f),
                        InkBlack,
                    ),
                ),
            ),
    ) {
        if (artwork != null) {
            Image(
                bitmap = artwork,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = 1.22f, scaleY = 1.22f)
                    .blur(52.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.26f,
                colorFilter = desktopArtworkColorFilter(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(highlightTone.copy(alpha = 0.22f), Color.Transparent),
                    ),
                ),
        )
    }
}

@Composable
private fun DesktopInfoBlock(
    title: String,
    body: String,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = FrostWhite)
            Text(body, style = MaterialTheme.typography.bodyLarge, color = MutedLavender)
        }
    }
}

@Composable
private fun DesktopInfoGrid(
    rows: List<Pair<String, String?>>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.filter { !it.second.isNullOrBlank() }.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { (label, value) ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f),
                        shape = RectangleShape,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(label, style = MaterialTheme.typography.labelLarge, color = MutedLavender)
                            Text(value.orEmpty(), style = MaterialTheme.typography.bodyLarge, color = FrostWhite)
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DesktopAlbumTrackCoverageBlock(
    trackTitles: List<String>,
    localTrackTitleKeys: Set<String>,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Track coverage", style = MaterialTheme.typography.titleMedium, color = FrostWhite)
            Text(
                "Imported album track list. Brighter rows are already in your local library.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedLavender,
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                trackTitles.forEachIndexed { index, title ->
                    val owned = normalizeAlbumTrackTitleForMatch(title) in localTrackTitleKeys
                    Surface(
                        color = if (owned) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.38f)
                        },
                        shape = RectangleShape,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${index + 1}. $title",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (owned) MaterialTheme.colorScheme.primary else FrostWhite,
                            )
                            Text(
                                text = if (owned) "In library" else "Missing",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (owned) MaterialTheme.colorScheme.primary else MutedLavender,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopAppBackdrop(track: DesktopTrack?) {
    val immersiveNowPlaying = desktopThemeNameCompatibility(desktopThemeForArtwork) == "Immersive Flow"
    val palette = if (isDesktopMonochromeTheme(desktopThemeForArtwork)) {
        listOf(Color(0xFF000000), Color(0xFF111111), Color(0xFF2B2B2B))
    } else {
        track?.palette ?: listOf(InkBlack, Color(0xFF060B1B), Color(0xFF0A1226))
    }
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
                        baseTone.copy(alpha = if (immersiveNowPlaying) 0.62f else 0.48f),
                        midTone.copy(alpha = if (immersiveNowPlaying) 0.40f else 0.28f),
                        InkBlack,
                    ),
                ),
            ),
    ) {
        if (artwork != null) {
            Image(
                bitmap = artwork,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = if (immersiveNowPlaying) 1.24f else 1.18f,
                        scaleY = if (immersiveNowPlaying) 1.24f else 1.18f,
                    )
                    .blur(if (immersiveNowPlaying) 56.dp else 48.dp),
                contentScale = ContentScale.Crop,
                alpha = if (immersiveNowPlaying) 0.30f else 0.22f,
                colorFilter = desktopArtworkColorFilter(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            highlightTone.copy(alpha = if (immersiveNowPlaying) 0.34f else 0.22f),
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
                            highlightTone.copy(alpha = if (immersiveNowPlaying) 0.12f else 0.06f),
                            Color.Transparent,
                            baseTone.copy(alpha = if (immersiveNowPlaying) 0.18f else 0.10f),
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
                            InkBlack.copy(alpha = 0.20f),
                            DeepSpace.copy(alpha = 0.42f),
                            InkBlack.copy(alpha = 0.82f),
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
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun DesktopPlayerActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    contentDescription: String,
    selected: Boolean,
    emphasize: Boolean = false,
    activeColor: Color = VerseBlue,
    activeBackgroundColor: Color = VerseBlue.copy(alpha = 0.22f),
    emphasisColor: Color = MaterialTheme.colorScheme.secondary,
    emphasisBackgroundColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
    onClick: () -> Unit,
) {
    val backgroundColor = when {
        selected -> activeBackgroundColor
        emphasize -> emphasisBackgroundColor
        else -> Color.White.copy(alpha = 0.05f)
    }
    val foregroundColor = when {
        selected -> activeColor
        emphasize -> emphasisColor
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
                colorFilter = desktopArtworkColorFilter(),
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

private fun List<DesktopPlayHistoryEntry>.toListeningRecapCards(): List<DesktopHistorySummaryCard> {
    if (isEmpty()) return emptyList()
    val now = System.currentTimeMillis()
    val weekCutoff = now - Duration.ofDays(7).toMillis()
    val monthCutoff = now - Duration.ofDays(30).toMillis()
    val weeklyEntries = filter { it.playedAtMs >= weekCutoff }
    val monthlyEntries = filter { it.playedAtMs >= monthCutoff }
    val longestStreak = listeningStreaks().maxOfOrNull { it.second } ?: 0
    val currentStreak = currentListeningStreak()

    return listOf(
        DesktopHistorySummaryCard(
            "Last 7 days",
            if (weeklyEntries.isEmpty()) "0 plays" else "${weeklyEntries.size} plays",
            formatDurationLong(weeklyEntries.sumOf(DesktopPlayHistoryEntry::listenedMs)),
        ),
        DesktopHistorySummaryCard(
            "Last 30 days",
            if (monthlyEntries.isEmpty()) "0 plays" else "${monthlyEntries.size} plays",
            formatDurationLong(monthlyEntries.sumOf(DesktopPlayHistoryEntry::listenedMs)),
        ),
        DesktopHistorySummaryCard(
            "Current streak",
            if (currentStreak == 1) "1 day" else "$currentStreak days",
            "Consecutive listening days",
        ),
        DesktopHistorySummaryCard(
            "Longest streak",
            if (longestStreak == 1) "1 day" else "$longestStreak days",
            "Best run so far",
        ),
    )
}

private fun List<DesktopPlayHistoryEntry>.topHistoryArtists(limit: Int = 6): List<Pair<String, Int>> =
    groupingBy(DesktopPlayHistoryEntry::artist)
        .eachCount()
        .entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key.lowercase() })
        .take(limit)
        .map { it.key to it.value }

private fun List<DesktopPlayHistoryEntry>.topHistoryAlbums(limit: Int = 6): List<DesktopHistoryAlbumRecap> =
    groupBy { "${it.artist}::${it.album}" }
        .values
        .sortedWith(
            compareByDescending<List<DesktopPlayHistoryEntry>> { entries -> entries.size }
                .thenByDescending { entries -> entries.sumOf(DesktopPlayHistoryEntry::listenedMs) }
                .thenBy { entries -> entries.first().album.lowercase() },
        )
        .take(limit)
        .map { entries ->
            DesktopHistoryAlbumRecap(
                artist = entries.first().artist,
                album = entries.first().album,
                plays = entries.size,
                listenedMs = entries.sumOf(DesktopPlayHistoryEntry::listenedMs),
            )
        }

private fun List<DesktopPlayHistoryEntry>.toTimeOfDayPatterns(): List<DesktopHistoryTimePattern> {
    if (isEmpty()) return emptyList()
    return listOf(
        "Morning" to entriesForHours(5..11),
        "Afternoon" to entriesForHours(12..16),
        "Evening" to entriesForHours(17..21),
        "Late night" to (entriesForHours(22..23) + entriesForHours(0..4)),
    ).mapNotNull { (label, entries) ->
        if (entries.isEmpty()) null else DesktopHistoryTimePattern(
            label = label,
            plays = entries.size,
            listenedMs = entries.sumOf(DesktopPlayHistoryEntry::listenedMs),
        )
    }.sortedByDescending(DesktopHistoryTimePattern::listenedMs)
}

private fun List<DesktopPlayHistoryEntry>.toHistoryHeatmap(days: Int = 84): List<List<DesktopHistoryHeatmapCell>> {
    if (isEmpty()) return emptyList()
    val endDate = LocalDate.now()
    val startDate = endDate.minusDays((days - 1).toLong())
    val playsByDate = groupBy {
        Instant.ofEpochMilli(it.playedAtMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }.mapValues { (_, entries) -> entries.size }
    val maxPlays = playsByDate.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val firstGridDate = startDate.minusDays(startDate.dayOfWeek.ordinal.toLong())
    val totalDays = java.time.temporal.ChronoUnit.DAYS.between(firstGridDate, endDate).toInt() + 1
    return (0 until totalDays)
        .map { offset -> firstGridDate.plusDays(offset.toLong()) }
        .chunked(7)
        .map { week ->
            week.map { date ->
                val playCount = playsByDate[date] ?: 0
                val intensity = when {
                    playCount <= 0 -> 0
                    playCount >= maxPlays -> 4
                    else -> ((playCount.toFloat() / maxPlays.toFloat()) * 4f).toInt().coerceIn(1, 3)
                }
                DesktopHistoryHeatmapCell(
                    date = date,
                    intensity = intensity,
                    playCount = playCount,
                )
            }
        }
}

private fun List<DesktopPlayHistoryEntry>.entriesForHours(hours: IntRange): List<DesktopPlayHistoryEntry> =
    filter { entry ->
        val hour = Instant.ofEpochMilli(entry.playedAtMs).atZone(ZoneId.systemDefault()).hour
        hour in hours
    }

private fun List<DesktopPlayHistoryEntry>.currentListeningStreak(): Int {
    val daySet = listeningDays()
    if (daySet.isEmpty()) return 0
    var cursor = when {
        LocalDate.now() in daySet -> LocalDate.now()
        LocalDate.now().minusDays(1) in daySet -> LocalDate.now().minusDays(1)
        else -> return 0
    }
    var streak = 0
    while (cursor in daySet) {
        streak += 1
        cursor = cursor.minusDays(1)
    }
    return streak
}

private fun List<DesktopPlayHistoryEntry>.listeningStreaks(): List<Pair<LocalDate, Int>> {
    val sortedDays = listeningDays().sorted()
    if (sortedDays.isEmpty()) return emptyList()
    val streaks = mutableListOf<Pair<LocalDate, Int>>()
    var streakStart = sortedDays.first()
    var streakLength = 1
    for (index in 1 until sortedDays.size) {
        val current = sortedDays[index]
        val previous = sortedDays[index - 1]
        if (current == previous.plusDays(1)) {
            streakLength += 1
        } else {
            streaks += streakStart to streakLength
            streakStart = current
            streakLength = 1
        }
    }
    streaks += streakStart to streakLength
    return streaks
}

private fun List<DesktopPlayHistoryEntry>.listeningDays(): Set<LocalDate> =
    map {
        Instant.ofEpochMilli(it.playedAtMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }.toSet()

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

private fun formatDesktopDateTime(timestampMs: Long): String =
    Instant.ofEpochMilli(timestampMs)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a"))

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
        .groupBy { normalizeGenreLabel(it.genre) }
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
    val throwbackTracks = tracks
        .filter { track -> (extractReleaseYear(track.releaseDate) ?: Int.MAX_VALUE) < 2010 }
        .sortedWith(
            compareBy<DesktopTrack> { extractReleaseYear(it.releaseDate) ?: Int.MAX_VALUE }
                .thenBy { it.title.lowercase() },
        )

    val candidates = buildList {
        if (recentTracks.isNotEmpty()) {
            add(
                DesktopPlaylistSummary(
                    id = "smart_recent_replay",
                    title = "Recent Replay",
                    subtitle = "Your latest desktop spins",
                    description = "A smart mix built from the songs you played most recently on desktop.",
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
        if (throwbackTracks.isNotEmpty()) {
            add(
                DesktopPlaylistSummary(
                    id = "smart_throwbacks",
                    title = "Throwbacks",
                    subtitle = "Built from release years before 2010",
                    description = "A smart playlist that pulls in every song in your library with a release year before 2010.",
                    supporting = "${throwbackTracks.size} songs • ${formatDuration(throwbackTracks.sumOf(DesktopTrack::durationMs))}",
                    tracks = throwbackTracks,
                    palette = throwbackTracks.first().palette,
                    artworkBytes = throwbackTracks.firstNotNullOfOrNull(DesktopTrack::artworkBytes),
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
        description = "A permanent VerseFlow playlist that collects every song you like in the desktop app.",
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
        .groupBy { normalizeGenreLabel(it.genre) }
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

private fun buildDesktopPreviewData(): DesktopPreviewData {
    val previewArtworkPath = "/Users/aliceakinyiolango/Documents/GitHub/verseflow/testalbumart.jpg"
    val artworkBytes = loadDesktopImageBytes(previewArtworkPath)
    val crimsonPalette = listOf(Color(0xFF120305), Color(0xFF6F1222), Color(0xFFE58A98))
    val cobaltPalette = listOf(Color(0xFF07111F), Color(0xFF1B4B8F), Color(0xFF86D6FF))
    val amberPalette = listOf(Color(0xFF130E05), Color(0xFF72531A), Color(0xFFF5C76B))
    val now = System.currentTimeMillis()

    val tracks = listOf(
        DesktopTrack(
            id = "preview-track-1",
            path = "/preview/Crimson Skyline/01 Midnight Echo.mp3",
            releaseDate = "2012",
            title = "Midnight Echo",
            artist = "Nova Rey",
            artistCredits = listOf("Nova Rey"),
            albumArtist = "Nova Rey",
            album = "Crimson Skyline",
            genre = "Synthwave",
            durationMs = 226_000L,
            mood = "Night drive",
            palette = crimsonPalette,
            lyrics = listOf(
                DesktopLyricLine(0L, "City lights bloom in slow motion"),
                DesktopLyricLine(14_000L, "Every window hums a neon prayer"),
                DesktopLyricLine(29_000L, "We keep moving like the dark is open"),
                DesktopLyricLine(43_000L, "Midnight echoes hanging in the air"),
            ),
            lyricsAttribution = "Preview data",
            artworkBytes = artworkBytes,
        ),
        DesktopTrack(
            id = "preview-track-2",
            path = "/preview/Crimson Skyline/02 Glass Hearts.mp3",
            releaseDate = "2012",
            title = "Glass Hearts",
            artist = "Nova Rey",
            artistCredits = listOf("Nova Rey", "Kairo"),
            albumArtist = "Nova Rey",
            album = "Crimson Skyline",
            genre = "Synthwave",
            durationMs = 208_000L,
            mood = "Late night",
            palette = crimsonPalette,
            plainLyrics = listOf(
                "We kept our glass hearts hidden in the bassline.",
                "Every chorus felt like headlights in the rain.",
            ),
            lyricsAttribution = "Preview data",
            artworkBytes = artworkBytes,
        ),
        DesktopTrack(
            id = "preview-track-3",
            path = "/preview/Tidal Bloom/01 Tidal Bloom.mp3",
            releaseDate = "2018",
            title = "Tidal Bloom",
            artist = "Ari Vale",
            artistCredits = listOf("Ari Vale"),
            albumArtist = "Ari Vale",
            album = "Tidal Bloom",
            genre = "Alt-Pop",
            durationMs = 194_000L,
            mood = "Lifted",
            palette = cobaltPalette,
            plainLyrics = listOf(
                "We rise with the tide and the skyline opens wide.",
            ),
            lyricsAttribution = "Preview data",
            artworkBytes = artworkBytes,
        ),
        DesktopTrack(
            id = "preview-track-4",
            path = "/preview/Tidal Bloom/02 Harborline.mp3",
            releaseDate = "2018",
            title = "Harborline",
            artist = "Ari Vale",
            artistCredits = listOf("Ari Vale", "Nova Rey"),
            albumArtist = "Ari Vale",
            album = "Tidal Bloom",
            genre = "Alt-Pop",
            durationMs = 238_000L,
            mood = "Open road",
            palette = cobaltPalette,
            plainLyrics = listOf(
                "Harbor lights and a fast lane heart.",
                "Everything restless finds a start.",
            ),
            lyricsAttribution = "Preview data",
            artworkBytes = artworkBytes,
        ),
        DesktopTrack(
            id = "preview-track-5",
            path = "/preview/Golden Static/01 Gold Static.mp3",
            releaseDate = "2008",
            title = "Gold Static",
            artist = "Kairo",
            artistCredits = listOf("Kairo"),
            albumArtist = "Kairo",
            album = "Golden Static",
            genre = "Electronic",
            durationMs = 214_000L,
            mood = "After hours",
            palette = amberPalette,
            plainLyrics = listOf(
                "Gold static in the rear-view mirror.",
            ),
            lyricsAttribution = "Preview data",
            artworkBytes = artworkBytes,
        ),
    )

    val albumKey = desktopAlbumKey("Nova Rey", "Crimson Skyline")
    val artistName = "Nova Rey"
    val playlistId = "preview-playlist-night-drive"

    return DesktopPreviewData(
        libraryState = DesktopLibraryUiState(
            sourcePaths = listOf("/preview/library"),
            tracks = tracks,
            isScanning = false,
            errorMessage = null,
        ),
        playHistoryEntries = listOf(
            DesktopPlayHistoryEntry(tracks[0].path, tracks[0].title, tracks[0].artist, tracks[0].album, 162_000L, now - 2 * 60 * 60 * 1000L),
            DesktopPlayHistoryEntry(tracks[1].path, tracks[1].title, tracks[1].artist, tracks[1].album, 145_000L, now - 26 * 60 * 60 * 1000L),
            DesktopPlayHistoryEntry(tracks[2].path, tracks[2].title, tracks[2].artist, tracks[2].album, 176_000L, now - 3 * 24 * 60 * 60 * 1000L),
            DesktopPlayHistoryEntry(tracks[4].path, tracks[4].title, tracks[4].artist, tracks[4].album, 204_000L, now - 8 * 24 * 60 * 60 * 1000L),
            DesktopPlayHistoryEntry(tracks[3].path, tracks[3].title, tracks[3].artist, tracks[3].album, 121_000L, now - 12 * 24 * 60 * 60 * 1000L),
        ),
        favoriteTrackPaths = listOf(tracks[0].path, tracks[2].path, tracks[4].path),
        userPlaylists = listOf(
            DesktopUserPlaylist(
                id = playlistId,
                title = "Night Drive",
                description = "A preview playlist for the desktop gallery.",
                trackPaths = listOf(tracks[0].path, tracks[1].path, tracks[4].path),
            ),
        ),
        artistProfileOverrides = mapOf(
            "Nova Rey" to DesktopArtistProfileOverride(
                photoPath = previewArtworkPath,
                about = "Nova Rey is a preview-only synth-pop artist used to stage VerseFlow desktop screens with richer artwork, lyrics, and metadata.",
            ),
            "Ari Vale" to DesktopArtistProfileOverride(
                photoPath = previewArtworkPath,
                about = "Ari Vale represents the lighter alt-pop side of the preview gallery and helps exercise album, artist, and playlist states.",
            ),
        ),
        albumProfileOverrides = mapOf(
            albumKey to DesktopAlbumProfileOverride(
                about = "Crimson Skyline is a preview album built for VerseFlow's desktop gallery. It exists to stage album art, track lists, and imported album metadata in one place.",
                releaseDate = "October 12, 2012",
                genre = "Synthwave",
                sourcePageTitle = "Crimson Skyline",
                totalTrackCount = 9,
                trackTitles = listOf(
                    "Midnight Echo",
                    "Glass Hearts",
                    "Afterglow District",
                    "Static Bloom",
                    "Crimson Skyline",
                    "Lifeline",
                    "Night Pulse",
                    "Windowlight",
                    "Exit Music",
                ),
            ),
        ),
        initialTrackId = tracks.first().id,
        initialAlbumKey = albumKey,
        initialArtistName = artistName,
        initialPlaylistId = playlistId,
    )
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

private suspend fun fetchDesktopArtistProfileFromWikipedia(
    artistName: String,
    representativeTrack: DesktopTrack?,
): DesktopFetchedArtistProfile =
    withContext(Dispatchers.IO) {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build()
        val exactTitleCandidates = buildList {
            add(artistName.trim())
            add("$artistName (singer)")
            add("$artistName (musician)")
            add("$artistName (rapper)")
            add("$artistName (band)")
        }.distinct()
        val exactMatch = exactTitleCandidates.firstNotNullOfOrNull { candidateTitle ->
            runCatching { fetchWikipediaSummaryPayload(client, candidateTitle) }
                .getOrNull()
                ?.takeIf { payload ->
                    isAcceptableArtistSummary(
                        pageTitle = candidateTitle,
                        summaryPayload = payload,
                        artistName = artistName,
                    )
                }
                ?.let { payload -> candidateTitle to payload }
        }
        val resolved = exactMatch ?: run {
            val candidate = searchWikipediaArtistCandidate(
                client = client,
                artistName = artistName,
                representativeTrack = representativeTrack,
            )
            val pageTitle = candidate?.pageTitle ?: error("No Wikipedia result found for $artistName.")
            val payload = fetchWikipediaSummaryPayload(client, pageTitle)
            if (!isAcceptableArtistSummary(pageTitle, payload, artistName)) {
                error("VerseFlow found a related Wikipedia page, but not a reliable artist biography for $artistName.")
            }
            pageTitle to payload
        }
        val pageTitle = resolved.first
        val summaryPayload = resolved.second
        val bio = summaryPayload.optString("extract")
            .substringBefore("\n")
            .trim()
            .takeIf(String::isNotEmpty)
        val imageUrl = summaryPayload.optJSONObject("originalimage")
            ?.optString("source")
            ?.takeIf(String::isNotBlank)
            ?: summaryPayload.optJSONObject("thumbnail")
                ?.optString("source")
                ?.takeIf(String::isNotBlank)

        if (bio == null && imageUrl == null) {
            error("Wikipedia found a page, but it didn't return a usable bio or photo.")
        }
        DesktopFetchedArtistProfile(
            bio = bio,
            imageUrl = imageUrl,
            sourcePageTitle = pageTitle,
        )
    }

private suspend fun fetchDesktopTrackProfileFromWikipedia(
    track: DesktopTrack,
): DesktopFetchedTrackProfile =
    withContext(Dispatchers.IO) {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build()

        val queries = buildList {
            add("${track.title} ${track.artist} song")
            add("${track.title} ${track.artist} ${track.album}")
            track.releaseDate?.takeIf(String::isNotBlank)?.let { add("${track.title} ${track.artist} $it song") }
        }.distinct()

        val candidate = queries
            .flatMap { query -> searchWikipediaPages(client, query, limit = 8) }
            .filter { isLikelyTrackCandidate(it, track) }
            .maxWithOrNull(
                compareByDescending<DesktopArtistLookupCandidate> { scoreWikipediaTrackCandidate(it.pageTitle, it.snippet, track) }
                    .thenBy { it.pageTitle.lowercase() },
            )
            ?: error("VerseFlow couldn't find a reliable Wikipedia page for ${track.title}.")

        fetchDesktopTrackProfileFromWikipediaPage(track, candidate.pageTitle)
    }

private suspend fun fetchDesktopTrackProfileFromWikipediaPage(
    track: DesktopTrack,
    pageTitle: String,
): DesktopFetchedTrackProfile =
    withContext(Dispatchers.IO) {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build()
        val payload = fetchWikipediaSummaryPayload(client, pageTitle)
        val extract = payload.optString("extract").trim()
        val sourceContent = fetchWikipediaPageSource(client, pageTitle)
        val releaseDate = extractReleaseDate(extract)?.toString()
            ?: parseWikipediaTrackReleaseYear(sourceContent)
            ?: track.releaseDate
        val genre = parseWikipediaGenre(sourceContent)
            ?: parseWikipediaGenre(extract)
            ?: track.genre.takeUnless(::isMissingGenreLabel)
        if (extract.isBlank() && genre == null && releaseDate == null) {
            error("Wikipedia found a page, but it didn't return usable song info.")
        }
        DesktopFetchedTrackProfile(
            title = track.title,
            artist = track.artist,
            album = track.album,
            releaseDate = releaseDate,
            genre = genre,
            sourcePageTitle = pageTitle,
        )
    }

private suspend fun fetchDesktopAlbumProfileFromWikipedia(
    album: DesktopAlbumSummary,
    albumTracks: List<DesktopTrack>,
): DesktopFetchedAlbumProfile =
    withContext(Dispatchers.IO) {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build()

        val primaryTrack = albumTracks.firstOrNull()
        val queries = buildList {
            add("${album.title} ${album.artist} album")
            primaryTrack?.title?.takeIf(String::isNotBlank)?.let { add("${album.title} ${album.artist} $it album") }
            album.releaseDate?.takeIf(String::isNotBlank)?.let { add("${album.title} ${album.artist} $it album") }
        }.distinct()

        val candidate = queries
            .flatMap { query -> searchWikipediaCandidates(client, query, album.artist, primaryTrack) }
            .firstOrNull { result ->
                val title = result.pageTitle.lowercase()
                val snippet = result.snippet.lowercase()
                album.title.lowercase() in title || album.title.lowercase() in snippet
            }
            ?: error("VerseFlow couldn't find a reliable Wikipedia page for ${album.title}.")

        fetchDesktopAlbumProfileFromWikipediaPage(
            album = album,
            albumTracks = albumTracks,
            pageTitle = candidate.pageTitle,
        )
    }

private fun isLikelyTrackCandidate(
    candidate: DesktopArtistLookupCandidate,
    track: DesktopTrack,
): Boolean {
    val combined = "${candidate.pageTitle} ${candidate.snippet}".lowercase()
    val title = track.title.lowercase()
    val artist = track.artist.lowercase()
    return combined.contains(title) && (combined.contains(artist) || combined.contains("song"))
}

private fun scoreWikipediaTrackCandidate(
    pageTitle: String,
    snippet: String,
    track: DesktopTrack,
): Int {
    val normalizedTitle = pageTitle.lowercase()
    val normalizedSnippet = snippet.lowercase()
    val songTitle = track.title.lowercase()
    val artistName = track.artist.lowercase()
    val albumName = track.album.lowercase()
    var score = 0
    if (normalizedTitle.contains(songTitle)) score += 45
    if (normalizedSnippet.contains(songTitle)) score += 24
    if (normalizedTitle.contains(artistName)) score += 22
    if (normalizedSnippet.contains(artistName)) score += 18
    if (normalizedSnippet.contains("song")) score += 26
    if (normalizedSnippet.contains("single")) score += 16
    if (normalizedSnippet.contains(albumName)) score += 10
    if (normalizedTitle.contains("album")) score -= 50
    if (normalizedTitle.startsWith("list of")) score -= 90
    if (normalizedTitle.contains("discography")) score -= 90
    return score
}

private suspend fun fetchDesktopAlbumProfileFromWikipediaPage(
    album: DesktopAlbumSummary,
    albumTracks: List<DesktopTrack>,
    pageTitle: String,
): DesktopFetchedAlbumProfile =
    withContext(Dispatchers.IO) {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build()
        val payload = fetchWikipediaSummaryPayload(client, pageTitle)
        val bio = payload.optString("extract")
            .substringBefore("\n")
            .trim()
            .ifBlank { null }
        val releaseDate = Regex("""\b(?:released|release[ds]? on)\s+([A-Z][a-z]+ \d{1,2}, \d{4}|\d{4})""")
            .find(payload.optString("extract"))
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            ?: album.releaseDate
        val trackTitles = fetchWikipediaAlbumTrackTitles(client, pageTitle)
        DesktopFetchedAlbumProfile(
            bio = bio,
            releaseDate = releaseDate,
            genre = album.genre.takeUnless(::isMissingGenreLabel),
            sourcePageTitle = pageTitle,
            totalTrackCount = trackTitles.size.takeIf { it > 0 },
            trackTitles = trackTitles,
        )
    }

private fun fetchWikipediaAlbumTrackTitles(
    client: HttpClient,
    pageTitle: String,
): List<String> {
    val encodedTitle = URLEncoder.encode(pageTitle, Charsets.UTF_8)
    val request = HttpRequest.newBuilder()
        .uri(
            URI(
                "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=$encodedTitle&rvslots=main&rvprop=content&formatversion=2&format=json&redirects=1",
            ),
        )
        .timeout(Duration.ofSeconds(12))
        .header("User-Agent", "VerseFlowDesktop/1.0")
        .GET()
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() !in 200..299) return emptyList()
    val payload = JSONObject(response.body())
    val content = payload
        .optJSONObject("query")
        ?.optJSONArray("pages")
        ?.optJSONObject(0)
        ?.optJSONArray("revisions")
        ?.optJSONObject(0)
        ?.optJSONObject("slots")
        ?.optJSONObject("main")
        ?.optString("content")
        .orEmpty()
    if (content.isBlank()) return emptyList()

    val trackSection = Regex("""(?is)==+\s*Track listing\s*==+(.*?)(?:\n==[^=].*?==|\z)""")
        .find(content)
        ?.groupValues
        ?.getOrNull(1)
        .orEmpty()
    if (trackSection.isBlank()) return emptyList()

    val titlesFromTemplate = Regex("""(?im)^\|\s*title\d+\s*=\s*(.+)$""")
        .findAll(trackSection)
        .mapNotNull { match -> sanitizeWikipediaTrackTitle(match.groupValues.getOrNull(1).orEmpty()) }
        .toList()
    if (titlesFromTemplate.isNotEmpty()) return titlesFromTemplate.distinct()

    return Regex("""(?im)^#\s*(.+)$""")
        .findAll(trackSection)
        .mapNotNull { match -> sanitizeWikipediaTrackTitle(match.groupValues.getOrNull(1).orEmpty()) }
        .distinct()
        .toList()
}

private fun sanitizeWikipediaTrackTitle(raw: String): String? {
    val withoutTemplates = raw.replace(Regex("""\{\{.*?}}"""), "")
    val withoutLinks = withoutTemplates
        .replace(Regex("""\[\[([^|\]]+\|)?([^\]]+)]]"""), "$2")
        .replace(Regex("""''+"""), "")
        .replace(Regex("""<.*?>"""), "")
        .replace(Regex("""\s*\(feat\..*?$""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\s*\|.*$"""), "")
        .trim()
        .trim('"')
    return withoutLinks.takeIf(String::isNotBlank)
}

private fun normalizeAlbumTrackTitleForMatch(title: String): String =
    title
        .lowercase()
        .replace(Regex("""(?i)\bfeat(?:uring)?\.?.*$"""), "")
        .replace(Regex("""[^a-z0-9]+"""), "")
        .trim()

private fun searchWikipediaArtistCandidate(
    client: HttpClient,
    artistName: String,
    representativeTrack: DesktopTrack?,
): DesktopArtistLookupCandidate? {
    val queries = buildList {
        val collaborators = representativeTrack
            ?.artistCredits
            ?.filterNot { it.equals(artistName, ignoreCase = true) }
            ?.take(2)
            .orEmpty()
        add(
            listOf(
                artistName.trim(),
                representativeTrack?.title?.trim().orEmpty(),
                collaborators.joinToString(" "),
                "singer",
            ).filter(String::isNotBlank).joinToString(" "),
        )
        add(
            listOf(
                artistName.trim(),
                representativeTrack?.album?.trim().orEmpty(),
                "musician",
            ).filter(String::isNotBlank).joinToString(" "),
        )
        add(
            listOf(
                artistName.trim(),
                "musician",
            ).filter(String::isNotBlank).joinToString(" "),
        )
    }.distinct()

    return queries
        .flatMap { query -> searchWikipediaCandidates(client, query, artistName, representativeTrack) }
        .filter { candidate -> candidate.score > MIN_WIKIPEDIA_ARTIST_SCORE }
        .groupBy { it.pageTitle }
        .map { (_, candidates) ->
            candidates.maxByOrNull(DesktopArtistLookupCandidate::score)!!
        }
        .maxWithOrNull(
            compareByDescending<DesktopArtistLookupCandidate> { it.score }
                .thenBy { it.pageTitle.lowercase() },
        )
}

private fun searchWikipediaCandidates(
    client: HttpClient,
    query: String,
    artistName: String,
    representativeTrack: DesktopTrack?,
): List<DesktopArtistLookupCandidate> {
    val searchUri = URI(
        "https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&srlimit=5&srsearch=" +
            URLEncoder.encode(query, Charsets.UTF_8),
    )
    val searchRequest = HttpRequest.newBuilder(searchUri)
        .timeout(Duration.ofSeconds(12))
        .header("User-Agent", "VerseFlowDesktop/1.0")
        .GET()
        .build()
    val searchResponse = client.send(searchRequest, HttpResponse.BodyHandlers.ofString())
    if (searchResponse.statusCode() !in 200..299) {
        error("Wikipedia search failed with HTTP ${searchResponse.statusCode()}.")
    }

    val searchPayload = JSONObject(searchResponse.body())
    val results = searchPayload.optJSONObject("query")?.optJSONArray("search") ?: return emptyList()
    return (0 until results.length())
        .mapNotNull { index ->
            val item = results.optJSONObject(index) ?: return@mapNotNull null
            val pageTitle = item.optString("title").trim().takeIf(String::isNotEmpty) ?: return@mapNotNull null
            val snippet = item.optString("snippet").replace(Regex("<[^>]+>"), " ").trim()
            DesktopArtistLookupCandidate(
                pageTitle = pageTitle,
                snippet = snippet,
                score = scoreWikipediaArtistCandidate(pageTitle, snippet, artistName, representativeTrack),
            )
        }
}

private fun scoreWikipediaArtistCandidate(
    pageTitle: String,
    snippet: String,
    artistName: String,
    representativeTrack: DesktopTrack?,
): Int {
    val normalizedTitle = pageTitle.lowercase()
    val normalizedSnippet = snippet.lowercase()
    val normalizedArtistName = artistName.lowercase()
    val normalizedSongTitle = representativeTrack?.title?.lowercase().orEmpty()
    val normalizedAlbum = representativeTrack?.album?.lowercase().orEmpty()
    val collaborators = representativeTrack
        ?.artistCredits
        ?.filterNot { it.equals(artistName, ignoreCase = true) }
        ?.map(String::lowercase)
        .orEmpty()

    var score = 0
    if (normalizedTitle == normalizedArtistName) score += 60
    if (normalizedTitle == "$normalizedArtistName (singer)") score += 50
    if (normalizedTitle == "$normalizedArtistName (musician)") score += 50
    if (normalizedTitle == "$normalizedArtistName (rapper)") score += 50
    if (normalizedTitle == "$normalizedArtistName (band)") score += 40
    if (normalizedTitle.contains(normalizedArtistName)) score += 25
    if (normalizedSnippet.contains(normalizedArtistName)) score += 18
    if (normalizedSnippet.contains("singer")) score += 20
    if (normalizedSnippet.contains("musician")) score += 20
    if (normalizedSnippet.contains("rapper")) score += 18
    if (normalizedSnippet.contains("dj")) score += 16
    if (normalizedSnippet.contains("record producer")) score += 14
    if (normalizedSnippet.contains("songwriter")) score += 14
    if (normalizedSongTitle.isNotBlank() && normalizedSnippet.contains(normalizedSongTitle)) score += 26
    if (normalizedAlbum.isNotBlank() && normalizedSnippet.contains(normalizedAlbum)) score += 10
    if (collaborators.any { collaborator -> collaborator.isNotBlank() && normalizedSnippet.contains(collaborator) }) {
        score += 14
    }
    if (normalizedTitle.startsWith("list of")) score -= 120
    if (normalizedTitle.contains("discography")) score -= 120
    if (normalizedTitle.contains("album")) score -= 90
    if (normalizedTitle.contains("song")) score -= 80
    if (normalizedTitle.contains("soundtrack")) score -= 70
    if (normalizedTitle.contains("city")) score -= 45
    if (normalizedSnippet.contains("city")) score -= 35
    if (normalizedSnippet.contains("district")) score -= 25
    if (normalizedSnippet.contains("place")) score -= 20
    if (normalizedSnippet.contains("company")) score -= 20
    if (normalizedSnippet.contains("film")) score -= 15
    return score
}

private fun isAcceptableArtistSummary(
    pageTitle: String,
    summaryPayload: JSONObject,
    artistName: String,
): Boolean {
    val normalizedTitle = pageTitle.lowercase()
    val normalizedArtistName = artistName.lowercase()
    val description = summaryPayload.optString("description").lowercase()
    val extract = summaryPayload.optString("extract").lowercase()
    val combined = "$description $extract"

    if (!normalizedTitle.contains(normalizedArtistName)) return false
    if (
        normalizedTitle.startsWith("list of") ||
        normalizedTitle.contains("discography") ||
        normalizedTitle.contains("album") ||
        normalizedTitle.contains("song") ||
        normalizedTitle.contains("soundtrack")
    ) {
        return false
    }

    val hasPersonSignals = listOf(
        "singer",
        "musician",
        "rapper",
        "songwriter",
        "dj",
        "record producer",
        "band",
    ).any(combined::contains)
    if (!hasPersonSignals) return false

    val hasBadSignals = listOf(
        "list of",
        "album by",
        "studio album",
        "song by",
        "city",
        "district",
        "company",
    ).any(combined::contains)
    return !hasBadSignals
}

private fun fetchWikipediaSummaryPayload(
    client: HttpClient,
    pageTitle: String,
): JSONObject {
    val restPathTitle = pageTitle
        .replace(" ", "_")
        .split('/')
        .joinToString("/") { segment ->
            URLEncoder.encode(segment, Charsets.UTF_8).replace("+", "%20")
        }
    val summaryUri = URI("https://en.wikipedia.org/api/rest_v1/page/summary/$restPathTitle")
    val summaryRequest = HttpRequest.newBuilder(summaryUri)
        .timeout(Duration.ofSeconds(12))
        .header("User-Agent", "VerseFlowDesktop/1.0")
        .GET()
        .build()
    val summaryResponse = client.send(summaryRequest, HttpResponse.BodyHandlers.ofString())
    if (summaryResponse.statusCode() in 200..299) {
        return JSONObject(summaryResponse.body())
    }

    val fallbackUri = URI(
        "https://en.wikipedia.org/w/api.php?action=query&prop=extracts|pageimages&format=json&exintro=1&explaintext=1&piprop=original&titles=" +
            URLEncoder.encode(pageTitle, Charsets.UTF_8),
    )
    val fallbackRequest = HttpRequest.newBuilder(fallbackUri)
        .timeout(Duration.ofSeconds(12))
        .header("User-Agent", "VerseFlowDesktop/1.0")
        .GET()
        .build()
    val fallbackResponse = client.send(fallbackRequest, HttpResponse.BodyHandlers.ofString())
    if (fallbackResponse.statusCode() !in 200..299) {
        error("Wikipedia summary failed with HTTP ${summaryResponse.statusCode()}.")
    }

    val payload = JSONObject(fallbackResponse.body())
        .optJSONObject("query")
        ?.optJSONObject("pages")
        ?: error("Wikipedia summary failed with HTTP ${summaryResponse.statusCode()}.")
    val page = payload.keys().asSequence()
        .mapNotNull(payload::optJSONObject)
        .firstOrNull()
        ?: error("Wikipedia summary failed with HTTP ${summaryResponse.statusCode()}.")

    val extract = page.optString("extract").trim()
    val originalImage = page.optJSONObject("original")?.optString("source")

    return JSONObject().apply {
        put("extract", extract)
        if (!originalImage.isNullOrBlank()) {
            put(
                "originalimage",
                JSONObject().put("source", originalImage),
            )
        }
    }
}

private fun fetchWikipediaPageSource(
    client: HttpClient,
    pageTitle: String,
): String? {
    val encodedTitle = URLEncoder.encode(pageTitle, Charsets.UTF_8)
    val request = HttpRequest.newBuilder()
        .uri(
            URI(
                "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=$encodedTitle&rvslots=main&rvprop=content&formatversion=2&format=json&redirects=1",
            ),
        )
        .timeout(Duration.ofSeconds(12))
        .header("User-Agent", "VerseFlowDesktop/1.0")
        .GET()
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() !in 200..299) return null
    val pages = JSONObject(response.body())
        .optJSONObject("query")
        ?.optJSONArray("pages")
        ?: return null
    return (0 until pages.length())
        .asSequence()
        .mapNotNull { index -> pages.optJSONObject(index) }
        .mapNotNull { page ->
            page.optJSONObject("revisions")
                ?.optJSONObject("main")
                ?.optString("content")
                ?.takeIf(String::isNotBlank)
                ?: page.optJSONArray("revisions")
                    ?.optJSONObject(0)
                    ?.optJSONObject("slots")
                    ?.optJSONObject("main")
                    ?.optString("content")
                    ?.takeIf(String::isNotBlank)
        }
        .firstOrNull()
}

private fun parseWikipediaGenre(source: String?): String? {
    val raw = source.orEmpty()
    val inlineGenre = Regex("""(?im)^\|\s*genre\s*=\s*(.+)$""")
        .find(raw)
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
    val proseGenre = Regex("""(?is)\bgenres?\b[^.:\n]{0,20}[:=]\s*([^.\n]{2,180})""")
        .find(raw)
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
    return sequenceOf(inlineGenre, proseGenre)
        .filterNotNull()
        .flatMap { candidate -> extractGenreCandidates(candidate).asSequence() }
        .map(::normalizeGenreCandidate)
        .filterNotNull()
        .firstOrNull()
}

private fun extractGenreCandidates(raw: String): List<String> {
    val normalized = raw
        .replace(Regex("""(?is)<ref[^>]*>.*?</ref>"""), " ")
        .replace(Regex("""(?i)<br\s*/?>"""), "|")
        .replace(Regex("""(?i)\{\{(?:hlist|plainlist|flatlist)\s*\|"""), "")
        .replace(Regex("""(?i)\{\{ubl\s*\|"""), "")
        .replace(Regex("""(?i)\{\{unbulleted list\s*\|"""), "")
        .replace(Regex("""\{\{small\|"""), "")
        .replace(Regex("""\{\{nowrap\|"""), "")
        .replace(Regex("""\{\{plainlist\}\}|\{\{flatlist\}\}|\{\{hlist\}\}"""), " ")
        .replace("[[", "")
        .replace("]]", "")
        .replace("{{", "")
        .replace("}}", "")
        .replace("&nbsp;", " ")
        .replace(Regex("""\s+"""), " ")
        .trim()

    return normalized
        .split('|', ',', ';', '\n', '*', '•')
        .map { part -> part.substringAfterLast("=").trim() }
        .map { part -> part.substringAfterLast("/").trim().ifBlank { part.trim() } }
        .filter(String::isNotBlank)
}

private fun normalizeGenreCandidate(raw: String): String? {
    val cleaned = raw
        .replace(Regex("""\([^)]*\)"""), " ")
        .replace(Regex("""(?i)\b(?:genre|music|stylistically|influences?)\b"""), " ")
        .replace(Regex("""<[^>]+>"""), " ")
        .replace(Regex("""[^A-Za-z0-9/&,\- ]"""), " ")
        .replace(Regex("""\s+"""), " ")
        .trim()
        .substringAfterLast("|")
        .trim()

    if (cleaned.isBlank()) return null
    val firstListValue = cleaned
        .split(',', '/', ';')
        .map(String::trim)
        .firstOrNull { it.isNotBlank() }
        ?: return null

    return firstListValue
        .takeUnless(::isMissingGenreLabel)
        ?.takeUnless {
            it.length < 3 ||
                it.equals("citation needed", ignoreCase = true) ||
                it.equals("see below", ignoreCase = true)
        }
}

private fun parseWikipediaTrackReleaseYear(source: String?): String? =
    extractReleaseYear(source)?.toString()

private fun normalizeLastFmTagToGenre(tag: String): String? {
    val normalized = tag.trim().lowercase()
    if (normalized.isBlank()) return null
    return when {
        normalized in setOf("hip hop", "hip-hop", "uk hip hop", "uk hip-hop", "rap", "grime", "drill", "trap") -> "Hip-Hop"
        normalized in setOf("rnb", "r&b", "rhythm and blues", "soul", "neo soul", "neosoul") -> "R&B"
        normalized in setOf("afrobeats", "afrobeat", "afropop", "afroswing") -> "Afrobeats"
        normalized in setOf("dancehall", "reggae") -> "Reggae"
        normalized in setOf("pop", "synthpop", "electropop", "dance pop") -> "Pop"
        normalized in setOf("rock", "alternative rock", "indie rock", "hard rock") -> "Rock"
        normalized in setOf("house", "deep house", "edm", "electronic", "dance", "club") -> "Electronic"
        normalized in setOf("jazz") -> "Jazz"
        normalized in setOf("classical") -> "Classical"
        normalized in setOf("gospel") -> "Gospel"
        normalized in setOf("folk", "indie folk") -> "Folk"
        normalized in setOf("country") -> "Country"
        else -> tag.trim()
            .split(' ', '-', '/')
            .joinToString(" ") { part -> part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
            .takeUnless(::isMissingGenreLabel)
    }
}

private fun fetchDesktopTrackGenreFromLastFm(
    track: DesktopTrack,
    apiKey: String,
): String? {
    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    fun tagsFor(method: String, params: Map<String, String>): List<String> {
        val query = buildString {
            append("method=")
            append(URLEncoder.encode(method, Charsets.UTF_8))
            params.forEach { (key, value) ->
                append('&')
                append(URLEncoder.encode(key, Charsets.UTF_8))
                append('=')
                append(URLEncoder.encode(value, Charsets.UTF_8))
            }
            append("&api_key=")
            append(URLEncoder.encode(apiKey, Charsets.UTF_8))
            append("&format=json")
        }
        val request = HttpRequest.newBuilder()
            .uri(URI("https://ws.audioscrobbler.com/2.0/?$query"))
            .timeout(Duration.ofSeconds(12))
            .header("User-Agent", "VerseFlowDesktop/1.0")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) return emptyList()
        val payload = JSONObject(response.body())
        val container = payload.optJSONObject("toptags") ?: return emptyList()
        val rawTags = container.opt("tag")
        val tagsArray = when (rawTags) {
            is JSONArray -> rawTags
            is JSONObject -> JSONArray().put(rawTags)
            else -> null
        } ?: return emptyList()
        return (0 until tagsArray.length())
            .mapNotNull { index -> tagsArray.optJSONObject(index) }
            .sortedByDescending { it.optInt("count", 0) }
            .mapNotNull { tagObject -> tagObject.optString("name").takeIf(String::isNotBlank) }
    }

    val candidateTags = buildList {
        addAll(tagsFor("track.getTopTags", mapOf("artist" to track.artist, "track" to track.title)))
        if (isEmpty()) addAll(tagsFor("album.getTopTags", mapOf("artist" to track.albumArtist, "album" to track.album)))
        if (isEmpty()) addAll(tagsFor("artist.getTopTags", mapOf("artist" to (track.artistCredits.firstOrNull() ?: track.artist))))
    }

    return candidateTags
        .mapNotNull(::normalizeLastFmTagToGenre)
        .firstOrNull()
}

private fun searchWikipediaPages(
    client: HttpClient,
    query: String,
    limit: Int = 5,
): List<DesktopArtistLookupCandidate> {
    val searchUri = URI(
        "https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&srlimit=$limit&srsearch=" +
            URLEncoder.encode(query, Charsets.UTF_8),
    )
    val searchRequest = HttpRequest.newBuilder(searchUri)
        .timeout(Duration.ofSeconds(12))
        .header("User-Agent", "VerseFlowDesktop/1.0")
        .GET()
        .build()
    val searchResponse = client.send(searchRequest, HttpResponse.BodyHandlers.ofString())
    if (searchResponse.statusCode() !in 200..299) {
        error("Wikipedia search failed with HTTP ${searchResponse.statusCode()}.")
    }

    val searchPayload = JSONObject(searchResponse.body())
    val results = searchPayload.optJSONObject("query")?.optJSONArray("search") ?: return emptyList()
    return (0 until results.length())
        .mapNotNull { index ->
            val item = results.optJSONObject(index) ?: return@mapNotNull null
            val pageTitle = item.optString("title").trim().takeIf(String::isNotEmpty) ?: return@mapNotNull null
            val snippet = item.optString("snippet").replace(Regex("<[^>]+>"), " ").trim()
            DesktopArtistLookupCandidate(pageTitle = pageTitle, snippet = snippet, score = 0)
        }
}

private suspend fun saveDesktopArtistReferenceImage(
    artistName: String,
    imageUrl: String,
): String? = withContext(Dispatchers.IO) {
    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    val request = HttpRequest.newBuilder(URI(imageUrl))
        .timeout(Duration.ofSeconds(15))
        .header("User-Agent", "VerseFlowDesktop/1.0")
        .GET()
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
    if (response.statusCode() !in 200..299) return@withContext null
    val bytes = response.body()
    if (bytes.isEmpty()) return@withContext null

    val appDirectory = Path.of(System.getProperty("user.home"), ".verseflow", "artist-images")
    Files.createDirectories(appDirectory)
    val fileExtension = imageUrl
        .substringAfterLast('.', "")
        .substringBefore('?')
        .lowercase()
        .takeIf { it in setOf("png", "jpg", "jpeg", "webp") }
        ?: "jpg"
    val fileName = artistName
        .lowercase()
        .replace(Regex("""[^a-z0-9]+"""), "-")
        .trim('-')
        .ifBlank { "artist" } + ".$fileExtension"
    val outputPath = appDirectory.resolve(fileName)
    Files.write(outputPath, bytes)
    outputPath.toString()
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
