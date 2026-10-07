package com.example.tracklayoff.features.chat.ui.mapper

import com.example.tracklayoff.features.chat.domain.model.ChatFailure
import com.example.tracklayoff.features.chat.domain.model.ChatMessage
import com.example.tracklayoff.features.chat.ui.state.ChatMessageUiModel

internal fun ChatMessage.toUiModel() = ChatMessageUiModel(id, role, text)

internal fun ChatFailure.toDisplayMessage(): String = when (this) {
    ChatFailure.AuthenticationRequired -> "Please sign in again to continue."
    ChatFailure.Network -> "Couldn’t connect. Check your internet connection."
    ChatFailure.Service -> "Couldn’t finish this reply. Please try a new message."
    ChatFailure.Protocol -> "We couldn’t read the server response."
    ChatFailure.Interrupted -> "The connection ended before this reply finished."
    ChatFailure.InvalidPrompt -> "Enter a message of up to 500 characters."
}
