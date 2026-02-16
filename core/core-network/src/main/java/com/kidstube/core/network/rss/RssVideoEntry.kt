package com.kidstube.core.network.rss

data class RssVideoEntry(
    val videoId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelId: String,
    val channelTitle: String,
    val publishedAt: String
)
