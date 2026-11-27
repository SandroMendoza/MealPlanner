package com.meal.presentation.common

import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.strings.englishStrings
import com.meal.domain.strings.frenchStrings
import com.meal.domain.strings.spanishStrings
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

    @Test
    fun amountIsTheQuantityFollowedByTheUnit() {
        assertEquals("2 cloves", formatAmount(2.0, MeasurementUnit.CLOVE, englishStrings))
        assertEquals("0.5 tsp", formatAmount(0.5, MeasurementUnit.TEASPOON, englishStrings))
    }

    @Test
    fun unitAgreesWithTheQuantityAsDisplayed() {
        assertEquals("1 piece", formatAmount(1.0, MeasurementUnit.PIECE, englishStrings))
        assertEquals("1 piece", formatAmount(0.999, MeasurementUnit.PIECE, englishStrings))
        assertEquals("8 pieces", formatAmount(8.0, MeasurementUnit.PIECE, englishStrings))
        assertEquals("1.5 cups", formatAmount(1.5, MeasurementUnit.CUP, englishStrings))
        assertEquals("2 leaves", formatAmount(2.0, MeasurementUnit.LEAF, englishStrings))
        assertEquals("500 g", formatAmount(500.0, MeasurementUnit.GRAMS, englishStrings))
    }

    @Test
    fun unitIsInTheAppLanguage() {
        assertEquals("1.5 tasse", formatAmount(1.5, MeasurementUnit.CUP, frenchStrings))
        assertEquals("8 pièces", formatAmount(8.0, MeasurementUnit.PIECE, frenchStrings))
        assertEquals("2 c. à s.", formatAmount(2.0, MeasurementUnit.TABLESPOON, frenchStrings))
        assertEquals("1 pieza", formatAmount(1.0, MeasurementUnit.PIECE, spanishStrings))
        assertEquals("3 dientes", formatAmount(3.0, MeasurementUnit.CLOVE, spanishStrings))
        assertEquals("250 g", formatAmount(250.0, MeasurementUnit.GRAMS, spanishStrings))
    }

    @Test
    fun missingMeasurementIsEmpty() {
        assertEquals("", formatAmount(null, null, englishStrings))
        assertEquals("", formatAmount(2.0, null, englishStrings))
    }

    @Test
    fun toTasteAmountHasNoNumber() {
        assertEquals("to taste", formatAmount(1.0, MeasurementUnit.TO_TASTE, englishStrings))
        assertEquals("au goût", formatAmount(1.0, MeasurementUnit.TO_TASTE, frenchStrings))
    }
}
