package com.meal.planner.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val GreenPrimary = Color(0xFF4B7A33)
private val GreenPrimaryDark = Color(0xFF2E4A1F)
private val CreamBackground = Color(0xFFF5F3EA)
private val CardWhite = Color(0xFFFFFFFF)
private val PaleGreenContainer = Color(0xFFE3ECD9)
private val DarkText = Color(0xFF1F2A17)
private val GrayText = Color(0xFF6B7280)

private val MealPlannerColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = PaleGreenContainer,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = GreenPrimary,
    secondaryContainer = PaleGreenContainer,
    onSecondaryContainer = GreenPrimaryDark,
    background = CreamBackground,
    onBackground = DarkText,
    surface = CardWhite,
    onSurface = DarkText,
    surfaceVariant = CreamBackground,
    onSurfaceVariant = GrayText
)

private val MealPlannerShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MealPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MealPlannerColorScheme,
        shapes = MealPlannerShapes,
        content = content
    )
}
