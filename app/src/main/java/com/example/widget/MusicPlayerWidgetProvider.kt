package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.model.PlaybackState
import com.example.player.AudioPlayerManager

class MusicPlayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val player = AudioPlayerManager.getInstance(context)

        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                player.playPause()
                updateAllWidgets(context)
            }
            ACTION_NEXT -> {
                player.next()
                updateAllWidgets(context)
            }
            ACTION_WIDGET_STATE_CHANGED -> {
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.fr_music.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.fr_music.ACTION_NEXT"
        const val ACTION_WIDGET_STATE_CHANGED = "com.example.fr_music.ACTION_WIDGET_STATE_CHANGED"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val player = AudioPlayerManager.getInstance(context)
            val state = player.uiState.value
            val currentTrack = state.currentTrack
            val isPlaying = state.playbackState == PlaybackState.PLAYING

            val views = RemoteViews(context.packageName, R.layout.widget_music_player)

            // Track info
            if (currentTrack != null) {
                views.setTextViewText(R.id.widget_song_title, currentTrack.title)
                views.setTextViewText(R.id.widget_song_artist, currentTrack.artist)
            } else {
                views.setTextViewText(R.id.widget_song_title, "FR Music")
                views.setTextViewText(R.id.widget_song_artist, "Your music. Your mood.")
            }

            // Play / Pause Icon
            val playPauseRes = if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
            views.setImageViewResource(R.id.widget_btn_play_pause, playPauseRes)

            // Click pending intent for Play / Pause
            val playPauseIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            val playPausePendingIntent = PendingIntent.getBroadcast(
                context,
                101,
                playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_play_pause, playPausePendingIntent)

            // Click pending intent for Next
            val nextIntent = Intent(context, MusicPlayerWidgetProvider::class.java).apply {
                action = ACTION_NEXT
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context,
                102,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_next, nextPendingIntent)

            // Click on widget body to launch MainActivity
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val launchPendingIntent = PendingIntent.getActivity(
                context,
                100,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, launchPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, MusicPlayerWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
