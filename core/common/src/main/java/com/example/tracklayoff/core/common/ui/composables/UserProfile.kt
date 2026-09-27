package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.tracklayoff.core.common.ui.state.AuthenticationState
import com.example.tracklayoff.designsystems.AppColors
import com.example.tracklayoff.designsystems.AppDimens

@Composable
fun UserProfile(
    onProfileClick:() -> Unit,
    authenticationState: AuthenticationState
) {
    IconButton(
        onClick = onProfileClick
    ) {
        if(!authenticationState.isAuthenticated || authenticationState.photoUrl.isNullOrEmpty()) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Sign in Modal Sheet",
                tint = AppColors.PrimaryTextColor,
                modifier = Modifier.size(AppDimens.ProfileAvatarSize)
            )
        } else {
            AsyncImage(
                model = authenticationState.photoUrl,
                contentDescription = "User Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(AppDimens.ProfileAvatarSize)
                    .clip(CircleShape)
            )
        }
    }
}