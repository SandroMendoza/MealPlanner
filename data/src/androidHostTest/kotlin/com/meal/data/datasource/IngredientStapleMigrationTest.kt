package com.meal.data.datasource

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.meal.data.db.MealPlannerDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Migration 11 → 12 moves the shopping list's "at home" overrides onto the ingredient rows. */
class IngredientStapleMigrationTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    private fun exec(sql: String) = driver.execute(null, sql, 0)

    @Test
    fun overridesBecomeIngredientStaples() {
        exec("CREATE TABLE IngredientEntity (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, normalizedName TEXT NOT NULL, category TEXT)")
        exec("CREATE UNIQUE INDEX IngredientEntity_normalizedName ON IngredientEntity(normalizedName)")
        exec("CREATE TABLE ShoppingAtHomeEntity (normalizedName TEXT NOT NULL PRIMARY KEY, atHome INTEGER NOT NULL)")
        exec(
            "CREATE TABLE ShoppingListItemEntity (itemKey TEXT NOT NULL, rangeStart TEXT NOT NULL, rangeEnd TEXT NOT NULL, " +
                "name TEXT NOT NULL, quantity REAL, unit TEXT, checked INTEGER NOT NULL DEFAULT 0, " +
                "removed INTEGER NOT NULL DEFAULT 0, price REAL, PRIMARY KEY (itemKey, rangeStart, rangeEnd))"
        )
        exec("INSERT INTO IngredientEntity VALUES ('a', 'Salt', 'salt', 'SPICE_AND_HERB')")
        exec("INSERT INTO IngredientEntity VALUES ('b', 'Rice', 'rice', 'GRAIN_AND_NOODLES')")
        exec("INSERT INTO ShoppingAtHomeEntity VALUES ('salt', 0)")
        exec("INSERT INTO ShoppingAtHomeEntity VALUES ('paper towels', 1)")
        exec("INSERT INTO ShoppingListItemEntity(itemKey, rangeStart, rangeEnd, name) VALUES ('k', '2026-01-04', '2026-01-10', 'Paper Towels')")

        MealPlannerDatabase.Schema.migrate(driver, 11, 12)

        val rows = MealPlannerDatabase(driver).ingredientQueries.selectAllIngredients().executeAsList().associateBy { it.normalizedName }
        assertEquals(0L, rows.getValue("salt").isStaple)
        assertEquals(null, rows.getValue("rice").isStaple)
        val towels = rows.getValue("paper towels")
        assertEquals("Paper Towels", towels.name)
        assertEquals(1L, towels.isStaple)
        assertTrue(Regex("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}").matches(towels.id))
    }
}
