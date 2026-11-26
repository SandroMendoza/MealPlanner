package com.meal.domain.model

import com.meal.domain.model.enums.DayPeriod
import kotlin.test.Test
import kotlin.test.assertEquals

class DayPeriodTest {

    @Test
    fun hoursMapToTheirPartOfTheDay() {
        assertEquals(DayPeriod.EVENING, DayPeriod.ofHour(0))
        assertEquals(DayPeriod.EVENING, DayPeriod.ofHour(4))
        assertEquals(DayPeriod.MORNING, DayPeriod.ofHour(5))
        assertEquals(DayPeriod.MORNING, DayPeriod.ofHour(11))
        assertEquals(DayPeriod.AFTERNOON, DayPeriod.ofHour(12))
        assertEquals(DayPeriod.AFTERNOON, DayPeriod.ofHour(17))
        assertEquals(DayPeriod.EVENING, DayPeriod.ofHour(18))
        assertEquals(DayPeriod.EVENING, DayPeriod.ofHour(23))
    }
}
