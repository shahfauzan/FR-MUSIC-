package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MainScreen
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.MyApplicationTheme
import com.example.widget.MusicShortcutsWidgetProvider

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleShortcutIntent(intent)
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShortcutIntent(intent)
    }

    private fun handleShortcutIntent(intent: Intent?) {
        val genre = intent?.getStringExtra(MusicShortcutsWidgetProvider.EXTRA_GENRE_SHORTCUT)
        if (!genre.isNullOrBlank()) {
            viewModel.navigateTo(ScreenDestination.Search)
            viewModel.onSearchQueryChanged(genre)
        }
    }
}
