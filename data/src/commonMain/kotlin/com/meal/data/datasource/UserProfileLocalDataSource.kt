package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.db.UserProfileEntity
import com.meal.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserProfileLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database.userProfileQueries

    fun profile(): Flow<UserProfile> =
        queries.selectProfile()
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { row -> row?.let { toDomain(it) } ?: UserProfile() }

    suspend fun update(profile: UserProfile) =
        withContext(ioDispatcher) {
            queries.upsertProfile(profile.name, profile.bio, profile.avatarUrl)
        }

    private fun toDomain(row: UserProfileEntity) = UserProfile(
        name = row.name,
        bio = row.bio,
        avatarUrl = row.avatarUrl
    )
}
