package com.example.tracklayoff.features.auth.domain

import com.example.tracklayoff.core.common.domain.AuthProviderClientType
import com.example.tracklayoff.core.common.user.data.AppUser
import com.example.tracklayoff.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authStateFlow: Flow<AppUser?>
    fun isUserLoggedIn(): Boolean

    suspend fun signInWithCredentialToken(
        clientType: AuthProviderClientType,
        authToken: String
    ): NetworkResult<AppUser>

    suspend fun signOut()
}