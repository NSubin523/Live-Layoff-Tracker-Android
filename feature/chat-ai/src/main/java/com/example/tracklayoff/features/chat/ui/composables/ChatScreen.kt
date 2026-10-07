package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import com.example.tracklayoff.features.chat.ui.mapper.toDisplayMessage
import com.example.tracklayoff.features.chat.ui.state.*

@Composable
fun ChatScreen(state: ChatUiState, onAction: (ChatAction) -> Unit) {
    var textFieldBounds by remember { mutableStateOf<Rect?>(null) }
    Column(Modifier.fillMaxSize().imePadding().dismissKeyboardOnOutsideTap(textFieldBounds)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (val history = state.history) {
                ChatLoadState.NotLoaded, ChatLoadState.Loading -> {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(Modifier.size(28.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Loading your conversation…", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is ChatLoadState.Failed -> {
                    Column(Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Couldn’t load your conversation", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(history.reason.toDisplayMessage(), style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { onAction(ChatAction.RetryHistory) }) { Text("Try again") }
                    }
                }
                ChatLoadState.Ready -> if (state.isEmpty) {
                    ChatEmptyContent(Modifier.align(Alignment.Center))
                } else {
                    ChatMessageList(state, onLoadOlder = { onAction(ChatAction.LoadOlder) })
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ChatComposer(
            draft = state.draft,
            enabled = state.history == ChatLoadState.Ready,
            canSend = state.canSend,
            validationError = state.validationError,
            onTextFieldBoundsChanged = { textFieldBounds = it },
            onDraftChanged = { onAction(ChatAction.DraftChanged(it)) },
            onSend = { onAction(ChatAction.SendClicked) }
        )
    }
}
