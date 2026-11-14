package com.meal.presentation.common

import kotlin.test.Test
import kotlin.test.assertEquals

class QuantityFormatTest {

    @Test
    fun wholeNumbersHaveNoDecimalPart() {
        assertEquals("2", formatQuantity(2.0))
        assertEquals("0", formatQuantity(0.0))
        assertEquals("250", formatQuantity(250.0))
    }

    @Test
    fun fractionsAreRoundedToTwoDecimals() {
        assertEquals("0.33", formatQuantity(1.0 / 3.0))
        assertEquals("1.5", formatQuantity(1.5))
        assertEquals("0.67", formatQuantity(2.0 / 3.0))
    }

    @Test
    fun floatingPointNoiseIsHidden() {
        assertEquals("0.3", formatQuantity(0.1 + 0.2))
        assertEquals("3", formatQuantity(2.999))
    }
}
