package com.example.tracklayoff.core.common.mapper

import com.example.tracklayoff.core.common.user.data.AppUser
import com.example.tracklayoff.features.notifications.data.AppNotificationPayload
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.RemoteMessage

fun RemoteMessage.toNotificationPayload(): AppNotificationPayload.LayoffAlert {
    val companyName = data["company_name"] ?: "Unknown Company"
    val status = data["status"] ?: "Confirmed"
    val impactCount = data["impact_count"]?.toIntOrNull() ?: 0

    return AppNotificationPayload.LayoffAlert(
        id = data["company_id"] ?: System.currentTimeMillis().toString(),
        title = "$companyName Layoff Alert",
        body = "$companyName announced layoffs ($status). Impact: $impactCount employees.",
        companyId = data["company_id"] ?: "",
        companyName = companyName,
        status = status,
        impactCount = impactCount,
        logoUrl = data["logo_url"] ?: "",
        newsUrl = data["news_url"] ?: ""
    )
}

fun FirebaseUser.toDomain(): AppUser {
    return AppUser(
        firebaseId = uid,
        name = displayName,
        photoUrl = photoUrl?.toString(),
        email = email
    )
}