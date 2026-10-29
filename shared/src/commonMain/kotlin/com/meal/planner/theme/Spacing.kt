package com.meal.planner.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale used for paddings, gaps and margins across the app.
 * Access via MaterialTheme-style helper: `LocalSpacing.current.md`.
 */
data class MealPlannerSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val huge: Dp = 40.dp,
    val screenHorizontal: Dp = 16.dp,
    val screenVertical: Dp = 16.dp,
    val sectionGap: Dp = 24.dp,
    val cardPadding: Dp = 16.dp
)

/** Corner radii matching MealPlannerShapes, for custom shapes (e.g. pill buttons). */
data class MealPlannerRadius(
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 28.dp,
    val pill: Dp = 999.dp
)

/** Standard icon sizes. */
data class MealPlannerIconSize(
    val sm: Dp = 16.dp,
    val md: Dp = 24.dp,
    val lg: Dp = 32.dp,
    val xl: Dp = 48.dp
)

val LocalSpacing = staticCompositionLocalOf { MealPlannerSpacing() }
val LocalRadius = staticCompositionLocalOf { MealPlannerRadius() }
val LocalIconSize = staticCompositionLocalOf { MealPlannerIconSize() }
