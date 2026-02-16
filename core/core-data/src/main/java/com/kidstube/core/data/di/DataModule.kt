package com.kidstube.core.data.di

import com.kidstube.core.data.repository.SettingsRepositoryImpl
import com.kidstube.core.data.repository.VideoRepositoryImpl
import com.kidstube.core.domain.repository.SettingsRepository
import com.kidstube.core.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindVideoRepository(impl: VideoRepositoryImpl): VideoRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
