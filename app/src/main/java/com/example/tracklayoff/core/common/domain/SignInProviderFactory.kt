package com.example.tracklayoff.core.common.domain

import com.example.tracklayoff.features.auth.client.GoogleSignInClient
import com.example.tracklayoff.features.auth.client.PhoneSignInClient
import javax.inject.Inject
import javax.inject.Singleton

enum class AuthProviderClientType {
    GOOGLE,
    PHONE
}

@Singleton
class SignInProviderFactory @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val phoneSignInClient: PhoneSignInClient
) {

    fun getProvider(type: AuthProviderClientType): AuthProviderClient {
        return when(type) {
            AuthProviderClientType.GOOGLE -> googleSignInClient
            AuthProviderClientType.PHONE -> phoneSignInClient
        }
    }
}