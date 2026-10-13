package com.example.tracklayoff.core.favorites.data

import com.example.tracklayoff.core.favorites.domain.FavoritesTokenProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseFavoritesTokenProvider @Inject constructor(private val auth: FirebaseAuth) : FavoritesTokenProvider {
    override suspend fun tokenFor(userId: String): String? {
        currentCoroutineContext().ensureActive()
        val user = auth.currentUser?.takeIf { it.uid == userId && !it.isAnonymous } ?: return null
        val token = try {
            Tasks.await(user.getIdToken(false), 15, TimeUnit.SECONDS).token
        } catch (error: ExecutionException) {
            when (val cause = error.cause) {
                is FirebaseAuthException -> return null
                is FirebaseNetworkException, is FirebaseTooManyRequestsException -> throw IOException("Token temporarily unavailable", cause)
                else -> throw error
            }
        } catch (error: TimeoutException) {
            throw IOException("Token request timed out", error)
        }
        currentCoroutineContext().ensureActive()
        return token.takeIf { auth.currentUser?.uid == userId }
    }
}
