package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import com.example.tracklayoff.features.chat.ui.mapper.toDisplayMessage
import com.example.tracklayoff.features.chat.ui.state.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
internal fun ChatMessageList(state: ChatUiState, onLoadOlder: () -> Unit) {
    // Reverse layout anchors the newest row at index 0 and preserves visible rows on older-page inserts.
    val listState = rememberLazyListState()
    val newest = state.messages.lastOrNull()
    val bottomThreshold = with(LocalDensity.current) { 48.dp.roundToPx() }
    val nearBottom by remember(bottomThreshold) { derivedStateOf {
        listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset <= bottomThreshold
    } }
    val currentLoadOlder by rememberUpdatedState(onLoadOlder)
    val canLoadOlder by rememberUpdatedState(state.hasMore && state.pagination == ChatLoadState.Ready)

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            canLoadOlder && info.totalItemsCount > 0 &&
                (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= info.totalItemsCount - 3
        }.distinctUntilChanged().filter { it }.collect { currentLoadOlder() }
    }

    LaunchedEffect(newest?.id, newest?.text, newest?.status) {
        if (nearBottom && !listState.isScrollInProgress) listState.scrollToItem(0)
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            items(state.messages.asReversed(), key = { it.id }, contentType = { it.role }) { message ->
                ChatMessageItem(message)
            }
            if (state.hasMore) {
                item(key = "older-history") {
                    Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        when (val pagination = state.pagination) {
                            ChatLoadState.Loading -> CircularProgressIndicator(Modifier.size(22.dp))
                            is ChatLoadState.Failed -> {
                                Text(pagination.reason.toDisplayMessage(), style = MaterialTheme.typography.bodySmall)
                                TextButton(onClick = onLoadOlder) { Text("Retry older messages") }
                            }
                            else -> TextButton(onClick = onLoadOlder) { Text("Load older messages") }
                        }
                    }
                }
            }
        }
        if (!nearBottom) {
            val scope = rememberCoroutineScope()
            FilledTonalButton(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
            ) { Text("Latest messages ↓") }
        }
    }
}
