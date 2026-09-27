package com.example.tracklayoff.features.feed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("companies")
data class CompanyEntity(

    @PrimaryKey
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo("company_name")
    val companyName: String,

    @ColumnInfo(name = "impact_count")
    val impactCount: Int?,

    @ColumnInfo(name = "layoff_status")
    val layoffStatus: String,

    @ColumnInfo(name = "industry")
    val industry: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "reported_at")
    val reportedAt: Long,

    @ColumnInfo(name = "logo_url")
    val logoUrl: String?,

    @ColumnInfo(name = "trend_direction")
    val trendDirection: String
)
