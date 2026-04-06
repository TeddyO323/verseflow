package com.example.verseflow.desktop

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.math.roundToLong

data class DesktopPlaybackState(
    val trackId: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val errorMessage: String? = null,
)

sealed interface DesktopPlaybackEvent {
    data object TrackCompleted : DesktopPlaybackEvent
}

private object DesktopJavaFxRuntime {
    private var initialized = false

    @Synchronized
    fun ensureStarted() {
        if (initialized) return
        JFXPanel()
        Platform.setImplicitExit(false)
        initialized = true
    }
}

class DesktopPlaybackController {
    private val _state = MutableStateFlow(DesktopPlaybackState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<DesktopPlaybackEvent>(extraBufferCapacity = 4)
    val events = _events.asSharedFlow()

    private var mediaPlayer: MediaPlayer? = null

    init {
        DesktopJavaFxRuntime.ensureStarted()
    }

    fun loadTrack(
        track: DesktopTrack,
        autoPlay: Boolean = true,
        restart: Boolean = true,
        startPositionMs: Long = 0L,
    ) {
        Platform.runLater {
            if (!restart && _state.value.trackId == track.id && mediaPlayer != null) {
                if (startPositionMs > 0L) {
                    mediaPlayer?.seek(Duration.millis(startPositionMs.toDouble()))
                }
                if (autoPlay) {
                    mediaPlayer?.play()
                } else {
                    mediaPlayer?.pause()
                }
                return@runLater
            }

            mediaPlayer?.stop()
            mediaPlayer?.dispose()

            val media = Media(File(track.path).toURI().toString())
            val player = MediaPlayer(media)
            mediaPlayer = player

            _state.value = DesktopPlaybackState(
                trackId = track.id,
                isPlaying = false,
                positionMs = 0L,
                durationMs = track.durationMs,
                errorMessage = null,
            )

            player.currentTimeProperty().addListener { _, _, newValue ->
                val currentMs = newValue?.toMillis()?.roundToLong()?.coerceAtLeast(0L) ?: 0L
                _state.update { state ->
                    state.copy(positionMs = currentMs)
                }
            }

            player.totalDurationProperty().addListener { _, _, newValue ->
                val durationMs = newValue?.toMillis()?.takeIf(Double::isFinite)?.roundToLong() ?: track.durationMs
                _state.update { state ->
                    state.copy(durationMs = durationMs)
                }
            }

            player.setOnReady {
                val durationMs = player.totalDuration?.toMillis()?.takeIf(Double::isFinite)?.roundToLong() ?: track.durationMs
                val targetPositionMs = startPositionMs.coerceIn(0L, durationMs.coerceAtLeast(0L))
                if (targetPositionMs > 0L) {
                    player.seek(Duration.millis(targetPositionMs.toDouble()))
                }
                _state.update { state ->
                    state.copy(
                        trackId = track.id,
                        durationMs = durationMs,
                        positionMs = targetPositionMs,
                        errorMessage = null,
                    )
                }
                if (autoPlay) {
                    player.play()
                }
            }

            player.setOnPlaying {
                _state.update { state ->
                    state.copy(isPlaying = true, errorMessage = null)
                }
            }

            player.setOnPaused {
                _state.update { state ->
                    state.copy(isPlaying = false)
                }
            }

            player.setOnStopped {
                _state.update { state ->
                    state.copy(isPlaying = false, positionMs = 0L)
                }
            }

            player.setOnEndOfMedia {
                _state.update { state ->
                    state.copy(isPlaying = false, positionMs = state.durationMs)
                }
                _events.tryEmit(DesktopPlaybackEvent.TrackCompleted)
            }

            player.setOnError {
                _state.update { state ->
                    state.copy(
                        isPlaying = false,
                        errorMessage = player.error?.message ?: media.error?.message ?: "VerseFlow could not play this file.",
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        Platform.runLater {
            val player = mediaPlayer ?: return@runLater
            when (player.status) {
                MediaPlayer.Status.PLAYING -> player.pause()
                MediaPlayer.Status.READY,
                MediaPlayer.Status.PAUSED,
                MediaPlayer.Status.STOPPED -> player.play()
                else -> Unit
            }
        }
    }

    fun seekTo(positionMs: Long) {
        Platform.runLater {
            val player = mediaPlayer ?: return@runLater
            val stateDuration = _state.value.durationMs
            val playerDuration = player.totalDuration?.toMillis()?.takeIf(Double::isFinite)?.roundToLong() ?: 0L
            val resolvedDuration = maxOf(stateDuration, playerDuration, positionMs)
            val target = positionMs.coerceIn(0L, resolvedDuration.coerceAtLeast(0L))
            player.seek(Duration.millis(target.toDouble()))
            _state.update { state ->
                state.copy(
                    positionMs = target,
                    durationMs = maxOf(state.durationMs, playerDuration, target),
                )
            }
        }
    }

    fun stopPlayback() {
        Platform.runLater {
            mediaPlayer?.stop()
            mediaPlayer?.dispose()
            mediaPlayer = null
            _state.value = DesktopPlaybackState()
        }
    }

    fun dispose() {
        stopPlayback()
    }
}
