package com.meal.data.repository

import com.meal.data.datasource.MealReminderLocalDataSource
import com.meal.data.datasource.SettingsLocalDataSource
import com.meal.data.datasource.UserProfileLocalDataSource
import com.meal.data.datasource.WeekNoteLocalDataSource
import com.meal.data.inMemoryDatabase
import com.meal.domain.model.AppSettings
import com.meal.domain.model.ThemeMode
import com.meal.domain.model.CalendarViewMode
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import com.meal.domain.model.MealReminder
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.UserProfile
import com.meal.domain.model.defaultReminderTime
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class SingleRowRepositoriesTest {

    private val database = inMemoryDatabase()
    private val profiles = UserProfileRepositoryImpl(UserProfileLocalDataSource(database, Dispatchers.Unconfined))
    private val settings = SettingsRepositoryImpl(SettingsLocalDataSource(database, Dispatchers.Unconfined))
    private val reminders = MealReminderRepositoryImpl(MealReminderLocalDataSource(database, Dispatchers.Unconfined))
    private val notes = WeekNoteRepositoryImpl(WeekNoteLocalDataSource(database, Dispatchers.Unconfined))

    @Test
    fun profileHasNoNameBeforeAnyEdit() = runTest {
        assertEquals(UserProfile(name = "", bio = "", avatarUrl = ""), profiles.getProfile().first())
    }

    @Test
    fun profileUpdatesReplaceTheSingleRow() = runTest {
        profiles.updateProfile(UserProfile("Ana", "Home cook", "file:///avatar.jpg"))
        profiles.updateProfile(UserProfile("Ana B", "Home cook", ""))

        assertEquals(UserProfile("Ana B", "Home cook", ""), profiles.getProfile().first())
    }

    @Test
    fun settingsDefaultBeforeAnyChange() = runTest {
        assertEquals(AppSettings(), settings.getSettings().first())
    }

    @Test
    fun everySettingIsStored() = runTest {
        val changed = AppSettings(
            themeMode = ThemeMode.DARK,
            language = Language.FRENCH,
            currency = Currency.PHP,
            lockPastDates = true,
            defaultCalendarView = CalendarViewMode.WEEK,
            readAloudEnabled = true,
            defaultRecipeView = RecipeViewMode.LIST
        )

        settings.updateSettings(changed)

        assertEquals(changed, settings.getSettings().first())
    }

    @Test
    fun everyThemeModeIsStored() = runTest {
        ThemeMode.entries.forEach { mode ->
            settings.updateSettings(AppSettings(themeMode = mode))
            assertEquals(mode, settings.getSettings().first().themeMode)
        }
    }

    @Test
    fun remindersListEveryMealTypeDisabledAtDefaultTimes() = runTest {
        val expected = MealType.entries.map { MealReminder(it, enabled = false, time = it.defaultReminderTime()) }

        assertEquals(expected, reminders.getReminders().first())
    }

    @Test
    fun updatedReminderIsStoredAndOthersKeepDefaults() = runTest {
        reminders.updateReminder(MealReminder(MealType.LUNCH, enabled = true, time = LocalTime(11, 45)))

        val byType = reminders.getReminders().first().associateBy { it.mealType }

        assertEquals(MealReminder(MealType.LUNCH, true, LocalTime(11, 45)), byType.getValue(MealType.LUNCH))
        assertEquals(MealType.entries.size, byType.size)
        MealType.entries.filter { it != MealType.LUNCH }.forEach { type ->
            assertEquals(MealReminder(type, false, type.defaultReminderTime()), byType.getValue(type))
        }
    }

    @Test
    fun remindersKeepMealTypeOrder() = runTest {
        reminders.updateReminder(MealReminder(MealType.SNACK, enabled = true, time = LocalTime(16, 0)))

        assertEquals(MealType.entries, reminders.getReminders().first().map { it.mealType })
    }

    @Test
    fun weekNoteIsEmptyUntilWritten() = runTest {
        assertEquals("", notes.getNote(LocalDate(2026, 11, 8)).first())
    }

    @Test
    fun weekNotesAreStoredPerWeekAndOverwritten() = runTest {
        val week1 = LocalDate(2026, 11, 8)
        val week2 = LocalDate(2026, 11, 15)

        notes.updateNote(week1, "Buy rice")
        notes.updateNote(week1, "Buy rice and eggs")
        notes.updateNote(week2, "Party on Saturday")

        assertEquals("Buy rice and eggs", notes.getNote(week1).first())
        assertEquals("Party on Saturday", notes.getNote(week2).first())
    }
}
