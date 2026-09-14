package com.example.tracklayoff.features.feed.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.tracklayoff.features.feed.data.local.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {

    @Query("SELECT * FROM companies ORDER BY reported_at DESC")
    fun observeCompanies(): Flow<List<CompanyEntity>>

    @Upsert
    suspend fun upsertCompanies(companies: List<CompanyEntity>)

    @Query("DELETE from companies")
    suspend fun clearAll()

    suspend fun clearAndInsert(companies: List<CompanyEntity>) {
        clearAll()
        upsertCompanies(companies)
    }
}