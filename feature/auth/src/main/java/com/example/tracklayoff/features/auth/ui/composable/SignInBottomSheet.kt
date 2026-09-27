package com.example.tracklayoff.features.auth.ui.composable

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tracklayoff.core.common.domain.AuthProviderClientType
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInBottomSheet(
    onDismissRequest: () -> Unit,
    onSignInClick: (AuthProviderClientType) -> Unit,
    modifier: Modifier = Modifier
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        modifier = modifier
    ) {
        SignInBottomSheetContent(
            onDismissRequest = onDismissRequest,
            onSignInClick = onSignInClick
        )
    }
}