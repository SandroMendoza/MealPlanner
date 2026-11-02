package com.meal.domain.model

import kotlinx.datetime.LocalDate

data class SpendingBucket(
    val bucketStart: LocalDate,
    val bucketEnd: LocalDate,
    val amount: Double
)

data class SpendingReport(
    val rangeStart: LocalDate,
    val rangeEnd: LocalDate,
    val totalSpent: Double,
    val buckets: List<SpendingBucket>,
    val lineItems: List<SpendingLineItem>
)
