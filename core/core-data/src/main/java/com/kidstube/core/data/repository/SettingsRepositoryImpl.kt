package com.kidstube.core.data.repository

import android.util.Base64
import com.kidstube.core.datastore.KidsTubePreferences
import com.kidstube.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferences: KidsTubePreferences
) : SettingsRepository {

    override fun getAllowedLanguages(): Flow<Set<String>> = preferences.getAllowedLanguages()

    override suspend fun setAllowedLanguages(languages: Set<String>) =
        preferences.setAllowedLanguages(languages)

    override fun getBlockedChannels(): Flow<Set<String>> = preferences.getBlockedChannels()

    override suspend fun addBlockedChannel(channelId: String, channelTitle: String) {
        preferences.addBlockedChannel(channelId)
        // Store name mapping
        val currentNames = preferences.getBlockedChannelNames().first()
        val namesMap = parseChannelNames(currentNames).toMutableMap()
        namesMap[channelId] = channelTitle
        preferences.setBlockedChannelNames(serializeChannelNames(namesMap))
    }

    override suspend fun removeBlockedChannel(channelId: String) {
        preferences.removeBlockedChannel(channelId)
        val currentNames = preferences.getBlockedChannelNames().first()
        val namesMap = parseChannelNames(currentNames).toMutableMap()
        namesMap.remove(channelId)
        preferences.setBlockedChannelNames(serializeChannelNames(namesMap))
    }

    override fun getBlockedChannelNames(): Flow<Map<String, String>> =
        preferences.getBlockedChannelNames().map { parseChannelNames(it) }

    override fun getFavoriteChannels(): Flow<Set<String>> = preferences.getFavoriteChannels()

    override suspend fun addFavoriteChannel(channelId: String) =
        preferences.addFavoriteChannel(channelId)

    override suspend fun removeFavoriteChannel(channelId: String) =
        preferences.removeFavoriteChannel(channelId)

    override fun getAllowUnknownLanguage(): Flow<Boolean> = preferences.getAllowUnknownLanguage()

    override suspend fun setAllowUnknownLanguage(allow: Boolean) =
        preferences.setAllowUnknownLanguage(allow)

    override fun isOnboardingComplete(): Flow<Boolean> = preferences.isOnboardingComplete()

    override suspend fun setOnboardingComplete(complete: Boolean) =
        preferences.setOnboardingComplete(complete)

    override suspend fun verifyPin(pin: String): Boolean = preferences.verifyPin(pin)

    override suspend fun setPin(pin: String) = preferences.setPin(pin)

    override suspend fun hasPin(): Boolean = preferences.hasPin().first()

    override fun isAutoPlayEnabled(): Flow<Boolean> = preferences.isAutoPlayEnabled()

    override suspend fun setAutoPlayEnabled(enabled: Boolean) =
        preferences.setAutoPlayEnabled(enabled)

    override fun getQuotaUsedToday(): Flow<Int> = preferences.getQuotaUsedToday()

    override suspend fun incrementQuotaUsed(units: Int) = preferences.incrementQuotaUsed(units)

    override suspend fun resetQuotaIfNewDay() = preferences.resetQuotaIfNewDay()

    private fun parseChannelNames(serialized: String): Map<String, String> {
        if (serialized.isBlank()) return emptyMap()
        return serialized.split(";").mapNotNull { entry ->
            val parts = entry.split("=", limit = 2)
            if (parts.size == 2) {
                val id = decodeBase64(parts[0])
                val name = decodeBase64(parts[1])
                if (id != null && name != null) id to name else null
            } else null
        }.toMap()
    }

    private fun serializeChannelNames(map: Map<String, String>): String =
        map.entries.joinToString(";") { "${encodeBase64(it.key)}=${encodeBase64(it.value)}" }

    private fun encodeBase64(value: String): String =
        Base64.encodeToString(value.toByteArray(), Base64.NO_WRAP)

    private fun decodeBase64(value: String): String? = try {
        String(Base64.decode(value, Base64.NO_WRAP))
    } catch (_: Exception) {
        // Fallback for old unencoded data
        value
    }
}
