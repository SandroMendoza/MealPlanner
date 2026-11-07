package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.db.MealReminderEntity
import com.meal.domain.model.MealReminder
import com.meal.domain.model.defaultReminderTime
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalTime

class MealReminderLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val queries = database.mealReminderQueries

    fun reminders(): Flow<List<MealReminder>> =
        queries.selectReminders()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> ensureAllMealTypesPresent(rows) }

    suspend fun update(reminder: MealReminder) = withContext(ioDispatcher) {
        queries.upsertReminder(
            reminder.mealType.name,
            if (reminder.enabled) 1L else 0L,
            reminder.time.hour.toLong(),
            reminder.time.minute.toLong()
        )
    }

    private suspend fun ensureAllMealTypesPresent(rows: List<MealReminderEntity>): List<MealReminder> {
        val byMealType = rows.associateBy { MealType.valueOf(it.mealType) }
        val missing = MealType.entries.filter { it !in byMealType }
        if (missing.isNotEmpty()) {
            withContext(ioDispatcher) {
                missing.forEach { mealType ->
                    val defaultTime = mealType.defaultReminderTime()
                    queries.upsertReminder(mealType.name, 0L, defaultTime.hour.toLong(), defaultTime.minute.toLong())
                }
            }
        }
        return MealType.entries.map { mealType ->
            byMealType[mealType]?.toDomain() ?: MealReminder(mealType, enabled = false, time = mealType.defaultReminderTime())
        }
    }

    private fun MealReminderEntity.toDomain() = MealReminder(
        mealType = MealType.valueOf(mealType),
        enabled = enabled != 0L,
        time = LocalTime(hour.toInt(), minute.toInt())
    )
}
