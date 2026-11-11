package com.example.data.repository

import com.example.data.local.dao.MusicDao
import com.example.data.local.entity.FavoriteTrackEntity
import com.example.data.local.entity.FollowedArtistEntity
import com.example.data.local.entity.PlaylistEntity
import com.example.data.local.entity.PlaylistTrackEntity
import com.example.data.local.entity.RecentlyPlayedEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track
import com.example.data.remote.MusicProvider
import com.example.data.remote.MusicProviderFactory
import com.example.data.remote.ProviderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class MusicRepository(
    private val musicDao: MusicDao,
    private val providerFactory: MusicProviderFactory = MusicProviderFactory()
) {
    private val _currentProviderType = MutableStateFlow(ProviderType.YOUTUBE_DATA_API)
    val currentProviderType: StateFlow<ProviderType> = _currentProviderType.asStateFlow()

    private val currentProvider: MusicProvider
        get() = providerFactory.getProvider(_currentProviderType.value)

    fun setProviderType(type: ProviderType) {
        _currentProviderType.value = type
    }

    val providerName: String
        get() = currentProvider.providerName

    val isDemoMode: Boolean
        get() = currentProvider.isDemoMode

    // Remote / Provider Operations
    suspend fun getTrending(): List<Track> = currentProvider.getTrending()
    suspend fun getHindiHits(): List<Track> = currentProvider.getHindiHits()
    suspend fun getEnglishHits(): List<Track> = currentProvider.getEnglishHits()
    suspend fun getBollywoodEssentials(): List<Track> = currentProvider.getBollywoodEssentials()
    suspend fun getPunjabiVibes(): List<Track> = currentProvider.getPunjabiVibes()
    suspend fun getRecommendations(seedGenre: String? = null): List<Track> = currentProvider.getRecommendations(seedGenre)
    suspend fun search(query: String): SearchResults = currentProvider.search(query)
    suspend fun getArtist(id: String): Artist? = currentProvider.getArtist(id)
    suspend fun getAlbum(id: String): Album? = currentProvider.getAlbum(id)
    suspend fun getPlaylist(id: String): Playlist? = currentProvider.getPlaylist(id)
    suspend fun getTracksByGenre(genre: String): List<Track> = currentProvider.getTracksByGenre(genre)

    // Favorites
    fun getAllFavorites(): Flow<List<Track>> {
        return musicDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                Track(
                    id = entity.id,
                    title = entity.title,
                    artist = entity.artist,
                    album = entity.album,
                    albumArtUrl = entity.albumArtUrl,
                    previewUrl = entity.previewUrl,
                    durationMs = entity.durationMs,
                    language = entity.language,
                    isFavorite = true
                )
            }
        }
    }

    fun isFavorite(trackId: String): Flow<Boolean> = musicDao.isFavorite(trackId)

    suspend fun toggleFavorite(track: Track) {
        val isFav = musicDao.isFavoriteSync(track.id)
        if (isFav) {
            musicDao.deleteFavorite(track.id)
        } else {
            musicDao.insertFavorite(
                FavoriteTrackEntity(
                    id = track.id,
                    title = track.title,
                    artist = track.artist,
                    album = track.album,
                    albumArtUrl = track.albumArtUrl,
                    previewUrl = track.previewUrl,
                    durationMs = track.durationMs,
                    language = track.language
                )
            )
        }
    }

    // Playlists
    fun getAllPlaylists(): Flow<List<Playlist>> {
        return musicDao.getAllPlaylists().map { list ->
            list.map { entity ->
                Playlist(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    coverArtUrl = entity.coverArtUrl,
                    trackCount = 0,
                    isCustom = true
                )
            }
        }
    }

    suspend fun createPlaylist(title: String, description: String): String {
        val id = "pl_user_${UUID.randomUUID()}"
        musicDao.insertPlaylist(
            PlaylistEntity(
                id = id,
                title = title,
                description = description,
                coverArtUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80"
            )
        )
        return id
    }

    suspend fun updatePlaylist(playlistId: String, title: String, description: String) {
        musicDao.updatePlaylist(playlistId, title, description)
    }

    suspend fun deletePlaylist(playlistId: String) {
        musicDao.clearPlaylistTracks(playlistId)
        musicDao.deletePlaylist(playlistId)
    }

    fun getPlaylistTracks(playlistId: String): Flow<List<Track>> {
        return musicDao.getTracksForPlaylist(playlistId).map { list ->
            list.map {
                Track(
                    id = it.trackId,
                    title = it.trackTitle,
                    artist = it.artist,
                    albumArtUrl = it.albumArtUrl,
                    previewUrl = it.previewUrl,
                    durationMs = it.durationMs
                )
            }
        }
    }

    suspend fun addTrackToPlaylist(playlistId: String, track: Track) {
        musicDao.insertPlaylistTrack(
            PlaylistTrackEntity(
                playlistId = playlistId,
                trackId = track.id,
                trackTitle = track.title,
                artist = track.artist,
                albumArtUrl = track.albumArtUrl,
                previewUrl = track.previewUrl,
                durationMs = track.durationMs,
                position = System.currentTimeMillis().toInt()
            )
        )
    }

    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        musicDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    // Recently Played
    fun getRecentlyPlayed(): Flow<List<Track>> {
        return musicDao.getRecentlyPlayed().map { list ->
            list.map {
                Track(
                    id = it.trackId,
                    title = it.title,
                    artist = it.artist,
                    albumArtUrl = it.albumArtUrl,
                    previewUrl = it.previewUrl,
                    durationMs = it.durationMs,
                    language = it.language
                )
            }
        }
    }

    suspend fun recordRecentlyPlayed(track: Track) {
        musicDao.insertRecentlyPlayed(
            RecentlyPlayedEntity(
                trackId = track.id,
                title = track.title,
                artist = track.artist,
                albumArtUrl = track.albumArtUrl,
                previewUrl = track.previewUrl,
                durationMs = track.durationMs,
                language = track.language,
                playedAt = System.currentTimeMillis()
            )
        )
    }

    // Followed Artists
    fun getAllFollowedArtists(): Flow<List<FollowedArtistEntity>> = musicDao.getAllFollowedArtists()
    fun isArtistFollowed(artistId: String): Flow<Boolean> = musicDao.isArtistFollowed(artistId)

    suspend fun toggleFollowArtist(artist: Artist) {
        val entity = FollowedArtistEntity(
            id = artist.id,
            name = artist.name,
            imageUrl = artist.imageUrl,
            genre = artist.genre
        )
        musicDao.followArtist(entity)
    }

    suspend fun unfollowArtist(artistId: String) {
        musicDao.unfollowArtist(artistId)
    }

    // User Profile (Local Temporary Login)
    fun getUserProfile(): Flow<UserProfileEntity?> = musicDao.getUserProfile()

    suspend fun saveUserProfile(name: String, email: String, preferredLanguages: String) {
        musicDao.saveUserProfile(
            UserProfileEntity(
                name = name,
                email = email,
                preferredLanguages = preferredLanguages,
                isLoggedIn = true
            )
        )
    }

    suspend fun logoutUser() {
        musicDao.clearUserProfile()
    }
}
