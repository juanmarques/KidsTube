package com.kidstube.core.domain.usecase

import com.kidstube.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsOnboardingCompleteUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Boolean {
        return settingsRepository.isOnboardingComplete().first()
    }
}
