package com.example.tracklayoff.core.database.favorites

import androidx.room.*
import com.example.tracklayoff.features.feed.data.local.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

data class FavoriteRowState(val companyId: String, val saved: Boolean, val status: String)

@Dao
interface FavoriteDao {
    @Query("""
        SELECT f.companyId, 1 AS saved, COALESCE(o.status, 'Synced') AS status
        FROM user_favorites f LEFT JOIN favorite_outbox o
        ON f.userId = o.userId AND f.companyId = o.companyId
        WHERE f.userId = :userId
        UNION ALL
        SELECT o.companyId, o.desiredSaved AS saved, o.status
        FROM favorite_outbox o WHERE o.userId = :userId
        AND NOT EXISTS (SELECT 1 FROM user_favorites f WHERE f.userId = o.userId AND f.companyId = o.companyId)
    """)
    fun observeState(userId: String): Flow<List<FavoriteRowState>>
    @Query("SELECT companyId FROM user_favorites WHERE userId = :userId")
    fun observeIds(userId: String): Flow<List<String>>
    @Query("SELECT companyId FROM user_favorites WHERE userId = :userId")
    suspend fun getIds(userId: String): List<String>
    @Upsert suspend fun save(row: UserFavoriteEntity)
    @Query("DELETE FROM user_favorites WHERE userId = :userId AND companyId = :companyId")
    suspend fun remove(userId: String, companyId: String)
    @Query("SELECT c.* FROM companies c INNER JOIN user_favorites f ON c.id = f.companyId WHERE f.userId = :userId ORDER BY c.reported_at DESC")
    fun observeCompanies(userId: String): Flow<List<CompanyEntity>>
}
