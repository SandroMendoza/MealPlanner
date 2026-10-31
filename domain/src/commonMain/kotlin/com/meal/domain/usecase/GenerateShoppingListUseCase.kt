package com.meal.domain.usecase

import com.meal.domain.model.ShoppingListItem
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate

class GenerateShoppingListUseCase(
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase,
    private val shoppingListRepository: ShoppingListRepository
) {
    fun execute(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<ShoppingListItem>> =
        combine(
            getWeeklyMealPlanUseCase.execute(rangeStart, rangeEnd),
            shoppingListRepository.getCheckedItemKeys()
        ) { entries, checkedKeys ->
            val aggregated = linkedMapOf<String, ShoppingListItem>()
            entries.forEach { entry ->
                entry.recipe.ingredients.forEach { ingredient ->
                    val key = "${ingredient.ingredient.name.lowercase().trim()}|${ingredient.unit.name}"
                    val existing = aggregated[key]
                    aggregated[key] = if (existing != null) {
                        existing.copy(totalQuantity = existing.totalQuantity + ingredient.quantity)
                    } else {
                        ShoppingListItem(
                            itemKey = key,
                            name = ingredient.ingredient.name.trim(),
                            totalQuantity = ingredient.quantity,
                            unit = ingredient.unit,
                            checked = false
                        )
                    }
                }
            }
            aggregated.values
                .map { it.copy(checked = it.itemKey in checkedKeys) }
                .sortedBy { it.name }
        }
}
