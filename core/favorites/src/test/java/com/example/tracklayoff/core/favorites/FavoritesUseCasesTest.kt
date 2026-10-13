package com.example.tracklayoff.core.favorites

import com.example.tracklayoff.core.common.user.UserSession
import com.example.tracklayoff.core.favorites.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesUseCasesTest {
    private class Session(id: String?) : UserSession {
        val identity = MutableStateFlow(id)
        override val userIds: Flow<String?> = identity
        override fun currentUserId() = identity.value
    }
    private class Repository : FavoritesRepository {
        val snapshots = mutableMapOf<String, MutableStateFlow<FavoritesSnapshot>>()
        val writes = mutableListOf<Triple<String, String, Boolean>>()
        var failWrite = false
        var syncs = 0
        override fun observe(userId: String) = snapshots.getOrPut(userId) {
            MutableStateFlow(FavoritesSnapshot(userId, mapOf(userId to FavoriteState(true))))
        }
        override suspend fun setSaved(userId: String, companyId: String, saved: Boolean) {
            if (failWrite) error("storage unavailable")
            writes += Triple(userId, companyId, saved)
        }
        override suspend fun synchronize(userId: String): FavoritesSyncResult { syncs++; return FavoritesSyncResult.Complete }
    }
    private class Scheduler : FavoritesSyncScheduler {
        val scheduled = mutableListOf<String>()
        var fail = false
        override suspend fun schedule(userId: String) { if (fail) error("scheduler unavailable"); scheduled += userId }
        override suspend fun cancel(userId: String) = Unit
    }
    @Test fun guestCannotWriteOrSync() = runTest {
        val session = Session(null); val repo = Repository(); val scheduler = Scheduler()
        assertEquals(FavoriteWriteResult.AuthenticationRequired, SetFavoriteUseCase(repo, scheduler, session)("c", true))
        assertEquals(FavoritesSyncResult.AuthenticationRequired, SyncFavoritesUseCase(repo, session)("a"))
        assertTrue(repo.writes.isEmpty()); assertEquals(0, repo.syncs); assertTrue(scheduler.scheduled.isEmpty())
    }
    @Test fun accountChangedBeforeWriteRejectsCapturedIdentity() = runTest {
        val repo = Repository(); val scheduler = Scheduler()
        assertEquals(FavoriteWriteResult.AuthenticationRequired, SetFavoriteUseCase(repo, scheduler, Session("b"))("c", true, "a"))
        assertTrue(repo.writes.isEmpty())
    }
    @Test fun commitPrecedesSchedulingAndSchedulerFailureDoesNotUndoCommit() = runTest {
        val repo = Repository(); val scheduler = Scheduler().apply { fail = true }
        assertEquals(FavoriteWriteResult.SavedLocally, SetFavoriteUseCase(repo, scheduler, Session("a"))("c", false))
        assertEquals(listOf(Triple("a", "c", false)), repo.writes)
    }
    @Test fun storageFailureDoesNotScheduleWork() = runTest {
        val repo = Repository().apply { failWrite = true }; val scheduler = Scheduler()
        assertEquals(FavoriteWriteResult.StorageFailure, SetFavoriteUseCase(repo, scheduler, Session("a"))("c", true))
        assertTrue(scheduler.scheduled.isEmpty())
    }
    @Test fun observationSwitchesAccountsAndClearsOnSignOut() = runTest {
        val repo = Repository(); val session = Session("a"); val results = mutableListOf<FavoritesSnapshot>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { ObserveFavoritesUseCase(repo, session)().toList(results) }
        runCurrent(); assertEquals(setOf("a"), results.last().companies.keys)
        session.identity.value = "b"; runCurrent()
        assertEquals("b", results.last().ownerUserId); assertEquals(setOf("b"), results.last().companies.keys)
        session.identity.value = null; runCurrent()
        assertEquals(FavoritesSnapshot(), results.last())
    }
}
