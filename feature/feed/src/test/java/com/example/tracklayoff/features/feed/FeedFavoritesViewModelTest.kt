package com.example.tracklayoff.features.feed

import androidx.lifecycle.viewModelScope
import com.example.tracklayoff.core.common.user.UserSession
import com.example.tracklayoff.core.favorites.domain.*
import com.example.tracklayoff.features.feed.ui.state.*
import com.example.tracklayoff.features.feed.ui.viewmodel.FeedFavoritesViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class FeedFavoritesViewModelTest {
    private class Session(id: String?) : UserSession {
        val ids = MutableStateFlow(id)
        override val userIds: Flow<String?> = ids
        override fun currentUserId() = ids.value
    }
    private class Repo : FavoritesRepository {
        val states = mutableMapOf<String, MutableStateFlow<FavoritesSnapshot>>()
        val writes = mutableListOf<Triple<String, String, Boolean>>()
        var gate: CompletableDeferred<Unit>? = null
        override fun observe(userId: String) = states.getOrPut(userId) { MutableStateFlow(FavoritesSnapshot(userId)) }
        override suspend fun setSaved(userId: String, companyId: String, saved: Boolean) {
            writes += Triple(userId, companyId, saved); gate?.await()
            states.getValue(userId).value = FavoritesSnapshot(userId, mapOf(companyId to FavoriteState(saved, FavoriteSyncState.Pending)))
        }
        override suspend fun synchronize(userId: String) = FavoritesSyncResult.Complete
    }
    private val scheduler = object : FavoritesSyncScheduler {
        override suspend fun schedule(userId: String) = Unit
        override suspend fun cancel(userId: String) = Unit
    }
    private fun withVm(id: String?, block: suspend TestScope.(FeedFavoritesViewModel, Repo, Session) -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = Repo(); val session = Session(id)
        val vm = FeedFavoritesViewModel(ObserveFavoritesUseCase(repo, session), SetFavoriteUseCase(repo, scheduler, session), session)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        runCurrent()
        try { block(vm, repo, session) } finally { vm.viewModelScope.cancel(); runCurrent(); Dispatchers.resetMain() }
    }
    @Test fun guestTapRequestsSharedSignIn() = withVm(null) { vm, repo, _ ->
        vm.toggle("c")
        assertEquals(FeedFavoriteEvent.SignInRequired, vm.uiEvents.first())
        assertTrue(repo.writes.isEmpty())
    }
    @Test fun duplicateTapsAreBlockedOnlyDuringLocalCommit() = withVm("a") { vm, repo, _ ->
        repo.gate = CompletableDeferred()
        vm.toggle("c"); vm.toggle("c"); runCurrent()
        assertEquals(1, repo.writes.size); assertTrue(vm.uiState.value.cards.getValue("c").isWriting)
        repo.gate!!.complete(Unit); runCurrent()
        assertTrue(vm.uiState.value.cards.getValue("c").isSaved)
        assertFalse(vm.uiState.value.cards.getValue("c").isWriting)
        // Remote sync is still pending; the user can immediately reverse the intention.
        vm.toggle("c"); runCurrent()
        assertEquals(listOf(Triple("a", "c", true), Triple("a", "c", false)), repo.writes)
        assertFalse(vm.uiState.value.cards.getValue("c").isSaved)
    }
    @Test fun switchingAccountsHidesPreviousFavorites() = withVm("a") { vm, _, session ->
        vm.toggle("c"); runCurrent(); assertTrue(vm.uiState.value.cards.getValue("c").isSaved)
        session.ids.value = "b"; runCurrent()
        assertEquals("b", vm.uiState.value.ownerUserId); assertTrue(vm.uiState.value.cards.isEmpty())
        session.ids.value = null; runCurrent()
        assertNull(vm.uiState.value.ownerUserId); assertTrue(vm.uiState.value.cards.isEmpty())
    }
}
