package com.example.tracklayoff.features.feed.ui.composables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.tracklayoff.features.feed.ui.viewmodel.FeedViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tracklayoff.core.common.ui.composables.ObserveAppEvent
import com.example.tracklayoff.core.common.ui.extension.showAppCustomSnackBar
import com.example.tracklayoff.features.feed.ui.state.FeedUiEvent

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by feedViewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing by feedViewModel.isRefreshing.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    ObserveAppEvent(feedViewModel.uiEvent) { event ->
        when(event) {
            is FeedUiEvent.ShowToast -> {
                snackBarHostState.showAppCustomSnackBar(
                    scope = coroutineScope,
                    message = event.message
                )
            }
        }
    }

    FeedContent(
        state = uiState,
        modifier = modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        pullToRefreshState = pullToRefreshState,
        onRefresh = { feedViewModel.fetchFeedData(isPullToRefresh = true) }
    )
}