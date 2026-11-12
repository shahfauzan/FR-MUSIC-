package com.example.data.remote

import android.util.Log
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track
import com.example.data.remote.audius.AudiusApiService
import com.example.data.remote.audius.AudiusTrackDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Full-Length Open Music Provider powered by Audius REST API v1.
 * Streams full-length tracks (2 to 6+ minutes) legally without DRM bypass or scraping,
 * with full-length DemoMusicProvider tracks as fallback.
 */
class AudiusMusicProvider(
    private val demoFallback: DemoMusicProvider = DemoMusicProvider()
) : MusicProvider {

    override val providerName: String = "FR Full Stream Engine (Audius Open API)"
    override val isDemoMode: Boolean = false

    private val apiService: AudiusApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.audius.co/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AudiusApiService::class.java)
    }

    private fun mapDtoToTrack(dto: AudiusTrackDto, defaultLang: String = "English"): Track? {
        val id = dto.id ?: return null
        val durationSec = dto.duration ?: 180
        if (durationSec < 45) return null // Filter out short clips; enforce full-length tracks

        val title = dto.title?.trim().orEmpty().ifEmpty { "Untitled Track" }
        val artistName = dto.user?.name?.trim().orEmpty().ifEmpty { "Independent Artist" }
        val artistId = dto.user?.id?.let { "art_aud_$it" } ?: "art_${artistName.hashCode()}"
        val artUrl = dto.artwork?.x480
            ?: dto.artwork?.x1000
            ?: dto.artwork?.x150
            ?: dto.user?.profilePicture?.x480
            ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80"

        val genre = dto.genre?.takeIf { it.isNotBlank() } ?: "Music"
        val lowerTitle = title.lowercase()
        val lowerGenre = genre.lowercase()
        val lang = when {
            lowerTitle.contains("hindi") || lowerTitle.contains("bollywood") || lowerTitle.contains("arijit") || lowerGenre.contains("indian") -> "Hindi"
            lowerTitle.contains("punjabi") || lowerTitle.contains("bhangra") || lowerTitle.contains("diljit") -> "Punjabi"
            else -> defaultLang
        }

        val streamUrl = "https://api.audius.co/v1/tracks/$id/stream?app_name=FRMusic"

        return Track(
            id = "aud_$id",
            title = title,
            artist = artistName,
            artistId = artistId,
            album = dto.mood?.takeIf { it.isNotBlank() }?.let { "$it Sessions" } ?: "Single Release",
            albumId = "alb_aud_$id",
            albumArtUrl = artUrl,
            previewUrl = streamUrl,
            durationMs = durationSec * 1000L,
            genre = genre,
            language = lang,
            releaseYear = dto.releaseDate?.take(4)?.takeIf { it.length == 4 } ?: "2024",
            playsCount = dto.playCount ?: 0L
        )
    }

    override suspend fun search(query: String): SearchResults {
        if (query.isBlank()) return SearchResults()
        return try {
            val response = apiService.searchTracks(query = query, limit = 25)
            val audiusTracks = response.data.mapNotNull { mapDtoToTrack(it) }
            val fallbackResults = demoFallback.search(query)

            val mergedTracks = if (audiusTracks.size >= 4) {
                audiusTracks
            } else {
                (audiusTracks + fallbackResults.tracks).distinctBy { it.id }
            }

            val artists = mergedTracks.map { it.artist }.distinct().take(4).map { name ->
                val match = mergedTracks.firstOrNull { it.artist == name }
                Artist(
                    id = match?.artistId?.ifEmpty { "art_${name.hashCode()}" } ?: "art_${name.hashCode()}",
                    name = name,
                    imageUrl = match?.albumArtUrl ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                    genre = match?.genre ?: "Pop",
                    language = match?.language ?: "Hindi",
                    monthlyListeners = "18.2M",
                    bio = "Full-length streaming artist on FR Music."
                )
            }

            val albums = mergedTracks.map { it.album }.distinct().take(4).map { albumTitle ->
                val match = mergedTracks.firstOrNull { it.album == albumTitle }
                Album(
                    id = match?.albumId?.ifEmpty { "alb_${albumTitle.hashCode()}" } ?: "alb_${albumTitle.hashCode()}",
                    title = albumTitle,
                    artist = match?.artist ?: "Various Artists",
                    artistId = match?.artistId ?: "",
                    coverArtUrl = match?.albumArtUrl ?: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
                    releaseYear = match?.releaseYear ?: "2024",
                    trackCount = 8,
                    genre = match?.genre ?: "Pop",
                    language = match?.language ?: "English"
                )
            }

            SearchResults(
                tracks = mergedTracks,
                artists = (artists + fallbackResults.artists).distinctBy { it.name }.take(6),
                albums = (albums + fallbackResults.albums).distinctBy { it.title }.take(6)
            )
        } catch (e: Exception) {
            Log.w("AudiusMusicProvider", "Search failed, falling back to full-length demo catalog", e)
            demoFallback.search(query)
        }
    }

    override suspend fun getTrending(): List<Track> {
        return try {
            val res = apiService.getTrendingTracks(limit = 15)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it, "English") }
            tracks.ifEmpty { demoFallback.getTrending() }
        } catch (e: Exception) {
            Log.w("AudiusMusicProvider", "Trending failed, using demo fallback", e)
            demoFallback.getTrending()
        }
    }

    override suspend fun getHindiHits(): List<Track> {
        return try {
            val res = apiService.searchTracks(query = "hindi bollywood remix", limit = 15)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it, "Hindi") }
            if (tracks.size >= 3) tracks else (tracks + demoFallback.getHindiHits()).distinctBy { it.id }
        } catch (e: Exception) {
            demoFallback.getHindiHits()
        }
    }

    override suspend fun getEnglishHits(): List<Track> {
        return try {
            val res = apiService.getTrendingTracks(genre = "Pop", limit = 15)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it, "English") }
            if (tracks.size >= 3) tracks else (tracks + demoFallback.getEnglishHits()).distinctBy { it.id }
        } catch (e: Exception) {
            demoFallback.getEnglishHits()
        }
    }

    override suspend fun getBollywoodEssentials(): List<Track> {
        return try {
            val res = apiService.searchTracks(query = "bollywood lofi romantic", limit = 15)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it, "Hindi") }
            if (tracks.size >= 3) tracks else (tracks + demoFallback.getBollywoodEssentials()).distinctBy { it.id }
        } catch (e: Exception) {
            demoFallback.getBollywoodEssentials()
        }
    }

    override suspend fun getPunjabiVibes(): List<Track> {
        return try {
            val res = apiService.searchTracks(query = "punjabi bhangra", limit = 15)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it, "Punjabi") }
            if (tracks.size >= 3) tracks else (tracks + demoFallback.getPunjabiVibes()).distinctBy { it.id }
        } catch (e: Exception) {
            demoFallback.getPunjabiVibes()
        }
    }

    override suspend fun getRecommendations(seedGenre: String?): List<Track> {
        return try {
            val query = seedGenre?.takeIf { it.isNotBlank() } ?: "lofi chill beats"
            val res = apiService.searchTracks(query = query, limit = 12)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it, "English") }
            if (tracks.size >= 3) tracks else (tracks + demoFallback.getRecommendations(seedGenre)).distinctBy { it.id }
        } catch (e: Exception) {
            demoFallback.getRecommendations(seedGenre)
        }
    }

    override suspend fun getArtist(id: String): Artist? {
        val demoArtist = demoFallback.getArtist(id)
        if (demoArtist != null) return demoArtist
        return try {
            val cleanQuery = id.removePrefix("art_aud_").removePrefix("art_")
            val res = apiService.searchTracks(query = cleanQuery, limit = 10)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it) }
            val first = tracks.firstOrNull() ?: return null
            Artist(
                id = id,
                name = first.artist,
                imageUrl = first.albumArtUrl,
                genre = first.genre,
                language = first.language,
                monthlyListeners = "18.2M",
                bio = "Full-length streaming artist on FR Music.",
                topTracks = tracks
            )
        } catch (e: Exception) {
            demoFallback.getArtist(id)
        }
    }

    override suspend fun getAlbum(id: String): Album? {
        val demoAlbum = demoFallback.getAlbum(id)
        if (demoAlbum != null) return demoAlbum
        return try {
            val cleanQuery = id.removePrefix("alb_aud_").removePrefix("alb_")
            val res = apiService.searchTracks(query = cleanQuery, limit = 8)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it) }
            val first = tracks.firstOrNull() ?: return null
            Album(
                id = id,
                title = first.album,
                artist = first.artist,
                artistId = first.artistId,
                coverArtUrl = first.albumArtUrl,
                releaseYear = first.releaseYear,
                trackCount = tracks.size,
                genre = first.genre,
                language = first.language,
                tracks = tracks
            )
        } catch (e: Exception) {
            demoFallback.getAlbum(id)
        }
    }

    override suspend fun getPlaylist(id: String): Playlist? = demoFallback.getPlaylist(id)

    override suspend fun getTrack(id: String): Track? = demoFallback.getTrack(id)

    override suspend fun getTracksByGenre(genre: String): List<Track> {
        return try {
            val res = apiService.searchTracks(query = genre, limit = 15)
            val tracks = res.data.mapNotNull { mapDtoToTrack(it) }
            if (tracks.size >= 3) tracks else (tracks + demoFallback.getTracksByGenre(genre)).distinctBy { it.id }
        } catch (e: Exception) {
            demoFallback.getTracksByGenre(genre)
        }
    }
}
