package com.kidstube.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidstube.core.database.entity.SearchCacheEntity

@Dao
interface SearchCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchCache(cache: SearchCacheEntity)

    @Query("SELECT * FROM search_cache WHERE `query` = :query AND cachedAt > :minTime")
    suspend fun getSearchCache(query: String, minTime: Long): SearchCacheEntity?

    @Query("DELETE FROM search_cache WHERE cachedAt < :olderThan")
    suspend fun deleteOldCache(olderThan: Long)
}
