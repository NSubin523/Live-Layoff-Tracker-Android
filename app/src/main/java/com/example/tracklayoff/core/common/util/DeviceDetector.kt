package com.example.tracklayoff.core.common.util

import android.os.Build

object DeviceDetector {
    val isEmulator: Boolean by lazy {
        (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MODEL.lowercase().contains("sdk_gphone") // 👈 Modern AVD System Images!
                || Build.HARDWARE.contains("goldfish")             // 👈 Android Emulator Hardware
                || Build.HARDWARE.contains("ranchu")               // 👈 Android Emulator QEMU Engine
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST.startsWith("Build")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.PRODUCT.lowercase().contains("sdk_gphone")
                || "google_sdk" == Build.PRODUCT)
    }

    fun getDynamicBaseUrl(physicalDeviceIp: String): String {
        val host = if (isEmulator) {
            "10.0.2.2" // Emulator local loopback
        } else {
            physicalDeviceIp // Physical Pixel Wi-Fi IP (e.g., 192.168.1.149)
        }
        return "http://$host:8000/api/v1/"
    }
}