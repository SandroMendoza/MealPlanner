package com.meal.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.Language
import com.meal.domain.model.ThemeMode
import com.meal.domain.usecase.GetSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppPreferencesViewModel(
    getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode?> =
        getSettingsUseCase.execute()
            .map { it.themeMode }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val language: StateFlow<Language?> =
        getSettingsUseCase.execute()
            .map { it.language }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
