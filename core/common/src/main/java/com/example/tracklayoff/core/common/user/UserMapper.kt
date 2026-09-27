package com.example.tracklayoff.core.common.user

import com.example.tracklayoff.core.common.user.data.AppUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(): AppUser {
    return AppUser(
        firebaseId = uid,
        name = displayName,
        photoUrl = photoUrl?.toString(),
        email = email
    )
}
