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

fun currentWeekMonday(): LocalDate {
    val todayDate = today()
    // kotlinx.datetime.DayOfWeek is declared in ISO order starting at MONDAY, so ordinal is days-since-Monday.
    return todayDate.minus(todayDate.dayOfWeek.ordinal, DateTimeUnit.DAY)
}

fun weekEndFor(weekStart: LocalDate): LocalDate = weekStart.plus(6, DateTimeUnit.DAY)
