package com.kidstube.core.domain.usecase

import com.kidstube.core.domain.model.Video
import com.kidstube.core.domain.repository.SettingsRepository
import com.kidstube.core.domain.repository.VideoRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRelatedVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(videoId: String): Result<List<Video>> {
        val allowedLanguages = settingsRepository.getAllowedLanguages().first()
        val allowUnknown = settingsRepository.getAllowUnknownLanguage().first()
        val blockedChannels = settingsRepository.getBlockedChannels().first()

        return videoRepository.getRelatedVideos(
            videoId = videoId,
            allowedLanguages = allowedLanguages,
            allowUnknownLanguage = allowUnknown,
            blockedChannels = blockedChannels
        )
    }
}
