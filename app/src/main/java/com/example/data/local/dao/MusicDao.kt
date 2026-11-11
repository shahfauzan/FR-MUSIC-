package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.FavoriteTrackEntity
import com.example.data.local.entity.FollowedArtistEntity
import com.example.data.local.entity.PlaylistEntity
import com.example.data.local.entity.PlaylistTrackEntity
import com.example.data.local.entity.RecentlyPlayedEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    // Favorites
    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE id = :trackId)")
    fun isFavorite(trackId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE id = :trackId)")
    suspend fun isFavoriteSync(trackId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(track: FavoriteTrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE id = :trackId")
    suspend fun deleteFavorite(trackId: String)

    // Playlists
    @Query("SELECT * FROM custom_playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM custom_playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: String): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("UPDATE custom_playlists SET title = :newTitle, description = :newDesc WHERE id = :playlistId")
    suspend fun updatePlaylist(playlistId: String, newTitle: String, newDesc: String)

    @Query("DELETE FROM custom_playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: String)

    // Playlist Tracks
    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY position ASC")
    fun getTracksForPlaylist(playlistId: String): Flow<List<PlaylistTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun clearPlaylistTracks(playlistId: String)

    // Recently Played
    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT 20")
    fun getRecentlyPlayed(): Flow<List<RecentlyPlayedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyPlayed(track: RecentlyPlayedEntity)

    @Query("DELETE FROM recently_played")
    suspend fun clearRecentlyPlayed()

    // Followed Artists
    @Query("SELECT * FROM followed_artists ORDER BY followedAt DESC")
    fun getAllFollowedArtists(): Flow<List<FollowedArtistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM followed_artists WHERE id = :artistId)")
    fun isArtistFollowed(artistId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followArtist(artist: FollowedArtistEntity)

    @Query("DELETE FROM followed_artists WHERE id = :artistId")
    suspend fun unfollowArtist(artistId: String)

    // User Profile (Local Temporary Login)
    @Query("SELECT * FROM user_profile WHERE id = 'primary_user'")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'primary_user'")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile WHERE id = 'primary_user'")
    suspend fun clearUserProfile()
}
