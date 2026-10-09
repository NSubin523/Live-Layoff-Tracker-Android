package com.example.tracklayoff

import android.Manifest
import androidx.test.rule.GrantPermissionRule
import androidx.test.filters.SdkSuppress

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = 33)
class LandingScreenTest {
    @get:Rule(order = 0) val notificationPermission = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()
    @Before fun guestSession() {
        compose.runOnUiThread { FirebaseAuth.getInstance().signOut() }
    }
    @Test fun guestChatShowsSignInAndBothProviders() {
        compose.onNodeWithContentDescription("Chat AI").performClick()
        compose.onNodeWithText("Sign in to ask about layoffs").assertIsDisplayed()
        compose.onNodeWithText("Sign in").performClick()
        compose.onNodeWithText("Sign in with Google").assertIsDisplayed()
        compose.onNodeWithText("Sign in with Phone").performClick()
        compose.onNodeWithText("Enter phone number").assertIsDisplayed()
        compose.onNodeWithText("Enter verification code").assertDoesNotExist()
    }
    @Test fun selectedChatTabSurvivesRecreation() {
        compose.onNodeWithContentDescription("Chat AI").performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Sign in to ask about layoffs").assertIsDisplayed()
        compose.onNodeWithContentDescription("Chat AI").assertIsSelected()
    }
    @Test fun guestCanNavigateBetweenFeedTrackerAndChat() {
        compose.onNodeWithContentDescription("Tracker").performClick()
        compose.onNodeWithText("Sign in").assertIsDisplayed()
        compose.onNodeWithContentDescription("Chat AI").performClick()
        compose.onNodeWithText("Sign in to ask about layoffs").assertIsDisplayed()
        compose.onNodeWithContentDescription("Feed").performClick()
        compose.onNodeWithText("Sign in to ask about layoffs").assertDoesNotExist()
    }
}
