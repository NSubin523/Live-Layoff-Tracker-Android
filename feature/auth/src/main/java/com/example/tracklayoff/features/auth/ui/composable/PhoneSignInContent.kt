package com.example.tracklayoff.features.auth.ui.composable

import androidx.compose.runtime.Composable
import com.example.tracklayoff.features.auth.ui.PhoneAuthState

@Composable
fun PhoneSignInContent(state: PhoneAuthState, onDismiss: () -> Unit, onSendCode: (String) -> Unit, onVerify: (String) -> Unit) {
    when (state) {
        PhoneAuthState.Closed -> Unit
        is PhoneAuthState.NumberEntry, is PhoneAuthState.SendingCode -> PhoneNumberInputDialog(
            onDismissRequest = onDismiss,
            onSubmitPhoneNumber = onSendCode,
            error = (state as? PhoneAuthState.NumberEntry)?.error,
            isSending = state is PhoneAuthState.SendingCode
        )
        is PhoneAuthState.CodeEntry, is PhoneAuthState.Verifying -> {
            val entry = if (state is PhoneAuthState.Verifying) state.entry else state as PhoneAuthState.CodeEntry
            OtpVerificationInputDialog(
                phoneNumber = entry.phoneNumber,
                onDismissRequest = onDismiss,
                onSubmitOtp = onVerify,
                error = entry.error,
                isVerifying = state is PhoneAuthState.Verifying
            )
        }
    }
}
