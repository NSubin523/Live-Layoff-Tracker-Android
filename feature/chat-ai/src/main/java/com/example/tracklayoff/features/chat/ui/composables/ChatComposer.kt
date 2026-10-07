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
import androidx.compose.ui.unit.dp
import com.example.tracklayoff.feature.chat.ai.R
import com.example.tracklayoff.features.chat.domain.model.ChatFailure
import com.example.tracklayoff.features.chat.domain.usecase.SendChatMessageUseCase
import com.example.tracklayoff.features.chat.ui.mapper.toDisplayMessage

@Composable
internal fun ChatComposer(
    draft: String,
    enabled: Boolean,
    canSend: Boolean,
    validationError: ChatFailure?,
    onTextFieldBoundsChanged: (Rect) -> Unit,
    onDraftChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    val length = draft.trim().codePointCount(0, draft.trim().length)
    val tooLong = length > SendChatMessageUseCase.MAX_PROMPT_LENGTH
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChanged,
                modifier = Modifier.weight(1f).onGloballyPositioned {
                    onTextFieldBoundsChanged(it.boundsInRoot())
                },
                enabled = enabled,
                placeholder = { Text("Ask about layoffs…") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 5,
                isError = tooLong || validationError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() })
            )
            FilledIconButton(onClick = onSend, enabled = canSend, modifier = Modifier.size(48.dp)) {
                Icon(painterResource(R.drawable.ic_send), contentDescription = "Send message")
            }
        }
        if (tooLong || validationError != null) {
            Text(
                validationError?.toDisplayMessage() ?: "Use up to 500 characters ($length/500).",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 6.dp)
            )
        }
    }
}
