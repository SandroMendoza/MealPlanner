package com.meal.planner.components

import com.meal.planner.common.LocalCompactHeight
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import com.meal.planner.strings.LocalStrings
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(initialHour = initialTime.hour, initialMinute = initialTime.minute)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(LocalTime(state.hour, state.minute))
                onDismiss()
            }) {
                Text(LocalStrings.current.ok)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
        },
        text = {
            TimePicker(
                state = state,
                layoutType = if (LocalCompactHeight.current) TimePickerLayoutType.Horizontal else TimePickerDefaults.layoutType()
            )
        }
    )
}
