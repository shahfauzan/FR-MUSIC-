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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Track
import com.example.ui.MainViewModel
import com.example.ui.components.MusicCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary
import com.example.ui.theme.FRViolet

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.homeState.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == com.example.data.model.PlaybackState.PLAYING
    val userName = userProfile?.name?.ifBlank { "Fauzan" } ?: "Fauzan"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // App Bar & Greeting
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE2E8F0))
                            ) {
                                AsyncImage(
                                    model = com.example.R.drawable.fr_music_logo,
                                    contentDescription = "FR Music Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FR MUSIC",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FRCyan,
                                    letterSpacing = 2.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.getGreeting(userName),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FRTextPrimary,
                                fontSize = 22.sp
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                                .clickable { viewModel.setShowLoginDialog(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = FRTextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your music. Your mood.",
                    color = FRTextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Quick Access 2-Column Grid (6 items)
        item {
            val quickItems = homeState.trendingTracks.take(6)
            if (quickItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0 until quickItems.size step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val track1 = quickItems[i]
                            QuickAccessCard(
                                track = track1,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.playTrack(track1, homeState.trendingTracks) }
                            )

                            if (i + 1 < quickItems.size) {
                                val track2 = quickItems[i + 1]
                                QuickAccessCard(
                                    track = track2,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.playTrack(track2, homeState.trendingTracks) }
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (homeState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FRCyan)
                }
            }
        } else {
            // Trending Now
            item {
                SectionHeader(
                    title = "Trending Now",
                    subtitle = "Global & Indian chart-toppers"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeState.trendingTracks) { track ->
                        MusicCard(
                            title = track.title,
                            subtitle = track.artist,
                            imageUrl = track.albumArtUrl,
                            badgeText = track.language,
                            onClick = { viewModel.playTrack(track, homeState.trendingTracks) },
                            onPlayClick = { viewModel.playTrack(track, homeState.trendingTracks) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // New Releases
            item {
                SectionHeader(
                    title = "New Releases",
                    subtitle = "Fresh singles & albums out this week",
                    actionText = "Explore",
                    onActionClick = { viewModel.loadCategoryTracks("Trending") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val newReleases = (homeState.trendingTracks.take(4) + homeState.punjabiVibes.take(2) + homeState.englishHits.take(2))
                    items(newReleases) { track ->
                        MusicCard(
                            title = track.title,
                            subtitle = track.artist,
                            imageUrl = track.albumArtUrl,
                            badgeText = "NEW",
                            onClick = { viewModel.playTrack(track, newReleases) },
                            onPlayClick = { viewModel.playTrack(track, newReleases) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Hindi Hits
            item {
                SectionHeader(
                    title = "Hindi Hits",
                    subtitle = "Arijit Singh, Pritam & Top Bollywood",
                    actionText = "See all",
                    onActionClick = { viewModel.loadCategoryTracks("Hindi") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeState.hindiHits) { track ->
                        MusicCard(
                            title = track.title,
                            subtitle = track.artist,
                            imageUrl = track.albumArtUrl,
                            badgeText = "Hindi",
                            onClick = { viewModel.playTrack(track, homeState.hindiHits) },
                            onPlayClick = { viewModel.playTrack(track, homeState.hindiHits) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Punjabi Vibes
            item {
                SectionHeader(
                    title = "Punjabi Vibes",
                    subtitle = "Diljit Dosanjh, AP Dhillon, Karan Aujla",
                    actionText = "Explore",
                    onActionClick = { viewModel.loadCategoryTracks("Punjabi") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeState.punjabiVibes) { track ->
                        MusicCard(
                            title = track.title,
                            subtitle = track.artist,
                            imageUrl = track.albumArtUrl,
                            badgeText = "Punjabi",
                            onClick = { viewModel.playTrack(track, homeState.punjabiVibes) },
                            onPlayClick = { viewModel.playTrack(track, homeState.punjabiVibes) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // English Hits
            item {
                SectionHeader(
                    title = "English Hits",
                    subtitle = "The Weeknd, Pop & Billboard Top 100",
                    actionText = "See all",
                    onActionClick = { viewModel.loadCategoryTracks("English") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeState.englishHits) { track ->
                        MusicCard(
                            title = track.title,
                            subtitle = track.artist,
                            imageUrl = track.albumArtUrl,
                            badgeText = "English",
                            onClick = { viewModel.playTrack(track, homeState.englishHits) },
                            onPlayClick = { viewModel.playTrack(track, homeState.englishHits) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bollywood Essentials
            item {
                SectionHeader(
                    title = "Bollywood Essentials",
                    subtitle = "Iconic soundtracks and timeless romantic melodies"
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    homeState.bollywoodEssentials.take(4).forEachIndexed { idx, track ->
                        val isTrackCurrent = currentTrack?.id == track.id
                        val isFav = favoriteTracks.any { it.id == track.id }
                        TrackRowItem(
                            track = track,
                            index = idx + 1,
                            isPlaying = isTrackCurrent && isPlaying,
                            isFavorite = isFav,
                            onClick = { viewModel.playTrack(track, homeState.bollywoodEssentials) },
                            onFavoriteToggle = { viewModel.toggleFavorite(track) },
                            onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                            onPlayNext = { viewModel.playerManager.playNext(track) },
                            onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Made For You
            item {
                SectionHeader(
                    title = "Made For You",
                    subtitle = "Personalized mix based on your listening mood"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeState.recommendations) { track ->
                        MusicCard(
                            title = track.title,
                            subtitle = track.artist,
                            imageUrl = track.albumArtUrl,
                            badgeText = "Recommended",
                            onClick = { viewModel.playTrack(track, homeState.recommendations) },
                            onPlayClick = { viewModel.playTrack(track, homeState.recommendations) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recently Played
            if (recentlyPlayed.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Recently Played",
                        subtitle = "Jump back into what you were listening to"
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        recentlyPlayed.take(4).forEach { track ->
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
fun QuickAccessCard(
    track: Track,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = FRSurfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                AsyncImage(
                    model = track.albumArtUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = track.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = FRTextPrimary,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(FRCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
