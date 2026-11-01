package com.meal.data.repository

import com.meal.data.datasource.UserProfileLocalDataSource
import com.meal.domain.model.UserProfile
import com.meal.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow

class UserProfileRepositoryImpl(
    private val localDataSource: UserProfileLocalDataSource
) : UserProfileRepository {

    override fun getProfile(): Flow<UserProfile> = localDataSource.profile()

    override suspend fun updateProfile(profile: UserProfile) {
        localDataSource.update(profile)
    }
}
