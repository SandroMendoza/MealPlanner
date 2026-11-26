package com.meal.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    /** Whether the first-launch walkthrough was finished or skipped on this install. */
    fun walkthroughSeen(): Flow<Boolean>
    suspend fun markWalkthroughSeen()
}
