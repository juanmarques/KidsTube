package com.kidstube.core.database.di

import android.content.Context
import androidx.room.Room
import com.kidstube.core.database.KidsTubeDatabase
import com.kidstube.core.database.dao.SearchCacheDao
import com.kidstube.core.database.dao.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KidsTubeDatabase =
        Room.databaseBuilder(
            context,
            KidsTubeDatabase::class.java,
            "kidstube_db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideVideoDao(db: KidsTubeDatabase): VideoDao = db.videoDao()

    @Provides
    fun provideSearchCacheDao(db: KidsTubeDatabase): SearchCacheDao = db.searchCacheDao()
}
