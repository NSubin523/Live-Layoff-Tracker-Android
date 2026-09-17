package com.example.tracklayoff.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tracklayoff.R

@Immutable
sealed class BottomNavIconType {
    data class VectorType(val imageVector: ImageVector): BottomNavIconType()
    data class ImageType(@DrawableRes val id: Int): BottomNavIconType()
}

@Immutable
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: BottomNavIconType,
    val unselectedIcon: BottomNavIconType
) {

    object Feed : BottomNavItem(
        route = "feed",
        title = "Feed",
        selectedIcon = BottomNavIconType.VectorType(Icons.Filled.Home),
        unselectedIcon = BottomNavIconType.VectorType(Icons.Outlined.Home)
    )

    object Tracker : BottomNavItem(
        route = "tracker",
        title = "Tracker",
        selectedIcon = BottomNavIconType.ImageType(R.drawable.ic_analytics_filled),
        unselectedIcon = BottomNavIconType.ImageType(R.drawable.ic_analytics)
    )

}