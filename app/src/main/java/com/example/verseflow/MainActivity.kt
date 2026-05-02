package com.example.verseflow

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    private var pendingExternalAudioUri by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingExternalAudioUri = intent.externalAudioUri()
        enableEdgeToEdge()
        setContent {
            VerseFlowApp(
                externalAudioUri = pendingExternalAudioUri,
                onExternalAudioUriConsumed = { consumedUri ->
                    if (pendingExternalAudioUri == consumedUri) {
                        pendingExternalAudioUri = null
                    }
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingExternalAudioUri = intent.externalAudioUri()
    }
}

private fun Intent?.externalAudioUri(): String? {
    val safeIntent = this ?: return null
    if (safeIntent.action != Intent.ACTION_VIEW) return null
    return safeIntent.dataString?.takeIf(String::isNotBlank)
}
