package com.meal.domain.usecase

import com.meal.domain.model.AppSettings
import com.meal.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    private val repository: SettingsRepository
) {
    suspend fun execute(settings: AppSettings) = repository.updateSettings(settings)
}
