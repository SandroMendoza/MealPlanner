package com.meal.planner.common

fun formatPrice(price: Double): String {
    val cents = (price * 100).toLong()
    val whole = cents / 100
    val fraction = (cents % 100).let { if (it < 0) -it else it }
    return "$whole.${fraction.toString().padStart(2, '0')}"
}
