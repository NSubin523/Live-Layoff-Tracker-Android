package com.example.tracklayoff.core.database.favorites

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteOutboxDao {
    @Upsert suspend fun upsert(row: FavoriteOutboxEntity)
    @Query("SELECT * FROM favorite_outbox WHERE userId = :userId")
    suspend fun pending(userId: String): List<FavoriteOutboxEntity>
    @Query("SELECT * FROM favorite_outbox WHERE userId = :userId")
    fun observe(userId: String): Flow<List<FavoriteOutboxEntity>>
    @Query("UPDATE favorite_outbox SET status = :status WHERE userId = :userId AND companyId = :companyId AND revision = :revision")
    suspend fun mark(userId: String, companyId: String, revision: String, status: String)
    @Query("DELETE FROM favorite_outbox WHERE userId = :userId AND companyId = :companyId AND revision = :revision")
    suspend fun acknowledge(userId: String, companyId: String, revision: String): Int
}
