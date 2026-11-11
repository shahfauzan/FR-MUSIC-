package com.example.data.remote.itunes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItunesSearchResponse(
    @Json(name = "resultCount") val resultCount: Int,
    @Json(name = "results") val results: List<ItunesTrackDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ItunesTrackDto(
    @Json(name = "trackId") val trackId: Long?,
    @Json(name = "trackName") val trackName: String?,
    @Json(name = "artistName") val artistName: String?,
    @Json(name = "artistId") val artistId: Long?,
    @Json(name = "collectionName") val collectionName: String?,
    @Json(name = "collectionId") val collectionId: Long?,
    @Json(name = "artworkUrl100") val artworkUrl100: String?,
    @Json(name = "previewUrl") val previewUrl: String?,
    @Json(name = "trackTimeMillis") val trackTimeMillis: Long?,
    @Json(name = "primaryGenreName") val primaryGenreName: String?,
    @Json(name = "releaseDate") val releaseDate: String?
)
