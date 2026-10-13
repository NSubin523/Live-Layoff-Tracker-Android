package com.example.tracklayoff.features.feed.ui.state

import androidx.compose.runtime.Immutable
import com.example.tracklayoff.core.favorites.domain.FavoriteSyncState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class FavoriteCardUiState(
    val isSaved: Boolean = false,
    val isWriting: Boolean = false,
    val sync: FavoriteSyncState = FavoriteSyncState.Synced
)
@Immutable
data class FeedFavoritesUiState(
    val ownerUserId: String? = null,
    val cards: ImmutableMap<String, FavoriteCardUiState> = persistentMapOf()
)
sealed interface FeedFavoriteEvent {
    data object SignInRequired : FeedFavoriteEvent
    data object LocalWriteFailed : FeedFavoriteEvent
}
