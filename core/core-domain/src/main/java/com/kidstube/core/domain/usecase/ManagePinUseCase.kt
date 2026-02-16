package com.kidstube.core.domain.usecase

import com.kidstube.core.domain.repository.SettingsRepository
import javax.inject.Inject

class ManagePinUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun verify(pin: String): Boolean = settingsRepository.verifyPin(pin)
    suspend fun setPin(pin: String) = settingsRepository.setPin(pin)
    suspend fun hasPin(): Boolean = settingsRepository.hasPin()
}
