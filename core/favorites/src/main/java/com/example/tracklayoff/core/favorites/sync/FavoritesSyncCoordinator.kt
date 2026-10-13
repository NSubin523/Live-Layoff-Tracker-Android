package com.example.tracklayoff.core.favorites.sync

import com.example.tracklayoff.core.common.user.UserSession
import com.example.tracklayoff.core.common.util.AppLifecycleTracker
import com.example.tracklayoff.core.favorites.domain.FavoritesSyncScheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/** Application lifetime: recover outbox rows at startup, sign-in, and foreground entry. */
@Singleton
class FavoritesSyncCoordinator @Inject constructor(
    private val session: UserSession,
    private val lifecycle: AppLifecycleTracker,
    private val scheduler: FavoritesSyncScheduler
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var started = false
    fun start() {
        if (started) return
        started = true
        scope.launch {
            var previousUserId: String? = null
            combine(session.userIds, lifecycle.isAppInForeground) { user, foreground -> user to foreground }
                .distinctUntilChanged().collect { (userId, foreground) ->
                    try {
                        if (previousUserId != userId) previousUserId?.let { scheduler.cancel(it) }
                        previousUserId = userId
                        if (userId != null && foreground) scheduler.schedule(userId)
                    } catch (cancelled: CancellationException) { throw cancelled }
                    catch (_: Exception) { /* The outbox remains durable for the next recovery trigger. */ }
                }
        }
    }
}
