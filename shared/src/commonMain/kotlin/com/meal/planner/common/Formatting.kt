package com.meal.planner.common

import kotlinx.datetime.LocalTime

fun formatPrice(price: Double): String {
    val cents = (price * 100).toLong()
    val whole = cents / 100
    val fraction = (cents % 100).let { if (it < 0) -it else it }
    return "$whole.${fraction.toString().padStart(2, '0')}"
}

fun formatTime(time: LocalTime): String =
    "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
