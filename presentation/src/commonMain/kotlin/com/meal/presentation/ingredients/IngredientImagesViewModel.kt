package com.meal.presentation.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.normalizedIngredientName
import com.meal.domain.usecase.GetAllIngredientsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** The pictures users chose for ingredients, by normalized ingredient name, for every screen that shows ingredients. */
class IngredientImagesViewModel(getAllIngredientsUseCase: GetAllIngredientsUseCase) : ViewModel() {

    val images: StateFlow<Map<String, String>> = getAllIngredientsUseCase.execute()
        .map { ingredients ->
            ingredients.mapNotNull { ingredient ->
                ingredient.imageUri?.let { normalizedIngredientName(ingredient.name) to it }
            }.toMap()
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())
}
