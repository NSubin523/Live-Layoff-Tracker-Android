package com.example.tracklayoff.features.auth

import android.content.Context
import com.example.tracklayoff.core.common.user.data.AppUser
import com.example.tracklayoff.core.network.NetworkResult
import com.example.tracklayoff.features.auth.domain.*
import com.example.tracklayoff.features.auth.ui.*
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneAuthTest {
    private val user = AppUser("phone-user", null, null, null)
    private class FakeGateway : PhoneAuthGateway {
        val events = MutableSharedFlow<PhoneVerificationEvent>()
        val numbers = mutableListOf<String>()
        val submissions = mutableListOf<Pair<String, String>>()
        var result: NetworkResult<AppUser> = NetworkResult.Success(AppUser("phone-user", null, null, null))
        var gate: CompletableDeferred<Unit>? = null
        override fun requestCode(context: Context, phoneNumber: String): Flow<PhoneVerificationEvent> {
            numbers += phoneNumber
            return events
        }
        override suspend fun verifyCode(verificationId: String, code: String): NetworkResult<AppUser> {
            submissions += verificationId to code
            gate?.await()
            return result
        }
    }
    private fun withVm(block: suspend TestScope.(PhoneAuthViewModel, FakeGateway, CentralTelemetryInterface) -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val gateway = FakeGateway()
        val telemetry = mock(CentralTelemetryInterface::class.java)
        val vm = PhoneAuthViewModel(gateway, telemetry)
        try { block(vm, gateway, telemetry) } finally { vm.dismiss(); runCurrent(); Dispatchers.resetMain() }
    }
    @Test fun `normalizes formatted test number and international numbers`() {
        assertEquals("+15555550100", PhoneInput.normalize("555-555-0100"))
        assertEquals("+15555550100", PhoneInput.normalize("+1 (555) 555-0100"))
        assertEquals("+447911123456", PhoneInput.normalize("+44 7911 123456"))
        listOf("", "123", "5555550100+", "++15555550100", "+012345678", "abc5555550100").forEach { assertNull(PhoneInput.normalize(it)) }
        assertTrue(PhoneInput.isValidCode("123456"))
        listOf("12345", "1234567", "abcdef", "１２３４５６").forEach { assertFalse(PhoneInput.isValidCode(it)) }
    }
    @Test fun `code is requested before OTP and entered OTP uses actual verification id`() = withVm { vm, gateway, telemetry ->
        vm.open(); vm.sendCode(mock(Context::class.java), "555-555-0100"); runCurrent()
        assertTrue(vm.state.value is PhoneAuthState.SendingCode)
        assertEquals(listOf("+15555550100"), gateway.numbers)
        gateway.events.emit(PhoneVerificationEvent.CodeSent("firebase-session")); runCurrent()
        assertTrue(vm.state.value is PhoneAuthState.CodeEntry)
        vm.verify("123456"); runCurrent()
        assertEquals(listOf("firebase-session" to "123456"), gateway.submissions)
        assertEquals(PhoneAuthState.Closed, vm.state.value)
        verify(telemetry).trackUserLogin()
    }
    @Test fun `wrong OTP keeps session available for retry and does not report login`() = withVm { vm, gateway, telemetry ->
        vm.open(); vm.sendCode(mock(Context::class.java), "5555550100"); runCurrent()
        gateway.events.emit(PhoneVerificationEvent.CodeSent("session")); runCurrent()
        gateway.result = NetworkResult.Error(IllegalArgumentException(), "Wrong code")
        vm.verify("000000"); runCurrent()
        val state = vm.state.value as PhoneAuthState.CodeEntry
        assertEquals("session", state.verificationId); assertEquals("Wrong code", state.error)
        verifyNoInteractions(telemetry)
        gateway.result = NetworkResult.Success(user); vm.verify("123456"); runCurrent()
        assertEquals(PhoneAuthState.Closed, vm.state.value)
    }
    @Test fun `automatic verification uses authenticated event without asking for OTP`() = withVm { vm, gateway, telemetry ->
        vm.open(); vm.sendCode(mock(Context::class.java), "5555550100"); runCurrent()
        gateway.events.emit(PhoneVerificationEvent.Authenticated(user)); runCurrent()
        assertEquals(PhoneAuthState.Closed, vm.state.value); verify(telemetry).trackUserLogin()
    }
    @Test fun `send failures return to number entry and duplicate sends are ignored`() = withVm { vm, gateway, telemetry ->
        vm.open(); vm.sendCode(mock(Context::class.java), "5555550100"); vm.sendCode(mock(Context::class.java), "5555550100"); runCurrent()
        assertEquals(1, gateway.numbers.size)
        gateway.events.emit(PhoneVerificationEvent.Failed("Quota error")); runCurrent()
        assertEquals(PhoneAuthState.NumberEntry("Quota error"), vm.state.value); verifyNoInteractions(telemetry)
    }
    @Test fun `dismiss ignores late callbacks and duplicate verify requests`() = withVm { vm, gateway, telemetry ->
        vm.open(); vm.sendCode(mock(Context::class.java), "5555550100"); runCurrent()
        gateway.events.emit(PhoneVerificationEvent.CodeSent("session")); runCurrent()
        gateway.gate = CompletableDeferred()
        vm.verify("123456"); vm.verify("123456"); runCurrent(); assertEquals(1, gateway.submissions.size)
        vm.dismiss(); gateway.gate!!.complete(Unit); runCurrent()
        gateway.events.emit(PhoneVerificationEvent.Authenticated(user)); runCurrent()
        assertEquals(PhoneAuthState.Closed, vm.state.value); verifyNoInteractions(telemetry)
    }
    @Test fun `invalid input never calls Firebase gateway`() = withVm { vm, gateway, _ ->
        vm.open(); vm.sendCode(mock(Context::class.java), "123"); runCurrent()
        assertTrue(gateway.numbers.isEmpty()); assertNotNull((vm.state.value as PhoneAuthState.NumberEntry).error)
    }
}
