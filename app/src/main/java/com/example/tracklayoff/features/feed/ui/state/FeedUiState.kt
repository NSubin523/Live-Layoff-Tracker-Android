package com.example.tracklayoff.features.feed.ui.state

import com.example.tracklayoff.features.feed.domain.model.Company

sealed interface FeedUiState {
    data object Loading: FeedUiState
    data class Success(val companies: List<Company>): FeedUiState
    data class Error(val error: String): FeedUiState
}