package com.kidstube.core.domain.usecase

import com.kidstube.core.domain.model.Video
import com.kidstube.core.domain.repository.FeedChannel
import com.kidstube.core.domain.repository.SettingsRepository
import com.kidstube.core.domain.repository.VideoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCuratedFeedUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val settingsRepository: SettingsRepository
) {
    private val usedChannelIds = mutableSetOf<String>()
    private val seenVideoIds = mutableSetOf<String>()

    suspend operator fun invoke(loadMore: Boolean = false): Result<List<Video>> {
        val allowedLanguages = settingsRepository.getAllowedLanguages().first()
        val allowUnknown = settingsRepository.getAllowUnknownLanguage().first()
        val blockedChannels = settingsRepository.getBlockedChannels().first()

        if (!loadMore) {
            usedChannelIds.clear()
            seenVideoIds.clear()
        }

        val favoriteChannelIds = settingsRepository.getFavoriteChannels().first()

        val allChannels = videoRepository.getRssFeedChannels(allowedLanguages)
        val availableChannels = allChannels.filter { it.channelId !in usedChannelIds }

        // Three-way partition: favorites, priority (non-favorite), other (non-favorite)
        val favoriteChannels = availableChannels.filter { it.channelId in favoriteChannelIds }
        val priorityChannels = availableChannels.filter { it.isPriority && it.channelId !in favoriteChannelIds }
        val otherChannels = availableChannels.filter { !it.isPriority && it.channelId !in favoriteChannelIds }

        // Select channels: if favorites exist, 2 favorites + 1 priority + 1 other
        // Otherwise, keep existing 2 priority + 2 other
        val selectedChannels = mutableListOf<FeedChannel>()
        if (favoriteChannelIds.isNotEmpty()) {
            val pickedFavorites = favoriteChannels.shuffled().take(FAVORITE_CHANNELS_PER_LOAD)
            selectedChannels.addAll(pickedFavorites)
            // Fill remaining favorite slots with priority
            val prioritySlots = PRIORITY_CHANNELS_WITH_FAVORITES + (FAVORITE_CHANNELS_PER_LOAD - pickedFavorites.size)
            selectedChannels.addAll(priorityChannels.shuffled().take(prioritySlots))
            val remainingSlots = CHANNELS_PER_LOAD - selectedChannels.size
            if (remainingSlots > 0) {
                selectedChannels.addAll(otherChannels.shuffled().take(remainingSlots))
            }
        } else {
            selectedChannels.addAll(priorityChannels.shuffled().take(PRIORITY_CHANNELS_PER_LOAD))
            val remainingSlots = CHANNELS_PER_LOAD - selectedChannels.size
            if (remainingSlots > 0) {
                selectedChannels.addAll(otherChannels.shuffled().take(remainingSlots))
            }
        }

        // If not enough channels available, reset and try again
        if (selectedChannels.size < CHANNELS_PER_LOAD) {
            usedChannelIds.clear()
            val resetChannels = allChannels.shuffled().take(CHANNELS_PER_LOAD)
            selectedChannels.clear()
            selectedChannels.addAll(resetChannels)
        }

        selectedChannels.forEach { usedChannelIds.add(it.channelId) }

        // Fetch all RSS feeds in parallel (free — no API quota)
        val videos = coroutineScope {
            selectedChannels.map { channel ->
                async {
                    videoRepository.getChannelFeed(channel.channelId, channel.languageCode)
                        .getOrDefault(emptyList())
                }
            }.awaitAll().flatten()
        }

        // Filter blocked channels and deduplicate
        val filteredVideos = videos
            .filter { it.channelId !in blockedChannels }
            .filter { it.id !in seenVideoIds }

        filteredVideos.forEach { seenVideoIds.add(it.id) }

        // Cache fallback: if all RSS feeds returned empty, use cached videos (white screen fix)
        if (filteredVideos.isEmpty()) {
            val cachedVideos = videoRepository.getAllCachedVideos(
                allowedLanguages = allowedLanguages,
                allowUnknownLanguage = allowUnknown,
                blockedChannels = blockedChannels
            ).filter { it.id !in seenVideoIds }

            if (cachedVideos.isNotEmpty()) {
                cachedVideos.forEach { seenVideoIds.add(it.id) }
                return Result.success(cachedVideos.shuffled())
            }
        }

        return Result.success(filteredVideos.shuffled())
    }

    companion object {
        private const val CHANNELS_PER_LOAD = 4
        private const val PRIORITY_CHANNELS_PER_LOAD = 2
        private const val FAVORITE_CHANNELS_PER_LOAD = 2
        private const val PRIORITY_CHANNELS_WITH_FAVORITES = 1
    }
}
