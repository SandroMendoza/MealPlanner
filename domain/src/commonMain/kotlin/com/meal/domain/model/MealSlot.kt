package com.meal.domain.model

import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class MealSlot(
    val id: Uuid,
    val date: LocalDate,
    val mealType: MealType,
    val recipeId: Uuid
)
