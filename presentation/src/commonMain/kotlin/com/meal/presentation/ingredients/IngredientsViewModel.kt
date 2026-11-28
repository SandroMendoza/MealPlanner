package com.meal.presentation.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.usecase.AddIngredientsUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.SetIngredientImageUseCase
import com.meal.domain.usecase.SetIngredientStapleUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Sections are rebuilt on [computeDispatcher] for every change of ingredients, query or filter;
 * searching all ingredients on each keystroke is too slow for the main thread.
 */
@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class IngredientsViewModel(
    getAllIngredientsUseCase: GetAllIngredientsUseCase,
    private val addIngredientsUseCase: AddIngredientsUseCase,
    private val setIngredientImageUseCase: SetIngredientImageUseCase,
    private val setIngredientStapleUseCase: SetIngredientStapleUseCase,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngredientsUiState())
    val state = _uiState.asStateFlow()

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(IngredientsFilter.ALL)

    init {
        viewModelScope.launch {
            combine(getAllIngredientsUseCase.execute(), query, filter) { ingredients, query, filter ->
                Triple(ingredients, query, filter)
            }
                .mapLatest { (ingredients, query, filter) ->
                    ingredients to withContext(computeDispatcher) { buildIngredientSections(ingredients, query, filter) }
                }
                .collect { (ingredients, sections) ->
                    _uiState.update { it.copy(ingredients = ingredients, sections = sections, isLoading = false) }
                }
        }
    }

    fun updateQuery(query: String) {
        this.query.value = query
        _uiState.update { it.copy(query = query) }
    }

    fun setFilter(filter: IngredientsFilter) {
        this.filter.value = filter
        _uiState.update { it.copy(filter = filter) }
    }

    /** Adds the names that aren't ingredients yet; the others are left as they are. */
    fun addIngredients(names: List<String>) {
        viewModelScope.launch { addIngredientsUseCase.execute(names) }
    }

    fun changeImage(id: Uuid, imageUri: String) {
        viewModelScope.launch { setIngredientImageUseCase.execute(id, imageUri) }
    }

    fun resetImage(id: Uuid) {
        viewModelScope.launch { setIngredientImageUseCase.execute(id, null) }
    }

    fun setStaple(id: Uuid, isStaple: Boolean) {
        viewModelScope.launch { setIngredientStapleUseCase.execute(id, isStaple) }
    }
}
