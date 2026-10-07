package com.example.tracklayoff.features.chat.data.remote

import com.example.tracklayoff.features.chat.data.dto.ChatHistoryResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApiService {
    @GET("chat/history")
    suspend fun getHistory(@Query("before") before: String?, @Query("limit") limit: Int): ChatHistoryResponseDto
}
