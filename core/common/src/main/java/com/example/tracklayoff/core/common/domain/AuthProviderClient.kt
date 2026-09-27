package com.example.tracklayoff.core.common.domain

import android.content.Context

enum class AuthProviderClientType {
    GOOGLE,
    PHONE
}

interface AuthProviderClient {
    suspend fun provideAuthenticationToken(context: Context): Result<String>
}
