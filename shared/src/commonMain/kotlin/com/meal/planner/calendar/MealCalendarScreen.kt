package com.meal.planner.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.enums.MealType
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.calendar.MealCalendarViewModel
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MealCalendarScreen(
    onSlotClick: (LocalDate, MealType) -> Unit,
    viewModel: MealCalendarViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { viewModel.previousWeek() }) { Text("< Prev") }
                Text(
                    "${uiState.weekStart} - ${uiState.weekEnd}",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { viewModel.nextWeek() }) { Text("Next >") }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(uiState.days) { day ->
                        Column(modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm)) {
                            Text(
                                text = "${day.dayOfWeek.name.take(3)} $day",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            MealType.entries.forEach { mealType ->
                                val entry = uiState.entryFor(day, mealType)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = spacing.xs),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mealType.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (entry != null) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TextButton(onClick = { onSlotClick(day, mealType) }) {
                                                Text(entry.recipe.name)
                                            }
                                            TextButton(onClick = { viewModel.removeSlot(day, mealType) }) {
                                                Text("Remove", color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    } else {
                                        TextButton(onClick = { onSlotClick(day, mealType) }) {
                                            Text("+ Add", color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}
