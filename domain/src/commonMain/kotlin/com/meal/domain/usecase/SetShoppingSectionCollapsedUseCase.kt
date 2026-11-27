package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingGroupRepository

class SetShoppingSectionCollapsedUseCase(
    private val repository: ShoppingGroupRepository
) {
    suspend fun execute(sectionKey: String, collapsed: Boolean) = repository.setSectionCollapsed(sectionKey, collapsed)
}
