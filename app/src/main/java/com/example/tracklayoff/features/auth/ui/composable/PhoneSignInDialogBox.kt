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
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.designsystems.PhoneDialogContentPlaceholder
import com.example.tracklayoff.designsystems.PhoneDialogContentTitle
import com.example.tracklayoff.designsystems.PhoneDialogHeader
import com.example.tracklayoff.designsystems.PhoneDialogSendCode

const val MAX_PH_LEN = 10

@Composable
fun PhoneNumberInputDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSubmitPhoneNumber: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    val isValidPhoneNumber = phoneNumber.trim().length >= MAX_PH_LEN

    AppAlertDialog(
        title = PhoneDialogHeader,
        confirmButtonText = PhoneDialogSendCode,
        isConfirmEnabled = isValidPhoneNumber,
        onDismissRequest = onDismissRequest,
        onConfirmClick = { onSubmitPhoneNumber(phoneNumber.trim()) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = PhoneDialogContentTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(AppDimens.PhoneInputDialogHeight))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '+' }) {
                        phoneNumber = input
                    }
                },
                placeholder = { Text(PhoneDialogContentPlaceholder) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isValidPhoneNumber) {
                            onSubmitPhoneNumber(phoneNumber.trim())
                        }
                    }
                ),
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}