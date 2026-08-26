package com.example.tracklayoff.features.notifications.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.tracklayoff.R
import com.example.tracklayoff.core.common.di.IoDispatcher
import com.example.tracklayoff.core.common.mapper.toNotificationPayload
import com.example.tracklayoff.core.common.util.AppLifecycleTracker
import com.example.tracklayoff.features.notifications.data.AppNotificationPayload
import com.example.tracklayoff.features.notifications.util.NotificationEventBus
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LayoffFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var lifecycleTracker: AppLifecycleTracker

    @Inject
    lateinit var notificationEventBus: NotificationEventBus

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var firebaseMessaging : FirebaseMessaging

    @Inject
    lateinit var telemetry: CentralTelemetryInterface

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        telemetry.trackNotificationReceived()

        if(message.data.isEmpty()) return

        val alertPayload = message.toNotificationPayload()

        if(lifecycleTracker.isAppInForeground.value) {
            CoroutineScope(ioDispatcher).launch {
                notificationEventBus.emitNotification(alertPayload)
            }
        } else {
            showSystemNotification(alertPayload)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "New FCM registration token generated: $token")
    }

    private fun showSystemNotification(alert: AppNotificationPayload.LayoffAlert){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "layoff_breaking_news_channel"

        // 1. Create Notification Channel (Android 8.0 / API 26+ Requirement)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Breaking Layoff Alerts",
                NotificationManager.IMPORTANCE_HIGH // HIGH/MAX enables Heads-Up Popups
            ).apply {
                description = "High-priority notifications for breaking layoff news"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. PendingIntent for Notification Tap (Opens MainActivity or Deep Link)
        val openAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(alert.newsUrl.ifEmpty { "https://google.com" })).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            alert.id.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Build Notification Content
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            // Collapsed / Base Properties
            .setSmallIcon(R.mipmap.ic_app_icon) // Make sure a monochrome/valid small vector exists
            .setContentTitle(alert.title)
            .setContentText(alert.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

            // Expanded State Customization
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${alert.companyName} has officially reported layoffs.\n\nStatus: ${alert.status}\nEmployees Affected: ${alert.impactCount}\n\nTap to read the full breaking news story.")
                    .setBigContentTitle("🚨 ${alert.companyName} Layoff Details")
                    .setSummaryText("Breaking Layoff Alert")
            )

            // Custom Action Button (Expanded State Action)
            .addAction(
                R.mipmap.ic_app_icon,
                "Read Article",
                pendingIntent
            )

        // 5. Fire Notification
        notificationManager.notify(alert.id.hashCode(), notificationBuilder.build())
    }
}