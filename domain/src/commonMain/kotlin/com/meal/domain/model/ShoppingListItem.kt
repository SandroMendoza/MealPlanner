package com.meal.domain.model

import com.meal.domain.model.enums.MeasurementUnit

data class ShoppingListItem(
    val itemKey: String,
    val name: String,
    val totalQuantity: Double,
    val unit: MeasurementUnit,
    val checked: Boolean
)
