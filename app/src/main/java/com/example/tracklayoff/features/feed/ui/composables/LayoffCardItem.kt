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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
    company: Company,
    modifier: Modifier = Modifier
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
            val placeholderIcon = rememberVectorPainter(Icons.Outlined.AccountBox)
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
                StatusPillComponent(status = company.layoffStatus.toString())
                Spacer(modifier = Modifier.height(AppDimens.SpacingLarge))
                Text(
                    text = company.impactCount?.let { "$it affected" } ?: "Impact Unknown",
                    fontSize = AppDimens.CompanyLocationFontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (company.impactCount != null) AppColors.CompanyNameTextColor else AppColors.CompanyLocationTextColor
                )
            }
        }
    }
}