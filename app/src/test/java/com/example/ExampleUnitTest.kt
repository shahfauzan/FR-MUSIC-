package com.example

import com.example.data.model.Track
import com.example.data.remote.DemoMusicProvider
import com.example.data.remote.youtube.YouTubeLinkResolver
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun track_durationFormatting_isCorrect() {
        val track = Track(
            id = "test_1",
            title = "Test Song",
            artist = "Arijit Singh",
            albumArtUrl = "",
            durationMs = 125000L // 2 minutes 5 seconds
        )
        assertEquals("2:05", track.durationFormatted)
    }

    @Test
    fun demoMusicProvider_returnsHindiAndEnglishTracks() = runBlocking {
        val provider = DemoMusicProvider()
        val hindi = provider.getHindiHits()
        val english = provider.getEnglishHits()
        val punjabi = provider.getPunjabiVibes()

        assertTrue("Should have Hindi tracks", hindi.isNotEmpty())
        assertTrue("Should have English tracks", english.isNotEmpty())
        assertTrue("Should have Punjabi tracks", punjabi.isNotEmpty())

        val arijitTrack = hindi.find { it.artist.contains("Arijit") }
        assertNotNull("Should contain Arijit Singh track", arijitTrack)
    }

    @Test
    fun demoMusicProvider_searchIndexHandlesFiltersAndTokens() = runBlocking {
        val provider = DemoMusicProvider()

        val diljitResults = provider.search("Diljit")
        assertTrue("Should find track or artist for Diljit", diljitResults.tracks.isNotEmpty() || diljitResults.artists.isNotEmpty())

        val allResults = provider.search("All")
        assertEquals(provider.catalogTracks.size, allResults.tracks.size)

        val newReleases = provider.search("New Releases")
        assertTrue("New Releases filter should return tracks", newReleases.tracks.isNotEmpty())

        val tokenSearch = provider.search("arijit romantic")
        assertTrue("Tokenized search should match Arijit Romantic tracks", tokenSearch.tracks.any { it.artist.contains("Arijit") })
    }

    @Test
    fun youtubeLinkResolver_extractsVideoIdAcrossFormats() {
        assertEquals("dQw4w9WgXcQ", YouTubeLinkResolver.extractVideoId("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", YouTubeLinkResolver.extractVideoId("https://youtu.be/dQw4w9WgXcQ?si=abc123"))
        assertEquals("dQw4w9WgXcQ", YouTubeLinkResolver.extractVideoId("https://music.youtube.com/watch?v=dQw4w9WgXcQ&list=RDAM"))
        assertEquals("dQw4w9WgXcQ", YouTubeLinkResolver.extractVideoId("https://youtube.com/shorts/dQw4w9WgXcQ"))
        assertNull(YouTubeLinkResolver.extractVideoId("arijit singh kesariya"))
    }

    @Test
    fun youtubeLinkResolver_cleansTitleAndArtistMetadata() {
        val (title, artist) = YouTubeLinkResolver.cleanMetadata(
            "Arijit Singh - Kesariya (Official Video) | Brahmastra",
            "SonyMusicIndiaVEVO"
        )
        assertEquals("Kesariya", title)
        assertEquals("Arijit Singh", artist)
    }
}
