package com.meal.presentation.calendar

import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalDate

data class MealCalendarUiState(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val days: List<LocalDate> = emptyList(),
    val entries: List<MealPlanEntry> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    fun entriesFor(date: LocalDate, mealType: MealType): List<MealPlanEntry> =
        entries.filter { it.date == date && it.mealType == mealType }
}
