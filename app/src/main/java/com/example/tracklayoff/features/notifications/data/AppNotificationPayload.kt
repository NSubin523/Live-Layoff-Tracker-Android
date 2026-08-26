package com.example.tracklayoff.features.notifications.data

sealed interface AppNotificationPayload {
    val id: String
    val title: String
    val body: String

    data class LayoffAlert(
        override val id: String,
        override val title: String,
        override val body: String,
        val companyId: String,
        val companyName: String,
        val status: String,
        val impactCount: Int,
        val logoUrl: String,
        val newsUrl: String
    ) : AppNotificationPayload
}