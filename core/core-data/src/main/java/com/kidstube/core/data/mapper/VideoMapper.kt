package com.kidstube.core.data.mapper

import com.kidstube.core.database.entity.CachedVideoEntity
import com.kidstube.core.domain.model.Video
import com.kidstube.core.network.dto.VideoItem
import com.kidstube.core.network.rss.RssVideoEntry

fun VideoItem.toDomain(): Video = Video(
    id = id,
    title = snippet.title,
    description = snippet.description,
    thumbnailUrl = snippet.thumbnails?.high?.url ?: snippet.thumbnails?.medium?.url ?: "",
    channelId = snippet.channelId,
    channelTitle = snippet.channelTitle,
    publishedAt = snippet.publishedAt,
    defaultLanguage = snippet.defaultLanguage,
    defaultAudioLanguage = snippet.defaultAudioLanguage,
    duration = contentDetails?.duration,
    hasLanguageMetadata = snippet.defaultLanguage != null || snippet.defaultAudioLanguage != null,
    madeForKids = status?.madeForKids ?: false
)

fun Video.toEntity(): CachedVideoEntity = CachedVideoEntity(
    id = id,
    title = title,
    description = description,
    thumbnailUrl = thumbnailUrl,
    channelId = channelId,
    channelTitle = channelTitle,
    publishedAt = publishedAt,
    defaultLanguage = defaultLanguage,
    defaultAudioLanguage = defaultAudioLanguage,
    duration = duration,
    madeForKids = madeForKids
)

fun CachedVideoEntity.toDomain(): Video = Video(
    id = id,
    title = title,
    description = description,
    thumbnailUrl = thumbnailUrl,
    channelId = channelId,
    channelTitle = channelTitle,
    publishedAt = publishedAt,
    defaultLanguage = defaultLanguage,
    defaultAudioLanguage = defaultAudioLanguage,
    duration = duration,
    hasLanguageMetadata = defaultLanguage != null || defaultAudioLanguage != null,
    madeForKids = madeForKids
)

fun RssVideoEntry.toDomain(languageCode: String?): Video = Video(
    id = videoId,
    title = title,
    description = description,
    thumbnailUrl = thumbnailUrl,
    channelId = channelId,
    channelTitle = channelTitle,
    publishedAt = publishedAt,
    defaultLanguage = languageCode,
    defaultAudioLanguage = languageCode,
    duration = null,
    hasLanguageMetadata = languageCode != null,
    madeForKids = false
)
