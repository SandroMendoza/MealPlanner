package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingGroupRepository
import kotlinx.coroutines.flow.Flow

class GetCollapsedShoppingSectionsUseCase(
    private val repository: ShoppingGroupRepository
) {
    fun execute(): Flow<Set<String>> = repository.getCollapsedSections()
}
