package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.meal.data.db.MealPlannerDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OnboardingLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database.onboardingQueries

    fun walkthroughSeen(): Flow<Boolean> =
        queries.selectOnboarding()
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { row -> row?.walkthroughSeen == 1L }

    suspend fun markWalkthroughSeen() =
        withContext(ioDispatcher) {
            queries.markWalkthroughSeen()
        }
}
