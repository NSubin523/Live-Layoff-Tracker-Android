package com.example.tracklayoff.features.feed.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.example.tracklayoff.feature.feed.R
import com.example.tracklayoff.features.feed.ui.state.FavoriteCardUiState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.tracklayoff.core.designsystem.AppColors
import com.example.tracklayoff.core.designsystem.AppDimens
import com.example.tracklayoff.features.feed.ui.state.FeedCardUiModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LayoffCardItem(
    modifier: Modifier = Modifier,
    card: FeedCardUiModel,
    placeholderIcon: Painter,
    favorite: FavoriteCardUiState = FavoriteCardUiState(),
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(
            horizontal = AppDimens.HorizontalPaddingLayoffCard,
            vertical = AppDimens.VerticalPaddingLayoffCard
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.FeedCardElevation),
        shape = RoundedCornerShape(AppDimens.RoundedCornerShapeLayoffCard),
        border = BorderStroke(AppDimens.BorderStrokeLayoffCard, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            Modifier.padding(AppDimens.LayoffCardContentPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimens.FeedSectionSpacing)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.CardSpacing)) {
                AsyncImage(
                    model = card.logoUrl,
                    contentDescription = null,
                    placeholder = placeholderIcon, fallback = placeholderIcon, error = placeholderIcon,
                    modifier = Modifier.size(AppDimens.LayoffCardImageSize)
                        .clip(RoundedCornerShape(AppDimens.FeedLogoCornerRadius))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentScale = ContentScale.Fit
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingStandard)) {
                    Text(card.companyName, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingStandard)) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null,
                            modifier = Modifier.size(AppDimens.FeedMetadataIconSize), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(card.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                val heartColor by animateColorAsState(
                    if (favorite.isSaved) AppColors.Impact else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "Favorite color"
                )
                IconButton(onClick = onFavoriteClick, enabled = !favorite.isWriting,
                    modifier = Modifier.align(Alignment.Top).semantics {
                        toggleableState = if (favorite.isSaved) ToggleableState.On else ToggleableState.Off
                    }) {
                    Icon(if (favorite.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(if (favorite.isSaved) R.string.favorite_unsave else R.string.favorite_save, card.companyName),
                        tint = heartColor, modifier = Modifier.size(AppDimens.FavoriteIconSize))
                }
            }
            if (favorite.sync == com.example.tracklayoff.core.favorites.domain.FavoriteSyncState.Failed ||
                favorite.sync == com.example.tracklayoff.core.favorites.domain.FavoriteSyncState.AuthenticationRequired) {
                Text(stringResource(R.string.favorite_sync_failed), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error)
            }
            Text(card.impactLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                color = if (card.hasKnownImpact) AppColors.Impact else MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(AppDimens.FeedMetadataSpacing),
                verticalArrangement = Arrangement.spacedBy(AppDimens.FeedMetadataSpacing)) {
                Surface(shape = RoundedCornerShape(AppDimens.SpacingStandard), color = MaterialTheme.colorScheme.surfaceContainer) {
                    Text(card.industry, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = AppDimens.SpacingLarge, vertical = AppDimens.SpacingStandard))
                }
                Text(card.reportedDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                StatusPillComponent(status = card.status)
            }
        }
    }
}
