package com.meal.domain.model.enums

enum class DayPeriod {
    MORNING,
    AFTERNOON,
    EVENING;

    companion object {
        fun ofHour(hour: Int): DayPeriod = when (hour) {
            in 5..11 -> MORNING
            in 12..17 -> AFTERNOON
            else -> EVENING
        }
    }
}
