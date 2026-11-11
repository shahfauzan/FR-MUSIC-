package com.example.data.remote

import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track

/**
 * Music Provider Abstraction Layer.
 * Allows swapping between legal preview APIs (e.g. iTunes Search, Deezer preview)
 * and curated royalty-free demo providers without rebuilding the UI.
 */
interface MusicProvider {
    val providerName: String
    val isDemoMode: Boolean

    suspend fun search(query: String): SearchResults
    suspend fun getTrending(): List<Track>
    suspend fun getHindiHits(): List<Track>
    suspend fun getEnglishHits(): List<Track>
    suspend fun getBollywoodEssentials(): List<Track>
    suspend fun getPunjabiVibes(): List<Track>
    suspend fun getRecommendations(seedGenre: String? = null): List<Track>
    suspend fun getArtist(id: String): Artist?
    suspend fun getAlbum(id: String): Album?
    suspend fun getPlaylist(id: String): Playlist?
    suspend fun getTrack(id: String): Track?
    suspend fun getTracksByGenre(genre: String): List<Track>
}
