package com.example.tracklayoff.features.auth.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracklayoff.core.network.NetworkResult
import com.example.tracklayoff.features.auth.domain.PhoneAuthGateway
import com.example.tracklayoff.features.auth.domain.PhoneInput
import com.example.tracklayoff.features.auth.domain.PhoneVerificationEvent
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PhoneAuthState {
    data object Closed : PhoneAuthState
    data class NumberEntry(val error: String? = null) : PhoneAuthState
    data class SendingCode(val phoneNumber: String) : PhoneAuthState
    data class CodeEntry(val phoneNumber: String, val verificationId: String, val error: String? = null) : PhoneAuthState
    data class Verifying(val entry: CodeEntry) : PhoneAuthState
}

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val gateway: PhoneAuthGateway,
    private val telemetry: CentralTelemetryInterface
) : ViewModel() {
    private val mutableState = MutableStateFlow<PhoneAuthState>(PhoneAuthState.Closed)
    val state = mutableState.asStateFlow()
    private var requestJob: Job? = null
    private var verifyJob: Job? = null
    fun open() { dismiss(); mutableState.value = PhoneAuthState.NumberEntry() }
    fun dismiss() {
        requestJob?.cancel(); verifyJob?.cancel()
        mutableState.value = PhoneAuthState.Closed
    }
    fun sendCode(context: Context, input: String) {
        if (mutableState.value !is PhoneAuthState.NumberEntry) return
        val phone = PhoneInput.normalize(input)
        if (phone == null) { mutableState.value = PhoneAuthState.NumberEntry("Enter a valid phone number"); return }
        mutableState.value = PhoneAuthState.SendingCode(phone)
        requestJob = viewModelScope.launch {
            gateway.requestCode(context, phone).collect { event ->
                when (event) {
                    is PhoneVerificationEvent.CodeSent -> if (mutableState.value is PhoneAuthState.SendingCode) {
                        mutableState.value = PhoneAuthState.CodeEntry(phone, event.verificationId)
                    }
                    is PhoneVerificationEvent.Authenticated -> complete()
                    is PhoneVerificationEvent.Failed -> mutableState.value = PhoneAuthState.NumberEntry(event.message)
                }
            }
        }
    }
    fun verify(code: String) {
        val entry = mutableState.value as? PhoneAuthState.CodeEntry ?: return
        if (!PhoneInput.isValidCode(code)) {
            mutableState.value = entry.copy(error = "Enter the 6-digit verification code"); return
        }
        requestJob?.cancel()
        mutableState.value = PhoneAuthState.Verifying(entry)
        verifyJob = viewModelScope.launch {
            when (val result = gateway.verifyCode(entry.verificationId, code)) {
                is NetworkResult.Success -> complete()
                is NetworkResult.Error -> mutableState.value = entry.copy(error = result.message ?: "Phone sign-in failed")
                NetworkResult.Loading -> Unit
            }
        }
    }
    private fun complete() {
        telemetry.trackUserLogin()
        mutableState.value = PhoneAuthState.Closed
    }
}
