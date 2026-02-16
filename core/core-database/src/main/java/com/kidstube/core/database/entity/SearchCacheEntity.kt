package com.kidstube.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_cache")
data class SearchCacheEntity(
    @PrimaryKey val query: String,
    val videoIds: String, // comma-separated video IDs
    val cachedAt: Long = System.currentTimeMillis()
) {
    fun getVideoIdList(): List<String> = videoIds.split(",").filter { it.isNotBlank() }

    companion object {
        const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L // 24 hours

        fun fromVideoIds(query: String, ids: List<String>): SearchCacheEntity =
            SearchCacheEntity(
                query = query,
                videoIds = ids.joinToString(",")
            )
    }
}
