package com.kidstube.core.data.repository

import com.kidstube.core.data.quota.QuotaTracker
import com.kidstube.core.data.mapper.toDomain
import com.kidstube.core.data.mapper.toEntity
import com.kidstube.core.database.dao.SearchCacheDao
import com.kidstube.core.database.dao.VideoDao
import com.kidstube.core.database.entity.SearchCacheEntity
import com.kidstube.core.domain.model.Video
import com.kidstube.core.domain.repository.FeedChannel
import com.kidstube.core.domain.repository.VideoRepository
import com.kidstube.core.network.rss.YouTubeRssService
import com.kidstube.core.network.service.YouTubeApiService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val youTubeApiService: YouTubeApiService,
    private val youTubeRssService: YouTubeRssService,
    private val rssChannelRegistry: RssChannelRegistry,
    private val videoDao: VideoDao,
    private val searchCacheDao: SearchCacheDao,
    private val quotaTracker: QuotaTracker,
    @Named("youtube_api_key") private val apiKey: String
) : VideoRepository {

    override suspend fun searchVideos(
        query: String,
        allowedLanguages: Set<String>,
        allowUnknownLanguage: Boolean,
        blockedChannels: Set<String>,
        relevanceLanguage: String?,
        regionCode: String?,
        forceRefresh: Boolean
    ): Result<List<Video>> = runCatching {
        val cacheKey = query.lowercase().trim()

        // Check cache first
        if (!forceRefresh) {
            val minTime = System.currentTimeMillis() - SearchCacheEntity.CACHE_TTL_MS
            val cached = searchCacheDao.getSearchCache(cacheKey, minTime)
            if (cached != null) {
                val videos = videoDao.getVideosByIds(cached.getVideoIdList())
                    .map { it.toDomain() }
                return@runCatching filterVideos(videos, allowedLanguages, allowUnknownLanguage, blockedChannels)
            }
        }

        // Check quota
        if (!quotaTracker.canMakeSearch()) {
            // Fall back to any cached results regardless of TTL
            val anyCached = searchCacheDao.getSearchCache(cacheKey, 0)
            if (anyCached != null) {
                val videos = videoDao.getVideosByIds(anyCached.getVideoIdList())
                    .map { it.toDomain() }
                return@runCatching filterVideos(videos, allowedLanguages, allowUnknownLanguage, blockedChannels)
            }
            throw QuotaExhaustedException()
        }

        // API call: search.list
        val searchResponse = youTubeApiService.searchVideos(
            query = query,
            relevanceLanguage = relevanceLanguage,
            regionCode = regionCode,
            apiKey = apiKey
        )

        val videoIds = searchResponse.items.mapNotNull { it.id.videoId }
        if (videoIds.isEmpty()) return@runCatching emptyList()

        // API call: videos.list for full metadata (including language fields)
        val videoDetails = youTubeApiService.getVideoDetails(
            ids = videoIds.joinToString(","),
            apiKey = apiKey
        )

        quotaTracker.recordSearch(videoIds.size)

        val videos = videoDetails.items.map { it.toDomain() }

        // Cache results
        videoDao.insertVideos(videos.map { it.toEntity() })
        searchCacheDao.insertSearchCache(
            SearchCacheEntity.fromVideoIds(cacheKey, videos.map { it.id })
        )

        filterVideos(videos, allowedLanguages, allowUnknownLanguage, blockedChannels)
    }

    override suspend fun getVideoById(videoId: String): Result<Video?> = runCatching {
        // Check cache first
        videoDao.getVideoById(videoId)?.toDomain()
            ?: run {
                if (!quotaTracker.canMakeSearch()) return@runCatching null
                val response = youTubeApiService.getVideoDetails(
                    ids = videoId,
                    apiKey = apiKey
                )
                quotaTracker.recordSearch(1)
                response.items.firstOrNull()?.toDomain()?.also { video ->
                    videoDao.insertVideos(listOf(video.toEntity()))
                }
            }
    }

    override suspend fun getRelatedVideos(
        videoId: String,
        allowedLanguages: Set<String>,
        allowUnknownLanguage: Boolean,
        blockedChannels: Set<String>
    ): Result<List<Video>> {
        val video = getVideoById(videoId).getOrNull() ?: return Result.success(emptyList())
        val primaryLang = allowedLanguages.firstOrNull()
        val query = video.channelTitle + " " + video.title.take(30)
        return searchVideos(
            query = query,
            allowedLanguages = allowedLanguages,
            allowUnknownLanguage = allowUnknownLanguage,
            blockedChannels = blockedChannels,
            relevanceLanguage = primaryLang,
            regionCode = primaryLang?.let { LanguageRegionMap.getRegion(it) }
        ).map { videos -> videos.filter { it.id != videoId } }
    }

    override suspend fun getChannelFeed(
        channelId: String,
        languageCode: String
    ): Result<List<Video>> = runCatching {
        val cacheKey = "rss:$channelId"

        // Check cache first
        val minTime = System.currentTimeMillis() - SearchCacheEntity.CACHE_TTL_MS
        val cached = searchCacheDao.getSearchCache(cacheKey, minTime)
        if (cached != null) {
            val videos = videoDao.getVideosByIds(cached.getVideoIdList())
                .map { it.toDomain() }
            return@runCatching videos
        }

        // Fetch from RSS
        val rssEntries = youTubeRssService.fetchChannelFeed(channelId)
        if (rssEntries.isEmpty()) {
            // Fall back to expired cache
            val anyCached = searchCacheDao.getSearchCache(cacheKey, 0)
            if (anyCached != null) {
                return@runCatching videoDao.getVideosByIds(anyCached.getVideoIdList())
                    .map { it.toDomain() }
            }
            return@runCatching emptyList()
        }

        val lang = if (languageCode == "mul") null else languageCode
        val videos = rssEntries.map { it.toDomain(lang) }

        // Cache results
        videoDao.insertVideos(videos.map { it.toEntity() })
        searchCacheDao.insertSearchCache(
            SearchCacheEntity.fromVideoIds(cacheKey, videos.map { it.id })
        )

        videos
    }

    override suspend fun getAllCachedVideos(
        allowedLanguages: Set<String>,
        allowUnknownLanguage: Boolean,
        blockedChannels: Set<String>
    ): List<Video> {
        val allCached = videoDao.getAllCachedVideos().map { it.toDomain() }
        return filterVideos(allCached, allowedLanguages, allowUnknownLanguage, blockedChannels)
    }

    override suspend fun getRssFeedChannels(allowedLanguages: Set<String>): List<FeedChannel> {
        return rssChannelRegistry.getChannelsForLanguages(allowedLanguages).map { channel ->
            FeedChannel(
                channelId = channel.channelId,
                languageCode = channel.languageCode,
                isPriority = channel.isPriority
            )
        }
    }

    private fun filterVideos(
        videos: List<Video>,
        allowedLanguages: Set<String>,
        allowUnknownLanguage: Boolean,
        blockedChannels: Set<String>
    ): List<Video> {
        val normalizedAllowed = allowedLanguages.map { it.take(2).lowercase() }.toSet()

        return videos.filter { video ->
            // Filter blocked channels
            if (video.channelId in blockedChannels) return@filter false

            // Kids safety: only allow videos that are madeForKids or from allowlisted channels
            val isKidsSafe = video.madeForKids ||
                    video.channelId in AllowlistedChannels.channelIds
            if (!isKidsSafe) return@filter false

            // Language filtering
            val videoLangs = video.languageCodes

            if (videoLangs.isEmpty()) {
                // No language metadata — defer to user preference
                return@filter allowUnknownLanguage
            }

            // Has language metadata — check against allowed languages
            videoLangs.any { it in normalizedAllowed }
        }
    }
}

class QuotaExhaustedException : Exception("Daily YouTube API quota exhausted. Using cached results only.")
