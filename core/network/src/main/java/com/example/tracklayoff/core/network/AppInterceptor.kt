package com.example.tracklayoff.core.network

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInterceptor @Inject constructor(private val firebaseAuth: FirebaseAuth) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val user = firebaseAuth.currentUser ?: return chain.proceed(originalRequest)
        val token = try {
            Tasks.await(user.getIdToken(false), 15, TimeUnit.SECONDS).token
        } catch (error: Exception) {
            Log.e("APP_INTERCEPTOR_ERROR", "Unable to obtain authentication token", error)
            null
        }
        val request = if (token.isNullOrEmpty()) originalRequest else originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
