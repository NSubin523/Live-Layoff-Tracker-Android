package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.tracklayoff.designsystems.AppDimens
import androidx.compose.ui.platform.LocalDensity
import com.example.tracklayoff.feature.chat.ai.R
import com.example.tracklayoff.features.chat.ui.state.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
internal fun ChatMessageList(state: ChatMessageListUiState, onLoadOlder: () -> Unit) {
    // Reverse layout anchors the newest row at index 0 and preserves visible rows on older-page inserts.
    val listState = rememberLazyListState()
    val newest = state.newestMessage
    val density = LocalDensity.current
    val bottomThreshold = remember(density) { with(density) { AppDimens.ChatScrollBottomThreshold.roundToPx() } }
    val nearBottom by remember(bottomThreshold) { derivedStateOf {
        listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset <= bottomThreshold
    } }
    val currentLoadOlder by rememberUpdatedState(onLoadOlder)
    val canLoadOlder by rememberUpdatedState(state.canLoadOlder)

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            canLoadOlder && info.totalItemsCount > 0 &&
                (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= info.totalItemsCount - AppDimens.ChatOlderPagePrefetchItemCount
        }.distinctUntilChanged().filter { it }.collect { currentLoadOlder() }
    }

    // A submitted turn adds a new reply row. Always bring that turn into view,
    // even if stable lazy-list keys preserve the user's previous scroll position.
    // Prepending history and streaming deltas leave the newest row ID unchanged.
    LaunchedEffect(newest?.id) {
        if (newest != null) listState.scrollToItem(0)
    }

    LaunchedEffect(newest?.text, newest?.status) {
        if (nearBottom && !listState.isScrollInProgress) listState.scrollToItem(0)
    }

    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = AppDimens.ChatHorizontalPadding, vertical = AppDimens.ChatVerticalPadding)
        ) {
            items(state.newestFirstMessages, key = { it.id }, contentType = { it.role }) { message ->
                ChatMessageItem(message)
            }
            if (state.hasMore) {
                item(key = "older-history") {
                    Column(Modifier.fillMaxWidth().padding(AppDimens.SpacingLarge), horizontalAlignment = Alignment.CenterHorizontally) {
                        when (val pagination = state.pagination) {
                            ChatLoadState.Loading -> CircularProgressIndicator(Modifier.size(AppDimens.ChatPaginationIndicatorSize))
                            is ChatLoadState.Failed -> {
                                Text(stringResource(pagination.message.resourceId), style = MaterialTheme.typography.bodySmall)
                                TextButton(onClick = onLoadOlder) { Text(stringResource(R.string.chat_retry_older)) }
                            }
                            else -> TextButton(onClick = onLoadOlder) { Text(stringResource(R.string.chat_load_older)) }
                        }
                    }
                }
            }
        }
        ChatLatestMessagesButton(
            isVisible = { !nearBottom },
            onClick = { scope.launch { listState.animateScrollToItem(0) } },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/** Read scroll-derived visibility only in the small button's composition scope. */
@Composable
private fun ChatLatestMessagesButton(isVisible: () -> Boolean, onClick: () -> Unit, modifier: Modifier) {
    if (isVisible()) {
        FilledTonalButton(onClick = onClick, modifier = modifier.padding(AppDimens.SpacingLarge)) {
            Text(stringResource(R.string.chat_latest_messages))
        }
    }
}
