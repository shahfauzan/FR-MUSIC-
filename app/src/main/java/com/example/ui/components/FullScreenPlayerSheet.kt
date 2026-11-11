package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PlaybackState
import com.example.data.model.RepeatMode as DomainRepeatMode
import com.example.data.model.Track
import com.example.ui.theme.FRBackground
import com.example.ui.theme.FRCharcoal
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRMagenta
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary
import com.example.ui.theme.FRViolet

@Composable
fun FullScreenPlayerSheet(
    track: Track,
    playbackState: PlaybackState,
    currentPositionMs: Long,
    durationMs: Long,
    isShuffle: Boolean,
    repeatMode: DomainRepeatMode,
    volume: Float,
    isFavorite: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onFavoriteToggle: () -> Unit,
    onOpenQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    val actualProgress = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val displayProgress = if (isDraggingSlider) sliderPosition else actualProgress

    fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%d:%02d".format(min, sec)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF8F9FA),
                        Color(0xFFF1F5F9)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("full_screen_player")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("collapse_player_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = FRTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = FRTextMuted,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Text(
                        text = track.album.ifEmpty { "FR Music Charts" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FRTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Listening to ${track.title} by ${track.artist} on FR Music! 🎵 #FRMusic"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Track"))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = FRTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Album Artwork with Ambient Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0x20000000),
                        spotColor = Color(0x30000000)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = track.albumArtUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Error Message Banner if any
            if (errorMessage != null) {
                Surface(
                    color = Color(0x1FE11D48),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFE11D48),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            // Song Info & Favorite Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FRTextPrimary,
                            fontSize = 20.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = FRTextSecondary,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Unlike" else "Like",
                        tint = if (isFavorite) FRMagenta else FRTextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Slider
            Slider(
                value = displayProgress,
                onValueChange = {
                    isDraggingSlider = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    isDraggingSlider = false
                    val targetMs = (sliderPosition * durationMs).toLong()
                    onSeek(targetMs)
                },
                colors = SliderDefaults.colors(
                    thumbColor = FRCharcoal,
                    activeTrackColor = FRCharcoal,
                    inactiveTrackColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_seek_slider")
            )

            // Timestamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentMs = if (isDraggingSlider) {
                    (sliderPosition * durationMs).toLong()
                } else currentPositionMs

                Text(
                    text = formatTime(currentMs),
                    color = FRTextMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = formatTime(durationMs),
                    color = FRTextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = onShuffleToggle,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) FRCharcoal else FRTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = FRTextPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause Primary Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(FRCharcoal)
                        .clickable(onClick = onPlayPause)
                        .testTag("player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (playbackState == PlaybackState.BUFFERING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (playbackState == PlaybackState.PLAYING) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (playbackState == PlaybackState.PLAYING) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                // Next
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = FRTextPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat Mode
                IconButton(
                    onClick = onRepeatToggle,
                    modifier = Modifier.size(48.dp)
                ) {
                    val repeatIcon = if (repeatMode == DomainRepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat
                    val repeatTint = if (repeatMode != DomainRepeatMode.OFF) FRCharcoal else FRTextMuted
                    Icon(
                        imageVector = repeatIcon,
                        contentDescription = "Repeat",
                        tint = repeatTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Volume Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                    contentDescription = "Volume Down",
                    tint = FRTextMuted,
                    modifier = Modifier.size(20.dp)
                )

                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = FRCharcoal,
                        activeTrackColor = FRCharcoal,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    )
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Volume Up",
                    tint = FRTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Actions: Queue Drawer Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = FRSurfaceVariant,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable(onClick = onOpenQueue)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Open Queue",
                            tint = FRCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Playing Queue",
                            color = FRTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
