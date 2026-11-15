package com.meal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.AppSettings
import com.meal.domain.model.CalendarViewMode
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.ThemeMode
import com.meal.domain.service.InvalidBackupException
import com.meal.domain.usecase.ExportBackupUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.ImportBackupUseCase
import com.meal.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val state = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    themeMode = settings.themeMode,
                    language = settings.language,
                    currency = settings.currency,
                    lockPastDates = settings.lockPastDates,
                    defaultCalendarView = settings.defaultCalendarView,
                    readAloudEnabled = settings.readAloudEnabled,
                    defaultRecipeView = settings.defaultRecipeView,
                    isLoading = false
                )
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) = persist { it.copy(themeMode = mode) }
    fun setLanguage(language: Language) = persist { it.copy(language = language) }
    fun setCurrency(currency: Currency) = persist { it.copy(currency = currency) }
    fun setLockPastDates(enabled: Boolean) = persist { it.copy(lockPastDates = enabled) }
    fun setDefaultCalendarView(view: CalendarViewMode) = persist { it.copy(defaultCalendarView = view) }
    fun setDefaultRecipeView(view: RecipeViewMode) = persist { it.copy(defaultRecipeView = view) }
    fun setReadAloudEnabled(enabled: Boolean) = persist { it.copy(readAloudEnabled = enabled) }

    fun exportBackup(destinationUri: String) = runBackup(successStatus = BackupStatus.EXPORTED) {
        exportBackupUseCase.execute(destinationUri)
    }

    /** On success the app restarts with the restored data, so no success status is shown. */
    fun importBackup(sourceUri: String) = runBackup(successStatus = BackupStatus.IDLE) {
        importBackupUseCase.execute(sourceUri)
    }

    fun clearBackupStatus() {
        _uiState.value = _uiState.value.copy(backupStatus = BackupStatus.IDLE)
    }

    private fun runBackup(successStatus: BackupStatus, action: suspend () -> Unit) {
        if (_uiState.value.backupStatus == BackupStatus.WORKING) return
        _uiState.value = _uiState.value.copy(backupStatus = BackupStatus.WORKING)
        viewModelScope.launch {
            val status = try {
                action()
                successStatus
            } catch (e: CancellationException) {
                throw e
            } catch (e: InvalidBackupException) {
                BackupStatus.INVALID_FILE
            } catch (e: Exception) {
                BackupStatus.FAILED
            }
            _uiState.value = _uiState.value.copy(backupStatus = status)
        }
    }

    private fun persist(transform: (AppSettings) -> AppSettings) {
        val current = _uiState.value
        val updated = transform(
            AppSettings(
                themeMode = current.themeMode,
                language = current.language,
                currency = current.currency,
                lockPastDates = current.lockPastDates,
                defaultCalendarView = current.defaultCalendarView,
                readAloudEnabled = current.readAloudEnabled,
                defaultRecipeView = current.defaultRecipeView
            )
        )
        _uiState.value = current.copy(
            themeMode = updated.themeMode,
            language = updated.language,
            currency = updated.currency,
            lockPastDates = updated.lockPastDates,
            defaultCalendarView = updated.defaultCalendarView,
            readAloudEnabled = updated.readAloudEnabled,
            defaultRecipeView = updated.defaultRecipeView
        )
        viewModelScope.launch { updateSettingsUseCase.execute(updated) }
    }
}
