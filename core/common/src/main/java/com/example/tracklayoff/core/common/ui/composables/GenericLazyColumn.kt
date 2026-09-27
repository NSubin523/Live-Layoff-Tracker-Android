package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tracklayoff.designsystems.AppDimens
import androidx.compose.foundation.lazy.items

@Composable
fun <T> AppLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    key: ((T) -> Any)? = null,
    emptyScreen: @Composable () -> Unit,
    contentPadding: PaddingValues = PaddingValues(vertical = AppDimens.SpacingLarge),
    itemContent: @Composable (T) -> Unit
) {
    if(items.isEmpty()){
        emptyScreen()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            items(items = items, key = key) { item ->
                itemContent(item)
            }
        }
    }
}