package com.example.data.remote

import android.util.Log
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track
import com.example.data.remote.itunes.ItunesApiService
import com.example.data.remote.itunes.ItunesTrackDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Legal Preview Music Provider.
 * Connects to legal, open public preview endpoints (iTunes Search API)
 * providing authorized 30-second audio preview streams and high-resolution album artwork.
 * Complies strictly with zero DRM-bypass, zero unauthorized downloads policy.
 */
class LegalPreviewMusicProvider(
    private val demoFallback: DemoMusicProvider = DemoMusicProvider()
) : MusicProvider {

    override val providerName: String = "FR Authorized Preview API (iTunes)"
    override val isDemoMode: Boolean = false

    private val apiService: ItunesApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ItunesApiService::class.java)
    }

    private fun mapDtoToTrack(dto: ItunesTrackDto, defaultLang: String = "Hindi"): Track {
        val highResArt = dto.artworkUrl100?.replace("100x100bb.jpg", "600x600bb.jpg")
            ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80"
        val genre = dto.primaryGenreName ?: "Pop"
        val lang = when {
            genre.contains("Indian", ignoreCase = true) || dto.artistName?.contains("Arijit", ignoreCase = true) == true -> "Hindi"
            dto.artistName?.contains("Diljit", ignoreCase = true) == true || dto.artistName?.contains("Dhillon", ignoreCase = true) == true -> "Punjabi"
            defaultLang.isNotEmpty() -> defaultLang
            else -> "English"
        }

        return Track(
            id = (dto.trackId ?: System.currentTimeMillis()).toString(),
            title = dto.trackName ?: "Unknown Track",
            artist = dto.artistName ?: "Unknown Artist",
            artistId = dto.artistId?.toString() ?: "",
            album = dto.collectionName ?: "Single",
            albumId = dto.collectionId?.toString() ?: "",
            albumArtUrl = highResArt,
            previewUrl = dto.previewUrl,
            durationMs = dto.trackTimeMillis ?: 30000L,
            genre = genre,
            language = lang,
            releaseYear = dto.releaseDate?.take(4) ?: "2024"
        )
    }

    override suspend fun search(query: String): SearchResults {
        if (query.isBlank()) return SearchResults()
        return try {
            val response = apiService.searchTracks(term = query, limit = 25)
            val tracks = response.results.map { mapDtoToTrack(it) }
            
            val artists = tracks.map { it.artist }.distinct().take(4).map { name ->
                val matchingTrack = tracks.firstOrNull { it.artist == name }
                Artist(
                    id = matchingTrack?.artistId?.ifEmpty { "art_${name.hashCode()}" } ?: "art_${name.hashCode()}",
                    name = name,
                    imageUrl = matchingTrack?.albumArtUrl ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                    genre = matchingTrack?.genre ?: "Pop",
                    language = matchingTrack?.language ?: "Hindi",
                    monthlyListeners = "12.4M",
                    bio = "Trending artist on FR Music streaming charts."
                )
            }

            val albums = tracks.map { it.album }.distinct().take(4).map { albumTitle ->
                val matchingTrack = tracks.firstOrNull { it.album == albumTitle }
                Album(
                    id = matchingTrack?.albumId?.ifEmpty { "alb_${albumTitle.hashCode()}" } ?: "alb_${albumTitle.hashCode()}",
                    title = albumTitle,
                    artist = matchingTrack?.artist ?: "Various Artists",
                    artistId = matchingTrack?.artistId ?: "",
                    coverArtUrl = matchingTrack?.albumArtUrl ?: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
                    releaseYear = matchingTrack?.releaseYear ?: "2024",
                    trackCount = 10,
                    genre = matchingTrack?.genre ?: "Pop",
                    language = matchingTrack?.language ?: "Hindi"
                )
            }

            SearchResults(tracks = tracks, artists = artists, albums = albums)
        } catch (e: Exception) {
            Log.w("LegalPreviewProvider", "Search error, falling back to demo", e)
            demoFallback.search(query)
        }
    }

    override suspend fun getTrending(): List<Track> {
        return try {
            val res = apiService.searchTracks(term = "top hits 2024", limit = 12)
            val tracks = res.results.map { mapDtoToTrack(it, "English") }
            tracks.ifEmpty { demoFallback.getTrending() }
        } catch (e: Exception) {
            demoFallback.getTrending()
        }
    }

    override suspend fun getHindiHits(): List<Track> {
        return try {
            val res = apiService.searchTracks(term = "arijit singh hindi hits", limit = 10)
            val tracks = res.results.map { mapDtoToTrack(it, "Hindi") }
            tracks.ifEmpty { demoFallback.getHindiHits() }
        } catch (e: Exception) {
            demoFallback.getHindiHits()
        }
    }

    override suspend fun getEnglishHits(): List<Track> {
        return try {
            val res = apiService.searchTracks(term = "the weeknd billboard pop", limit = 10)
            val tracks = res.results.map { mapDtoToTrack(it, "English") }
            tracks.ifEmpty { demoFallback.getEnglishHits() }
        } catch (e: Exception) {
            demoFallback.getEnglishHits()
        }
    }

    override suspend fun getBollywoodEssentials(): List<Track> {
        return try {
            val res = apiService.searchTracks(term = "bollywood romantic songs", limit = 10)
            val tracks = res.results.map { mapDtoToTrack(it, "Hindi") }
            tracks.ifEmpty { demoFallback.getBollywoodEssentials() }
        } catch (e: Exception) {
            demoFallback.getBollywoodEssentials()
        }
    }

    override suspend fun getPunjabiVibes(): List<Track> {
        return try {
            val res = apiService.searchTracks(term = "diljit dosanjh punjabi", limit = 10)
            val tracks = res.results.map { mapDtoToTrack(it, "Punjabi") }
            tracks.ifEmpty { demoFallback.getPunjabiVibes() }
        } catch (e: Exception) {
            demoFallback.getPunjabiVibes()
        }
    }

    override suspend fun getRecommendations(seedGenre: String?): List<Track> {
        return try {
            val term = seedGenre ?: "lofi chill beats"
            val res = apiService.searchTracks(term = term, limit = 8)
            val tracks = res.results.map { mapDtoToTrack(it) }
            tracks.ifEmpty { demoFallback.getRecommendations(seedGenre) }
        } catch (e: Exception) {
            demoFallback.getRecommendations(seedGenre)
        }
    }

    override suspend fun getArtist(id: String): Artist? {
        val demoArtist = demoFallback.getArtist(id)
        if (demoArtist != null) return demoArtist
        return try {
            val res = apiService.searchTracks(term = id, limit = 10)
            val tracks = res.results.map { mapDtoToTrack(it) }
            val first = tracks.firstOrNull() ?: return null
            Artist(
                id = id,
                name = first.artist,
                imageUrl = first.albumArtUrl,
                genre = first.genre,
                language = first.language,
                monthlyListeners = "24.5M",
                bio = "Popular charting music artist on FR Music.",
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
            val res = apiService.searchTracks(term = id, limit = 8)
            val tracks = res.results.map { mapDtoToTrack(it) }
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
            val res = apiService.searchTracks(term = genre, limit = 12)
            val tracks = res.results.map { mapDtoToTrack(it) }
            tracks.ifEmpty { demoFallback.getTracksByGenre(genre) }
        } catch (e: Exception) {
            demoFallback.getTracksByGenre(genre)
        }
    }
}
