package com.meal.presentation.preferences

import com.meal.domain.model.AppSettings
import com.meal.domain.model.ThemeMode
import com.meal.domain.model.Language
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.presentation.fakes.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AppPreferencesViewModelTest {

    private val repository = FakeSettingsRepository(AppSettings(themeMode = ThemeMode.DARK, language = Language.SPANISH))

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun valuesAreUnknownUntilCollected() {
        val vm = AppPreferencesViewModel(GetSettingsUseCase(repository))

        assertNull(vm.themeMode.value)
        assertNull(vm.language.value)
    }

    @Test
    fun exposesAndFollowsThemeModeAndLanguage() = runTest {
        val vm = AppPreferencesViewModel(GetSettingsUseCase(repository))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.themeMode.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.language.collect {} }

        assertEquals(ThemeMode.DARK, vm.themeMode.value)
        assertEquals(Language.SPANISH, vm.language.value)

        repository.settings.value = AppSettings(themeMode = ThemeMode.LIGHT, language = Language.FRENCH)

        assertEquals(ThemeMode.LIGHT, vm.themeMode.value)
        assertEquals(Language.FRENCH, vm.language.value)
    }
}
