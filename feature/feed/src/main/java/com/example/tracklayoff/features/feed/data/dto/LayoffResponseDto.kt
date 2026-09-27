package com.example.tracklayoff.features.feed.data.dto

import com.google.gson.annotations.SerializedName

data class LayoffResponseDto(
    @SerializedName("id")
    val companyId: String,

    @SerializedName("company_name")
    val companyName: String,

    @SerializedName("impact_count")
    val impactCount: Int?,

    @SerializedName("status")
    val layoffStatus: String,

    @SerializedName("industry")
    val industry: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("reported_at")
    val reportedAt: String,

    @SerializedName("logo_url")
    val logoUrl: String?,

    @SerializedName("trend_direction")
    val trendDirection: String
)
