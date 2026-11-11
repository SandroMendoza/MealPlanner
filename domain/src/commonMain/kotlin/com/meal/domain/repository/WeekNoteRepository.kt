package com.meal.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface WeekNoteRepository {
    fun getNote(weekStart: LocalDate): Flow<String>
    suspend fun updateNote(weekStart: LocalDate, note: String)
}
