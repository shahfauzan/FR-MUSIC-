package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Category
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track
import com.example.data.remote.ProviderType
import com.example.data.repository.MusicRepository
import com.example.player.AudioPlayerManager
import com.example.player.PlayerUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class ScreenDestination {
    data object Home : ScreenDestination()
    data object Search : ScreenDestination()
    data object Discover : ScreenDestination()
    data object Library : ScreenDestination()
    data object Profile : ScreenDestination()
    data class ArtistDetail(val artistId: String) : ScreenDestination()
    data class AlbumDetail(val albumId: String) : ScreenDestination()
    data class PlaylistDetail(val playlistId: String, val isCustom: Boolean = false) : ScreenDestination()
    data class CategoryDetail(val categoryName: String) : ScreenDestination()
}

data class HomeDataState(
    val isLoading: Boolean = true,
    val trendingTracks: List<Track> = emptyList(),
    val hindiHits: List<Track> = emptyList(),
    val englishHits: List<Track> = emptyList(),
    val bollywoodEssentials: List<Track> = emptyList(),
    val punjabiVibes: List<Track> = emptyList(),
    val recommendations: List<Track> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = MusicRepository(database.musicDao())
    val playerManager = AudioPlayerManager(application)

    val playerUiState: StateFlow<PlayerUiState> = playerManager.uiState

    // Navigation Backstack State
    private val _navigationStack = MutableStateFlow<List<ScreenDestination>>(listOf(ScreenDestination.Home))
    val currentScreen: StateFlow<ScreenDestination> = _navigationStack.combine(_navigationStack) { stack, _ ->
        stack.lastOrNull() ?: ScreenDestination.Home
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ScreenDestination.Home)

    // Full screen player visibility
    private val _isPlayerExpanded = MutableStateFlow(false)
    val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

    // Queue sheet visibility
    private val _isQueueOpen = MutableStateFlow(false)
    val isQueueOpen: StateFlow<Boolean> = _isQueueOpen.asStateFlow()

    // User Profile & Temporary Login State
    val userProfile: StateFlow<UserProfileEntity?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    // Playlist Dialog State
    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

    private val _trackToAddToPlaylist = MutableStateFlow<Track?>(null)
    val trackToAddToPlaylist: StateFlow<Track?> = _trackToAddToPlaylist.asStateFlow()

    // Home Data State
    private val _homeState = MutableStateFlow(HomeDataState())
    val homeState: StateFlow<HomeDataState> = _homeState.asStateFlow()

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow(SearchResults())
    val searchResults: StateFlow<SearchResults> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Room Persistent Flows
    val favoriteTracks: StateFlow<List<Track>> = repository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPlaylists: StateFlow<List<Playlist>> = repository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayed: StateFlow<List<Track>> = repository.getRecentlyPlayed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Detail Objects
    private val _activeArtist = MutableStateFlow<Artist?>(null)
    val activeArtist: StateFlow<Artist?> = _activeArtist.asStateFlow()

    private val _activeAlbum = MutableStateFlow<Album?>(null)
    val activeAlbum: StateFlow<Album?> = _activeAlbum.asStateFlow()

    private val _activePlaylist = MutableStateFlow<Playlist?>(null)
    val activePlaylist: StateFlow<Playlist?> = _activePlaylist.asStateFlow()

    private val _categoryTracks = MutableStateFlow<List<Track>>(emptyList())
    val categoryTracks: StateFlow<List<Track>> = _categoryTracks.asStateFlow()

    init {
        loadHomeData()
    }

    fun getGreeting(userName: String = "Fauzan"): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeGreeting = when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        return "$timeGreeting, $userName"
    }

    fun navigateTo(destination: ScreenDestination) {
        val currentStack = _navigationStack.value
        if (currentStack.lastOrNull() != destination) {
            _navigationStack.value = currentStack + destination
        }
    }

    fun navigateBack(): Boolean {
        val currentStack = _navigationStack.value
        return if (currentStack.size > 1) {
            _navigationStack.value = currentStack.dropLast(1)
            true
        } else {
            false
        }
    }

    fun setPlayerExpanded(expanded: Boolean) {
        _isPlayerExpanded.value = expanded
    }

    fun setQueueOpen(open: Boolean) {
        _isQueueOpen.value = open
    }

    fun setShowCreatePlaylistDialog(show: Boolean) {
        _showCreatePlaylistDialog.value = show
    }

    fun setShowLoginDialog(show: Boolean) {
        _showLoginDialog.value = show
    }

    fun saveUserProfile(name: String, email: String, languages: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUserProfile(name, email, languages)
            _showLoginDialog.value = false
        }
    }

    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logoutUser()
            _showLoginDialog.value = true
        }
    }

    fun setTrackToAddToPlaylist(track: Track?) {
        _trackToAddToPlaylist.value = track
    }

    fun loadHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            _homeState.value = _homeState.value.copy(isLoading = true)
            try {
                val trending = repository.getTrending()
                val hindi = repository.getHindiHits()
                val english = repository.getEnglishHits()
                val bollywood = repository.getBollywoodEssentials()
                val punjabi = repository.getPunjabiVibes()
                val recommendations = repository.getRecommendations()

                _homeState.value = HomeDataState(
                    isLoading = false,
                    trendingTracks = trending,
                    hindiHits = hindi,
                    englishHits = english,
                    bollywoodEssentials = bollywood,
                    punjabiVibes = punjabi,
                    recommendations = recommendations
                )
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(isLoading = false)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = SearchResults()
            _isSearching.value = false
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSearching.value = true
            try {
                val results = repository.search(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = SearchResults()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun playTrack(track: Track, queue: List<Track>? = null) {
        playerManager.playTrack(track, queue)
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordRecentlyPlayed(track)
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(track)
        }
    }

    fun createPlaylist(title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.createPlaylist(title, description)
            _showCreatePlaylistDialog.value = false
            _trackToAddToPlaylist.value?.let { track ->
                repository.addTrackToPlaylist(id, track)
                _trackToAddToPlaylist.value = null
            }
        }
    }

    fun addTrackToPlaylist(playlistId: String, track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTrackToPlaylist(playlistId, track)
            _trackToAddToPlaylist.value = null
        }
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeTrackFromPlaylist(playlistId, trackId)
            loadPlaylistDetail(playlistId, isCustom = true)
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlaylist(playlistId)
            navigateBack()
        }
    }

    fun setProviderType(type: ProviderType) {
        repository.setProviderType(type)
        loadHomeData()
    }

    fun loadArtistDetail(artistId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _activeArtist.value = repository.getArtist(artistId)
            navigateTo(ScreenDestination.ArtistDetail(artistId))
        }
    }

    fun loadAlbumDetail(albumId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _activeAlbum.value = repository.getAlbum(albumId)
            navigateTo(ScreenDestination.AlbumDetail(albumId))
        }
    }

    fun loadPlaylistDetail(playlistId: String, isCustom: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isCustom) {
                val dbPlaylists = userPlaylists.value
                val customPl = dbPlaylists.find { it.id == playlistId }
                if (customPl != null) {
                    val tracksFlow = repository.getPlaylistTracks(playlistId)
                    tracksFlow.collect { tracks ->
                        _activePlaylist.value = customPl.copy(tracks = tracks, trackCount = tracks.size)
                    }
                }
            } else {
                _activePlaylist.value = repository.getPlaylist(playlistId)
            }
            navigateTo(ScreenDestination.PlaylistDetail(playlistId, isCustom))
        }
    }

    fun loadCategoryTracks(categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tracks = repository.getTracksByGenre(categoryName)
            _categoryTracks.value = tracks
            navigateTo(ScreenDestination.CategoryDetail(categoryName))
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.releasePlayer()
    }
}
