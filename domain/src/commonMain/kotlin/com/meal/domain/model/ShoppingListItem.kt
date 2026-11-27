package com.meal.domain.model

import com.meal.domain.model.enums.MeasurementUnit

data class ShoppingListItem(
    val itemKey: String,
    val name: String,
    /** Null, together with [unit], when the item has no measurement and shows by name only. */
    val totalQuantity: Double?,
    val unit: MeasurementUnit?,
    val checked: Boolean,
    val price: Double? = null,
    val groupId: String? = null,
    /** In the "Pantry staples" section: the user has it already, so it isn't among the items to buy. */
    val atHome: Boolean = false
)
