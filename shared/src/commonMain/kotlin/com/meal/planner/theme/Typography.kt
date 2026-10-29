package com.meal.planner.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Font sizes used across the app, matching the UI kit scale.
 * Use these constants when a Material3 TextStyle role doesn't fit
 * (e.g. a one-off caption); otherwise prefer MaterialTheme.typography.*
 */
object MealPlannerFontSize {
    val display = 32.sp
    val headline = 24.sp
    val title = 20.sp
    val subtitle = 18.sp
    val bodyLarge = 16.sp
    val bodyMedium = 14.sp
    val bodySmall = 13.sp
    val caption = 12.sp
    val label = 11.sp
}

private val MealPlannerFontFamily = FontFamily.Default

val MealPlannerTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = MealPlannerFontSize.display,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = MealPlannerFontSize.headline,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = MealPlannerFontSize.title,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = MealPlannerFontSize.subtitle,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = MealPlannerFontSize.bodyLarge,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = MealPlannerFontSize.bodyMedium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = MealPlannerFontSize.bodyLarge,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = MealPlannerFontSize.bodyMedium,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = MealPlannerFontSize.bodySmall,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = MealPlannerFontSize.bodyMedium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = MealPlannerFontSize.caption,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MealPlannerFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = MealPlannerFontSize.label,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    )
)
