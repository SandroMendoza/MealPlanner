package com.meal.planner.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.displayName
import com.meal.planner.common.icon
import com.meal.planner.components.RecipeThumbnail
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.calendar.MealCalendarViewModel
import com.meal.presentation.common.today
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun MealCalendarScreen(
    onSlotClick: (LocalDate, MealType, Uuid?) -> Unit,
    onRecipeClick: (Uuid) -> Unit = {},
    viewModel: MealCalendarViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var selectedDate by remember { mutableStateOf(today().takeIf { it in uiState.days } ?: uiState.weekStart) }
    LaunchedEffect(uiState.weekStart) {
        selectedDate = today().takeIf { it in uiState.days } ?: uiState.weekStart
    }

    val entriesByMealType = remember(uiState.entries, selectedDate) {
        MealType.entries.associateWith { uiState.entriesFor(selectedDate, it) }
    }
    val firstEmptyMealType = remember(entriesByMealType) {
        MealType.entries.firstOrNull { entriesByMealType[it].isNullOrEmpty() }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.sm, vertical = spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousWeek() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous week")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Meal Plan", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "${uiState.weekStart} - ${uiState.weekEnd}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { viewModel.nextWeek() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next week")
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(uiState.days) { day ->
                    DayChip(date = day, selected = day == selectedDate, onClick = { selectedDate = day })
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
                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.lg)
                ) {
                    items(MealType.entries) { mealType ->
                        MealSection(
                            mealType = mealType,
                            entries = entriesByMealType[mealType].orEmpty(),
                            onAdd = { onSlotClick(selectedDate, mealType, null) },
                            onChangeEntry = { entry -> onSlotClick(selectedDate, mealType, entry.id) },
                            onRemoveEntry = { entry -> viewModel.removeSlot(entry.id) },
                            onOpenRecipe = onRecipeClick
                        )
                    }
                }
            }

            Button(
                onClick = { firstEmptyMealType?.let { onSlotClick(selectedDate, it, null) } },
                enabled = firstEmptyMealType != null,
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().padding(spacing.lg)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(spacing.xs))
                Text(if (firstEmptyMealType != null) "Add Meal" else "Day fully planned")
            }
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
            text = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
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
private fun MealSection(
    mealType: MealType,
    entries: List<MealPlanEntry>,
    onAdd: () -> Unit,
    onChangeEntry: (MealPlanEntry) -> Unit,
    onRemoveEntry: (MealPlanEntry) -> Unit,
    onOpenRecipe: (Uuid) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    Column {
        Text(mealType.displayName(), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(spacing.xs))
        if (entries.isEmpty()) {
            EmptySlotRow(mealType = mealType, onClick = onAdd)
        } else {
            entries.forEachIndexed { index, entry ->
                MealEntryCard(
                    entry = entry,
                    onClick = { onOpenRecipe(entry.recipe.id) },
                    onChange = { onChangeEntry(entry) },
                    onRemove = { onRemoveEntry(entry) }
                )
                if (index != entries.lastIndex) {
                    Spacer(Modifier.height(spacing.xs))
                }
            }
            Spacer(Modifier.height(spacing.xs))
            EmptySlotRow(
                mealType = mealType,
                onClick = onAdd,
                label = "Add another ${mealType.displayName().lowercase()}"
            )
        }
    }
}

@Composable
private fun MealEntryCard(entry: MealPlanEntry, onClick: () -> Unit, onChange: () -> Unit, onRemove: () -> Unit) {
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
                    text = "${entry.recipe.preparationTimeMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Meal options")
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("View recipe") },
                        onClick = { menuExpanded = false; onClick() }
                    )
                    DropdownMenuItem(
                        text = { Text("Change recipe") },
                        onClick = { menuExpanded = false; onChange() }
                    )
                    DropdownMenuItem(
                        text = { Text("Remove", color = MaterialTheme.colorScheme.error) },
                        onClick = { menuExpanded = false; onRemove() }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySlotRow(
    mealType: MealType,
    onClick: () -> Unit,
    label: String = "Add ${mealType.displayName().lowercase()}"
) {
    val spacing = MealPlannerTokens.spacing
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(MealPlannerTokens.radius.lg))
            .clickable(onClick = onClick)
            .padding(spacing.cardPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
