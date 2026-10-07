package com.example.tracklayoff.features.chat.domain.usecase

import com.example.tracklayoff.features.chat.domain.repository.ChatRepository
import javax.inject.Inject

class LoadChatHistoryUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(before: String? = null) = repository.getHistory(before, PAGE_SIZE)
    companion object { const val PAGE_SIZE = 20 }
}
