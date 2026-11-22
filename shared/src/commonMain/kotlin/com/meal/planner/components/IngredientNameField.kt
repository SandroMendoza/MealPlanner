package com.meal.planner.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens

/**
 * Ingredient name input that suggests matching ingredient names once three or more characters
 * are typed. Suggestions show in a dropdown under the field. On a short screen the keyboard leaves
 * no room below the field, so they show as a row of chips just above it instead.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientNameField(
    value: String,
    suggestions: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSuggestionsVisibleChange: (Boolean) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    val trimmedValue = value.trim()
    val filtered = remember(suggestions, trimmedValue) {
        if (trimmedValue.length >= 3) {
            suggestions.filter { it.contains(trimmedValue, ignoreCase = true) }
        } else {
            emptyList()
        }
    }
    val showSuggestions = expanded && filtered.isNotEmpty()
    val compactHeight = LocalCompactHeight.current

    LaunchedEffect(showSuggestions) { onSuggestionsVisibleChange(showSuggestions) }

    val onTextChange: (String) -> Unit = {
        onValueChange(it)
        expanded = true
    }
    val keyboardActions = KeyboardActions(onDone = {
        expanded = false
        focusManager.clearFocus()
    })
    val pick: (String) -> Unit = { suggestion ->
        onValueChange(suggestion)
        expanded = false
    }

    if (compactHeight) {
        val spacing = MealPlannerTokens.spacing
        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        LaunchedEffect(showSuggestions) {
            if (showSuggestions) {
                // Wait one frame so the chip row has been laid out above the field.
                withFrameNanos { }
                bringIntoViewRequester.bringIntoView()
            }
        }

        Column(modifier = modifier.bringIntoViewRequester(bringIntoViewRequester)) {
            if (showSuggestions) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    modifier = Modifier.fillMaxWidth().padding(bottom = spacing.xs)
                ) {
                    items(filtered) { suggestion ->
                        SuggestionChip(onClick = { pick(suggestion) }, label = { Text(suggestion) })
                    }
                }
            }
            OutlinedTextField(
                value = value,
                onValueChange = onTextChange,
                singleLine = true,
                label = { Text(LocalStrings.current.name) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = keyboardActions,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        ExposedDropdownMenuBox(expanded = showSuggestions, onExpandedChange = { expanded = it }, modifier = modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = onTextChange,
                singleLine = true,
                label = { Text(LocalStrings.current.name) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSuggestions) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = keyboardActions,
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = showSuggestions, onDismissRequest = { expanded = false }) {
                filtered.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = { pick(suggestion) }
                    )
                }
            }
        }
    }
}
