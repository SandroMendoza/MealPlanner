package com.meal.planner.common

import androidx.compose.runtime.Composable

/** The version name of the installed build, as set by the platform's build configuration. */
@Composable
expect fun rememberAppVersionName(): String
