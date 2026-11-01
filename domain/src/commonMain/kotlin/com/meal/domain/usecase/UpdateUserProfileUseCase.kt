package com.meal.domain.usecase

import com.meal.domain.model.UserProfile
import com.meal.domain.repository.UserProfileRepository

class UpdateUserProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend fun execute(profile: UserProfile) = repository.updateProfile(profile)
}
