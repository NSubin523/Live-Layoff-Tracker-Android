package com.example.tracklayoff.core.database.favorites

import androidx.room.Entity

@Entity(tableName = "user_favorites", primaryKeys = ["userId", "companyId"])
data class UserFavoriteEntity(val userId: String, val companyId: String)

/** An unsave must remain here even after its membership row has been deleted. */
@Entity(tableName = "favorite_outbox", primaryKeys = ["userId", "companyId"])
data class FavoriteOutboxEntity(
    val userId: String,
    val companyId: String,
    val desiredSaved: Boolean,
    val revision: String,
    val status: String = "Pending"
)
