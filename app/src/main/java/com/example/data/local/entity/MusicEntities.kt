package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtUrl: String,
    val previewUrl: String?,
    val durationMs: Long,
    val language: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val coverArtUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackEntity(
    val playlistId: String,
    val trackId: String,
    val trackTitle: String,
    val artist: String,
    val albumArtUrl: String,
    val previewUrl: String?,
    val durationMs: Long,
    val position: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey val trackId: String,
    val title: String,
    val artist: String,
    val albumArtUrl: String,
    val previewUrl: String?,
    val durationMs: Long,
    val language: String,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "followed_artists")
data class FollowedArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val genre: String,
    val followedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "primary_user",
    val name: String,
    val email: String,
    val preferredLanguages: String,
    val isLoggedIn: Boolean = true,
    val lastLoginAt: Long = System.currentTimeMillis()
)
