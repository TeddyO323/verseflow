package com.example.verseflow.ui.screens.settings

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.verseflow.model.ThemePreset
import com.example.verseflow.model.VerseFlowUiState
import com.example.verseflow.ui.components.AlbumArtwork
import com.example.verseflow.ui.components.GlassPanel
import com.example.verseflow.ui.components.GlowIconButton
import com.example.verseflow.ui.components.SectionHeader
import com.example.verseflow.ui.components.VerseFilterChip
import com.example.verseflow.ui.car.rememberIsCarLandscapeMode

@Composable
fun SettingsScreen(
    uiState: VerseFlowUiState,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onProfileNameChange: (String) -> Unit,
    onThemeSelected: (ThemePreset) -> Unit,
    onAutoplayChange: (Boolean) -> Unit,
    onImmersiveMotionChange: (Boolean) -> Unit,
    onSyncedLyricsChange: (Boolean) -> Unit,
    onWifiDownloadsChange: (Boolean) -> Unit,
    onExplicitContentChange: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onUseTestArtworkChange: (Boolean) -> Unit = {},
) {
    val settings = uiState.profile.settings
    val context = LocalContext.current
    val isDebugBuild = remember(context) {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    val isCarLandscapeMode = rememberIsCarLandscapeMode()
    if (isCarLandscapeMode) {
        CarSettingsScreen(
            uiState = uiState,
            isDebugBuild = isDebugBuild,
            onOpenSearch = onOpenSearch,
            onProfileNameChange = onProfileNameChange,
            onThemeSelected = onThemeSelected,
            onAutoplayChange = onAutoplayChange,
            onImmersiveMotionChange = onImmersiveMotionChange,
            onSyncedLyricsChange = onSyncedLyricsChange,
            onWifiDownloadsChange = onWifiDownloadsChange,
            onExplicitContentChange = onExplicitContentChange,
            onLanguageSelected = onLanguageSelected,
            onUseTestArtworkChange = onUseTestArtworkChange,
        )
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(top = 20.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 6.dp),
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
                        text = "Settings",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Tune the futuristic playback room to your preferences.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                surfaceAlpha = 0.56f,
                surfaceVariantAlpha = 0.18f,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AlbumArtwork(
                            title = uiState.profile.displayName,
                            subtitle = uiState.profile.membershipTier,
                            palette = uiState.profile.avatarPalette,
                            modifier = Modifier.size(104.dp),
                            shape = RectangleShape,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = uiState.profile.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = uiState.profile.handle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = uiState.profile.membershipTier,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                    TextField(
                        value = uiState.profile.name,
                        onValueChange = onProfileNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Display name") },
                        placeholder = { Text("Music Lover") },
                        singleLine = true,
                        shape = RectangleShape,
                    )
                }
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionHeader(
                    title = "Theme",
                    subtitle = "Choose the glow profile for the entire experience",
                )
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ThemePreset.entries.forEach { preset ->
                    VerseFilterChip(
                        label = preset.label,
                        selected = preset == settings.themePreset,
                        onClick = { onThemeSelected(preset) },
                    )
                }
            }
        }
        item {
            SettingsSection(
                title = "Playback",
                toggles = listOf(
                    SettingToggle("Autoplay next tracks", settings.autoplay, onAutoplayChange),
                    SettingToggle("Immersive motion", settings.immersiveMotion, onImmersiveMotionChange),
                    SettingToggle("Allow explicit content", settings.explicitContent, onExplicitContentChange),
                ),
            )
        }
        item {
            SettingsSection(
                title = "Lyrics",
                toggles = listOf(
                    SettingToggle("Synced lyrics by default", settings.showSyncedLyricsByDefault, onSyncedLyricsChange),
                    SettingToggle("Download over Wi-Fi only", settings.downloadOnWifiOnly, onWifiDownloadsChange),
                ),
            )
        }
        if (isDebugBuild) {
            item {
                SettingsSection(
                    title = "Debug",
                    toggles = listOf(
                        SettingToggle("Use test artwork", settings.useTestArtwork, onUseTestArtworkChange),
                    ),
                )
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionHeader(
                    title = "Language",
                    subtitle = "Prepare for localization and multi-script lyrics later",
                )
            }
        }
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                listOf("English", "Japanese", "French").forEach { language ->
                    VerseFilterChip(
                        label = language,
                        selected = settings.language == language,
                        onClick = { onLanguageSelected(language) },
                    )
                }
            }
        }
        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                surfaceAlpha = 0.56f,
                surfaceVariantAlpha = 0.18f,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Storage & Cache",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Downloads, offline media, and cache controls are wired as polished placeholders for future media integration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                surfaceAlpha = 0.56f,
                surfaceVariantAlpha = 0.18f,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "About VerseFlow",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "A futuristic music player concept focused on cinematic playback and real-time lyrics. Built with Jetpack Compose, Material 3, and mock data structured for future backend integration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CarSettingsScreen(
    uiState: VerseFlowUiState,
    isDebugBuild: Boolean,
    onOpenSearch: () -> Unit,
    onProfileNameChange: (String) -> Unit,
    onThemeSelected: (ThemePreset) -> Unit,
    onAutoplayChange: (Boolean) -> Unit,
    onImmersiveMotionChange: (Boolean) -> Unit,
    onSyncedLyricsChange: (Boolean) -> Unit,
    onWifiDownloadsChange: (Boolean) -> Unit,
    onExplicitContentChange: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onUseTestArtworkChange: (Boolean) -> Unit,
) {
    val settings = uiState.profile.settings
    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        GlassPanel(
            modifier = Modifier
                .weight(0.38f)
                .fillMaxHeight(),
            shape = RectangleShape,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    GlowIconButton(
                        icon = Icons.Rounded.Search,
                        contentDescription = "Search",
                        onClick = onOpenSearch,
                    )
                }
                AlbumArtwork(
                    title = uiState.profile.displayName,
                    subtitle = uiState.profile.membershipTier,
                    palette = uiState.profile.avatarPalette,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RectangleShape,
                )
                TextField(
                    value = uiState.profile.name,
                    onValueChange = onProfileNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Display name") },
                    singleLine = true,
                    shape = RectangleShape,
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ThemePreset.entries.forEach { preset ->
                        VerseFilterChip(
                            label = preset.label,
                            selected = preset == settings.themePreset,
                            onClick = { onThemeSelected(preset) },
                        )
                    }
                }
            }
            item {
                SettingsSection(
                    title = "Playback",
                    toggles = listOf(
                        SettingToggle("Autoplay next tracks", settings.autoplay, onAutoplayChange),
                        SettingToggle("Immersive motion", settings.immersiveMotion, onImmersiveMotionChange),
                        SettingToggle("Allow explicit content", settings.explicitContent, onExplicitContentChange),
                    ),
                )
            }
            item {
                SettingsSection(
                    title = "Lyrics",
                    toggles = listOf(
                        SettingToggle("Synced lyrics by default", settings.showSyncedLyricsByDefault, onSyncedLyricsChange),
                        SettingToggle("Download over Wi-Fi only", settings.downloadOnWifiOnly, onWifiDownloadsChange),
                    ),
                )
            }
            if (isDebugBuild) {
                item {
                    SettingsSection(
                        title = "Debug",
                        toggles = listOf(
                            SettingToggle("Use test artwork", settings.useTestArtwork, onUseTestArtworkChange),
                        ),
                    )
                }
            }
            item {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    surfaceAlpha = 0.56f,
                    surfaceVariantAlpha = 0.18f,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Language",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("English", "Japanese", "French").forEach { language ->
                                VerseFilterChip(
                                    label = language,
                                    selected = settings.language == language,
                                    onClick = { onLanguageSelected(language) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class SettingToggle(
    val label: String,
    val enabled: Boolean,
    val onChange: (Boolean) -> Unit,
)

@Composable
private fun SettingsSection(
    title: String,
    toggles: List<SettingToggle>,
) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        surfaceAlpha = 0.56f,
        surfaceVariantAlpha = 0.18f,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            toggles.forEach { toggle ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = toggle.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Switch(
                        checked = toggle.enabled,
                        onCheckedChange = toggle.onChange,
                    )
                }
            }
        }
    }
}
