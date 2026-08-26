package com.example.tracklayoff.features.feed.data.remote

import com.example.tracklayoff.features.feed.data.dto.LayoffResponseDto
import retrofit2.http.GET

interface FeedApiService {

    @GET("feed")
    suspend fun getLayoffFeedData(): List<LayoffResponseDto>
}