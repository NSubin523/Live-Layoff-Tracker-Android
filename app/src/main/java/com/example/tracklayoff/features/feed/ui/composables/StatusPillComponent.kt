package com.example.tracklayoff.features.feed.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.tracklayoff.designsystems.AppColors
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.designsystems.White

@Composable
fun StatusPillComponent(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when(status.uppercase()) {
        "CONFIRMED" -> White to AppColors.StatusPillConfirmedColor
        else -> White to AppColors.StatusPillRumoredColor
    }

    Box(
        modifier = modifier.background(
            color = backgroundColor,
            shape = RoundedCornerShape(AppDimens.SpacingStandard)
        ).padding(
            horizontal = AppDimens.SpacingLarge,
            vertical = AppDimens.SpacingStandard
        )
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = AppDimens.StandardFontSize,
            fontWeight = FontWeight.Bold
        )
    }
}