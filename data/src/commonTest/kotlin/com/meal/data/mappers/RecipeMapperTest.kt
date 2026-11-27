package com.meal.data.mappers

import com.meal.data.model.RecipeDto
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeMapperTest {

    private val dto = RecipeDto(
        id = Uuid.random(),
        name = "Chicken Adobo",
        description = "Braised chicken",
        categories = setOf(FoodCategory.MAIN_DISH, FoodCategory.SOUP),
        preparationTimeMinutes = 45,
        servings = 4,
        imageUrl = "files/recipes/chicken_adobo.webp",
        ingredients = listOf(
            RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Chicken"), 1.0, MeasurementUnit.KILOGRAMS)
        ),
        instructions = listOf(Instruction(1, "Marinate"), Instruction(2, "Simmer")),
        isFavorite = true,
        isUserCreated = false,
        cuisines = setOf(Cuisine.FILIPINO),
        mealTypes = setOf(MealType.LUNCH, MealType.DINNER)
    )

    @Test
    fun toDomainCopiesEveryField() {
        val recipe = RecipeMapper.toDomain(dto)

        assertEquals(dto.id, recipe.id)
        assertEquals(dto.name, recipe.name)
        assertEquals(dto.description, recipe.description)
        assertEquals(dto.categories, recipe.categories)
        assertEquals(dto.preparationTimeMinutes, recipe.preparationTimeMinutes)
        assertEquals(dto.servings, recipe.servings)
        assertEquals(dto.imageUrl, recipe.imageUrl)
        assertEquals(dto.ingredients, recipe.ingredients)
        assertEquals(dto.instructions, recipe.instructions)
        assertEquals(dto.isFavorite, recipe.isFavorite)
        assertEquals(dto.isUserCreated, recipe.isUserCreated)
        assertEquals(dto.cuisines, recipe.cuisines)
        assertEquals(dto.mealTypes, recipe.mealTypes)
    }

    @Test
    fun toDtoReversesToDomain() {
        assertEquals(dto, RecipeMapper.toDto(RecipeMapper.toDomain(dto)))
    }

    @Test
    fun nullableFieldsStayNull() {
        val bare = dto.copy(description = null, imageUrl = null, cuisines = emptySet(), categories = emptySet())

        val roundTripped = RecipeMapper.toDto(RecipeMapper.toDomain(bare))

        assertEquals(bare, roundTripped)
    }

    @Test
    fun listMappingKeepsOrder() {
        val second = dto.copy(id = Uuid.random(), name = "Sinigang")

        val names = RecipeMapper.toDomain(listOf(dto, second)).map { it.name }

        assertEquals(listOf("Chicken Adobo", "Sinigang"), names)
        assertEquals(listOf(dto, second), RecipeMapper.toDto(RecipeMapper.toDomain(listOf(dto, second))))
    }
}
