package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tracklayoff.core.designsystem.AppDimens

@Composable
fun AppSnackBar(snackbarData: SnackbarData) {
    Snackbar(
        modifier = Modifier.padding(
            horizontal = AppDimens.SnackBarPaddingHorizontal,
            vertical = AppDimens.SnackBarPaddingVertical
        ),
        shape = RoundedCornerShape(AppDimens.RoundedCornerShapeSnackBar),
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        action = snackbarData.visuals.actionLabel?.let { actionText ->
            {
                TextButton(
                    onClick = { snackbarData.performAction() }
                ) {
                    Text(
                        text = actionText,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        fontSize = AppDimens.SnackBarFontSize
                    )
                }
            }
        }
    ) {
        Text(
            text = snackbarData.visuals.message,
            fontSize = AppDimens.SnackBarFontSize,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}