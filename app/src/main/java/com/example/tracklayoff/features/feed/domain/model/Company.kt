package com.example.tracklayoff.features.feed.domain.model

import java.time.Instant

enum class LayoffStatus {
    RUMORED, CONFIRMED, UNKNOWN
}

enum class TrendDirection {
    INCREASING, DECREASING, STABLE, UNKNOWN
}

data class Company (
    val id: String,
    val companyName: String,
    val impactCount: Int?,
    val layoffStatus: LayoffStatus,
    val industry: String,
    val location: String,
    val reportedAt: Instant,
    val logoUrl: String?,
    val trendDirection: TrendDirection
)