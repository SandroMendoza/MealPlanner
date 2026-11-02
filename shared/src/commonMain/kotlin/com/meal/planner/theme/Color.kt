package com.meal.planner.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Raw color palette extracted from the Meal Planner UI kit.
 * Prefer referencing semantic roles (MaterialTheme.colorScheme.*) in screens;
 * these raw tokens exist for building the scheme and for the rare case
 * a screen needs a color with no Material3 role (e.g. favorite/heart red).
 */
object MealPlannerPalette {
    // Greens (brand / primary actions)
    val GreenPrimary = Color(0xFF4B7A33)
    val GreenPrimaryDark = Color(0xFF2E4A1F)
    val GreenPrimaryLight = Color(0xFF6B9950)
    val PaleGreenContainer = Color(0xFFE3ECD9)

    // Neutrals
    val CreamBackground = Color(0xFFF5F3EA)
    val CardWhite = Color(0xFFFFFFFF)
    val DarkText = Color(0xFF1F2A17)
    val GrayText = Color(0xFF6B7280)
    val PlaceholderGray = Color(0xFF9CA3AF)
    val DividerGray = Color(0xFFE5E7EB)
    val DisabledGray = Color(0xFFD1D5DB)

    // Status / semantic
    val Error = Color(0xFFD92D20)
    val ErrorContainer = Color(0xFFFBE4E2)
    val Warning = Color(0xFFF5A623)
    val WarningContainer = Color(0xFFFCEFD2)
    val Success = GreenPrimary
    val SuccessContainer = PaleGreenContainer
    val FavoriteRed = Color(0xFFE63946)

    // Dark-mode neutrals (green-tinted near-black, keeps brand warmth instead of pure gray)
    val DarkBackground = Color(0xFF14170F)
    val DarkSurface = Color(0xFF1E2318)
    val DarkSurfaceVariant = Color(0xFF2A3020)
    val DarkOnSurface = Color(0xFFE8E6DD)
    val DarkOnSurfaceVariant = Color(0xFFB8BBAE)
    val DarkOutline = Color(0xFF4A5240)
    val DarkOutlineVariant = Color(0xFF343B2A)
    val DarkErrorContainer = Color(0xFF5C1611)
}

val MealPlannerColorScheme = lightColorScheme(
    primary = MealPlannerPalette.GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = MealPlannerPalette.PaleGreenContainer,
    onPrimaryContainer = MealPlannerPalette.GreenPrimaryDark,
    secondary = MealPlannerPalette.GreenPrimary,
    onSecondary = Color.White,
    secondaryContainer = MealPlannerPalette.PaleGreenContainer,
    onSecondaryContainer = MealPlannerPalette.GreenPrimaryDark,
    tertiary = MealPlannerPalette.Warning,
    onTertiary = Color.White,
    tertiaryContainer = MealPlannerPalette.WarningContainer,
    onTertiaryContainer = MealPlannerPalette.GreenPrimaryDark,
    background = MealPlannerPalette.CreamBackground,
    onBackground = MealPlannerPalette.DarkText,
    surface = MealPlannerPalette.CardWhite,
    onSurface = MealPlannerPalette.DarkText,
    surfaceVariant = MealPlannerPalette.CreamBackground,
    onSurfaceVariant = MealPlannerPalette.GrayText,
    outline = MealPlannerPalette.DividerGray,
    outlineVariant = MealPlannerPalette.DividerGray,
    error = MealPlannerPalette.Error,
    onError = Color.White,
    errorContainer = MealPlannerPalette.ErrorContainer,
    onErrorContainer = MealPlannerPalette.Error
)

val MealPlannerDarkColorScheme = darkColorScheme(
    primary = MealPlannerPalette.GreenPrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = MealPlannerPalette.GreenPrimaryDark,
    onPrimaryContainer = MealPlannerPalette.PaleGreenContainer,
    secondary = MealPlannerPalette.GreenPrimaryLight,
    onSecondary = Color.Black,
    secondaryContainer = MealPlannerPalette.GreenPrimaryDark,
    onSecondaryContainer = MealPlannerPalette.PaleGreenContainer,
    tertiary = MealPlannerPalette.Warning,
    onTertiary = Color.Black,
    tertiaryContainer = MealPlannerPalette.GreenPrimaryDark,
    onTertiaryContainer = MealPlannerPalette.WarningContainer,
    background = MealPlannerPalette.DarkBackground,
    onBackground = MealPlannerPalette.DarkOnSurface,
    surface = MealPlannerPalette.DarkSurface,
    onSurface = MealPlannerPalette.DarkOnSurface,
    surfaceVariant = MealPlannerPalette.DarkSurfaceVariant,
    onSurfaceVariant = MealPlannerPalette.DarkOnSurfaceVariant,
    outline = MealPlannerPalette.DarkOutline,
    outlineVariant = MealPlannerPalette.DarkOutlineVariant,
    error = MealPlannerPalette.Error,
    onError = Color.Black,
    errorContainer = MealPlannerPalette.DarkErrorContainer,
    onErrorContainer = MealPlannerPalette.ErrorContainer
)
