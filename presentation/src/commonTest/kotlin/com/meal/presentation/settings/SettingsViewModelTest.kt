package com.meal.presentation.settings

import com.meal.domain.model.AppSettings
import com.meal.domain.model.CalendarViewMode
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.UpdateSettingsUseCase
import com.meal.presentation.fakes.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val stored = AppSettings(
        darkModeEnabled = true,
        language = Language.FRENCH,
        currency = Currency.PHP,
        lockPastDates = true,
        defaultCalendarView = CalendarViewMode.WEEK,
        readAloudEnabled = true,
        defaultRecipeView = RecipeViewMode.LIST
    )
    private val repository = FakeSettingsRepository(stored)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = SettingsViewModel(GetSettingsUseCase(repository), UpdateSettingsUseCase(repository))

    @Test
    fun loadsStoredSettings() {
        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertEquals(true, state.darkModeEnabled)
        assertEquals(Language.FRENCH, state.language)
        assertEquals(Currency.PHP, state.currency)
        assertEquals(true, state.lockPastDates)
        assertEquals(CalendarViewMode.WEEK, state.defaultCalendarView)
        assertEquals(true, state.readAloudEnabled)
        assertEquals(RecipeViewMode.LIST, state.defaultRecipeView)
    }

    @Test
    fun eachSetterPersistsOnlyItsOwnField() {
        val vm = viewModel()

        vm.setDarkModeEnabled(false)
        assertEquals(stored.copy(darkModeEnabled = false), repository.updates.last())

        vm.setLanguage(Language.SPANISH)
        assertEquals(stored.copy(darkModeEnabled = false, language = Language.SPANISH), repository.updates.last())

        vm.setCurrency(Currency.EUR)
        vm.setLockPastDates(false)
        vm.setDefaultCalendarView(CalendarViewMode.DAY)
        vm.setDefaultRecipeView(RecipeViewMode.GRID)
        vm.setReadAloudEnabled(false)

        assertEquals(
            AppSettings(
                darkModeEnabled = false,
                language = Language.SPANISH,
                currency = Currency.EUR,
                lockPastDates = false,
                defaultCalendarView = CalendarViewMode.DAY,
                readAloudEnabled = false,
                defaultRecipeView = RecipeViewMode.GRID
            ),
            repository.updates.last()
        )
        assertEquals(7, repository.updates.size)
        assertEquals(Currency.EUR, vm.state.value.currency)
    }
}
