package com.example.tracklayoff.core.favorites.domain

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observe(userId: String): Flow<FavoritesSnapshot>
    suspend fun setSaved(userId: String, companyId: String, saved: Boolean)
    suspend fun synchronize(userId: String): FavoritesSyncResult
}
interface FavoritesSyncScheduler {
    suspend fun schedule(userId: String)
    suspend fun cancel(userId: String)
}
interface FavoritesTokenProvider {
    suspend fun tokenFor(userId: String): String?
}
