package com.example.tracklayoff.features.feed.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tracklayoff.core.designsystem.AppColors
import com.example.tracklayoff.core.designsystem.AppDimens
import com.example.tracklayoff.features.feed.domain.model.LayoffStatus

@Composable
fun StatusPillComponent(status: LayoffStatus, modifier: Modifier = Modifier) {
    val foreground = when (status) {
        LayoffStatus.CONFIRMED -> AppColors.Confirmed
        LayoffStatus.RUMORED -> AppColors.Rumored
        LayoffStatus.UNKNOWN -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val background = when (status) {
        LayoffStatus.CONFIRMED -> AppColors.ConfirmedContainer
        LayoffStatus.RUMORED -> AppColors.RumoredContainer
        LayoffStatus.UNKNOWN -> MaterialTheme.colorScheme.surfaceContainer
    }
    Surface(modifier, shape = RoundedCornerShape(AppDimens.SpacingStandard), color = background, contentColor = foreground) {
        Row(Modifier.padding(horizontal = AppDimens.SpacingLarge, vertical = AppDimens.SpacingStandard),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingStandard)) {
            Icon(if (status == LayoffStatus.CONFIRMED) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                contentDescription = null, modifier = Modifier.size(AppDimens.FeedStatusIconSize))
            Text(status.name, style = MaterialTheme.typography.labelSmall)
        }
    }
}
