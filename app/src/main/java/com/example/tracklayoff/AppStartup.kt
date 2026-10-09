package com.example.tracklayoff

import com.example.tracklayoff.core.common.util.AppLifecycleTracker
import com.example.tracklayoff.features.reporting.data.model.TelemetryEvents
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

/** Startup side effects are separate from the Android Application so they can be verified. */
class AppStartup @Inject constructor(
    private val lifecycleTracker: AppLifecycleTracker,
    private val messaging: FirebaseMessaging,
    private val telemetry: CentralTelemetryInterface
) {
    fun start() {
        lifecycleTracker.init()
        messaging.subscribeToTopic("all_users")
            .addOnSuccessListener { telemetry.track(TelemetryEvents.FIREBASE_MESSAGING_CONNECTION_ESTABLISHED) }
            .addOnFailureListener { error -> telemetry.track(
                TelemetryEvents.FIREBASE_MESSAGING_CONNECTION_FAILED,
                mapOf("errorMessage" to (error.message ?: error.javaClass.simpleName))
            ) }
    }
}
