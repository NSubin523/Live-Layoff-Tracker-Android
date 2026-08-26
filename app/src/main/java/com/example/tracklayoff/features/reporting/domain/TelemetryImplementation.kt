package com.example.tracklayoff.features.reporting.domain

import com.example.tracklayoff.core.common.util.TelemetryQueue
import com.example.tracklayoff.features.reporting.data.model.TelemetryEvents
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelemetryImplementation @Inject constructor(
    private val queue: TelemetryQueue
): CentralTelemetryInterface {
    override fun trackAppLaunched() = track(TelemetryEvents.EVENT_APP_LAUNCHED)

    override fun trackAppPaused() = track(TelemetryEvents.EVENT_APP_PAUSED)

    override fun trackAppResumed() = track(TelemetryEvents.EVENT_APP_LAUNCHED)

    override fun trackCompanyCardClicked() = track(TelemetryEvents.EVENT_COMPANY_CARD_CLICKED)

    override fun trackInternetDisconnected() = track(TelemetryEvents.EVENT_INTERNET_DISCONNECTED)

    override fun trackInternetConnectionResumed() = track(TelemetryEvents.EVENT_INTERNET_CONNECTED)

    override fun trackNotificationReceived() = track(TelemetryEvents.EVENT_NOTIFICATION_RECEIVED)

    override fun trackNotificationPermissionDenied() = track(TelemetryEvents.EVENT_NOTIFICATION_DENIED)

    override fun trackNotificationClicked() = track(TelemetryEvents.EVENT_NOTIFICATION_CLICKED)
    override fun trackUserLogin() = track(TelemetryEvents.EVENT_USER_SIGN_IN)
    override fun trackUserLoggedOut() = track(TelemetryEvents.EVENT_USER_SIGN_OUT)

    override fun track(
        eventName: String,
        payload: Map<String, String>?
    ) {
        queue.enqueue(
            eventName = eventName,
            payload = payload
        )
    }

}