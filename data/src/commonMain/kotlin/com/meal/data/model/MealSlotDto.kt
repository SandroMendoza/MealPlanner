package com.meal.data.model

import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class MealSlotDto(
    val id: Uuid,
    val date: LocalDate,
    val mealType: MealType,
    val recipeId: Uuid
)
