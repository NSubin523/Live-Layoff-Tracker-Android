package com.example.tracklayoff.features.tracker.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tracklayoff.core.common.ui.composables.EmptyScreen


@Composable
fun AuthenticatedTrackerContent(
    modifier: Modifier = Modifier
) {
    EmptyScreen(
        title = "You are authenticated",
        subtitle = "Something is coming hold tight",
        modifier = modifier
    )
}