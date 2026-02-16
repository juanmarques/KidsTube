package com.kidstube.feature.player

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidstube.core.ui.components.ErrorState
import com.kidstube.core.ui.components.LoadingState
import com.kidstube.core.ui.components.VideoCard

private val RedOrange = Color(0xFFFF3D00)

private fun formatTime(seconds: Float): String {
    val totalSeconds = seconds.toInt().coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60
    return "$minutes:${secs.toString().padStart(2, '0')}"
}

@Composable
fun PlayerScreen(
    videoId: String,
    onBack: () -> Unit,
    onVideoClick: (String) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId)
    }

    // Listen for auto-play navigation
    LaunchedEffect(Unit) {
        viewModel.navigateToVideo.collect { nextId ->
            onVideoClick(nextId)
        }
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            uiState.error != null -> ErrorState(
                message = uiState.error!!,
                onRetry = { viewModel.loadVideo(videoId) },
                modifier = Modifier.padding(padding)
            )
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(padding)
                ) {
                    // YouTube Player - fixed 16:9 aspect ratio, centered in available space
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        var webViewRef by remember { mutableStateOf<android.webkit.WebView?>(null) }

                        YouTubePlayerComposable(
                            videoId = videoId,
                            onVideoEnded = { viewModel.onVideoEnded() },
                            onWebViewCreated = { webViewRef = it },
                            onProgressUpdate = { currentTime, duration ->
                                viewModel.updateProgress(currentTime, duration)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(0.dp))
                        )

                        // Transparent touch interceptor - shows controls on tap
                        // Always visible to capture taps over the WebView
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(Color.Transparent)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    viewModel.toggleControls()
                                }
                        )

                        // Auto-play overlay with countdown
                        val countdown = uiState.autoPlayCountdown
                        if (countdown != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .background(Color.Black.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$countdown",
                                    color = Color.White,
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Playback controls overlay
                        androidx.compose.animation.AnimatedVisibility(
                            visible = uiState.showControls,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        viewModel.hideControls()
                                    }
                            ) {
                                // Center playback controls
                                Row(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Previous button
                                    IconButton(
                                        onClick = {
                                            viewModel.hideControls()
                                            viewModel.playPrevious()
                                        },
                                        enabled = viewModel.hasPrevious(),
                                        modifier = Modifier
                                            .size(72.dp)
                                            .background(
                                                if (viewModel.hasPrevious()) Color.White.copy(alpha = 0.3f)
                                                else Color.Gray.copy(alpha = 0.2f),
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.SkipPrevious,
                                            contentDescription = "Previous",
                                            tint = if (viewModel.hasPrevious()) Color.White else Color.Gray,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }

                                    // Play/Pause button
                                    IconButton(
                                        onClick = {
                                            val newPlaying = !uiState.isPlaying
                                            viewModel.setPlaying(newPlaying)
                                            // Inject JS to play/pause
                                            val js = if (newPlaying) {
                                                "document.querySelector('video')?.play();"
                                            } else {
                                                "document.querySelector('video')?.pause();"
                                            }
                                            webViewRef?.evaluateJavascript(js, null)
                                        },
                                        modifier = Modifier
                                            .size(96.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.4f),
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(64.dp)
                                        )
                                    }

                                    // Next button
                                    IconButton(
                                        onClick = {
                                            viewModel.hideControls()
                                            viewModel.playNextOrRandom()
                                        },
                                        modifier = Modifier
                                            .size(72.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.3f),
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.SkipNext,
                                            contentDescription = "Next",
                                            tint = Color.White,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }

                                // Favorite heart button - top end
                                IconButton(
                                    onClick = { viewModel.toggleFavoriteChannel() },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .size(64.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.4f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        if (uiState.isFavoriteChannel) Icons.Default.Favorite
                                        else Icons.Default.FavoriteBorder,
                                        contentDescription = if (uiState.isFavoriteChannel) "Unfavorite" else "Favorite",
                                        tint = if (uiState.isFavoriteChannel) RedOrange else Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                // Seekable progress bar - bottom
                                if (uiState.duration > 0f) {
                                    var isSeeking by remember { mutableStateOf(false) }
                                    var seekPosition by remember { mutableFloatStateOf(0f) }

                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatTime(if (isSeeking) seekPosition else uiState.currentTime),
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                        Slider(
                                            value = if (isSeeking) seekPosition else uiState.currentTime,
                                            onValueChange = { value ->
                                                isSeeking = true
                                                seekPosition = value
                                                viewModel.keepControlsVisible()
                                            },
                                            onValueChangeFinished = {
                                                isSeeking = false
                                                webViewRef?.evaluateJavascript(
                                                    "try{document.querySelector('video').currentTime=$seekPosition;}catch(e){}",
                                                    null
                                                )
                                            },
                                            valueRange = 0f..uiState.duration,
                                            colors = SliderDefaults.colors(
                                                thumbColor = RedOrange,
                                                activeTrackColor = RedOrange,
                                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp)
                                        )
                                        Text(
                                            text = formatTime(uiState.duration - (if (isSeeking) seekPosition else uiState.currentTime)),
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Large back arrow overlay - always visible
                        IconButton(
                            onClick = {
                                viewModel.cancelAutoPlay()
                                onBack()
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(64.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.4f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Horizontal related video thumbnails - shown only when controls are visible
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.showControls && uiState.relatedVideos.isNotEmpty(),
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it }
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(vertical = 8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.relatedVideos, key = { it.id }) { video ->
                                VideoCard(
                                    thumbnailUrl = video.thumbnailUrl,
                                    onClick = {
                                        viewModel.cancelAutoPlay()
                                        viewModel.hideControls()
                                        onVideoClick(video.id)
                                    },
                                    modifier = Modifier
                                        .width(180.dp)
                                        .aspectRatio(16f / 9f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private class VideoProgressBridge(
    private val onProgressUpdate: (Float, Float) -> Unit
) {
    @JavascriptInterface
    fun updateProgress(currentTime: Float, duration: Float) {
        onProgressUpdate(currentTime, duration)
    }
}

@android.annotation.SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YouTubePlayerComposable(
    videoId: String,
    onVideoEnded: () -> Unit,
    onWebViewCreated: (android.webkit.WebView) -> Unit = {},
    onProgressUpdate: (Float, Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<android.webkit.WebView?>(null) }

    Log.d("KT_PLAYER", "YouTubePlayerComposable with videoId=$videoId (length=${videoId.length})")

    // Use YouTube mobile site directly - only reliable method that bypasses embed restrictions
    fun getYouTubeUrl(vid: String) = "https://m.youtube.com/watch?v=$vid"

    // When videoId changes, reload
    LaunchedEffect(videoId) {
        Log.d("KT_PLAYER", "LaunchedEffect: loading YouTube watch URL for videoId=$videoId")
        webViewRef?.loadUrl(getYouTubeUrl(videoId))
    }

    AndroidView(
        factory = { ctx ->
            Log.d("KT_PLAYER", "Creating WebView for videoId=$videoId")
            android.webkit.WebView(ctx).apply {
                webViewRef = this
                onWebViewCreated(this)
                setBackgroundColor(android.graphics.Color.BLACK)

                // Force hardware layer for proper video compositing
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

                // Disable scrolling - contain the YouTube page
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = android.view.View.OVER_SCROLL_NEVER

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                }

                // Register JS bridge for progress updates
                addJavascriptInterface(
                    VideoProgressBridge(onProgressUpdate),
                    "KidsTubeProgress"
                )

                // Enable third-party cookies
                val webView = this
                android.webkit.CookieManager.getInstance().apply {
                    setAcceptCookie(true)
                    setAcceptThirdPartyCookies(webView, true)
                }

                webChromeClient = object : android.webkit.WebChromeClient() {
                    override fun onConsoleMessage(msg: android.webkit.ConsoleMessage?): Boolean {
                        msg?.let {
                            Log.d("KT_PLAYER", "JS: ${it.message()}")
                            if (it.message() == "VIDEO_ENDED") {
                                onVideoEnded()
                            }
                        }
                        return true
                    }

                    // Required for fullscreen video
                    override fun onShowCustomView(view: android.view.View?, callback: CustomViewCallback?) {
                        Log.d("KT_PLAYER", "onShowCustomView")
                    }

                    override fun onHideCustomView() {
                        Log.d("KT_PLAYER", "onHideCustomView")
                    }
                }

                webViewClient = object : android.webkit.WebViewClient() {
                    override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("KT_PLAYER", "Page finished loading: $url")

                        // Inject CSS to hide YouTube chrome but preserve video player
                        val hideYouTubeUI = """
                            (function() {
                                var style = document.createElement('style');
                                style.id = 'kidstube-style';
                                style.textContent = `
                                    /* Black background for page */
                                    html, body {
                                        margin: 0 !important;
                                        padding: 0 !important;
                                        overflow: hidden !important;
                                        background: #000 !important;
                                    }
                                    /* Hide YouTube navigation and metadata */
                                    header, footer, nav,
                                    ytm-mobile-topbar-renderer,
                                    ytm-pivot-bar-renderer,
                                    ytm-slim-video-metadata-section-renderer,
                                    ytm-single-column-watch-next-results-renderer,
                                    ytm-comments-entry-point-teaser-renderer,
                                    ytm-item-section-renderer,
                                    .watch-below-the-player,
                                    ytm-rich-section-renderer {
                                        display: none !important;
                                    }
                                    /* Make player container fill screen */
                                    ytm-player-container, #player-container-id,
                                    .player-container-id {
                                        position: fixed !important;
                                        top: 0 !important;
                                        left: 0 !important;
                                        width: 100vw !important;
                                        height: 100vh !important;
                                        background: #000 !important;
                                        z-index: 9999 !important;
                                    }
                                    /* Ensure video fills container */
                                    #movie_player, .html5-video-player {
                                        width: 100% !important;
                                        height: 100% !important;
                                        background: #000 !important;
                                    }
                                    .html5-video-container {
                                        width: 100% !important;
                                        height: 100% !important;
                                    }
                                    video {
                                        width: 100% !important;
                                        height: 100% !important;
                                        object-fit: contain !important;
                                    }
                                `;

                                var old = document.getElementById('kidstube-style');
                                if (old) old.remove();
                                document.head.appendChild(style);
                                window.scrollTo(0, 0);

                                // Periodically scroll to top and hide new elements
                                if (!window.kidstubeInterval) {
                                    window.kidstubeInterval = setInterval(function() {
                                        window.scrollTo(0, 0);
                                    }, 500);
                                }

                                // Try to auto-play and unmute the video
                                setTimeout(function() {
                                    var video = document.querySelector('video');
                                    if (video) {
                                        // Unmute the video
                                        video.muted = false;
                                        video.volume = 1.0;

                                        // Try to play if paused
                                        if (video.paused) {
                                            video.play().catch(function(e) {
                                                console.log('Autoplay blocked: ' + e);
                                            });
                                        }
                                    }

                                    // Also try YouTube's player API to unmute
                                    try {
                                        var player = document.getElementById('movie_player');
                                        if (player && player.unMute) {
                                            player.unMute();
                                            player.setVolume(100);
                                        }
                                    } catch(e) {}
                                }, 1000);

                                // Keep trying to unmute periodically
                                if (!window.kidstubeUnmuteInterval) {
                                    window.kidstubeUnmuteInterval = setInterval(function() {
                                        var video = document.querySelector('video');
                                        if (video && video.muted) {
                                            video.muted = false;
                                            video.volume = 1.0;
                                        }
                                        try {
                                            var player = document.getElementById('movie_player');
                                            if (player && player.isMuted && player.isMuted()) {
                                                player.unMute();
                                            }
                                        } catch(e) {}
                                    }, 2000);
                                }

                                // Progress polling — report currentTime/duration to native bridge
                                if (!window.kidstubeProgressInterval) {
                                    window.kidstubeProgressInterval = setInterval(function() {
                                        try {
                                            var video = document.querySelector('video');
                                            if (video && video.duration && !isNaN(video.duration)) {
                                                KidsTubeProgress.updateProgress(video.currentTime || 0, video.duration || 0);
                                            }
                                        } catch(e) {}
                                    }, 500);
                                }

                                console.log('YT_UI_HIDDEN');
                            })();
                        """.trimIndent()
                        view?.evaluateJavascript(hideYouTubeUI, null)
                    }

                    override fun onReceivedError(
                        view: android.webkit.WebView?,
                        request: android.webkit.WebResourceRequest?,
                        error: android.webkit.WebResourceError?
                    ) {
                        Log.e("KT_PLAYER", "WebView error: ${error?.description} for ${request?.url}")
                    }
                }

                loadUrl(getYouTubeUrl(videoId))
            }
        },
        modifier = modifier,
        onRelease = { it.destroy() }
    )
}
