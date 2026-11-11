package com.meal.data.repository

import com.meal.data.datasource.WeekNoteLocalDataSource
import com.meal.domain.repository.WeekNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class WeekNoteRepositoryImpl(
    private val localDataSource: WeekNoteLocalDataSource
) : WeekNoteRepository {

    override fun getNote(weekStart: LocalDate): Flow<String> = localDataSource.note(weekStart)

    override suspend fun updateNote(weekStart: LocalDate, note: String) {
        localDataSource.update(weekStart, note)
    }
}
