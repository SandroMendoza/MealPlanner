package com.meal.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.meal.data.db.MealPlannerDatabase

fun inMemoryDatabase(): MealPlannerDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    MealPlannerDatabase.Schema.create(driver)
    return MealPlannerDatabase(driver)
}
