package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.tracklayoff.designsystems.AppColors
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.designsystems.White

@Composable
fun AppSnackBar(snackbarData: SnackbarData) {
    Snackbar(
        modifier = Modifier.padding(
            horizontal = AppDimens.SnackBarPaddingHorizontal,
            vertical = AppDimens.SnackBarPaddingVertical
        ),
        shape = RoundedCornerShape(AppDimens.RoundedCornerShapeSnackBar),
        containerColor = AppColors.PrimarySnackBarColor,
        contentColor = Color.White,
        action = snackbarData.visuals.actionLabel?.let { actionText ->
            {
                TextButton(
                    onClick = { snackbarData.performAction() }
                ) {
                    Text(
                        text = actionText,
                        color = White,
                        fontSize = AppDimens.SnackBarFontSize
                    )
                }
            }
        }
    ) {
        Text(
            text = snackbarData.visuals.message,
            fontSize = AppDimens.SnackBarFontSize,
            color = Color.White
        )
    }
}