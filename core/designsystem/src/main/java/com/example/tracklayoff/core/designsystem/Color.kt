package com.example.tracklayoff.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Semantic accents for feed data, separate from navigation and interactive colors. */
object AppColors {
    val Impact: Color
        @Composable get() = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFFFFB4AB) else Color(0xFFB3261E)
    val Confirmed: Color
        @Composable get() = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFF9ED8B1) else Color(0xFF23643D)
    val ConfirmedContainer: Color
        @Composable get() = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFF203D2B) else Color(0xFFE8F4EB)
    val Rumored: Color
        @Composable get() = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFFF3CD83) else Color(0xFF74520D)
    val RumoredContainer: Color
        @Composable get() = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFF43351D) else Color(0xFFFFF3DA)
}
