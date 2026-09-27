package com.example.tracklayoff.core.common.ui.extension

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val appSnackBarDuration = 5000L

fun SnackbarHostState.showAppCustomSnackBar(
    scope: CoroutineScope,
    message: String,
    actionLabel: String? = null,
    customDuration : Long = appSnackBarDuration,
    onActionClick: (() -> Unit)? = null
) {
    val snackBarJob = scope.launch {
        val result = showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Indefinite
        )

        if(result == SnackbarResult.ActionPerformed) {
            onActionClick?.invoke()
        }
    }

    scope.launch {
        delay(customDuration)
        snackBarJob.cancel()
    }
}