package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Track
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRMagenta
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary
import com.example.ui.theme.FRViolet

@Composable
fun TrackRowItem(
    track: Track,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    index: Int? = null,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onAddToQueue: () -> Unit = {},
    onPlayNext: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("track_row_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (index != null) {
            Box(
                modifier = Modifier.width(28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    EqualizerAnimation()
                } else {
                    Text(
                        text = "$index",
                        color = FRTextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE2E8F0))
        ) {
            AsyncImage(
                model = track.albumArtUrl,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(46.dp)
            )

            if (isPlaying && index == null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    EqualizerAnimation()
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPlaying) FRCyan else FRTextPrimary,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = FRTextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (track.language.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${track.language}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FRTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Unlike" else "Like",
                tint = if (isFavorite) FRMagenta else FRTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = FRTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(FRSurfaceVariant)
            ) {
                DropdownMenuItem(
                    text = { Text("Add to Queue", color = FRTextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onAddToQueue()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Play Next", color = FRTextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onPlayNext()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to Playlist", color = FRTextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onAddToPlaylist()
                    }
                )
            }
        }
    }
}

@Composable
fun EqualizerAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "equalizer")
    val bar1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        modifier = modifier
            .width(16.dp)
            .height(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(bar1)
                .background(FRCyan, RoundedCornerShape(1.dp))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(bar2)
                .background(FRViolet, RoundedCornerShape(1.dp))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(bar3)
                .background(FRCyan, RoundedCornerShape(1.dp))
        )
    }
}
