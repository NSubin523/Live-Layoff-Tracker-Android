package com.example.tracklayoff.features.auth.domain

import android.content.Context
import com.example.tracklayoff.core.common.user.data.AppUser
import com.example.tracklayoff.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface PhoneAuthGateway {
    fun requestCode(context: Context, phoneNumber: String): Flow<PhoneVerificationEvent>
    suspend fun verifyCode(verificationId: String, code: String): NetworkResult<AppUser>
}

sealed interface PhoneVerificationEvent {
    data class CodeSent(val verificationId: String) : PhoneVerificationEvent
    data class Authenticated(val user: AppUser) : PhoneVerificationEvent
    data class Failed(val message: String) : PhoneVerificationEvent
}

object PhoneInput {
    /** Unprefixed numbers use the US country code; international input must start with +. */
    fun normalize(number: String): String? {
        val input = number.trim()
        if (input.any { it !in "0123456789+ -()." }) return null
        val compact = input.filter { it in "0123456789+" }
        if (compact.startsWith('+')) {
            val digits = compact.drop(1)
            return compact.takeIf { digits.length in 8..15 && digits.all { it in '0'..'9' } && digits.firstOrNull() != '0' }
        }
        return when {
            compact.length == 10 && compact.all { it in '0'..'9' } -> "+1$compact"
            compact.length == 11 && compact.startsWith('1') && compact.all { it in '0'..'9' } -> "+$compact"
            else -> null
        }
    }
    fun isValidCode(code: String) = code.length == 6 && code.all { it in '0'..'9' }
}
