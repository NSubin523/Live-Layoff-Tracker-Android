package com.example.tracklayoff.features.chat.domain.repository

import com.example.tracklayoff.features.chat.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getHistory(before: String?, limit: Int): ChatResult<ChatHistoryPage>
    fun sendMessage(prompt: String): Flow<ChatStreamEvent>
}
