package com.meal.planner.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Screens that show a banner ad. Each has its own ad unit. */
enum class AdPlacement { HOME, MEAL_PLAN }

/** A banner ad for [placement]. It takes no space until an ad has loaded. */
@Composable
expect fun AdBanner(placement: AdPlacement, modifier: Modifier = Modifier)

/**
 * Returns a callback that reopens the ad consent form, or null when the user's region does
 * not require a way to change ad consent.
 */
@Composable
expect fun rememberAdPrivacyOptions(): (() -> Unit)?
