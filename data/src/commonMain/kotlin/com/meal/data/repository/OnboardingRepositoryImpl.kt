package com.meal.data.repository

import com.meal.data.datasource.OnboardingLocalDataSource
import com.meal.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl(
    private val localDataSource: OnboardingLocalDataSource
) : OnboardingRepository {

    override fun walkthroughSeen(): Flow<Boolean> = localDataSource.walkthroughSeen()

    override suspend fun markWalkthroughSeen() {
        localDataSource.markWalkthroughSeen()
    }
}
