package com.example.tracklayoff.core.common.ui.state

data class AuthenticationState(
    val isAuthenticated: Boolean = false,
    val photoUrl: String? = null
)