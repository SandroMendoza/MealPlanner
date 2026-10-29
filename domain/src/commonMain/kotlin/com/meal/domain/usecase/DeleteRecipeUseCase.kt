package com.meal.domain.usecase

import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.RecipeRepository
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface DeleteRecipeResult {
    data object Deleted : DeleteRecipeResult
    data class RequiresConfirmation(val affectedDates: List<LocalDate>) : DeleteRecipeResult
}

@OptIn(ExperimentalUuidApi::class)
class DeleteRecipeUseCase(
    private val recipeRepository: RecipeRepository,
    private val mealPlanRepository: MealPlanRepository
) {
    suspend fun execute(recipeId: Uuid, force: Boolean = false): DeleteRecipeResult {
        val futureDates = mealPlanRepository.getFutureSlotDatesForRecipe(recipeId)
        if (futureDates.isNotEmpty() && !force) {
            return DeleteRecipeResult.RequiresConfirmation(futureDates)
        }
        if (futureDates.isNotEmpty()) {
            mealPlanRepository.removeSlotsForRecipeOnOrAfter(recipeId)
        }
        recipeRepository.softDeleteRecipe(recipeId)
        return DeleteRecipeResult.Deleted
    }
}
