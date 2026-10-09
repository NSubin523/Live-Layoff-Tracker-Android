package com.example.tracklayoff.features.feed

import androidx.lifecycle.viewModelScope
import com.example.tracklayoff.core.common.util.AppLifecycleTracker
import com.example.tracklayoff.core.network.*
import com.example.tracklayoff.features.feed.data.dto.LayoffResponseDto
import com.example.tracklayoff.features.feed.data.local.dao.CompanyDao
import com.example.tracklayoff.features.feed.data.local.entity.CompanyEntity
import com.example.tracklayoff.features.feed.data.remote.FeedApiService
import com.example.tracklayoff.features.feed.data.repository.FeedRepository
import com.example.tracklayoff.features.feed.domain.mapper.*
import com.example.tracklayoff.features.feed.domain.model.*
import com.example.tracklayoff.features.feed.ui.state.*
import com.example.tracklayoff.features.feed.ui.viewmodel.FeedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class FeedTest {
    private fun dto(status: String = "Confirmed", trend: String = "increasing", date: String = "2026-10-08T12:00:00Z") =
        LayoffResponseDto("company", "Acme", 100, status, "Technology", "New York", date, null, trend)
    private class Dao : CompanyDao {
        val rows = MutableStateFlow<List<CompanyEntity>>(emptyList())
        override fun observeCompanies() = rows
        override suspend fun upsertCompanies(companies: List<CompanyEntity>) { rows.value = companies }
        override suspend fun clearAll() { rows.value = emptyList() }
    }
    private class Api : FeedApiService {
        var answer: suspend () -> List<LayoffResponseDto> = { emptyList() }
        var calls = 0
        override suspend fun getLayoffFeedData(): List<LayoffResponseDto> { calls++; return answer() }
    }
    @Test fun `API and cache mapping agree for known statuses and trends`() {
        for (status in listOf("Confirmed", "Rumored")) for (trend in listOf("increasing", "decreasing", "stable")) {
            val response = dto(status, trend)
            assertEquals(response.toDomain(), response.toEntity().toDomain())
            assertNotEquals(LayoffStatus.UNKNOWN, response.toEntity().toDomain().layoffStatus)
        }
        val unknown = dto("Other", "other").toEntity().toDomain()
        assertEquals(LayoffStatus.UNKNOWN, unknown.layoffStatus)
        assertEquals(TrendDirection.UNKNOWN, unknown.trendDirection)
    }
    @Test fun `invalid dates fall back to current time without losing optional values`() {
        val before = Instant.now().minusSeconds(1)
        val response = dto(date = "invalid").copy(impactCount = null, logoUrl = null)
        val direct = response.toDomain(); val cached = response.toEntity().toDomain()
        assertTrue(direct.reportedAt >= before); assertTrue(cached.reportedAt >= before)
        assertNull(cached.impactCount); assertNull(cached.logoUrl)
    }
    @Test fun `refresh publishes mapped rows and network failure preserves cached data`() = runTest {
        val api = Api(); val dao = Dao(); val repo = FeedRepository(api, dao, StandardTestDispatcher(testScheduler))
        api.answer = { listOf(dto()) }
        assertTrue(repo.getFeed() is NetworkResult.Success)
        assertEquals(LayoffStatus.CONFIRMED, repo.companiesStream.first().single().layoffStatus)
        api.answer = { throw java.io.IOException("Offline") }
        val result = repo.getFeed() as NetworkResult.Error
        assertEquals("Offline", result.message)
        assertEquals("Acme", repo.companiesStream.first().single().companyName)
    }
    @Test fun `repository does not swallow cancellation`() = runTest {
        val api = Api(); api.answer = { throw CancellationException("cancel") }
        val repo = FeedRepository(api, Dao(), StandardTestDispatcher(testScheduler))
        try { repo.getFeed(); fail("Expected cancellation") } catch (_: CancellationException) { }
    }
    private fun withVm(block: suspend TestScope.(FeedViewModel, Api, Dao, MutableStateFlow<NetworkStatus>, MutableStateFlow<Boolean>) -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val api = Api(); val dao = Dao()
        val network = MutableStateFlow(NetworkStatus.AVAILABLE)
        val foreground = MutableStateFlow(false)
        val observer = mock(NetworkObserver::class.java)
        `when`(observer.networkStatus).thenReturn(network)
        val lifecycle = mock(AppLifecycleTracker::class.java)
        `when`(lifecycle.isAppInForeground).thenReturn(foreground)
        `when`(lifecycle.lastBackgroundTimeStamp).thenReturn(System.currentTimeMillis() - 600_000)
        val vm = FeedViewModel(FeedRepository(api, dao, StandardTestDispatcher(testScheduler)), observer, lifecycle)
        val collection = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        try { block(vm, api, dao, network, foreground) } finally {
            collection.cancel()
            vm.viewModelScope.cancel()
            runCurrent()
            Dispatchers.resetMain()
        }
    }
    @Test fun `initial fetch ends in empty success`() = withVm { vm, api, _, _, _ ->
        runCurrent(); assertEquals(1, api.calls)
        assertTrue(vm.uiState.value is FeedUiState.Success)
        assertTrue((vm.uiState.value as FeedUiState.Success).companies.isEmpty())
    }
    @Test fun `uncached failure shows error and retry shows loaded companies`() = withVm { vm, api, _, _, _ ->
        api.answer = { throw java.io.IOException("Offline") }; runCurrent()
        assertEquals(FeedUiState.Error("Offline"), vm.uiState.value)
        api.answer = { listOf(dto()) }; vm.fetchFeedData(); runCurrent()
        assertEquals("Acme", (vm.uiState.value as FeedUiState.Success).companies.single().companyName)
    }
    @Test fun `pull refresh exposes progress and preserves content on error`() = withVm { vm, api, dao, _, _ ->
        api.answer = { listOf(dto()) }; runCurrent()
        val gate = CompletableDeferred<Unit>()
        api.answer = { gate.await(); throw java.io.IOException("Offline") }
        vm.fetchFeedData(isPullToRefresh = true); runCurrent()
        assertTrue(vm.isRefreshing.value)
        assertTrue(vm.uiState.value is FeedUiState.Success)
        gate.complete(Unit); runCurrent()
        assertFalse(vm.isRefreshing.value)
        assertTrue(vm.uiState.value is FeedUiState.Success)
    }
    @Test fun `network events omit initial state and fresh foreground does not fetch`() = withVm { vm, api, _, network, foreground ->
        val events = mutableListOf<FeedUiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiEvent.toList(events) }
        runCurrent(); assertTrue(events.isEmpty())
        network.value = NetworkStatus.DISCONNECTED; runCurrent()
        assertTrue((events.single() as FeedUiEvent.ShowToast).message.contains("Unavailable"))
        foreground.value = true; runCurrent(); assertEquals(1, api.calls)
    }
    @Test fun `foreground retries stale failed feed after a long background period`() = withVm { vm, api, _, _, foreground ->
        api.answer = { throw java.io.IOException("Offline") }; runCurrent()
        assertEquals(1, api.calls)
        api.answer = { listOf(dto()) }
        foreground.value = true; runCurrent()
        assertEquals(2, api.calls)
        assertTrue(vm.uiState.value is FeedUiState.Success)
    }

}
