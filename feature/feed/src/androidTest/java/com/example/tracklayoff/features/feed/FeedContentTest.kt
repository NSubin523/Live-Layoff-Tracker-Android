package com.example.tracklayoff.features.feed

import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tracklayoff.features.feed.domain.model.*
import com.example.tracklayoff.features.feed.ui.composables.FeedContent
import com.example.tracklayoff.features.feed.ui.state.FeedUiState
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
class FeedContentTest {
    @get:Rule val compose = createComposeRule()
    private val company = Company("1", "Acme", 100, LayoffStatus.CONFIRMED, "Tech", "New York", Instant.EPOCH, null, TrendDirection.STABLE)
    @Test fun emptySuccessShowsPlaceholder() {
        compose.setContent { MaterialTheme { FeedContent(FeedUiState.Success(persistentListOf()), Modifier, false, rememberPullToRefreshState(), {}) } }
        compose.onNodeWithText("No companies found").assertIsDisplayed()
    }
    @Test fun failureShowsErrorAndLoadedContentReplacesIt() {
        val state = mutableStateOf<FeedUiState>(FeedUiState.Error("Offline"))
        compose.setContent { MaterialTheme { FeedContent(state.value, Modifier, false, rememberPullToRefreshState(), {}) } }
        compose.onNodeWithText("Connection Error").assertIsDisplayed()
        compose.runOnIdle { state.value = FeedUiState.Success(persistentListOf(company)) }
        compose.onNodeWithText("Connection Error").assertDoesNotExist()
        compose.onNodeWithText("Acme").assertIsDisplayed()
        compose.onNodeWithText("100 affected").assertIsDisplayed()
        compose.onNodeWithText("CONFIRMED").assertIsDisplayed()
    }
    @Test fun unknownImpactUsesPlaceholderAndPullRefreshInvokesCallback() {
        var refreshes = 0
        compose.setContent { MaterialTheme { FeedContent(FeedUiState.Success(persistentListOf(company.copy(impactCount = null))), Modifier.testTag("feed"), false, rememberPullToRefreshState(), { refreshes++ }) } }
        compose.onNodeWithText("Impact Unknown").assertIsDisplayed()
        compose.onNodeWithTag("feed").performTouchInput { swipeDown(startY = height * .1f, endY = height * .9f, durationMillis = 600) }
        compose.waitForIdle()
        assertEquals(1, refreshes)
    }
    @Test fun loadingDoesNotShowEmptyPlaceholder() {
        compose.setContent { MaterialTheme { FeedContent(FeedUiState.Loading, Modifier, false, rememberPullToRefreshState(), {}) } }
        compose.onNodeWithText("No companies found").assertDoesNotExist()
        compose.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }
}
