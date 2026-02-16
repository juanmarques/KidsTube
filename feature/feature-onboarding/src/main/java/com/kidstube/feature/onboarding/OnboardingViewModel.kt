package com.kidstube.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidstube.core.domain.usecase.ManagePinUseCase
import com.kidstube.core.domain.usecase.ManageSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OnboardingStep {
    WELCOME,
    CREATE_PIN,
    CONFIRM_PIN,
    SELECT_LANGUAGES
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val pin: String = "",
    val confirmPin: String = "",
    val pinError: String? = null,
    val selectedLanguages: Set<String> = setOf("en"),
    val isComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val managePinUseCase: ManagePinUseCase,
    private val manageSettingsUseCase: ManageSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextStep() {
        val current = _uiState.value
        when (current.step) {
            OnboardingStep.WELCOME -> {
                _uiState.value = current.copy(step = OnboardingStep.CREATE_PIN)
            }
            OnboardingStep.CREATE_PIN -> {
                if (current.pin.length in 4..6) {
                    _uiState.value = current.copy(step = OnboardingStep.CONFIRM_PIN, pinError = null)
                } else {
                    _uiState.value = current.copy(pinError = "PIN must be 4-6 digits")
                }
            }
            OnboardingStep.CONFIRM_PIN -> {
                if (current.confirmPin == current.pin) {
                    _uiState.value = current.copy(step = OnboardingStep.SELECT_LANGUAGES, pinError = null)
                } else {
                    _uiState.value = current.copy(confirmPin = "", pinError = "PINs don't match")
                }
            }
            OnboardingStep.SELECT_LANGUAGES -> {
                completeOnboarding()
            }
        }
    }

    fun onPinDigit(digit: String) {
        val current = _uiState.value
        when (current.step) {
            OnboardingStep.CREATE_PIN -> {
                if (current.pin.length < 6) {
                    _uiState.value = current.copy(pin = current.pin + digit, pinError = null)
                }
            }
            OnboardingStep.CONFIRM_PIN -> {
                if (current.confirmPin.length < 6) {
                    _uiState.value = current.copy(confirmPin = current.confirmPin + digit, pinError = null)
                }
            }
            else -> {}
        }
    }

    fun onPinBackspace() {
        val current = _uiState.value
        when (current.step) {
            OnboardingStep.CREATE_PIN -> {
                if (current.pin.isNotEmpty()) {
                    _uiState.value = current.copy(pin = current.pin.dropLast(1))
                }
            }
            OnboardingStep.CONFIRM_PIN -> {
                if (current.confirmPin.isNotEmpty()) {
                    _uiState.value = current.copy(confirmPin = current.confirmPin.dropLast(1))
                }
            }
            else -> {}
        }
    }

    fun toggleLanguage(code: String) {
        val current = _uiState.value.selectedLanguages.toMutableSet()
        if (code in current) {
            if (current.size > 1) current.remove(code)
        } else {
            current.add(code)
        }
        _uiState.value = _uiState.value.copy(selectedLanguages = current)
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            managePinUseCase.setPin(_uiState.value.pin)
            manageSettingsUseCase.setAllowedLanguages(_uiState.value.selectedLanguages)
            manageSettingsUseCase.completeOnboarding()
            _uiState.value = _uiState.value.copy(isComplete = true)
        }
    }
}
