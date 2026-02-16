package com.kidstube.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getAllowedLanguages(): Flow<Set<String>>
    suspend fun setAllowedLanguages(languages: Set<String>)

    fun getBlockedChannels(): Flow<Set<String>>
    suspend fun addBlockedChannel(channelId: String, channelTitle: String)
    suspend fun removeBlockedChannel(channelId: String)
    fun getBlockedChannelNames(): Flow<Map<String, String>>

    fun getFavoriteChannels(): Flow<Set<String>>
    suspend fun addFavoriteChannel(channelId: String)
    suspend fun removeFavoriteChannel(channelId: String)

    fun getAllowUnknownLanguage(): Flow<Boolean>
    suspend fun setAllowUnknownLanguage(allow: Boolean)

    fun isOnboardingComplete(): Flow<Boolean>
    suspend fun setOnboardingComplete(complete: Boolean)

    suspend fun verifyPin(pin: String): Boolean
    suspend fun setPin(pin: String)
    suspend fun hasPin(): Boolean

    fun isAutoPlayEnabled(): Flow<Boolean>
    suspend fun setAutoPlayEnabled(enabled: Boolean)

    fun getQuotaUsedToday(): Flow<Int>
    suspend fun incrementQuotaUsed(units: Int)
    suspend fun resetQuotaIfNewDay()
}
