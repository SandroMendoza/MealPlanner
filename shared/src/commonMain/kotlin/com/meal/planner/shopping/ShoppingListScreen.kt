package com.meal.planner.shopping

import com.meal.presentation.shopping.ShoppingListUiState
import com.meal.presentation.shopping.ShoppingListSection
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.common.LocalWideWidth
import com.meal.planner.common.centeredMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.VerticalDivider
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.IngredientUnits
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.planner.components.DateRangePickerDialog
import com.meal.planner.components.IngredientPickerSheet
import com.meal.planner.components.IngredientThumbnail
import com.meal.planner.components.MealDatePickerDialog
import com.meal.planner.common.formatPrice
import com.meal.planner.components.ScrollAwayHeader
import com.meal.planner.components.SwipeActionsRow
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.formatAmount
import com.meal.presentation.common.formatQuantity
import com.meal.presentation.recipes.IngredientFormRow
import com.meal.presentation.common.today
import com.meal.presentation.shopping.ShoppingListRangeMode
import com.meal.presentation.shopping.ShoppingListViewModel
import org.koin.compose.viewmodel.koinViewModel

private val CONTROLS_PANE_WIDTH = 280.dp

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
            val unit = IngredientUnits.unitsFor(quickAddItemName)?.first() ?: MeasurementUnit.PIECE
            viewModel.addItem(quickAddItemName, 1.0, unit)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        val wide = LocalWideWidth.current
        if (LocalCompactHeight.current || wide) {
            Row(modifier = Modifier.padding(padding).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .width(CONTROLS_PANE_WIDTH)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    ShoppingRangeControls(uiState, viewModel, onPickDate = { showDatePicker = true }, stacked = true)
                    Spacer(Modifier.height(spacing.sectionGap))
                    if (uiState.isSelecting) {
                        SelectionBar(uiState, viewModel)
                    } else {
                        ShoppingTotal(uiState)
                        AddItemButton(onClick = { viewModel.openAddItemDialog() }, modifier = Modifier.fillMaxWidth())
                    }
                }
                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ShoppingListContent(
                    uiState = uiState,
                    sections = sections,
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .then(if (wide) Modifier.centeredMaxWidth() else Modifier)
                )
            }
        } else {
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                ScrollAwayHeader(
                    modifier = Modifier.weight(1f),
                    header = {
                        Column(modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm)) {
                            ShoppingRangeControls(uiState, viewModel, onPickDate = { showDatePicker = true }, stacked = false)
                        }
                    }
                ) {
                    ShoppingListContent(uiState = uiState, sections = sections, viewModel = viewModel, modifier = Modifier.fillMaxSize())
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.lg, vertical = spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.isSelecting) {
                        SelectionBar(uiState, viewModel)
                    } else {
                        ShoppingTotal(uiState)
                        AddItemButton(onClick = { viewModel.openAddItemDialog() })
                    }
                }
            }
        }

        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = uiState.isSelecting,
            onBackCompleted = { viewModel.clearSelection() }
        )

        if (uiState.confirmingRemoveSelected) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissRemoveSelected() },
                title = { Text(LocalStrings.current.shoppingRemoveSelectedTitle) },
                text = { Text(LocalStrings.current.shoppingRemoveSelectedBody(uiState.selectedItemKeys.size)) },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmRemoveSelected() }) {
                        Text(LocalStrings.current.remove, color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissRemoveSelected() }) { Text(LocalStrings.current.cancel) }
                }
            )
        }

        if (uiState.showAddItemDialog) {
            var groupId by remember { mutableStateOf<String?>(null) }
            IngredientPickerSheet(
                knownNames = remember(uiState.availableIngredients) { uiState.availableIngredients.map { it.name } },
                editing = null,
                onConfirm = { row -> row.quantity.toDoubleOrNull()?.let { viewModel.addItem(row.name, it, row.unit, groupId) } },
                onDismiss = { viewModel.dismissAddItemDialog() },
                defaultQuantity = "1",
                requireQuantity = true,
                amountExtras = if (uiState.groups.isEmpty()) null else {
                    { GroupChoice(groups = uiState.groups, selectedId = groupId, onSelect = { groupId = it }) }
                }
            )
        }

        uiState.itemBeingEdited?.let { item ->
            IngredientPickerSheet(
                knownNames = emptyList(),
                editing = IngredientFormRow(item.name, formatQuantity(item.totalQuantity), item.unit),
                onConfirm = { row -> row.quantity.toDoubleOrNull()?.let { viewModel.updateItemAmount(it, row.unit) } },
                onDismiss = { viewModel.dismissEditAmount() },
                requireQuantity = true,
                canChangeName = false
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

/** Label, arrows and "go to today" for the selected range mode. Range mode has no arrows. */
private class RangeNavigation(
    val label: String,
    val labelFillsRow: Boolean,
    val onPrevious: (() -> Unit)?,
    val previousCd: String,
    val onNext: (() -> Unit)?,
    val nextCd: String,
    val showToday: Boolean,
    val todayCd: String,
    val pickCd: String
)

@Composable
private fun rangeNavigation(uiState: ShoppingListUiState, viewModel: ShoppingListViewModel): RangeNavigation {
    val strings = LocalStrings.current
    return when (uiState.mode) {
        ShoppingListRangeMode.WEEK -> RangeNavigation(
            label = "${uiState.rangeStart} - ${uiState.rangeEnd}",
            labelFillsRow = true,
            onPrevious = { viewModel.previousWeek() },
            previousCd = strings.previousWeek,
            onNext = { viewModel.nextWeek() },
            nextCd = strings.nextWeek,
            showToday = uiState.rangeStart != currentWeekSunday(),
            todayCd = strings.goToCurrentWeek,
            pickCd = strings.pickAWeek
        )
        ShoppingListRangeMode.DAY -> RangeNavigation(
            label = "${uiState.rangeStart}",
            labelFillsRow = false,
            onPrevious = { viewModel.previousDay() },
            previousCd = strings.previousDay,
            onNext = { viewModel.nextDay() },
            nextCd = strings.nextDay,
            showToday = uiState.rangeStart != today(),
            todayCd = strings.goToToday,
            pickCd = strings.pickADate
        )
        ShoppingListRangeMode.RANGE -> RangeNavigation(
            label = "${uiState.rangeStart} - ${uiState.rangeEnd}",
            labelFillsRow = true,
            onPrevious = null,
            previousCd = "",
            onNext = null,
            nextCd = "",
            showToday = false,
            todayCd = "",
            pickCd = strings.pickADateRange
        )
    }
}

/**
 * The range mode toggle, the range label with its arrows, and the range actions. [stacked] puts
 * the actions on their own row under the label, for the narrow side pane on short screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingRangeControls(
    uiState: ShoppingListUiState,
    viewModel: ShoppingListViewModel,
    onPickDate: () -> Unit,
    stacked: Boolean
) {
    val spacing = MealPlannerTokens.spacing
    val nav = rangeNavigation(uiState, viewModel)

    Column {
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

        if (stacked) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                nav.onPrevious?.let { onPrevious ->
                    IconButton(onClick = onPrevious) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = nav.previousCd)
                    }
                }
                Text(
                    text = nav.label,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
                nav.onNext?.let { onNext ->
                    IconButton(onClick = onNext) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = nav.nextCd)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RangeActions(nav, uiState, viewModel, onPickDate)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                nav.onPrevious?.let { onPrevious ->
                    IconButton(onClick = onPrevious) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = nav.previousCd)
                    }
                }
                Text(
                    text = nav.label,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (nav.labelFillsRow) Modifier.weight(1f) else Modifier
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RangeActions(nav, uiState, viewModel, onPickDate)
                    nav.onNext?.let { onNext ->
                        IconButton(onClick = onNext) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = nav.nextCd)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RangeActions(
    nav: RangeNavigation,
    uiState: ShoppingListUiState,
    viewModel: ShoppingListViewModel,
    onPickDate: () -> Unit
) {
    if (nav.showToday) {
        IconButton(onClick = { viewModel.goToToday() }) {
            Icon(Icons.Filled.Today, contentDescription = nav.todayCd)
        }
    }
    IconButton(onClick = onPickDate) {
        Icon(Icons.Filled.CalendarMonth, contentDescription = nav.pickCd)
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

@Composable
private fun ShoppingListContent(
    uiState: ShoppingListUiState,
    sections: List<ShoppingListSection>,
    viewModel: ShoppingListViewModel,
    modifier: Modifier = Modifier
) {
    val spacing = MealPlannerTokens.spacing
    when {
        uiState.isLoading -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        uiState.errorMessage != null -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                uiState.errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        uiState.isEmpty -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                LocalStrings.current.shoppingNoMealsPlannedPeriod,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        else -> LazyColumn(modifier = modifier) {
            (sections + listOfNotNull(uiState.atHomeSection)).forEach { section ->
                val headerKey = if (section.atHome) "at-home" else section.group?.id ?: "ungrouped"
                item(key = "header-$headerKey") {
                    Text(
                        text = when {
                            section.atHome -> LocalStrings.current.shoppingAtHome
                            else -> section.group?.name ?: LocalStrings.current.shoppingUngrouped
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = spacing.lg)
                    )
                }
                items(section.items, key = { it.itemKey }) { item ->
                    val selecting = uiState.isSelecting
                    val selected = item.itemKey in uiState.selectedItemKeys
                    SwipeActionsRow(
                        onSwipeRightToAssignGroup = { viewModel.openGroupPicker(item) },
                        onSwipeLeftToDelete = { viewModel.requestDeleteItem(item) },
                        enabled = !selecting
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
                                .background(
                                    if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background
                                )
                                .semantics {
                                    if (selecting) this.selected = selected else toggleableState = ToggleableState(item.checked)
                                }
                                .combinedClickable(
                                    role = if (selecting) null else Role.Checkbox,
                                    onLongClickLabel = LocalStrings.current.shoppingSelectItemCd,
                                    onLongClick = {
                                        if (selecting) viewModel.toggleSelection(item.itemKey) else viewModel.startSelection(item.itemKey)
                                    },
                                    onClick = {
                                        if (selecting) viewModel.toggleSelection(item.itemKey) else viewModel.toggleItem(item.itemKey, !item.checked)
                                    }
                                )
                                .heightIn(min = MealPlannerTokens.iconSize.xl)
                                .padding(horizontal = spacing.lg),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selected) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = spacing.md).size(MealPlannerTokens.iconSize.lg)
                                )
                            } else {
                                IngredientThumbnail(
                                    name = item.name,
                                    alpha = if (item.checked) 0.4f else 1f,
                                    modifier = Modifier.padding(end = spacing.md)
                                )
                            }
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = contentColor,
                                textDecoration = textDecoration,
                                modifier = Modifier.weight(1f).padding(end = spacing.sm)
                            )
                            Text(
                                text = formatAmount(item.totalQuantity, item.unit, LocalStrings.current),
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor,
                                textDecoration = textDecoration,
                                modifier = Modifier
                                    .clickable(enabled = !selecting, onClickLabel = LocalStrings.current.shoppingEditAmountCd) {
                                        viewModel.openEditAmount(item)
                                    }
                                    .padding(horizontal = spacing.xs, vertical = spacing.sm)
                            )
                            val price = item.price
                            if (price != null) {
                                Text(
                                    text = "${uiState.currencySymbol}${formatPrice(price)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable(enabled = !selecting) { viewModel.openRecordPriceDialog(item) }
                                )
                            } else {
                                Text(
                                    text = uiState.currencySymbol,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.clickable(enabled = !selecting) { viewModel.openRecordPriceDialog(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Picks the group a new item goes into; no pick leaves it ungrouped. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroupChoice(groups: List<ShoppingGroup>, selectedId: String?, onSelect: (String?) -> Unit) {
    val spacing = MealPlannerTokens.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Text(LocalStrings.current.shoppingGroupLabel, style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
            groups.sortedBy { it.sortOrder }.forEach { group ->
                FilterChip(
                    selected = group.id == selectedId,
                    onClick = { onSelect(if (group.id == selectedId) null else group.id) },
                    label = { Text(group.name) }
                )
            }
        }
    }
}

@Composable
private fun SelectionBar(uiState: ShoppingListUiState, viewModel: ShoppingListViewModel) {
    val spacing = MealPlannerTokens.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        IconButton(onClick = { viewModel.clearSelection() }) {
            Icon(Icons.Filled.Close, contentDescription = LocalStrings.current.shoppingCancelSelectionCd)
        }
        Text(
            text = LocalStrings.current.shoppingSelectedCount(uiState.selectedItemKeys.size),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { viewModel.moveSelectedAtHome() }) {
            Icon(
                imageVector = if (uiState.selectionIsAtHome) Icons.Filled.AddShoppingCart else Icons.Filled.Home,
                contentDescription = if (uiState.selectionIsAtHome) {
                    LocalStrings.current.shoppingMoveToListCd
                } else {
                    LocalStrings.current.shoppingMoveToAtHomeCd
                }
            )
        }
        Button(
            onClick = { viewModel.requestRemoveSelected() },
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(MealPlannerTokens.iconSize.sm))
            Spacer(Modifier.width(spacing.xxs))
            Text(LocalStrings.current.remove)
        }
    }
}

@Composable
private fun ShoppingTotal(uiState: ShoppingListUiState) {
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
}

@Composable
private fun AddItemButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = MealPlannerTokens.spacing
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.xs),
        modifier = modifier
    ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(MealPlannerTokens.iconSize.sm))
        Spacer(Modifier.width(spacing.xxs))
        Text(LocalStrings.current.shoppingAddItemButton, style = MaterialTheme.typography.labelMedium)
    }
}
