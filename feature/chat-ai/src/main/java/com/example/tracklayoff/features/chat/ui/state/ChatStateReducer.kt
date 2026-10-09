package com.example.tracklayoff.features.chat.ui.state

import com.example.tracklayoff.features.chat.domain.model.*
import com.example.tracklayoff.features.chat.ui.mapper.toUiModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

sealed interface ChatStateChange {
    data class DraftChanged(val text: String) : ChatStateChange
    data class HistoryStarted(val older: Boolean) : ChatStateChange
    data class HistoryLoaded(val page: ChatHistoryPage, val older: Boolean) : ChatStateChange
    data class HistoryFailed(val reason: ChatFailure, val older: Boolean) : ChatStateChange
    data class ReplyUpdated(
        val update: ChatReplyUpdate,
        val userMessageId: String,
        val assistantMessageId: String
    ) : ChatStateChange
}

/** Pure presentation logic: no coroutines, HTTP, mutable store, or lifecycle dependencies. */
class ChatStateReducer @Inject constructor() {
    fun reduce(state: ChatUiState, change: ChatStateChange): ChatUiState = when (change) {
        is ChatStateChange.DraftChanged -> state.copy(draft = change.text, validationError = null)
        is ChatStateChange.HistoryStarted -> if (change.older) {
            state.copy(pagination = ChatLoadState.Loading)
        } else state.copy(history = ChatLoadState.Loading)
        is ChatStateChange.HistoryLoaded -> {
            val page = change.page
            // Existing rows win on overlapping pages; streaming rows are never replaced.
            val existingIds = state.messages.mapTo(hashSetOf()) { it.id }
            val rows = page.messages.map { it.toUiModel() }.filter { it.id !in existingIds }
            state.copy(
                messages = (rows + state.messages).distinctBy { it.id }.toImmutableList(),
                history = ChatLoadState.Ready,
                pagination = ChatLoadState.Ready,
                hasMore = page.hasMore,
                nextBefore = page.nextBefore
            )
        }
        is ChatStateChange.HistoryFailed -> state.copy(
            history = if (change.older) state.history else ChatLoadState.Failed(change.reason),
            pagination = if (change.older) ChatLoadState.Failed(change.reason) else state.pagination,
            authenticationRequired = state.authenticationRequired || change.reason == ChatFailure.AuthenticationRequired
        )
        is ChatStateChange.ReplyUpdated -> reduceReply(state, change)
    }

    private fun reduceReply(state: ChatUiState, change: ChatStateChange.ReplyUpdated): ChatUiState =
        when (val update = change.update) {
            is ChatReplyUpdate.Started -> state.copy(
                messages = (state.messages + listOf(
                    ChatMessageUiModel(change.userMessageId, ChatRole.USER, update.prompt),
                    ChatMessageUiModel(change.assistantMessageId, ChatRole.ASSISTANT, "", ChatMessageStatus.Waiting)
                )).toImmutableList(),
                draft = "",
                validationError = null,
                reply = ChatReplyState.Connecting
            )
            is ChatReplyUpdate.TextDelta -> if (!state.reply.isActive) state else state.copy(
                messages = state.messages.map { row ->
                    if (row.id == change.assistantMessageId) row.copy(
                        text = row.text + update.text, status = ChatMessageStatus.Streaming
                    ) else row
                }.toImmutableList(),
                reply = ChatReplyState.Streaming
            )
            ChatReplyUpdate.Completed -> if (!state.reply.isActive) state else state.copy(
                messages = state.messages.map { row ->
                    if (row.id == change.assistantMessageId) row.copy(status = ChatMessageStatus.Complete) else row
                }.toImmutableList(),
                reply = ChatReplyState.Idle
            )
            is ChatReplyUpdate.Failed -> if (update.reason == ChatFailure.InvalidPrompt) {
                state.copy(validationError = update.reason)
            } else state.copy(
                messages = state.messages.map { row ->
                    if (row.id == change.assistantMessageId) row.copy(
                        status = if (update.reason == ChatFailure.Interrupted) ChatMessageStatus.Interrupted else ChatMessageStatus.Failed,
                        failure = update.reason
                    ) else row
                }.toImmutableList(),
                reply = ChatReplyState.Failed(update.reason),
                authenticationRequired = state.authenticationRequired || update.reason == ChatFailure.AuthenticationRequired
            )
        }
}
