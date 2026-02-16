package com.kidstube.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kidstube_prefs")

@Singleton
class KidsTubePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_ALLOWED_LANGUAGES = stringSetPreferencesKey("allowed_languages")
        private val KEY_BLOCKED_CHANNELS = stringSetPreferencesKey("blocked_channels")
        private val KEY_BLOCKED_CHANNEL_NAMES = stringPreferencesKey("blocked_channel_names")
        private val KEY_ALLOW_UNKNOWN_LANGUAGE = booleanPreferencesKey("allow_unknown_language")
        private val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val KEY_PIN_HASH = stringPreferencesKey("pin_hash")
        private val KEY_PIN_SALT = stringPreferencesKey("pin_salt")
        private val KEY_QUOTA_USED = intPreferencesKey("quota_used")
        private val KEY_QUOTA_DATE = longPreferencesKey("quota_date")
        private val KEY_AUTO_PLAY_ENABLED = booleanPreferencesKey("auto_play_enabled")
        private val KEY_FAVORITE_CHANNELS = stringSetPreferencesKey("favorite_channels")
    }

    // Language settings
    fun getAllowedLanguages(): Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[KEY_ALLOWED_LANGUAGES] ?: setOf("en")
    }

    suspend fun setAllowedLanguages(languages: Set<String>) {
        dataStore.edit { it[KEY_ALLOWED_LANGUAGES] = languages }
    }

    // Blocked channels
    fun getBlockedChannels(): Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[KEY_BLOCKED_CHANNELS] ?: emptySet()
    }

    suspend fun addBlockedChannel(channelId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_BLOCKED_CHANNELS] ?: emptySet()
            prefs[KEY_BLOCKED_CHANNELS] = current + channelId
        }
    }

    suspend fun removeBlockedChannel(channelId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_BLOCKED_CHANNELS] ?: emptySet()
            prefs[KEY_BLOCKED_CHANNELS] = current - channelId
        }
    }

    fun getBlockedChannelNames(): Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_BLOCKED_CHANNEL_NAMES] ?: ""
    }

    suspend fun setBlockedChannelNames(names: String) {
        dataStore.edit { it[KEY_BLOCKED_CHANNEL_NAMES] = names }
    }

    // Unknown language policy
    fun getAllowUnknownLanguage(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ALLOW_UNKNOWN_LANGUAGE] ?: false
    }

    suspend fun setAllowUnknownLanguage(allow: Boolean) {
        dataStore.edit { it[KEY_ALLOW_UNKNOWN_LANGUAGE] = allow }
    }

    // Onboarding
    fun isOnboardingComplete(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETE] ?: false
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[KEY_ONBOARDING_COMPLETE] = complete }
    }

    // PIN management
    suspend fun setPin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        dataStore.edit { prefs ->
            prefs[KEY_PIN_SALT] = salt
            prefs[KEY_PIN_HASH] = hash
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val prefs = dataStore.data.first()
        val storedHash = prefs[KEY_PIN_HASH] ?: return false
        val storedSalt = prefs[KEY_PIN_SALT] ?: return false
        if (storedHash.isEmpty() || storedSalt.isEmpty()) return false
        return hashPin(pin, storedSalt) == storedHash
    }

    fun hasPin(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_PIN_HASH]?.isNotEmpty() == true
    }

    // Auto-play
    fun isAutoPlayEnabled(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_AUTO_PLAY_ENABLED] ?: true
    }

    suspend fun setAutoPlayEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_PLAY_ENABLED] = enabled }
    }

    // Favorite channels
    fun getFavoriteChannels(): Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[KEY_FAVORITE_CHANNELS] ?: emptySet()
    }

    suspend fun addFavoriteChannel(channelId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_CHANNELS] ?: emptySet()
            prefs[KEY_FAVORITE_CHANNELS] = current + channelId
        }
    }

    suspend fun removeFavoriteChannel(channelId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_CHANNELS] ?: emptySet()
            prefs[KEY_FAVORITE_CHANNELS] = current - channelId
        }
    }

    // Quota tracking
    fun getQuotaUsedToday(): Flow<Int> = dataStore.data.map { prefs ->
        val quotaDate = prefs[KEY_QUOTA_DATE] ?: 0L
        if (isSameDay(quotaDate)) {
            prefs[KEY_QUOTA_USED] ?: 0
        } else {
            0
        }
    }

    suspend fun incrementQuotaUsed(units: Int) {
        dataStore.edit { prefs ->
            val quotaDate = prefs[KEY_QUOTA_DATE] ?: 0L
            if (isSameDay(quotaDate)) {
                prefs[KEY_QUOTA_USED] = (prefs[KEY_QUOTA_USED] ?: 0) + units
            } else {
                prefs[KEY_QUOTA_DATE] = System.currentTimeMillis()
                prefs[KEY_QUOTA_USED] = units
            }
        }
    }

    suspend fun resetQuotaIfNewDay() {
        dataStore.edit { prefs ->
            val quotaDate = prefs[KEY_QUOTA_DATE] ?: 0L
            if (!isSameDay(quotaDate)) {
                prefs[KEY_QUOTA_DATE] = System.currentTimeMillis()
                prefs[KEY_QUOTA_USED] = 0
            }
        }
    }

    private fun isSameDay(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hashPin(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("$salt$pin".toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
