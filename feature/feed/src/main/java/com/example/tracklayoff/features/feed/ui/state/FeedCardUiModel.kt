package com.example.tracklayoff.features.feed.ui.state

import androidx.compose.runtime.Immutable
import com.example.tracklayoff.features.feed.domain.model.Company
import com.example.tracklayoff.features.feed.domain.model.LayoffStatus
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Immutable
data class FeedCardUiModel(
    val id: String,
    val companyName: String,
    val logoUrl: String?,
    val location: String,
    val industry: String,
    val reportedDate: String,
    val impactLabel: String,
    val hasKnownImpact: Boolean,
    val status: LayoffStatus
)

/** Format display data when the state is created, outside composition. */
internal fun Company.toCardUiModel(): FeedCardUiModel = FeedCardUiModel(
    id = id, companyName = companyName, logoUrl = logoUrl, location = location,
    industry = industry,
    reportedDate = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
        .withZone(ZoneId.systemDefault()).format(reportedAt),
    impactLabel = impactCount?.let { "${NumberFormat.getIntegerInstance().format(it)} affected" } ?: "Impact Unknown",
    hasKnownImpact = impactCount != null,
    status = layoffStatus
)
