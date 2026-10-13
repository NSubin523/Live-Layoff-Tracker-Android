package com.example.tracklayoff.core.favorites.sync

import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher
import com.example.tracklayoff.core.common.di.IoDispatcher
import android.content.Context
import androidx.work.*
import com.example.tracklayoff.core.favorites.domain.FavoritesSyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerFavoritesScheduler @Inject constructor(@ApplicationContext private val context: Context, @IoDispatcher private val io: CoroutineDispatcher) : FavoritesSyncScheduler {
    private val work: WorkManager get() = WorkManager.getInstance(context)
    override suspend fun schedule(userId: String) = withContext(io) {
        val request = OneTimeWorkRequestBuilder<FavoritesSyncWorker>()
            .setInputData(workDataOf(FavoritesSyncWorker.USER_ID to userId))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(tag(userId)).build()
        // KEEP can lose a tap made while an existing worker is finishing.
        // Append ensures a successor; replacement recovers a previously failed chain.
        work.enqueueUniqueWork("favorite-sync:$userId", ExistingWorkPolicy.APPEND_OR_REPLACE, request).result.get()
        Unit
    }
    override suspend fun cancel(userId: String) = withContext(io) { work.cancelAllWorkByTag(tag(userId)).result.get(); Unit }
    private fun tag(userId: String) = "favorites:$userId"
}
