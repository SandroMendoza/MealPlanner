@file:OptIn(ExperimentalUuidApi::class)

package com.meal.domain.fakes

import com.meal.domain.model.AppSettings
import com.meal.domain.model.Ingredient
import com.meal.domain.model.MealReminder
import com.meal.domain.model.MealSlot
import com.meal.domain.model.Recipe
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.SpendingLineItem
import com.meal.domain.model.UserProfile
import com.meal.domain.model.enums.MealType
import com.meal.domain.repository.IngredientRepository
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.repository.RecipeRepository
import com.meal.domain.repository.SettingsRepository
import com.meal.domain.repository.ShoppingGroupRepository
import com.meal.domain.repository.ShoppingListRepository
import com.meal.domain.repository.UserProfileRepository
import com.meal.domain.repository.WeekNoteRepository
import com.meal.domain.service.BackupService
import com.meal.domain.service.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeRecipeRepository(initial: List<Recipe> = emptyList()) : RecipeRepository {
    val recipes = MutableStateFlow(initial)
    val softDeleted = mutableListOf<Uuid>()

    override fun getAllRecipes(): Flow<List<Recipe>> = recipes

    override fun getRecipeById(id: Uuid): Flow<Recipe?> = recipes.map { all -> all.firstOrNull { it.id == id } }

    override suspend fun addRecipe(recipe: Recipe) {
        recipes.value = recipes.value + recipe
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipes.value = recipes.value.map { if (it.id == recipe.id) recipe else it }
    }

    override suspend fun softDeleteRecipe(id: Uuid) {
        softDeleted += id
        recipes.value = recipes.value.filterNot { it.id == id }
    }

    override suspend fun setFavorite(id: Uuid, isFavorite: Boolean) {
        recipes.value = recipes.value.map { if (it.id == id) it.copy(isFavorite = isFavorite) else it }
    }
}

class FakeMealPlanRepository(initial: List<MealSlot> = emptyList()) : MealPlanRepository {
    val slots = MutableStateFlow(initial)
    var futureDates: Map<Uuid, List<LocalDate>> = emptyMap()
    val removedFutureSlotsFor = mutableListOf<Uuid>()

    override fun getMealSlotsForWeek(weekStart: LocalDate, weekEnd: LocalDate): Flow<List<MealSlot>> =
        slots.map { all -> all.filter { it.date in weekStart..weekEnd } }

    override suspend fun addRecipeToSlot(date: LocalDate, mealType: MealType, recipeId: Uuid) {
        slots.value = slots.value + MealSlot(Uuid.random(), date, mealType, recipeId)
    }

    override suspend fun replaceRecipeInSlot(slotId: Uuid, recipeId: Uuid) {
        slots.value = slots.value.map { if (it.id == slotId) it.copy(recipeId = recipeId) else it }
    }

    override suspend fun removeMealSlot(slotId: Uuid) {
        slots.value = slots.value.filterNot { it.id == slotId }
    }

    override suspend fun getFutureSlotDatesForRecipe(recipeId: Uuid): List<LocalDate> =
        futureDates[recipeId].orEmpty()

    override suspend fun removeSlotsForRecipeOnOrAfter(recipeId: Uuid) {
        removedFutureSlotsFor += recipeId
    }
}

data class SyncCall(val rangeStart: LocalDate, val rangeEnd: LocalDate, val required: List<ShoppingListItem>)

data class ItemCall(val itemKey: String, val rangeStart: LocalDate, val rangeEnd: LocalDate, val value: Any? = null)

class FakeShoppingListRepository : ShoppingListRepository {
    val items = MutableStateFlow<List<ShoppingListItem>>(emptyList())
    val pricedItems = MutableStateFlow<List<SpendingLineItem>>(emptyList())
    val syncCalls = mutableListOf<SyncCall>()
    val addedItems = mutableListOf<Triple<ShoppingListItem, LocalDate, LocalDate>>()
    val checkedCalls = mutableListOf<ItemCall>()
    val removedCalls = mutableListOf<ItemCall>()
    val priceCalls = mutableListOf<ItemCall>()

    override fun getItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<ShoppingListItem>> = items

    override suspend fun syncGeneratedItems(rangeStart: LocalDate, rangeEnd: LocalDate, required: List<ShoppingListItem>) {
        syncCalls += SyncCall(rangeStart, rangeEnd, required)
    }

    override suspend fun setItemChecked(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, checked: Boolean) {
        checkedCalls += ItemCall(itemKey, rangeStart, rangeEnd, checked)
    }

    override suspend fun addManualItem(item: ShoppingListItem, rangeStart: LocalDate, rangeEnd: LocalDate) {
        addedItems += Triple(item, rangeStart, rangeEnd)
    }

    override suspend fun removeItem(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate) {
        removedCalls += ItemCall(itemKey, rangeStart, rangeEnd)
    }

    override suspend fun setItemPrice(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, price: Double) {
        priceCalls += ItemCall(itemKey, rangeStart, rangeEnd, price)
    }

    override fun getPricedItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<SpendingLineItem>> = pricedItems
}

class FakeShoppingGroupRepository : ShoppingGroupRepository {
    val groups = MutableStateFlow<List<ShoppingGroup>>(emptyList())
    val assignments = mutableMapOf<String, String?>()

    override fun getGroups(): Flow<List<ShoppingGroup>> = groups

    override suspend fun createGroup(name: String): ShoppingGroup {
        val group = ShoppingGroup(id = "group-${groups.value.size + 1}", name = name, sortOrder = groups.value.size)
        groups.value = groups.value + group
        return group
    }

    override suspend fun renameGroup(id: String, name: String) {
        groups.value = groups.value.map { if (it.id == id) it.copy(name = name) else it }
    }

    override suspend fun deleteGroup(id: String) {
        groups.value = groups.value.filterNot { it.id == id }
    }

    override suspend fun assignItemToGroup(itemKey: String, groupId: String?) {
        assignments[itemKey] = groupId
    }
}

class FakeSettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {
    val settings = MutableStateFlow(initial)

    override fun getSettings(): Flow<AppSettings> = settings

    override suspend fun updateSettings(settings: AppSettings) {
        this.settings.value = settings
    }
}

class FakeUserProfileRepository(initial: UserProfile = UserProfile()) : UserProfileRepository {
    val profile = MutableStateFlow(initial)

    override fun getProfile(): Flow<UserProfile> = profile

    override suspend fun updateProfile(profile: UserProfile) {
        this.profile.value = profile
    }
}

class FakeWeekNoteRepository : WeekNoteRepository {
    val notes = MutableStateFlow<Map<LocalDate, String>>(emptyMap())

    override fun getNote(weekStart: LocalDate): Flow<String> = notes.map { it[weekStart].orEmpty() }

    override suspend fun updateNote(weekStart: LocalDate, note: String) {
        notes.value = notes.value + (weekStart to note)
    }
}

class FakeMealReminderRepository(initial: List<MealReminder> = emptyList()) : MealReminderRepository {
    val reminders = MutableStateFlow(initial)

    override fun getReminders(): Flow<List<MealReminder>> = reminders

    override suspend fun updateReminder(reminder: MealReminder) {
        reminders.value = reminders.value.filterNot { it.mealType == reminder.mealType } + reminder
    }
}

class FakeIngredientRepository(initial: List<Ingredient> = emptyList()) : IngredientRepository {
    val ingredients = MutableStateFlow(initial)

    override fun getAllIngredients(): Flow<List<Ingredient>> = ingredients
}

class FakeReminderScheduler : ReminderScheduler {
    val scheduled = mutableListOf<MealReminder>()
    val cancelled = mutableListOf<MealType>()
    val rescheduled = mutableListOf<List<MealReminder>>()

    override fun schedule(reminder: MealReminder) {
        scheduled += reminder
    }

    override fun cancel(mealType: MealType) {
        cancelled += mealType
    }

    override fun rescheduleAll(reminders: List<MealReminder>) {
        rescheduled += reminders
    }
}

class FakeBackupService(private val failure: Exception? = null) : BackupService {
    val exported = mutableListOf<String>()
    val imported = mutableListOf<String>()

    override suspend fun export(destinationUri: String) {
        failure?.let { throw it }
        exported += destinationUri
    }

    override suspend fun import(sourceUri: String) {
        failure?.let { throw it }
        imported += sourceUri
    }
}
