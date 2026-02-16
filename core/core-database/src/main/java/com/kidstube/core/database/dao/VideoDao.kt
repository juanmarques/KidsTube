package com.kidstube.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidstube.core.database.entity.CachedVideoEntity

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<CachedVideoEntity>)

    @Query("SELECT * FROM cached_videos WHERE id = :videoId")
    suspend fun getVideoById(videoId: String): CachedVideoEntity?

    @Query("SELECT * FROM cached_videos WHERE id IN (:videoIds)")
    suspend fun getVideosByIds(videoIds: List<String>): List<CachedVideoEntity>

    @Query("DELETE FROM cached_videos WHERE cachedAt < :olderThan")
    suspend fun deleteOldCache(olderThan: Long)

    @Query("SELECT * FROM cached_videos ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getAllCachedVideos(limit: Int = 200): List<CachedVideoEntity>
}
