package com.meal.planner.shopping

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.meal.domain.model.ShoppingListItem

@Composable
fun RecordPriceDialog(
    item: ShoppingListItem,
    onConfirm: (price: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var priceText by remember(item.itemKey) {
        mutableStateOf(item.price?.toString() ?: "")
    }

    val price = priceText.toDoubleOrNull()
    val canConfirm = price != null && price >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How much did ${item.name} cost?") },
        text = {
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Price ($)") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { price?.let { onConfirm(it) } },
                enabled = canConfirm
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
