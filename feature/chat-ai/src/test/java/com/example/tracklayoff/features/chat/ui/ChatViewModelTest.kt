package com.example.tracklayoff.features.chat.ui

import com.example.tracklayoff.core.common.user.UserSession
import com.example.tracklayoff.features.chat.domain.model.*
import com.example.tracklayoff.features.chat.domain.repository.ChatRepository
import com.example.tracklayoff.features.chat.domain.usecase.*
import com.example.tracklayoff.features.chat.ui.state.*
import com.example.tracklayoff.features.chat.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private class Session : UserSession {
        val identity = MutableStateFlow<String?>("user-a")
        override val userIds: Flow<String?> = identity
        override fun currentUserId() = identity.value
    }
    private class Repository : ChatRepository {
        var historyCalls = 0
        var sendCalls = 0
        var streamCancelled = false
        var holdHistory = false
        val historyGate = CompletableDeferred<Unit>()
        override suspend fun getHistory(before: String?, limit: Int): ChatResult<ChatHistoryPage> {
            historyCalls++
            assertEquals(20, limit)
            if (holdHistory) {
                // Simulate an uncooperative result arriving after cancellation.
                withContext(NonCancellable) { historyGate.await() }
            }
            return ChatResult.Success(ChatHistoryPage(emptyList(), false, null))
        }
        override fun sendMessage(prompt: String) = flow<ChatStreamEvent> {
            sendCalls++
            try {
                emit(ChatStreamEvent.Text("Partial"))
                awaitCancellation()
            } finally { streamCancelled = true }
        }
    }
    private val session = Session()
    private val repo = Repository()
    private fun vm() = ChatViewModel(LoadChatHistoryUseCase(repo), SendChatMessageUseCase(repo), ChatStateReducer(), session)

    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun cleanup() { Dispatchers.resetMain() }

    @Test fun `rapid send taps create one stream`() = runTest(dispatcher) {
        val vm = vm()
        runCurrent()
        vm.onAction(ChatAction.Opened)
        runCurrent()
        vm.onAction(ChatAction.DraftChanged("Meta?"))
        vm.onAction(ChatAction.SendClicked)
        vm.onAction(ChatAction.SendClicked)
        runCurrent()
        assertEquals(1, repo.sendCalls)
        assertEquals(2, vm.uiState.value.messages.size)
        assertEquals("Partial", vm.uiState.value.messages.last().text)
        session.identity.value = null
        runCurrent()
    }

    @Test fun `sign out cancels stream and clears all state`() = runTest(dispatcher) {
        val vm = vm()
        runCurrent()
        vm.onAction(ChatAction.Opened)
        runCurrent()
        vm.onAction(ChatAction.DraftChanged("Meta?"))
        vm.onAction(ChatAction.SendClicked)
        runCurrent()
        vm.onAction(ChatAction.DraftChanged("unsent draft"))
        session.identity.value = null
        runCurrent()
        assertTrue(repo.streamCancelled)
        assertTrue(vm.uiState.value.messages.isEmpty())
        assertEquals("", vm.uiState.value.draft)
        assertTrue(vm.uiState.value.authenticationRequired)
    }

    @Test fun `old history result is ignored after account changes`() = runTest(dispatcher) {
        repo.holdHistory = true
        val vm = vm()
        runCurrent()
        vm.onAction(ChatAction.Opened)
        runCurrent()
        session.identity.value = null
        runCurrent()
        repo.historyGate.complete(Unit)
        runCurrent()
        assertEquals(ChatLoadState.NotLoaded, vm.uiState.value.history)
        assertTrue(vm.uiState.value.authenticationRequired)
    }

    @Test fun `guest never loads history and new account starts fresh`() = runTest(dispatcher) {
        session.identity.value = null
        val vm = vm()
        runCurrent()
        vm.onAction(ChatAction.Opened)
        runCurrent()
        assertEquals(0, repo.historyCalls)
        session.identity.value = "user-b"
        runCurrent()
        assertEquals(0, repo.historyCalls)
        vm.onAction(ChatAction.Opened)
        runCurrent()
        assertEquals(1, repo.historyCalls)
        vm.onAction(ChatAction.Opened)
        runCurrent()
        assertEquals(1, repo.historyCalls)
        assertEquals(ChatLoadState.Ready, vm.uiState.value.history)
        session.identity.value = null
        runCurrent()
    }
}
