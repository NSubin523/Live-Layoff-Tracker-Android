package com.example.tracklayoff.features.feed.domain.mapper

import com.example.tracklayoff.features.feed.data.dto.LayoffResponseDto
import com.example.tracklayoff.features.feed.data.local.entity.CompanyEntity
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

fun LayoffResponseDto.toEntity(): CompanyEntity {
    val epochMillis = try {
        Instant.parse(reportedAt).toEpochMilli()
    } catch (e: DateTimeParseException) {
        System.currentTimeMillis()
    }

    return CompanyEntity(
        id = companyId,
        companyName = companyName,
        impactCount = impactCount,
        layoffStatus = this.layoffStatus,
        industry = industry,
        location = location,
        reportedAt = epochMillis,
        logoUrl = logoUrl,
        trendDirection = trendDirection.uppercase(),
    )
}

fun CompanyEntity.toDomain(): Company {
    val statusEnum = try {
        LayoffStatus.valueOf(layoffStatus)
    } catch (e: IllegalArgumentException) {
        LayoffStatus.UNKNOWN
    }

    val trendEnum = try {
        TrendDirection.valueOf(trendDirection)
    } catch (e: IllegalArgumentException) {
        TrendDirection.UNKNOWN
    }

    return Company(
        id = id,
        companyName = companyName,
        impactCount = impactCount,
        layoffStatus = statusEnum,
        industry = industry,
        location = location,
        reportedAt = Instant.ofEpochMilli(reportedAt),
        logoUrl = logoUrl,
        trendDirection = trendEnum
    )
}