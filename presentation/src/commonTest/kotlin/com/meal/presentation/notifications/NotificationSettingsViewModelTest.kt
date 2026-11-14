package com.meal.presentation.notifications

import com.meal.domain.model.MealReminder
import com.meal.domain.model.defaultReminderTime
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetMealRemindersUseCase
import com.meal.domain.usecase.UpdateMealReminderUseCase
import com.meal.presentation.fakes.FakeMealReminderRepository
import com.meal.presentation.fakes.FakeReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest {

    private val storedReminders = MealType.entries.map { MealReminder(it, enabled = false, time = it.defaultReminderTime()) }
    private val repository = FakeMealReminderRepository(storedReminders)
    private val scheduler = FakeReminderScheduler()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = NotificationSettingsViewModel(
        GetMealRemindersUseCase(repository),
        UpdateMealReminderUseCase(repository, scheduler)
    )

    @Test
    fun defaultStateIsLoadingWithEveryMealDisabledAtItsDefaultTime() {
        val initial = NotificationSettingsUiState()
        assertTrue(initial.isLoading)
        assertEquals(MealType.entries, initial.reminders.map { it.mealType })
        assertTrue(initial.reminders.none { it.enabled })
        assertEquals(LocalTime(8, 0), initial.reminders.first { it.mealType == MealType.BREAKFAST }.time)
    }

    @Test
    fun loadsStoredReminders() {
        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertEquals(storedReminders.map { MealReminderUiState(it.mealType, it.enabled, it.time) }, state.reminders)
    }

    @Test
    fun enablingAReminderSavesAndSchedulesIt() {
        val vm = viewModel()

        vm.setEnabled(MealType.LUNCH, true)

        val expected = MealReminder(MealType.LUNCH, enabled = true, time = LocalTime(12, 0))
        assertEquals(expected, repository.updates.single())
        assertEquals(expected, scheduler.scheduled.single())
        assertTrue(vm.state.value.reminders.first { it.mealType == MealType.LUNCH }.enabled)
    }

    @Test
    fun changingTheTimeKeepsTheEnabledFlag() {
        repository.reminders.value = listOf(MealReminder(MealType.DINNER, enabled = true, time = LocalTime(18, 0)))
        val vm = viewModel()

        vm.setTime(MealType.DINNER, LocalTime(19, 30))

        assertEquals(MealReminder(MealType.DINNER, enabled = true, time = LocalTime(19, 30)), repository.updates.single())
        assertEquals(LocalTime(19, 30), vm.state.value.reminders.single().time)
    }
}
