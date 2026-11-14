package com.meal.presentation.spending

import com.meal.domain.model.AppSettings
import com.meal.domain.model.Currency
import com.meal.domain.model.SpendingLineItem
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetSpendingReportUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import com.meal.presentation.fakes.FakeSettingsRepository
import com.meal.presentation.fakes.FakeShoppingListRepository
import com.meal.presentation.shopping.ShoppingListRangeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SpendingReportsViewModelTest {

    private val shopping = FakeShoppingListRepository()
    private val settings = FakeSettingsRepository()
    private val weekStart = currentWeekSunday()
    private val weekEnd = weekEndFor(weekStart)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = SpendingReportsViewModel(GetSpendingReportUseCase(shopping), GetSettingsUseCase(settings))

    private fun lineItem(name: String, price: Double) =
        SpendingLineItem(name, name, 1.0, MeasurementUnit.PIECE, price, weekStart, weekEnd)

    @Test
    fun startsOnTheCurrentWeekWithAnEmptyReport() {
        val state = viewModel().state.value

        assertEquals(ShoppingListRangeMode.WEEK, state.mode)
        assertEquals(weekStart to weekEnd, state.rangeStart to state.rangeEnd)
        assertFalse(state.isLoading)
        assertTrue(state.isEmpty)
        assertEquals(0.0, state.report!!.totalSpent)
    }

    @Test
    fun showsTheReportAndFollowsNewPrices() {
        shopping.pricedItems.value = listOf(lineItem("Rice", 5.0))
        val vm = viewModel()
        assertEquals(5.0, vm.state.value.report!!.totalSpent)
        assertFalse(vm.state.value.isEmpty)

        shopping.pricedItems.value = listOf(lineItem("Rice", 5.0), lineItem("Eggs", 3.5))

        assertEquals(8.5, vm.state.value.report!!.totalSpent)
        assertEquals(2, vm.state.value.report!!.lineItems.size)
    }

    @Test
    fun usesTheCurrencySymbolFromSettings() {
        val vm = viewModel()
        assertEquals("$", vm.state.value.currencySymbol)

        settings.settings.value = AppSettings(currency = Currency.GBP)

        assertEquals("£", vm.state.value.currencySymbol)
    }

    @Test
    fun loadFailureShowsAnError() {
        shopping.loadFailure = IllegalStateException("db gone")

        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertTrue(state.errorMessage!!.contains("db gone"))
        assertFalse(state.isEmpty)
    }

    @Test
    fun navigationRequestsTheMatchingRange() {
        val vm = viewModel()

        vm.nextWeek()
        assertEquals(weekStart.plus(7, DateTimeUnit.DAY) to weekEnd.plus(7, DateTimeUnit.DAY), shopping.pricedRanges.last())
        vm.previousWeek()
        assertEquals(weekStart to weekEnd, shopping.pricedRanges.last())

        vm.selectMode(ShoppingListRangeMode.DAY)
        assertEquals(today() to today(), shopping.pricedRanges.last())
        vm.nextDay()
        assertEquals(today().plus(1, DateTimeUnit.DAY), vm.state.value.rangeStart)
        vm.previousDay()
        assertEquals(today(), vm.state.value.rangeStart)

        vm.selectMode(ShoppingListRangeMode.RANGE)
        assertEquals(weekStart to weekEnd, vm.state.value.rangeStart to vm.state.value.rangeEnd)
        vm.selectRange(LocalDate(2030, 2, 10), LocalDate(2030, 1, 1))
        assertEquals(LocalDate(2030, 1, 1) to LocalDate(2030, 2, 10), shopping.pricedRanges.last())
    }

    @Test
    fun goToDateAndTodayDependOnTheMode() {
        val vm = viewModel()
        val target = LocalDate(2030, 5, 15)

        vm.goToDate(target)
        assertEquals(LocalDate(2030, 5, 12) to LocalDate(2030, 5, 18), vm.state.value.rangeStart to vm.state.value.rangeEnd)
        vm.goToToday()
        assertEquals(weekStart, vm.state.value.rangeStart)

        vm.selectMode(ShoppingListRangeMode.DAY)
        vm.goToDate(target)
        assertEquals(target, vm.state.value.rangeStart)
        vm.goToToday()
        assertEquals(today(), vm.state.value.rangeStart)

        vm.selectRange(LocalDate(2030, 1, 1), LocalDate(2030, 1, 10))
        vm.goToDate(target)
        vm.goToToday()
        assertEquals(LocalDate(2030, 1, 1) to LocalDate(2030, 1, 10), vm.state.value.rangeStart to vm.state.value.rangeEnd)
    }

    @Test
    fun selectingTheCurrentModeAgainDoesNothing() {
        val vm = viewModel()
        vm.nextWeek()

        vm.selectMode(ShoppingListRangeMode.WEEK)

        assertEquals(weekStart.plus(7, DateTimeUnit.DAY), vm.state.value.rangeStart)
    }
}
