package com.example.tracklayoff.navigation

import android.app.Activity
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
    val activity = context as? Activity

    val authUiState by authViewModel.authUiState.collectAsState()
    val authenticatedState = authUiState as? AuthUiState.Authenticated
    var showSignInSheet by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    val isSigningOutEvent by authViewModel.isSigningOutLoading.collectAsState()

    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Feed) }
    var showPhoneInputAlertDialog by remember { mutableStateOf(false) }
    var showOtpInputDialog by remember { mutableStateOf(false) }
    var enteredPhoneNumber by remember { mutableStateOf("") }
    var activeVerificationId by remember { mutableStateOf("") }

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
                    authenticationState = AuthenticationState(
                        isAuthenticated = authenticatedState != null,
                        photoUrl = authenticatedState?.appUser?.photoUrl
                    ),
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
                onTabSelected = { selectedTab = it }
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