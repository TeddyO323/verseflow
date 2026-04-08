package com.example.verseflow.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.verseflow.data.BackdropPaletteResult
import com.example.verseflow.data.LocalArtworkResolver
import com.example.verseflow.model.AccentPalette
import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.MusicCatalogSource
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.RepeatMode as PlaybackRepeatMode
import com.example.verseflow.model.Song
import com.example.verseflow.model.SongSource
import com.example.verseflow.ui.navigation.TopLevelDestination
import com.example.verseflow.ui.navigation.topLevelDestinations
import kotlin.math.roundToInt

@Composable
fun AuroraBackdrop(
    palette: AccentPalette,
    modifier: Modifier = Modifier,
    backgroundAlpha: Float = 1f,
) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12_000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "auroraDrift",
    )
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.background.copy(alpha = backgroundAlpha),
                        palette.background.copy(alpha = 0.98f * backgroundAlpha),
                        Color.Black.copy(alpha = backgroundAlpha),
                    ),
                ),
            ),
    ) {
        GlowBlob(
            colorA = palette.primary.copy(alpha = 0.80f),
            colorB = palette.secondary.copy(alpha = 0.15f),
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 4.dp)
                .blur(120.dp),
            rotation = -10f,
            scale = drift,
        )
        GlowBlob(
            colorA = palette.tertiary.copy(alpha = 0.74f),
            colorB = palette.primary.copy(alpha = 0.12f),
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
                .blur(140.dp),
            rotation = 38f,
            scale = 1.08f,
        )
        GlowBlob(
            colorA = palette.glow.copy(alpha = 0.42f),
            colorB = Color.Transparent,
            modifier = Modifier
                .size(420.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .blur(150.dp),
            rotation = 0f,
            scale = 1.0f,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.16f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.40f),
                        ),
                    ),
                ),
        )
    }
}

@Composable
fun ArtworkReactiveBackdrop(
    palette: AccentPalette,
    modifier: Modifier = Modifier,
    artworkUri: String? = null,
    fallbackMediaUri: String? = null,
) {
    val context = LocalContext.current
    val artwork by produceState<androidx.compose.ui.graphics.ImageBitmap?>(
        initialValue = null,
        artworkUri,
        fallbackMediaUri,
    ) {
        value = LocalArtworkResolver.loadArtwork(
            context = context,
            artworkUri = artworkUri,
            fallbackMediaUri = fallbackMediaUri,
        )
    }
    val backdrop by produceState<BackdropPaletteResult?>(
        initialValue = null,
        artworkUri,
        fallbackMediaUri,
    ) {
        value = LocalArtworkResolver.loadArtworkBackdropPalette(
            context = context,
            artworkUri = artworkUri,
            fallbackMediaUri = fallbackMediaUri,
        )
    }
    val reactivePalette = backdrop?.palette ?: palette
    val isMonochromeArtwork = backdrop?.isMonochrome == true

    Box(modifier = modifier.background(Color.Black)) {
        if (artwork != null) {
            Image(
                bitmap = artwork!!,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = 1.35f,
                        scaleY = 1.35f,
                        alpha = if (isMonochromeArtwork) 0.96f else 0.92f,
                    )
                    .blur(120.dp),
            )
            Image(
                bitmap = artwork!!,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = if (isMonochromeArtwork) 0.04f else 0.10f),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.16f),
                                reactivePalette.background.copy(alpha = if (isMonochromeArtwork) 0.08f else 0.18f),
                                Color.Black.copy(alpha = 0.52f),
                            ),
                        ),
                    ),
            )
            AuroraBackdrop(
                palette = reactivePalette,
                backgroundAlpha = if (isMonochromeArtwork) 0.08f else 0.18f,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AuroraBackdrop(
                palette = palette,
                backgroundAlpha = 1f,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun BoxScope.GlowBlob(
    colorA: Color,
    colorB: Color,
    modifier: Modifier,
    rotation: Float,
    scale: Float,
) {
    Box(
        modifier = modifier
            .size((260 * scale).dp)
            .align(Alignment.Center)
            .rotate(rotation)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(colorA, colorB),
                ),
            )
            .shadow(20.dp, CircleShape),
    )
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
    surfaceAlpha: Float = 0.74f,
    surfaceVariantAlpha: Float = 0.48f,
    borderAlpha: Float = 0.12f,
    shadowElevation: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(shadowElevation, shape, clip = false)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = surfaceAlpha),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = surfaceVariantAlpha),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = borderAlpha),
                shape = shape,
            ),
        content = content,
    )
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable(onClick = onActionClick),
            )
        }
    }
}

@Composable
fun AlbumArtwork(
    title: String,
    subtitle: String,
    palette: AccentPalette,
    modifier: Modifier = Modifier,
    artworkUri: String? = null,
    fallbackMediaUri: String? = null,
    shape: Shape = RoundedCornerShape(32.dp),
    borderColor: Color = Color.White.copy(alpha = 0.12f),
    showOverlay: Boolean = true,
) {
    val context = LocalContext.current
    val rotation by rememberInfiniteTransition(label = "artRotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28_000),
            repeatMode = RepeatMode.Restart,
        ),
        label = "artRotationValue",
    )
    val artwork by produceState<androidx.compose.ui.graphics.ImageBitmap?>(
        initialValue = null,
        artworkUri,
        fallbackMediaUri,
    ) {
        value = LocalArtworkResolver.loadArtwork(
            context = context,
            artworkUri = artworkUri,
            fallbackMediaUri = fallbackMediaUri,
        )
    }
    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, borderColor, shape),
    ) {
        Crossfade(targetState = artwork, label = "artworkCrossfade") { image ->
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = "$title album art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.14f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.68f),
                                ),
                            ),
                        ),
                )
            } else {
                AlbumArtworkFallback(
                    palette = palette,
                )
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = if (artwork != null) 0.14f else 0.24f), Color.Transparent),
                ),
                radius = size.minDimension * 0.45f,
                center = center.copy(x = size.width * 0.30f, y = size.height * 0.28f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(palette.glow.copy(alpha = if (artwork != null) 0.22f else 0.36f), Color.Transparent),
                ),
                radius = size.minDimension * 0.50f,
                center = center.copy(x = size.width * 0.70f, y = size.height * 0.70f),
            )
        }
        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.Center)
                        .rotate(rotation)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.35f),
                                ),
                            ),
                            shape = CircleShape,
                        ),
                )
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.76f),
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = title.take(2).uppercase(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 78.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.White.copy(alpha = if (artwork != null) 0.09f else 0.14f),
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }
    }
}

@Composable
private fun AlbumArtworkFallback(
    palette: AccentPalette,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        palette.primary,
                        palette.secondary,
                        palette.tertiary,
                    ),
                ),
            ),
    )
}

@Composable
fun AlbumCard(
    album: Album,
    artistName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
    fixedWidth: Dp? = 250.dp,
    surfaceAlpha: Float = 0.74f,
    surfaceVariantAlpha: Float = 0.48f,
    topArtworkBleed: Boolean = false,
    artworkHeight: Dp = 220.dp,
) {
    GlassPanel(
        modifier = modifier
            .then(if (fixedWidth != null) Modifier.width(fixedWidth) else Modifier)
            .clickable(onClick = onClick),
        shape = shape,
        surfaceAlpha = surfaceAlpha,
        surfaceVariantAlpha = surfaceVariantAlpha,
    ) {
        if (topArtworkBleed) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                AlbumArtwork(
                    title = album.title,
                    subtitle = artistName,
                    palette = album.palette,
                    artworkUri = album.artworkUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(artworkHeight),
                    shape = RectangleShape,
                    borderColor = Color.Transparent,
                    showOverlay = false,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${album.trackIds.size} songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AlbumArtwork(
                    title = album.title,
                    subtitle = artistName,
                    palette = album.palette,
                    artworkUri = album.artworkUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(artworkHeight),
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "$artistName • ${album.year}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
    fixedWidth: Dp? = 250.dp,
    surfaceAlpha: Float = 0.74f,
    surfaceVariantAlpha: Float = 0.48f,
    topArtworkBleed: Boolean = false,
    artworkHeight: Dp = 180.dp,
) {
    GlassPanel(
        modifier = modifier
            .then(if (fixedWidth != null) Modifier.width(fixedWidth) else Modifier)
            .clickable(onClick = onClick),
        shape = shape,
        surfaceAlpha = surfaceAlpha,
        surfaceVariantAlpha = surfaceVariantAlpha,
    ) {
        if (topArtworkBleed) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                AlbumArtwork(
                    title = playlist.title,
                    subtitle = playlist.curator,
                    palette = playlist.palette,
                    artworkUri = playlist.artworkUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(artworkHeight),
                    shape = RectangleShape,
                    borderColor = Color.Transparent,
                    showOverlay = false,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = playlist.curator,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = playlist.followers,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AlbumArtwork(
                    title = playlist.title,
                    subtitle = playlist.curator,
                    palette = playlist.palette,
                    artworkUri = playlist.artworkUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(artworkHeight),
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = playlist.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${playlist.followers} • ${playlist.curator}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistCard(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
    fixedWidth: Dp? = 190.dp,
    surfaceAlpha: Float = 0.74f,
    surfaceVariantAlpha: Float = 0.48f,
) {
    GlassPanel(
        modifier = modifier
            .then(if (fixedWidth != null) Modifier.width(fixedWidth) else Modifier)
            .clickable(onClick = onClick),
        shape = shape,
        surfaceAlpha = surfaceAlpha,
        surfaceVariantAlpha = surfaceVariantAlpha,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                artist.heroPalette.primary,
                                artist.heroPalette.secondary,
                                artist.heroPalette.tertiary,
                            ),
                        ),
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = artist.name.take(2).uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White.copy(alpha = 0.92f),
                )
            }
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = artist.genre,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    artistName: String,
    supportingText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    surfaceAlpha: Float = 0.74f,
    surfaceVariantAlpha: Float = 0.48f,
    borderAlpha: Float = 0.12f,
    shadowElevation: Dp = 18.dp,
    itemPadding: Dp = 14.dp,
    artworkSize: Dp = 72.dp,
    itemSpacing: Dp = 14.dp,
    artworkShape: Shape = RoundedCornerShape(24.dp),
    showArtworkOverlay: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    GlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = shape,
        surfaceAlpha = surfaceAlpha,
        surfaceVariantAlpha = surfaceVariantAlpha,
        borderAlpha = borderAlpha,
        shadowElevation = shadowElevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(itemPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            AlbumArtwork(
                title = song.title,
                subtitle = artistName,
                palette = song.palette,
                artworkUri = song.artworkUri,
                fallbackMediaUri = song.mediaUri,
                modifier = Modifier.size(artworkSize),
                shape = artworkShape,
                showOverlay = showArtworkOverlay,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$artistName • $supportingText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            trailingContent?.invoke()
        }
    }
}

@Composable
fun LiveBadge(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "liveBadge")
    val pulseScale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "livePulseScale",
    )
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "livePulseAlpha",
    )
    val dotAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "liveDotAlpha",
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f),
                shape = RoundedCornerShape(999.dp),
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .alpha(pulseAlpha)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .alpha(dotAlpha)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape,
                    ),
            )
        }
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
        Spacer(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp),
        )
    }
}

@Composable
fun SongOverflowMenu(
    song: Song,
    artistName: String,
    albumTitle: String,
    playlists: List<Playlist>,
    isFavorite: Boolean,
    onRemoveFromVerseFlow: (songId: String) -> Unit,
    onAddToPlaylist: (playlistId: String, songId: String) -> Unit,
    onAddToPlayQueue: (songId: String) -> Unit,
    onToggleFavorite: (songId: String) -> Unit,
    onOpenArtist: () -> Unit,
    onOpenAlbum: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteFromStorage: ((String) -> Unit)? = null,
    onEditMusicInfo: ((String) -> Unit)? = null,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showPlaylistPicker by remember { mutableStateOf(false) }
    var pendingInfoDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    val sortedPlaylists = remember(playlists) { playlists.sortedBy { it.title.lowercase() } }

    if (showPlaylistPicker) {
        AlertDialog(
            onDismissRequest = { showPlaylistPicker = false },
            title = { Text("Add to playlist") },
            text = {
                if (sortedPlaylists.isEmpty()) {
                    Text(
                        text = "No playlists are available yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(sortedPlaylists, key = { it.id }) { playlist ->
                            TextButton(
                                onClick = {
                                    onAddToPlaylist(playlist.id, song.id)
                                    showPlaylistPicker = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = playlist.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = playlist.curator,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPlaylistPicker = false }) {
                    Text("Close")
                }
            },
        )
    }

    pendingInfoDialog?.let { (title, body) ->
        AlertDialog(
            onDismissRequest = { pendingInfoDialog = null },
            title = { Text(title) },
            text = {
                Text(
                    text = body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(onClick = { pendingInfoDialog = null }) {
                    Text("Close")
                }
            },
        )
    }

    Box(modifier = modifier) {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "Song options",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = if (albumTitle.isBlank()) artistName else "$artistName • $albumTitle",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                onClick = {},
                enabled = false,
            )
            DropdownMenuItem(
                text = { Text("Add to playlist", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                onClick = {
                    menuExpanded = false
                    showPlaylistPicker = true
                },
            )
            DropdownMenuItem(
                text = { Text("Add to play queue", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                onClick = {
                    menuExpanded = false
                    onAddToPlayQueue(song.id)
                },
            )
            DropdownMenuItem(
                text = {
                    Text(
                        if (isFavorite) "Remove from favourites" else "Add to favourites",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
                    )
                },
                onClick = {
                    menuExpanded = false
                    onToggleFavorite(song.id)
                },
            )
            DropdownMenuItem(
                text = { Text("Remove from VerseFlow", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                onClick = {
                    menuExpanded = false
                    onRemoveFromVerseFlow(song.id)
                },
            )
            DropdownMenuItem(
                text = { Text("Delete from storage", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                enabled = song.source == SongSource.Local && !song.mediaUri.isNullOrBlank(),
                onClick = {
                    menuExpanded = false
                    if (onDeleteFromStorage != null) {
                        onDeleteFromStorage(song.id)
                    } else {
                        pendingInfoDialog = "Delete from storage" to
                            "Storage deletion is not wired yet. I left the action visible, but kept it safe so the app does not accidentally delete local music."
                    }
                },
            )
            DropdownMenuItem(
                text = { Text("Artist", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                onClick = {
                    menuExpanded = false
                    onOpenArtist()
                },
            )
            DropdownMenuItem(
                text = { Text("Album", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                onClick = {
                    menuExpanded = false
                    onOpenAlbum()
                },
            )
            DropdownMenuItem(
                text = { Text("Edit Music Info", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)) },
                onClick = {
                    menuExpanded = false
                    if (onEditMusicInfo != null) {
                        onEditMusicInfo(song.id)
                    } else {
                        pendingInfoDialog = "Edit Music Info" to
                            "Tag editing is not wired yet. The menu item is in place so we can connect a real metadata editor in the next pass."
                    }
                },
            )
        }
    }
}

@Composable
fun GlowIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.56f),
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(containerColor)
            .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = contentDescription, tint = tint)
        }
    }
}

@Composable
fun VerseFlowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(24.dp)),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.74f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.54f),
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.40f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Composable
fun VerseFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.SansSerif),
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
            selectedLabelColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color.White.copy(alpha = if (selected) 0.22f else 0.08f),
            selectedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.42f),
        ),
    )
}

@Composable
fun MiniPlayerBar(
    song: Song,
    artistName: String,
    progress: Float,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit,
) {
    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AlbumArtwork(
                    title = song.title,
                    subtitle = artistName,
                    palette = song.palette,
                    artworkUri = song.artworkUri,
                    fallbackMediaUri = song.mediaUri,
                    modifier = Modifier.size(64.dp),
                    shape = RectangleShape,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                GlowIconButton(
                    icon = Icons.Rounded.SkipPrevious,
                    contentDescription = "Previous",
                    onClick = onPrevious,
                    modifier = Modifier.size(44.dp),
                )
                GlowIconButton(
                    icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play pause",
                    onClick = onPlayPause,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                )
                GlowIconButton(
                    icon = Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    onClick = onNext,
                    modifier = Modifier.size(44.dp),
                )
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RectangleShape),
                color = Color(0xFF0000FF),
                trackColor = Color.White.copy(alpha = 0.10f),
            )
        }
    }
}

@Composable
fun PlaybackProgress(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val progressBlue = Color(0xFF0000FF)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Slider(
            value = positionMs.toFloat().coerceAtLeast(0f),
            onValueChange = { onSeek(it.roundToInt().toLong()) },
            valueRange = 0f..durationMs.coerceAtLeast(1L).toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(scaleY = 0.72f),
            colors = SliderDefaults.colors(
                thumbColor = progressBlue,
                activeTrackColor = progressBlue,
                inactiveTrackColor = Color.White.copy(alpha = 0.14f),
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatDuration(positionMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatDuration(durationMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatMode: PlaybackRepeatMode,
    isLiked: Boolean,
    onShuffle: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
    onLike: () -> Unit,
    onQueue: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlowIconButton(
                icon = Icons.Rounded.Shuffle,
                contentDescription = "Shuffle",
                onClick = onShuffle,
                tint = if (isShuffled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            GlowIconButton(
                icon = if (repeatMode == PlaybackRepeatMode.One) {
                    Icons.Rounded.RepeatOne
                } else {
                    Icons.Rounded.Repeat
                },
                contentDescription = "Repeat",
                onClick = onRepeat,
                tint = if (repeatMode == PlaybackRepeatMode.Off) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.secondary
                },
            )
            GlowIconButton(
                icon = Icons.Rounded.Favorite,
                contentDescription = "Like",
                onClick = onLike,
                tint = if (isLiked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            GlowIconButton(
                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                contentDescription = "Queue",
                onClick = onQueue,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            GlowIconButton(
                icon = Icons.Rounded.SkipPrevious,
                contentDescription = "Previous",
                onClick = onPrevious,
            )
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(42.dp),
                    )
                }
            }
            GlowIconButton(
                icon = Icons.Rounded.SkipNext,
                contentDescription = "Next",
                onClick = onNext,
            )
        }
    }
}

@Composable
fun LyricsLineChip(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
) {
    val targetAlpha = if (active) 1f else 0.38f
    val targetScale = if (active) 1f else 0.94f
    GlassPanel(
        modifier = modifier,
        shape = shape,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text(
                text = text,
                style = (if (active) {
                    MaterialTheme.typography.headlineMedium
                } else {
                    MaterialTheme.typography.titleMedium
                }).copy(
                    lineHeight = if (active) 34.sp else 26.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = targetAlpha),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .graphicsLayer(scaleX = targetScale, scaleY = targetScale),
            )
        }
    }
}

@Composable
fun EmptyStatePanel(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
) {
    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun DeviceLibraryStatusCard(
    audioPermissionGranted: Boolean,
    hasScannedDeviceAudio: Boolean,
    isScanningDeviceAudio: Boolean,
    catalogSource: MusicCatalogSource,
    songCount: Int,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
    surfaceAlpha: Float = 0.74f,
    surfaceVariantAlpha: Float = 0.48f,
) {
    val title: String
    val body: String
    val actionLabel: String?

    when {
        isScanningDeviceAudio -> {
            title = "Scanning your device library"
            body = "VerseFlow is reading audio saved on this phone so you can test real local playback."
            actionLabel = null
        }
        catalogSource == MusicCatalogSource.Device && songCount > 0 -> {
            title = "Device library active"
            body = "$songCount songs from this phone are now available across Library, Search, and playback."
            actionLabel = "Refresh"
        }
        audioPermissionGranted && hasScannedDeviceAudio -> {
            title = "No songs"
            body = "No songs were found on this phone yet. Download a song and scan again."
            actionLabel = "Rescan"
        }
        else -> {
            title = "Load songs from this device"
            body = "Allow audio access to browse and play music already saved on your phone."
            actionLabel = "Enable Audio"
        }
    }

    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        surfaceAlpha = surfaceAlpha,
        surfaceVariantAlpha = surfaceVariantAlpha,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (actionLabel != null) {
                VerseFilterChip(
                    label = actionLabel,
                    selected = catalogSource == MusicCatalogSource.Device && audioPermissionGranted,
                    onClick = onAction,
                )
            }
        }
    }
}

@Composable
fun LoadingSkeletonCard(
    modifier: Modifier = Modifier,
) {
    val shimmer by rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.24f,
        targetValue = 0.62f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "skeletonAlpha",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = shimmer)),
    )
}

@Composable
fun WaveVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "wave")
    val bars = listOf(0.28f, 0.46f, 0.72f, 0.52f, 0.34f)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        bars.forEachIndexed { index, base ->
            val animated by transition.animateFloat(
                initialValue = if (isPlaying) base else 0.24f,
                targetValue = if (isPlaying) base + 0.24f else 0.24f,
                animationSpec = infiniteRepeatable(
                    animation = tween(420 + index * 80),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "wave$index",
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(animated)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
            )
        }
    }
}

@Composable
fun VerseFlowBottomBar(
    currentRoute: String?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.56f),
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding(),
    ) {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                    )
                },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackQueueSheet(
    songs: List<Song>,
    currentSongId: String?,
    artistsById: Map<String, Artist>,
    onDismiss: () -> Unit,
    onSongSelected: (Song) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Up next",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(songs, key = { it.id }) { song ->
                    SongListItem(
                        song = song,
                        artistName = artistsById[song.artistId]?.name.orEmpty(),
                        supportingText = formatDuration(song.durationMs),
                        onClick = { onSongSelected(song) },
                        surfaceAlpha = 0f,
                        surfaceVariantAlpha = 0f,
                        borderAlpha = 0f,
                        shadowElevation = 0.dp,
                        trailingContent = {
                            if (song.id == currentSongId) {
                                LiveBadge()
                            }
                        },
                    )
                }
            }
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1_000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
