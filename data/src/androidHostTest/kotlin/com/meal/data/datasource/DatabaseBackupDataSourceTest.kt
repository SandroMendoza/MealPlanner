package com.meal.data.datasource

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.meal.data.db.MealPlannerDatabase
import com.meal.domain.model.BUNDLED_IMAGE_PREFIX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseBackupDataSourceTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { MealPlannerDatabase.Schema.create(it) }
    private val database = MealPlannerDatabase(driver)
    private val dataSource = DatabaseBackupDataSource(driver, database, Dispatchers.Unconfined)

    private val newDir = "file:///data/user/10/sandragon.inc.mealplanner/files/recipe_images/"

    private fun insertRecipe(id: String, imageUrl: String?) =
        database.recipeQueries.insertRecipe(id, "Recipe $id", null, "MAIN", 10, 2, imageUrl, 1, null, null)

    private fun imageUrl(id: String) = database.recipeQueries.selectRecipeById(id).executeAsOne().imageUrl

    @Test
    fun pointsPhotosFromAnotherDeviceAtThisDevicesFolder() = runTest {
        insertRecipe("a", "file:///data/user/0/sandragon.inc.mealplanner/files/recipe_images/a.jpg")
        database.userProfileQueries.upsertProfile(
            "Chef", "", "file:///data/data/sandragon.inc.mealplanner/files/recipe_images/me.jpg"
        )

        dataSource.rewritePhotoPaths(newDir)

        assertEquals("${newDir}a.jpg", imageUrl("a"))
        assertEquals("${newDir}me.jpg", database.userProfileQueries.selectProfile().executeAsOne().avatarUrl)
    }

    @Test
    fun leavesBundledWebAndMissingImagesAlone() = runTest {
        insertRecipe("seed", "${BUNDLED_IMAGE_PREFIX}chicken_adobo.webp")
        insertRecipe("web", "https://example.com/recipe_images/x.jpg")
        insertRecipe("blank", "")
        insertRecipe("none", null)
        database.userProfileQueries.upsertProfile("Chef", "", "")

        dataSource.rewritePhotoPaths(newDir)

        assertEquals("${BUNDLED_IMAGE_PREFIX}chicken_adobo.webp", imageUrl("seed"))
        assertEquals("https://example.com/recipe_images/x.jpg", imageUrl("web"))
        assertEquals("", imageUrl("blank"))
        assertEquals(null, imageUrl("none"))
        assertEquals("", database.userProfileQueries.selectProfile().executeAsOne().avatarUrl)
    }

    @Test
    fun checkpointRunsOnAnOpenDatabase() = runTest {
        dataSource.checkpoint()
    }
}
