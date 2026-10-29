package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.AddRecipeUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.UpdateRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeFormViewModel(
    private val recipeId: Uuid?,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val addRecipeUseCase: AddRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeFormUiState(isEditMode = recipeId != null))
    val state = _uiState.asStateFlow()

    init {
        recipeId?.let { loadRecipe(it) }
    }

    private fun loadRecipe(id: Uuid) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val recipe = getRecipeByIdUseCase.execute(id).first()
            _uiState.value = if (recipe != null) {
                _uiState.value.copy(
                    isLoading = false,
                    name = recipe.name,
                    description = recipe.description.orEmpty(),
                    category = recipe.category,
                    mealTypes = recipe.mealTypes.toSet(),
                    preparationTimeMinutes = recipe.preparationTimeMinutes.toString(),
                    servings = recipe.servings.toString(),
                    calories = recipe.calories?.toString().orEmpty(),
                    imageUrl = recipe.imageUrl.orEmpty(),
                    ingredients = recipe.ingredients.map {
                        IngredientFormRow(it.name, it.quantity.toString(), it.unit)
                    },
                    instructions = recipe.instructions
                        .sortedBy { it.stepNumber }
                        .map { InstructionFormRow(it.description) }
                )
            } else {
                _uiState.value.copy(isLoading = false, errorMessage = "Recipe not found")
            }
        }
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun updateCategory(value: FoodCategory) {
        _uiState.value = _uiState.value.copy(category = value)
    }

    fun toggleMealType(value: MealType) {
        val current = _uiState.value.mealTypes
        _uiState.value = _uiState.value.copy(
            mealTypes = if (value in current) current - value else current + value
        )
    }

    fun updatePreparationTime(value: String) {
        _uiState.value = _uiState.value.copy(preparationTimeMinutes = value)
    }

    fun updateServings(value: String) {
        _uiState.value = _uiState.value.copy(servings = value)
    }

    fun updateCalories(value: String) {
        _uiState.value = _uiState.value.copy(calories = value)
    }

    fun updateImageUrl(value: String) {
        _uiState.value = _uiState.value.copy(imageUrl = value)
    }

    fun addIngredientRow() {
        _uiState.value = _uiState.value.copy(ingredients = _uiState.value.ingredients + IngredientFormRow())
    }

    fun updateIngredientRow(index: Int, row: IngredientFormRow) {
        _uiState.value = _uiState.value.copy(
            ingredients = _uiState.value.ingredients.toMutableList().apply { this[index] = row }
        )
    }

    fun removeIngredientRow(index: Int) {
        _uiState.value = _uiState.value.copy(
            ingredients = _uiState.value.ingredients.filterIndexed { i, _ -> i != index }
        )
    }

    fun addInstructionRow() {
        _uiState.value = _uiState.value.copy(instructions = _uiState.value.instructions + InstructionFormRow())
    }

    fun updateInstructionRow(index: Int, row: InstructionFormRow) {
        _uiState.value = _uiState.value.copy(
            instructions = _uiState.value.instructions.toMutableList().apply { this[index] = row }
        )
    }

    fun removeInstructionRow(index: Int) {
        _uiState.value = _uiState.value.copy(
            instructions = _uiState.value.instructions.filterIndexed { i, _ -> i != index }
        )
    }

    fun save() {
        val current = _uiState.value
        if (!current.canSave) return
        viewModelScope.launch {
            _uiState.value = current.copy(isSaving = true, errorMessage = null)
            try {
                val recipe = Recipe(
                    id = recipeId ?: Uuid.random(),
                    name = current.name,
                    description = current.description.ifBlank { null },
                    mealTypes = current.mealTypes.toList(),
                    category = current.category,
                    preparationTimeMinutes = current.preparationTimeMinutes.toIntOrNull() ?: 0,
                    servings = current.servings.toIntOrNull() ?: 1,
                    calories = current.calories.toIntOrNull(),
                    imageUrl = current.imageUrl.ifBlank { null },
                    ingredients = current.ingredients
                        .filter { it.name.isNotBlank() }
                        .map { Ingredient(Uuid.random(), it.name, it.quantity.toDoubleOrNull() ?: 0.0, it.unit) },
                    instructions = current.instructions
                        .filter { it.description.isNotBlank() }
                        .mapIndexed { index, row -> Instruction(index + 1, row.description) }
                )
                if (recipeId != null) {
                    updateRecipeUseCase.execute(recipe)
                } else {
                    addRecipeUseCase.execute(recipe)
                }
                _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = "Failed to save: ${e.message}")
            }
        }
    }
}
