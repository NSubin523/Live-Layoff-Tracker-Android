package com.example.tracklayoff.features.feed.data.repository

import com.example.tracklayoff.core.common.di.IoDispatcher
import com.example.tracklayoff.core.network.NetworkResult
import com.example.tracklayoff.features.feed.data.remote.FeedApiService
import com.example.tracklayoff.features.feed.domain.mapper.toDomain
import com.example.tracklayoff.features.feed.domain.model.Company
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedApiService: FeedApiService,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getFeed(): NetworkResult<List<Company>> = withContext(ioDispatcher) {
        try {
            val response = feedApiService.getLayoffFeedData()
            val result = response.map { it.toDomain() }
            NetworkResult.Success(result)
        } catch (ex: Exception) {
            NetworkResult.Error(
                exception = ex,
                message = ex.message
            )
        }
    }
}