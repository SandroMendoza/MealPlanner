package com.meal.planner.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.meal.domain.model.IngredientUnits
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.model.ingredientDisplayName
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.common.displayName
import com.meal.planner.common.filterDecimalInput
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.planner.theme.activeFilterChipColors
import com.meal.presentation.recipes.IngredientFormRow
import com.meal.presentation.recipes.searchIngredients

/** Units offered for an ingredient that isn't in the catalogue; the rest sit behind "More units…". */
private val generalUnits = listOf(
    MeasurementUnit.PIECE,
    MeasurementUnit.GRAMS,
    MeasurementUnit.KILOGRAMS,
    MeasurementUnit.MILLILITER,
    MeasurementUnit.LITER,
    MeasurementUnit.TEASPOON,
    MeasurementUnit.TABLESPOON,
    MeasurementUnit.CUP,
    MeasurementUnit.PACK,
    MeasurementUnit.TO_TASTE
)

/**
 * Bottom sheet for adding an ingredient in two steps: find it by name, then give the amount.
 *
 * With [editing] set, the sheet opens on the amount step for that ingredient and confirms once.
 * Without it, every confirmed ingredient goes to [onConfirm] and "Add & next" returns to the
 * search, so several ingredients can be added without closing the sheet.
 *
 * A new ingredient's amount starts at [defaultQuantity], selected so typing replaces it. With
 * [requireQuantity], an ingredient can only be confirmed with an amount above zero.
 *
 * On a short screen the keyboard leaves room for only a strip of the sheet, so both steps switch
 * to a layout of horizontally scrolling rows and the drag handle is left out.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientPickerSheet(
    knownNames: List<String>,
    editing: IngredientFormRow?,
    onConfirm: (IngredientFormRow) -> Unit,
    onDismiss: () -> Unit,
    defaultQuantity: String = "",
    requireQuantity: Boolean = false,
    canChangeName: Boolean = true,
    amountExtras: (@Composable () -> Unit)? = null
) {
    val spacing = MealPlannerTokens.spacing
    val compact = LocalCompactHeight.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var searching by remember { mutableStateOf(editing == null) }
    var query by remember { mutableStateOf("") }
    var name by remember { mutableStateOf(editing?.name.orEmpty()) }
    // An existing amount keeps the cursor at its end so typing extends or corrects it; the default
    // amount is selected so typing replaces it.
    val newQuantity = { TextFieldValue(defaultQuantity, TextRange(0, defaultQuantity.length)) }
    var quantity by remember {
        mutableStateOf(editing?.quantity?.let { TextFieldValue(it, TextRange(it.length)) } ?: newQuantity())
    }
    var unit by remember { mutableStateOf(editing?.unit ?: MeasurementUnit.PIECE) }
    var showAllUnits by remember { mutableStateOf(false) }

    val pick: (String) -> Unit = { picked ->
        unit = IngredientUnits.unitAfterRename(name, picked, unit)
        name = picked
        query = ""
        showAllUnits = false
        searching = false
    }
    val canConfirm = !requireQuantity || (quantity.text.toDoubleOrNull() ?: 0.0) > 0.0
    val confirm: (closeAfter: Boolean) -> Unit = confirm@{ closeAfter ->
        if (!canConfirm) return@confirm
        onConfirm(IngredientFormRow(name, quantity.text, unit))
        if (closeAfter) {
            onDismiss()
        } else {
            name = ""
            quantity = newQuantity()
            unit = MeasurementUnit.PIECE
            showAllUnits = false
            searching = true
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = if (compact) null else { { BottomSheetDefaults.DragHandle() } }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // A full-height search step keeps the sheet from jumping as the result list changes.
                .then(if (searching && !compact) Modifier.fillMaxHeight() else Modifier)
                .imePadding()
                .padding(horizontal = spacing.lg, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(if (compact) spacing.sm else spacing.md)
        ) {
            if (searching) {
                SearchStep(
                    query = query,
                    onQueryChange = { query = it },
                    knownNames = knownNames,
                    compact = compact,
                    onPick = pick,
                    onClose = onDismiss
                )
            } else {
                val suggestedUnits = remember(name) { IngredientUnits.unitsFor(name) ?: generalUnits }
                AmountStep(
                    name = name,
                    quantity = quantity,
                    onQuantityChange = {
                        val filtered = filterDecimalInput(it.text)
                        quantity = if (filtered == it.text) it else TextFieldValue(filtered, TextRange(filtered.length))
                    },
                    // The chosen unit stays listed even when it isn't one of the suggested units.
                    units = if (showAllUnits) MeasurementUnit.entries else listOf(unit).filterNot { it in suggestedUnits } + suggestedUnits,
                    unit = unit,
                    onUnitChange = { unit = it },
                    onShowAllUnits = if (showAllUnits) null else { { showAllUnits = true } },
                    isEditing = editing != null,
                    canConfirm = canConfirm,
                    compact = compact,
                    onBack = if (canChangeName) { { searching = true } } else null,
                    onConfirm = confirm,
                    extras = amountExtras
                )
            }
        }
    }
}

@Composable
private fun SearchStep(
    query: String,
    onQueryChange: (String) -> Unit,
    knownNames: List<String>,
    compact: Boolean,
    onPick: (String) -> Unit,
    onClose: () -> Unit
) {
    val strings = LocalStrings.current
    val spacing = MealPlannerTokens.spacing
    val result = remember(query, knownNames) { searchIngredients(query, knownNames) }
    val customName = query.trim().takeIf { it.isNotEmpty() && !result.hasExactMatch }?.let(::ingredientDisplayName)
    val searchFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { searchFocus.requestFocus() }

    val searchField = @Composable {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(strings.ingredientSearchPlaceholder) },
            leadingIcon = {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                }
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = strings.clearSearch)
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                (result.matches.firstOrNull()?.name ?: customName)?.let(onPick)
            }),
            modifier = Modifier.fillMaxWidth().focusRequester(searchFocus)
        )
    }

    if (compact) {
        // Results sit in one scrolling row above the field, the only space the keyboard leaves.
        LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
            items(result.matches, key = { it.name }) { match ->
                SuggestionChip(
                    onClick = { onPick(match.name) },
                    icon = { IngredientThumbnail(name = match.name) },
                    label = { Text(match.alias?.let { "${match.name} ($it)" } ?: match.name) }
                )
            }
            if (customName != null) {
                item {
                    SuggestionChip(
                        onClick = { onPick(customName) },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        label = { Text(strings.ingredientAddCustom(customName)) }
                    )
                }
            }
        }
        searchField()
        return
    }

    searchField()
    if (query.isBlank()) {
        Text(
            text = strings.ingredientSearchHint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val itemColors = ListItemDefaults.colors(containerColor = Color.Transparent)
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(result.matches, key = { it.name }) { match ->
            ListItem(
                headlineContent = { Text(match.name) },
                supportingContent = match.alias?.let { alias -> { Text(alias) } },
                leadingContent = { IngredientThumbnail(name = match.name, size = MealPlannerTokens.iconSize.lg) },
                colors = itemColors,
                modifier = Modifier.clickable { onPick(match.name) }
            )
        }
        if (customName != null) {
            item {
                ListItem(
                    headlineContent = { Text(strings.ingredientAddCustom(customName)) },
                    leadingContent = { Icon(Icons.Filled.Add, contentDescription = null) },
                    colors = itemColors,
                    modifier = Modifier.clickable { onPick(customName) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.AmountStep(
    name: String,
    quantity: TextFieldValue,
    onQuantityChange: (TextFieldValue) -> Unit,
    units: List<MeasurementUnit>,
    unit: MeasurementUnit,
    onUnitChange: (MeasurementUnit) -> Unit,
    onShowAllUnits: (() -> Unit)?,
    isEditing: Boolean,
    canConfirm: Boolean,
    compact: Boolean,
    onBack: (() -> Unit)?,
    onConfirm: (closeAfter: Boolean) -> Unit,
    extras: (@Composable () -> Unit)? = null
) {
    val strings = LocalStrings.current
    val spacing = MealPlannerTokens.spacing
    val qtyFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { qtyFocus.requestFocus() }

    val header = @Composable { modifier: Modifier ->
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
            }
        }
        IngredientThumbnail(name = name, size = MealPlannerTokens.iconSize.lg)
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }
    val quantityField = @Composable { modifier: Modifier ->
        OutlinedTextField(
            value = quantity,
            onValueChange = onQuantityChange,
            label = { Text(strings.qty) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onConfirm(isEditing) }),
            modifier = modifier.focusRequester(qtyFocus)
        )
    }
    val buttons = @Composable {
        if (isEditing) {
            Button(onClick = { onConfirm(true) }, enabled = canConfirm) { Text(strings.save) }
        } else {
            OutlinedButton(onClick = { onConfirm(true) }, enabled = canConfirm) { Text(strings.ingredientDone) }
            Button(onClick = { onConfirm(false) }, enabled = canConfirm) { Text(strings.ingredientAddAndNext) }
        }
    }
    val unitChip = @Composable { option: MeasurementUnit ->
        FilterChip(
            selected = option == unit,
            onClick = { onUnitChange(option) },
            label = { Text(option.displayName()) },
            colors = activeFilterChipColors()
        )
    }
    val moreUnitsChip = @Composable {
        if (onShowAllUnits != null) {
            FilterChip(
                selected = false,
                onClick = onShowAllUnits,
                label = { Text(strings.moreUnits, color = MaterialTheme.colorScheme.primary) }
            )
        }
    }

    if (compact) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.fillMaxWidth()
        ) {
            header(Modifier.weight(1f))
            quantityField(Modifier.width(120.dp))
            buttons()
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
            items(units, key = { it.name }) { unitChip(it) }
            item { moreUnitsChip() }
        }
        extras?.invoke()
        return
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        header(Modifier.weight(1f))
    }
    quantityField(Modifier.fillMaxWidth())
    // Only the units scroll, so the buttons below stay above the keyboard.
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())
    ) {
        Text(strings.unit, style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
            units.forEach { unitChip(it) }
            moreUnitsChip()
        }
        extras?.invoke()
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.End),
        modifier = Modifier.fillMaxWidth()
    ) {
        buttons()
    }
}
