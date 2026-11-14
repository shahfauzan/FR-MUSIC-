package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.PlaybackState
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.FullScreenPlayerSheet
import com.example.ui.components.LoginDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.QueueSheet
import com.example.ui.components.YouTubeLinkInputDialog
import com.example.ui.components.YouTubePlayerSheet
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
import com.example.ui.theme.FRSurface
import com.example.ui.theme.FRTextMuted

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
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

    val activeYouTubeLink by viewModel.activeYouTubeLink.collectAsState()
    val showYouTubeDialog by viewModel.showYouTubeDialog.collectAsState()
    val isResolvingYouTube by viewModel.isResolvingYouTube.collectAsState()
    val youTubeError by viewModel.youTubeError.collectAsState()

    val currentTrack = playerUiState.currentTrack
    val isFav = currentTrack != null && favoriteTracks.any { it.id == currentTrack.id }

    // Gracefully request POST_NOTIFICATIONS on Android 13+ when music starts playing for background controls
    var askedNotificationPermission by remember { mutableStateOf(false) }
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )
    LaunchedEffect(playerUiState.playbackState) {
        if (!askedNotificationPermission &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (playerUiState.playbackState == PlaybackState.PLAYING || playerUiState.playbackState == PlaybackState.BUFFERING)
        ) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                askedNotificationPermission = true
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Intercept back button when sub-screens, player, queue, or YouTube sheet are active
    BackHandler(
        enabled = activeYouTubeLink != null ||
            isPlayerExpanded ||
            isQueueOpen ||
            currentScreen !is ScreenDestination.Home
    ) {
        when {
            activeYouTubeLink != null -> viewModel.dismissYouTubePlayer()
            isQueueOpen -> viewModel.setQueueOpen(false)
            isPlayerExpanded -> viewModel.setPlayerExpanded(false)
            else -> viewModel.navigateBack()
        }
    }

    val isHome = currentScreen is ScreenDestination.Home
    val isSearch = currentScreen is ScreenDestination.Search
    val isDiscover = currentScreen is ScreenDestination.Discover
    val isLibrary = currentScreen is ScreenDestination.Library
    val isProfile = currentScreen is ScreenDestination.Profile

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(FRBackground)
    ) {
        val useNavigationRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail) {
                val railColors = NavigationRailItemDefaults.colors(
                    selectedIconColor = FRCharcoal,
                    selectedTextColor = FRCharcoal,
                    indicatorColor = Color(0xFFE2E8F0),
                    unselectedIconColor = FRTextMuted,
                    unselectedTextColor = FRTextMuted
                )
                NavigationRail(
                    containerColor = FRSurface,
                    modifier = Modifier
                        .fillMaxHeight()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .testTag("side_nav_rail")
                ) {
                    Spacer(modifier = Modifier.weight(0.15f))
                    NavigationRailItem(
                        selected = isHome,
                        onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                        icon = { Icon(if (isHome) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = railColors
                    )
                    NavigationRailItem(
                        selected = isSearch,
                        onClick = { viewModel.navigateTo(ScreenDestination.Search) },
                        icon = { Icon(if (isSearch) Icons.Filled.Search else Icons.Outlined.Search, contentDescription = "Search") },
                        label = { Text("Search", fontSize = 11.sp) },
                        colors = railColors
                    )
                    NavigationRailItem(
                        selected = isDiscover,
                        onClick = { viewModel.navigateTo(ScreenDestination.Discover) },
                        icon = { Icon(if (isDiscover) Icons.Filled.Explore else Icons.Outlined.Explore, contentDescription = "Discover") },
                        label = { Text("Discover", fontSize = 11.sp) },
                        colors = railColors
                    )
                    NavigationRailItem(
                        selected = isLibrary,
                        onClick = { viewModel.navigateTo(ScreenDestination.Library) },
                        icon = { Icon(if (isLibrary) Icons.Filled.QueueMusic else Icons.Outlined.QueueMusic, contentDescription = "Library") },
                        label = { Text("Library", fontSize = 11.sp) },
                        colors = railColors
                    )
                    NavigationRailItem(
                        selected = isProfile,
                        onClick = { viewModel.navigateTo(ScreenDestination.Profile) },
                        icon = { Icon(if (isProfile) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = railColors
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = FRBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .background(FRSurface)
                            .navigationBarsPadding()
                    ) {
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

                        if (!useNavigationRail) {
                            NavigationBar(
                                containerColor = FRSurface,
                                tonalElevation = 4.dp,
                                windowInsets = WindowInsets(0, 0, 0, 0),
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
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
                                    label = { Text("Home", fontSize = 11.sp, maxLines = 1) },
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
                                    label = { Text("Search", fontSize = 11.sp, maxLines = 1) },
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
                                    label = { Text("Discover", fontSize = 11.sp, maxLines = 1) },
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
                                    label = { Text("Library", fontSize = 11.sp, maxLines = 1) },
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
                                    label = { Text("Profile", fontSize = 11.sp, maxLines = 1) },
                                    colors = navColors,
                                    modifier = Modifier.testTag("nav_item_profile")
                                )
                            }
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

        // YouTube Link In-App Player Sheet
        AnimatedVisibility(
            visible = activeYouTubeLink != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            activeYouTubeLink?.let { ytLink ->
                YouTubePlayerSheet(
                    link = ytLink,
                    onDismiss = { viewModel.dismissYouTubePlayer() },
                    onPlayAudioInBackground = { viewModel.playYouTubeMatchInBackground(ytLink) }
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

        // Paste YouTube Link Dialog
        if (showYouTubeDialog) {
            YouTubeLinkInputDialog(
                isLoading = isResolvingYouTube,
                errorMessage = youTubeError,
                onDismiss = { viewModel.setShowYouTubeDialog(false) },
                onSubmitUrl = { url -> viewModel.openYouTubeLink(url) }
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
