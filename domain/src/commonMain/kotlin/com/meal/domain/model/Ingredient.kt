package com.meal.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Ingredient(
    val id: Uuid,
    val name: String
)
