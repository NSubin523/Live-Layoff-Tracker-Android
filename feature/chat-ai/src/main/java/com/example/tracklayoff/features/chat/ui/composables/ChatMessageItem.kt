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
import androidx.compose.ui.unit.dp
import com.example.tracklayoff.features.chat.domain.model.ChatRole
import com.example.tracklayoff.features.chat.ui.mapper.toDisplayMessage
import com.example.tracklayoff.features.chat.ui.state.ChatMessageStatus
import com.example.tracklayoff.features.chat.ui.state.ChatMessageUiModel

@Composable
internal fun ChatMessageItem(message: ChatMessageUiModel) {
    val user = message.role == ChatRole.USER
    Column(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalAlignment = if (user) Alignment.End else Alignment.Start
    ) {
        Text(
            if (user) "You" else "Chat AI",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (user) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 560.dp)
        ) {
            Column(Modifier.padding(14.dp)) {
                if (message.text.isNotEmpty()) {
                    SelectionContainer {
                        Text(message.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                if (message.status == ChatMessageStatus.Waiting) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text("Thinking…", style = MaterialTheme.typography.bodyMedium)
                    }
                } else if (message.status == ChatMessageStatus.Streaming) {
                    LinearProgressIndicator(Modifier.padding(top = 10.dp).width(32.dp))
                }
                message.failure?.let { failure ->
                    if (message.text.isNotEmpty()) Spacer(Modifier.height(10.dp))
                    Text(
                        failure.toDisplayMessage(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
            }
        }
    }
}
