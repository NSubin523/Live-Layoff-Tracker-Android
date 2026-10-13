package com.example.tracklayoff.core.favorites.data

import androidx.room.withTransaction
import com.example.tracklayoff.core.common.di.IoDispatcher
import com.example.tracklayoff.core.database.AppDatabase
import com.example.tracklayoff.core.database.favorites.*
import com.example.tracklayoff.core.favorites.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomFavoritesRepository @Inject constructor(
    private val database: AppDatabase,
    private val api: FavoritesApi,
    private val tokens: FavoritesTokenProvider,
    @IoDispatcher private val io: CoroutineDispatcher
) : FavoritesRepository {
    private val favorites = database.favoriteDao()
    private val outbox = database.favoriteOutboxDao()
    private val syncMutex = Mutex()

    override fun observe(userId: String) = favorites.observeState(userId).map { rows ->
        FavoritesSnapshot(userId, rows.associate { row ->
            row.companyId to FavoriteState(row.saved,
                runCatching { FavoriteSyncState.valueOf(row.status) }.getOrDefault(FavoriteSyncState.Pending))
        })
    }.flowOn(io)

    override suspend fun setSaved(userId: String, companyId: String, saved: Boolean) = withContext(io) {
        database.withTransaction {
            if (saved) favorites.save(UserFavoriteEntity(userId, companyId)) else favorites.remove(userId, companyId)
            outbox.upsert(FavoriteOutboxEntity(userId, companyId, saved, UUID.randomUUID().toString(), FavoriteSyncState.Pending.name))
        }
    }

    override suspend fun synchronize(userId: String): FavoritesSyncResult = withContext(io) {
        // Serial delivery also protects callers outside WorkManager.
        syncMutex.withLock {
            while (true) {
                ensureActive()
                val rows = outbox.pending(userId).filter { it.status != FavoriteSyncState.Failed.name }
                if (rows.isEmpty()) break
                for (row in rows) {
                    ensureActive()
                    try {
                        val auth = tokens.tokenFor(userId) ?: return@withLock authenticationRequired(row)
                        outbox.mark(userId, row.companyId, row.revision, FavoriteSyncState.Syncing.name)
                        val result = api.setSaved("Bearer $auth", row.companyId, SetFavoriteRequestDto(row.desiredSaved))
                        check(result.companyId == row.companyId && result.saved == row.desiredSaved) { "Invalid favorite acknowledgement" }
                        // Never replace membership or delete a newer intention with an old response.
                        outbox.acknowledge(userId, row.companyId, row.revision)
                    } catch (cancelled: CancellationException) {
                        withContext(NonCancellable) { outbox.mark(userId, row.companyId, row.revision, FavoriteSyncState.Pending.name) }
                        throw cancelled
                    } catch (error: Exception) {
                        val result = classify(error)
                        val state = when (result) {
                            FavoritesSyncResult.AuthenticationRequired -> FavoriteSyncState.AuthenticationRequired
                            FavoritesSyncResult.Retry -> FavoriteSyncState.Pending
                            else -> FavoriteSyncState.Failed
                        }
                        outbox.mark(userId, row.companyId, row.revision, state.name)
                        if (result != FavoritesSyncResult.Failed) return@withLock result
                    }
                }
            }
            // Read-back restores favorites on a new install and reflects other devices.
            // Each remote call obtains a token bound to this account, never the next account.
            try {
                val token = tokens.tokenFor(userId) ?: return@withLock FavoritesSyncResult.AuthenticationRequired
                val remoteIds = api.getIds("Bearer $token").toSet()
                check(remoteIds.none { it.isBlank() })
                val companyToken = tokens.tokenFor(userId) ?: return@withLock FavoritesSyncResult.AuthenticationRequired
                val companies = api.getCompanies("Bearer $companyToken").mapNotNull { it.toEntity() }
                database.withTransaction {
                    val protectedIds = outbox.pending(userId).mapTo(hashSetOf()) { it.companyId }
                    (favorites.getIds(userId).toSet() - remoteIds - protectedIds).forEach { favorites.remove(userId, it) }
                    (remoteIds - protectedIds).forEach { favorites.save(UserFavoriteEntity(userId, it)) }
                    database.companyDao().upsertCompanies(companies)
                }
                if (outbox.pending(userId).any { it.status == FavoriteSyncState.Failed.name }) FavoritesSyncResult.Failed
                else if (outbox.pending(userId).isNotEmpty()) FavoritesSyncResult.Retry else FavoritesSyncResult.Complete
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { classify(error) }
        }
    }
    private suspend fun authenticationRequired(row: FavoriteOutboxEntity): FavoritesSyncResult {
        outbox.mark(row.userId, row.companyId, row.revision, FavoriteSyncState.AuthenticationRequired.name)
        return FavoritesSyncResult.AuthenticationRequired
    }
    private fun classify(error: Exception): FavoritesSyncResult = when (error) {
        is HttpException -> when (error.code()) {
            401, 403 -> FavoritesSyncResult.AuthenticationRequired
            408, 429 -> FavoritesSyncResult.Retry
            in 500..599 -> FavoritesSyncResult.Retry
            else -> FavoritesSyncResult.Failed
        }
        is IOException -> FavoritesSyncResult.Retry
        else -> FavoritesSyncResult.Failed
    }
}
