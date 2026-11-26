package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.Recipe
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.AddRecipeUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.UpdateRecipeUseCase
import com.meal.presentation.common.formatQuantity
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
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val getAllIngredientsUseCase: GetAllIngredientsUseCase,
    private val getAllRecipesUseCase: GetAllRecipesUseCase
) : ViewModel() {

    // A new recipe starts with one empty step, ready to type into.
    private val _uiState = MutableStateFlow(
        RecipeFormUiState(
            isEditMode = recipeId != null,
            instructions = if (recipeId == null) listOf(InstructionFormRow()) else emptyList()
        ).let { it.copy(initialContent = it.content) }
    )
    val state = _uiState.asStateFlow()

    init {
        recipeId?.let { loadRecipe(it) }
        viewModelScope.launch {
            getAllIngredientsUseCase.execute().collect { list ->
                _uiState.value = _uiState.value.copy(availableIngredients = list)
            }
        }
        viewModelScope.launch {
            getAllRecipesUseCase.execute().collect { list ->
                _uiState.value = _uiState.value.copy(
                    existingRecipeNames = list.filter { it.id != recipeId }.map { it.name }
                )
            }
        }
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
                    cuisine = recipe.cuisine,
                    mealTypes = recipe.mealTypes,
                    preparationTimeMinutes = recipe.preparationTimeMinutes.toString(),
                    servings = recipe.servings.toString(),
                    imageUrl = recipe.imageUrl.orEmpty(),
                    ingredients = recipe.ingredients.map {
                        IngredientFormRow(it.ingredient.name, formatQuantity(it.quantity), it.unit)
                    },
                    instructions = recipe.instructions
                        .sortedBy { it.stepNumber }
                        .map { InstructionFormRow(it.description) }
                ).let { it.copy(initialContent = it.content) }
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

    fun updateCuisine(value: Cuisine) {
        val current = _uiState.value.cuisine
        _uiState.value = _uiState.value.copy(cuisine = if (current == value) null else value)
    }

    fun toggleMealType(value: MealType) {
        val current = _uiState.value.mealTypes
        _uiState.value = _uiState.value.copy(mealTypes = if (value in current) current - value else current + value)
    }

    fun updatePreparationTime(value: String) {
        _uiState.value = _uiState.value.copy(preparationTimeMinutes = value)
    }

    fun updateServings(value: String) {
        _uiState.value = _uiState.value.copy(servings = value)
    }

    fun updateImageUrl(value: String) {
        _uiState.value = _uiState.value.copy(imageUrl = value)
    }

    fun addIngredient(row: IngredientFormRow) {
        _uiState.value = _uiState.value.copy(ingredients = _uiState.value.ingredients + row)
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

    /** Replaces step [index] with [steps], in order; used when an edit splits one step into several. */
    fun replaceInstructionRow(index: Int, steps: List<String>) {
        val current = _uiState.value.instructions
        _uiState.value = _uiState.value.copy(
            instructions = current.take(index) + steps.map(::InstructionFormRow) + current.drop(index + 1)
        )
    }

    fun moveInstructionRow(from: Int, to: Int) {
        val rows = _uiState.value.instructions
        if (from !in rows.indices || to !in rows.indices) return
        _uiState.value = _uiState.value.copy(
            instructions = rows.toMutableList().apply { add(to, removeAt(from)) }
        )
    }

    /** Replaces every step with [steps], keeping one empty step when there are none. */
    fun replaceInstructions(steps: List<String>) {
        _uiState.value = _uiState.value.copy(
            instructions = steps.ifEmpty { listOf("") }.map(::InstructionFormRow)
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
                    category = current.category,
                    cuisine = current.cuisine,
                    mealTypes = current.mealTypes,
                    preparationTimeMinutes = current.preparationTimeMinutes.toIntOrNull() ?: 0,
                    servings = current.servings.toIntOrNull() ?: 1,
                    imageUrl = current.imageUrl.ifBlank { null },
                    ingredients = current.ingredients
                        .filter { it.name.isNotBlank() }
                        .map {
                            RecipeIngredient(
                                id = Uuid.random(),
                                ingredient = Ingredient(Uuid.random(), it.name),
                                quantity = it.quantity.toDoubleOrNull() ?: 0.0,
                                unit = it.unit
                            )
                        },
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
