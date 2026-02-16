package com.kidstube.core.domain.usecase

import com.kidstube.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    fun getAllowedLanguages(): Flow<Set<String>> = settingsRepository.getAllowedLanguages()
    suspend fun setAllowedLanguages(languages: Set<String>) = settingsRepository.setAllowedLanguages(languages)

    fun getBlockedChannels(): Flow<Set<String>> = settingsRepository.getBlockedChannels()
    fun getBlockedChannelNames(): Flow<Map<String, String>> = settingsRepository.getBlockedChannelNames()
    suspend fun addBlockedChannel(channelId: String, channelTitle: String) =
        settingsRepository.addBlockedChannel(channelId, channelTitle)
    suspend fun removeBlockedChannel(channelId: String) = settingsRepository.removeBlockedChannel(channelId)

    fun getFavoriteChannels(): Flow<Set<String>> = settingsRepository.getFavoriteChannels()
    suspend fun addFavoriteChannel(channelId: String) = settingsRepository.addFavoriteChannel(channelId)
    suspend fun removeFavoriteChannel(channelId: String) = settingsRepository.removeFavoriteChannel(channelId)

    fun getAllowUnknownLanguage(): Flow<Boolean> = settingsRepository.getAllowUnknownLanguage()
    suspend fun setAllowUnknownLanguage(allow: Boolean) = settingsRepository.setAllowUnknownLanguage(allow)

    suspend fun completeOnboarding() = settingsRepository.setOnboardingComplete(true)

    fun isAutoPlayEnabled(): Flow<Boolean> = settingsRepository.isAutoPlayEnabled()
    suspend fun setAutoPlayEnabled(enabled: Boolean) = settingsRepository.setAutoPlayEnabled(enabled)

    fun getQuotaUsedToday(): Flow<Int> = settingsRepository.getQuotaUsedToday()
}
