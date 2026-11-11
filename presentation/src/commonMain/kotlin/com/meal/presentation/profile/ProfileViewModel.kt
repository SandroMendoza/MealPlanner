package com.meal.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.UserProfile
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetUserProfileUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.UpdateUserProfileUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val state = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserProfileUseCase.execute().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    name = profile.name,
                    bio = profile.bio,
                    avatarUrl = profile.avatarUrl
                )
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getAllRecipesUseCase.execute().collect { recipes ->
                _uiState.value = _uiState.value.copy(
                    recipeCount = recipes.size,
                    createdCount = recipes.count { it.isUserCreated },
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            val weekStart = currentWeekSunday()
            getWeeklyMealPlanUseCase.execute(weekStart, weekEndFor(weekStart)).collect { entries ->
                _uiState.value = _uiState.value.copy(thisWeekCount = entries.size)
            }
        }
    }

    fun openEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = true)
    }

    fun dismissEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = false)
    }

    fun saveProfile(name: String, bio: String, avatarUrl: String) {
        viewModelScope.launch {
            updateUserProfileUseCase.execute(UserProfile(name = name, bio = bio, avatarUrl = avatarUrl))
        }
        dismissEditDialog()
    }
}
