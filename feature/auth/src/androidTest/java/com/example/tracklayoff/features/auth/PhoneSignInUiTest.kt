package com.example.tracklayoff.features.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tracklayoff.features.auth.ui.PhoneAuthState
import com.example.tracklayoff.features.auth.ui.composable.*
import com.example.tracklayoff.core.common.domain.AuthProviderClientType
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PhoneSignInUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun bothProvidersRemainAvailable() {
        val clicked = mutableListOf<AuthProviderClientType>()
        compose.setContent { MaterialTheme { SignInBottomSheetContent({}, { clicked += it }) } }
        compose.onNodeWithText("Sign in with Google").performClick()
        compose.onNodeWithText("Sign in with Phone").performClick()
        assertEquals(listOf(AuthProviderClientType.GOOGLE, AuthProviderClientType.PHONE), clicked)
    }
    @Test fun formattedNumberCanBeSubmittedAndInvalidNumberCannot() {
        var number: String? = null
        compose.setContent { MaterialTheme { PhoneNumberInputDialog(onDismissRequest = {}, onSubmitPhoneNumber = { number = it }) } }
        compose.onNodeWithText("Send Code").assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).performTextInput("650-555-0100")
        compose.onNodeWithText("Send Code").assertIsEnabled().performClick()
        assertEquals("650-555-0100", number)
    }
    @Test fun OTPRequiresExactlySixDigitsAndSubmitsEnteredCode() {
        var code: String? = null
        compose.setContent { MaterialTheme { OtpVerificationInputDialog(phoneNumber = "+16505550100", onDismissRequest = {}, onSubmitOtp = { code = it }) } }
        compose.onNodeWithText("Verify").assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).performTextInput("12345")
        compose.onNodeWithText("Verify").assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).performTextInput("6")
        compose.onNodeWithText("Verify").assertIsEnabled().performClick()
        assertEquals("123456", code)
    }
    @Test fun failureIsVisibleAndVerificationDisablesRepeatedSubmit() {
        val state = androidx.compose.runtime.mutableStateOf<PhoneAuthState>(PhoneAuthState.CodeEntry("+16505550100", "session", "Wrong code"))
        compose.setContent { MaterialTheme { PhoneSignInContent(state.value, {}, {}, {}) } }
        compose.onNodeWithText("Wrong code").assertIsDisplayed()
        compose.runOnIdle { state.value = PhoneAuthState.Verifying(PhoneAuthState.CodeEntry("+16505550100", "session")) }
        compose.onNodeWithText("Verify").assertIsNotEnabled()
    }
}
