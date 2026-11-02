package com.meal.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.usecase.GetSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(
    getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    val darkModeEnabled: StateFlow<Boolean?> =
        getSettingsUseCase.execute()
            .map { it.darkModeEnabled }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
