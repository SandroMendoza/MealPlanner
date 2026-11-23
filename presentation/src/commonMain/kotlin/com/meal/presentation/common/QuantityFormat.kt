package com.meal.presentation.common

import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.strings.Strings
import kotlin.math.roundToLong

/**
 * Formats an ingredient quantity for display and read-aloud: whole numbers without a decimal
 * part ("2", not "2.0") and anything else rounded to at most two decimals ("0.33", "1.5").
 * Rounding also hides floating-point noise from summed shopping-list totals.
 */
fun formatQuantity(value: Double): String {
    val rounded = roundQuantity(value)
    return if (rounded % 1.0 == 0.0) rounded.roundToLong().toString() else rounded.toString()
}

/**
 * Quantity and unit as shown next to an ingredient ("2 cloves", "0.5 tsp"), with the unit in the
 * language of [strings] and agreeing with the quantity as displayed. A [MeasurementUnit.TO_TASTE]
 * amount has no meaningful number, so it shows as "to taste" alone.
 */
fun formatAmount(quantity: Double, unit: MeasurementUnit, strings: Strings): String {
    val label = strings.measurementUnitLabel(unit, roundQuantity(quantity))
    return if (unit == MeasurementUnit.TO_TASTE) label else "${formatQuantity(quantity)} $label"
}

private fun roundQuantity(value: Double): Double = (value * 100).roundToLong() / 100.0
