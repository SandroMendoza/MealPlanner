package com.meal.planner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeActionsRow(
    onSwipeRightToAssignGroup: () -> Unit,
    onSwipeLeftToDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> onSwipeRightToAssignGroup()
                SwipeToDismissBoxValue.EndToStart -> onSwipeLeftToDelete()
                SwipeToDismissBoxValue.Settled -> Unit
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
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
                        Icons.Filled.Label,
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
