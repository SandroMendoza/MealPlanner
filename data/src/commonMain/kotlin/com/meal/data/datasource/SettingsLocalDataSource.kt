package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.db.SettingsEntity
import com.meal.domain.model.AppSettings
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val queries = database.settingsQueries

    fun settings(): Flow<AppSettings> =
        queries.selectSettings()
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { row -> row?.let { toDomain(it) } ?: AppSettings() }

    suspend fun update(settings: AppSettings) =
        withContext(ioDispatcher) {
            queries.upsertSettings(
                if (settings.notificationsEnabled) 1L else 0L,
                if (settings.darkModeEnabled) 1L else 0L,
                settings.language.name,
                settings.currency.name
            )
        }

    private fun toDomain(row: SettingsEntity) = AppSettings(
        notificationsEnabled = row.notificationsEnabled != 0L,
        darkModeEnabled = row.darkModeEnabled != 0L,
        language = Language.valueOf(row.language),
        currency = Currency.valueOf(row.currency)
    )
}
