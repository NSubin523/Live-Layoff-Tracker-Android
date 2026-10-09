package com.example.tracklayoff.features.chat.ui.composables

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
internal fun Modifier.dismissKeyboardOnOutsideTap(textFieldBounds: () -> Rect?): Modifier {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val currentBounds by rememberUpdatedState(textFieldBounds)
    val coordinates = remember { KeyboardDismissCoordinates() }

    return onGloballyPositioned { coordinates.origin = it.positionInRoot() }
        .pointerInput(focusManager, keyboard) {
            awaitEachGesture {
                // Observe before child handlers, without consuming their clicks or text selection.
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                val bounds = currentBounds()
                if (up != null && bounds != null &&
                    !bounds.contains(down.position + coordinates.origin) && !bounds.contains(up.position + coordinates.origin)
                ) {
                    focusManager.clearFocus()
                    keyboard?.hide()
                }
            }
        }
}

// Geometry is read by pointer handlers, never by composition.
private class KeyboardDismissCoordinates {
    var origin: Offset = Offset.Zero
}
