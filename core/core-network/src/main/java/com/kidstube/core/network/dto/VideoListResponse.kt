package com.kidstube.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoListResponse(
    @Json(name = "items") val items: List<VideoItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class VideoItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "snippet") val snippet: VideoSnippet = VideoSnippet(),
    @Json(name = "contentDetails") val contentDetails: ContentDetails? = null,
    @Json(name = "status") val status: VideoStatus? = null
)

@JsonClass(generateAdapter = true)
data class VideoSnippet(
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "channelId") val channelId: String = "",
    @Json(name = "channelTitle") val channelTitle: String = "",
    @Json(name = "publishedAt") val publishedAt: String = "",
    @Json(name = "thumbnails") val thumbnails: Thumbnails? = null,
    @Json(name = "defaultLanguage") val defaultLanguage: String? = null,
    @Json(name = "defaultAudioLanguage") val defaultAudioLanguage: String? = null
)

@JsonClass(generateAdapter = true)
data class VideoStatus(
    @Json(name = "madeForKids") val madeForKids: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ContentDetails(
    @Json(name = "duration") val duration: String? = null
)
