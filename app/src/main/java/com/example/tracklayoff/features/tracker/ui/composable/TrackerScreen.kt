package com.example.tracklayoff.features.tracker.ui.composable

import androidx.compose.runtime.Composable
import com.example.tracklayoff.features.auth.ui.state.AuthUiState

@Composable
fun TrackerScreen(
    authUiState: AuthUiState,
    onSignInClick: () -> Unit,
) {
    when(authUiState) {
        is AuthUiState.Authenticated -> {
            AuthenticatedTrackerContent()
        }
        is AuthUiState.Guest -> {
            UnAuthenticatedTrackerContent(
                onSignInClick = onSignInClick
            )
        }
    }
}