package com.meal.planner.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.meal.domain.model.ShoppingGroup
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens

@Composable
fun ManageGroupsDialog(
    groups: List<ShoppingGroup>,
    onRename: (id: String, name: String) -> Unit,
    onRequestDelete: (ShoppingGroup) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(LocalStrings.current.shoppingManageGroupsTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                groups.forEach { group ->
                    GroupManagementRow(group = group, onRename = onRename, onRequestDelete = onRequestDelete)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.ok) }
        }
    )
}

@Composable
private fun GroupManagementRow(
    group: ShoppingGroup,
    onRename: (id: String, name: String) -> Unit,
    onRequestDelete: (ShoppingGroup) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val focusManager = LocalFocusManager.current
    var name by remember(group.id) { mutableStateOf(group.name) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (name.isNotBlank()) onRename(group.id, name)
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onRequestDelete(group) }) {
            Icon(Icons.Filled.Delete, contentDescription = LocalStrings.current.remove)
        }
    }
}
