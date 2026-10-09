package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.stringResource
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.feature.chat.ai.R
import com.example.tracklayoff.features.chat.ui.state.*

@Composable
fun ChatScreen(state: ChatUiState, onAction: (ChatAction) -> Unit) {
    val textFieldBounds = remember { ChatTextFieldBounds() }
    Column(Modifier.fillMaxSize().imePadding().dismissKeyboardOnOutsideTap { textFieldBounds.value }) {
        ChatConversationContent(
            history = state.history,
            isEmpty = state.isEmpty,
            messageList = state.messageList,
            onRetry = { onAction(ChatAction.RetryHistory) },
            onLoadOlder = { onAction(ChatAction.LoadOlder) },
            modifier = Modifier.weight(1f).fillMaxWidth()
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ChatComposer(
            state = state.composer,
            onTextFieldBoundsChanged = { textFieldBounds.value = it },
            onDraftChanged = { onAction(ChatAction.DraftChanged(it)) },
            onSend = { onAction(ChatAction.SendClicked) }
        )
    }
}

@Composable
private fun ChatConversationContent(
    history: ChatLoadState,
    isEmpty: Boolean,
    messageList: ChatMessageListUiState,
    onRetry: () -> Unit,
    onLoadOlder: () -> Unit,
    modifier: Modifier
) {
    Box(modifier) {
        when (history) {
            ChatLoadState.NotLoaded, ChatLoadState.Loading -> {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(Modifier.size(AppDimens.ChatHistoryIndicatorSize))
                    Spacer(Modifier.height(AppDimens.ChatHorizontalPadding))
                    Text(stringResource(R.string.chat_history_loading), style = MaterialTheme.typography.bodyMedium)
                }
            }
            is ChatLoadState.Failed -> {
                Column(Modifier.align(Alignment.Center).padding(AppDimens.ChatErrorContentPadding), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.chat_history_failed), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(AppDimens.SpacingLarge))
                    Text(stringResource(history.message.resourceId), style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onRetry) { Text(stringResource(R.string.chat_retry)) }
                }
            }
            ChatLoadState.Ready -> if (isEmpty) {
                ChatEmptyContent(Modifier.align(Alignment.Center))
            } else {
                ChatMessageList(messageList, onLoadOlder)
            }
        }
    }
}

// Layout geometry is local UI state; read it only when a pointer event needs it.
private class ChatTextFieldBounds {
    var value: Rect? = null
}
