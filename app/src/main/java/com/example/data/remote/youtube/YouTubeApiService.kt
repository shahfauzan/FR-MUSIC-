package com.example.data.remote.youtube

import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("youtube/v3/search")
    suspend fun searchMusicVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("videoCategoryId") videoCategoryId: String = "10", // 10 = Music Category
        @Query("maxResults") maxResults: Int = 15,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}
