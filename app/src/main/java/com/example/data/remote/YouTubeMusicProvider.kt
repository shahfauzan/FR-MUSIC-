package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track
import com.example.data.remote.youtube.YouTubeApiService
import com.example.data.remote.youtube.YouTubeLinkResolver
import com.example.data.remote.youtube.YouTubeSearchResultItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Unified Search & Music Provider powered by YouTube Data API v3,
 * enriched concurrently with iTunes Search catalog & Audius Open full-length streams.
 * Ensures rich YouTube-grade search results for Hindi, Bollywood, Punjabi, and English queries,
 * even when the YouTube quota is reached or no YouTube API key is supplied.
 */
class YouTubeMusicProvider(
    private val audiusProvider: MusicProvider = AudiusMusicProvider(),
    private val legalPreviewProvider: MusicProvider = LegalPreviewMusicProvider(),
    private val demoFallback: DemoMusicProvider = DemoMusicProvider()
) : MusicProvider {

    override val providerName: String = "FR Unified Search (YouTube + Open Streams)"
    override val isDemoMode: Boolean = false

    private val apiKey: String
        get() = BuildConfig.YOUTUBE_API_KEY

    private val hasValidApiKey: Boolean
        get() = apiKey.isNotBlank() &&
            apiKey != "YOUR_YOUTUBE_API_KEY" &&
            !apiKey.contains("your_key", ignoreCase = true)

    private val apiService: YouTubeApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
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

    private fun decodeHtmlEntities(input: String): String {
        return input
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }

    private fun mapItemToTrack(item: YouTubeSearchResultItem, defaultLang: String = "Hindi"): Track? {
        val videoId = item.id?.videoId?.takeIf { it.isNotBlank() } ?: return null
        val rawTitle = decodeHtmlEntities(item.snippet?.title ?: "YouTube Music Track")
        val rawChannel = decodeHtmlEntities(item.snippet?.channelTitle ?: "YouTube Artist")
        val thumbnail = item.snippet?.thumbnails?.high?.url
            ?: item.snippet?.thumbnails?.medium?.url
            ?: item.snippet?.thumbnails?.default?.url
            ?: "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

        val (cleanTitle, artist) = YouTubeLinkResolver.cleanMetadata(rawTitle, rawChannel)

        val lowerAll = "$rawTitle $rawChannel".lowercase()
        val lang = when {
            lowerAll.contains("punjabi") || lowerAll.contains("diljit") || lowerAll.contains("aujla") ||
                lowerAll.contains("sidhu") || lowerAll.contains("speed records") || lowerAll.contains("ap dhillon") -> "Punjabi"
            lowerAll.contains("t-series") || lowerAll.contains("sony music india") || lowerAll.contains("zee music") ||
                lowerAll.contains("arijit") || lowerAll.contains("bollywood") || lowerAll.contains("hindi") -> "Hindi"
            else -> defaultLang
        }

        return Track(
            id = "yt_$videoId",
            title = cleanTitle,
            artist = artist,
            artistId = "art_yt_${artist.hashCode()}",
            album = "YouTube",
            albumId = "alb_yt_$videoId",
            albumArtUrl = thumbnail,
            previewUrl = null,
            durationMs = 240000L,
            genre = "YouTube",
            language = lang,
            releaseYear = item.snippet?.publishedAt?.take(4)?.takeIf { it.length == 4 } ?: "2024",
            youtubeVideoId = videoId
        )
    }

    override suspend fun search(query: String): SearchResults = coroutineScope {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return@coroutineScope SearchResults()

        val lower = trimmed.lowercase()
        val ytQuery = when (lower) {
            "all", "explore" -> "top trending hindi english songs official music video"
            "new releases", "new", "latest" -> "new hindi punjabi english songs 2025 official"
            else -> trimmed
        }

        val ytDeferred = async {
            if (!hasValidApiKey) {
                emptyList()
            } else {
                try {
                    val response = apiService.searchMusicVideos(
                        query = ytQuery,
                        apiKey = apiKey
                    )
                    response.items.mapNotNull { mapItemToTrack(it, defaultLang = "English") }
                } catch (e: Exception) {
                    Log.w("YouTubeMusicProvider", "YouTube Data API search fallback triggered: ${e.message}")
                    emptyList()
                }
            }
        }

        val itunesDeferred = async {
            try {
                legalPreviewProvider.search(if (lower == "all" || lower == "new releases") "top hits 2025" else trimmed)
            } catch (_: Exception) {
                SearchResults()
            }
        }

        val audiusDeferred = async {
            try {
                audiusProvider.search(trimmed)
            } catch (_: Exception) {
                SearchResults()
            }
        }

        val localResults = demoFallback.search(trimmed)
        val ytTracks = ytDeferred.await()
        val itunesResults = itunesDeferred.await()
        val audiusResults = audiusDeferred.await()

        // Put real YouTube results first, then real iTunes catalog tracks, then Audius full streams & local curated matches
        val combinedTracks = (ytTracks + itunesResults.tracks + audiusResults.tracks + localResults.tracks)
            .distinctBy {
                it.youtubeVideoId?.let { vid -> "yt:$vid" }
                    ?: "${it.title.lowercase().trim()}|${it.artist.lowercase().trim()}"
            }

        val ytArtists = ytTracks.map { it.artist }.distinct().take(4).map { name ->
            val match = ytTracks.firstOrNull { it.artist == name }
            Artist(
                id = match?.artistId ?: "art_yt_${name.hashCode()}",
                name = name,
                imageUrl = match?.albumArtUrl
                    ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                genre = match?.genre ?: "Music",
                language = match?.language ?: "Hindi",
                monthlyListeners = "YouTube",
                bio = "Artist from YouTube search results on FR Music."
            )
        }

        val combinedArtists = (localResults.artists + itunesResults.artists + ytArtists + audiusResults.artists)
            .distinctBy { it.name.lowercase().trim() }
            .take(8)

        val combinedAlbums = (localResults.albums + itunesResults.albums + audiusResults.albums)
            .distinctBy { it.title.lowercase().trim() }
            .take(8)

        val combinedPlaylists = (localResults.playlists + audiusResults.playlists)
            .distinctBy { it.id }

        SearchResults(
            tracks = combinedTracks,
            artists = combinedArtists,
            albums = combinedAlbums,
            playlists = combinedPlaylists
        )
    }

    override suspend fun getTrending(): List<Track> = audiusProvider.getTrending()
    override suspend fun getHindiHits(): List<Track> = audiusProvider.getHindiHits()
    override suspend fun getEnglishHits(): List<Track> = audiusProvider.getEnglishHits()
    override suspend fun getBollywoodEssentials(): List<Track> = audiusProvider.getBollywoodEssentials()
    override suspend fun getPunjabiVibes(): List<Track> = audiusProvider.getPunjabiVibes()
    override suspend fun getRecommendations(seedGenre: String?): List<Track> = audiusProvider.getRecommendations(seedGenre)
    override suspend fun getArtist(id: String): Artist? = audiusProvider.getArtist(id)
    override suspend fun getAlbum(id: String): Album? = audiusProvider.getAlbum(id)
    override suspend fun getPlaylist(id: String): Playlist? = audiusProvider.getPlaylist(id)
    override suspend fun getTrack(id: String): Track? = audiusProvider.getTrack(id)
    override suspend fun getTracksByGenre(genre: String): List<Track> = audiusProvider.getTracksByGenre(genre)
}
