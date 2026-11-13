package com.meal.planner.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.planner.strings.LocalStrings

fun MealType.icon(): ImageVector = when (this) {
    MealType.BREAKFAST -> Icons.Default.BreakfastDining
    MealType.LUNCH -> Icons.Default.LunchDining
    MealType.DINNER -> Icons.Default.DinnerDining
    MealType.SNACK -> Icons.Default.Cookie
}

@Composable
fun MealType.displayName(): String = LocalStrings.current.mealTypeName(this)

@Composable
fun FoodCategory.displayName(): String = LocalStrings.current.foodCategoryName(this)

@Composable
fun Cuisine.displayName(): String = LocalStrings.current.cuisineName(this)

@Composable
fun MeasurementUnit.displayName(): String = LocalStrings.current.measurementUnitName(this)
