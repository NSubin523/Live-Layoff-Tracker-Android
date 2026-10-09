package com.example.tracklayoff.features.chat.ui

import com.example.tracklayoff.features.chat.domain.model.*
import com.example.tracklayoff.features.chat.ui.state.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class ChatStateReducerTest {
    private val reducer = ChatStateReducer()
    private fun row(id: String) = ChatMessage(id, ChatRole.USER, id, Instant.EPOCH)
    private fun reply(state: ChatUiState, update: ChatReplyUpdate) = reducer.reduce(state, ChatStateChange.ReplyUpdated(update, "user", "assistant"))

    @Test fun `empty appears only after successful history`() {
        assertFalse(ChatUiState().isEmpty)
        val failed = reducer.reduce(ChatUiState(), ChatStateChange.HistoryFailed(ChatFailure.Network, false))
        assertFalse(failed.isEmpty)
        val loaded = reducer.reduce(ChatUiState(), ChatStateChange.HistoryLoaded(ChatHistoryPage(emptyList(), false, null), false))
        assertTrue(loaded.isEmpty)
    }

    @Test fun `older page is prepended and deduplicated without losing streaming message`() {
        var state = reducer.reduce(ChatUiState(), ChatStateChange.HistoryLoaded(ChatHistoryPage(listOf(row("2")), true, "cursor"), false))
        state = reply(state, ChatReplyUpdate.Started("Meta?"))
        state = reply(state, ChatReplyUpdate.TextDelta("3,0"))
        state = reducer.reduce(state, ChatStateChange.HistoryLoaded(ChatHistoryPage(listOf(row("1"), row("2")), false, null), true))
        state = reply(state, ChatReplyUpdate.TextDelta("00"))
        assertEquals(listOf("1", "2", "user", "assistant"), state.messages.map { it.id })
        assertEquals("3,000", state.messages.last().text)
        assertEquals(ChatReplyState.Streaming, state.reply)
    }

    @Test fun `failed reply preserves partial text and cannot be completed by done`() {
        var state = reply(ChatUiState(history = ChatLoadState.Ready), ChatReplyUpdate.Started("Meta?"))
        state = reply(state, ChatReplyUpdate.TextDelta("Partial"))
        state = reply(state, ChatReplyUpdate.Failed(ChatFailure.Interrupted))
        state = reply(state, ChatReplyUpdate.Completed)
        assertEquals("Partial", state.messages.last().text)
        assertEquals(ChatMessageStatus.Interrupted, state.messages.last().status)
        assertEquals(ChatReplyState.Failed(ChatFailure.Interrupted), state.reply)
    }

    @Test fun `history and reply loading are independent`() {
        var state = ChatUiState(history = ChatLoadState.Ready, draft = "Meta?")
        assertTrue(state.canSend)
        state = reply(state, ChatReplyUpdate.Started("Meta?"))
        state = reducer.reduce(state, ChatStateChange.HistoryStarted(true))
        assertEquals(ChatLoadState.Ready, state.history)
        assertEquals(ChatLoadState.Loading, state.pagination)
        assertEquals(ChatReplyState.Connecting, state.reply)
        assertFalse(state.canSend)
    }
}
