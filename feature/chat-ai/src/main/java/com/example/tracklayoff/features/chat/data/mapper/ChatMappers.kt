package com.example.tracklayoff.features.chat.data.mapper

import com.example.tracklayoff.features.chat.data.dto.*
import com.example.tracklayoff.features.chat.domain.model.*
import java.time.Instant

internal fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = requireNotNull(messageId).also { require(it.isNotBlank()) },
    role = when (role) {
        "user" -> ChatRole.USER
        "agent" -> ChatRole.ASSISTANT
        else -> throw IllegalArgumentException("Unknown chat role")
    },
    text = requireNotNull(text),
    createdAt = Instant.parse(requireNotNull(createdAt))
)

internal fun ChatHistoryResponseDto.toDomain(): ChatHistoryPage {
    val more = requireNotNull(hasMore)
    require(!more || !nextBefore.isNullOrBlank()) { "Missing history cursor" }
    return ChatHistoryPage(requireNotNull(messages).map { it.toDomain() }, more, nextBefore)
}

internal fun ChatStreamEventDto.toDomain(): ChatStreamEvent = when (this) {
    is ChatStreamEventDto.Intent -> ChatStreamEvent.Intent(requireNotNull(payload.intent))
    is ChatStreamEventDto.Cards -> ChatStreamEvent.Cards(payload.map {
        ChatCard(requireNotNull(it.layoffId), requireNotNull(it.companyName))
    })
    is ChatStreamEventDto.Text -> ChatStreamEvent.Text(requireNotNull(payload.chunk))
    is ChatStreamEventDto.Error -> ChatStreamEvent.Error(ChatFailure.Service)
    ChatStreamEventDto.Done -> ChatStreamEvent.Done
}
