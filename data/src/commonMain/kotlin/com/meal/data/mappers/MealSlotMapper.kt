package com.meal.data.mappers

import com.meal.data.model.MealSlotDto
import com.meal.domain.model.MealSlot
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object MealSlotMapper {

    fun toDomain(dto: MealSlotDto): MealSlot = MealSlot(
        id = dto.id,
        date = dto.date,
        mealType = dto.mealType,
        recipeId = dto.recipeId
    )

    fun toDomain(dtos: List<MealSlotDto>): List<MealSlot> = dtos.map { toDomain(it) }
}
