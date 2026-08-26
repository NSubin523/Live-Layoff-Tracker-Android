package com.example.tracklayoff.features.tracker.ui.composable

import androidx.compose.runtime.Composable
import com.example.tracklayoff.core.common.ui.composables.EmptyScreen

@Composable
fun UnAuthenticatedTrackerContent(
    onSignInClick: () -> Unit
) {
    EmptyScreen(
        title = "Login to track companies",
        subtitle = "Login to track companies and get notified about companies",
        hasButton = true,
        onClickAction = onSignInClick
    )
}