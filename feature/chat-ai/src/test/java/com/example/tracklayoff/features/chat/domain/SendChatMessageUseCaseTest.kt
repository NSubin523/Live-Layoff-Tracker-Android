package com.example.tracklayoff.features.chat.domain

import com.example.tracklayoff.features.chat.domain.model.*
import com.example.tracklayoff.features.chat.domain.repository.ChatRepository
import com.example.tracklayoff.features.chat.domain.usecase.SendChatMessageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class SendChatMessageUseCaseTest {
    private class Repository(private val events: Flow<ChatStreamEvent>) : ChatRepository {
        var sent: String? = null
        override suspend fun getHistory(before: String?, limit: Int) = ChatResult.Success(ChatHistoryPage(emptyList(), false, null))
        override fun sendMessage(prompt: String): Flow<ChatStreamEvent> {
            sent = prompt
            return events
        }
    }

    @Test fun `chunks remain exact and cards do not become messages`() = runTest {
        val repo = Repository(flowOf(
            ChatStreamEvent.Intent("layoff_query"),
            ChatStreamEvent.Cards(listOf(ChatCard("meta", "Meta"))),
            ChatStreamEvent.Text("3,0"), ChatStreamEvent.Text("00 affected"), ChatStreamEvent.Done
        ))
        val updates = SendChatMessageUseCase(repo)("  Meta?  ").toList()
        assertEquals("Meta?", repo.sent)
        assertEquals(listOf(
            ChatReplyUpdate.Started("Meta?"), ChatReplyUpdate.TextDelta("3,0"),
            ChatReplyUpdate.TextDelta("00 affected"), ChatReplyUpdate.Completed
        ), updates)
    }

    @Test fun `error followed by done stays failed`() = runTest {
        val repo = Repository(flowOf(ChatStreamEvent.Text("Partial"), ChatStreamEvent.Error(ChatFailure.Service), ChatStreamEvent.Done))
        val updates = SendChatMessageUseCase(repo)("Meta?").toList()
        assertEquals(ChatReplyUpdate.Failed(ChatFailure.Service), updates.last())
        assertFalse(updates.contains(ChatReplyUpdate.Completed))
    }

    @Test fun `eof without done marks interruption`() = runTest {
        val updates = SendChatMessageUseCase(Repository(flowOf(ChatStreamEvent.Text("Partial"))))("Meta?").toList()
        assertEquals(ChatReplyUpdate.Failed(ChatFailure.Interrupted), updates.last())
    }

    @Test fun `invalid prompts never call repository`() = runTest {
        for (prompt in listOf("   ", "x".repeat(501))) {
            val repo = Repository(emptyFlow())
            assertEquals(listOf(ChatReplyUpdate.Failed(ChatFailure.InvalidPrompt)), SendChatMessageUseCase(repo)(prompt).toList())
            assertNull(repo.sent)
        }
    }

    @Test fun `unicode length matches server code points`() = runTest {
        val repo = Repository(flowOf(ChatStreamEvent.Done))
        SendChatMessageUseCase(repo)("😀".repeat(500)).toList()
        assertNotNull(repo.sent)
    }

    @Test fun `cancellation is not converted to a reply failure`() = runTest {
        val repo = Repository(flow { throw CancellationException("cancelled") })
        try {
            SendChatMessageUseCase(repo)("Meta?").toList()
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { }
    }
    @Test fun `repository exception becomes failure but consumer exception propagates`() = runTest {
        val failingRepo = Repository(flow { throw java.io.IOException("connection lost") })
        val updates = SendChatMessageUseCase(failingRepo)("Meta?").toList()
        assertEquals(ChatReplyUpdate.Failed(ChatFailure.Service), updates.last())
        val normalRepo = Repository(flowOf(ChatStreamEvent.Text("chunk")))
        try {
            SendChatMessageUseCase(normalRepo)("Meta?").collect {
                if (it is ChatReplyUpdate.TextDelta) throw IllegalStateException("consumer error")
            }
            fail("Consumer failure must propagate")
        } catch (error: IllegalStateException) {
            assertEquals("consumer error", error.message)
        }
    }

}
