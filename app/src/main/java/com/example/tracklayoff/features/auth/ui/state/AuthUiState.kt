package com.example.tracklayoff.features.auth.ui.state

import com.example.tracklayoff.core.common.user.data.AppUser

sealed interface AuthUiState {
    data class Authenticated(val appUser: AppUser): AuthUiState
    object Guest: AuthUiState
}

sealed interface  AuthUiEvent {
    data class ShowAuthenticationEvent(
        val message: String,
        val isError: Boolean = false
    ): AuthUiEvent
}