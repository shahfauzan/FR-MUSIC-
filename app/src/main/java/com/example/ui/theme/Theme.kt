package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FRWhiteGrayColorScheme = lightColorScheme(
    primary = FRCharcoal,
    onPrimary = Color.White,
    primaryContainer = FRSurfaceElevated,
    onPrimaryContainer = FRCharcoal,
    secondary = FRSlate,
    onSecondary = Color.White,
    secondaryContainer = FRSurfaceVariant,
    onSecondaryContainer = FRTextPrimary,
    tertiary = FRMagenta,
    onTertiary = Color.White,
    background = FRBackground,
    onBackground = FRTextPrimary,
    surface = FRSurface,
    onSurface = FRTextPrimary,
    surfaceVariant = FRSurfaceVariant,
    onSurfaceVariant = FRTextSecondary,
    outline = FRDivider,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = FRWhiteGrayColorScheme,
        typography = Typography,
        content = content
    )
}
