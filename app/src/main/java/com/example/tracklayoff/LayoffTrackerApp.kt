package com.example.tracklayoff

import android.app.Application
import com.example.tracklayoff.core.common.util.AppLifecycleTracker
import com.example.tracklayoff.features.reporting.data.model.TelemetryEvents
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LayoffTrackerApp: Application() {

    @Inject
    lateinit var lifecycleTracker: AppLifecycleTracker

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var telemetry: CentralTelemetryInterface

    override fun onCreate() {
        super.onCreate()
        lifecycleTracker.init()

        firebaseMessaging.subscribeToTopic("all_users")
            .addOnSuccessListener { _ ->
                telemetry.track(TelemetryEvents.FIREBASE_MESSAGING_CONNECTION_ESTABLISHED)
            }
            .addOnFailureListener { error ->
                val attribute = mapOf(
                    "errorMessage" to error.message.toString()
                )
                telemetry.track(
                    TelemetryEvents.FIREBASE_MESSAGING_CONNECTION_FAILED,
                    attribute
                )
            }
    }
}