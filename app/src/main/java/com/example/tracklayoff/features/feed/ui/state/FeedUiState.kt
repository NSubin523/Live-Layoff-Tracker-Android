package com.example.tracklayoff.features.feed.ui.state

import com.example.tracklayoff.features.feed.domain.model.Company
import kotlinx.collections.immutable.ImmutableList

sealed interface FeedUiState {
    data object Loading: FeedUiState
    data class Success(val companies: ImmutableList<Company>): FeedUiState
    data class Error(val error: String): FeedUiState
}