package com.example.tracklayoff.features.auth.client

import android.content.Context
import com.example.tracklayoff.core.common.domain.AuthProviderClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneSignInClient @Inject constructor(): AuthProviderClient {
    override suspend fun provideAuthenticationToken(context: Context): Result<String> {
        return Result.failure(NotImplementedError("Phone sign-in coming soon"))
    }
}