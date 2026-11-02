package com.meal.planner.strings

import androidx.compose.runtime.staticCompositionLocalOf
import com.meal.domain.strings.Strings
import com.meal.domain.strings.englishStrings

val LocalStrings = staticCompositionLocalOf<Strings> { englishStrings }
