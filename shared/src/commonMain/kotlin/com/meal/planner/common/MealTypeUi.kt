package com.meal.planner.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.ui.graphics.vector.ImageVector
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit

fun MealType.icon(): ImageVector = when (this) {
    MealType.BREAKFAST -> Icons.Default.BreakfastDining
    MealType.LUNCH -> Icons.Default.LunchDining
    MealType.DINNER -> Icons.Default.DinnerDining
    MealType.SNACK -> Icons.Default.Cookie
}

fun MealType.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }

fun FoodCategory.displayName(): String =
    name.lowercase().split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

fun MeasurementUnit.displayName(): String =
    "${name.lowercase().replaceFirstChar { it.uppercase() }} ($unit)"
