package com.example.tracklayoff.features.chat.ui.mapper

import com.example.tracklayoff.features.chat.domain.model.ChatFailure
import com.example.tracklayoff.features.chat.domain.model.ChatMessage
import com.example.tracklayoff.features.chat.ui.state.ChatMessageUiModel
import com.example.tracklayoff.features.chat.ui.state.ChatFailureMessage

internal fun ChatMessage.toUiModel() = ChatMessageUiModel(id, role, text)

internal fun ChatFailure.toMessageKey(): ChatFailureMessage = when (this) {
    ChatFailure.AuthenticationRequired -> ChatFailureMessage.AUTHENTICATION_REQUIRED
    ChatFailure.Network -> ChatFailureMessage.NETWORK
    ChatFailure.Service -> ChatFailureMessage.SERVICE
    ChatFailure.Protocol -> ChatFailureMessage.PROTOCOL
    ChatFailure.Interrupted -> ChatFailureMessage.INTERRUPTED
    ChatFailure.InvalidPrompt -> ChatFailureMessage.INVALID_PROMPT
}
