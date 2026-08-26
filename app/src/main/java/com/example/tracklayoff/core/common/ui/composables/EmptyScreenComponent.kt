package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.tracklayoff.designsystems.AppColors
import com.example.tracklayoff.designsystems.AppDimens

@Composable
fun EmptyScreen(
    title: String,
    subtitle: String,
    hasButton: Boolean = false,
    onClickAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppDimens.EmptyScreenBoxSpacing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = AppDimens.CompanyTitleFontSize,
                fontWeight = FontWeight.Bold,
                color = AppColors.CompanyNameTextColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = modifier.height(AppDimens.SpacingLarge))
            Text(
                text = subtitle,
                fontSize = AppDimens.CompanyLocationFontSize,
                color = AppColors.CompanyLocationTextColor,
                textAlign = TextAlign.Center
            )
            if(hasButton) {
                Spacer(modifier.height(AppDimens.SpacingXxl))
                Button(
                    onClick = onClickAction,
                    modifier = modifier
                        .width(AppDimens.ButtonWidth)
                        .height(AppDimens.GoogleSignInBtnHeight)
                ) {
                    Text(text = "Sign in")
                }
            }
        }
    }
}