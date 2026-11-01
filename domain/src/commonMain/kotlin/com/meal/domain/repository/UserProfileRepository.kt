package com.meal.domain.repository

import com.meal.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getProfile(): Flow<UserProfile>
    suspend fun updateProfile(profile: UserProfile)
}
