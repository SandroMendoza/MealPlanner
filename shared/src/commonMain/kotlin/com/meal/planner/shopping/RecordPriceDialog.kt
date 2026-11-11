package com.meal.planner.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.DialogProperties
import com.meal.domain.model.ShoppingListItem
import com.meal.planner.components.KeyboardDismissButton
import com.meal.planner.strings.LocalStrings

@Composable
fun RecordPriceDialog(
    item: ShoppingListItem,
    currencySymbol: String,
    onConfirm: (price: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var priceText by remember(item.itemKey) {
        mutableStateOf(item.price?.toString() ?: "")
    }

    val price = priceText.toDoubleOrNull()
    val canConfirm = price != null && price >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(decorFitsSystemWindows = false),
        title = { Text(LocalStrings.current.recordPriceTitle(item.name)) },
        text = {
            Column {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(LocalStrings.current.recordPricePriceLabel(currencySymbol)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    KeyboardDismissButton()
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { price?.let { onConfirm(it) } },
                enabled = canConfirm
            ) {
                Text(LocalStrings.current.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
        }
    )
}
