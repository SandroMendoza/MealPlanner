package com.meal.domain.usecase

import com.meal.domain.repository.WeekNoteRepository
import kotlinx.datetime.LocalDate

class UpdateWeekNoteUseCase(
    private val repository: WeekNoteRepository
) {
    suspend fun execute(weekStart: LocalDate, note: String) = repository.updateNote(weekStart, note)
}
