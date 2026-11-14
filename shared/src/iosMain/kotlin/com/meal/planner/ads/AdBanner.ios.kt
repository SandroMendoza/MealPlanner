package com.meal.planner.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// The iOS app has no ad SDK, so it shows no ads and needs no consent form.
@Composable
actual fun AdBanner(placement: AdPlacement, modifier: Modifier) = Unit

@Composable
actual fun rememberAdPrivacyOptions(): (() -> Unit)? = null
