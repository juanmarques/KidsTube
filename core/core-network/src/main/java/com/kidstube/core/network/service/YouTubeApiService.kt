package com.kidstube.core.network.service

import com.kidstube.core.network.dto.SearchResponse
import com.kidstube.core.network.dto.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("safeSearch") safeSearch: String = "strict",
        @Query("relevanceLanguage") relevanceLanguage: String? = null,
        @Query("regionCode") regionCode: String? = null,
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String
    ): SearchResponse

    @GET("youtube/v3/videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "snippet,contentDetails,status",
        @Query("id") ids: String,
        @Query("key") apiKey: String
    ): VideoListResponse
}
