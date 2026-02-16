package com.kidstube.core.domain.model

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelId: String,
    val channelTitle: String,
    val publishedAt: String,
    val defaultLanguage: String?,
    val defaultAudioLanguage: String?,
    val duration: String?,
    val hasLanguageMetadata: Boolean,
    val madeForKids: Boolean
) {
    val languageCodes: Set<String>
        get() = listOfNotNull(defaultLanguage, defaultAudioLanguage)
            .map { it.take(2).lowercase() }
            .toSet()
}
