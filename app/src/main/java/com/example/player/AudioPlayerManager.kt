package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import com.example.R
import com.example.data.model.PlaybackState
import com.example.data.model.RepeatMode
import com.example.data.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class PlayerUiState(
    val currentTrack: Track? = null,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 180000L,
    val queue: List<Track> = emptyList(),
    val currentQueueIndex: Int = -1,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val volume: Float = 1.0f,
    val errorMessage: String? = null
)

class AudioPlayerManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var streamPrepareJob: Job? = null

    private val redirectClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var originalQueue: List<Track> = emptyList()

    private fun getFallbackResource(track: Track): Int {
        val lang = track.language.lowercase()
        val genre = track.genre.lowercase()
        return when {
            lang.contains("punjabi") -> R.raw.fr_punjabi_beat
            lang.contains("english") || genre.contains("pop") -> R.raw.fr_english_pop
            genre.contains("lo-fi") || genre.contains("chill") -> R.raw.fr_lofi_chill
            else -> R.raw.fr_hindi_melody
        }
    }

    private suspend fun resolveDirectStreamUrl(url: String): String = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "FRMusic/1.0 (Android; Linux)")
                .header("Range", "bytes=0-1")
                .get()
                .build()
            redirectClient.newCall(request).execute().use { response ->
                val finalUrl = response.request.url.toString()
                if (response.isSuccessful && finalUrl.isNotBlank()) finalUrl else url
            }
        } catch (e: Exception) {
            Log.w("AudioPlayer", "Redirect resolve skipped for $url: ${e.message}")
            url
        }
    }

    private fun initMediaPlayer() {
        releasePlayer()
        mediaPlayer = MediaPlayer().apply {
            // Keep CPU awake when playing in background or with screen off
            setWakeMode(context.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener { mp ->
                try {
                    mp.start()
                    val dur = mp.duration.toLong().coerceAtLeast(1000L)
                    _uiState.value = _uiState.value.copy(
                        playbackState = PlaybackState.PLAYING,
                        durationMs = dur,
                        errorMessage = null
                    )
                    startProgressTracker()
                    notifyWidgetUpdate()
                } catch (e: Exception) {
                    Log.e("AudioPlayer", "Error starting prepared player", e)
                }
            }
            setOnCompletionListener {
                handleTrackCompletion()
            }
            setOnErrorListener { _, what, extra ->
                Log.w("AudioPlayer", "Stream playback error ($what, $extra). Recovering with bundled track...")
                val track = _uiState.value.currentTrack
                if (track != null) {
                    playRawResource(getFallbackResource(track))
                } else {
                    _uiState.value = _uiState.value.copy(
                        playbackState = PlaybackState.ERROR,
                        errorMessage = "Playback error. Retrying with cached track."
                    )
                    stopProgressTracker()
                }
                true
            }
        }
    }

    private fun playRawResource(resId: Int) {
        try {
            initMediaPlayer()
            val afd = context.resources.openRawResourceFd(resId) ?: return
            mediaPlayer?.apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setVolume(_uiState.value.volume, _uiState.value.volume)
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error preparing raw audio resource", e)
            _uiState.value = _uiState.value.copy(playbackState = PlaybackState.ERROR)
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    try {
                        if (mp.isPlaying) {
                            _uiState.value = _uiState.value.copy(
                                currentPositionMs = mp.currentPosition.toLong(),
                                durationMs = mp.duration.toLong().coerceAtLeast(1000L)
                            )
                        }
                    } catch (e: Exception) {
                        // ignore state sync hiccups
                    }
                }
                delay(250)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playTrack(track: Track, newQueue: List<Track>? = null) {
        streamPrepareJob?.cancel()
        val updatedQueue = newQueue ?: if (_uiState.value.queue.none { it.id == track.id }) {
            _uiState.value.queue + track
        } else {
            _uiState.value.queue
        }
        val targetIndex = updatedQueue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)

        originalQueue = updatedQueue
        _uiState.value = _uiState.value.copy(
            currentTrack = track,
            queue = updatedQueue,
            currentQueueIndex = targetIndex,
            playbackState = PlaybackState.BUFFERING,
            currentPositionMs = 0L,
            durationMs = track.durationMs.coerceAtLeast(180000L),
            errorMessage = null
        )

        val streamUrl = track.previewUrl
        val fallbackRes = getFallbackResource(track)

        if (streamUrl != null && (streamUrl.startsWith("http://") || streamUrl.startsWith("https://"))) {
            streamPrepareJob = scope.launch {
                try {
                    val directUrl = resolveDirectStreamUrl(streamUrl)
                    if (!isActive) return@launch
                    initMediaPlayer()
                    mediaPlayer?.apply {
                        val headers = mapOf("User-Agent" to "FRMusic/1.0 (Android; Linux)")
                        setDataSource(context.applicationContext, Uri.parse(directUrl), headers)
                        setVolume(_uiState.value.volume, _uiState.value.volume)
                        prepareAsync()
                    }
                } catch (e: Exception) {
                    Log.w("AudioPlayer", "Network playback init failed, falling back to 3-min bundled track", e)
                    playRawResource(fallbackRes)
                }
            }
        } else {
            // Play verified 3-min bundled audio
            playRawResource(fallbackRes)
        }
    }

    fun playPause() {
        val current = _uiState.value.playbackState
        val player = mediaPlayer ?: return

        when (current) {
            PlaybackState.PLAYING -> {
                try {
                    player.pause()
                    _uiState.value = _uiState.value.copy(playbackState = PlaybackState.PAUSED)
                    stopProgressTracker()
                } catch (e: Exception) {
                    Log.e("AudioPlayer", "Error pausing", e)
                }
            }
            PlaybackState.PAUSED -> {
                try {
                    player.start()
                    _uiState.value = _uiState.value.copy(playbackState = PlaybackState.PLAYING)
                    startProgressTracker()
                } catch (e: Exception) {
                    Log.e("AudioPlayer", "Error resuming", e)
                }
            }
            PlaybackState.IDLE, PlaybackState.ERROR -> {
                _uiState.value.currentTrack?.let { playTrack(it) }
            }
            PlaybackState.BUFFERING -> {}
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _uiState.value = _uiState.value.copy(playbackState = PlaybackState.PAUSED)
            stopProgressTracker()
            notifyWidgetUpdate()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error in pause()", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
            _uiState.value = _uiState.value.copy(playbackState = PlaybackState.PLAYING)
            startProgressTracker()
            notifyWidgetUpdate()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error in resume()", e)
        }
    }

    fun next() {
        val state = _uiState.value
        if (state.queue.isEmpty()) return

        val nextIndex = when {
            state.repeatMode == RepeatMode.ONE -> state.currentQueueIndex
            state.currentQueueIndex < state.queue.size - 1 -> state.currentQueueIndex + 1
            state.repeatMode == RepeatMode.ALL -> 0
            else -> -1
        }

        if (nextIndex in state.queue.indices) {
            val nextTrack = state.queue[nextIndex]
            playTrack(nextTrack, state.queue)
        } else {
            stopProgressTracker()
            mediaPlayer?.stop()
            _uiState.value = state.copy(playbackState = PlaybackState.IDLE, currentPositionMs = 0L)
        }
    }

    fun previous() {
        val state = _uiState.value
        if (state.queue.isEmpty()) return

        if (state.currentPositionMs > 3000L) {
            seekTo(0L)
            return
        }

        val prevIndex = if (state.currentQueueIndex > 0) {
            state.currentQueueIndex - 1
        } else {
            if (state.repeatMode == RepeatMode.ALL) state.queue.size - 1 else 0
        }

        if (prevIndex in state.queue.indices) {
            val prevTrack = state.queue[prevIndex]
            playTrack(prevTrack, state.queue)
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.seekTo(positionMs.toInt())
            _uiState.value = _uiState.value.copy(currentPositionMs = positionMs)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error seeking to $positionMs", e)
        }
    }

    fun toggleShuffle() {
        val state = _uiState.value
        val newShuffle = !state.isShuffle
        val currentTrack = state.currentTrack

        val newQueue = if (newShuffle) {
            val shuffled = state.queue.toMutableList()
            if (currentTrack != null) {
                shuffled.remove(currentTrack)
                shuffled.shuffle()
                listOf(currentTrack) + shuffled
            } else {
                shuffled.shuffled()
            }
        } else {
            originalQueue.ifEmpty { state.queue }
        }

        val newIndex = currentTrack?.let { ct -> newQueue.indexOfFirst { it.id == ct.id } } ?: 0
        _uiState.value = state.copy(
            isShuffle = newShuffle,
            queue = newQueue,
            currentQueueIndex = newIndex
        )
    }

    fun shuffleQueue() {
        toggleShuffle()
    }

    fun toggleRepeat() {
        val nextMode = when (_uiState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _uiState.value = _uiState.value.copy(repeatMode = nextMode)
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(clamped, clamped)
        _uiState.value = _uiState.value.copy(volume = clamped)
    }

    fun addToQueue(track: Track) {
        val currentQueue = _uiState.value.queue
        val updated = currentQueue + track
        _uiState.value = _uiState.value.copy(queue = updated)
    }

    fun playNext(track: Track) {
        val state = _uiState.value
        val currentIdx = state.currentQueueIndex
        val updated = state.queue.toMutableList()
        val insertAt = (currentIdx + 1).coerceAtMost(updated.size)
        updated.add(insertAt, track)
        _uiState.value = state.copy(queue = updated)
    }

    fun removeFromQueue(index: Int) {
        val state = _uiState.value
        if (index !in state.queue.indices) return
        val updated = state.queue.toMutableList().apply { removeAt(index) }
        val newIndex = when {
            index < state.currentQueueIndex -> state.currentQueueIndex - 1
            index == state.currentQueueIndex -> state.currentQueueIndex.coerceAtMost(updated.size - 1)
            else -> state.currentQueueIndex
        }
        _uiState.value = state.copy(queue = updated, currentQueueIndex = newIndex)
    }

    fun clearQueue() {
        val state = _uiState.value
        val current = state.currentTrack
        _uiState.value = state.copy(
            queue = if (current != null) listOf(current) else emptyList(),
            currentQueueIndex = if (current != null) 0 else -1
        )
    }

    fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val state = _uiState.value
        if (fromIndex !in state.queue.indices || toIndex !in state.queue.indices) return
        val updated = state.queue.toMutableList()
        val item = updated.removeAt(fromIndex)
        updated.add(toIndex, item)
        val currentTrack = state.currentTrack
        val newIndex = currentTrack?.let { ct -> updated.indexOfFirst { it.id == ct.id } } ?: -1
        _uiState.value = state.copy(queue = updated, currentQueueIndex = newIndex)
    }

    private fun handleTrackCompletion() {
        val state = _uiState.value
        if (state.repeatMode == RepeatMode.ONE) {
            seekTo(0L)
            mediaPlayer?.start()
            _uiState.value = state.copy(playbackState = PlaybackState.PLAYING)
            startProgressTracker()
        } else {
            next()
        }
    }

    fun releasePlayer() {
        stopProgressTracker()
        try {
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error releasing MediaPlayer", e)
        }
        mediaPlayer = null
    }

    fun notifyWidgetUpdate() {
        try {
            val intent = android.content.Intent("com.example.fr_music.ACTION_WIDGET_STATE_CHANGED")
            intent.setPackage(context.packageName)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            // ignore
        }
    }

    companion object {
        @Volatile
        private var instance: AudioPlayerManager? = null

        fun getInstance(context: Context): AudioPlayerManager {
            return instance ?: synchronized(this) {
                instance ?: AudioPlayerManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
