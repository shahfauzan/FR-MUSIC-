package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.PlaybackState
import com.example.ui.MainViewModel
import com.example.ui.components.MusicCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.FRBackground
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary
import com.example.ui.theme.FRViolet

@Composable
fun ArtistDetailScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val artist by viewModel.activeArtist.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()

    BackHandler { viewModel.navigateBack() }

    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING

    if (artist == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Artist not found", color = FRTextMuted)
        }
        return
    }

    val art = artist!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("artist_detail_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = art.imageUrl,
                    contentDescription = art.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0x99000000),
                                    FRBackground
                                )
                            )
                        )
                )

                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = FRCyan,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "VERIFIED ARTIST",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = art.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 28.sp
                        )
                    )

                    Text(
                        text = "${art.monthlyListeners} monthly listeners • ${art.genre}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.5.sp
                    )
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (art.topTracks.isNotEmpty()) {
                            viewModel.playTrack(art.topTracks.first(), art.topTracks)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FRCyan,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = {
                        // Toggle follow
                    },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("Follow", color = FRTextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Top Songs Section
        item {
            SectionHeader(title = "Popular Tracks")
        }

        itemsIndexed(art.topTracks) { idx, track ->
            val isTrackCurrent = currentTrack?.id == track.id
            val isFav = favoriteTracks.any { it.id == track.id }
            TrackRowItem(
                track = track,
                index = idx + 1,
                isPlaying = isTrackCurrent && isPlaying,
                isFavorite = isFav,
                onClick = { viewModel.playTrack(track, art.topTracks) },
                onFavoriteToggle = { viewModel.toggleFavorite(track) },
                onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                onPlayNext = { viewModel.playerManager.playNext(track) },
                onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
            )
        }

        // Albums Section if any
        if (art.albums.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Discography")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(art.albums) { alb ->
                        MusicCard(
                            title = alb.title,
                            subtitle = "${alb.releaseYear} • Album",
                            imageUrl = alb.coverArtUrl,
                            onClick = { viewModel.loadAlbumDetail(alb.id) }
                        )
                    }
                }
            }
        }

        // Bio Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = FRTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = FRSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = art.bio,
                        color = FRTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumDetailScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val album by viewModel.activeAlbum.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()

    BackHandler { viewModel.navigateBack() }

    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING

    if (album == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Album not found", color = FRTextMuted)
        }
        return
    }

    val alb = album!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("album_detail_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FRTextPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    AsyncImage(
                        model = alb.coverArtUrl,
                        contentDescription = alb.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = alb.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = FRTextPrimary,
                        fontSize = 20.sp
                    )
                )

                Text(
                    text = "${alb.artist} • ${alb.releaseYear} • ${alb.genre}",
                    color = FRTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (alb.tracks.isNotEmpty()) {
                                viewModel.playTrack(alb.tracks.first(), alb.tracks)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FRCyan,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play All", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            if (alb.tracks.isNotEmpty()) {
                                viewModel.playTrack(alb.tracks.shuffled().first(), alb.tracks.shuffled())
                            }
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = null,
                            tint = FRCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Shuffle", color = FRTextPrimary)
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Tracklist (${alb.tracks.size} tracks)")
        }

        itemsIndexed(alb.tracks) { idx, track ->
            val isTrackCurrent = currentTrack?.id == track.id
            val isFav = favoriteTracks.any { it.id == track.id }
            TrackRowItem(
                track = track,
                index = idx + 1,
                isPlaying = isTrackCurrent && isPlaying,
                isFavorite = isFav,
                onClick = { viewModel.playTrack(track, alb.tracks) },
                onFavoriteToggle = { viewModel.toggleFavorite(track) },
                onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                onPlayNext = { viewModel.playerManager.playNext(track) },
                onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
            )
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    viewModel: MainViewModel,
    isCustom: Boolean,
    modifier: Modifier = Modifier
) {
    val playlist by viewModel.activePlaylist.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()

    BackHandler { viewModel.navigateBack() }

    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING

    if (playlist == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Playlist not found", color = FRTextMuted)
        }
        return
    }

    val pl = playlist!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("playlist_detail_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FRTextPrimary
                        )
                    }

                    if (isCustom) {
                        IconButton(onClick = { viewModel.deletePlaylist(pl.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Playlist",
                                tint = Color(0xFFE11D48)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    AsyncImage(
                        model = pl.coverArtUrl,
                        contentDescription = pl.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = pl.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = FRTextPrimary,
                        fontSize = 20.sp
                    )
                )

                if (pl.description.isNotBlank()) {
                    Text(
                        text = pl.description,
                        color = FRTextSecondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (pl.tracks.isNotEmpty()) {
                            viewModel.playTrack(pl.tracks.first(), pl.tracks)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FRCyan,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play All (${pl.tracks.size})", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (pl.tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "This playlist has no songs yet.\nAdd songs using the '...' menu on any track.",
                        color = FRTextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            itemsIndexed(pl.tracks) { idx, track ->
                val isTrackCurrent = currentTrack?.id == track.id
                val isFav = favoriteTracks.any { it.id == track.id }
                TrackRowItem(
                    track = track,
                    index = idx + 1,
                    isPlaying = isTrackCurrent && isPlaying,
                    isFavorite = isFav,
                    onClick = { viewModel.playTrack(track, pl.tracks) },
                    onFavoriteToggle = { viewModel.toggleFavorite(track) },
                    onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                    onPlayNext = { viewModel.playerManager.playNext(track) },
                    onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
                )
            }
        }
    }
}

@Composable
fun CategoryDetailScreen(
    categoryName: String,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.categoryTracks.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()

    BackHandler { viewModel.navigateBack() }

    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("category_detail_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = FRTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FRTextPrimary,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "${tracks.size} songs curated for you",
                        color = FRTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        itemsIndexed(tracks) { idx, track ->
            val isTrackCurrent = currentTrack?.id == track.id
            val isFav = favoriteTracks.any { it.id == track.id }
            TrackRowItem(
                track = track,
                index = idx + 1,
                isPlaying = isTrackCurrent && isPlaying,
                isFavorite = isFav,
                onClick = { viewModel.playTrack(track, tracks) },
                onFavoriteToggle = { viewModel.toggleFavorite(track) },
                onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                onPlayNext = { viewModel.playerManager.playNext(track) },
                onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
            )
        }
    }
}
