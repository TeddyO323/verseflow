package com.example.verseflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.ThemePreset

private fun colorSchemeFor(preset: ThemePreset) = when (preset) {
    ThemePreset.Nebula -> darkColorScheme(
        primary = NebulaBlue,
        onPrimary = InkBlack,
        secondary = AuroraCyan,
        tertiary = NovaPink,
        background = InkBlack,
        onBackground = FrostWhite,
        surface = DeepSpace,
        onSurface = FrostWhite,
        surfaceVariant = Color(0xFF10162A),
        onSurfaceVariant = MutedLavender,
        outline = Color(0x33FFFFFF),
    )
    ThemePreset.Eclipse -> darkColorScheme(
        primary = Color(0xFF89B4FF),
        onPrimary = EclipseBlack,
        secondary = Color(0xFFE0ECFF),
        tertiary = Color(0xFF6EE7FF),
        background = EclipseBlack,
        onBackground = FrostWhite,
        surface = Color(0xFF080A10),
        onSurface = FrostWhite,
        surfaceVariant = Color(0xFF101319),
        onSurfaceVariant = Color(0xFFB9C1D9),
        outline = Color(0x26FFFFFF),
    )
    ThemePreset.Crimson -> darkColorScheme(
        primary = Color(0xFFFF6B81),
        onPrimary = Color(0xFF22070C),
        secondary = Color(0xFFFFB38A),
        tertiary = Color(0xFFFFD166),
        background = Color(0xFF11070B),
        onBackground = Color(0xFFFFF1F3),
        surface = Color(0xFF1A0D13),
        onSurface = Color(0xFFFFF1F3),
        surfaceVariant = Color(0xFF301721),
        onSurfaceVariant = Color(0xFFF4C4CD),
        outline = Color(0x4DFF8FA4),
    )
    ThemePreset.Solar -> darkColorScheme(
        primary = Color(0xFFFFC145),
        onPrimary = Color(0xFF2B1800),
        secondary = Color(0xFFFF8C42),
        tertiary = Color(0xFFFFE08A),
        background = Color(0xFF171006),
        onBackground = Color(0xFFFFF6E7),
        surface = Color(0xFF211707),
        onSurface = Color(0xFFFFF6E7),
        surfaceVariant = Color(0xFF3A260B),
        onSurfaceVariant = Color(0xFFF6D8A4),
        outline = Color(0x59FFB347),
    )
    ThemePreset.Cobalt -> darkColorScheme(
        primary = Color(0xFF4D7CFE),
        onPrimary = Color(0xFFF8FBFF),
        secondary = Color(0xFF7BDFF6),
        tertiary = Color(0xFF9F86FF),
        background = Color(0xFF04101F),
        onBackground = Color(0xFFF5F9FF),
        surface = Color(0xFF0B172C),
        onSurface = Color(0xFFF5F9FF),
        surfaceVariant = Color(0xFF132546),
        onSurfaceVariant = Color(0xFFC8D8FF),
        outline = Color(0x5E78A6FF),
    )
    ThemePreset.Arctic -> lightColorScheme(
        primary = Color(0xFF2B6CFF),
        onPrimary = Color.White,
        secondary = Color(0xFF76C7FF),
        tertiary = Color(0xFF95A8FF),
        background = Color(0xFFF4FAFF),
        onBackground = Color(0xFF0E1A26),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF0E1A26),
        surfaceVariant = Color(0xFFDCEBFA),
        onSurfaceVariant = Color(0xFF42576E),
        outline = Color(0x663F7EBD),
    )
    ThemePreset.Rose -> lightColorScheme(
        primary = Color(0xFFD94F70),
        onPrimary = Color.White,
        secondary = Color(0xFFFFA8B8),
        tertiary = Color(0xFFFFD6DE),
        background = Color(0xFFFFF6F8),
        onBackground = Color(0xFF2B1820),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF2B1820),
        surfaceVariant = Color(0xFFF9E2E7),
        onSurfaceVariant = Color(0xFF6F4C59),
        outline = Color(0x66D7748F),
    )
    ThemePreset.Mint -> lightColorScheme(
        primary = Color(0xFF0A9E88),
        onPrimary = Color.White,
        secondary = Color(0xFF7BE0C9),
        tertiary = Color(0xFFFFD166),
        background = Color(0xFFF2FFF9),
        onBackground = Color(0xFF112820),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF112820),
        surfaceVariant = Color(0xFFD8F4EA),
        onSurfaceVariant = Color(0xFF4D6F66),
        outline = Color(0x6617A38E),
    )
    ThemePreset.Amber -> lightColorScheme(
        primary = Color(0xFFD97706),
        onPrimary = Color.White,
        secondary = Color(0xFFFFC857),
        tertiary = Color(0xFFFF8F66),
        background = Color(0xFFFFF8EE),
        onBackground = Color(0xFF2D2010),
        surface = Color(0xFFFFFEFB),
        onSurface = Color(0xFF2D2010),
        surfaceVariant = Color(0xFFF6E7CE),
        onSurfaceVariant = Color(0xFF6F5A3D),
        outline = Color(0x66D68B17),
    )
    ThemePreset.Mono -> lightColorScheme(
        primary = Color(0xFF202020),
        onPrimary = Color.White,
        secondary = Color(0xFF5C5C5C),
        tertiary = Color(0xFFB1B1B1),
        background = Color(0xFFF1F1F1),
        onBackground = Color(0xFF121212),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF121212),
        surfaceVariant = Color(0xFFE1E1E1),
        onSurfaceVariant = Color(0xFF5A5A5A),
        outline = Color(0x665A5A5A),
    )
}

private val VerseFlowShapes = Shapes(
    extraSmall = RoundedCornerShape(14.dp),
    small = RoundedCornerShape(18.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(30.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun VerseFlowTheme(
    preset: ThemePreset = ThemePreset.Nebula,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorSchemeFor(preset),
        typography = VerseFlowTypography,
        shapes = VerseFlowShapes,
        content = content,
    )
}
