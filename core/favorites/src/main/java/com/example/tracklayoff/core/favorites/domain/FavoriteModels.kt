package com.example.tracklayoff.core.favorites.domain

/** Saved membership and remote delivery are independent concerns. */
enum class FavoriteSyncState { Synced, Pending, Syncing, Failed, AuthenticationRequired }
data class FavoriteState(val isSaved: Boolean = false, val sync: FavoriteSyncState = FavoriteSyncState.Synced)
data class FavoritesSnapshot(val ownerUserId: String? = null, val companies: Map<String, FavoriteState> = emptyMap())
sealed interface FavoriteWriteResult {
    data object SavedLocally : FavoriteWriteResult
    data object AuthenticationRequired : FavoriteWriteResult
    data object StorageFailure : FavoriteWriteResult
}
enum class FavoritesSyncResult { Complete, Retry, AuthenticationRequired, Failed }
