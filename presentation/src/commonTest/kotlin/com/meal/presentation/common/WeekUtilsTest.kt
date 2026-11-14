package com.meal.presentation.common

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.test.Test
import kotlin.test.assertEquals

class WeekUtilsTest {

    @Test
    fun sundayOfReturnsThePrecedingSundayForEveryDayOfTheWeek() {
        val sunday = LocalDate(2026, 11, 8)
        (0..6).forEach { offset ->
            val day = LocalDate(2026, 11, 8 + offset)
            assertEquals(sunday, sundayOf(day), "sundayOf($day)")
        }
    }

    @Test
    fun sundayOfCrossesMonthAndYearBoundaries() {
        assertEquals(LocalDate(2025, 12, 28), sundayOf(LocalDate(2026, 1, 1)))
        assertEquals(LocalDate(2026, 2, 22), sundayOf(LocalDate(2026, 3, 1).minus(1, DateTimeUnit.DAY)))
    }

    @Test
    fun weekEndForIsSixDaysAfterTheStart() {
        assertEquals(LocalDate(2026, 11, 14), weekEndFor(LocalDate(2026, 11, 8)))
        assertEquals(LocalDate(2027, 1, 2), weekEndFor(LocalDate(2026, 12, 27)))
    }

    @Test
    fun currentWeekSundayIsTheSundayOfToday() {
        val sunday = currentWeekSunday()
        assertEquals(DayOfWeek.SUNDAY, sunday.dayOfWeek)
        assertEquals(sundayOf(today()), sunday)
    }

    @Test
    fun isoWeekNumberFollowsIso8601() {
        assertEquals(1, isoWeekNumber(LocalDate(2026, 1, 1)))
        assertEquals(53, isoWeekNumber(LocalDate(2026, 12, 31)))
        assertEquals(53, isoWeekNumber(LocalDate(2027, 1, 1)))
        assertEquals(1, isoWeekNumber(LocalDate(2024, 12, 30)))
        assertEquals(46, isoWeekNumber(LocalDate(2026, 11, 14)))
    }

    @Test
    fun weekNumberForASundayStartedWeekUsesTheFollowingIsoWeek() {
        assertEquals(46, weekNumberFor(LocalDate(2026, 11, 8)))
        assertEquals(1, weekNumberFor(LocalDate(2025, 12, 28)))
    }
}
