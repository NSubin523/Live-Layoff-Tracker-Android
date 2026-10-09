package com.example.tracklayoff.features.chat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracklayoff.core.common.user.UserSession
import com.example.tracklayoff.features.chat.domain.model.ChatResult
import com.example.tracklayoff.features.chat.domain.usecase.LoadChatHistoryUseCase
import com.example.tracklayoff.features.chat.domain.usecase.SendChatMessageUseCase
import com.example.tracklayoff.features.chat.ui.state.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val loadHistory: LoadChatHistoryUseCase,
    private val sendMessage: SendChatMessageUseCase,
    private val reducer: ChatStateReducer,
    private val session: UserSession
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()
    private var historyJob: Job? = null
    private var replyJob: Job? = null
    private var userId: String? = null
    private var sessionVersion = 0L

    init {
        viewModelScope.launch {
            session.userIds.collect { newUserId ->
                if (newUserId != userId || _uiState.value.authenticationRequired && newUserId != null) {
                    resetSession(newUserId)
                }
            }
        }
    }

    fun onAction(action: ChatAction) {
        if (action == ChatAction.Opened) {
            val currentUser = session.currentUserId()
            if (currentUser != userId) resetSession(currentUser)
            if (currentUser != null && _uiState.value.history == ChatLoadState.NotLoaded) fetchHistory(older = false)
            return
        }
        // Reject actions during the gap between Firebase changing identity and its observer running.
        if (userId == null || userId != session.currentUserId()) return
        when (action) {
            is ChatAction.DraftChanged -> apply(ChatStateChange.DraftChanged(action.text))
            ChatAction.SendClicked -> send()
            ChatAction.LoadOlder -> fetchHistory(older = true)
            ChatAction.RetryHistory -> if (_uiState.value.history is ChatLoadState.Failed) fetchHistory(older = false)
            ChatAction.Opened -> Unit
        }
    }

    private fun resetSession(newUserId: String?) {
        sessionVersion++
        historyJob?.cancel()
        replyJob?.cancel()
        historyJob = null
        replyJob = null
        userId = newUserId
        _uiState.value = ChatUiState(ownerUserId = newUserId, authenticationRequired = newUserId == null)
        // History starts only when this account opens the tab, not on sign-in elsewhere.
    }

    private fun fetchHistory(older: Boolean) {
        val state = _uiState.value
        if (historyJob?.isActive == true || state.authenticationRequired) return
        if (older && (state.history != ChatLoadState.Ready || !state.hasMore || state.nextBefore == null)) return
        val version = sessionVersion
        val cursor = if (older) state.nextBefore else null
        apply(ChatStateChange.HistoryStarted(older))
        historyJob = viewModelScope.launch {
            val result = loadHistory(cursor)
            if (!isCurrentSession(version)) return@launch
            apply(when (result) {
                is ChatResult.Success -> ChatStateChange.HistoryLoaded(result.value, older)
                is ChatResult.Failure -> ChatStateChange.HistoryFailed(result.reason, older)
            })
        }
    }

    private fun send() {
        val state = _uiState.value
        if (!state.canSend || replyJob?.isActive == true) return
        val version = sessionVersion
        val userMessageId = "local-user-${UUID.randomUUID()}"
        val assistantMessageId = "local-assistant-${UUID.randomUUID()}"
        replyJob = viewModelScope.launch {
            sendMessage(state.draft).collect { update ->
                if (isCurrentSession(version)) {
                    apply(ChatStateChange.ReplyUpdated(update, userMessageId, assistantMessageId))
                }
            }
        }
    }

    private fun isCurrentSession(version: Long) = version == sessionVersion &&
        userId != null && userId == session.currentUserId()

    private fun apply(change: ChatStateChange) {
        _uiState.update { reducer.reduce(it, change) }
    }
}
