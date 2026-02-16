package com.kidstube.core.domain.usecase

import com.kidstube.core.domain.model.Video
import com.kidstube.core.domain.repository.SettingsRepository
import com.kidstube.core.domain.repository.VideoRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SearchVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        query: String,
        forceRefresh: Boolean = false
    ): Result<List<Video>> {
        val allowedLanguages = settingsRepository.getAllowedLanguages().first()
        val allowUnknown = settingsRepository.getAllowUnknownLanguage().first()
        val blockedChannels = settingsRepository.getBlockedChannels().first()
        val primaryLanguage = allowedLanguages.firstOrNull()

        // Append kids qualifier to bias search towards children's content
        val kidsQuery = appendKidsQualifier(query, primaryLanguage)

        return videoRepository.searchVideos(
            query = kidsQuery,
            allowedLanguages = allowedLanguages,
            allowUnknownLanguage = allowUnknown,
            blockedChannels = blockedChannels,
            relevanceLanguage = primaryLanguage,
            regionCode = primaryLanguage?.let { getRegionCode(it) },
            forceRefresh = forceRefresh
        )
    }

    private fun appendKidsQualifier(query: String, languageCode: String?): String {
        val lowerQuery = query.lowercase()
        // Don't double-append if query already contains kids-related terms
        val kidsTerms = listOf(
            "kids", "children", "child",
            "niños", "infantil", "enfants", "crianças", "kinder",
            "bambini", "kinderen", "детей", "子供", "어린이", "儿童",
            "أطفال", "बच्चों", "çocuk"
        )
        if (kidsTerms.any { lowerQuery.contains(it) }) return query

        val qualifier = kidsQualifiers[languageCode?.take(2)?.lowercase()] ?: "for kids"
        return "$query $qualifier"
    }

    private fun getRegionCode(languageCode: String): String? {
        return regionMap[languageCode.take(2).lowercase()]
    }

    companion object {
        private val kidsQualifiers = mapOf(
            "en" to "for kids",
            "es" to "para niños",
            "fr" to "pour enfants",
            "pt" to "para crianças",
            "de" to "für Kinder",
            "it" to "per bambini",
            "nl" to "voor kinderen",
            "ru" to "для детей",
            "ja" to "子供向け",
            "ko" to "어린이",
            "zh" to "儿童",
            "ar" to "للأطفال",
            "hi" to "बच्चों के लिए",
            "tr" to "çocuklar için",
        )

        private val regionMap = mapOf(
            "en" to "US",
            "es" to "ES",
            "fr" to "FR",
            "pt" to "BR",
            "de" to "DE",
            "it" to "IT",
            "nl" to "NL",
            "ru" to "RU",
            "ja" to "JP",
            "ko" to "KR",
            "zh" to "CN",
            "ar" to "SA",
            "hi" to "IN",
            "tr" to "TR",
        )
    }
}
