package com.meal.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.Language
import com.meal.domain.model.ThemeMode
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetWalkthroughSeenUseCase
import com.meal.domain.usecase.MarkWalkthroughSeenUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppPreferencesViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    getWalkthroughSeenUseCase: GetWalkthroughSeenUseCase,
    private val markWalkthroughSeenUseCase: MarkWalkthroughSeenUseCase
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode?> =
        getSettingsUseCase.execute()
            .map { it.themeMode }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val language: StateFlow<Language?> =
        getSettingsUseCase.execute()
            .map { it.language }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Whether the first-launch walkthrough is due; null until that is known. */
    val showWalkthrough: StateFlow<Boolean?> =
        getWalkthroughSeenUseCase.execute()
            .map { seen -> !seen }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun finishWalkthrough() {
        viewModelScope.launch { markWalkthroughSeenUseCase.execute() }
    }
}
