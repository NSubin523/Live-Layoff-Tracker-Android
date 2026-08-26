package com.example.tracklayoff.features.notifications.util

import com.example.tracklayoff.features.notifications.data.AppNotificationPayload
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationEventBus @Inject constructor() {
    private val _notificationEvent = MutableSharedFlow<AppNotificationPayload>(
        extraBufferCapacity = 1
    )

    val notificationEvents : SharedFlow<AppNotificationPayload> = _notificationEvent.asSharedFlow()

    suspend fun emitNotification(notification: AppNotificationPayload) {
        _notificationEvent.emit(notification)
    }
}