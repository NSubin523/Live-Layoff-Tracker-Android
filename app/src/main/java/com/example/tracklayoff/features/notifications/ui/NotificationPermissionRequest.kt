package com.example.tracklayoff.features.notifications.ui

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.tracklayoff.core.common.ui.extension.showAppCustomSnackBar
import com.example.tracklayoff.core.common.util.isPermissionGranted
import com.example.tracklayoff.core.common.util.openAppSettingsScreen
import com.example.tracklayoff.features.reporting.data.model.TelemetryEvents
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermissionDialog(
    snackBarState: SnackbarHostState,
    telemetry: CentralTelemetryInterface
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = context as? Activity
    val permission = Manifest.permission.POST_NOTIFICATIONS

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            telemetry.track(
                TelemetryEvents.EVENT_NOTIFICATION_DENIED
            )
        }
    }

    LaunchedEffect(Unit) {
        if (!context.isPermissionGranted(permission)) {
            val showRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false

            if (!showRationale) {
                permissionLauncher.launch(permission)
            } else {
                telemetry.track(TelemetryEvents.EVENT_ENABLE_NOTIFICATION_BANNER_SHOWN)
                snackBarState.showAppCustomSnackBar(
                    scope = scope,
                    message = "Notifications disabled. Enable to receive live layoff alerts.",
                    actionLabel = "Enable",
                    onActionClick = {
                        telemetry.track(TelemetryEvents.EVENT_NOTIFICATION_ALLOWED)
                        context.openAppSettingsScreen()
                    }
                )
            }
        }
    }
}