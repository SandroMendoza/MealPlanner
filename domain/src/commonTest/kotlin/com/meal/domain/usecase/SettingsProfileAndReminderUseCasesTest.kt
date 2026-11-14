package com.meal.domain.usecase

import com.meal.domain.fakes.FakeMealReminderRepository
import com.meal.domain.fakes.FakeReminderScheduler
import com.meal.domain.fakes.FakeSettingsRepository
import com.meal.domain.fakes.FakeUserProfileRepository
import com.meal.domain.fakes.FakeWeekNoteRepository
import com.meal.domain.model.AppSettings
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import com.meal.domain.model.MealReminder
import com.meal.domain.model.UserProfile
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsProfileAndReminderUseCasesTest {

    @Test
    fun settingsCanBeReadAndUpdated() = runTest {
        val repository = FakeSettingsRepository()
        val updated = AppSettings(darkModeEnabled = true, language = Language.FRENCH, currency = Currency.PHP)

        UpdateSettingsUseCase(repository).execute(updated)

        assertEquals(updated, GetSettingsUseCase(repository).execute().first())
    }

    @Test
    fun profileCanBeReadAndUpdated() = runTest {
        val repository = FakeUserProfileRepository()
        val updated = UserProfile(name = "Ana", bio = "Home cook")

        UpdateUserProfileUseCase(repository).execute(updated)

        assertEquals(updated, GetUserProfileUseCase(repository).execute().first())
    }

    @Test
    fun weekNotesAreStoredPerWeek() = runTest {
        val repository = FakeWeekNoteRepository()
        val thisWeek = LocalDate(2026, 11, 8)
        val nextWeek = LocalDate(2026, 11, 15)

        UpdateWeekNoteUseCase(repository).execute(thisWeek, "Buy rice")

        assertEquals("Buy rice", GetWeekNoteUseCase(repository).execute(thisWeek).first())
        assertEquals("", GetWeekNoteUseCase(repository).execute(nextWeek).first())
    }

    @Test
    fun gettingRemindersReturnsTheStoredReminders() = runTest {
        val stored = listOf(MealReminder(MealType.BREAKFAST, enabled = true, time = LocalTime(7, 30)))

        assertEquals(stored, GetMealRemindersUseCase(FakeMealReminderRepository(stored)).execute().first())
    }

    @Test
    fun updatingAReminderSavesItAndSchedulesIt() = runTest {
        val repository = FakeMealReminderRepository()
        val scheduler = FakeReminderScheduler()
        val reminder = MealReminder(MealType.DINNER, enabled = true, time = LocalTime(18, 30))

        UpdateMealReminderUseCase(repository, scheduler).execute(reminder)

        assertEquals(listOf(reminder), repository.reminders.value)
        assertEquals(listOf(reminder), scheduler.scheduled)
    }

    @Test
    fun turningAReminderOffStillGoesToTheScheduler() = runTest {
        val repository = FakeMealReminderRepository()
        val scheduler = FakeReminderScheduler()
        val reminder = MealReminder(MealType.LUNCH, enabled = false, time = LocalTime(12, 0))

        UpdateMealReminderUseCase(repository, scheduler).execute(reminder)

        assertEquals(listOf(reminder), repository.reminders.value)
        assertEquals(listOf(reminder), scheduler.scheduled)
    }
}
