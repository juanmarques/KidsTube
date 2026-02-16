package com.kidstube.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidstube.core.domain.model.Video
import com.kidstube.core.domain.repository.SettingsRepository
import com.kidstube.core.domain.usecase.GetCuratedFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val videos: List<Video> = emptyList(),
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val favoriteChannelIds: Set<String> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCuratedFeedUseCase: GetCuratedFeedUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
        viewModelScope.launch {
            settingsRepository.getFavoriteChannels().collect { favorites ->
                _uiState.value = _uiState.value.copy(favoriteChannelIds = favorites)
            }
        }
    }

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getCuratedFeedUseCase(loadMore = false)
                .onSuccess { videos ->
                    if (videos.isEmpty()) {
                        val existing = _uiState.value.videos
                        if (existing.isNotEmpty()) {
                            // Keep showing existing videos
                            _uiState.value = _uiState.value.copy(isLoading = false)
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "No videos available. Please check your connection and try again."
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, videos = videos)
                    }
                }
                .onFailure { e ->
                    val existing = _uiState.value.videos
                    if (existing.isNotEmpty()) {
                        // Keep existing videos on failure
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                    }
                }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            getCuratedFeedUseCase(loadMore = true)
                .onSuccess { moreVideos ->
                    // Combine and shuffle all videos so content feels fresh
                    val allVideos = (_uiState.value.videos + moreVideos).shuffled()
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        videos = allVideos
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingMore = false)
                }
        }
    }

    fun shuffle() {
        // Reshuffle existing videos without loading more
        _uiState.value = _uiState.value.copy(
            videos = _uiState.value.videos.shuffled()
        )
    }
}
