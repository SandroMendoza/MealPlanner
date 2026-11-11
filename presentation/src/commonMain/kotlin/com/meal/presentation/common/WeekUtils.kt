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
