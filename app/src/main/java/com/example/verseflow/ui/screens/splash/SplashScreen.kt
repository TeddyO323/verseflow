package com.example.verseflow.ui.screens.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.verseflow.ui.theme.AuroraCyan
import com.example.verseflow.ui.theme.InkBlack
import com.example.verseflow.ui.theme.NebulaBlue
import com.example.verseflow.ui.theme.NovaPink
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "splash")
    val rotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8_000),
            repeatMode = RepeatMode.Restart,
        ),
        label = "splashRotation",
    )
    val pulse = transition.animateFloat(
        initialValue = 0.84f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "splashPulse",
    )

    LaunchedEffect(Unit) {
        delay(2_300L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size((280 * pulse.value).dp)
                .rotate(rotation.value)
                .border(
                    width = 1.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.Transparent,
                            AuroraCyan.copy(alpha = 0.85f),
                            NovaPink.copy(alpha = 0.72f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(188.dp)
                .rotate(-rotation.value * 0.6f)
                .border(
                    width = 1.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.Transparent,
                            NebulaBlue.copy(alpha = 0.72f),
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(114.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(AuroraCyan.copy(alpha = 0.28f), Color.Transparent),
                        ),
                        CircleShape,
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.12f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "V",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "VerseFlow",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
            )
            Text(
                text = "Your music, rendered in motion.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.74f),
            )
        }
    }
}
