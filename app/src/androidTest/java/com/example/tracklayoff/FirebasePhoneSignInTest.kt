package com.example.tracklayoff

import android.Manifest
import androidx.test.rule.GrantPermissionRule
import androidx.test.filters.SdkSuppress

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import org.junit.Rule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tracklayoff.features.auth.client.PhoneSignInClient
import com.example.tracklayoff.features.auth.domain.PhoneVerificationEvent
import com.example.tracklayoff.core.network.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test

/** Opt-in integration check: pass configured fictional Firebase phone/code as instrumentation args. */
@SdkSuppress(minSdkVersion = 33)
class FirebasePhoneSignInTest {
    @get:Rule(order = 0) val notificationPermission = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
    // Install Compose test hooks before MainActivity, while leaving activity ownership to this test.
    @get:Rule(order = 1) val compose = createEmptyComposeRule()
    @Test fun configuredFirebaseNumberAuthenticates() = runBlocking {
        val args = InstrumentationRegistry.getArguments()
        val number = args.getString("testPhone")
        val code = args.getString("testCode")
        assumeTrue("Pass testPhone and testCode to run Firebase integration", number != null && code != null)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val activity = instrumentation.startActivitySync(Intent(instrumentation.targetContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        val auth = FirebaseAuth.getInstance()
        // Official Firebase test-number mode; this setting never exists in production code.
        auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
        val client = PhoneSignInClient(auth)
        try {
            val event = withTimeout(90_000) { client.requestCode(activity, number!!).first() }
            when (event) {
                is PhoneVerificationEvent.CodeSent -> {
                    val result = client.verifyCode(event.verificationId, code!!)
                    assertTrue("Firebase error: ${(result as? NetworkResult.Error)?.message}", result is NetworkResult.Success)
                }
                is PhoneVerificationEvent.Authenticated -> assertTrue(event.user.firebaseId.isNotBlank())
                is PhoneVerificationEvent.Failed -> fail(event.message)
            }
            assertNotNull(auth.currentUser)
        } finally {
            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(false)
            auth.signOut()
            instrumentation.runOnMainSync { activity.finish() }
        }
    }
}
