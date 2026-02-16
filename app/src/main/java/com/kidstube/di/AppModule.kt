package com.kidstube.di

import com.kidstube.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("youtube_api_key")
    fun provideYouTubeApiKey(): String = BuildConfig.YOUTUBE_API_KEY
}
