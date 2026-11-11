package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    MaterialTheme(
        colorScheme = FRWhiteGrayColorScheme,
        typography = Typography,
        content = content
    )
}
