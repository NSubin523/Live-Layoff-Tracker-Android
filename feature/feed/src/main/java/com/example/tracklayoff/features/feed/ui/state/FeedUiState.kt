package com.example.tracklayoff.features.feed.ui.state

import com.example.tracklayoff.features.feed.domain.model.Company
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

sealed interface FeedUiState {
    data object Loading: FeedUiState
    data class Success(val companies: ImmutableList<Company>): FeedUiState {
        val cards = companies.map { it.toCardUiModel() }.toImmutableList()
    }
    data class Error(val error: String): FeedUiState
}