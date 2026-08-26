package com.example.tracklayoff.core.network

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val currentUser = firebaseAuth.currentUser ?: return chain.proceed(originalRequest)

        return try {
            val tokenResult = Tasks.await(currentUser.getIdToken(false))
            val idToken = tokenResult.token

            if (!idToken.isNullOrEmpty()) {
                val authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $idToken")
                    .build()
                chain.proceed(authenticatedRequest)
            } else {
                chain.proceed(originalRequest)
            }
        } catch (e: Exception) {
            Log.e("APP_INTERCEPTOR_ERROR",e.message.toString())
            chain.proceed(originalRequest)
        }
    }
}