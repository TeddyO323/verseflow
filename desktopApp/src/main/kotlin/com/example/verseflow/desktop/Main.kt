package com.example.verseflow.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "VerseFlow",
        state = rememberWindowState(size = DpSize(1440.dp, 920.dp)),
    ) {
        VerseFlowDesktopApp()
    }
}
