package com.example.tracklayoff.core.common.domain

import android.content.Context

interface AuthProviderClient {

    suspend fun provideAuthenticationToken(context: Context): Result<String>
}