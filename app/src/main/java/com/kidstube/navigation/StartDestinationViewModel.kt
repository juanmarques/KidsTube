package com.kidstube.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidstube.core.domain.usecase.IsOnboardingCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartDestinationViewModel @Inject constructor(
    private val isOnboardingComplete: IsOnboardingCompleteUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        viewModelScope.launch {
            val complete = isOnboardingComplete()
            _startDestination.value = if (complete) Routes.HOME else Routes.ONBOARDING
        }
    }
}
