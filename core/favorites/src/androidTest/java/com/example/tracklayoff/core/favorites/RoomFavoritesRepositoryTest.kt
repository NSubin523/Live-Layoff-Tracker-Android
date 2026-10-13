package com.example.tracklayoff.core.favorites

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tracklayoff.core.database.AppDatabase
import com.example.tracklayoff.core.favorites.data.*
import com.example.tracklayoff.core.favorites.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import org.junit.*
import org.junit.Assert.*
import java.io.IOException

class RoomFavoritesRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: RoomFavoritesRepository
    private val api = FakeApi()
    private var currentUser: String? = "a"
    private class FakeApi : FavoritesApi {
        val remote = linkedSetOf<String>()
        val requests = mutableListOf<Pair<String, Boolean>>()
        var afterWrite: suspend () -> Unit = {}
        var beforeRead: suspend () -> Unit = {}
        override suspend fun setSaved(authorization: String, companyId: String, body: SetFavoriteRequestDto): FavoriteResponseDto {
            requests += companyId to body.saved
            if (body.saved) remote += companyId else remote -= companyId
            afterWrite()
            return FavoriteResponseDto(companyId, body.saved)
        }
        override suspend fun getIds(authorization: String): List<String> { beforeRead(); return remote.toList() }
        override suspend fun getCompanies(authorization: String) = emptyList<FavoriteCompanyDto>()
    }
    @Before fun setup() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java).build()
        repo = RoomFavoritesRepository(db, api, object : FavoritesTokenProvider {
            override suspend fun tokenFor(userId: String) = if (currentUser == userId) "token-$userId" else null
        }, Dispatchers.IO)
    }
    @After fun close() { db.close() }
    @Test fun rapidIntentionsCoalesceAndUnsaveRetainsOutbox() = runTest {
        repo.setSaved("a", "c", true); repo.setSaved("a", "c", false)
        assertTrue(db.favoriteDao().getIds("a").isEmpty())
        val row = db.favoriteOutboxDao().pending("a").single()
        assertFalse(row.desiredSaved)
        assertEquals(FavoritesSyncResult.Complete, repo.synchronize("a"))
        assertEquals(listOf("c" to false), api.requests)
        assertTrue(db.favoriteOutboxDao().pending("a").isEmpty())
    }
    @Test fun lostAcknowledgementRetriesExplicitStateWithoutToggling() = runTest {
        repo.setSaved("a", "c", true)
        api.afterWrite = { throw IOException("response lost after server committed") }
        assertEquals(FavoritesSyncResult.Retry, repo.synchronize("a"))
        assertEquals(setOf("c"), api.remote)
        assertTrue(repo.observe("a").first().companies.getValue("c").isSaved)
        api.afterWrite = {}
        assertEquals(FavoritesSyncResult.Complete, repo.synchronize("a"))
        assertEquals(listOf("c" to true, "c" to true), api.requests)
        assertEquals(setOf("c"), api.remote)
    }
    @Test fun newerIntentionSurvivesOldAcknowledgement() = runTest {
        repo.setSaved("a", "c", true)
        var changed = false
        api.afterWrite = {
            if (!changed) {
                changed = true
                repo.setSaved("a", "c", false)
            }
        }
        assertEquals(FavoritesSyncResult.Complete, repo.synchronize("a"))
        assertEquals(listOf("c" to true, "c" to false), api.requests)
        assertTrue(api.remote.isEmpty()); assertTrue(db.favoriteDao().getIds("a").isEmpty())
    }
    @Test fun readBackCannotOverwriteAnUnsentLocalChange() = runTest {
        api.remote += "c"
        api.beforeRead = { repo.setSaved("a", "c", false) }
        assertEquals(FavoritesSyncResult.Retry, repo.synchronize("a"))
        assertTrue(db.favoriteDao().getIds("a").isEmpty())
        assertFalse(db.favoriteOutboxDao().pending("a").single().desiredSaved)
    }
    @Test fun wrongAccountCannotSendPendingWorkAndStateIsIsolated() = runTest {
        repo.setSaved("a", "c", true); currentUser = "b"
        assertEquals(FavoritesSyncResult.AuthenticationRequired, repo.synchronize("a"))
        assertTrue(api.requests.isEmpty()); assertTrue(repo.observe("b").first().companies.isEmpty())
        assertEquals(FavoriteSyncState.AuthenticationRequired, repo.observe("a").first().companies.getValue("c").sync)
        currentUser = "a"
        assertEquals(FavoritesSyncResult.Complete, repo.synchronize("a"))
    }
    @Test fun rateLimitKeepsIntentForRetry() = runTest {
        repo.setSaved("a", "c", true)
        api.afterWrite = { throw HttpException(Response.error<FavoriteResponseDto>(429, "limited".toResponseBody())) }
        assertEquals(FavoritesSyncResult.Retry, repo.synchronize("a"))
        assertEquals("Pending", db.favoriteOutboxDao().pending("a").single().status)
    }
    @Test fun danglingRemoteIdIsPreservedWithoutCompanyObject() = runTest {
        api.remote += "missing-company"
        assertEquals(FavoritesSyncResult.Complete, repo.synchronize("a"))
        assertEquals(listOf("missing-company"), db.favoriteDao().getIds("a"))
    }
    @Test fun permanentRejectionKeepsLocalStateAndDoesNotRetryForever() = runTest {
        repo.setSaved("a", "c", true)
        api.afterWrite = { throw HttpException(Response.error<FavoriteResponseDto>(400, "bad request".toResponseBody())) }
        assertEquals(FavoritesSyncResult.Failed, repo.synchronize("a"))
        assertEquals(FavoriteSyncState.Failed, repo.observe("a").first().companies.getValue("c").sync)
        assertEquals(FavoritesSyncResult.Failed, repo.synchronize("a"))
        assertEquals(1, api.requests.size)
    }
    @Test fun pendingIntentSurvivesDatabaseReopening() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "favorites-durability-test"
        context.deleteDatabase(name)
        db.close()
        db = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
        val tokens = object : FavoritesTokenProvider { override suspend fun tokenFor(userId: String) = "token" }
        repo = RoomFavoritesRepository(db, api, tokens, Dispatchers.IO)
        try {
            repo.setSaved("a", "c", true)
            db.close()
            db = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
            repo = RoomFavoritesRepository(db, api, tokens, Dispatchers.IO)
            assertTrue(repo.observe("a").first().companies.getValue("c").isSaved)
            assertEquals(1, db.favoriteOutboxDao().pending("a").size)
            assertEquals(FavoritesSyncResult.Complete, repo.synchronize("a"))
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
