package com.example.tracklayoff.core.favorites

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.tracklayoff.core.common.user.UserSession
import com.example.tracklayoff.core.favorites.domain.*
import com.example.tracklayoff.core.favorites.sync.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class FavoritesWorkerTest {
    private class Session : UserSession {
        override val userIds = flowOf("a")
        override fun currentUserId() = "a"
    }
    private class Repo : FavoritesRepository {
        var result = FavoritesSyncResult.Complete
        var calls = 0
        override fun observe(userId: String) = flowOf(FavoritesSnapshot(userId))
        override suspend fun setSaved(userId: String, companyId: String, saved: Boolean) = Unit
        override suspend fun synchronize(userId: String): FavoritesSyncResult { calls++; return result }
    }
    private fun factory(repo: Repo) = object : WorkerFactory() {
        override fun createWorker(context: Context, workerClassName: String, parameters: WorkerParameters): ListenableWorker? =
            if (workerClassName == FavoritesSyncWorker::class.java.name) FavoritesSyncWorker(context, parameters, SyncFavoritesUseCase(repo, Session())) else null
    }
    @Test fun workerRetriesTransientFailureAndNeverSendsForAnotherAccount() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val repo = Repo().apply { result = FavoritesSyncResult.Retry }
        val worker = TestListenableWorkerBuilder<FavoritesSyncWorker>(context)
            .setWorkerFactory(factory(repo)).setInputData(workDataOf(FavoritesSyncWorker.USER_ID to "a")).build()
        assertEquals(ListenableWorker.Result.retry(), worker.doWork())
        val other = TestListenableWorkerBuilder<FavoritesSyncWorker>(context)
            .setWorkerFactory(factory(repo)).setInputData(workDataOf(FavoritesSyncWorker.USER_ID to "b")).build()
        assertEquals(ListenableWorker.Result.success(), other.doWork())
        assertEquals(1, repo.calls)
    }
    @Test fun schedulingKeepsSuccessorAndCancelsOnlySelectedAccount() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        WorkManagerTestInitHelper.initializeTestWorkManager(context, Configuration.Builder().setWorkerFactory(factory(Repo())).build())
        val scheduler = WorkManagerFavoritesScheduler(context, Dispatchers.IO)
        scheduler.schedule("a"); scheduler.schedule("a"); scheduler.schedule("b")
        val work = WorkManager.getInstance(context)
        val a = work.getWorkInfosForUniqueWork("favorite-sync:a").get()
        assertEquals(2, a.size)
        assertTrue(a.all { it.constraints.requiredNetworkType == NetworkType.CONNECTED })
        assertTrue(a.any { it.state == WorkInfo.State.BLOCKED })
        scheduler.cancel("a")
        assertTrue(work.getWorkInfosForUniqueWork("favorite-sync:a").get().all { it.state == WorkInfo.State.CANCELLED })
        assertFalse(work.getWorkInfosForUniqueWork("favorite-sync:b").get().single().state.isFinished)
        scheduler.cancel("b")
    }
}
