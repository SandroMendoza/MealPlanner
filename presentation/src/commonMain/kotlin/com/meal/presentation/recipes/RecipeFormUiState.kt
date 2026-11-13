package com.meal.presentation.recipes

import com.meal.domain.model.Ingredient
import com.meal.domain.model.enums.Cuisine
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
    val cuisine: Cuisine? = null,
    val mealTypes: Set<MealType> = emptySet(),
    val preparationTimeMinutes: String = "",
    val servings: String = "",
    val calories: String = "",
    val imageUrl: String = "",
    val ingredients: List<IngredientFormRow> = emptyList(),
    val instructions: List<InstructionFormRow> = emptyList(),
    val availableIngredients: List<Ingredient> = emptyList(),
    val existingRecipeNames: List<String> = emptyList()
) {
    val isDuplicateName: Boolean
        get() = name.isNotBlank() && existingRecipeNames.any { it.trim().equals(name.trim(), ignoreCase = true) }

    val canSave: Boolean
        get() = name.isNotBlank() &&
            !isDuplicateName &&
            mealTypes.isNotEmpty() &&
            ingredients.any { it.name.isNotBlank() } &&
            instructions.any { it.description.isNotBlank() } &&
            (preparationTimeMinutes.isBlank() || preparationTimeMinutes.toIntOrNull() != null) &&
            (servings.isBlank() || servings.toIntOrNull() != null)
}
