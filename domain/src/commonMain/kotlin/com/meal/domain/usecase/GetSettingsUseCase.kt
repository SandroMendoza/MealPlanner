package com.meal.domain.usecase

import com.meal.domain.repository.SettingsRepository

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {
    fun execute() = repository.getSettings()
}
