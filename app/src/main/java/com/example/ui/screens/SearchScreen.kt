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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PlaybackState
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
fun SearchScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()

    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING

    var selectedFilter by remember { mutableStateOf("All") }
    val quickFilters = listOf("All", "New Releases", "Hindi", "English", "Punjabi", "Bollywood", "Lo-fi", "Romantic")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Search Header & Text Field
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Search",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = FRTextPrimary,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search songs, artists, Hindi, Punjabi...", color = FRTextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = FRCyan
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = FRTextMuted
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = FRTextPrimary,
                    unfocusedTextColor = FRTextPrimary,
                    focusedContainerColor = FRSurfaceVariant,
                    unfocusedContainerColor = FRSurfaceVariant,
                    focusedBorderColor = FRCyan,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickFilters) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        color = if (isSelected) FRCyan else FRSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable {
                            selectedFilter = filter
                            if (filter != "All") {
                                viewModel.onSearchQueryChanged(filter)
                            }
                        }
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else FRTextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FRCyan)
            }
        } else if (query.isBlank()) {
            // Empty State: Search Ideas / Suggestions
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    SectionHeader(
                        title = "Popular Searches",
                        subtitle = "Trending artists and genres right now"
                    )
                }

                val suggestions = listOf(
                    "Arijit Singh",
                    "Diljit Dosanjh",
                    "The Weeknd",
                    "AP Dhillon",
                    "Kesariya",
                    "Blinding Lights",
                    "Lo-fi Chill Hindi",
                    "Bollywood Classics"
                )

                items(suggestions) { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onSearchQueryChanged(suggestion) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = FRTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = suggestion,
                            color = FRTextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else if (searchResults.tracks.isEmpty() && searchResults.artists.isEmpty() && searchResults.albums.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp, start = 24.dp, end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No results found for \"$query\"",
                        color = FRTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Please check the spelling or search for another song, artist, Hindi, or English genre.",
                        color = FRTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Search Results
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Artists row if any
                if (searchResults.artists.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Artists")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(searchResults.artists) { artist ->
                                Column(
                                    modifier = Modifier
                                        .width(96.dp)
                                        .clickable { viewModel.loadArtistDetail(artist.id) },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE2E8F0))
                                    ) {
                                        AsyncImage(
                                            model = artist.imageUrl,
                                            contentDescription = artist.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = artist.name,
                                        color = FRTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Artist",
                                        color = FRTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Tracks List
                if (searchResults.tracks.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Songs")
                    }

                    items(searchResults.tracks) { track ->
                        val isTrackCurrent = currentTrack?.id == track.id
                        val isFav = favoriteTracks.any { it.id == track.id }
                        TrackRowItem(
                            track = track,
                            isPlaying = isTrackCurrent && isPlaying,
                            isFavorite = isFav,
                            onClick = { viewModel.playTrack(track, searchResults.tracks) },
                            onFavoriteToggle = { viewModel.toggleFavorite(track) },
                            onAddToQueue = { viewModel.playerManager.addToQueue(track) },
                            onPlayNext = { viewModel.playerManager.playNext(track) },
                            onAddToPlaylist = { viewModel.setTrackToAddToPlaylist(track) }
                        )
                    }
                }

                // Albums row if any
                if (searchResults.albums.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        SectionHeader(title = "Albums & Singles")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(searchResults.albums) { album ->
                                MusicCard(
                                    title = album.title,
                                    subtitle = album.artist,
                                    imageUrl = album.coverArtUrl,
                                    onClick = { viewModel.loadAlbumDetail(album.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
