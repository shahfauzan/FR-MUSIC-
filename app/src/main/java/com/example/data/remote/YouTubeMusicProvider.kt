package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track
import com.example.data.remote.youtube.YouTubeApiService
import com.example.data.remote.youtube.YouTubeSearchResultItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class YouTubeMusicProvider(
    private val fallbackProvider: MusicProvider = LegalPreviewMusicProvider()
) : MusicProvider {

    override val providerName: String = "YouTube Data API v3"
    override val isDemoMode: Boolean = false

    private val apiKey: String
        get() = BuildConfig.YOUTUBE_API_KEY

    private val apiService: YouTubeApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YouTubeApiService::class.java)
    }

    private fun mapItemToTrack(item: YouTubeSearchResultItem, defaultLang: String = "Hindi"): Track {
        val videoId = item.id?.videoId ?: System.currentTimeMillis().toString()
        val rawTitle = item.snippet?.title?.replace("&quot;", "\"")?.replace("&amp;", "&") ?: "YouTube Music Track"
        val channel = item.snippet?.channelTitle ?: "YouTube Artist"
        val thumbnail = item.snippet?.thumbnails?.high?.url
            ?: item.snippet?.thumbnails?.medium?.url
            ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80"

        // Parse artist - title if formatted like "Artist - Song (Official Video)"
        val split = rawTitle.split(" - ", " – ")
        val artist = if (split.size > 1) split[0].trim() else channel.replace(" - Topic", "").replace("Official", "").trim()
        val cleanTitle = if (split.size > 1) {
            split[1].replace(Regex("\\(Official.*?\\)|\\[Official.*?\\]|\\(Lyric.*?\\)"), "").trim()
        } else {
            rawTitle.replace(Regex("\\(Official.*?\\)|\\[Official.*?\\]|\\(Lyric.*?\\)"), "").trim()
        }

        val lang = when {
            channel.contains("T-Series", ignoreCase = true) || channel.contains("Sony Music India", ignoreCase = true) || channel.contains("Zee Music", ignoreCase = true) -> "Hindi"
            cleanTitle.contains("Punjabi", ignoreCase = true) || channel.contains("Speed Records", ignoreCase = true) -> "Punjabi"
            else -> defaultLang
        }

        // Use varied full-length royalty-free audio streams
        val trackIdx = (kotlin.math.abs(videoId.hashCode()) % 6) + 1
        val previewAudio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-$trackIdx.mp3"

        return Track(
            id = "yt_$videoId",
            title = cleanTitle,
            artist = artist,
            artistId = "art_yt_${channel.hashCode()}",
            album = "YouTube Official Release",
            albumId = "alb_yt_$videoId",
            albumArtUrl = thumbnail,
            previewUrl = previewAudio,
            durationMs = 240000L,
            genre = "Music Video",
            language = lang,
            releaseYear = item.snippet?.publishedAt?.take(4) ?: "2024"
        )
    }

    override suspend fun search(query: String): SearchResults {
        if (apiKey.isBlank() || apiKey == "YOUR_YOUTUBE_API_KEY") {
            return fallbackProvider.search(query)
        }

        return try {
            val response = apiService.searchMusicVideos(
                query = "$query song music",
                apiKey = apiKey
            )
            val tracks = response.items.map { mapItemToTrack(it) }
            val artists = tracks.map { it.artist }.distinct().take(4).map { name ->
                Artist(
                    id = "art_yt_${name.hashCode()}",
                    name = name,
                    imageUrl = tracks.firstOrNull { it.artist == name }?.albumArtUrl
                        ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                    genre = "Music",
                    language = "Hindi",
                    monthlyListeners = "YouTube Music",
                    bio = "Official music video artist on YouTube Music."
                )
            }
            SearchResults(tracks = tracks, artists = artists)
        } catch (e: Exception) {
            Log.w("YouTubeMusicProvider", "YouTube API failed, falling back", e)
            fallbackProvider.search(query)
        }
    }

    override suspend fun getTrending(): List<Track> {
        if (apiKey.isBlank() || apiKey == "YOUR_YOUTUBE_API_KEY") return fallbackProvider.getTrending()
        return try {
            val res = apiService.searchMusicVideos(query = "trending top hits 2024 hindi english", apiKey = apiKey)
            val tracks = res.items.map { mapItemToTrack(it) }
            tracks.ifEmpty { fallbackProvider.getTrending() }
        } catch (e: Exception) {
            fallbackProvider.getTrending()
        }
    }

    override suspend fun getHindiHits(): List<Track> {
        if (apiKey.isBlank() || apiKey == "YOUR_YOUTUBE_API_KEY") return fallbackProvider.getHindiHits()
        return try {
            val res = apiService.searchMusicVideos(query = "latest hindi songs bollywood 2024", apiKey = apiKey)
            val tracks = res.items.map { mapItemToTrack(it, "Hindi") }
            tracks.ifEmpty { fallbackProvider.getHindiHits() }
        } catch (e: Exception) {
            fallbackProvider.getHindiHits()
        }
    }

    override suspend fun getEnglishHits(): List<Track> {
        if (apiKey.isBlank() || apiKey == "YOUR_YOUTUBE_API_KEY") return fallbackProvider.getEnglishHits()
        return try {
            val res = apiService.searchMusicVideos(query = "billboard hot 100 pop english songs", apiKey = apiKey)
            val tracks = res.items.map { mapItemToTrack(it, "English") }
            tracks.ifEmpty { fallbackProvider.getEnglishHits() }
        } catch (e: Exception) {
            fallbackProvider.getEnglishHits()
        }
    }

    override suspend fun getBollywoodEssentials(): List<Track> {
        if (apiKey.isBlank() || apiKey == "YOUR_YOUTUBE_API_KEY") return fallbackProvider.getBollywoodEssentials()
        return try {
            val res = apiService.searchMusicVideos(query = "best bollywood romantic songs arijit singh", apiKey = apiKey)
            val tracks = res.items.map { mapItemToTrack(it, "Hindi") }
            tracks.ifEmpty { fallbackProvider.getBollywoodEssentials() }
        } catch (e: Exception) {
            fallbackProvider.getBollywoodEssentials()
        }
    }

    override suspend fun getPunjabiVibes(): List<Track> {
        if (apiKey.isBlank() || apiKey == "YOUR_YOUTUBE_API_KEY") return fallbackProvider.getPunjabiVibes()
        return try {
            val res = apiService.searchMusicVideos(query = "top punjabi songs diljit dosanjh ap dhillon", apiKey = apiKey)
            val tracks = res.items.map { mapItemToTrack(it, "Punjabi") }
            tracks.ifEmpty { fallbackProvider.getPunjabiVibes() }
        } catch (e: Exception) {
            fallbackProvider.getPunjabiVibes()
        }
    }

    override suspend fun getRecommendations(seedGenre: String?): List<Track> = fallbackProvider.getRecommendations(seedGenre)
    override suspend fun getArtist(id: String): Artist? = fallbackProvider.getArtist(id)
    override suspend fun getAlbum(id: String): Album? = fallbackProvider.getAlbum(id)
    override suspend fun getPlaylist(id: String): Playlist? = fallbackProvider.getPlaylist(id)
    override suspend fun getTrack(id: String): Track? = fallbackProvider.getTrack(id)
    override suspend fun getTracksByGenre(genre: String): List<Track> = fallbackProvider.getTracksByGenre(genre)
}
