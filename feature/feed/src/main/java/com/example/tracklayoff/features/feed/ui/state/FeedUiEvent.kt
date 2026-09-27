package com.example.tracklayoff.features.feed.ui.state

sealed interface FeedUiEvent {
    data class ShowToast(val message: String): FeedUiEvent
}