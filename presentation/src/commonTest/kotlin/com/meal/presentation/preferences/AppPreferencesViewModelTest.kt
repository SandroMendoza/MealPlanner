package com.meal.presentation.preferences

import com.meal.domain.model.AppSettings
import com.meal.domain.model.ThemeMode
import com.meal.domain.model.Language
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetWalkthroughSeenUseCase
import com.meal.domain.usecase.MarkWalkthroughSeenUseCase
import com.meal.presentation.fakes.FakeOnboardingRepository
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppPreferencesViewModelTest {

    private val repository = FakeSettingsRepository(AppSettings(themeMode = ThemeMode.DARK, language = Language.SPANISH))
    private val onboarding = FakeOnboardingRepository()

    private fun viewModel() = AppPreferencesViewModel(
        GetSettingsUseCase(repository),
        GetWalkthroughSeenUseCase(onboarding),
        MarkWalkthroughSeenUseCase(onboarding)
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun valuesAreUnknownUntilCollected() {
        val vm = viewModel()

        assertNull(vm.themeMode.value)
        assertNull(vm.language.value)
    }

    @Test
    fun exposesAndFollowsThemeModeAndLanguage() = runTest {
        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.themeMode.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.language.collect {} }

        assertEquals(ThemeMode.DARK, vm.themeMode.value)
        assertEquals(Language.SPANISH, vm.language.value)

        repository.settings.value = AppSettings(themeMode = ThemeMode.LIGHT, language = Language.FRENCH)

        assertEquals(ThemeMode.LIGHT, vm.themeMode.value)
        assertEquals(Language.FRENCH, vm.language.value)
    }

    @Test
    fun walkthroughShowsUntilItIsFinished() = runTest {
        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.showWalkthrough.collect {} }

        assertTrue(vm.showWalkthrough.value == true)

        vm.finishWalkthrough()

        assertFalse(vm.showWalkthrough.value == true)
        assertTrue(onboarding.walkthroughSeen.value)
    }

    @Test
    fun walkthroughIsNotShownOnceSeen() = runTest {
        onboarding.walkthroughSeen.value = true
        val vm = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.showWalkthrough.collect {} }

        assertEquals(false, vm.showWalkthrough.value)
    }
}
