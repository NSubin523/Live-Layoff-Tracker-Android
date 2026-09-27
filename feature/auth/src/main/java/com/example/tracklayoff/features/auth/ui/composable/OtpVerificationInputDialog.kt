package com.example.tracklayoff.features.auth.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.tracklayoff.core.common.ui.composables.AppAlertDialog
import com.example.tracklayoff.designsystems.AppDimens.PhoneInputDialogHeight
import com.example.tracklayoff.designsystems.OtpDialogPlaceHolderText
import com.example.tracklayoff.designsystems.OtpVerificationButtonText
import com.example.tracklayoff.designsystems.OtpVerificationHeader

const val OTP_LEN = 6

@Composable
fun OtpVerificationInputDialog(
    modifier: Modifier = Modifier,
    phoneNumber: String,
    onDismissRequest: () -> Unit,
    onSubmitOtp: (String) -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    val isOtpValid = otpCode.trim().length >= OTP_LEN

    AppAlertDialog(
        title = OtpVerificationHeader,
        confirmButtonText = OtpVerificationButtonText,
        onDismissRequest = onDismissRequest,
        onConfirmClick = { onSubmitOtp(otpCode.trim()) },
        isConfirmEnabled = isOtpValid,
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = "Enter the 6-digit verification code sent to $phoneNumber.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = modifier.height(PhoneInputDialogHeight))

            OutlinedTextField(
                value = otpCode,
                onValueChange = { input ->
                    if (input.length <= 6 && input.all { it.isDigit() }) {
                        otpCode = input
                    }
                },
                placeholder = { Text(OtpDialogPlaceHolderText) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isOtpValid) {
                            onSubmitOtp(otpCode.trim())
                        }
                    }
                ),
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}