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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.planner.components.DateRangePickerDialog
import com.meal.planner.components.MealDatePickerDialog
import com.meal.planner.common.formatPrice
import com.meal.planner.components.SwipeActionsRow
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.formatQuantity
import com.meal.presentation.common.today
import com.meal.presentation.shopping.ShoppingListRangeMode
import com.meal.presentation.shopping.ShoppingListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun ShoppingListRangeMode.label(): String = when (this) {
    ShoppingListRangeMode.WEEK -> LocalStrings.current.week
    ShoppingListRangeMode.RANGE -> LocalStrings.current.range
    ShoppingListRangeMode.DAY -> LocalStrings.current.day
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel = koinViewModel(), quickAddItemName: String? = null) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val sections = remember(uiState) { uiState.sections }

    LaunchedEffect(quickAddItemName) {
        if (quickAddItemName != null) {
            viewModel.addItem(quickAddItemName, 1.0, MeasurementUnit.PIECE)
        }
    }

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
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = LocalStrings.current.previousWeek)
                            }
                            Text(
                                text = "${uiState.rangeStart} - ${uiState.rangeEnd}",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.rangeStart != currentWeekSunday()) {
                                    IconButton(onClick = { viewModel.goToToday() }) {
                                        Icon(Icons.Filled.Today, contentDescription = LocalStrings.current.goToCurrentWeek)
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Filled.CalendarMonth, contentDescription = LocalStrings.current.pickAWeek)
                                }
                                IconButton(onClick = { viewModel.openGroupManagement() }) {
                                    Icon(Icons.Filled.Folder, contentDescription = LocalStrings.current.shoppingManageGroupsCd)
                                }
                                if (uiState.readAloudAvailable) {
                                    IconButton(onClick = { viewModel.toggleReadAloud() }) {
                                        Icon(
                                            imageVector = if (uiState.isSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = if (uiState.isSpeaking) {
                                                LocalStrings.current.shoppingStopReadAloudCd
                                            } else {
                                                LocalStrings.current.shoppingReadAloudCd
                                            }
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.nextWeek() }) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = LocalStrings.current.nextWeek)
                                }
                            }
                        }
                        ShoppingListRangeMode.DAY -> {
                            IconButton(onClick = { viewModel.previousDay() }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = LocalStrings.current.previousDay)
                            }
                            Text(
                                text = "${uiState.rangeStart}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.rangeStart != today()) {
                                    IconButton(onClick = { viewModel.goToToday() }) {
                                        Icon(Icons.Filled.Today, contentDescription = LocalStrings.current.goToToday)
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Filled.CalendarMonth, contentDescription = LocalStrings.current.pickADate)
                                }
                                IconButton(onClick = { viewModel.openGroupManagement() }) {
                                    Icon(Icons.Filled.Folder, contentDescription = LocalStrings.current.shoppingManageGroupsCd)
                                }
                                if (uiState.readAloudAvailable) {
                                    IconButton(onClick = { viewModel.toggleReadAloud() }) {
                                        Icon(
                                            imageVector = if (uiState.isSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = if (uiState.isSpeaking) {
                                                LocalStrings.current.shoppingStopReadAloudCd
                                            } else {
                                                LocalStrings.current.shoppingReadAloudCd
                                            }
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.nextDay() }) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = LocalStrings.current.nextDay)
                                }
                            }
                        }
                        ShoppingListRangeMode.RANGE -> {
                            Text(
                                text = "${uiState.rangeStart} - ${uiState.rangeEnd}",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Filled.CalendarMonth, contentDescription = LocalStrings.current.pickADateRange)
                                }
                                IconButton(onClick = { viewModel.openGroupManagement() }) {
                                    Icon(Icons.Filled.Folder, contentDescription = LocalStrings.current.shoppingManageGroupsCd)
                                }
                                if (uiState.readAloudAvailable) {
                                    IconButton(onClick = { viewModel.toggleReadAloud() }) {
                                        Icon(
                                            imageVector = if (uiState.isSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = if (uiState.isSpeaking) {
                                                LocalStrings.current.shoppingStopReadAloudCd
                                            } else {
                                                LocalStrings.current.shoppingReadAloudCd
                                            }
                                        )
                                    }
                                }
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
                        LocalStrings.current.shoppingNoMealsPlannedPeriod,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> LazyColumn(modifier = Modifier.weight(1f)) {
                    sections.forEach { section ->
                        item(key = "header-${section.group?.id ?: "ungrouped"}") {
                            Text(
                                text = section.group?.name ?: LocalStrings.current.shoppingUngrouped,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(horizontal = spacing.lg)
                            )
                        }
                        items(section.items, key = { it.itemKey }) { item ->
                            SwipeActionsRow(
                                onSwipeRightToAssignGroup = { viewModel.openGroupPicker(item) },
                                onSwipeLeftToDelete = { viewModel.requestDeleteItem(item) }
                            ) {
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
                                        .padding(horizontal = spacing.lg),
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
                                        text = "${formatQuantity(item.totalQuantity)} ${item.unit.unit}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = contentColor,
                                        textDecoration = textDecoration,
                                        modifier = Modifier.padding(end = spacing.sm)
                                    )
                                    val price = item.price
                                    if (price != null) {
                                        Text(
                                            text = "${uiState.currencySymbol}${formatPrice(price)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable { viewModel.openRecordPriceDialog(item) }
                                        )
                                    } else {
                                        Text(
                                            text = uiState.currencySymbol,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.clickable { viewModel.openRecordPriceDialog(item) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.lg, vertical = spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (!uiState.isEmpty) {
                        Text(LocalStrings.current.totalSpent, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${uiState.currencySymbol}${formatPrice(uiState.totalSpent)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Button(
                    onClick = { viewModel.openAddItemDialog() },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.xs)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(MealPlannerTokens.iconSize.sm))
                    Spacer(Modifier.width(spacing.xxs))
                    Text(LocalStrings.current.shoppingAddItemButton, style = MaterialTheme.typography.labelMedium)
                }
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
                currencySymbol = uiState.currencySymbol,
                onConfirm = { price -> viewModel.recordItemPrice(item.itemKey, price) },
                onDismiss = { viewModel.dismissRecordPriceDialog() }
            )
        }

        uiState.itemPendingDeletion?.let { item ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteConfirmation() },
                title = { Text(LocalStrings.current.shoppingRemoveItemTitle) },
                text = { Text(LocalStrings.current.shoppingRemoveItemBody(item.name)) },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDeleteItem() }) {
                        Text(LocalStrings.current.remove, color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) { Text(LocalStrings.current.cancel) }
                }
            )
        }

        if (uiState.itemPendingGroupAssignment != null) {
            AssignGroupDialog(
                groups = uiState.groups,
                onAssign = { groupId -> viewModel.assignGroup(groupId) },
                onCreateGroup = { name -> viewModel.createGroupAndAssign(name) },
                onDismiss = { viewModel.dismissGroupPicker() }
            )
        }

        if (uiState.groupManagementOpen) {
            ManageGroupsDialog(
                groups = uiState.groups,
                onRename = { id, name -> viewModel.renameGroup(id, name) },
                onRequestDelete = { group -> viewModel.requestDeleteGroup(group) },
                onDismiss = { viewModel.dismissGroupManagement() }
            )
        }

        uiState.groupPendingDeletion?.let { group ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteGroupConfirmation() },
                title = { Text(LocalStrings.current.shoppingDeleteGroupTitle) },
                text = { Text(LocalStrings.current.shoppingDeleteGroupBody(group.name)) },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDeleteGroup() }) {
                        Text(LocalStrings.current.remove, color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDeleteGroupConfirmation() }) { Text(LocalStrings.current.cancel) }
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
