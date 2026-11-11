package com.meal.planner.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.meal.planner.strings.LocalStrings

/**
 * Floats a "hide keyboard" button just above the on-screen keyboard, anchored via
 * [Modifier.align] + [Modifier.imePadding] inside the caller's container. That
 * container must span the real window height (e.g. `Modifier.fillMaxSize()`) for
 * the alignment to land correctly — a container only as tall as its own content
 * (shorter than the keyboard) pushes the button out past its own top edge instead
 * of down near the keyboard.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeyboardDismissButton(modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    AnimatedVisibility(
        visible = WindowInsets.isImeVisible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        SmallFloatingActionButton(
            onClick = {
                focusManager.clearFocus(force = true)
                keyboardController?.hide()
            },
            modifier = Modifier.imePadding().padding(8.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = LocalStrings.current.hideKeyboard)
        }
    }
}
