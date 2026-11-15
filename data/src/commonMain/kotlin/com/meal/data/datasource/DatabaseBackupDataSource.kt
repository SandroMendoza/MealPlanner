package com.meal.data.datasource

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.meal.data.db.MealPlannerDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Database operations needed to copy the database file out of the app and back in. */
class DatabaseBackupDataSource(
    private val driver: SqlDriver,
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val schemaVersion: Long get() = MealPlannerDatabase.Schema.version

    /** Writes any pending write-ahead log into the main file, so the file alone is complete. */
    suspend fun checkpoint() = withContext(ioDispatcher) {
        driver.executeQuery(null, "PRAGMA wal_checkpoint(TRUNCATE)", { QueryResult.Value(it.next().value) }, 0)
        Unit
    }

    fun close() = driver.close()

    /**
     * Photo paths are stored as absolute `file://` URIs into the app's private
     * `recipe_images` folder, which differs between devices and user profiles. Points every
     * such path at the same file name inside [photoDirUri], which must end with `/`.
     */
    suspend fun rewritePhotoPaths(photoDirUri: String) = withContext(ioDispatcher) {
        database.transaction {
            database.recipeQueries.rewritePhotoPaths(photoDirUri)
            database.userProfileQueries.rewritePhotoPath(photoDirUri)
        }
    }
}
