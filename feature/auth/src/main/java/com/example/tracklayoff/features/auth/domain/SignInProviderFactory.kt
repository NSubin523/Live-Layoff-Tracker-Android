package com.example.tracklayoff.features.auth.domain

import com.example.tracklayoff.core.common.domain.AuthProviderClient
import com.example.tracklayoff.core.common.domain.AuthProviderClientType
import com.example.tracklayoff.features.auth.client.GoogleSignInClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInProviderFactory @Inject constructor(
    private val googleSignInClient: GoogleSignInClient
) {

    fun getProvider(type: AuthProviderClientType): AuthProviderClient {
        return when(type) {
            AuthProviderClientType.GOOGLE -> googleSignInClient
            AuthProviderClientType.PHONE -> error("Use the phone verification flow")
        }
    }
}
