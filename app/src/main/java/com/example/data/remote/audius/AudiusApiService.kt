package com.example.data.remote.audius

import retrofit2.http.GET
import retrofit2.http.Query

interface AudiusApiService {

    @GET("v1/tracks/trending")
    suspend fun getTrendingTracks(
        @Query("genre") genre: String? = null,
        @Query("time") time: String = "week",
        @Query("limit") limit: Int = 15,
        @Query("app_name") appName: String = "FRMusic"
    ): AudiusTrackListResponse

    @GET("v1/tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("limit") limit: Int = 25,
        @Query("app_name") appName: String = "FRMusic"
    ): AudiusTrackListResponse
}
