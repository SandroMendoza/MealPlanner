package com.meal.domain.model

import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.datetime.LocalDate

data class SpendingLineItem(
    val itemKey: String,
    val name: String,
    val totalQuantity: Double,
    val unit: MeasurementUnit,
    val price: Double,
    val rangeStart: LocalDate,
    val rangeEnd: LocalDate
)
