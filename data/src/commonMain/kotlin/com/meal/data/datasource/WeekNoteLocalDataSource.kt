package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.meal.data.db.MealPlannerDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class WeekNoteLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database.weekNoteQueries

    fun note(weekStart: LocalDate): Flow<String> =
        queries.selectNoteForWeek(weekStart.toString())
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { row -> row?.note.orEmpty() }

    suspend fun update(weekStart: LocalDate, note: String) =
        withContext(ioDispatcher) {
            queries.upsertNote(weekStart.toString(), note)
        }
}
