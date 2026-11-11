package com.meal.presentation.common

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

fun currentWeekSunday(): LocalDate = sundayOf(today())

// kotlinx.datetime.DayOfWeek is declared in ISO order starting at MONDAY (ordinal 0) through
// SUNDAY (ordinal 6), so (ordinal + 1) % 7 is days-since-Sunday.
fun sundayOf(date: LocalDate): LocalDate = date.minus((date.dayOfWeek.ordinal + 1) % 7, DateTimeUnit.DAY)

fun weekEndFor(weekStart: LocalDate): LocalDate = weekStart.plus(6, DateTimeUnit.DAY)

// ISO-8601: a date's week number is the week number of the Thursday that falls in that week
// (Monday=1..Sunday=7), which is what keeps the first/last week of a year correct.
fun isoWeekNumber(date: LocalDate): Int {
    val isoDayNumber = date.dayOfWeek.ordinal + 1
    val thursday = date.plus(4 - isoDayNumber, DateTimeUnit.DAY)
    return (thursday.dayOfYear - 1) / 7 + 1
}

// The app's week runs Sunday-Saturday, which splits across two ISO (Monday-Sunday) weeks at the
// Sunday boundary. Numbering by the Thursday mid-week attributes the number to the ISO week that
// contains most of the days shown (Monday-Saturday).
fun weekNumberFor(weekStart: LocalDate): Int = isoWeekNumber(weekStart.plus(4, DateTimeUnit.DAY))
