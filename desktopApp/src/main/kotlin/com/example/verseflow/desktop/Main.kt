package com.example.verseflow.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main(args: Array<String>) = application {
    val previewMode = args.any { it == "--preview" } ||
        System.getProperty("verseflow.desktop.preview")
            ?.equals("true", ignoreCase = true) == true ||
        System.getenv("VERSEFLOW_DESKTOP_PREVIEW")
            ?.equals("true", ignoreCase = true) == true
    Window(
        onCloseRequest = ::exitApplication,
        title = if (previewMode) "VerseFlow Preview" else "VerseFlow",
        state = rememberWindowState(size = DpSize(1440.dp, 920.dp)),
    ) {
        VerseFlowDesktopApp(previewMode = previewMode)
    }
}
