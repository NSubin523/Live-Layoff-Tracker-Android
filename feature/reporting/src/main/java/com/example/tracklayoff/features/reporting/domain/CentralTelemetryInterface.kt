package com.example.tracklayoff.features.reporting.domain

interface CentralTelemetryInterface {
    fun trackAppLaunched()
    fun trackAppPaused()
    fun trackAppResumed()
    fun trackCompanyCardClicked()
    fun trackInternetDisconnected()
    fun trackInternetConnectionResumed()
    fun trackNotificationReceived()
    fun trackNotificationPermissionDenied()
    fun trackNotificationClicked()
    fun trackUserLogin()
    fun trackUserLoggedOut()

    fun track(eventName: String, payload: Map<String, String>? = null)
}