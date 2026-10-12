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
import com.example.tracklayoff.core.designsystem.AppDimens
import com.example.tracklayoff.core.designsystem.AppTopBarText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    authenticationState: AuthenticationState,
    onProfileClick: () -> Unit,
    title: String = AppTopBarText
){
    Column {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
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