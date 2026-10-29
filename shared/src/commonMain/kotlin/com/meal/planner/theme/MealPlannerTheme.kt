package com.meal.planner.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp

private val MealPlannerShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Global design system entry point. Wrap the app root in this once;
 * screens then read tokens via MaterialTheme.colorScheme / .typography / .shapes
 * and MealPlannerTheme.spacing / .radius / .iconSize.
 */
@Composable
fun MealPlannerTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSpacing provides MealPlannerSpacing(),
        LocalRadius provides MealPlannerRadius(),
        LocalIconSize provides MealPlannerIconSize()
    ) {
        MaterialTheme(
            colorScheme = MealPlannerColorScheme,
            typography = MealPlannerTypography,
            shapes = MealPlannerShapes,
            content = content
        )
    }
}

/** Convenience accessors mirroring MaterialTheme's `MaterialTheme.colorScheme` style. */
object MealPlannerTokens {
    val spacing: MealPlannerSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val radius: MealPlannerRadius
        @Composable
        @ReadOnlyComposable
        get() = LocalRadius.current

    val iconSize: MealPlannerIconSize
        @Composable
        @ReadOnlyComposable
        get() = LocalIconSize.current
}
