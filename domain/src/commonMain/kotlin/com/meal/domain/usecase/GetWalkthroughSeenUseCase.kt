package com.meal.domain.usecase

import com.meal.domain.repository.OnboardingRepository

class GetWalkthroughSeenUseCase(
    private val repository: OnboardingRepository
) {
    fun execute() = repository.walkthroughSeen()
}
