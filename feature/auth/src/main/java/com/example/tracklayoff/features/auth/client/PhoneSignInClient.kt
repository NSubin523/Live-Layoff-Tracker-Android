package com.example.tracklayoff.features.auth.client

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.example.tracklayoff.core.common.user.toDomain
import com.example.tracklayoff.core.common.user.data.AppUser
import com.example.tracklayoff.core.network.NetworkResult
import com.example.tracklayoff.features.auth.domain.PhoneAuthGateway
import com.example.tracklayoff.features.auth.domain.PhoneInput
import com.example.tracklayoff.features.auth.domain.PhoneVerificationEvent
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneSignInClient @Inject constructor(private val firebaseAuth: FirebaseAuth) : PhoneAuthGateway {
    override fun requestCode(context: Context, phoneNumber: String): Flow<PhoneVerificationEvent> = callbackFlow {
        val activity = context.findActivity()
        val normalized = PhoneInput.normalize(phoneNumber)
        if (activity == null || normalized == null) {
            trySend(PhoneVerificationEvent.Failed(if (activity == null) "Activity context required" else "Enter a valid phone number"))
            close()
            return@callbackFlow
        }
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                trySend(PhoneVerificationEvent.CodeSent(id))
            }
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Firebase already provided the complete credential; never reconstruct it from signInMethod.
                launch {
                    when (val result = signIn(credential)) {
                        is NetworkResult.Success -> trySend(PhoneVerificationEvent.Authenticated(result.data))
                        is NetworkResult.Error -> trySend(PhoneVerificationEvent.Failed(result.message ?: "Phone sign-in failed"))
                        NetworkResult.Loading -> Unit
                    }
                    close()
                }
            }
            override fun onVerificationFailed(error: FirebaseException) {
                trySend(PhoneVerificationEvent.Failed(error.localizedMessage ?: "Couldn’t send verification code"))
                close()
            }
            override fun onCodeAutoRetrievalTimeOut(id: String) {
                // Manual verification remains valid after auto-retrieval ends.
                close()
            }
        }
        try {
            PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(normalized).setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity).setCallbacks(callbacks).build())
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            trySend(PhoneVerificationEvent.Failed(error.localizedMessage ?: "Couldn’t send verification code"))
            close()
        }
        awaitClose { /* Firebase has no cancellation API; a closed channel ignores late callbacks. */ }
    }

    override suspend fun verifyCode(verificationId: String, code: String): NetworkResult<AppUser> {
        if (verificationId.isBlank() || !PhoneInput.isValidCode(code)) {
            return NetworkResult.Error(IllegalArgumentException("Invalid verification code"), "Enter the 6-digit verification code")
        }
        return signIn(PhoneAuthProvider.getCredential(verificationId, code))
    }

    private suspend fun signIn(credential: PhoneAuthCredential): NetworkResult<AppUser> = try {
        val user = firebaseAuth.signInWithCredential(credential).await().user
            ?: error("Sign-in returned no user")
        NetworkResult.Success(user.toDomain())
    } catch (error: Exception) {
        if (error is CancellationException) throw error
        NetworkResult.Error(error, error.localizedMessage ?: "Phone sign-in failed")
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> if (baseContext !== this) baseContext.findActivity() else null
    else -> null
}
