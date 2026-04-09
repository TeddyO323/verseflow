package com.example.verseflow.ui.car

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import android.content.pm.ApplicationInfo
import com.example.verseflow.R

@Composable
fun rememberIsCarLandscapeMode(): Boolean {
    val configuration = LocalConfiguration.current
    return remember(
        configuration.screenWidthDp,
        configuration.screenHeightDp,
    ) {
        val width = configuration.screenWidthDp
        val height = configuration.screenHeightDp
        width >= 700 && width > height
    }
}

@Composable
fun rememberCarModeArtworkUri(useTestArtwork: Boolean): String? {
    val context = LocalContext.current
    val isDebugBuild = remember(context) {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    return remember(useTestArtwork, context.packageName) {
        if (isDebugBuild && useTestArtwork) {
            "android.resource://${context.packageName}/${R.drawable.test_cover_art}"
        } else {
            null
        }
    }
}
