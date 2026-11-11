package com.example.data.remote.youtube

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeSearchResponse(
    @Json(name = "items") val items: List<YouTubeSearchResultItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class YouTubeSearchResultItem(
    @Json(name = "id") val id: YouTubeResourceId?,
    @Json(name = "snippet") val snippet: YouTubeSnippet?
)

@JsonClass(generateAdapter = true)
data class YouTubeResourceId(
    @Json(name = "videoId") val videoId: String?
)

@JsonClass(generateAdapter = true)
data class YouTubeSnippet(
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "channelTitle") val channelTitle: String?,
    @Json(name = "publishedAt") val publishedAt: String?,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnails?
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnails(
    @Json(name = "high") val high: YouTubeThumbnailDetail?,
    @Json(name = "medium") val medium: YouTubeThumbnailDetail?,
    @Json(name = "default") val default: YouTubeThumbnailDetail?
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnailDetail(
    @Json(name = "url") val url: String?
)
