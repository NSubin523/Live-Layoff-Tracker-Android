package com.example.tracklayoff.core.favorites.sync

import android.content.Context
import android.database.sqlite.SQLiteException
import kotlinx.coroutines.CancellationException
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tracklayoff.core.favorites.domain.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FavoritesSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val sync: SyncFavoritesUseCase
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val userId = inputData.getString(USER_ID) ?: return Result.failure()
        return try { when (sync(userId)) {
            FavoritesSyncResult.Complete, FavoritesSyncResult.AuthenticationRequired -> Result.success()
            FavoritesSyncResult.Retry -> Result.retry()
            FavoritesSyncResult.Failed -> Result.failure()
        } } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: SQLiteException) {
            // A temporary database failure must not discard the durable sync request.
            Result.retry()
        }
    }
    companion object { const val USER_ID = "favorite_user_id" }
}
