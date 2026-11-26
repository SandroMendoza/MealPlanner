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

/**
 * The part of the recipe form a person types or picks. Blank steps are left out, because the
 * form adds empty steps on its own and those are never saved.
 */
data class RecipeFormContent(
    val name: String,
    val description: String,
    val category: FoodCategory,
    val cuisine: Cuisine?,
    val mealTypes: Set<MealType>,
    val preparationTimeMinutes: String,
    val servings: String,
    val imageUrl: String,
    val ingredients: List<IngredientFormRow>,
    val instructions: List<String>
)

data class RecipeFormUiState(
    /** What the form held when it opened: empty for a new recipe, the stored recipe when editing. */
    val initialContent: RecipeFormContent? = null,
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
    val imageUrl: String = "",
    val ingredients: List<IngredientFormRow> = emptyList(),
    val instructions: List<InstructionFormRow> = emptyList(),
    val availableIngredients: List<Ingredient> = emptyList(),
    val existingRecipeNames: List<String> = emptyList()
) {
    val content: RecipeFormContent
        get() = RecipeFormContent(
            name = name,
            description = description,
            category = category,
            cuisine = cuisine,
            mealTypes = mealTypes,
            preparationTimeMinutes = preparationTimeMinutes,
            servings = servings,
            imageUrl = imageUrl,
            ingredients = ingredients,
            instructions = instructions.map { it.description }.filter { it.isNotBlank() }
        )

    val hasUnsavedChanges: Boolean
        get() = !saved && initialContent != null && content != initialContent

    val isDuplicateName: Boolean
        get() = name.isNotBlank() && existingRecipeNames.any { it.trim().equals(name.trim(), ignoreCase = true) }

    val canSave: Boolean
        get() = name.isNotBlank() &&
            !isDuplicateName &&
            ingredients.any { it.name.isNotBlank() } &&
            instructions.any { it.description.isNotBlank() } &&
            (preparationTimeMinutes.isBlank() || preparationTimeMinutes.toIntOrNull() != null) &&
            (servings.isBlank() || servings.toIntOrNull() != null)
}
