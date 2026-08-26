package com.example.tracklayoff.features.auth.data

import com.example.tracklayoff.core.common.di.IoDispatcher
import com.example.tracklayoff.core.common.domain.AuthProviderClientType
import com.example.tracklayoff.core.common.mapper.toDomain
import com.example.tracklayoff.core.common.user.data.AppUser
import com.example.tracklayoff.core.network.NetworkResult
import com.example.tracklayoff.features.auth.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
): AuthRepository {

    override val authStateFlow: Flow<AppUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomain())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun signInWithCredentialToken(
        clientType: AuthProviderClientType,
        authToken: String
    ): NetworkResult<AppUser> = withContext(ioDispatcher) {
        try {
            val credential = when(clientType) {
                AuthProviderClientType.GOOGLE -> GoogleAuthProvider.getCredential(authToken, null)
                AuthProviderClientType.PHONE -> {
                    val (verificationId, smsCode) = authToken.split(":")
                    PhoneAuthProvider.getCredential(verificationId, smsCode)
                }
            }

            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user ?: return@withContext NetworkResult.Error(
                exception = IllegalStateException("Sign in failed: null user"),
                message = "Authentication Failed"
            )

            NetworkResult.Success(user.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(exception = e, message = e.localizedMessage ?: "Google Sign-in error")
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}