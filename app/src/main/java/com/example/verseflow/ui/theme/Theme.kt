package com.example.verseflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
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
    ThemePreset.Aurora -> darkColorScheme(
        primary = AuroraCyan,
        onPrimary = InkBlack,
        secondary = SolarGold,
        tertiary = VelvetMagenta,
        background = Color(0xFF040912),
        onBackground = FrostWhite,
        surface = Color(0xFF0B1320),
        onSurface = FrostWhite,
        surfaceVariant = Color(0xFF11233B),
        onSurfaceVariant = Color(0xFFB7CDE1),
        outline = Color(0x3D8AF5FF),
    )
    ThemePreset.Cobalt -> darkColorScheme(
        primary = VerseBlue,
        onPrimary = FrostWhite,
        secondary = ElectricBlue,
        tertiary = IceBlue,
        background = Color(0xFF030714),
        onBackground = FrostWhite,
        surface = Color(0xFF091024),
        onSurface = FrostWhite,
        surfaceVariant = Color(0xFF101A38),
        onSurfaceVariant = Color(0xFFC4D0FF),
        outline = Color(0x3D304CFF),
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
