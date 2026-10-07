package com.example.tracklayoff.features.chat.domain.usecase

import com.example.tracklayoff.features.chat.domain.model.*
import com.example.tracklayoff.features.chat.domain.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(draft: String): Flow<ChatReplyUpdate> = flow {
        val prompt = draft.trim()
        if (prompt.isBlank() || prompt.codePointCount(0, prompt.length) > MAX_PROMPT_LENGTH) {
            emit(ChatReplyUpdate.Failed(ChatFailure.InvalidPrompt))
            return@flow
        }
        emit(ChatReplyUpdate.Started(prompt))
        var terminal = false
        repository.sendMessage(prompt)
            .catch { error ->
                if (error is CancellationException) throw error
                emit(ChatStreamEvent.Error(ChatFailure.Service))
            }
            .onEach { event ->
                when (event) {
                    is ChatStreamEvent.Text -> emit(ChatReplyUpdate.TextDelta(event.chunk))
                    is ChatStreamEvent.Error -> {
                        terminal = true
                        emit(ChatReplyUpdate.Failed(event.reason))
                    }
                    ChatStreamEvent.Done -> {
                        terminal = true
                        emit(ChatReplyUpdate.Completed)
                    }
                    is ChatStreamEvent.Intent, is ChatStreamEvent.Cards -> Unit
                }
            }
            .takeWhile { !terminal }
            .collect()
        if (!terminal) emit(ChatReplyUpdate.Failed(ChatFailure.Interrupted))
    }
    companion object { const val MAX_PROMPT_LENGTH = 500 }
}
