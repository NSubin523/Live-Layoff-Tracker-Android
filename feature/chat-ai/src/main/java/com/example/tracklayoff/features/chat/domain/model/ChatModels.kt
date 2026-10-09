package com.example.tracklayoff.features.chat.domain.model

import java.time.Instant

// Backend and UI models are deliberately separate from these domain values.
data class ChatMessage(val id: String, val role: ChatRole, val text: String, val createdAt: Instant)
enum class ChatRole { USER, ASSISTANT }
data class ChatHistoryPage(val messages: List<ChatMessage>, val hasMore: Boolean, val nextBefore: String?)
data class ChatCard(val layoffId: String, val companyName: String)

sealed interface ChatFailure {
    data object AuthenticationRequired : ChatFailure
    data object Network : ChatFailure
    data object Service : ChatFailure
    data object Protocol : ChatFailure
    data object Interrupted : ChatFailure
    data object InvalidPrompt : ChatFailure
}

sealed interface ChatResult<out T> {
    data class Success<T>(val value: T) : ChatResult<T>
    data class Failure(val reason: ChatFailure) : ChatResult<Nothing>
}

sealed interface ChatStreamEvent {
    data class Intent(val value: String) : ChatStreamEvent
    data class Cards(val cards: List<ChatCard>) : ChatStreamEvent
    data class Text(val chunk: String) : ChatStreamEvent
    data class Error(val reason: ChatFailure) : ChatStreamEvent
    data object Done : ChatStreamEvent
}

sealed interface ChatReplyUpdate {
    data class Started(val prompt: String) : ChatReplyUpdate
    data class TextDelta(val text: String) : ChatReplyUpdate
    data object Completed : ChatReplyUpdate
    data class Failed(val reason: ChatFailure) : ChatReplyUpdate
}
