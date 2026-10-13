package com.example.tracklayoff.core.favorites.data

data class SetFavoriteRequestDto(val saved: Boolean)
data class FavoriteResponseDto(val companyId: String, val saved: Boolean)
data class FavoriteCompanyDto(
    val id: String?, val companyName: String?, val impactCount: Int?, val layoffStatus: String?,
    val industry: String?, val location: String?, val reportedAt: String?, val logoUrl: String?, val trendDirection: String?
)
