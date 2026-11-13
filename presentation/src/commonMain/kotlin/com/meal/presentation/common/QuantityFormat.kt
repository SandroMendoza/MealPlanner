package com.meal.presentation.common

import kotlin.math.roundToLong

/**
 * Formats an ingredient quantity for display and read-aloud: whole numbers without a decimal
 * part ("2", not "2.0") and anything else rounded to at most two decimals ("0.33", "1.5").
 * Rounding also hides floating-point noise from summed shopping-list totals.
 */
fun formatQuantity(value: Double): String {
    val rounded = (value * 100).roundToLong() / 100.0
    return if (rounded % 1.0 == 0.0) rounded.roundToLong().toString() else rounded.toString()
}
