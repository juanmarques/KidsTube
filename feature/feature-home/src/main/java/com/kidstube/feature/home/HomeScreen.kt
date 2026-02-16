package com.kidstube.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidstube.core.ui.components.ErrorState
import com.kidstube.core.ui.components.LoadingState
import com.kidstube.core.ui.components.VideoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onVideoClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(
                message = uiState.error!!,
                onRetry = { viewModel.loadFeed() }
            )
            else -> {
                val gridState = rememberLazyGridState()

                // Infinite scroll: trigger loadMore and shuffle when near end
                val shouldLoadMore by remember {
                    derivedStateOf {
                        val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        val totalItems = gridState.layoutInfo.totalItemsCount
                        lastVisibleItem >= totalItems - 4 && totalItems > 6
                    }
                }

                LaunchedEffect(shouldLoadMore) {
                    if (shouldLoadMore && !uiState.isLoadingMore) {
                        viewModel.loadMore()
                    }
                }

                // Pull to refresh - shuffles and loads fresh content
                PullToRefreshBox(
                    isRefreshing = uiState.isLoadingMore,
                    onRefresh = { viewModel.loadFeed() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.videos, key = { it.id }) { video ->
                            VideoCard(
                                thumbnailUrl = video.thumbnailUrl,
                                onClick = { onVideoClick(video.id) },
                                isFavorite = video.channelId in uiState.favoriteChannelIds,
                                channelTitle = video.channelTitle,
                                videoTitle = video.title
                            )
                        }
                    }
                }
            }
        }

        // Settings gear: tiny, low opacity, top-right — invisible to toddlers
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(32.dp)
                .alpha(0.3f)
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
