package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.feature.chat.ai.R
import com.example.tracklayoff.features.chat.ui.state.ChatMessageStatus
import com.example.tracklayoff.features.chat.ui.state.ChatMessageUiModel

@Composable
internal fun ChatMessageItem(message: ChatMessageUiModel) {
    val user = message.isUser
    Column(
        Modifier.fillMaxWidth().padding(vertical = AppDimens.ChatMessageVerticalPadding),
        horizontalAlignment = if (user) Alignment.End else Alignment.Start
    ) {
        Text(
            stringResource(if (user) R.string.chat_user_label else R.string.chat_title),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = AppDimens.ChatMessageLabelHorizontalPadding, vertical = AppDimens.ChatMessageLabelVerticalPadding)
        )
        Surface(
            shape = RoundedCornerShape(AppDimens.ChatMessageCornerRadius),
            color = if (user) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = AppDimens.ChatMessageMaxWidth)
        ) {
            Column(Modifier.padding(AppDimens.ChatMessageContentPadding)) {
                if (message.hasText) {
                    SelectionContainer {
                        Text(message.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                if (message.status == ChatMessageStatus.Waiting) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingXl)) {
                        CircularProgressIndicator(Modifier.size(AppDimens.ChatReplyIndicatorSize), strokeWidth = AppDimens.ChatReplyIndicatorStrokeWidth)
                        Text(stringResource(R.string.chat_thinking), style = MaterialTheme.typography.bodyMedium)
                    }
                } else if (message.status == ChatMessageStatus.Streaming) {
                    LinearProgressIndicator(Modifier.padding(top = AppDimens.SpacingXl).width(AppDimens.ChatStreamingIndicatorWidth))
                }
                message.failureMessage?.let { failure ->
                    if (message.hasText) Spacer(Modifier.height(AppDimens.SpacingXl))
                    Text(
                        stringResource(failure.resourceId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
            }
        }
    }
}
