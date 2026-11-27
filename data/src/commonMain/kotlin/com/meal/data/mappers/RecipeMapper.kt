package com.meal.data.mappers

import com.meal.data.model.RecipeDto
import com.meal.domain.model.Recipe
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object RecipeMapper {

    fun toDomain(dto: RecipeDto): Recipe {
        return Recipe(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            categories = dto.categories,
            preparationTimeMinutes = dto.preparationTimeMinutes,
            servings = dto.servings,
            imageUrl = dto.imageUrl,
            ingredients = dto.ingredients,
            instructions = dto.instructions,
            isFavorite = dto.isFavorite,
            isUserCreated = dto.isUserCreated,
            cuisines = dto.cuisines,
            mealTypes = dto.mealTypes
        )
    }

    fun toDomain(dtos: List<RecipeDto>): List<Recipe> {
        return dtos.map { toDomain(it) }
    }

    fun toDto(domain: Recipe): RecipeDto {
        return RecipeDto(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            categories = domain.categories,
            preparationTimeMinutes = domain.preparationTimeMinutes,
            servings = domain.servings,
            imageUrl = domain.imageUrl,
            ingredients = domain.ingredients,
            instructions = domain.instructions,
            isFavorite = domain.isFavorite,
            isUserCreated = domain.isUserCreated,
            cuisines = domain.cuisines,
            mealTypes = domain.mealTypes
        )
    }

    fun toDto(domains: List<Recipe>): List<RecipeDto> {
        return domains.map { toDto(it) }
    }
}
