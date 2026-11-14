package com.meal.data.mappers

import com.meal.data.model.MealSlotDto
import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MealSlotMapperTest {

    @Test
    fun toDomainCopiesEveryField() {
        val dto = MealSlotDto(Uuid.random(), LocalDate(2026, 11, 14), MealType.DINNER, Uuid.random())

        val slot = MealSlotMapper.toDomain(dto)

        assertEquals(dto.id, slot.id)
        assertEquals(dto.date, slot.date)
        assertEquals(dto.mealType, slot.mealType)
        assertEquals(dto.recipeId, slot.recipeId)
    }

    @Test
    fun listMappingKeepsOrder() {
        val first = MealSlotDto(Uuid.random(), LocalDate(2026, 11, 14), MealType.BREAKFAST, Uuid.random())
        val second = MealSlotDto(Uuid.random(), LocalDate(2026, 11, 15), MealType.LUNCH, Uuid.random())

        val ids = MealSlotMapper.toDomain(listOf(first, second)).map { it.id }

        assertEquals(listOf(first.id, second.id), ids)
    }
}
