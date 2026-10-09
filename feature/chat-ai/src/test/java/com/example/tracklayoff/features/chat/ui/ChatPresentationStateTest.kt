package com.example.tracklayoff.features.chat.ui

import com.example.tracklayoff.features.chat.domain.model.ChatFailure
import com.example.tracklayoff.features.chat.domain.model.ChatRole
import com.example.tracklayoff.features.chat.ui.mapper.toMessageKey
import com.example.tracklayoff.features.chat.ui.state.*
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.*
import org.junit.Test

class ChatPresentationStateTest {
    @Test fun `composer counts Unicode code points and refreshes on state copy`() {
        val ready = ChatUiState(history = ChatLoadState.Ready, draft = "  " + "😀".repeat(500) + "  ")
        assertEquals(500, ready.composer.characterCount)
        assertTrue(ready.composer.canSend)
        val overLimit = ready.copy(draft = ready.draft.trim() + "a")
        assertEquals(501, overLimit.composer.characterCount)
        assertTrue(overLimit.composer.isError)
        assertFalse(overLimit.composer.canSend)
        assertFalse(ready.copy(draft = " \n ").composer.canSend)
        assertFalse(ready.copy(reply = ChatReplyState.Streaming).composer.canSend)
        assertFalse(ready.copy(authenticationRequired = true).composer.enabled)
    }

    @Test fun `each failure maps to a distinct resource backed enum`() {
        val failures = listOf(ChatFailure.AuthenticationRequired, ChatFailure.Network, ChatFailure.Service,
            ChatFailure.Protocol, ChatFailure.Interrupted, ChatFailure.InvalidPrompt)
        assertEquals(ChatFailureMessage.entries.toSet(), failures.map { it.toMessageKey() }.toSet())
        assertEquals(failures.size, failures.map { it.toMessageKey().resourceId }.toSet().size)
        assertEquals(ChatFailureMessage.NETWORK, ChatLoadState.Failed(ChatFailure.Network).message)
        assertEquals(ChatFailureMessage.INVALID_PROMPT,
            ChatUiState(validationError = ChatFailure.InvalidPrompt).composer.validationMessage)
    }

    @Test fun `message projections update without mutating the previous snapshot`() {
        val user = ChatMessageUiModel("1", ChatRole.USER, "question")
        val reply = ChatMessageUiModel("2", ChatRole.ASSISTANT, "")
        val state = ChatUiState(messages = persistentListOf(user, reply), history = ChatLoadState.Ready, hasMore = true)
        val updated = state.copy(messages = persistentListOf(user, reply.copy(text = "partial", failure = ChatFailure.Interrupted)), pagination = ChatLoadState.Loading)
        assertEquals(listOf("2", "1"), updated.messageList.newestFirstMessages.map { it.id })
        assertTrue(updated.messageList.newestMessage!!.hasText)
        assertEquals(ChatFailureMessage.INTERRUPTED, updated.messageList.newestMessage!!.failureMessage)
        assertFalse(state.messageList.newestMessage!!.hasText)
        assertTrue(state.messageList.canLoadOlder)
        assertFalse(updated.messageList.canLoadOlder)
        assertTrue(user.isUser)
    }
}
