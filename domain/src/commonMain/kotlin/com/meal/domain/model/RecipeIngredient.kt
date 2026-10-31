package com.meal.domain.model

import com.meal.domain.model.enums.MeasurementUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class RecipeIngredient(
    val id: Uuid,
    val ingredient: Ingredient,
    val quantity: Double,
    val unit: MeasurementUnit
)
