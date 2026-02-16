package com.kidstube.feature.parental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidstube.core.domain.usecase.ManagePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinEntryUiState(
    val pin: String = "",
    val error: String? = null,
    val failedAttempts: Int = 0,
    val isLocked: Boolean = false,
    val lockRemainingSeconds: Int = 0
)

@HiltViewModel
class PinEntryViewModel @Inject constructor(
    private val managePinUseCase: ManagePinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinEntryUiState())
    val uiState: StateFlow<PinEntryUiState> = _uiState.asStateFlow()

    companion object {
        private const val MAX_ATTEMPTS = 3
        private const val LOCKOUT_SECONDS = 30
    }

    fun onDigitEntered(digit: String) {
        if (_uiState.value.isLocked) return
        val current = _uiState.value.pin
        if (current.length < 6) {
            val newPin = current + digit
            _uiState.value = _uiState.value.copy(pin = newPin, error = null)
            if (newPin.length >= 4) {
                verifyPin(newPin)
            }
        }
    }

    fun onBackspace() {
        if (_uiState.value.isLocked) return
        val current = _uiState.value.pin
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                pin = current.dropLast(1),
                error = null
            )
        }
    }

    private var onSuccessCallback: (() -> Unit)? = null

    fun setOnSuccess(callback: () -> Unit) {
        onSuccessCallback = callback
    }

    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            val valid = managePinUseCase.verify(pin)
            if (valid) {
                onSuccessCallback?.invoke()
            } else {
                val attempts = _uiState.value.failedAttempts + 1
                if (attempts >= MAX_ATTEMPTS) {
                    _uiState.value = _uiState.value.copy(
                        pin = "",
                        error = "Too many attempts. Locked for $LOCKOUT_SECONDS seconds.",
                        failedAttempts = attempts,
                        isLocked = true,
                        lockRemainingSeconds = LOCKOUT_SECONDS
                    )
                    startLockoutTimer()
                } else {
                    _uiState.value = _uiState.value.copy(
                        pin = "",
                        error = "Wrong PIN. ${MAX_ATTEMPTS - attempts} attempts remaining.",
                        failedAttempts = attempts
                    )
                }
            }
        }
    }

    private fun startLockoutTimer() {
        viewModelScope.launch {
            for (i in LOCKOUT_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(lockRemainingSeconds = i)
                delay(1000)
            }
            _uiState.value = _uiState.value.copy(
                isLocked = false,
                failedAttempts = 0,
                error = null,
                lockRemainingSeconds = 0
            )
        }
    }
}
