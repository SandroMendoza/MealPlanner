package com.meal.domain.usecase

import com.meal.domain.fakes.FakeShoppingListRepository
import com.meal.domain.model.SpendingBucket
import com.meal.domain.model.SpendingLineItem
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSpendingReportUseCaseTest {

    private val repository = FakeShoppingListRepository()
    private val useCase = GetSpendingReportUseCase(repository)

    private fun item(key: String, price: Double, weekStart: LocalDate) = SpendingLineItem(
        itemKey = key,
        name = key,
        totalQuantity = 1.0,
        unit = MeasurementUnit.PIECE,
        price = price,
        rangeStart = weekStart,
        rangeEnd = weekStart.plus(6, DateTimeUnit.DAY)
    )

    @Test
    fun anEmptyRangeReportsZeroWithAZeroBucketPerDay() = runTest {
        val report = useCase.execute(LocalDate(2026, 11, 8), LocalDate(2026, 11, 14)).first()

        assertEquals(0.0, report.totalSpent)
        assertEquals(7, report.buckets.size)
        assertTrue(report.buckets.all { it.amount == 0.0 })
        assertTrue(report.lineItems.isEmpty())
    }

    @Test
    fun aShortRangeIsBucketedByDayAndClampsEarlierItemsToTheFirstDay() = runTest {
        val start = LocalDate(2026, 11, 8)
        val end = LocalDate(2026, 11, 14)
        repository.pricedItems.value = listOf(
            item("garlic", 10.0, start),
            item("rice", 5.0, LocalDate(2026, 11, 10)),
            item("eggs", 2.5, LocalDate(2026, 11, 1))
        )

        val report = useCase.execute(start, end).first()

        assertEquals(17.5, report.totalSpent)
        assertEquals((8..14).map { LocalDate(2026, 11, it) }, report.buckets.map { it.bucketStart })
        assertTrue(report.buckets.all { it.bucketStart == it.bucketEnd })
        assertEquals(12.5, report.buckets.first { it.bucketStart == start }.amount)
        assertEquals(5.0, report.buckets.first { it.bucketStart == LocalDate(2026, 11, 10) }.amount)
        assertEquals(report.totalSpent, report.buckets.sumOf { it.amount })
    }

    @Test
    fun clampsLaterItemsToTheLastDay() = runTest {
        val start = LocalDate(2026, 11, 8)
        val end = LocalDate(2026, 11, 14)
        repository.pricedItems.value = listOf(item("garlic", 4.0, LocalDate(2026, 11, 20)))

        val report = useCase.execute(start, end).first()

        assertEquals(4.0, report.buckets.last().amount)
        assertEquals(end, report.buckets.last().bucketStart)
    }

    @Test
    fun aFourteenDayRangeIsStillBucketedByDay() = runTest {
        val report = useCase.execute(LocalDate(2026, 11, 1), LocalDate(2026, 11, 14)).first()

        assertEquals(14, report.buckets.size)
        assertTrue(report.buckets.all { it.bucketStart == it.bucketEnd })
    }

    @Test
    fun aRangeLongerThanFourteenDaysIsBucketedBySundayStartingWeeks() = runTest {
        val start = LocalDate(2026, 11, 4)
        val end = LocalDate(2026, 11, 30)
        repository.pricedItems.value = listOf(
            item("garlic", 10.0, LocalDate(2026, 10, 25)),
            item("rice", 6.0, LocalDate(2026, 11, 18)),
            item("eggs", 3.0, LocalDate(2026, 12, 6))
        )

        val report = useCase.execute(start, end).first()

        assertEquals(
            listOf(1, 8, 15, 22, 29).map { day ->
                val weekStart = LocalDate(2026, 11, day)
                weekStart to weekStart.plus(6, DateTimeUnit.DAY)
            },
            report.buckets.map { it.bucketStart to it.bucketEnd }
        )
        assertEquals(
            listOf(10.0, 0.0, 6.0, 0.0, 3.0),
            report.buckets.map(SpendingBucket::amount)
        )
        assertEquals(19.0, report.totalSpent)
        assertEquals(report.totalSpent, report.buckets.sumOf { it.amount })
    }

    @Test
    fun listsLineItemsOldestWeekFirst() = runTest {
        repository.pricedItems.value = listOf(
            item("late", 1.0, LocalDate(2026, 11, 15)),
            item("early", 1.0, LocalDate(2026, 11, 1)),
            item("middle", 1.0, LocalDate(2026, 11, 8))
        )

        val report = useCase.execute(LocalDate(2026, 11, 1), LocalDate(2026, 11, 21)).first()

        assertEquals(listOf("early", "middle", "late"), report.lineItems.map { it.itemKey })
    }

    @Test
    fun reportsTheRequestedRange() = runTest {
        val start = LocalDate(2026, 11, 1)
        val end = LocalDate(2026, 11, 30)

        val report = useCase.execute(start, end).first()

        assertEquals(start, report.rangeStart)
        assertEquals(end, report.rangeEnd)
    }
}
