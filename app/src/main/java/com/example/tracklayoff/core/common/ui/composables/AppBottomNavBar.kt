package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.navigation.BottomNavIconType
import com.example.tracklayoff.navigation.BottomNavItem

@Composable
fun AppBottomNavBar(
    modifier: Modifier = Modifier,
    selectedTab: BottomNavItem,
    onTabSelected: (BottomNavItem) -> Unit,
    items: List<BottomNavItem> = listOf(BottomNavItem.Feed, BottomNavItem.Tracker),
) {
    NavigationBar(
        modifier = modifier.height(AppDimens.AppNavbarHeight)
    ) {
        items.forEach { item ->
            val isSelected = selectedTab == item
            val iconType = if (isSelected) item.selectedIcon else item.unselectedIcon
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item) },
                icon = {
                    when(iconType) {
                        is BottomNavIconType.ImageType -> {
                            Icon(
                                painter = painterResource(iconType.id),
                                contentDescription = item.title
                            )
                        }
                        is BottomNavIconType.VectorType -> {
                            Icon(
                                imageVector = iconType.imageVector,
                                contentDescription = item.title
                            )
                        }
                    }
                }
            )
        }
    }
}