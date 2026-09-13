package com.example.tracklayoff.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tracklayoff.core.common.domain.AuthProviderClientType
import com.example.tracklayoff.core.common.ui.composables.AppAccountDropdownMenu
import com.example.tracklayoff.core.common.ui.composables.AppBottomNavBar
import com.example.tracklayoff.core.common.ui.composables.AppLoadingOverlay
import com.example.tracklayoff.core.common.ui.composables.AppSnackBar
import com.example.tracklayoff.core.common.ui.composables.CustomTopAppBar
import com.example.tracklayoff.core.common.ui.composables.ObserveAppEvent
import com.example.tracklayoff.core.common.ui.extension.showAppCustomSnackBar
import com.example.tracklayoff.core.common.ui.state.AuthenticationState
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.features.auth.ui.AuthViewmodel
import com.example.tracklayoff.features.auth.ui.composable.OtpVerificationInputDialog
import com.example.tracklayoff.features.auth.ui.composable.PhoneNumberInputDialog
import com.example.tracklayoff.features.auth.ui.composable.SignInBottomSheet
import com.example.tracklayoff.features.auth.ui.state.AuthUiEvent
import com.example.tracklayoff.features.auth.ui.state.AuthUiState
import com.example.tracklayoff.features.feed.ui.composables.FeedScreen
import com.example.tracklayoff.features.notifications.ui.NotificationPermissionDialog
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import com.example.tracklayoff.features.tracker.ui.composable.TrackerScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    telemetry: CentralTelemetryInterface,
    authViewModel: AuthViewmodel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Lifecycle aware state collection
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()
    val isSigningOutEvent by authViewModel.isSigningOutLoading.collectAsStateWithLifecycle()

    // Survive process death and state configuration
    var selectedTabRoute by rememberSaveable { mutableStateOf(BottomNavItem.Feed.route) }
    val selectedTab = remember(selectedTabRoute) {
        when(selectedTabRoute) {
            BottomNavItem.Tracker.route -> BottomNavItem.Tracker
            else -> BottomNavItem.Feed
        }
    }

    var showSignInSheet by rememberSaveable { mutableStateOf(false) }
    var showDropdownMenu by rememberSaveable { mutableStateOf(false) }
    var showPhoneInputAlertDialog by rememberSaveable { mutableStateOf(false) }
    var showOtpInputDialog by rememberSaveable { mutableStateOf(false) }
    var enteredPhoneNumber by rememberSaveable { mutableStateOf("") }

    // Stabilizing composite param to allow CustomTopAppbar to skip composition when not needed
    val authenticationState = remember(authUiState) {
        val authenticated = authUiState as? AuthUiState.Authenticated
        AuthenticationState(
            isAuthenticated = authenticated != null,
            photoUrl = authenticated?.appUser?.photoUrl
        )
    }

    NotificationPermissionDialog(
        snackBarState = snackBarHostState,
        telemetry = telemetry
    )

    ObserveAppEvent(authViewModel.authenticationEvent) { event ->
        when(event) {
            is AuthUiEvent.ShowAuthenticationEvent -> {
                snackBarHostState.showAppCustomSnackBar(
                    scope = coroutineScope,
                    message = event.message
                )
            }
        }
    }

    if(showSignInSheet) {
        SignInBottomSheet(
            onDismissRequest = { showSignInSheet = false },
            onSignInClick = { provider ->
                showSignInSheet = false
                when(provider) {
                    AuthProviderClientType.GOOGLE -> {
                        authViewModel.signIn(providerType = provider, context = context)
                    }
                    AuthProviderClientType.PHONE -> {
                        showPhoneInputAlertDialog = true
                    }
                }
            },
        )
    }

    if(showPhoneInputAlertDialog) {
        PhoneNumberInputDialog(
            onDismissRequest = { showPhoneInputAlertDialog = false },
            onSubmitPhoneNumber = { phoneNumber ->
                showPhoneInputAlertDialog = false
                enteredPhoneNumber = phoneNumber
                /** Use phone number later **/
                showOtpInputDialog = true
            }
        )
    }

    if(showOtpInputDialog) {
        OtpVerificationInputDialog(
            phoneNumber = enteredPhoneNumber,
            onDismissRequest = { showOtpInputDialog = false },
            onSubmitOtp = { otpCode ->
                showOtpInputDialog = false
                authViewModel.signIn(
                    providerType = AuthProviderClientType.PHONE,
                    context = context
                )
            },
        )
    }

    Scaffold(
        topBar = {
            Box (
                modifier = Modifier.fillMaxWidth()
            ){
                CustomTopAppBar(
                    authenticationState = authenticationState,
                    onProfileClick = {
                        when(authUiState) {
                            is AuthUiState.Authenticated -> showDropdownMenu = true
                            AuthUiState.Guest -> showSignInSheet = true
                        }
                    }
                )

                Box(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(
                            top = AppDimens.DropdownMenuPaddingTop,
                            end = AppDimens.DropdownMenuPaddingEnd
                        )
                ) {
                    AppAccountDropdownMenu(
                        expanded = showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false },
                        onAccountClick = {
                            // TODO: Navigate or show Account details dialog
                        },
                        onAboutClick = {
                            // TODO: Navigate or show About dialog
                        },
                        onSignOutClick = {
                            showDropdownMenu = false
                            authViewModel.signOut()
                        }
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                AppSnackBar(snackbarData = data)
            }
        },
        bottomBar = {
            AppBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTabRoute = it.route }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                BottomNavItem.Feed -> {
                    FeedScreen(snackBarHostState = snackBarHostState,)
                }

                BottomNavItem.Tracker -> {
                    TrackerScreen(
                        authUiState = authUiState,
                        onSignInClick = { showSignInSheet = true }
                    )
                }
            }
        }

        if(isSigningOutEvent) {
            AppLoadingOverlay(
                isLoading = isSigningOutEvent,
                isOverlay = isSigningOutEvent
            )
        }
    }
}