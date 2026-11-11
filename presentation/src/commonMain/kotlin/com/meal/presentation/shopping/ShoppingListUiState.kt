package com.meal.presentation.shopping

import com.meal.domain.model.Ingredient
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.model.ShoppingListItem
import kotlinx.datetime.LocalDate

enum class ShoppingListRangeMode { WEEK, RANGE, DAY }

data class ShoppingListSection(
    val group: ShoppingGroup?,
    val items: List<ShoppingListItem>
)

data class ShoppingListUiState(
    val mode: ShoppingListRangeMode,
    val rangeStart: LocalDate,
    val rangeEnd: LocalDate,
    val items: List<ShoppingListItem> = emptyList(),
    val groups: List<ShoppingGroup> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableIngredients: List<Ingredient> = emptyList(),
    val showAddItemDialog: Boolean = false,
    val itemBeingPriced: ShoppingListItem? = null,
    val itemPendingDeletion: ShoppingListItem? = null,
    val itemPendingGroupAssignment: ShoppingListItem? = null,
    val groupManagementOpen: Boolean = false,
    val groupPendingDeletion: ShoppingGroup? = null,
    val currencySymbol: String = "$"
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty() && errorMessage == null

    val totalSpent: Double
        get() = items.sumOf { it.price ?: 0.0 }

    val sections: List<ShoppingListSection>
        get() {
            val byGroupId = items.groupBy { it.groupId }
            val groupSections = groups.sortedBy { it.sortOrder }
                .mapNotNull { group -> byGroupId[group.id]?.let { ShoppingListSection(group, it) } }
            val ungrouped = byGroupId[null].orEmpty()
            return if (ungrouped.isEmpty()) groupSections else groupSections + ShoppingListSection(null, ungrouped)
        }
}
