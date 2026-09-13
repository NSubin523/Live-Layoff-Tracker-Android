package com.example.tracklayoff.features.auth.client

import android.app.Activity
import android.content.Context
import com.example.tracklayoff.core.common.domain.AuthProviderClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class PhoneSignInClient @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthProviderClient {
    override suspend fun provideAuthenticationToken(context: Context): Result<String> {
        val activity = context as? Activity
            ?: return Result.failure(IllegalArgumentException("Activity context required"))

        return suspendCancellableCoroutine { continuation ->
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    if(continuation.isActive) {
                        continuation.resume(Result.success(p0))
                    }
                }
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val code = p0.smsCode ?: "12345"
                    val verificationId = p0.signInMethod ?: ""

                    if(continuation.isActive) {
                        continuation.resume(Result.success("$verificationId:$code"))
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    if(continuation.isActive) {
                        continuation.resume(Result.failure(p0))
                    }
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+1 555-555-0100")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
}