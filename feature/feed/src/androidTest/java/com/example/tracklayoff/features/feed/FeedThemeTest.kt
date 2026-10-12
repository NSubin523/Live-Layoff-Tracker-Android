package com.example.tracklayoff.features.feed

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tracklayoff.core.designsystem.TrackLayoffTheme
import com.example.tracklayoff.features.feed.domain.model.*
import com.example.tracklayoff.features.feed.ui.composables.FeedContent
import com.example.tracklayoff.features.feed.ui.state.FeedUiState
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.io.File
import android.graphics.Bitmap

@OptIn(ExperimentalMaterial3Api::class)
class FeedThemeTest {
    @get:Rule val compose = createComposeRule()
    private val state = FeedUiState.Success(persistentListOf(
        Company("1", "TechCorp", 1200, LayoffStatus.CONFIRMED, "Software", "San Francisco, CA", Instant.parse("2026-10-12T12:00:00Z"), null, TrendDirection.INCREASING),
        Company("2", "FinFlow", 450, LayoffStatus.RUMORED, "Finance", "New York, NY", Instant.parse("2026-10-11T12:00:00Z"), null, TrendDirection.STABLE),
        Company("3", "Global Logistics", null, LayoffStatus.UNKNOWN, "Retail", "Chicago, IL", Instant.parse("2026-10-10T12:00:00Z"), null, TrendDirection.UNKNOWN)
    ))
    @Test fun lightThemeKeepsFeedReadable() = showFeed(false, 1f, "feed-light")
    @Test fun darkThemeKeepsFeedReadable() = showFeed(true, 1f, "feed-dark")
    @Test fun largeTextKeepsCompanyAndImpactVisible() = showFeed(true, 1.6f, "feed-large-text")

    private fun showFeed(dark: Boolean, fontScale: Float, name: String) {
        compose.setContent {
            TrackLayoffTheme(darkTheme = dark) {
                CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        FeedContent(state, Modifier.fillMaxSize(), false, rememberPullToRefreshState(), {})
                    }
                }
            }
        }
        compose.onNodeWithText("TechCorp").assertIsDisplayed()
        compose.onNodeWithText("1,200 affected").assertIsDisplayed()
        compose.onNodeWithText("CONFIRMED").assertIsDisplayed()
        if (fontScale == 1f) {
            compose.onNodeWithText("RUMORED").assertIsDisplayed()
            compose.onNodeWithText("UNKNOWN").assertIsDisplayed()
        }
        // Optional screenshots for manual visual review; normal runs need no output directory.
        InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let { directory ->
            val output = File(directory).apply { mkdirs() }
            File(output, "$name.png").outputStream().use {
                compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }
}
