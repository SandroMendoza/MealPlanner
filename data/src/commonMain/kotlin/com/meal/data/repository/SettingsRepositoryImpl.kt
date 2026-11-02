package com.meal.data.repository

import com.meal.data.datasource.SettingsLocalDataSource
import com.meal.domain.model.AppSettings
import com.meal.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> = localDataSource.settings()

    override suspend fun updateSettings(settings: AppSettings) {
        localDataSource.update(settings)
    }
}
