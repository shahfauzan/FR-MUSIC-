package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class MusicShortcutsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val EXTRA_GENRE_SHORTCUT = "EXTRA_GENRE_SHORTCUT"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_music_shortcuts)

            // Main launch intent
            val rootIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val rootPendingIntent = PendingIntent.getActivity(
                context,
                200,
                rootIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_shortcuts_root, rootPendingIntent)

            // Hindi Shortcut
            views.setOnClickPendingIntent(
                R.id.widget_shortcut_hindi,
                createGenrePendingIntent(context, "Hindi", 201)
            )

            // English Shortcut
            views.setOnClickPendingIntent(
                R.id.widget_shortcut_english,
                createGenrePendingIntent(context, "English", 202)
            )

            // Punjabi Shortcut
            views.setOnClickPendingIntent(
                R.id.widget_shortcut_punjabi,
                createGenrePendingIntent(context, "Punjabi", 203)
            )

            // Lo-fi Shortcut
            views.setOnClickPendingIntent(
                R.id.widget_shortcut_lofi,
                createGenrePendingIntent(context, "Lo-fi", 204)
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun createGenrePendingIntent(context: Context, genre: String, requestCode: Int): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_GENRE_SHORTCUT, genre)
            }
            return PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
