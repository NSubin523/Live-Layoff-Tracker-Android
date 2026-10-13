package com.example.tracklayoff.core.favorites.data

import com.example.tracklayoff.features.feed.data.local.entity.CompanyEntity
import java.time.Instant

internal fun FavoriteCompanyDto.toEntity(): CompanyEntity? {
    val companyId = id?.takeIf { it.isNotBlank() } ?: return null
    val name = companyName?.takeIf { it.isNotBlank() } ?: return null
    val reported = runCatching { Instant.parse(reportedAt).toEpochMilli() }.getOrNull() ?: return null
    return CompanyEntity(companyId, name, impactCount, layoffStatus ?: "UNKNOWN", industry.orEmpty(), location.orEmpty(), reported, logoUrl, trendDirection ?: "UNKNOWN")
}
