package com.meal.domain.usecase

import com.meal.domain.repository.OnboardingRepository

class MarkWalkthroughSeenUseCase(
    private val repository: OnboardingRepository
) {
    suspend fun execute() = repository.markWalkthroughSeen()
}
