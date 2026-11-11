package com.example

import com.example.data.model.Track
import com.example.data.remote.DemoMusicProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    fun demoMusicProvider_searchWorksCorrectly() = runBlocking {
        val provider = DemoMusicProvider()
        val results = provider.search("Diljit")

        assertTrue("Should find track or artist for Diljit", results.tracks.isNotEmpty() || results.artists.isNotEmpty())
    }
}
