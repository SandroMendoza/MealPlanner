package com.meal.planner.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.planner.common.displayName
import com.meal.planner.strings.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdownField(
    value: MeasurementUnit,
    onValueChange: (MeasurementUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = value.displayName(),
            onValueChange = {},
            readOnly = true,
            label = { Text(LocalStrings.current.unit) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MeasurementUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.displayName()) },
                    onClick = {
                        onValueChange(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
