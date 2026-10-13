package com.example.tracklayoff.core.favorites.domain

import com.example.tracklayoff.core.common.user.UserSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(private val repository: FavoritesRepository, private val session: UserSession) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = session.userIds.distinctUntilChanged().flatMapLatest { userId ->
        if (userId == null) flowOf(FavoritesSnapshot()) else repository.observe(userId)
    }.map { snapshot ->
        // Never emit old-account data during the identity-observer transition.
        if (snapshot.ownerUserId == session.currentUserId()) snapshot else FavoritesSnapshot()
    }
}
class SetFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository,
    private val scheduler: FavoritesSyncScheduler,
    private val session: UserSession
) {
    suspend operator fun invoke(companyId: String, saved: Boolean, expectedUserId: String? = session.currentUserId()): FavoriteWriteResult {
        val userId = session.currentUserId() ?: return FavoriteWriteResult.AuthenticationRequired
        if (userId != expectedUserId) return FavoriteWriteResult.AuthenticationRequired
        try {
            require(companyId.isNotBlank())
            repository.setSaved(userId, companyId, saved)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { return FavoriteWriteResult.StorageFailure }
        // A committed outbox survives a scheduling failure; startup/sign-in recovers it.
        try {
            if (session.currentUserId() == userId) scheduler.schedule(userId)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { /* Keep the committed local intention. */ }
        return FavoriteWriteResult.SavedLocally
    }
}
class SyncFavoritesUseCase @Inject constructor(private val repository: FavoritesRepository, private val session: UserSession) {
    suspend operator fun invoke(userId: String): FavoritesSyncResult =
        if (session.currentUserId() != userId) FavoritesSyncResult.AuthenticationRequired else repository.synchronize(userId)
}
