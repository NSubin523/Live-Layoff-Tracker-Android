package com.example.tracklayoff.features.auth.client

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.tracklayoff.core.common.domain.AuthProviderClient
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInClient @Inject constructor(): AuthProviderClient {
    override suspend fun provideAuthenticationToken(context: Context): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val webClientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            val webClientId = if (webClientIdResId != 0) context.getString(webClientIdResId) else ""

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            Result.success(googleCredential.idToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}