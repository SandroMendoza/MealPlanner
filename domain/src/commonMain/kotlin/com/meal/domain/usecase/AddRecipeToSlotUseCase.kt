package com.meal.domain.usecase

import com.meal.domain.model.enums.MealType
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
class AddRecipeToSlotUseCase(
    private val repository: MealPlanRepository,
    private val settingsRepository: SettingsRepository,
    private val today: () -> LocalDate = { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
) {
    /** Plans the recipe and returns true, or returns false when past dates are locked and [date] is past. */
    suspend fun execute(date: LocalDate, mealType: MealType, recipeId: Uuid): Boolean {
        if (date < today() && settingsRepository.getSettings().first().lockPastDates) return false
        repository.addRecipeToSlot(date, mealType, recipeId)
        return true
    }
}
