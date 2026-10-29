package com.meal.domain.model

import com.meal.domain.model.enums.MeasurementUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Ingredient(
    val id: Uuid,
    val name: String,
    val quantity: Double,
    val unit: MeasurementUnit
)
