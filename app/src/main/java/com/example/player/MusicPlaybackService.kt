package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.PlaybackState

/**
 * Foreground Media Playback Service for FR Music.
 * Keeps audio streaming reliably in the background and when the device screen is turned off,
 * displaying persistent lock-screen & notification shade transport controls.
 */
class MusicPlaybackService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val player = AudioPlayerManager.getInstance(applicationContext)

        when (intent?.action) {
            ACTION_PLAY_PAUSE -> player.playPause()
            ACTION_NEXT -> player.next()
            ACTION_PREVIOUS -> player.previous()
            ACTION_STOP -> {
                player.pause()
                stopForegroundCompat()
                stopSelf()
                return START_NOT_STICKY
            }
        }

        val state = player.uiState.value
        val track = state.currentTrack
        if (track == null && state.playbackState == PlaybackState.IDLE) {
            stopForegroundCompat()
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = buildNotification(state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Keep playing if actively playing in background; only stop if paused/idle
        val state = AudioPlayerManager.getInstance(applicationContext).uiState.value
        if (state.playbackState != PlaybackState.PLAYING && state.playbackState != PlaybackState.BUFFERING) {
            stopForegroundCompat()
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FR Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for background music playback"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(state: PlayerUiState): Notification {
        val track = state.currentTrack
        val isPlaying = state.playbackState == PlaybackState.PLAYING || state.playbackState == PlaybackState.BUFFERING

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            200,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevPendingIntent = PendingIntent.getService(
            this,
            201,
            Intent(this, MusicPlaybackService::class.java).apply { action = ACTION_PREVIOUS },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPausePendingIntent = PendingIntent.getService(
            this,
            202,
            Intent(this, MusicPlaybackService::class.java).apply { action = ACTION_PLAY_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextPendingIntent = PendingIntent.getService(
            this,
            203,
            Intent(this, MusicPlaybackService::class.java).apply { action = ACTION_NEXT },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getService(
            this,
            204,
            Intent(this, MusicPlaybackService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseLabel = if (isPlaying) "Pause" else "Play"
        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play

        val statusSubtitle = when (state.playbackState) {
            PlaybackState.BUFFERING -> "Buffering • ${track?.artist ?: "FR Music"}"
            PlaybackState.PAUSED -> "Paused • ${track?.artist ?: "FR Music"}"
            else -> track?.artist ?: "Your music. Your mood."
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.fr_music_logo)
            .setContentTitle(track?.title ?: "FR Music")
            .setContentText(statusSubtitle)
            .setSubText(track?.album?.takeIf { it.isNotBlank() } ?: "Background Play")
            .setContentIntent(contentPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
            .addAction(playPauseIcon, playPauseLabel, playPausePendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .setDeleteIntent(stopPendingIntent)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "fr_music_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_UPDATE = "com.example.fr_music.service.ACTION_UPDATE"
        const val ACTION_PLAY_PAUSE = "com.example.fr_music.service.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.fr_music.service.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.fr_music.service.ACTION_PREVIOUS"
        const val ACTION_STOP = "com.example.fr_music.service.ACTION_STOP"

        fun syncService(context: Context, state: PlayerUiState) {
            val appCtx = context.applicationContext
            if (state.currentTrack == null && state.playbackState == PlaybackState.IDLE) {
                appCtx.stopService(Intent(appCtx, MusicPlaybackService::class.java))
                return
            }

            val intent = Intent(appCtx, MusicPlaybackService::class.java).apply {
                action = ACTION_UPDATE
            }
            try {
                if (state.playbackState == PlaybackState.PLAYING || state.playbackState == PlaybackState.BUFFERING) {
                    ContextCompat.startForegroundService(appCtx, intent)
                } else {
                    appCtx.startService(intent)
                }
            } catch (_: Exception) {
                // Avoid crashes on background start restrictions on older/restricted states
            }
        }
    }
}
