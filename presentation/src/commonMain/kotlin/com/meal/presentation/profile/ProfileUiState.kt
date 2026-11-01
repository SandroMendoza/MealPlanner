package com.meal.presentation.profile

data class ProfileUiState(
    val name: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val recipeCount: Int = 0,
    val createdCount: Int = 0,
    val thisWeekCount: Int = 0,
    val isLoading: Boolean = false,
    val showEditDialog: Boolean = false
)
