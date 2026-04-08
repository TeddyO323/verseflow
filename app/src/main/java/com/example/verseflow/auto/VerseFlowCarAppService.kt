package com.example.verseflow.auto

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.ScreenManager
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.validation.HostValidator
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.verseflow.VerseFlowPlaybackService
import com.example.verseflow.data.CachedLyrics
import com.example.verseflow.data.DeviceAudioCatalog
import com.example.verseflow.data.DeviceAudioStoreLoader
import com.example.verseflow.data.LocalLyricsMetadataResolver
import com.example.verseflow.data.LyricsCacheStore
import com.example.verseflow.model.Song
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class VerseFlowCarAppService : CarAppService() {
    override fun createHostValidator(): HostValidator = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession(): Session = VerseFlowCarSession()
}

private class VerseFlowCarSession : Session() {
    private val playbackBridge by lazy { VerseFlowCarPlaybackBridge(carContext) }

    init {
        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    playbackBridge.release()
                }
            },
        )
    }

    override fun onCreateScreen(intent: Intent): Screen = VerseFlowNowPlayingScreen(carContext, playbackBridge)
}

private class VerseFlowNowPlayingScreen(
    carContext: CarContext,
    private val playbackBridge: VerseFlowCarPlaybackBridge,
) : Screen(carContext) {

    override fun onGetTemplate(): androidx.car.app.model.Template {
        val currentSong = playbackBridge.currentSong()
        val screenManager = carContext.getCarService(CarContext.SCREEN_SERVICE) as ScreenManager
        val listBuilder = ItemList.Builder()
            .addItem(
                Row.Builder()
                    .setTitle(currentSong?.title?.takeIf(String::isNotBlank) ?: "Nothing is playing")
                    .addText(
                        playbackBridge.currentArtistName()
                            ?.takeIf(String::isNotBlank)
                            ?: "Start playback from VerseFlow to see track details here",
                    )
                    .build(),
            )
            .addItem(
                Row.Builder()
                    .setTitle("Live lyrics")
                    .addText(
                        playbackBridge.currentLyricLine()
                            ?.takeIf(String::isNotBlank)
                            ?: "Open the dedicated lyrics screen",
                    )
                    .setOnClickListener {
                        screenManager.push(VerseFlowLyricsScreen(carContext, playbackBridge))
                    }
                    .build(),
            )
        if (currentSong != null) {
            listBuilder
                .addItem(
                    Row.Builder()
                        .setTitle(if (playbackBridge.isPlaying()) "Pause" else "Play")
                        .setOnClickListener { playbackBridge.togglePlayPause() }
                        .build(),
                )
                .addItem(
                    Row.Builder()
                        .setTitle("Next")
                        .setOnClickListener { playbackBridge.skipNext() }
                        .build(),
                )
        }
        val list = listBuilder.build()

        return ListTemplate.Builder()
            .setTitle("Now playing")
            .setSingleList(list)
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}

private class VerseFlowLyricsScreen(
    carContext: CarContext,
    private val playbackBridge: VerseFlowCarPlaybackBridge,
) : Screen(carContext) {
    private val invalidationListener: () -> Unit = { invalidate() }

    init {
        playbackBridge.addListener(invalidationListener)
        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    playbackBridge.removeListener(invalidationListener)
                }
            },
        )
    }

    override fun onGetTemplate(): androidx.car.app.model.Template {
        val lyricLine = playbackBridge.currentLyricLine()
            ?.takeIf(String::isNotBlank)
            ?: "No live lyric available for the current song"
        val playPauseTitle = if (playbackBridge.isPlaying()) "Pause" else "Play"

        val pane = Pane.Builder()
            .addRow(
                Row.Builder()
                    .setTitle(lyricLine)
                    .build(),
            )
            .addAction(
                Action.Builder()
                    .setTitle("Prev")
                    .setOnClickListener { playbackBridge.skipPrevious() }
                    .build(),
            )
            .addAction(
                Action.Builder()
                    .setTitle(playPauseTitle)
                    .setOnClickListener { playbackBridge.togglePlayPause() }
                    .build(),
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle("Live lyrics")
            .setHeaderAction(Action.BACK)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle("Next")
                            .setOnClickListener { playbackBridge.skipNext() }
                            .build(),
                    )
                    .build(),
            )
            .build()
    }
}

private class VerseFlowCarPlaybackBridge(
    private val carContext: CarContext,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val lyricsCacheStore = LyricsCacheStore(carContext)
    private val deviceAudioLoader = DeviceAudioStoreLoader(carContext)
    private val listeners = linkedSetOf<() -> Unit>()

    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var catalog: DeviceAudioCatalog? = null
    private var tickerJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (
                events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                events.contains(Player.EVENT_IS_PLAYING_CHANGED) ||
                events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                events.contains(Player.EVENT_POSITION_DISCONTINUITY)
            ) {
                refreshTicker()
                notifyChanged()
            }
        }
    }

    init {
        connectController()
        scope.launch(Dispatchers.IO) {
            catalog = deviceAudioLoader.load()
            notifyChanged()
        }
    }

    fun addListener(listener: () -> Unit) {
        listeners += listener
    }

    fun removeListener(listener: () -> Unit) {
        listeners -= listener
    }

    fun currentSong(): Song? {
        val activeCatalog = catalog ?: return null
        val mediaId = controller?.currentMediaItem?.mediaId
        if (!mediaId.isNullOrBlank()) {
            activeCatalog.songs.firstOrNull { it.id == mediaId }?.let { return it }
        }
        val currentUri = controller?.currentMediaItem?.localConfiguration?.uri?.toString()
        return activeCatalog.songs.firstOrNull { it.mediaUri == currentUri }
    }

    fun currentLyricLine(): String? {
        val song = currentSong() ?: return null
        val positionMs = controller?.currentPosition?.coerceAtLeast(0L) ?: 0L
        val cached = lyricsCacheStore.load(song.mediaUri)
        val synced = cached?.syncedLyrics.orEmpty().filter { it.text.isNotBlank() }
        if (synced.isNotEmpty()) {
            val activeIndex = synced.indexOfLast { it.timestampMs <= positionMs }
                .takeIf { it >= 0 }
                ?: 0
            return synced[activeIndex].text
        }
        val plain = cached?.plainLyrics?.firstOrNull(String::isNotBlank)
        if (!plain.isNullOrBlank()) return plain
        if (song.mediaUri.isNullOrBlank()) return null
        return runBlocking {
            LocalLyricsMetadataResolver.loadPlainLyrics(
                context = carContext,
                mediaUri = song.mediaUri,
            )
        }.firstOrNull(String::isNotBlank)
    }

    fun currentArtistName(): String? {
        val song = currentSong() ?: return null
        val activeCatalog = catalog ?: return null
        return activeCatalog.artists.firstOrNull { it.id == song.artistId }?.name
    }

    fun isPlaying(): Boolean = controller?.isPlaying == true

    fun togglePlayPause() {
        val activeController = controller ?: return
        if (activeController.isPlaying) activeController.pause() else activeController.play()
        notifyChanged()
    }

    fun skipPrevious() {
        val activeController = controller ?: return
        if (activeController.currentPosition > 5_000L) {
            activeController.seekTo(0L)
        } else if (activeController.hasPreviousMediaItem()) {
            activeController.seekToPreviousMediaItem()
        } else {
            activeController.seekTo(0L)
        }
        if (!activeController.isPlaying) activeController.play()
        notifyChanged()
    }

    fun skipNext() {
        val activeController = controller ?: return
        if (activeController.hasNextMediaItem()) {
            activeController.seekToNextMediaItem()
            if (!activeController.isPlaying) activeController.play()
            notifyChanged()
        }
    }

    fun release() {
        tickerJob?.cancel()
        controller?.removeListener(playerListener)
        controllerFuture?.let(MediaController::releaseFuture)
        controller = null
        scope.cancel()
    }

    private fun connectController() {
        val token = SessionToken(
            carContext,
            ComponentName(carContext, VerseFlowPlaybackService::class.java),
        )
        controllerFuture = MediaController.Builder(carContext, token).buildAsync().also { future ->
            future.addListener(
                {
                    val builtController = runCatching { future.get() }.getOrNull() ?: return@addListener
                    controller = builtController
                    builtController.addListener(playerListener)
                    refreshTicker()
                    notifyChanged()
                },
                ContextCompat.getMainExecutor(carContext),
            )
        }
    }

    private fun refreshTicker() {
        tickerJob?.cancel()
        val activeController = controller ?: return
        if (!activeController.isPlaying) return
        tickerJob = scope.launch {
            while (isActive && controller?.isPlaying == true) {
                notifyChanged()
                delay(1_250L)
            }
        }
    }

    private fun notifyChanged() {
        listeners.forEach { it.invoke() }
    }
}
