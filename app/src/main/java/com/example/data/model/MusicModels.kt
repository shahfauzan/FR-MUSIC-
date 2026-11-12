package com.example.data.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String = "",
    val album: String = "",
    val albumId: String = "",
    val albumArtUrl: String,
    val previewUrl: String? = null,
    val durationMs: Long = 180000L, // Full-length track duration
    val genre: String = "Pop",
    val language: String = "Hindi", // "Hindi", "English", "Punjabi"
    val releaseYear: String = "2024",
    val playsCount: Long = 1250000L,
    val isFavorite: Boolean = false
) {
    val durationFormatted: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String,
    val genre: String,
    val language: String,
    val monthlyListeners: String,
    val bio: String,
    val isFollowed: Boolean = false,
    val topTracks: List<Track> = emptyList(),
    val albums: List<Album> = emptyList()
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String,
    val coverArtUrl: String,
    val releaseYear: String,
    val trackCount: Int,
    val genre: String,
    val language: String,
    val tracks: List<Track> = emptyList()
)

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverArtUrl: String,
    val trackCount: Int,
    val isCustom: Boolean = false,
    val language: String = "All",
    val tracks: List<Track> = emptyList()
)

data class Category(
    val id: String,
    val name: String,
    val language: String,
    val gradientStartHex: Long,
    val gradientEndHex: Long,
    val subcategories: List<String>
)

data class SearchResults(
    val tracks: List<Track> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList()
)

enum class PlaybackState {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}
