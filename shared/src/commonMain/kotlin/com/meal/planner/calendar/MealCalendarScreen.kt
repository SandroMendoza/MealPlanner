package com.meal.planner.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.displayName
import com.meal.planner.common.icon
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.components.MealDatePickerDialog
import com.meal.planner.components.RecipeThumbnail
import com.meal.planner.components.ScrollAwayHeader
import com.meal.planner.components.SwipeActionsRow
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.calendar.CalendarViewMode
import com.meal.presentation.calendar.MealCalendarUiState
import com.meal.presentation.calendar.MealCalendarViewModel
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.today
import com.meal.presentation.common.weekNumberFor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
private fun CalendarViewMode.label(): String = when (this) {
    CalendarViewMode.DAY -> LocalStrings.current.day
    CalendarViewMode.WEEK -> LocalStrings.current.week
}

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealCalendarScreen(
    onSlotClick: (LocalDate, MealType?, Uuid?) -> Unit,
    onRecipeClick: (Uuid) -> Unit = {},
    viewModel: MealCalendarViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val selectedDate = uiState.selectedDate
    var showDatePicker by remember { mutableStateOf(false) }

    val isCurrentWeek = uiState.weekStart == currentWeekSunday()

    val dayEntries = remember(uiState.entries, selectedDate) { uiState.entriesFor(selectedDate) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ScrollAwayHeader(
                modifier = Modifier.weight(1f),
                header = { CalendarHeader(uiState, isCurrentWeek, selectedDate, viewModel, onPickDate = { showDatePicker = true }) }
            ) {
                CalendarContent(uiState, dayEntries, selectedDate, viewModel, onSlotClick, onRecipeClick)
            }

            // On a short screen the open keyboard leaves no room for both this button and the
            // note being typed, so the button steps aside until the keyboard closes.
            val keyboardCoversButton = LocalCompactHeight.current && WindowInsets.isImeVisible
            if (uiState.viewMode == CalendarViewMode.DAY && !keyboardCoversButton) {
                Button(
                    onClick = { onSlotClick(selectedDate, null, null) },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().padding(spacing.lg)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(spacing.xs))
                    Text(LocalStrings.current.calendarAddMeal)
                }
            }
        }

        if (showDatePicker) {
            MealDatePickerDialog(
                initialDate = selectedDate,
                onDateSelected = { picked -> viewModel.goToDate(picked) },
                onDismiss = { showDatePicker = false }
            )
        }

        uiState.entryPendingRemoval?.let { entry ->
            AlertDialog(
                onDismissRequest = viewModel::dismissRemoveSlot,
                title = { Text(LocalStrings.current.calendarRemoveMealTitle) },
                text = { Text(LocalStrings.current.calendarRemoveMealBody(entry.recipe.name)) },
                confirmButton = {
                    TextButton(onClick = viewModel::confirmRemoveSlot) {
                        Text(LocalStrings.current.remove, color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismissRemoveSlot) { Text(LocalStrings.current.cancel) }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarHeader(
    uiState: MealCalendarUiState,
    isCurrentWeek: Boolean,
    selectedDate: LocalDate,
    viewModel: MealCalendarViewModel,
    onPickDate: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.sm, vertical = spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousWeek() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = LocalStrings.current.previousWeek)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(LocalStrings.current.calendarTitle, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = formatWeekRange(uiState.weekStart, uiState.weekEnd),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = LocalStrings.current.calendarWeekNumber(weekNumberFor(uiState.weekStart)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isCurrentWeek) {
                    IconButton(onClick = { viewModel.goToToday() }) {
                        Icon(Icons.Filled.Today, contentDescription = LocalStrings.current.goToToday)
                    }
                }
                IconButton(onClick = onPickDate) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = LocalStrings.current.pickADate)
                }
                IconButton(onClick = { viewModel.nextWeek() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = LocalStrings.current.nextWeek)
                }
            }
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.lg)
        ) {
            CalendarViewMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = uiState.viewMode == mode,
                    onClick = { viewModel.setViewMode(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = CalendarViewMode.entries.size)
                ) {
                    Text(mode.label())
                }
            }
        }

        Spacer(Modifier.height(spacing.sm))

        if (uiState.viewMode == CalendarViewMode.DAY) {
            val compactHeight = LocalCompactHeight.current
            LazyRow(
                modifier = if (compactHeight) Modifier.fillMaxWidth() else Modifier,
                contentPadding = PaddingValues(horizontal = spacing.lg),
                horizontalArrangement = if (compactHeight) {
                    Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally)
                } else {
                    Arrangement.spacedBy(spacing.sm)
                }
            ) {
                items(uiState.days, key = { it }) { day ->
                    DayChip(date = day, selected = day == selectedDate, onClick = { viewModel.selectDate(day) })
                }
            }
        }

        Spacer(Modifier.height(spacing.lg))
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun CalendarContent(
    uiState: MealCalendarUiState,
    dayEntries: List<MealPlanEntry>,
    selectedDate: LocalDate,
    viewModel: MealCalendarViewModel,
    onSlotClick: (LocalDate, MealType?, Uuid?) -> Unit,
    onRecipeClick: (Uuid) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    when {
        uiState.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        uiState.errorMessage != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = uiState.errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        uiState.viewMode == CalendarViewMode.WEEK -> MealWeekList(
            modifier = Modifier.fillMaxSize(),
            days = uiState.days,
            entries = uiState.entries,
            weekNote = uiState.weekNote,
            onNoteChange = viewModel::updateWeekNote,
            onEntryClick = { entry -> onRecipeClick(entry.recipe.id) },
            onAddMeal = { day -> onSlotClick(day, null, null) },
            isLocked = uiState::isLocked,
            onRemove = viewModel::requestRemoveSlot
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            if (dayEntries.isEmpty()) {
                item {
                    Text(
                        text = LocalStrings.current.calendarNoMealsPlannedDay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(dayEntries, key = { it.id.toString() }) { entry ->
                val locked = uiState.isLocked(selectedDate)
                SwipeToRemove(
                    enabled = !locked,
                    onRemove = { viewModel.requestRemoveSlot(entry) },
                    shape = MaterialTheme.shapes.large
                ) {
                    MealEntryCard(
                        entry = entry,
                        locked = locked,
                        onClick = { onRecipeClick(entry.recipe.id) },
                        onChange = { onSlotClick(selectedDate, entry.mealType, entry.id) },
                        onRemove = { viewModel.removeSlot(entry) }
                    )
                }
            }
            item(key = "week-notes") {
                WeekNotesCard(note = uiState.weekNote, onNoteChange = viewModel::updateWeekNote)
            }
        }
    }
}

@Composable
private fun SwipeToRemove(
    enabled: Boolean,
    onRemove: () -> Unit,
    shape: Shape,
    content: @Composable () -> Unit
) {
    if (enabled) {
        SwipeActionsRow(onSwipeLeftToDelete = onRemove, modifier = Modifier.clip(shape)) { content() }
    } else {
        content()
    }
}

@Composable
private fun DayChip(date: LocalDate, selected: Boolean, onClick: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    val containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(containerColor)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = MealPlannerTokens.iconSize.xl)
            .padding(vertical = spacing.sm, horizontal = spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.xxs)
    ) {
        Text(
            text = LocalStrings.current.weekdayShort(date.dayOfWeek),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) contentColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = date.day.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor
        )
    }
}

@Composable
private fun formatWeekRange(start: LocalDate, end: LocalDate): String {
    val strings = LocalStrings.current
    val startText = "${strings.monthAbbreviation(start.month.number)} ${start.day}"
    val endText = "${strings.monthAbbreviation(end.month.number)} ${end.day}, ${end.year}"
    return if (start.year == end.year) {
        "$startText – $endText"
    } else {
        "$startText, ${start.year} – $endText"
    }
}

@Composable
private fun MealWeekList(
    modifier: Modifier = Modifier,
    days: List<LocalDate>,
    entries: List<MealPlanEntry>,
    weekNote: String,
    onNoteChange: (String) -> Unit,
    onEntryClick: (MealPlanEntry) -> Unit,
    onAddMeal: (LocalDate) -> Unit,
    isLocked: (LocalDate) -> Boolean,
    onRemove: (MealPlanEntry) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val todayDate = today()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = spacing.lg, end = spacing.lg, bottom = spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        items(days, key = { it.toString() }) { day ->
            WeekDayCard(
                day = day,
                entries = entries.filter { it.date == day }.sortedBy { it.mealType.ordinal },
                isToday = day == todayDate,
                onEntryClick = onEntryClick,
                onAddMeal = { onAddMeal(day) },
                canRemove = !isLocked(day),
                onRemove = onRemove
            )
        }
        item(key = "week-notes") {
            WeekNotesCard(note = weekNote, onNoteChange = onNoteChange)
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun WeekDayCard(
    day: LocalDate,
    entries: List<MealPlanEntry>,
    isToday: Boolean,
    onEntryClick: (MealPlanEntry) -> Unit,
    onAddMeal: () -> Unit,
    canRemove: Boolean,
    onRemove: (MealPlanEntry) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val iconSize = MealPlannerTokens.iconSize
    val colors = MaterialTheme.colorScheme
    val strings = LocalStrings.current
    val cardColor = if (isToday) colors.primaryContainer else colors.surface
    // Today's card is tinted with primaryContainer, which the default tonal/outlined
    // button colours blend into, so its action buttons sit on the plain surface colour.
    val actionContainer = if (isToday) colors.surface else colors.secondaryContainer
    val tonalColors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = actionContainer)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeekDayDateBlock(day = day, isToday = isToday, modifier = Modifier.fillMaxHeight())
            if (!isToday) {
                VerticalDivider(
                    modifier = Modifier.padding(vertical = spacing.md),
                    color = colors.outlineVariant
                )
            }
            if (entries.isEmpty()) {
                Row(
                    modifier = Modifier.weight(1f).padding(horizontal = spacing.md, vertical = spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(iconSize.lg)
                            .clip(CircleShape)
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Restaurant,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(iconSize.sm)
                        )
                    }
                    Spacer(Modifier.width(spacing.sm))
                    Text(
                        text = strings.calendarNoMealsPlanned,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedButton(
                    onClick = onAddMeal,
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isToday) colors.surface else Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = spacing.md),
                    modifier = Modifier.padding(end = spacing.md)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(iconSize.sm))
                    Spacer(Modifier.width(spacing.xs))
                    Text(strings.add)
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = spacing.md, vertical = spacing.sm)
                ) {
                    entries.forEach { entry ->
                        key(entry.id.toString()) {
                            SwipeToRemove(
                                enabled = canRemove,
                                onRemove = { onRemove(entry) },
                                shape = MaterialTheme.shapes.small
                            ) {
                                WeekMealRow(
                                    entry = entry,
                                    backgroundColor = cardColor,
                                    onClick = { onEntryClick(entry) }
                                )
                            }
                        }
                    }
                }
                FilledTonalIconButton(
                    onClick = onAddMeal,
                    colors = tonalColors,
                    modifier = Modifier.padding(end = spacing.sm)
                ) {
                    Icon(Icons.Default.Add, contentDescription = strings.calendarAddMeal, modifier = Modifier.size(iconSize.sm))
                }
            }
        }
    }
}

@Composable
private fun WeekDayDateBlock(day: LocalDate, isToday: Boolean, modifier: Modifier = Modifier) {
    val spacing = MealPlannerTokens.spacing
    val colors = MaterialTheme.colorScheme
    val contentColor = if (isToday) colors.onPrimary else colors.onSurface

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(if (isToday) colors.primary else Color.Transparent)
            .padding(horizontal = spacing.md, vertical = spacing.md)
            .width(MealPlannerTokens.iconSize.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = LocalStrings.current.weekdayShort(day.dayOfWeek),
            style = MaterialTheme.typography.labelLarge,
            color = if (isToday) contentColor else colors.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = day.day.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = contentColor,
            maxLines = 1
        )
    }
}

@Composable
private fun WeekMealRow(entry: MealPlanEntry, backgroundColor: Color, onClick: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    // Opaque so the delete background stays hidden until the row is swiped.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            entry.mealType.icon(),
            contentDescription = entry.mealType.displayName(),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MealPlannerTokens.iconSize.sm)
        )
        Spacer(Modifier.width(spacing.sm))
        Text(
            text = entry.recipe.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WeekNotesCard(note: String, onNoteChange: (String) -> Unit) {
    val spacing = MealPlannerTokens.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(spacing.cardPadding)) {
            Text(
                text = LocalStrings.current.calendarWeekNotes,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(spacing.sm))
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                placeholder = { Text(LocalStrings.current.calendarWeekNotesPlaceholder) },
                textStyle = MaterialTheme.typography.bodyMedium,
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MealEntryCard(entry: MealPlanEntry, locked: Boolean, onClick: () -> Unit, onChange: () -> Unit, onRemove: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecipeThumbnail(
                imageUrl = entry.recipe.imageUrl,
                fallbackIcon = entry.mealType.icon(),
                size = MealPlannerTokens.iconSize.xl,
                shape = MaterialTheme.shapes.small
            )
            Spacer(Modifier.width(spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.mealType.displayName().uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = entry.recipe.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = LocalStrings.current.minutesLabel(entry.recipe.preparationTimeMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = LocalStrings.current.calendarMealOptionsCd)
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(LocalStrings.current.calendarViewRecipe) },
                        onClick = { menuExpanded = false; onClick() }
                    )
                    DropdownMenuItem(
                        text = { Text(LocalStrings.current.calendarChangeRecipe) },
                        onClick = { menuExpanded = false; onChange() }
                    )
                    if (!locked) {
                        DropdownMenuItem(
                            text = { Text(LocalStrings.current.remove, color = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onRemove() }
                        )
                    }
                }
            }
        }
    }
}
