package com.meal.domain.usecase

import com.meal.domain.model.SpendingBucket
import com.meal.domain.model.SpendingLineItem
import com.meal.domain.model.SpendingReport
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private const val DAY_BUCKET_THRESHOLD_DAYS = 14

class GetSpendingReportUseCase(
    private val repository: ShoppingListRepository
) {
    fun execute(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<SpendingReport> =
        repository.getPricedItems(rangeStart, rangeEnd).map { items -> build(rangeStart, rangeEnd, items) }

    private fun build(rangeStart: LocalDate, rangeEnd: LocalDate, items: List<SpendingLineItem>): SpendingReport {
        val spanDays = rangeStart.daysUntil(rangeEnd) + 1
        val buckets = if (spanDays <= DAY_BUCKET_THRESHOLD_DAYS) {
            bucketByDay(rangeStart, rangeEnd, items)
        } else {
            bucketByWeek(rangeStart, rangeEnd, items)
        }
        return SpendingReport(
            rangeStart = rangeStart,
            rangeEnd = rangeEnd,
            totalSpent = items.sumOf { it.price },
            buckets = buckets,
            lineItems = items.sortedBy { it.rangeStart }
        )
    }

    // An item only carries the meal-plan week it belongs to, not a real purchase date. rangeStart
    // (clamped into the requested window) is the best available proxy for "when this was shopped for,"
    // and attributing the full price to one bucket keeps bucket amounts summing exactly to totalSpent.
    private fun bucketDateOf(item: SpendingLineItem, rangeStart: LocalDate, rangeEnd: LocalDate): LocalDate =
        item.rangeStart.coerceIn(rangeStart, rangeEnd)

    private fun bucketByDay(rangeStart: LocalDate, rangeEnd: LocalDate, items: List<SpendingLineItem>): List<SpendingBucket> {
        val totalsByDay = items.groupBy { bucketDateOf(it, rangeStart, rangeEnd) }
            .mapValues { (_, bucketItems) -> bucketItems.sumOf { it.price } }
        return generateSequence(rangeStart) { it.plus(1, DateTimeUnit.DAY) }
            .takeWhile { it <= rangeEnd }
            .map { day -> SpendingBucket(bucketStart = day, bucketEnd = day, amount = totalsByDay[day] ?: 0.0) }
            .toList()
    }

    private fun bucketByWeek(rangeStart: LocalDate, rangeEnd: LocalDate, items: List<SpendingLineItem>): List<SpendingBucket> {
        val totalsByWeek = items.groupBy { mondayOf(bucketDateOf(it, rangeStart, rangeEnd)) }
            .mapValues { (_, bucketItems) -> bucketItems.sumOf { it.price } }
        return generateSequence(mondayOf(rangeStart)) { it.plus(7, DateTimeUnit.DAY) }
            .takeWhile { it <= rangeEnd }
            .map { weekStart ->
                SpendingBucket(bucketStart = weekStart, bucketEnd = weekStart.plus(6, DateTimeUnit.DAY), amount = totalsByWeek[weekStart] ?: 0.0)
            }
            .toList()
    }

    // kotlinx.datetime.DayOfWeek is declared in ISO order starting at MONDAY, so ordinal is days-since-Monday.
    private fun mondayOf(date: LocalDate): LocalDate = date.minus(date.dayOfWeek.ordinal, DateTimeUnit.DAY)
}
