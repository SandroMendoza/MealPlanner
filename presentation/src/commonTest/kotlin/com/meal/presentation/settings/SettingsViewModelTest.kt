package com.meal.presentation.settings

import com.meal.domain.model.AppSettings
import com.meal.domain.model.ThemeMode
import com.meal.domain.model.CalendarViewMode
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.presentation.fakes.FakeBackupService
import com.meal.domain.usecase.ImportBackupUseCase
import com.meal.domain.usecase.ExportBackupUseCase
import com.meal.domain.service.InvalidBackupException
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
        themeMode = ThemeMode.DARK,
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

    private val backup = FakeBackupService()

    private fun viewModel() = SettingsViewModel(
        GetSettingsUseCase(repository),
        UpdateSettingsUseCase(repository),
        ExportBackupUseCase(backup),
        ImportBackupUseCase(backup)
    )

    @Test
    fun loadsStoredSettings() {
        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertEquals(ThemeMode.DARK, state.themeMode)
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

        vm.setThemeMode(ThemeMode.LIGHT)
        assertEquals(stored.copy(themeMode = ThemeMode.LIGHT), repository.updates.last())

        vm.setLanguage(Language.SPANISH)
        assertEquals(stored.copy(themeMode = ThemeMode.LIGHT, language = Language.SPANISH), repository.updates.last())

        vm.setCurrency(Currency.EUR)
        vm.setLockPastDates(false)
        vm.setDefaultCalendarView(CalendarViewMode.DAY)
        vm.setDefaultRecipeView(RecipeViewMode.GRID)
        vm.setReadAloudEnabled(false)

        assertEquals(
            AppSettings(
                themeMode = ThemeMode.LIGHT,
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

    @Test
    fun exportReportsSuccessAndCanBeCleared() {
        val vm = viewModel()

        vm.exportBackup("content://backup.zip")

        assertEquals(listOf("content://backup.zip"), backup.exported)
        assertEquals(BackupStatus.EXPORTED, vm.state.value.backupStatus)
        vm.clearBackupStatus()
        assertEquals(BackupStatus.IDLE, vm.state.value.backupStatus)
    }

    @Test
    fun exportFailureIsReported() {
        backup.failure = IllegalStateException("disk full")
        val vm = viewModel()

        vm.exportBackup("content://backup.zip")

        assertEquals(BackupStatus.FAILED, vm.state.value.backupStatus)
    }

    @Test
    fun importOfAFileThatIsNotABackupIsReportedSeparately() {
        backup.failure = InvalidBackupException("not a backup")
        val vm = viewModel()

        vm.importBackup("content://photo.zip")

        assertEquals(BackupStatus.INVALID_FILE, vm.state.value.backupStatus)
    }

    @Test
    fun importPassesTheChosenFile() {
        val vm = viewModel()

        vm.importBackup("content://backup.zip")

        assertEquals(listOf("content://backup.zip"), backup.imported)
        assertEquals(BackupStatus.IDLE, vm.state.value.backupStatus)
    }
}
