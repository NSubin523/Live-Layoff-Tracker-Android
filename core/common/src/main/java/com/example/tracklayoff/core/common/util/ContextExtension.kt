package com.example.tracklayoff.core.common.util

import android.content.pm.PackageManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String): Boolean {
    return if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        &&
        permission == android.Manifest.permission.POST_NOTIFICATIONS
        ) {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
    else if (
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        &&
        permission == android.Manifest.permission.POST_NOTIFICATIONS
        ) {
        true // Notifications are enabled by default on API < 33
        }
        else {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
}

fun Context.openAppSettingsScreen() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}