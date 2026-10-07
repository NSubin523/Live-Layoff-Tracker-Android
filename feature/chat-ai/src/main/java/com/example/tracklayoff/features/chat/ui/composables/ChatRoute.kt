package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tracklayoff.core.common.ui.composables.EmptyScreen
import com.example.tracklayoff.features.chat.ui.viewmodel.ChatViewModel
import com.example.tracklayoff.features.chat.ui.state.ChatAction

/** The app calls this only after its authentication gate, so guests never create the VM. */
@Composable
fun ChatRoute(userId: String, onSignInClick: () -> Unit, viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, userId, state.authenticationRequired) {
        if (!state.authenticationRequired) viewModel.onAction(ChatAction.Opened)
    }
    if (state.ownerUserId != userId) {
        // Never render the previous account’s rows while the session observer catches up.
        ChatScreen(state = com.example.tracklayoff.features.chat.ui.state.ChatUiState(), onAction = {})
    } else if (state.authenticationRequired) {
        ChatGuestContent(onSignInClick)
    } else {
        ChatScreen(state = state, onAction = viewModel::onAction)
    }
}

@Composable
fun ChatGuestContent(onSignInClick: () -> Unit) {
    EmptyScreen(
        title = "Sign in to ask about layoffs",
        subtitle = "Explore company layoffs and market trends.",
        hasButton = true,
        onClickAction = onSignInClick
    )
}
