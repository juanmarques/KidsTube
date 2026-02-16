package com.kidstube.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_videos")
data class CachedVideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelId: String,
    val channelTitle: String,
    val publishedAt: String,
    val defaultLanguage: String?,
    val defaultAudioLanguage: String?,
    val duration: String?,
    val madeForKids: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)
