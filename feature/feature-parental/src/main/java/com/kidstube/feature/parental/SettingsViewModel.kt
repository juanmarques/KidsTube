package com.kidstube.feature.parental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidstube.core.domain.usecase.ManagePinUseCase
import com.kidstube.core.domain.usecase.ManageSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val allowedLanguages: Set<String> = setOf("en"),
    val blockedChannelNames: Map<String, String> = emptyMap(),
    val allowUnknownLanguage: Boolean = false,
    val autoPlayEnabled: Boolean = true,
    val quotaUsed: Int = 0,
    val showChangePinDialog: Boolean = false,
    val pinChangeSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val manageSettingsUseCase: ManageSettingsUseCase,
    private val managePinUseCase: ManagePinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                manageSettingsUseCase.getAllowedLanguages(),
                manageSettingsUseCase.getBlockedChannelNames(),
                manageSettingsUseCase.getAllowUnknownLanguage(),
                manageSettingsUseCase.isAutoPlayEnabled(),
                manageSettingsUseCase.getQuotaUsedToday()
            ) { languages, channels, allowUnknown, autoPlay, quota ->
                SettingsUiState(
                    allowedLanguages = languages,
                    blockedChannelNames = channels,
                    allowUnknownLanguage = allowUnknown,
                    autoPlayEnabled = autoPlay,
                    quotaUsed = quota
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleLanguage(languageCode: String) {
        viewModelScope.launch {
            val current = _uiState.value.allowedLanguages.toMutableSet()
            if (languageCode in current) {
                if (current.size > 1) current.remove(languageCode)
            } else {
                current.add(languageCode)
            }
            manageSettingsUseCase.setAllowedLanguages(current)
        }
    }

    fun setAllowUnknownLanguage(allow: Boolean) {
        viewModelScope.launch {
            manageSettingsUseCase.setAllowUnknownLanguage(allow)
        }
    }

    fun setAutoPlayEnabled(enabled: Boolean) {
        viewModelScope.launch {
            manageSettingsUseCase.setAutoPlayEnabled(enabled)
        }
    }

    fun removeBlockedChannel(channelId: String) {
        viewModelScope.launch {
            manageSettingsUseCase.removeBlockedChannel(channelId)
        }
    }

    fun showChangePinDialog() {
        _uiState.value = _uiState.value.copy(showChangePinDialog = true)
    }

    fun dismissChangePinDialog() {
        _uiState.value = _uiState.value.copy(showChangePinDialog = false, pinChangeSuccess = false)
    }

    fun changePin(newPin: String) {
        viewModelScope.launch {
            managePinUseCase.setPin(newPin)
            _uiState.value = _uiState.value.copy(
                showChangePinDialog = false,
                pinChangeSuccess = true
            )
        }
    }
}
