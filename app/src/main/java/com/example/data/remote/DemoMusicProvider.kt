package com.example.data.remote

import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.SearchResults
import com.example.data.model.Track

/**
 * Demo and Offline Music Provider.
 * Provides curated Hindi, English, Punjabi, Bollywood, and Lo-Fi tracks with
 * verified, royalty-free audio streams for instant playback out-of-the-box.
 */
class DemoMusicProvider : MusicProvider {
    override val providerName: String = "FR Curated Demo Mode"
    override val isDemoMode: Boolean = true

    // Verified legal royalty-free stream samples (SoundHelix & Wikimedia Creative Commons audio streams)
    private val sampleAudioUrls = listOf(
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3"
    )

    private val allTracks: List<Track> = listOf(
        // Hindi & Bollywood Hits
        Track(
            id = "hi_01",
            title = "Tum Hi Ho (Soul Acoustic)",
            artist = "Arijit Singh",
            artistId = "art_arijit",
            album = "Aashiqui 2 Essentials",
            albumId = "alb_aashiqui",
            albumArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[0],
            durationMs = 30000L,
            genre = "Romantic",
            language = "Hindi",
            releaseYear = "2023",
            playsCount = 4280000L
        ),
        Track(
            id = "hi_02",
            title = "Kesariya (Rhythm Lounge)",
            artist = "Arijit Singh & Pritam",
            artistId = "art_arijit",
            album = "Brahmastra Soundscapes",
            albumId = "alb_brahmastra",
            albumArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[1],
            durationMs = 30000L,
            genre = "Bollywood",
            language = "Hindi",
            releaseYear = "2024",
            playsCount = 3910000L
        ),
        Track(
            id = "hi_03",
            title = "Raataan Lambiyan (Twilight Remix)",
            artist = "Jubin Nautiyal & Asees Kaur",
            artistId = "art_jubin",
            album = "Shershaah Vibes",
            albumId = "alb_shershaah",
            albumArtUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[2],
            durationMs = 30000L,
            genre = "Romantic",
            language = "Hindi",
            releaseYear = "2023",
            playsCount = 3120000L
        ),
        Track(
            id = "hi_04",
            title = "Ghungroo (Retro Groove)",
            artist = "Arijit Singh & Shilpa Rao",
            artistId = "art_arijit",
            album = "War Groove Edition",
            albumId = "alb_war",
            albumArtUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[3],
            durationMs = 30000L,
            genre = "Hindi Pop",
            language = "Hindi",
            releaseYear = "2022",
            playsCount = 2890000L
        ),
        Track(
            id = "hi_05",
            title = "Apna Bana Le",
            artist = "Arijit Singh & Sachin-Jigar",
            artistId = "art_arijit",
            album = "Bhediya Acoustic",
            albumId = "alb_bhediya",
            albumArtUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[4],
            durationMs = 30000L,
            genre = "Romantic",
            language = "Hindi",
            releaseYear = "2023",
            playsCount = 3450000L
        ),
        Track(
            id = "hi_06",
            title = "Pasoori Nu (Urban Chill)",
            artist = "Arijit Singh & Tulsi Kumar",
            artistId = "art_arijit",
            album = "Satyaprem Ki Katha",
            albumId = "alb_satyaprem",
            albumArtUrl = "https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[5],
            durationMs = 30000L,
            genre = "Indie Hindi",
            language = "Hindi",
            releaseYear = "2023",
            playsCount = 2750000L
        ),

        // Punjabi Vibes
        Track(
            id = "pb_01",
            title = "Lover (Neon Pulse)",
            artist = "Diljit Dosanjh",
            artistId = "art_diljit",
            album = "MoonChild Era",
            albumId = "alb_moonchild",
            albumArtUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[0],
            durationMs = 30000L,
            genre = "Punjabi Pop",
            language = "Punjabi",
            releaseYear = "2023",
            playsCount = 5120000L
        ),
        Track(
            id = "pb_02",
            title = "Brown Munde (Bass Anthem)",
            artist = "AP Dhillon & Gurinder Gill",
            artistId = "art_apdhillon",
            album = "Not by Chance",
            albumId = "alb_notbychance",
            albumArtUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[1],
            durationMs = 30000L,
            genre = "Punjabi Hip-Hop",
            language = "Punjabi",
            releaseYear = "2022",
            playsCount = 6840000L
        ),
        Track(
            id = "pb_03",
            title = "Softly (Midnight Drive)",
            artist = "Karan Aujla & Ikky",
            artistId = "art_karan",
            album = "Making Memories",
            albumId = "alb_makingmemories",
            albumArtUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[2],
            durationMs = 30000L,
            genre = "Punjabi Pop",
            language = "Punjabi",
            releaseYear = "2024",
            playsCount = 4900000L
        ),
        Track(
            id = "pb_04",
            title = "295 (Tribute Session)",
            artist = "Sidhu Moosewala",
            artistId = "art_sidhu",
            album = "Moosetape Deluxe",
            albumId = "alb_moosetape",
            albumArtUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[3],
            durationMs = 30000L,
            genre = "Punjabi Hip-Hop",
            language = "Punjabi",
            releaseYear = "2021",
            playsCount = 8200000L
        ),
        Track(
            id = "pb_05",
            title = "Excuses (Acoustic Sunset)",
            artist = "AP Dhillon & Intense",
            artistId = "art_apdhillon",
            album = "Hidden Gems",
            albumId = "alb_hiddengems",
            albumArtUrl = "https://images.unsplash.com/photo-1526478806334-5fd488fcaabc?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[4],
            durationMs = 30000L,
            genre = "Bhangra",
            language = "Punjabi",
            releaseYear = "2023",
            playsCount = 5600000L
        ),

        // English Hits & Pop
        Track(
            id = "en_01",
            title = "Blinding Lights (After Hours Synth)",
            artist = "The Weeknd",
            artistId = "art_weeknd",
            album = "After Hours",
            albumId = "alb_afterhours",
            albumArtUrl = "https://images.unsplash.com/photo-1571266028243-3716f02d2d2e?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[5],
            durationMs = 30000L,
            genre = "Pop",
            language = "English",
            releaseYear = "2020",
            playsCount = 9400000L
        ),
        Track(
            id = "en_02",
            title = "Levitating (Cosmic Disco)",
            artist = "Dua Lipa",
            artistId = "art_dualipa",
            album = "Future Nostalgia",
            albumId = "alb_futurenostalgia",
            albumArtUrl = "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[0],
            durationMs = 30000L,
            genre = "Pop",
            language = "English",
            releaseYear = "2021",
            playsCount = 7800000L
        ),
        Track(
            id = "en_03",
            title = "Cruel Summer (Electric Glow)",
            artist = "Taylor Swift",
            artistId = "art_taylor",
            album = "Lover",
            albumId = "alb_lover",
            albumArtUrl = "https://images.unsplash.com/photo-1520523839898-5071282543e1?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[1],
            durationMs = 30000L,
            genre = "Pop",
            language = "English",
            releaseYear = "2023",
            playsCount = 8900000L
        ),
        Track(
            id = "en_04",
            title = "Starboy (Cyber Funk)",
            artist = "The Weeknd & Daft Punk",
            artistId = "art_weeknd",
            album = "Starboy Deluxe",
            albumId = "alb_starboy",
            albumArtUrl = "https://images.unsplash.com/photo-1598387181032-a3103a2db5b3?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[2],
            durationMs = 30000L,
            genre = "R&B",
            language = "English",
            releaseYear = "2022",
            playsCount = 7600000L
        ),
        Track(
            id = "en_05",
            title = "Sunflower (Spider Verse Vibes)",
            artist = "Post Malone & Swae Lee",
            artistId = "art_postmalone",
            album = "Hollywood's Bleeding",
            albumId = "alb_hollywood",
            albumArtUrl = "https://images.unsplash.com/photo-1506157786151-b8491531f063?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[3],
            durationMs = 30000L,
            genre = "Hip-Hop",
            language = "English",
            releaseYear = "2020",
            playsCount = 8300000L
        ),

        // Lo-Fi & Chill Beats
        Track(
            id = "lofi_01",
            title = "Midnight Chai (Mumbai Rain)",
            artist = "FR Chill Collective",
            artistId = "art_frchill",
            album = "Monsoon Lo-Fi Vol. 1",
            albumId = "alb_monsoonlofi",
            albumArtUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[4],
            durationMs = 30000L,
            genre = "Lo-fi",
            language = "Hindi",
            releaseYear = "2024",
            playsCount = 1450000L
        ),
        Track(
            id = "lofi_02",
            title = "Tokyo Neon Slumber",
            artist = "Aesthetic Beats",
            artistId = "art_aesthetic",
            album = "Midnight Express",
            albumId = "alb_midnightexpress",
            albumArtUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
            previewUrl = sampleAudioUrls[5],
            durationMs = 30000L,
            genre = "Lo-fi",
            language = "English",
            releaseYear = "2024",
            playsCount = 1890000L
        )
    )

    private val allArtists: List<Artist> = listOf(
        Artist(
            id = "art_arijit",
            name = "Arijit Singh",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            genre = "Bollywood & Romantic",
            language = "Hindi",
            monthlyListeners = "38.4M",
            bio = "The undisputed voice of modern Bollywood romantic anthems, delivering poignant melodies and record-breaking blockbusters worldwide."
        ),
        Artist(
            id = "art_diljit",
            name = "Diljit Dosanjh",
            imageUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            genre = "Punjabi Pop & Fusion",
            language = "Punjabi",
            monthlyListeners = "21.6M",
            bio = "Global icon, Coachella performer, and Punjabi sensation blending traditional folk rhythms with cutting-edge pop production."
        ),
        Artist(
            id = "art_apdhillon",
            name = "AP Dhillon",
            imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            genre = "Punjabi Hip-Hop & Trap",
            language = "Punjabi",
            monthlyListeners = "18.2M",
            bio = "Pioneer of modern brown wave music, crafting hypnotic basslines and stadium-sized hooks."
        ),
        Artist(
            id = "art_weeknd",
            name = "The Weeknd",
            imageUrl = "https://images.unsplash.com/photo-1571266028243-3716f02d2d2e?w=600&auto=format&fit=crop&q=80",
            genre = "R&B & Pop",
            language = "English",
            monthlyListeners = "104.5M",
            bio = "Multi-platinum superstar defining the sound of 80s synth-wave resurgence and cinematic dark pop."
        ),
        Artist(
            id = "art_dualipa",
            name = "Dua Lipa",
            imageUrl = "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=600&auto=format&fit=crop&q=80",
            genre = "Dance Pop & Disco",
            language = "English",
            monthlyListeners = "72.1M",
            bio = "Grammy-winning queen of modern dance-pop, bringing euphoric club energy to global airwaves."
        ),
        Artist(
            id = "art_taylor",
            name = "Taylor Swift",
            imageUrl = "https://images.unsplash.com/photo-1520523839898-5071282543e1?w=600&auto=format&fit=crop&q=80",
            genre = "Pop & Indie",
            language = "English",
            monthlyListeners = "98.9M",
            bio = "Generational songwriter and cultural phenomenon known for deeply personal storytelling and record-shattering eras."
        )
    )

    private val allAlbums: List<Album> = listOf(
        Album(
            id = "alb_aashiqui",
            title = "Aashiqui 2 Essentials",
            artist = "Arijit Singh",
            artistId = "art_arijit",
            coverArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            releaseYear = "2023",
            trackCount = 10,
            genre = "Romantic",
            language = "Hindi"
        ),
        Album(
            id = "alb_moonchild",
            title = "MoonChild Era",
            artist = "Diljit Dosanjh",
            artistId = "art_diljit",
            coverArtUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            releaseYear = "2023",
            trackCount = 9,
            genre = "Punjabi Pop",
            language = "Punjabi"
        ),
        Album(
            id = "alb_afterhours",
            title = "After Hours",
            artist = "The Weeknd",
            artistId = "art_weeknd",
            coverArtUrl = "https://images.unsplash.com/photo-1571266028243-3716f02d2d2e?w=600&auto=format&fit=crop&q=80",
            releaseYear = "2020",
            trackCount = 14,
            genre = "Pop",
            language = "English"
        )
    )

    private val allPlaylists: List<Playlist> = listOf(
        Playlist(
            id = "pl_hindi_hits",
            title = "Hindi Hits 2024",
            description = "The hottest chartbusters from Bollywood and Indie Hindi music right now.",
            coverArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            trackCount = 12,
            language = "Hindi"
        ),
        Playlist(
            id = "pl_punjabi_vibes",
            title = "Punjabi Vibes & Swag",
            description = "High energy Punjabi bangers, bass heavy beats and lyrical flow.",
            coverArtUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            trackCount = 10,
            language = "Punjabi"
        ),
        Playlist(
            id = "pl_global_pop",
            title = "English Chart Toppers",
            description = "Top English pop, dance, and R&B anthems conquering global charts.",
            coverArtUrl = "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=600&auto=format&fit=crop&q=80",
            trackCount = 15,
            language = "English"
        ),
        Playlist(
            id = "pl_lofi_monsoon",
            title = "Monsoon Lo-Fi Coffee",
            description = "Gentle rain sounds, warm keys, and peaceful acoustic instrumentals.",
            coverArtUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=600&auto=format&fit=crop&q=80",
            trackCount = 8,
            language = "All"
        )
    )

    override suspend fun search(query: String): SearchResults {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return SearchResults()

        val matchedTracks = allTracks.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.album.lowercase().contains(q) ||
            it.genre.lowercase().contains(q) ||
            it.language.lowercase().contains(q)
        }

        val matchedArtists = allArtists.filter {
            it.name.lowercase().contains(q) || it.genre.lowercase().contains(q)
        }

        val matchedAlbums = allAlbums.filter {
            it.title.lowercase().contains(q) || it.artist.lowercase().contains(q)
        }

        val matchedPlaylists = allPlaylists.filter {
            it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
        }

        return SearchResults(
            tracks = matchedTracks,
            artists = matchedArtists,
            albums = matchedAlbums,
            playlists = matchedPlaylists
        )
    }

    override suspend fun getTrending(): List<Track> = allTracks.shuffled().take(8)

    override suspend fun getHindiHits(): List<Track> =
        allTracks.filter { it.language.equals("Hindi", ignoreCase = true) }

    override suspend fun getEnglishHits(): List<Track> =
        allTracks.filter { it.language.equals("English", ignoreCase = true) }

    override suspend fun getBollywoodEssentials(): List<Track> =
        allTracks.filter { it.genre.contains("Bollywood", ignoreCase = true) || it.genre.contains("Romantic", ignoreCase = true) }

    override suspend fun getPunjabiVibes(): List<Track> =
        allTracks.filter { it.language.equals("Punjabi", ignoreCase = true) }

    override suspend fun getRecommendations(seedGenre: String?): List<Track> =
        if (seedGenre != null) {
            allTracks.filter { it.genre.contains(seedGenre, ignoreCase = true) }
        } else {
            allTracks.shuffled().take(6)
        }

    override suspend fun getArtist(id: String): Artist? {
        val artist = allArtists.find { it.id == id } ?: return null
        val artistTracks = allTracks.filter { it.artistId == id || it.artist.contains(artist.name) }
        val artistAlbums = allAlbums.filter { it.artistId == id }
        return artist.copy(topTracks = artistTracks, albums = artistAlbums)
    }

    override suspend fun getAlbum(id: String): Album? {
        val album = allAlbums.find { it.id == id } ?: return null
        val albumTracks = allTracks.filter { it.albumId == id }
        return album.copy(tracks = albumTracks.ifEmpty { allTracks.take(4) })
    }

    override suspend fun getPlaylist(id: String): Playlist? {
        val playlist = allPlaylists.find { it.id == id } ?: return null
        val tracks = when (id) {
            "pl_hindi_hits" -> getHindiHits()
            "pl_punjabi_vibes" -> getPunjabiVibes()
            "pl_global_pop" -> getEnglishHits()
            "pl_lofi_monsoon" -> allTracks.filter { it.genre == "Lo-fi" }
            else -> allTracks.shuffled().take(6)
        }
        return playlist.copy(tracks = tracks)
    }

    override suspend fun getTrack(id: String): Track? = allTracks.find { it.id == id }

    override suspend fun getTracksByGenre(genre: String): List<Track> {
        return allTracks.filter {
            it.genre.contains(genre, ignoreCase = true) ||
            it.language.contains(genre, ignoreCase = true)
        }
    }
}
