package com.example.tracklayoff.features.feed.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tracklayoff.core.common.ui.composables.AppLazyColumn
import com.example.tracklayoff.core.common.ui.composables.AppLoadingOverlay
import com.example.tracklayoff.core.common.ui.composables.EmptyScreen
import com.example.tracklayoff.features.feed.ui.state.FeedUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    state : FeedUiState,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    pullToRefreshState: PullToRefreshState,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is FeedUiState.Error -> {
                    EmptyScreen(
                        title = "Connection Error",
                        subtitle = "Connection Error. Please try again later."
                    )
                }

                FeedUiState.Loading -> {
                    AppLoadingOverlay(isLoading = state is FeedUiState.Loading)
                }

                is FeedUiState.Success -> {
                    AppLazyColumn(
                        items = state.companies,
                        key = { company -> company.id },
                        emptyScreen = {
                            EmptyScreen(
                                title = "No companies found",
                                subtitle = "Firestore database is empty. No records found"
                            )
                        }
                    ) { company ->
                        LayoffCardItem(company = company)
                    }
                }
            }
        }
    }
}