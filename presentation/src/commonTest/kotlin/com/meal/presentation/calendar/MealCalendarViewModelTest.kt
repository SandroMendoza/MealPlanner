package com.meal.presentation.calendar

import com.meal.domain.model.AppSettings
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetWeekNoteUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.RemoveMealSlotUseCase
import com.meal.domain.usecase.UpdateWeekNoteUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import com.meal.presentation.fakes.FakeMealPlanRepository
import com.meal.presentation.fakes.FakeRecipeRepository
import com.meal.presentation.fakes.FakeSettingsRepository
import com.meal.presentation.fakes.FakeWeekNoteRepository
import com.meal.presentation.fakes.recipe
import com.meal.presentation.fakes.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import com.meal.domain.model.CalendarViewMode as DomainCalendarViewMode

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class MealCalendarViewModelTest {

    private val dish = recipe(name = "Adobo")
    private val recipes = FakeRecipeRepository(listOf(dish))
    private val mealPlan = FakeMealPlanRepository()
    private val settings = FakeSettingsRepository()
    private val notes = FakeWeekNoteRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = MealCalendarViewModel(
        GetWeeklyMealPlanUseCase(mealPlan, recipes),
        RemoveMealSlotUseCase(mealPlan),
        GetSettingsUseCase(settings),
        GetWeekNoteUseCase(notes),
        UpdateWeekNoteUseCase(notes)
    )

    @Test
    fun startsOnTheCurrentWeekWithTodaySelected() {
        val state = viewModel().state.value
        val sunday = currentWeekSunday()

        assertEquals(sunday, state.weekStart)
        assertEquals(weekEndFor(sunday), state.weekEnd)
        assertEquals(today(), state.selectedDate)
        assertEquals((0..6).map { sunday.plus(it, DateTimeUnit.DAY) }, state.days)
        assertFalse(state.isLoading)
    }

    @Test
    fun showsOnlyTheDisplayedWeeksEntries() {
        val sunday = currentWeekSunday()
        mealPlan.slots.value = listOf(
            slot(sunday.plus(1, DateTimeUnit.DAY), MealType.LUNCH, dish),
            slot(sunday.plus(7, DateTimeUnit.DAY), MealType.LUNCH, dish)
        )

        val entries = viewModel().state.value.entries

        assertEquals(listOf(sunday.plus(1, DateTimeUnit.DAY)), entries.map { it.date })
    }

    @Test
    fun appliesSettingsAndTheDefaultViewOnlyOnce() {
        settings.settings.value = AppSettings(lockPastDates = true, defaultCalendarView = DomainCalendarViewMode.WEEK)
        val vm = viewModel()
        assertTrue(vm.state.value.lockPastDates)
        assertEquals(CalendarViewMode.WEEK, vm.state.value.viewMode)

        vm.setViewMode(CalendarViewMode.DAY)
        settings.settings.value = AppSettings(lockPastDates = false, defaultCalendarView = DomainCalendarViewMode.WEEK)

        assertFalse(vm.state.value.lockPastDates)
        assertEquals(CalendarViewMode.DAY, vm.state.value.viewMode)
    }

    @Test
    fun loadFailureShowsAnError() {
        mealPlan.loadFailure = IllegalStateException("db closed")

        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertTrue(state.errorMessage!!.contains("db closed"))
    }

    @Test
    fun weekNavigationMovesBySevenDaysAndReloads() {
        val vm = viewModel()
        val sunday = currentWeekSunday()

        vm.nextWeek()
        assertEquals(sunday.plus(7, DateTimeUnit.DAY), vm.state.value.weekStart)
        assertEquals(sunday.plus(7, DateTimeUnit.DAY), vm.state.value.selectedDate)
        assertEquals(sunday.plus(7, DateTimeUnit.DAY) to sunday.plus(13, DateTimeUnit.DAY), mealPlan.requestedRanges.last())

        vm.previousWeek()
        vm.previousWeek()
        assertEquals(sunday.minus(7, DateTimeUnit.DAY), vm.state.value.weekStart)
    }

    @Test
    fun returningToTheCurrentWeekSelectsToday() {
        val vm = viewModel()

        vm.nextWeek()
        vm.previousWeek()

        assertEquals(today(), vm.state.value.selectedDate)
    }

    @Test
    fun goToTodayAndGoToDateJumpToTheRightWeek() {
        val vm = viewModel()
        val target = LocalDate(2030, 5, 15)

        vm.goToDate(target)
        assertEquals(LocalDate(2030, 5, 12), vm.state.value.weekStart)
        assertEquals(target, vm.state.value.selectedDate)

        vm.goToToday()
        assertEquals(currentWeekSunday(), vm.state.value.weekStart)
        assertEquals(today(), vm.state.value.selectedDate)
    }

    @Test
    fun navigationKeepsViewModeAndLockSetting() {
        settings.settings.value = AppSettings(lockPastDates = true)
        val vm = viewModel()
        vm.setViewMode(CalendarViewMode.WEEK)

        vm.nextWeek()

        assertEquals(CalendarViewMode.WEEK, vm.state.value.viewMode)
        assertTrue(vm.state.value.lockPastDates)
    }

    @Test
    fun selectDateChangesOnlyTheSelection() {
        val vm = viewModel()
        val day = currentWeekSunday().plus(3, DateTimeUnit.DAY)

        vm.selectDate(day)

        assertEquals(day, vm.state.value.selectedDate)
        assertEquals(currentWeekSunday(), vm.state.value.weekStart)
    }

    @Test
    fun removeSlotDeletesUnlockedEntries() {
        val tomorrow = today().plus(1, DateTimeUnit.DAY)
        val planned = slot(tomorrow, MealType.DINNER, dish)
        mealPlan.slots.value = listOf(planned)
        val vm = viewModel()

        vm.removeSlot(MealPlanEntry(planned.id, tomorrow, MealType.DINNER, dish))

        assertEquals(listOf(planned.id), mealPlan.removedSlots)
    }

    @Test
    fun removeSlotIgnoresPastEntriesWhenPastDatesAreLocked() {
        settings.settings.value = AppSettings(lockPastDates = true)
        val yesterday = today().minus(1, DateTimeUnit.DAY)
        val planned = slot(yesterday, MealType.DINNER, dish)
        val vm = viewModel()

        vm.removeSlot(MealPlanEntry(planned.id, yesterday, MealType.DINNER, dish))

        assertTrue(mealPlan.removedSlots.isEmpty())
    }

    @Test
    fun weekNoteIsLoadedPerWeekAndSaved() {
        val sunday = currentWeekSunday()
        val nextSunday = sunday.plus(7, DateTimeUnit.DAY)
        notes.notes.value = mapOf(sunday to "Buy rice", nextSunday to "Party")
        val vm = viewModel()
        assertEquals("Buy rice", vm.state.value.weekNote)

        vm.nextWeek()
        assertEquals("Party", vm.state.value.weekNote)

        vm.updateWeekNote("Party at 7")
        assertEquals(nextSunday to "Party at 7", notes.updates.single())
        assertEquals("Party at 7", vm.state.value.weekNote)
    }
}
