package com.example.tracklayoff.features.chat.ui.state

import com.example.tracklayoff.features.chat.domain.model.ChatFailure
import com.example.tracklayoff.features.chat.domain.model.ChatRole
import com.example.tracklayoff.features.chat.domain.usecase.SendChatMessageUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ChatUiState(
    val ownerUserId: String? = null,
    val messages: ImmutableList<ChatMessageUiModel> = persistentListOf(),
    val draft: String = "",
    val history: ChatLoadState = ChatLoadState.NotLoaded,
    val pagination: ChatLoadState = ChatLoadState.Ready,
    val hasMore: Boolean = false,
    val nextBefore: String? = null,
    val reply: ChatReplyState = ChatReplyState.Idle,
    val validationError: ChatFailure? = null,
    val authenticationRequired: Boolean = false
) {
    val canSend: Boolean get() = !authenticationRequired && history == ChatLoadState.Ready &&
        !reply.isActive && draft.isNotBlank() &&
        draft.trim().codePointCount(0, draft.trim().length) <= SendChatMessageUseCase.MAX_PROMPT_LENGTH
    val isEmpty: Boolean get() = history == ChatLoadState.Ready && messages.isEmpty()
}

data class ChatMessageUiModel(
    val id: String,
    val role: ChatRole,
    val text: String,
    val status: ChatMessageStatus = ChatMessageStatus.Complete,
    val failure: ChatFailure? = null
)
enum class ChatMessageStatus { Complete, Waiting, Streaming, Failed, Interrupted }

sealed interface ChatLoadState {
    data object NotLoaded : ChatLoadState
    data object Loading : ChatLoadState
    data object Ready : ChatLoadState
    data class Failed(val reason: ChatFailure) : ChatLoadState
}
sealed interface ChatReplyState {
    data object Idle : ChatReplyState
    data object Connecting : ChatReplyState
    data object Streaming : ChatReplyState
    data class Failed(val reason: ChatFailure) : ChatReplyState
    val isActive: Boolean get() = this == Connecting || this == Streaming
}

sealed interface ChatAction {
    data object Opened : ChatAction
    data class DraftChanged(val text: String) : ChatAction
    data object SendClicked : ChatAction
    data object LoadOlder : ChatAction
    data object RetryHistory : ChatAction
}
