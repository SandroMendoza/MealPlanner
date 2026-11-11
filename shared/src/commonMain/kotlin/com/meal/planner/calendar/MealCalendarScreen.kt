package com.meal.planner.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.displayName
import com.meal.planner.common.icon
import com.meal.planner.components.MealDatePickerDialog
import com.meal.planner.components.RecipeThumbnail
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.calendar.CalendarViewMode
import com.meal.presentation.calendar.MealCalendarViewModel
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.today
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
private fun CalendarViewMode.label(): String = when (this) {
    CalendarViewMode.DAY -> LocalStrings.current.day
    CalendarViewMode.WEEK -> LocalStrings.current.week
}

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
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

    val entriesByMealType = remember(uiState.entries, selectedDate) {
        MealType.entries.associateWith { uiState.entriesFor(selectedDate, it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
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
                        text = "${uiState.weekStart} - ${uiState.weekEnd}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isCurrentWeek) {
                        IconButton(onClick = { viewModel.goToToday() }) {
                            Icon(Icons.Filled.Today, contentDescription = LocalStrings.current.goToToday)
                        }
                    }
                    IconButton(onClick = { showDatePicker = true }) {
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
                LazyRow(
                    contentPadding = PaddingValues(horizontal = spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    items(uiState.days, key = { it }) { day ->
                        DayChip(date = day, selected = day == selectedDate, onClick = { viewModel.selectDate(day) })
                    }
                }
            }

            Spacer(Modifier.height(spacing.lg))

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
                uiState.viewMode == CalendarViewMode.WEEK -> MealWeekGrid(
                    modifier = Modifier.weight(1f),
                    days = uiState.days,
                    entries = uiState.entries,
                    weekNote = uiState.weekNote,
                    onNoteChange = viewModel::updateWeekNote,
                    onSlotClick = onSlotClick,
                    onOpenRecipe = onRecipeClick
                )
                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.lg)
                ) {
                    items(MealType.entries, key = { it.name }) { mealType ->
                        MealSection(
                            mealType = mealType,
                            entries = entriesByMealType[mealType].orEmpty(),
                            locked = uiState.isLocked(selectedDate),
                            onAdd = { onSlotClick(selectedDate, mealType, null) },
                            onChangeEntry = { entry -> onSlotClick(selectedDate, mealType, entry.id) },
                            onRemoveEntry = { entry -> viewModel.removeSlot(entry) },
                            onOpenRecipe = onRecipeClick
                        )
                    }
                }
            }

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

        if (showDatePicker) {
            MealDatePickerDialog(
                initialDate = selectedDate,
                onDateSelected = { picked -> viewModel.goToDate(picked) },
                onDismiss = { showDatePicker = false }
            )
        }
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
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun MealWeekGrid(
    modifier: Modifier = Modifier,
    days: List<LocalDate>,
    entries: List<MealPlanEntry>,
    weekNote: String,
    onNoteChange: (String) -> Unit,
    onSlotClick: (LocalDate, MealType?, Uuid?) -> Unit,
    onOpenRecipe: (Uuid) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val todayDate = today()

    // uiState.days already starts on Sunday (the app's week model is Sunday-first).
    val onEntryClick: (MealPlanEntry) -> Unit = { entry -> onOpenRecipe(entry.recipe.id) }
    val onAddMeal: (LocalDate) -> Unit = { day -> onSlotClick(day, null, null) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.sm, vertical = spacing.sm)
    ) {
        WeekTable(
            days = days.take(3),
            entries = entries,
            todayDate = todayDate,
            noteText = null,
            onNoteChange = onNoteChange,
            onEntryClick = onEntryClick,
            onAddMeal = onAddMeal
        )
        Spacer(Modifier.height(spacing.lg))
        WeekTable(
            days = days.drop(3).take(3),
            entries = entries,
            todayDate = todayDate,
            noteText = null,
            onNoteChange = onNoteChange,
            onEntryClick = onEntryClick,
            onAddMeal = onAddMeal
        )
        Spacer(Modifier.height(spacing.lg))
        WeekTable(
            days = days.drop(6),
            entries = entries,
            todayDate = todayDate,
            noteText = weekNote,
            onNoteChange = onNoteChange,
            onEntryClick = onEntryClick,
            onAddMeal = onAddMeal
        )
    }
}

@Composable
private fun WeekTable(
    days: List<LocalDate>,
    entries: List<MealPlanEntry>,
    todayDate: LocalDate,
    noteText: String?,
    onNoteChange: (String) -> Unit,
    onEntryClick: (MealPlanEntry) -> Unit,
    onAddMeal: (LocalDate) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            days.forEachIndexed { index, day ->
                if (index != 0) {
                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                val dayEntries = entries.filter { it.date == day }.sortedBy { it.mealType.ordinal }
                WeekDayColumn(
                    day = day,
                    entries = dayEntries,
                    isToday = day == todayDate,
                    onEntryClick = onEntryClick,
                    onAddMeal = { onAddMeal(day) },
                    modifier = Modifier.weight(1f)
                )
            }
            if (noteText != null) {
                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(modifier = Modifier.weight(2f).padding(spacing.xxs)) {
                    Text(
                        text = LocalStrings.current.calendarWeekNotes,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.xxs)
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = onNoteChange,
                        placeholder = {
                            Text(LocalStrings.current.calendarWeekNotesPlaceholder, style = MaterialTheme.typography.labelSmall)
                        },
                        textStyle = MaterialTheme.typography.labelSmall,
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
    }
}

@Composable
private fun WeekGridHeaderCell(day: LocalDate, isToday: Boolean, modifier: Modifier = Modifier) {
    val spacing = MealPlannerTokens.spacing
    val backgroundColor = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val contentColor = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        modifier = modifier
            .background(backgroundColor)
            .padding(vertical = spacing.xxs),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = LocalStrings.current.weekdayShort(day.dayOfWeek),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
        Text(
            text = " - ${day.dayOfMonth}",
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun WeekDayColumn(
    day: LocalDate,
    entries: List<MealPlanEntry>,
    isToday: Boolean,
    onEntryClick: (MealPlanEntry) -> Unit,
    onAddMeal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MealPlannerTokens.spacing
    Column(
        modifier = (if (isToday) modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)) else modifier)
            .padding(horizontal = spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeekGridHeaderCell(day = day, isToday = isToday, modifier = Modifier.fillMaxWidth())
        entries.forEach { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEntryClick(entry) }
                    .padding(vertical = spacing.xxs)
            ) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(end = spacing.xxs)
                )
                Text(
                    text = entry.recipe.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(top = spacing.xxs, bottom = spacing.xxs)
                .size(MealPlannerTokens.iconSize.md)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = onAddMeal),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = LocalStrings.current.calendarAddMeal,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(MealPlannerTokens.iconSize.sm)
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun MealSection(
    mealType: MealType,
    entries: List<MealPlanEntry>,
    locked: Boolean,
    onAdd: () -> Unit,
    onChangeEntry: (MealPlanEntry) -> Unit,
    onRemoveEntry: (MealPlanEntry) -> Unit,
    onOpenRecipe: (Uuid) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(mealType.displayName(), style = MaterialTheme.typography.titleMedium)
            AddMealButton(mealType = mealType, onClick = onAdd)
        }
        Spacer(Modifier.height(spacing.xs))
        if (entries.isEmpty()) {
            Text(
                text = LocalStrings.current.calendarNoMealTypePlanned(mealType.displayName().lowercase()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            entries.forEachIndexed { index, entry ->
                MealEntryCard(
                    entry = entry,
                    locked = locked,
                    onClick = { onOpenRecipe(entry.recipe.id) },
                    onChange = { onChangeEntry(entry) },
                    onRemove = { onRemoveEntry(entry) }
                )
                if (index != entries.lastIndex) {
                    Spacer(Modifier.height(spacing.xs))
                }
            }
        }
    }
}

@Composable
private fun AddMealButton(mealType: MealType, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = LocalStrings.current.calendarAddMealTypeCd(mealType.displayName().lowercase()),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(MealPlannerTokens.iconSize.sm)
        )
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
