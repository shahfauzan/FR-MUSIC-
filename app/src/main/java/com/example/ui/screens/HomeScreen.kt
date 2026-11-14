package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PlaybackState
import com.example.data.model.Track
import com.example.ui.MainViewModel
import com.example.ui.components.AmbientArtworkBackdrop
import com.example.ui.components.MusicCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.FRCharcoal
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary

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
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING
    val userName = userProfile?.name?.ifBlank { "Fauzan" } ?: "Fauzan"

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val quickColumns = if (maxWidth >= 600.dp) 3 else 2

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .testTag("home_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // App Bar & Greeting
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
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

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = viewModel.getGreeting(userName),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FRTextPrimary,
                                    fontSize = 22.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                                .clickable { viewModel.setShowLoginDialog(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = FRTextPrimary,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your music. Your mood.",
                        color = FRTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            // Active Song Atmospheric Banner when a track is loaded
            if (currentTrack != null) {
                item {
                    NowPlayingAtmosphereBanner(
                        track = currentTrack,
                        isPlaying = isPlaying,
                        onBannerClick = { viewModel.setPlayerExpanded(true) },
                        onPlayPauseClick = { viewModel.playerManager.playPause() },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Paste YouTube Link Action Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FRSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.setShowYouTubeDialog(true) }
                        .testTag("home_youtube_link_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FRCharcoal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Paste YouTube Link",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Listen from YouTube Link",
                                fontWeight = FontWeight.Bold,
                                color = FRTextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Paste any YouTube or YT Music URL to play here",
                                color = FRTextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            color = FRCharcoal,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Paste Link",
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Quick Access Responsive Grid (2 columns on compact, 3 columns on wide)
            item {
                val quickItems = homeState.trendingTracks.take(6)
                if (quickItems.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in quickItems.indices step quickColumns) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0 until quickColumns) {
                                    val idx = i + col
                                    if (idx < quickItems.size) {
                                        val track = quickItems[idx]
                                        QuickAccessCard(
                                            track = track,
                                            modifier = Modifier.weight(1f),
                                            onClick = { viewModel.playTrack(track, homeState.trendingTracks) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
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
}

@Composable
fun NowPlayingAtmosphereBanner(
    track: Track,
    isPlaying: Boolean,
    onBannerClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onBannerClick)
            .testTag("home_now_playing_banner")
    ) {
        AmbientArtworkBackdrop(
            imageUrl = track.albumArtUrl,
            isPlaying = isPlaying,
            modifier = Modifier.matchParentSize(),
            intensity = 0.40f
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.albumArtUrl,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE2E8F0))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = FRCharcoal,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPlaying) "PLAYING IN BACKGROUND" else "PAUSED",
                        color = FRCharcoal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = track.title,
                    color = FRTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    color = FRTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(FRCharcoal)
                    .clickable(onClick = onPlayPauseClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
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
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = FRSurfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
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
                    .background(FRCharcoal),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
