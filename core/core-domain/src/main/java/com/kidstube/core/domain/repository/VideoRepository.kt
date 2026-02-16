package com.kidstube.core.domain.repository

import com.kidstube.core.domain.model.Video

data class FeedChannel(
    val channelId: String,
    val languageCode: String,
    val isPriority: Boolean
)

interface VideoRepository {
    suspend fun searchVideos(
        query: String,
        allowedLanguages: Set<String>,
        allowUnknownLanguage: Boolean,
        blockedChannels: Set<String>,
        relevanceLanguage: String?,
        regionCode: String? = null,
        forceRefresh: Boolean = false
    ): Result<List<Video>>

    suspend fun getVideoById(videoId: String): Result<Video?>

    suspend fun getRelatedVideos(
        videoId: String,
        allowedLanguages: Set<String>,
        allowUnknownLanguage: Boolean,
        blockedChannels: Set<String>
    ): Result<List<Video>>

    suspend fun getChannelFeed(
        channelId: String,
        languageCode: String
    ): Result<List<Video>>

    suspend fun getAllCachedVideos(
        allowedLanguages: Set<String>,
        allowUnknownLanguage: Boolean,
        blockedChannels: Set<String>
    ): List<Video>

    suspend fun getRssFeedChannels(allowedLanguages: Set<String>): List<FeedChannel>
}
