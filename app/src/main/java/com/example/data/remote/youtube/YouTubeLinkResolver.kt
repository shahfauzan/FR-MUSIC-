package com.example.data.remote.youtube

import com.example.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class ResolvedYouTubeLink(
    val videoId: String,
    val originalUrl: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val matchedAudioTrack: Track? = null
)

/**
 * Parses YouTube & YouTube Music links and resolves official metadata via YouTube oEmbed.
 * Supports playback inside the app via official YouTube Embed Player and pairs with
 * matched open-API audio tracks for optional background playback.
 */
object YouTubeLinkResolver {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    private val videoIdRegexList = listOf(
        Regex("""(?:youtube\.com|music\.youtube\.com)/watch\?(?:.*&)?v=([A-Za-z0-9_-]{11})"""),
        Regex("""youtu\.be/([A-Za-z0-9_-]{11})"""),
        Regex("""(?:youtube\.com|music\.youtube\.com)/shorts/([A-Za-z0-9_-]{11})"""),
        Regex("""(?:youtube\.com|music\.youtube\.com)/embed/([A-Za-z0-9_-]{11})"""),
        Regex("""(?:youtube\.com|music\.youtube\.com)/live/([A-Za-z0-9_-]{11})""")
    )

    fun extractVideoId(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null
        for (regex in videoIdRegexList) {
            val match = regex.find(trimmed)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1]
            }
        }
        return null
    }

    fun isYouTubeUrl(input: String): Boolean = extractVideoId(input) != null

    fun cleanMetadata(rawTitle: String, authorName: String): Pair<String, String> {
        val cleanChannel = authorName
            .replace(" - Topic", "", ignoreCase = true)
            .replace("Official", "", ignoreCase = true)
            .trim()
            .ifBlank { "YouTube Artist" }

        val cleanedTitle = rawTitle
            .replace(Regex("""\(Official.*?\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\[Official.*?\]""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\(Lyric.*?\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\[Lyric.*?\]""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\(Full Audio.*?\)""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\|.*$"""), "")
            .trim()

        val split = cleanedTitle.split(" - ", " – ")
        return if (split.size >= 2) {
            val parsedArtist = split[0].trim().ifBlank { cleanChannel }
            val parsedTitle = split.drop(1).joinToString(" - ").trim().ifBlank { cleanedTitle }
            parsedTitle to parsedArtist
        } else {
            cleanedTitle.ifBlank { rawTitle } to cleanChannel
        }
    }

    suspend fun resolve(url: String): ResolvedYouTubeLink? = withContext(Dispatchers.IO) {
        val videoId = extractVideoId(url) ?: return@withContext null
        val watchUrl = "https://www.youtube.com/watch?v=$videoId"
        val fallbackThumb = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

        try {
            val encoded = URLEncoder.encode(watchUrl, "UTF-8")
            val oembedUrl = "https://www.youtube.com/oembed?url=$encoded&format=json"
            val request = Request.Builder()
                .url(oembedUrl)
                .header("User-Agent", "FRMusic/1.0 (Android)")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    if (body.isNotBlank()) {
                        val json = JSONObject(body)
                        val rawTitle = json.optString("title", "YouTube Track")
                        val author = json.optString("author_name", "YouTube Artist")
                        val thumb = json.optString("thumbnail_url", fallbackThumb).ifBlank { fallbackThumb }
                        val (title, artist) = cleanMetadata(rawTitle, author)
                        return@withContext ResolvedYouTubeLink(
                            videoId = videoId,
                            originalUrl = watchUrl,
                            title = title,
                            artist = artist,
                            thumbnailUrl = thumb
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Fall back gracefully to basic resolved videoId link
        }

        ResolvedYouTubeLink(
            videoId = videoId,
            originalUrl = watchUrl,
            title = "YouTube Track ($videoId)",
            artist = "YouTube Music",
            thumbnailUrl = fallbackThumb
        )
    }

    fun buildEmbedHtml(videoId: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                        width: 100%;
                        height: 100%;
                        background-color: #0F172A;
                        overflow: hidden;
                    }
                    iframe {
                        width: 100%;
                        height: 100%;
                        border: 0;
                    }
                </style>
            </head>
            <body>
                <iframe
                    src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&rel=0&modestbranding=1&enablejsapi=1"
                    title="FR Music YouTube Player"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen>
                </iframe>
            </body>
            </html>
        """.trimIndent()
    }
}
