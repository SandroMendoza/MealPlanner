package com.meal.planner.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.planner.components.IngredientNameField
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
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf(MeasurementUnit.PIECE) }

    val quantity = quantityText.toDoubleOrNull()
    val canConfirm = name.isNotBlank() && quantity != null && quantity > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(LocalStrings.current.shoppingAddItemButton) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                IngredientNameField(
                    value = name,
                    suggestions = suggestions,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text(LocalStrings.current.qty) },
                        modifier = Modifier.weight(1f)
                    )
                    UnitDropdownField(
                        value = unit,
                        onValueChange = { unit = it },
                        modifier = Modifier.weight(1f)
                    )
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
