package com.example.data.remote.itunes

import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 25
    ): ItunesSearchResponse
}
