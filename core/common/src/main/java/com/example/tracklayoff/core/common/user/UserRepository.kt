package com.example.tracklayoff.core.common.user

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.example.tracklayoff.core.common.user.data.AppUser
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

const val NON_USER_ID = "NON-USER"
@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserSession {
    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    override val userIds: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    /**
     * Returns the current user id if there is a session for an active user
     * else returns guest user id
     */
    fun getUserId(): String {
        return firebaseAuth.currentUser?.uid ?: NON_USER_ID
    }

    /**
     * Returns current logged-in user and maps it to app user
     */
    fun getCurrentUser(): AppUser? {
        Log.d("OUR_CURRENT_USER", firebaseAuth.currentUser?.toDomain().toString())
        return firebaseAuth.currentUser?.toDomain()
    }
}