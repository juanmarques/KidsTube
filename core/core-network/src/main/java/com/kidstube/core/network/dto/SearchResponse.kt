package com.kidstube.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @Json(name = "items") val items: List<SearchItem> = emptyList(),
    @Json(name = "nextPageToken") val nextPageToken: String? = null
)

@JsonClass(generateAdapter = true)
data class SearchItem(
    @Json(name = "id") val id: SearchItemId,
    @Json(name = "snippet") val snippet: SearchSnippet
)

@JsonClass(generateAdapter = true)
data class SearchItemId(
    @Json(name = "videoId") val videoId: String? = null
)

@JsonClass(generateAdapter = true)
data class SearchSnippet(
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "channelId") val channelId: String = "",
    @Json(name = "channelTitle") val channelTitle: String = "",
    @Json(name = "publishedAt") val publishedAt: String = "",
    @Json(name = "thumbnails") val thumbnails: Thumbnails? = null
)

@JsonClass(generateAdapter = true)
data class Thumbnails(
    @Json(name = "medium") val medium: Thumbnail? = null,
    @Json(name = "high") val high: Thumbnail? = null
)

@JsonClass(generateAdapter = true)
data class Thumbnail(
    @Json(name = "url") val url: String = ""
)
