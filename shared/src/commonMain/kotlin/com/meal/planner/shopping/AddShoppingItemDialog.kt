package com.meal.planner.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.planner.common.filterDecimalInput
import com.meal.planner.components.IngredientNameField
import com.meal.planner.components.KeyboardDismissButton
import com.meal.planner.components.UnitDropdownField
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens

@Composable
fun AddShoppingItemDialog(
    suggestions: List<String>,
    onConfirm: (name: String, quantity: Double, unit: MeasurementUnit) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf(MeasurementUnit.PIECE) }
    var suggestionsVisible by remember { mutableStateOf(false) }

    val quantity = quantityText.toDoubleOrNull()
    val canConfirm = name.isNotBlank() && quantity != null && quantity > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(LocalStrings.current.shoppingAddItemButton) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                IngredientNameField(
                    value = name,
                    suggestions = suggestions,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    onSuggestionsVisibleChange = { suggestionsVisible = it }
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = filterDecimalInput(it) },
                        label = { Text(LocalStrings.current.qty) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.weight(1f)
                    )
                    UnitDropdownField(
                        value = unit,
                        onValueChange = { unit = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (!suggestionsVisible) {
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        KeyboardDismissButton()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { quantity?.let { onConfirm(name, it, unit) } },
                enabled = canConfirm
            ) {
                Text(LocalStrings.current.add)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
        }
    )
}
