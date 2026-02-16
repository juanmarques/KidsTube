package com.kidstube.feature.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidstube.core.domain.model.Video
import com.kidstube.core.domain.repository.SettingsRepository
import com.kidstube.core.domain.repository.VideoRepository
import com.kidstube.core.domain.usecase.GetCuratedFeedUseCase
import com.kidstube.core.domain.usecase.GetRelatedVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val video: Video? = null,
    val relatedVideos: List<Video> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val autoPlayCountdown: Int? = null,
    val showControls: Boolean = false,
    val isPlaying: Boolean = true,
    val isFavoriteChannel: Boolean = false,
    val currentTime: Float = 0f,
    val duration: Float = 0f
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val getRelatedVideosUseCase: GetRelatedVideosUseCase,
    private val getCuratedFeedUseCase: GetCuratedFeedUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _navigateToVideo = MutableSharedFlow<String>()
    val navigateToVideo: SharedFlow<String> = _navigateToVideo.asSharedFlow()

    private var countdownJob: Job? = null
    private var hideControlsJob: Job? = null
    private val videoHistory = mutableListOf<String>()
    private var randomVideosCache: List<Video> = emptyList()

    fun loadVideo(videoId: String) {
        // Add to history for "previous" navigation
        val currentVideoId = _uiState.value.video?.id
        if (currentVideoId != null && currentVideoId != videoId) {
            videoHistory.add(currentVideoId)
            // Keep history limited
            if (videoHistory.size > 20) videoHistory.removeAt(0)
        }
        cancelAutoPlay()
        viewModelScope.launch {
            _uiState.value = PlayerUiState(isLoading = true)

            videoRepository.getVideoById(videoId)
                .onSuccess { video ->
                    val favoriteIds = settingsRepository.getFavoriteChannels().first()
                    _uiState.value = _uiState.value.copy(
                        video = video,
                        isLoading = false,
                        isFavoriteChannel = video?.channelId in favoriteIds
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }

            // Load related videos
            Log.d("KT_PLAYER", "Loading related videos for videoId=$videoId")
            getRelatedVideosUseCase(videoId)
                .onSuccess { related ->
                    Log.d("KT_PLAYER", "Got ${related.size} related videos")
                    related.take(3).forEach { v ->
                        Log.d("KT_PLAYER", "  Related: ${v.id} - ${v.title.take(30)} - thumb=${v.thumbnailUrl.take(50)}")
                    }
                    _uiState.value = _uiState.value.copy(relatedVideos = related)
                }
                .onFailure { e ->
                    Log.e("KT_PLAYER", "Failed to load related videos", e)
                }
        }
    }

    fun onVideoEnded() {
        viewModelScope.launch {
            val autoPlayEnabled = settingsRepository.isAutoPlayEnabled().first()
            if (!autoPlayEnabled) return@launch
            if (_uiState.value.relatedVideos.isEmpty()) return@launch

            countdownJob = viewModelScope.launch {
                for (i in AUTO_PLAY_COUNTDOWN downTo 1) {
                    _uiState.value = _uiState.value.copy(autoPlayCountdown = i)
                    delay(1000)
                }
                playNext()
            }
        }
    }

    fun playNext() {
        cancelAutoPlay()
        val nextVideo = _uiState.value.relatedVideos.firstOrNull() ?: return
        viewModelScope.launch {
            _navigateToVideo.emit(nextVideo.id)
        }
    }

    fun cancelAutoPlay() {
        countdownJob?.cancel()
        countdownJob = null
        _uiState.value = _uiState.value.copy(autoPlayCountdown = null)
    }

    fun toggleControls() {
        hideControlsJob?.cancel()
        val newShowControls = !_uiState.value.showControls
        _uiState.value = _uiState.value.copy(showControls = newShowControls)

        // Auto-hide controls after 4 seconds
        if (newShowControls) {
            hideControlsJob = viewModelScope.launch {
                delay(4000)
                _uiState.value = _uiState.value.copy(showControls = false)
            }
        }
    }

    fun hideControls() {
        hideControlsJob?.cancel()
        _uiState.value = _uiState.value.copy(showControls = false)
    }

    fun setPlaying(isPlaying: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
    }

    fun playPrevious() {
        if (videoHistory.isEmpty()) return
        cancelAutoPlay()
        val previousId = videoHistory.removeAt(videoHistory.size - 1)
        viewModelScope.launch {
            _navigateToVideo.emit(previousId)
        }
    }

    fun playNextOrRandom() {
        cancelAutoPlay()
        viewModelScope.launch {
            // Try related videos first
            val nextVideo = _uiState.value.relatedVideos.firstOrNull()
            if (nextVideo != null) {
                _navigateToVideo.emit(nextVideo.id)
                return@launch
            }

            // No related videos - get random from curated feed
            if (randomVideosCache.isEmpty()) {
                getCuratedFeedUseCase(loadMore = false)
                    .onSuccess { videos ->
                        randomVideosCache = videos.filter { it.id != _uiState.value.video?.id }
                    }
            }

            val randomVideo = randomVideosCache.randomOrNull()
            if (randomVideo != null) {
                randomVideosCache = randomVideosCache.filter { it.id != randomVideo.id }
                _navigateToVideo.emit(randomVideo.id)
            }
        }
    }

    fun toggleFavoriteChannel() {
        val channelId = _uiState.value.video?.channelId ?: return
        val isFavorite = _uiState.value.isFavoriteChannel
        viewModelScope.launch {
            if (isFavorite) {
                settingsRepository.removeFavoriteChannel(channelId)
            } else {
                settingsRepository.addFavoriteChannel(channelId)
            }
            _uiState.value = _uiState.value.copy(isFavoriteChannel = !isFavorite)
        }
    }

    fun updateProgress(currentTime: Float, duration: Float) {
        _uiState.value = _uiState.value.copy(currentTime = currentTime, duration = duration)
    }

    fun keepControlsVisible() {
        hideControlsJob?.cancel()
        hideControlsJob = viewModelScope.launch {
            delay(4000)
            _uiState.value = _uiState.value.copy(showControls = false)
        }
    }

    fun hasPrevious(): Boolean = videoHistory.isNotEmpty()

    companion object {
        private const val AUTO_PLAY_COUNTDOWN = 5
    }
}
