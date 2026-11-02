package com.meal.planner.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.planner.components.DateRangePickerDialog
import com.meal.planner.components.MealDatePickerDialog
import com.meal.planner.common.formatPrice
import com.meal.planner.components.SwipeToDeleteRow
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.common.currentWeekMonday
import com.meal.presentation.common.today
import com.meal.presentation.shopping.ShoppingListRangeMode
import com.meal.presentation.shopping.ShoppingListViewModel
import org.koin.compose.viewmodel.koinViewModel

private fun ShoppingListRangeMode.label(): String = when (this) {
    ShoppingListRangeMode.WEEK -> "Week"
    ShoppingListRangeMode.RANGE -> "Range"
    ShoppingListRangeMode.DAY -> "Day"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel = koinViewModel()) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ShoppingListRangeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = uiState.mode == mode,
                            onClick = { viewModel.selectMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ShoppingListRangeMode.entries.size
                            )
                        ) {
                            Text(mode.label())
                        }
                    }
                }

                Spacer(Modifier.height(spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (uiState.mode) {
                        ShoppingListRangeMode.WEEK -> {
                            IconButton(onClick = { viewModel.previousWeek() }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous week")
                            }
                            Text(
                                text = "${uiState.rangeStart} - ${uiState.rangeEnd}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.rangeStart != currentWeekMonday()) {
                                    IconButton(onClick = { viewModel.goToToday() }) {
                                        Icon(Icons.Filled.Today, contentDescription = "Go to current week")
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick a week")
                                }
                                IconButton(onClick = { viewModel.nextWeek() }) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next week")
                                }
                            }
                        }
                        ShoppingListRangeMode.DAY -> {
                            IconButton(onClick = { viewModel.previousDay() }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous day")
                            }
                            Text(
                                text = "${uiState.rangeStart}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.rangeStart != today()) {
                                    IconButton(onClick = { viewModel.goToToday() }) {
                                        Icon(Icons.Filled.Today, contentDescription = "Go to today")
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick a date")
                                }
                                IconButton(onClick = { viewModel.nextDay() }) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next day")
                                }
                            }
                        }
                        ShoppingListRangeMode.RANGE -> {
                            Text(
                                text = "${uiState.rangeStart} - ${uiState.rangeEnd}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = "Pick a date range")
                            }
                        }
                    }
                }
            }

            when {
                uiState.isLoading -> Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null -> Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        uiState.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.isEmpty -> Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "No meals planned for this period.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.items, key = { it.itemKey }) { item ->
                        SwipeToDeleteRow(onRequestDelete = { viewModel.requestDeleteItem(item) }) {
                            val contentColor = if (item.checked) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                            val textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .clickable { viewModel.toggleItem(item.itemKey, !item.checked) }
                                    .padding(horizontal = spacing.lg, vertical = spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.checked,
                                    onCheckedChange = { checked -> viewModel.toggleItem(item.itemKey, checked) },
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = contentColor,
                                    textDecoration = textDecoration,
                                    modifier = Modifier.weight(1f).padding(end = spacing.sm)
                                )
                                Text(
                                    text = "${item.totalQuantity} ${item.unit.unit}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = contentColor,
                                    textDecoration = textDecoration,
                                    modifier = Modifier.padding(end = spacing.sm)
                                )
                                val priceLabel = item.price?.let { price -> "$${formatPrice(price)}" } ?: "Add price"
                                Text(
                                    text = priceLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (item.price != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textDecoration = textDecoration,
                                    modifier = Modifier.clickable { viewModel.openRecordPriceDialog(item) }
                                )
                            }
                        }
                    }
                }
            }

            if (!uiState.isEmpty) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.lg, vertical = spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total spent", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "$${formatPrice(uiState.totalSpent)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = { viewModel.openAddItemDialog() },
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().padding(spacing.lg)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(spacing.xs))
                Text("Add Item")
            }
        }

        if (uiState.showAddItemDialog) {
            AddShoppingItemDialog(
                suggestions = uiState.availableIngredients.map { it.name },
                onConfirm = { name, quantity, unit -> viewModel.addItem(name, quantity, unit) },
                onDismiss = { viewModel.dismissAddItemDialog() }
            )
        }

        uiState.itemBeingPriced?.let { item ->
            RecordPriceDialog(
                item = item,
                onConfirm = { price -> viewModel.recordItemPrice(item.itemKey, price) },
                onDismiss = { viewModel.dismissRecordPriceDialog() }
            )
        }

        uiState.itemPendingDeletion?.let { item ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteConfirmation() },
                title = { Text("Remove item?") },
                text = { Text("Remove ${item.name} from the shopping list?") },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDeleteItem() }) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) { Text("Cancel") }
                }
            )
        }

        if (showDatePicker) {
            when (uiState.mode) {
                ShoppingListRangeMode.RANGE -> DateRangePickerDialog(
                    initialStart = uiState.rangeStart,
                    initialEnd = uiState.rangeEnd,
                    onRangeSelected = { start, end -> viewModel.selectRange(start, end) },
                    onDismiss = { showDatePicker = false }
                )
                else -> MealDatePickerDialog(
                    initialDate = uiState.rangeStart,
                    onDateSelected = { picked -> viewModel.goToDate(picked) },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}
