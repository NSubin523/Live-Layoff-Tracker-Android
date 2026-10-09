package com.example.tracklayoff

import com.example.tracklayoff.core.common.util.AppLifecycleTracker
import com.example.tracklayoff.features.reporting.data.model.TelemetryEvents
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import com.google.android.gms.tasks.*
import com.google.firebase.messaging.FirebaseMessaging
import org.junit.Test
import org.mockito.Mockito.*

class AppStartupTest {
    @Suppress("UNCHECKED_CAST")
    private fun setup(success: Boolean, error: Exception = IllegalStateException("Offline")): Triple<AppStartup, AppLifecycleTracker, CentralTelemetryInterface> {
        val lifecycle = mock(AppLifecycleTracker::class.java)
        val messaging = mock(FirebaseMessaging::class.java)
        val telemetry = mock(CentralTelemetryInterface::class.java)
        val task = mock(Task::class.java) as Task<Void>
        `when`(messaging.subscribeToTopic("all_users")).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer {
            if (success) (it.arguments[0] as OnSuccessListener<Void>).onSuccess(null)
            task
        }
        `when`(task.addOnFailureListener(any())).thenAnswer {
            if (!success) (it.arguments[0] as OnFailureListener).onFailure(error)
            task
        }
        return Triple(AppStartup(lifecycle, messaging, telemetry), lifecycle, telemetry)
    }
    @Test fun startupInitializesLifecycleAndReportsTopicSubscriptionSuccess() {
        val (startup, lifecycle, telemetry) = setup(true)
        startup.start()
        verify(lifecycle).init()
        verify(telemetry).track(TelemetryEvents.FIREBASE_MESSAGING_CONNECTION_ESTABLISHED)
        verifyNoMoreInteractions(telemetry)
    }
    @Test fun subscriptionFailureIncludesErrorContext() {
        val (startup, _, telemetry) = setup(false)
        startup.start()
        verify(telemetry).track(TelemetryEvents.FIREBASE_MESSAGING_CONNECTION_FAILED, mapOf("errorMessage" to "Offline"))
        verifyNoMoreInteractions(telemetry)
    }
    @Test fun missingErrorMessageUsesExceptionType() {
        val (startup, _, telemetry) = setup(false, IllegalStateException())
        startup.start()
        verify(telemetry).track(TelemetryEvents.FIREBASE_MESSAGING_CONNECTION_FAILED, mapOf("errorMessage" to "IllegalStateException"))
    }
}
