package com.meal.domain.usecase

import com.meal.domain.repository.WeekNoteRepository
import kotlinx.datetime.LocalDate

class GetWeekNoteUseCase(
    private val repository: WeekNoteRepository
) {
    fun execute(weekStart: LocalDate) = repository.getNote(weekStart)
}
