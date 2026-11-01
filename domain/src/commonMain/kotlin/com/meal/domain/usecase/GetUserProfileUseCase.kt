package com.meal.domain.usecase

import com.meal.domain.repository.UserProfileRepository

class GetUserProfileUseCase(
    private val repository: UserProfileRepository
) {
    fun execute() = repository.getProfile()
}
