package com.kidstube.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kidstube.core.database.dao.SearchCacheDao
import com.kidstube.core.database.dao.VideoDao
import com.kidstube.core.database.entity.CachedVideoEntity
import com.kidstube.core.database.entity.SearchCacheEntity

@Database(
    entities = [
        CachedVideoEntity::class,
        SearchCacheEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class KidsTubeDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun searchCacheDao(): SearchCacheDao
}
