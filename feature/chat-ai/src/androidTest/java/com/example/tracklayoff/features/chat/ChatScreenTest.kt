package com.example.tracklayoff.features.chat

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tracklayoff.features.chat.domain.model.ChatFailure
import com.example.tracklayoff.features.chat.domain.model.ChatRole
import com.example.tracklayoff.features.chat.ui.composables.ChatGuestContent
import com.example.tracklayoff.features.chat.ui.composables.ChatScreen
import com.example.tracklayoff.features.chat.ui.state.*
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class ChatScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun guestSignInUsesCallback() {
        var clicked = false
        compose.setContent { MaterialTheme { ChatGuestContent { clicked = true } } }
        compose.onNodeWithText("Sign in to ask about layoffs").assertIsDisplayed()
        compose.onNodeWithText("Sign in").performClick()
        assertTrue(clicked)
    }

    @Test fun emptyConversationAcceptsTextAndSends() {
        val state = mutableStateOf(ChatUiState(history = ChatLoadState.Ready))
        var sent = false
        compose.setContent { MaterialTheme {
            ChatScreen(state.value) { action ->
                when (action) {
                    is ChatAction.DraftChanged -> state.value = state.value.copy(draft = action.text)
                    ChatAction.SendClicked -> sent = true
                    else -> Unit
                }
            }
        } }
        compose.onNodeWithText("Ask me about layoff trends").assertIsDisplayed()
        compose.onNodeWithContentDescription("Send message").assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).performTextInput("Is Meta laying off?")
        compose.onNodeWithContentDescription("Send message").assertIsEnabled().performClick()
        assertTrue(sent)
        capture("chat-empty")
    }

    @Test fun loadingAndFailureDoNotShowEmptyConversation() {
        val state = mutableStateOf(ChatUiState(history = ChatLoadState.Loading))
        compose.setContent { MaterialTheme { ChatScreen(state.value) {} } }
        compose.onNodeWithText("Loading your conversation…").assertIsDisplayed()
        compose.onNodeWithText("Ask me about layoff trends").assertDoesNotExist()
        compose.onNodeWithContentDescription("Send message").assertIsNotEnabled()
        compose.runOnIdle { state.value = state.value.copy(history = ChatLoadState.Failed(ChatFailure.Network)) }
        compose.onNodeWithText("Couldn’t load your conversation").assertIsDisplayed()
        compose.onNodeWithText("Try again").assertIsDisplayed()
    }

    @Test fun streamedReplyKeepsPartialTextOnFailure() {
        val state = mutableStateOf(ChatUiState(
            history = ChatLoadState.Ready,
            reply = ChatReplyState.Streaming,
            messages = persistentListOf(
                ChatMessageUiModel("u", ChatRole.USER, "Is Meta laying off?"),
                ChatMessageUiModel("a", ChatRole.ASSISTANT, "Meta has reported layoffs affecting 3,000 workers.", ChatMessageStatus.Streaming)
            )
        ))
        compose.setContent { MaterialTheme { ChatScreen(state.value) {} } }
        compose.onNodeWithText("Meta has reported layoffs affecting 3,000 workers.").assertIsDisplayed()
        compose.onNodeWithContentDescription("Send message").assertIsNotEnabled()
        compose.runOnIdle {
            state.value = state.value.copy(
                reply = ChatReplyState.Failed(ChatFailure.Interrupted),
                messages = persistentListOf(state.value.messages[0], state.value.messages[1].copy(
                    status = ChatMessageStatus.Interrupted, failure = ChatFailure.Interrupted
                ))
            )
        }
        compose.onNodeWithText("Meta has reported layoffs affecting 3,000 workers.").assertIsDisplayed()
        compose.onNodeWithText("The connection ended before this reply finished.").assertIsDisplayed()
        capture("chat-reply")
    }

    private fun capture(name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val image = compose.onRoot().captureToImage().asAndroidBitmap()
        File(context.getExternalFilesDir(null), "$name.png").outputStream().use {
            image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
