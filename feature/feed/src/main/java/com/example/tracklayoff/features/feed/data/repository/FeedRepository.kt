package com.example.tracklayoff.features.feed.data.repository

import com.example.tracklayoff.core.common.di.IoDispatcher
import com.example.tracklayoff.core.network.NetworkResult
import com.example.tracklayoff.features.feed.data.local.dao.CompanyDao
import com.example.tracklayoff.features.feed.data.remote.FeedApiService
import com.example.tracklayoff.features.feed.domain.mapper.toDomain
import com.example.tracklayoff.features.feed.domain.mapper.toEntity
import com.example.tracklayoff.features.feed.domain.model.Company
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedApiService: FeedApiService,
    private val companyDao: CompanyDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    val companiesStream: Flow<List<Company>> = companyDao.observeCompanies()
        .map { entities -> entities.map { it.toDomain() } }
        .flowOn(ioDispatcher)

    suspend fun getFeed(): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            val response = feedApiService.getLayoffFeedData()
            val entities = response.map { it.toEntity() }

            companyDao.upsertCompanies(entities)

            NetworkResult.Success(Unit)
        } catch (ex: Exception) {
            NetworkResult.Error(
                exception = ex,
                message = ex.message
            )
        }
    }
}