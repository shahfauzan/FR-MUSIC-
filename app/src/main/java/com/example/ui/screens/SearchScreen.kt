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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
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
import com.example.ui.theme.FRCharcoal
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val selectedFilter by viewModel.selectedSearchFilter.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    val detectedYouTubeUrl by viewModel.detectedYouTubeUrl.collectAsState()
    val isResolvingYouTube by viewModel.isResolvingYouTube.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()

    val clipboard = LocalClipboardManager.current
    val currentTrack = playerUiState.currentTrack
    val isPlaying = playerUiState.playbackState == PlaybackState.PLAYING

    val quickFilters = listOf("All", "New Releases", "Hindi", "English", "Punjabi", "Bollywood", "Lo-fi", "Romantic")

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .testTag("search_screen")
    ) {
        // Search Header & Text Field
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = FRTextPrimary,
                        fontSize = 24.sp
                    )
                )

                Surface(
                    color = FRSurfaceVariant,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { viewModel.setShowYouTubeDialog(true) }
                        .testTag("search_youtube_link_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Paste YouTube Link",
                            tint = FRCharcoal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "YouTube Link",
                            color = FRCharcoal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        "Search songs, artists, or paste YouTube link...",
                        color = FRTextMuted,
                        fontSize = 13.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = FRCyan
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (query.isEmpty()) {
                            IconButton(
                                onClick = {
                                    val pasted = clipboard.getText()?.text.orEmpty().trim()
                                    if (pasted.isNotEmpty()) {
                                        viewModel.onSearchQueryChanged(pasted, immediate = true)
                                    } else {
                                        viewModel.setShowYouTubeDialog(true)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste Link or Query",
                                    tint = FRCharcoal
                                )
                            }
                        } else {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = FRTextMuted
                                )
                            }
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
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.selectSearchFilter(filter) }
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else FRTextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // YouTube Link Auto-Detection Card
        if (!detectedYouTubeUrl.isNullOrBlank()) {
            Surface(
                color = FRCharcoal,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YouTube Link Detected",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open in FR Music player & match background audio",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.openYouTubeLink(detectedYouTubeUrl!!) },
                        enabled = !isResolvingYouTube,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = FRCharcoal
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isResolvingYouTube) {
                            CircularProgressIndicator(
                                color = FRCharcoal,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Listen", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }
                    }
                }
            }
        }

        val hasResults = searchResults.tracks.isNotEmpty() ||
            searchResults.artists.isNotEmpty() ||
            searchResults.albums.isNotEmpty() ||
            searchResults.playlists.isNotEmpty()

        if (isSearching && !hasResults) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FRCyan)
            }
        } else if (query.isBlank() && !hasResults) {
            // Empty State: Search Ideas / Suggestions
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    SectionHeader(
                        title = "Popular Searches",
                        subtitle = "Trending artists and genres right now"
                    )
                }

                val suggestions = if (searchSuggestions.isNotEmpty()) {
                    searchSuggestions
                } else {
                    listOf(
                        "Arijit Singh",
                        "Diljit Dosanjh",
                        "The Weeknd",
                        "AP Dhillon",
                        "Kesariya",
                        "Blinding Lights",
                        "Lo-fi",
                        "Bollywood"
                    )
                }

                items(suggestions) { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onSearchQueryChanged(suggestion, immediate = true) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                            fontSize = 14.5.sp
                        )
                    }
                }
            }
        } else if (!hasResults && detectedYouTubeUrl == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
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
                        text = "Try searching another song, artist, Hindi, Punjabi, or paste a YouTube link.",
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
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Live Suggestions Chips while typing
                if (query.isNotBlank() && searchSuggestions.isNotEmpty() && detectedYouTubeUrl == null) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(searchSuggestions) { suggestion ->
                                Surface(
                                    color = FRSurfaceVariant,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.clickable {
                                        viewModel.onSearchQueryChanged(suggestion, immediate = true)
                                    }
                                ) {
                                    Text(
                                        text = suggestion,
                                        color = FRTextSecondary,
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

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
                        SectionHeader(
                            title = if (selectedFilter == "New Releases") "New Releases" else "Songs",
                            subtitle = "${searchResults.tracks.size} indexed tracks"
                        )
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

                // Playlists row if any
                if (searchResults.playlists.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        SectionHeader(title = "Curated Playlists")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(searchResults.playlists) { playlist ->
                                MusicCard(
                                    title = playlist.title,
                                    subtitle = "${playlist.trackCount} tracks",
                                    imageUrl = playlist.coverArtUrl,
                                    onClick = { viewModel.loadPlaylistDetail(playlist.id, isCustom = false) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
