package com.example.tracklayoff.core.common.user.data

data class AppUser(
    val firebaseId: String,
    val name: String?,
    val photoUrl: String?,
    val email: String?
)
