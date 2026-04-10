package com.example.verseflow.ui.car

import java.io.File
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
    return remember(useTestArtwork, context.packageName) {
        if (useTestArtwork) {
            val workspaceTestArt = File("/Users/aliceakinyiolango/Documents/GitHub/verseflow/testalbumart.jpg")
            if (workspaceTestArt.exists()) {
                workspaceTestArt.toURI().toString()
            } else {
                "android.resource://${context.packageName}/${R.drawable.test_cover_art}"
            }
        } else {
            null
        }
    }
}

@Composable
fun ProvideCarTestArtwork(
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalCarUseTestArtwork provides enabled) {
        content()
    }
}
