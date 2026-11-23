package com.meal.domain.strings

import com.meal.domain.model.Language
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalizedStringsTest {

    private val allStrings = Language.entries.associateWith { it.strings() }

    private fun <T> assertDistinctNonBlankNames(what: String, values: List<T>, name: (Strings, T) -> String) {
        allStrings.forEach { (language, strings) ->
            val names = values.map { name(strings, it) }
            assertTrue(names.none { it.isBlank() }, "$language has a blank $what name: $names")
            assertEquals(names.size, names.toSet().size, "$language repeats a $what name: $names")
        }
    }

    @Test
    fun everyWeekdayHasADistinctShortName() =
        assertDistinctNonBlankNames("weekday", DayOfWeek.entries) { s, day -> s.weekdayShort(day) }

    @Test
    fun everyWeekdayHasAnInitial() {
        allStrings.forEach { (language, strings) ->
            DayOfWeek.entries.forEach { day ->
                assertTrue(strings.weekdayInitial(day).isNotBlank(), "$language has no initial for $day")
            }
        }
    }

    @Test
    fun everyMonthHasADistinctAbbreviation() =
        assertDistinctNonBlankNames("month", (1..12).toList()) { s, month -> s.monthAbbreviation(month) }

    @Test
    fun everyMealTypeHasADistinctName() =
        assertDistinctNonBlankNames("meal type", MealType.entries) { s, type -> s.mealTypeName(type) }

    @Test
    fun everyFoodCategoryHasADistinctName() =
        assertDistinctNonBlankNames("food category", FoodCategory.entries) { s, category -> s.foodCategoryName(category) }

    @Test
    fun everyCuisineHasADistinctName() =
        assertDistinctNonBlankNames("cuisine", Cuisine.entries) { s, cuisine -> s.cuisineName(cuisine) }

    @Test
    fun everyMeasurementUnitNameShowsItsSymbolUnlessTheNameIsTheSymbol() {
        assertDistinctNonBlankNames("measurement unit", MeasurementUnit.entries) { s, unit -> s.measurementUnitName(unit) }
        allStrings.forEach { (language, strings) ->
            MeasurementUnit.entries.filter { it != MeasurementUnit.TO_TASTE }.forEach { unit ->
                val name = strings.measurementUnitName(unit)
                assertTrue(
                    name.endsWith("(${unit.unit})") || name.equals(unit.unit, ignoreCase = true),
                    "$language name for $unit neither ends with (${unit.unit}) nor is ${unit.unit}"
                )
            }
        }
    }

    @Test
    fun englishFormatsCountsAndUnits() {
        val en = Language.ENGLISH.strings()

        assertEquals("1 serving", en.servingsLabel(1))
        assertEquals("4 servings", en.servingsLabel(4))
        assertEquals("45 min", en.minutesLabel(45))
        assertEquals("Week 46", en.calendarWeekNumber(46))
        assertEquals("Grams (g)", en.measurementUnitName(MeasurementUnit.GRAMS))
        assertEquals("To taste", en.measurementUnitName(MeasurementUnit.TO_TASTE))
        assertEquals("Clove", en.measurementUnitName(MeasurementUnit.CLOVE))
        assertEquals("Prep time: 10–30 min", en.recipesPrepTimeRange(10, 30))
        assertEquals("Prep time: 10–180+ min", en.recipesPrepTimeRangeNoMax(10, 180))
        assertEquals("Good morning, Ana", en.homeGreetingWithName("Ana"))
        assertEquals("Dinner reminder", en.mealReminderNotificationTitle(MealType.DINNER))
    }

    @Test
    fun formattedMessagesIncludeTheirValuesInEveryLanguage() {
        allStrings.forEach { (language, strings) ->
            val deleteBody = strings.recipeDetailDeleteBody(3, "Nov 20, Nov 27")
            assertTrue("3" in deleteBody && "Nov 20, Nov 27" in deleteBody, "$language delete message: $deleteBody")
            assertTrue("Ana" in strings.homeGreetingWithName("Ana"), "$language greeting drops the name")
            assertTrue("Garlic" in strings.shoppingRemoveItemBody("Garlic"), "$language remove message drops the item")
            assertTrue("Produce" in strings.shoppingDeleteGroupBody("Produce"), "$language delete group message drops the group")
            assertTrue("₱" in strings.recordPricePriceLabel("₱"), "$language price label drops the currency")
            assertTrue("12.50" in strings.spendingPdfTotalSpent("12.50"), "$language total drops the amount")
            assertTrue(
                strings.mealTypeName(MealType.LUNCH).lowercase() in strings.mealReminderNotificationTitle(MealType.LUNCH).lowercase(),
                "$language reminder title does not name the meal"
            )
        }
    }
}
