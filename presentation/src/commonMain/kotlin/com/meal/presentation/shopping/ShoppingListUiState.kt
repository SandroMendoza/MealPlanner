package com.meal.presentation.shopping

import com.meal.domain.model.Ingredient
import com.meal.domain.model.IngredientCatalog
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.model.ShoppingListItem
import kotlinx.datetime.LocalDate

enum class ShoppingListRangeMode { WEEK, RANGE, DAY }

data class ShoppingListSection(
    val group: ShoppingGroup?,
    val items: List<ShoppingListItem>,
    val atHome: Boolean = false
) {
    /** Identifies the section across list updates: the group's id, or a fixed key for the others. */
    val key: String
        get() = when {
            atHome -> AT_HOME_SECTION_KEY
            group != null -> group.id
            else -> UNGROUPED_SECTION_KEY
        }
}

const val AT_HOME_SECTION_KEY = "at-home"
const val UNGROUPED_SECTION_KEY = "ungrouped"

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
    val itemBeingEdited: ShoppingListItem? = null,
    /** Keys of the items picked in selection mode; selection mode is on while it isn't empty. */
    val selectedItemKeys: Set<String> = emptySet(),
    val confirmingRemoveSelected: Boolean = false,
    val itemPendingDeletion: ShoppingListItem? = null,
    val itemPendingGroupAssignment: ShoppingListItem? = null,
    /** True while the group picker is open for every selected item at once. */
    val selectionPendingGroupAssignment: Boolean = false,
    val groupManagementOpen: Boolean = false,
    val groupPendingDeletion: ShoppingGroup? = null,
    /** [ShoppingListSection.key]s of the group sections whose items are hidden. */
    val collapsedSectionKeys: Set<String> = emptySet(),
    val currencySymbol: String = "$",
    val readAloudAvailable: Boolean = false,
    val isSpeaking: Boolean = false
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty() && errorMessage == null

    val isSelecting: Boolean
        get() = selectedItemKeys.isNotEmpty()

    val isPickingGroup: Boolean
        get() = itemPendingGroupAssignment != null || selectionPendingGroupAssignment

    /** True when every selected item is in "Pantry staples", so the move action sends them back to the list. */
    val selectionIsAtHome: Boolean
        get() = isSelecting && items.filter { it.itemKey in selectedItemKeys }.all { it.atHome }

    /** The items already at home, after the items to buy; null when there are none. */
    val atHomeSection: ShoppingListSection?
        get() = items.filter { it.atHome }
            .sortedBy { it.name.lowercase() }
            .takeIf { it.isNotEmpty() }
            ?.let { ShoppingListSection(group = null, items = it, atHome = true) }

    val totalSpent: Double
        get() = items.sumOf { it.price ?: 0.0 }

    /**
     * Items to buy, split by shopping group, in group order with ungrouped items last. Items at
     * home are left out; they are in [atHomeSection]. Within a section,
     * items are ordered by ingredient category (in [com.meal.domain.model.enums.IngredientCategory]
     * order, items outside the catalogue last), then by name.
     */
    val sections: List<ShoppingListSection>
        get() {
            val byGroupId = items.filterNot { it.atHome }.sortedWith(categoryThenName).groupBy { it.groupId }
            val groupSections = groups.sortedBy { it.sortOrder }
                .mapNotNull { group -> byGroupId[group.id]?.let { ShoppingListSection(group, it) } }
            val ungrouped = byGroupId[null].orEmpty()
            return if (ungrouped.isEmpty()) groupSections else groupSections + ShoppingListSection(null, ungrouped)
        }
}

private val categoryThenName: Comparator<ShoppingListItem> =
    compareBy<ShoppingListItem> { IngredientCatalog.find(it.name)?.category?.ordinal ?: Int.MAX_VALUE }
        .thenBy { it.name.lowercase() }
