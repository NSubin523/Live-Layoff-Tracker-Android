package com.example.tracklayoff.features.auth.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracklayoff.core.common.domain.AuthProviderClientType
import com.example.tracklayoff.core.common.domain.SignInProviderFactory
import com.example.tracklayoff.core.common.user.UserRepository
import com.example.tracklayoff.features.auth.domain.AuthRepository
import com.example.tracklayoff.features.auth.ui.state.AuthUiEvent
import com.example.tracklayoff.features.auth.ui.state.AuthUiState
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewmodel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val authProvider: SignInProviderFactory,
    private val telemetry: CentralTelemetryInterface
): ViewModel() {

    val authUiState: StateFlow<AuthUiState> = authRepository.authStateFlow
        .map { user ->
        if(user != null) {
            AuthUiState.Authenticated(user)
        } else {
            AuthUiState.Guest
        }
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = userRepository.getCurrentUser()?.let { user ->
                AuthUiState.Authenticated(appUser = user)
            } ?: AuthUiState.Guest
    )

    private val _authenticationEvent = MutableSharedFlow<AuthUiEvent>()
    val authenticationEvent: SharedFlow<AuthUiEvent> = _authenticationEvent.asSharedFlow()

    private val _isSigningOutLoading = MutableStateFlow(false)
    val isSigningOutLoading: StateFlow<Boolean> = _isSigningOutLoading.asStateFlow()

    fun signIn(providerType: AuthProviderClientType, context: Context) {
        viewModelScope.launch {
            val client = authProvider.getProvider(providerType)
            val tokenResult = client.provideAuthenticationToken(context)

            tokenResult.onSuccess { idToken ->
                authRepository.signInWithCredentialToken(providerType, idToken)
                telemetry.trackUserLogin()
                _authenticationEvent.emit(
                    AuthUiEvent.ShowAuthenticationEvent(
                        message = "Signed in successfully"
                    )
                )
            }.onFailure { exception ->
                if (exception !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                    _authenticationEvent.emit(
                        AuthUiEvent.ShowAuthenticationEvent(
                            message = exception.localizedMessage ?: "Sign in failed. Please try again",
                            isError = true
                        )
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            telemetry.trackUserLoggedOut()
            _isSigningOutLoading.value = true

            delay(1000)
            authRepository.signOut()

            _isSigningOutLoading.value = false
            _authenticationEvent.emit(
                AuthUiEvent.ShowAuthenticationEvent(
                    message = "Signed out successfully"
                )
            )
        }
    }
}