package com.meal.planner.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.displayName
import com.meal.planner.common.icon
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import kotlinx.datetime.LocalDate

@Composable
fun MealTypeDialog(
    recipeName: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    onSelect: (MealType) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(LocalStrings.current.recipePickerAddRecipeNameTo(recipeName), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = date.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = LocalStrings.current.recipePickerChange,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = spacing.xs))
                MealType.entries.forEach { mealType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mealType) }
                            .padding(vertical = spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.md)
                    ) {
                        Icon(
                            imageVector = mealType.icon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(mealType.displayName(), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
        }
    )

    if (showDatePicker) {
        MealDatePickerDialog(
            initialDate = date,
            onDateSelected = onDateChange,
            onDismiss = { showDatePicker = false }
        )
    }
}
