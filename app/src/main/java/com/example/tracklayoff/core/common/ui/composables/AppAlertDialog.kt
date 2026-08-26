package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tracklayoff.designsystems.AppAlertDialogCancelText

@Composable
fun AppAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    confirmButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    isConfirmEnabled: Boolean = true,
    dismissButtonText: String = AppAlertDialogCancelText,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title)
        },
        text = {
            content()
        },
        confirmButton = {
            Button(
                onClick = onConfirmClick,
                enabled = isConfirmEnabled
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(dismissButtonText)
            }
        },
        modifier = modifier
    )
}