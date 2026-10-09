package com.example.tracklayoff.features.chat.ui.state

import androidx.compose.runtime.Immutable
import com.example.tracklayoff.features.chat.ui.mapper.toMessageKey
import com.example.tracklayoff.features.chat.domain.model.ChatFailure
import com.example.tracklayoff.features.chat.domain.model.ChatRole
import com.example.tracklayoff.features.chat.domain.usecase.SendChatMessageUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
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
    // These immutable projections are calculated on state creation/copy, before composition.
    private val normalizedDraft = draft.trim()
    private val characterCount = normalizedDraft.codePointCount(0, normalizedDraft.length)
    private val tooLong = characterCount > SendChatMessageUseCase.MAX_PROMPT_LENGTH
    val canSend = !authenticationRequired && history == ChatLoadState.Ready &&
        !reply.isActive && normalizedDraft.isNotEmpty() && !tooLong
    val isEmpty = history == ChatLoadState.Ready && messages.isEmpty()
    val composer = ChatComposerUiState(
        draft = draft,
        enabled = !authenticationRequired && history == ChatLoadState.Ready,
        canSend = canSend,
        characterCount = characterCount,
        maxPromptLength = SendChatMessageUseCase.MAX_PROMPT_LENGTH,
        tooLong = tooLong,
        validationMessage = validationError?.toMessageKey()
    )
    val messageList = ChatMessageListUiState(messages, pagination, hasMore)

}

@Immutable
data class ChatMessageUiModel(
    val id: String,
    val role: ChatRole,
    val text: String,
    val status: ChatMessageStatus = ChatMessageStatus.Complete,
    val failure: ChatFailure? = null
) {
    val isUser = role == ChatRole.USER
    val hasText = text.isNotEmpty()
    val failureMessage = failure?.toMessageKey()
}
@Immutable
data class ChatComposerUiState(
    val draft: String,
    val enabled: Boolean,
    val canSend: Boolean,
    val characterCount: Int,
    val maxPromptLength: Int,
    val tooLong: Boolean,
    val validationMessage: ChatFailureMessage?
) {
    val isError = tooLong || validationMessage != null
}

@Immutable
data class ChatMessageListUiState(
    val messages: ImmutableList<ChatMessageUiModel>,
    val pagination: ChatLoadState,
    val hasMore: Boolean
) {
    // This read-only reverse view wraps an immutable list; it never copies history in composition.
    val newestFirstMessages: List<ChatMessageUiModel> = messages.asReversed()
    val newestMessage = messages.lastOrNull()
    val canLoadOlder = hasMore && pagination == ChatLoadState.Ready
}

enum class ChatMessageStatus { Complete, Waiting, Streaming, Failed, Interrupted }

sealed interface ChatLoadState {
    data object NotLoaded : ChatLoadState
    data object Loading : ChatLoadState
    data object Ready : ChatLoadState
    data class Failed(val reason: ChatFailure) : ChatLoadState {
        val message = reason.toMessageKey()
    }
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
