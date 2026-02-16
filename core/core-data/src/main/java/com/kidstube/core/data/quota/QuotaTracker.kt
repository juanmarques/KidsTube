package com.kidstube.core.data.quota

import com.kidstube.core.datastore.KidsTubePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuotaTracker @Inject constructor(
    private val preferences: KidsTubePreferences
) {
    private val mutex = Mutex()

    companion object {
        const val DAILY_QUOTA_LIMIT = 10_000
        const val QUOTA_WARNING_THRESHOLD = 0.95 // 95%
        const val SEARCH_COST = 100
        const val VIDEO_LIST_COST = 1
    }

    suspend fun canMakeSearch(): Boolean = mutex.withLock {
        preferences.resetQuotaIfNewDay()
        val used = preferences.getQuotaUsedToday().first()
        used + SEARCH_COST + VIDEO_LIST_COST < (DAILY_QUOTA_LIMIT * QUOTA_WARNING_THRESHOLD).toInt()
    }

    suspend fun recordSearch(videoCount: Int) = mutex.withLock {
        val cost = SEARCH_COST + VIDEO_LIST_COST * ((videoCount + 49) / 50)
        preferences.incrementQuotaUsed(cost)
    }

    suspend fun getUsedQuota(): Int {
        preferences.resetQuotaIfNewDay()
        return preferences.getQuotaUsedToday().first()
    }

    suspend fun getRemainingQuota(): Int {
        return DAILY_QUOTA_LIMIT - getUsedQuota()
    }

    suspend fun isQuotaExhausted(): Boolean {
        return getUsedQuota() >= (DAILY_QUOTA_LIMIT * QUOTA_WARNING_THRESHOLD).toInt()
    }
}
