package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.tracklayoff.core.common.ui.state.AuthenticationState
import com.example.tracklayoff.designsystems.AppColors
import com.example.tracklayoff.designsystems.AppDimens
import com.example.tracklayoff.designsystems.AppTopBarText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    authenticationState: AuthenticationState,
    onProfileClick: () -> Unit
){
    Column {
        TopAppBar(
            title = {
                Text(
                    text = AppTopBarText,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryTextColor
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppColors.PrimaryAppTopBarBgColor,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            actions = {
                UserProfile(
                    onProfileClick = onProfileClick,
                    authenticationState = authenticationState
                )
            }
        )

        HorizontalDivider(
            thickness = AppDimens.TopAppBarDividerSize,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}