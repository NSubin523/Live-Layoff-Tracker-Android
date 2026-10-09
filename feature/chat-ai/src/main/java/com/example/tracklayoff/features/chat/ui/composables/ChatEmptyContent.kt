package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.feature.chat.ai.R

@Composable
internal fun ChatEmptyContent(modifier: Modifier = Modifier) {
    Column(modifier.padding(AppDimens.ChatEmptyContentPadding), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.chat_empty_title), style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(AppDimens.SpacingXxl))
        Text(
            stringResource(R.string.chat_empty_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
