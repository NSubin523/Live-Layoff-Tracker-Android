package com.example.tracklayoff.features.feed.domain.mapper

import com.example.tracklayoff.features.feed.data.dto.LayoffResponseDto
import com.example.tracklayoff.features.feed.domain.model.Company
import com.example.tracklayoff.features.feed.domain.model.LayoffStatus
import com.example.tracklayoff.features.feed.domain.model.TrendDirection
import java.time.Instant
import java.time.format.DateTimeParseException

fun LayoffResponseDto.toDomain(): Company {
    val mappedStatus = when(this.layoffStatus) {
        "Confirmed" -> LayoffStatus.CONFIRMED
        "Rumored"   -> LayoffStatus.RUMORED
        else        -> LayoffStatus.UNKNOWN
    }

    val mappedTrend = when(this.trendDirection) {
        "increasing" -> TrendDirection.INCREASING
        "decreasing" -> TrendDirection.DECREASING
        "stable"     -> TrendDirection.STABLE
        else         -> TrendDirection.UNKNOWN
    }

    val parsedInstant = try {
        Instant.parse(this.reportedAt)
    } catch (_: DateTimeParseException){
        Instant.now()
    }

    return Company(
        id = this.companyId,
        companyName = this.companyName,
        impactCount = this.impactCount,
        layoffStatus = mappedStatus,
        industry = this.industry,
        location = this.location,
        reportedAt = parsedInstant,
        logoUrl = this.logoUrl,
        trendDirection = mappedTrend
    )
}