package com.example.tracklayoff.core.common.user

import kotlinx.coroutines.flow.Flow

/** Observable identity only; consumers never need Firebase types. */
interface UserSession {
    val userIds: Flow<String?>
    fun currentUserId(): String?
}
