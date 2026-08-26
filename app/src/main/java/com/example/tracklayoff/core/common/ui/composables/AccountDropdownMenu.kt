package com.example.tracklayoff.core.common.ui.composables

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tracklayoff.designsystems.AppColors
import com.example.tracklayoff.designsystems.AppDimens

@Composable
fun AppAccountDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onAccountClick: () -> Unit,
    onAboutClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.DropdownMenuRoundedCorners),
        containerColor = AppColors.PrimaryContainerColorDropdown,
        tonalElevation = AppDimens.DropdownMenuElevation
    ) {
        DropdownMenuItem(
            text = { Text("Account") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onAccountClick()
            }
        )
        DropdownMenuItem(
            text = { Text("About") },
            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onAboutClick()
            }
        )
        DropdownMenuItem(
            text = { Text("Sign Out") },
            leadingIcon = { Icon(Icons.Outlined.ExitToApp, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onSignOutClick()
            }
        )
    }
}