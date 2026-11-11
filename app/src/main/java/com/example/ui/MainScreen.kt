package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.FullScreenPlayerSheet
import com.example.ui.components.LoginDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.QueueSheet
import com.example.ui.screens.AlbumDetailScreen
import com.example.ui.screens.ArtistDetailScreen
import com.example.ui.screens.CategoryDetailScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.FRBackground
import com.example.ui.theme.FRCharcoal
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRSurface
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val playerUiState by viewModel.playerUiState.collectAsState()
    val isPlayerExpanded by viewModel.isPlayerExpanded.collectAsState()
    val isQueueOpen by viewModel.isQueueOpen.collectAsState()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsState()
    val showLoginDialog by viewModel.showLoginDialog.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val trackToAddToPlaylist by viewModel.trackToAddToPlaylist.collectAsState()
    val userPlaylists by viewModel.userPlaylists.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()

    val currentTrack = playerUiState.currentTrack
    val isFav = currentTrack != null && favoriteTracks.any { it.id == currentTrack.id }

    // Intercept back button when sub-screens, player, or queue are active
    BackHandler(enabled = isPlayerExpanded || isQueueOpen || currentScreen !is ScreenDestination.Home) {
        when {
            isQueueOpen -> viewModel.setQueueOpen(false)
            isPlayerExpanded -> viewModel.setPlayerExpanded(false)
            else -> viewModel.navigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FRBackground)
    ) {
        Scaffold(
            containerColor = FRBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    // Persistent Mini Player above the bottom navigation bar
                    if (currentTrack != null) {
                        MiniPlayerBar(
                            track = currentTrack,
                            playbackState = playerUiState.playbackState,
                            currentPositionMs = playerUiState.currentPositionMs,
                            durationMs = playerUiState.durationMs,
                            isFavorite = isFav,
                            onBarClick = { viewModel.setPlayerExpanded(true) },
                            onPlayPauseClick = { viewModel.playerManager.playPause() },
                            onNextClick = { viewModel.playerManager.next() },
                            onFavoriteToggle = { viewModel.toggleFavorite(currentTrack) }
                        )
                    }

                    // Main Bottom Navigation Bar
                    NavigationBar(
                        containerColor = FRSurface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        val isHome = currentScreen is ScreenDestination.Home
                        val isSearch = currentScreen is ScreenDestination.Search
                        val isDiscover = currentScreen is ScreenDestination.Discover
                        val isLibrary = currentScreen is ScreenDestination.Library
                        val isProfile = currentScreen is ScreenDestination.Profile

                        val navColors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FRCharcoal,
                            selectedTextColor = FRCharcoal,
                            indicatorColor = Color(0xFFE2E8F0),
                            unselectedIconColor = FRTextMuted,
                            unselectedTextColor = FRTextMuted
                        )

                        NavigationBarItem(
                            selected = isHome,
                            onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                            icon = {
                                Icon(
                                    imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_item_home")
                        )

                        NavigationBarItem(
                            selected = isSearch,
                            onClick = { viewModel.navigateTo(ScreenDestination.Search) },
                            icon = {
                                Icon(
                                    imageVector = if (isSearch) Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = "Search"
                                )
                            },
                            label = { Text("Search", fontSize = 11.sp) },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_item_search")
                        )

                        NavigationBarItem(
                            selected = isDiscover,
                            onClick = { viewModel.navigateTo(ScreenDestination.Discover) },
                            icon = {
                                Icon(
                                    imageVector = if (isDiscover) Icons.Filled.Explore else Icons.Outlined.Explore,
                                    contentDescription = "Discover"
                                )
                            },
                            label = { Text("Discover", fontSize = 11.sp) },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_item_discover")
                        )

                        NavigationBarItem(
                            selected = isLibrary,
                            onClick = { viewModel.navigateTo(ScreenDestination.Library) },
                            icon = {
                                Icon(
                                    imageVector = if (isLibrary) Icons.Filled.QueueMusic else Icons.Outlined.QueueMusic,
                                    contentDescription = "Library"
                                )
                            },
                            label = { Text("Library", fontSize = 11.sp) },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_item_library")
                        )

                        NavigationBarItem(
                            selected = isProfile,
                            onClick = { viewModel.navigateTo(ScreenDestination.Profile) },
                            icon = {
                                Icon(
                                    imageVector = if (isProfile) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontSize = 11.sp) },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_item_profile")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val dest = currentScreen) {
                    is ScreenDestination.Home -> HomeScreen(viewModel)
                    is ScreenDestination.Search -> SearchScreen(viewModel)
                    is ScreenDestination.Discover -> DiscoverScreen(viewModel)
                    is ScreenDestination.Library -> LibraryScreen(viewModel)
                    is ScreenDestination.Profile -> ProfileScreen(viewModel)
                    is ScreenDestination.ArtistDetail -> ArtistDetailScreen(viewModel)
                    is ScreenDestination.AlbumDetail -> AlbumDetailScreen(viewModel)
                    is ScreenDestination.PlaylistDetail -> PlaylistDetailScreen(viewModel, dest.isCustom)
                    is ScreenDestination.CategoryDetail -> CategoryDetailScreen(dest.categoryName, viewModel)
                }
            }
        }

        // Full Screen Player Animated Sheet
        AnimatedVisibility(
            visible = isPlayerExpanded && currentTrack != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            if (currentTrack != null) {
                FullScreenPlayerSheet(
                    track = currentTrack,
                    playbackState = playerUiState.playbackState,
                    currentPositionMs = playerUiState.currentPositionMs,
                    durationMs = playerUiState.durationMs,
                    isShuffle = playerUiState.isShuffle,
                    repeatMode = playerUiState.repeatMode,
                    volume = playerUiState.volume,
                    isFavorite = isFav,
                    errorMessage = playerUiState.errorMessage,
                    onDismiss = { viewModel.setPlayerExpanded(false) },
                    onPlayPause = { viewModel.playerManager.playPause() },
                    onNext = { viewModel.playerManager.next() },
                    onPrevious = { viewModel.playerManager.previous() },
                    onSeek = { viewModel.playerManager.seekTo(it) },
                    onShuffleToggle = { viewModel.playerManager.toggleShuffle() },
                    onRepeatToggle = { viewModel.playerManager.toggleRepeat() },
                    onVolumeChange = { viewModel.playerManager.setVolume(it) },
                    onFavoriteToggle = { viewModel.toggleFavorite(currentTrack) },
                    onOpenQueue = { viewModel.setQueueOpen(true) }
                )
            }
        }

        // Queue Sheet
        AnimatedVisibility(
            visible = isQueueOpen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            QueueSheet(
                queue = playerUiState.queue,
                currentTrackIndex = playerUiState.currentQueueIndex,
                onDismiss = { viewModel.setQueueOpen(false) },
                onTrackSelect = { viewModel.playTrack(it, playerUiState.queue) },
                onRemoveFromQueue = { viewModel.playerManager.removeFromQueue(it) },
                onClearQueue = { viewModel.playerManager.clearQueue() },
                onShuffleQueue = { viewModel.playerManager.shuffleQueue() }
            )
        }

        // Create Playlist Dialog
        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { viewModel.setShowCreatePlaylistDialog(false) },
                onConfirm = { title, desc -> viewModel.createPlaylist(title, desc) }
            )
        }

        // Add to Playlist Dialog
        if (trackToAddToPlaylist != null) {
            AddToPlaylistDialog(
                track = trackToAddToPlaylist!!,
                playlists = userPlaylists,
                onDismiss = { viewModel.setTrackToAddToPlaylist(null) },
                onPlaylistSelected = { pl -> viewModel.addTrackToPlaylist(pl.id, trackToAddToPlaylist!!) },
                onCreateNewPlaylist = {
                    viewModel.setShowCreatePlaylistDialog(true)
                }
            )
        }

        // Temporary Local Login Dialog
        if (showLoginDialog) {
            LoginDialog(
                onDismiss = { viewModel.setShowLoginDialog(false) },
                onSaveProfile = { name, email, langs ->
                    viewModel.saveUserProfile(name, email, langs)
                },
                initialName = userProfile?.name ?: "Fauzan Shah",
                initialEmail = userProfile?.email ?: "shahfauzan49@gmail.com",
                initialLanguages = userProfile?.preferredLanguages ?: "Hindi, English, Punjabi"
            )
        }
    }
}
