package com.example.tracklayoff.features.tracker.ui.composable

import androidx.compose.runtime.Composable
import com.example.tracklayoff.core.common.ui.composables.EmptyScreen


@Composable
fun AuthenticatedTrackerContent() {
    EmptyScreen(
        title = "You are authenticated",
        subtitle = "Something is coming hold tight"
    )
}