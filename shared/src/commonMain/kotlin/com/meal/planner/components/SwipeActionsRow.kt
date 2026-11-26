package com.meal.planner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeActionsRow(
    onSwipeLeftToDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onSwipeRightToAssignGroup: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val dismissState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = onSwipeRightToAssignGroup != null,
        gesturesEnabled = enabled,
        onDismiss = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> onSwipeRightToAssignGroup?.invoke()
                SwipeToDismissBoxValue.EndToStart -> onSwipeLeftToDelete()
                SwipeToDismissBoxValue.Settled -> Unit
            }
            // A swipe triggers its action but never dismisses the row: the caller decides
            // whether the row goes away, so the row always returns to rest.
            scope.launch { dismissState.reset() }
        },
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = spacing.lg),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Label,
                        contentDescription = LocalStrings.current.shoppingAssignGroupCd,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                SwipeToDismissBoxValue.EndToStart -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = spacing.lg),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = LocalStrings.current.remove,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                SwipeToDismissBoxValue.Settled -> Unit
            }
        },
        content = { content() }
    )
}
