package com.example.data.remote.audius

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AudiusTrackListResponse(
    @param:Json(name = "data") val data: List<AudiusTrackDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AudiusTrackDto(
    @param:Json(name = "id") val id: String?,
    @param:Json(name = "title") val title: String?,
    @param:Json(name = "duration") val duration: Int?,
    @param:Json(name = "genre") val genre: String?,
    @param:Json(name = "mood") val mood: String?,
    @param:Json(name = "release_date") val releaseDate: String?,
    @param:Json(name = "play_count") val playCount: Long?,
    @param:Json(name = "artwork") val artwork: AudiusArtworkDto?,
    @param:Json(name = "user") val user: AudiusUserDto?
)

@JsonClass(generateAdapter = true)
data class AudiusArtworkDto(
    @param:Json(name = "150x150") val x150: String?,
    @param:Json(name = "480x480") val x480: String?,
    @param:Json(name = "1000x1000") val x1000: String?
)

@JsonClass(generateAdapter = true)
data class AudiusUserDto(
    @param:Json(name = "id") val id: String?,
    @param:Json(name = "name") val name: String?,
    @param:Json(name = "handle") val handle: String?,
    @param:Json(name = "profile_picture") val profilePicture: AudiusArtworkDto?
)
