package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PlaybackState
import com.example.data.model.Playlist
import com.example.ui.MainViewModel
import com.example.ui.components.TrackRowItem
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
fun LibraryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()
    val userPlaylists by viewModel.userPlaylists.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()

    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Playlists", "Liked Songs", "Recently Played")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = FRTextPrimary,
                    fontSize = 24.sp
                )
            )

            Button(
                onClick = { viewModel.setShowCreatePlaylistDialog(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FRSurfaceVariant,
                    contentColor = FRCyan
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("create_playlist_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Playlist", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = FRBackground,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = FRCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tabTitle,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) FRCyan else FRTextMuted,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Playlists Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pre-pinned Liked Songs card
                    item {
                        LikedSongsCard(
                            count = favoriteTracks.size,
                            onClick = { selectedTabIndex = 1 }
                        )
                    }

                    if (userPlaylists.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No custom playlists yet",
                                        color = FRTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tap 'New Playlist' above to create one (e.g. Late Night Coding, Chill Hindi)",
                                        color = FRTextSecondary,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(userPlaylists) { pl ->
                            PlaylistRowItem(
                                playlist = pl,
                                onClick = { viewModel.loadPlaylistDetail(pl.id, isCustom = true) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Liked Songs Tab
                if (favoriteTracks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = FRMagenta.copy(alpha = 0.5f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Songs you like will appear here",
                                color = FRTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the heart icon on any track while browsing or playing to add it to your favorites.",
                                color = FRTextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
                    ) {
                        items(favoriteTracks) { track ->
                            val isTrackCurrent = currentTrack?.id == track.id
                            TrackRowItem(
                                track = track,
                                isPlaying = isTrackCurrent && isPlaying,
                                isFavorite = true,
                                onClick = { viewModel.playTrack(track, favoriteTracks) },
                                onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                                onPlayNext = { viewModel.playerManager.playNext(track) },
                                onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Recently Played Tab
                if (recentlyPlayed.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recently played tracks.",
                            color = FRTextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
                    ) {
                        items(recentlyPlayed) { track ->
                            val isTrackCurrent = currentTrack?.id == track.id
                            val isFav = favoriteTracks.any { it.id == track.id }
                            TrackRowItem(
                                track = track,
                                isPlaying = isTrackCurrent && isPlaying,
                                isFavorite = isFav,
                                onClick = { viewModel.playTrack(track, recentlyPlayed) },
                                onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                                onPlayNext = { viewModel.playerManager.playNext(track) },
                                onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LikedSongsCard(
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FRSurfaceVariant)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFE11D48), Color(0xFFBE123C))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Liked Songs",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = FRTextPrimary,
                    fontSize = 16.sp
                )
            )

            Text(
                text = "$count tracks",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = FRTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun PlaylistRowItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(FRSurfaceVariant)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QueueMusic,
                contentDescription = null,
                tint = FRCharcoal,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = FRTextPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = if (playlist.description.isNotBlank()) playlist.description else "Custom Playlist",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = FRTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    }
}
