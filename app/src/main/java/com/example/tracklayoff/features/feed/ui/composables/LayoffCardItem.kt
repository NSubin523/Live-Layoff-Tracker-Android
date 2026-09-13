package com.example.tracklayoff.features.feed.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tracklayoff.designsystems.AppColors
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.designsystems.White
import com.example.tracklayoff.features.feed.domain.model.Company

@Composable
fun LayoffCardItem(
    modifier: Modifier = Modifier,
    company: Company,
    placeholderIcon: Painter
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .padding(
                horizontal = AppDimens.HorizontalPaddingLayoffCard,
                vertical = AppDimens.VerticalPaddingLayoffCard
            ),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(AppDimens.RoundedCornerShapeLayoffCard),
        border = BorderStroke(1.dp, White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.HorizontalPaddingLayoffCard),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = company.logoUrl,
                contentDescription = "${company.companyName} Logo",
                placeholder = placeholderIcon,
                fallback = placeholderIcon,
                error = placeholderIcon,
                modifier = Modifier
                    .size(AppDimens.LayoffCardImageSize)
                    .clip(RoundedCornerShape(AppDimens.SpacingStandard)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(AppDimens.SpacingXxl))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = company.companyName,
                    fontSize = AppDimens.CompanyTitleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.CompanyNameTextColor
                )
                Spacer(modifier = Modifier.height(AppDimens.SpacingSmall))
                Text(
                    text = company.location,
                    fontSize = AppDimens.CompanyLocationFontSize,
                    color = AppColors.CompanyLocationTextColor
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val status = remember(company.layoffStatus) { company.layoffStatus.toString() }
                StatusPillComponent(status = status)
                Spacer(modifier = Modifier.height(AppDimens.SpacingLarge))
                val impactText = remember(company.impactCount) {
                    company.impactCount?.let { "$it affected" } ?: "Impact Unknown"
                }
                Text(
                    text = impactText,
                    fontSize = AppDimens.CompanyLocationFontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (company.impactCount != null) AppColors.CompanyNameTextColor else AppColors.CompanyLocationTextColor
                )
            }
        }
    }
}