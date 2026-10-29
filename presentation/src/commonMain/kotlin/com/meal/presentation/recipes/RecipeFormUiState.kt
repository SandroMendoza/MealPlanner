package com.meal.presentation.recipes

import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit

data class IngredientFormRow(
    val name: String = "",
    val quantity: String = "",
    val unit: MeasurementUnit = MeasurementUnit.PIECE
)

data class InstructionFormRow(
    val description: String = ""
)

data class RecipeFormUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null,
    val name: String = "",
    val description: String = "",
    val category: FoodCategory = FoodCategory.MAIN_DISH,
    val mealTypes: Set<MealType> = emptySet(),
    val preparationTimeMinutes: String = "",
    val servings: String = "",
    val calories: String = "",
    val imageUrl: String = "",
    val ingredients: List<IngredientFormRow> = emptyList(),
    val instructions: List<InstructionFormRow> = emptyList()
) {
    val canSave: Boolean
        get() = name.isNotBlank() &&
            preparationTimeMinutes.toIntOrNull() != null &&
            servings.toIntOrNull() != null
}
