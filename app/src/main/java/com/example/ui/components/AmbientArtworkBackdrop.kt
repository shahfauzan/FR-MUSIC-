package com.example.ui.components

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Renders a soft, atmospheric album-artwork backdrop blended cleanly into
 * FR Music's crisp White & Gray theme so the playing song's mood illuminates the UI
 * while preserving high-contrast readability.
 */
@Composable
fun AmbientArtworkBackdrop(
    imageUrl: String?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    intensity: Float = 0.28f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_pulse")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = intensity,
        targetValue = (intensity + 0.12f).coerceAtMost(0.45f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val effectiveAlpha = if (isPlaying) animatedAlpha else (intensity * 0.7f)

    Box(modifier = modifier.background(Color(0xFFFFFFFF))) {
        if (!imageUrl.isNullOrBlank()) {
            val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier
                    .fillMaxSize()
                    .blur(42.dp)
                    .alpha(effectiveAlpha)
            } else {
                Modifier
                    .fillMaxSize()
                    .alpha(effectiveAlpha * 0.65f)
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = blurModifier
            )
        }

        // Translucent pearl/gray atmospheric wash for clean white-and-gray legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCCFFFFFF),
                            Color(0xB8F8F9FA),
                            Color(0xE6F1F5F9),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        )
    }
}
