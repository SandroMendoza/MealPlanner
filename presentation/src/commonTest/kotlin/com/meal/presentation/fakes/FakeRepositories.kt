package com.meal.presentation.fakes

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
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.repository.IngredientRepository
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.repository.OnboardingRepository
import com.meal.domain.repository.RecipeRepository
import com.meal.domain.repository.SettingsRepository
import com.meal.domain.repository.ShoppingGroupRepository
import com.meal.domain.repository.ShoppingListRepository
import com.meal.domain.repository.UserProfileRepository
import com.meal.domain.repository.WeekNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private fun <T> failingFlow(error: Throwable): Flow<T> = flow { throw error }

@OptIn(ExperimentalUuidApi::class)
class FakeRecipeRepository(initial: List<Recipe> = emptyList()) : RecipeRepository {
    val recipes = MutableStateFlow(initial)
    var loadFailure: Throwable? = null
    var saveFailure: Throwable? = null
    val added = mutableListOf<Recipe>()
    val updated = mutableListOf<Recipe>()
    val softDeleted = mutableListOf<Uuid>()
    val favoriteCalls = mutableListOf<Pair<Uuid, Boolean>>()

    override fun getAllRecipes(): Flow<List<Recipe>> = loadFailure?.let { failingFlow(it) } ?: recipes

    override fun getRecipeById(id: Uuid): Flow<Recipe?> =
        loadFailure?.let { failingFlow(it) } ?: recipes.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun addRecipe(recipe: Recipe) {
        saveFailure?.let { throw it }
        added += recipe
        recipes.value = recipes.value + recipe
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        saveFailure?.let { throw it }
        updated += recipe
        recipes.value = recipes.value.map { if (it.id == recipe.id) recipe else it }
    }

    override suspend fun softDeleteRecipe(id: Uuid) {
        softDeleted += id
        recipes.value = recipes.value.filterNot { it.id == id }
    }

    override suspend fun setFavorite(id: Uuid, isFavorite: Boolean) {
        favoriteCalls += id to isFavorite
        recipes.value = recipes.value.map { if (it.id == id) it.copy(isFavorite = isFavorite) else it }
    }
}

@OptIn(ExperimentalUuidApi::class)
class FakeMealPlanRepository(initial: List<MealSlot> = emptyList()) : MealPlanRepository {
    val slots = MutableStateFlow(initial)
    var loadFailure: Throwable? = null
    val futureDatesByRecipe = mutableMapOf<Uuid, List<LocalDate>>()
    val removedSlots = mutableListOf<Uuid>()
    val slotsRemovedForRecipe = mutableListOf<Uuid>()
    val requestedRanges = mutableListOf<Pair<LocalDate, LocalDate>>()

    override fun getMealSlotsForWeek(weekStart: LocalDate, weekEnd: LocalDate): Flow<List<MealSlot>> {
        requestedRanges += weekStart to weekEnd
        return loadFailure?.let { failingFlow(it) }
            ?: slots.map { list -> list.filter { it.date in weekStart..weekEnd } }
    }

    override suspend fun addRecipeToSlot(date: LocalDate, mealType: MealType, recipeId: Uuid) {
        slots.value = slots.value + MealSlot(Uuid.random(), date, mealType, recipeId)
    }

    override suspend fun replaceRecipeInSlot(slotId: Uuid, recipeId: Uuid) {
        slots.value = slots.value.map { if (it.id == slotId) it.copy(recipeId = recipeId) else it }
    }

    override suspend fun removeMealSlot(slotId: Uuid) {
        removedSlots += slotId
        slots.value = slots.value.filterNot { it.id == slotId }
    }

    override suspend fun getFutureSlotDatesForRecipe(recipeId: Uuid): List<LocalDate> =
        futureDatesByRecipe[recipeId].orEmpty()

    override suspend fun removeSlotsForRecipeOnOrAfter(recipeId: Uuid) {
        slotsRemovedForRecipe += recipeId
    }
}

class FakeSettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {
    val settings = MutableStateFlow(initial)
    val updates = mutableListOf<AppSettings>()

    override fun getSettings(): Flow<AppSettings> = settings

    override suspend fun updateSettings(settings: AppSettings) {
        updates += settings
        this.settings.value = settings
    }
}

class FakeUserProfileRepository(initial: UserProfile = UserProfile()) : UserProfileRepository {
    val profile = MutableStateFlow(initial)
    val updates = mutableListOf<UserProfile>()

    override fun getProfile(): Flow<UserProfile> = profile

    override suspend fun updateProfile(profile: UserProfile) {
        updates += profile
        this.profile.value = profile
    }
}

class FakeIngredientRepository(initial: List<Ingredient> = emptyList()) : IngredientRepository {
    val ingredients = MutableStateFlow(initial)

    override fun getAllIngredients(): Flow<List<Ingredient>> = ingredients
}

class FakeMealReminderRepository(initial: List<MealReminder> = emptyList()) : MealReminderRepository {
    val reminders = MutableStateFlow(initial)
    val updates = mutableListOf<MealReminder>()

    override fun getReminders(): Flow<List<MealReminder>> = reminders

    override suspend fun updateReminder(reminder: MealReminder) {
        updates += reminder
        reminders.value = reminders.value.filterNot { it.mealType == reminder.mealType } + reminder
    }
}

class FakeWeekNoteRepository(initial: Map<LocalDate, String> = emptyMap()) : WeekNoteRepository {
    val notes = MutableStateFlow(initial)
    val updates = mutableListOf<Pair<LocalDate, String>>()

    override fun getNote(weekStart: LocalDate): Flow<String> = notes.map { it[weekStart].orEmpty() }

    override suspend fun updateNote(weekStart: LocalDate, note: String) {
        updates += weekStart to note
        notes.value = notes.value + (weekStart to note)
    }
}

class FakeOnboardingRepository(seen: Boolean = false) : OnboardingRepository {
    val walkthroughSeen = MutableStateFlow(seen)

    override fun walkthroughSeen(): Flow<Boolean> = walkthroughSeen

    override suspend fun markWalkthroughSeen() {
        walkthroughSeen.value = true
    }
}

class FakeShoppingListRepository : ShoppingListRepository {
    private val itemsByRange = MutableStateFlow<Map<Pair<LocalDate, LocalDate>, List<ShoppingListItem>>>(emptyMap())
    val pricedItems = MutableStateFlow<List<SpendingLineItem>>(emptyList())
    var loadFailure: Throwable? = null
    val requestedRanges = mutableListOf<Pair<LocalDate, LocalDate>>()
    val pricedRanges = mutableListOf<Pair<LocalDate, LocalDate>>()
    val checkedCalls = mutableListOf<CheckedCall>()
    val manualItems = mutableListOf<Pair<ShoppingListItem, Pair<LocalDate, LocalDate>>>()
    val removedItems = mutableListOf<Pair<String, Pair<LocalDate, LocalDate>>>()
    val priceCalls = mutableListOf<Pair<String, Double>>()

    data class CheckedCall(val itemKey: String, val rangeStart: LocalDate, val rangeEnd: LocalDate, val checked: Boolean)

    fun setItems(rangeStart: LocalDate, rangeEnd: LocalDate, items: List<ShoppingListItem>) {
        itemsByRange.value = itemsByRange.value + ((rangeStart to rangeEnd) to items)
    }

    fun itemsFor(rangeStart: LocalDate, rangeEnd: LocalDate): List<ShoppingListItem> =
        itemsByRange.value[rangeStart to rangeEnd].orEmpty()

    override fun getItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<ShoppingListItem>> {
        requestedRanges += rangeStart to rangeEnd
        return loadFailure?.let { failingFlow(it) } ?: itemsByRange.map { it[rangeStart to rangeEnd].orEmpty() }
    }

    override suspend fun syncGeneratedItems(rangeStart: LocalDate, rangeEnd: LocalDate, required: List<ShoppingListItem>) {
        val range = rangeStart to rangeEnd
        val current = itemsFor(rangeStart, rangeEnd)
        val existing = current.associateBy { it.itemKey }
        val requiredKeys = required.map { it.itemKey }.toSet()
        val previouslyGenerated = generatedKeysByRange[range].orEmpty()
        val merged = required.map { item ->
            existing[item.itemKey]?.let { item.copy(checked = it.checked, price = it.price, groupId = it.groupId) } ?: item
        }
        val others = current.filter { it.itemKey !in requiredKeys && it.itemKey !in previouslyGenerated }
        generatedKeysByRange[range] = requiredKeys
        setItems(rangeStart, rangeEnd, merged + others)
    }

    private val generatedKeysByRange = mutableMapOf<Pair<LocalDate, LocalDate>, Set<String>>()

    override suspend fun setItemChecked(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, checked: Boolean) {
        checkedCalls += CheckedCall(itemKey, rangeStart, rangeEnd, checked)
        setItems(rangeStart, rangeEnd, itemsFor(rangeStart, rangeEnd).map { if (it.itemKey == itemKey) it.copy(checked = checked) else it })
    }

    override suspend fun addManualItem(item: ShoppingListItem, rangeStart: LocalDate, rangeEnd: LocalDate) {
        manualItems += item to (rangeStart to rangeEnd)
        setItems(rangeStart, rangeEnd, itemsFor(rangeStart, rangeEnd) + item)
    }

    override suspend fun updateItemAmount(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, quantity: Double, unit: MeasurementUnit) {
        setItems(rangeStart, rangeEnd, itemsFor(rangeStart, rangeEnd).map {
            if (it.itemKey == itemKey) it.copy(totalQuantity = quantity, unit = unit) else it
        })
    }

    override suspend fun removeItem(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate) {
        removedItems += itemKey to (rangeStart to rangeEnd)
        setItems(rangeStart, rangeEnd, itemsFor(rangeStart, rangeEnd).filterNot { it.itemKey == itemKey })
    }

    override suspend fun setItemPrice(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, price: Double) {
        priceCalls += itemKey to price
        setItems(rangeStart, rangeEnd, itemsFor(rangeStart, rangeEnd).map { if (it.itemKey == itemKey) it.copy(price = price) else it })
    }

    override fun getPricedItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<SpendingLineItem>> {
        pricedRanges += rangeStart to rangeEnd
        return loadFailure?.let { failingFlow(it) } ?: pricedItems
    }
}

class FakeShoppingGroupRepository(initial: List<ShoppingGroup> = emptyList()) : ShoppingGroupRepository {
    val groups = MutableStateFlow(initial)
    val assignments = mutableListOf<Pair<String, String?>>()
    val renamed = mutableListOf<Pair<String, String>>()
    val deleted = mutableListOf<String>()

    override fun getGroups(): Flow<List<ShoppingGroup>> = groups

    override suspend fun createGroup(name: String): ShoppingGroup {
        val group = ShoppingGroup(id = "group-${groups.value.size + 1}", name = name, sortOrder = groups.value.size)
        groups.value = groups.value + group
        return group
    }

    override suspend fun renameGroup(id: String, name: String) {
        renamed += id to name
        groups.value = groups.value.map { if (it.id == id) it.copy(name = name) else it }
    }

    override suspend fun deleteGroup(id: String) {
        deleted += id
        groups.value = groups.value.filterNot { it.id == id }
    }

    override suspend fun assignItemToGroup(itemKey: String, groupId: String?) {
        assignments += itemKey to groupId
    }
}
