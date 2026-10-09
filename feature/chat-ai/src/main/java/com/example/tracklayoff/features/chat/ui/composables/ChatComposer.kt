package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.stringResource
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.feature.chat.ai.R
import com.example.tracklayoff.features.chat.ui.state.ChatComposerUiState

@Composable
internal fun ChatComposer(
    state: ChatComposerUiState,
    onTextFieldBoundsChanged: (Rect) -> Unit,
    onDraftChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = AppDimens.ChatHorizontalPadding, vertical = AppDimens.ChatVerticalPadding)) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(AppDimens.ChatComposerSpacing)) {
            OutlinedTextField(
                value = state.draft,
                onValueChange = onDraftChanged,
                modifier = Modifier.weight(1f).onGloballyPositioned {
                    onTextFieldBoundsChanged(it.boundsInRoot())
                },
                enabled = state.enabled,
                placeholder = { Text(stringResource(R.string.chat_prompt_placeholder)) },
                shape = RoundedCornerShape(AppDimens.ChatComposerCornerRadius),
                maxLines = AppDimens.ChatInputMaxLines,
                isError = state.isError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (state.canSend) onSend() })
            )
            FilledIconButton(onClick = onSend, enabled = state.canSend, modifier = Modifier.size(AppDimens.ChatSendButtonSize)) {
                Icon(painterResource(R.drawable.ic_send), contentDescription = stringResource(R.string.chat_send_description))
            }
        }
        if (state.isError) {
            Text(
                state.validationMessage?.let { stringResource(it.resourceId) }
                    ?: stringResource(R.string.chat_prompt_too_long, state.characterCount, state.maxPromptLength),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = AppDimens.ChatValidationPaddingStart, top = AppDimens.ChatValidationPaddingTop)
            )
        }
    }
}
