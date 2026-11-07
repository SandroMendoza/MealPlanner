package com.meal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.AppSettings
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val state = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    darkModeEnabled = settings.darkModeEnabled,
                    language = settings.language,
                    currency = settings.currency,
                    isLoading = false
                )
            }
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) = persist { it.copy(darkModeEnabled = enabled) }
    fun setLanguage(language: Language) = persist { it.copy(language = language) }
    fun setCurrency(currency: Currency) = persist { it.copy(currency = currency) }

    private fun persist(transform: (AppSettings) -> AppSettings) {
        val current = _uiState.value
        val updated = transform(
            AppSettings(
                darkModeEnabled = current.darkModeEnabled,
                language = current.language,
                currency = current.currency
            )
        )
        _uiState.value = current.copy(
            darkModeEnabled = updated.darkModeEnabled,
            language = updated.language,
            currency = updated.currency
        )
        viewModelScope.launch { updateSettingsUseCase.execute(updated) }
    }
}
