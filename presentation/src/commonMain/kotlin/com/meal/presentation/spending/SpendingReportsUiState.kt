package com.meal.presentation.spending

import com.meal.domain.model.SpendingReport
import com.meal.presentation.shopping.ShoppingListRangeMode
import kotlinx.datetime.LocalDate

data class SpendingReportsUiState(
    val mode: ShoppingListRangeMode,
    val rangeStart: LocalDate,
    val rangeEnd: LocalDate,
    val report: SpendingReport? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && report?.lineItems.isNullOrEmpty()
}
