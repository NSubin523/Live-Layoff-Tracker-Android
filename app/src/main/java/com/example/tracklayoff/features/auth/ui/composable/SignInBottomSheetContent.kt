package com.example.tracklayoff.features.auth.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.tracklayoff.core.common.domain.AuthProviderClientType
import com.example.tracklayoff.designsystems.AppDimens

@Composable
fun SignInBottomSheetContent(
    onDismissRequest: () -> Unit,
    onSignInClick: (AuthProviderClientType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = AppDimens.AuthBottomSheetHorizontalPadding,
                end = AppDimens.AuthBottomSheetHorizontalPadding,
                bottom = AppDimens.AuthBottomSheetHorizontalPadding,
                top = AppDimens.AuthBottomSheetVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sign in to Live Layoff Tracker",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.AuthBottomSheetSpacingTitle)
            )

            IconButton(
                onClick = onDismissRequest,
                modifier = modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close sign in sheet"
                )
            }
        }

        Spacer(modifier = modifier.height(AppDimens.SpacingLarge))

        Text(
            text = "Access personalized layoff alerts, and set preferences",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = modifier.height(AppDimens.AuthBottomSheetBottomSpacing))

        Button(
            onClick = { onSignInClick(AuthProviderClientType.GOOGLE) },
            modifier = modifier
                .fillMaxWidth()
                .height(AppDimens.GoogleSignInBtnHeight)
        ) {
            Text(text = "Sign in with Google")
        }

        Spacer(modifier = modifier.height(AppDimens.AuthBottomSheetBottomSpacing))

        Button(
            onClick = { onSignInClick(AuthProviderClientType.PHONE) } ,
            modifier = modifier
                .fillMaxWidth()
                .height(AppDimens.GoogleSignInBtnHeight)
        ) {
            Text(text = "Sign in with Phone")
        }
    }
}