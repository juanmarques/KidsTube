package com.kidstube.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidstube.core.domain.model.Video
import com.kidstube.core.domain.usecase.SearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val videos: List<Video> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchVideosUseCase: SearchVideosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(500) // debounce
                search(query)
            }
        }
    }

    fun onSearch() {
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                search(query)
            }
        }
    }

    private suspend fun search(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        searchVideosUseCase(query)
            .onSuccess { videos ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    videos = videos,
                    hasSearched = true
                )
            }
            .onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                    hasSearched = true
                )
            }
    }
}
