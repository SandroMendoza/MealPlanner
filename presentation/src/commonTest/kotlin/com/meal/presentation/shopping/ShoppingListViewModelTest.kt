package com.meal.presentation.shopping

import com.meal.domain.model.AppSettings
import com.meal.domain.model.Currency
import com.meal.domain.model.Ingredient
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.usecase.AddShoppingListItemUseCase
import com.meal.domain.usecase.AssignItemToGroupUseCase
import com.meal.domain.usecase.CreateShoppingGroupUseCase
import com.meal.domain.usecase.DeleteShoppingGroupUseCase
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetShoppingGroupsUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.RecordItemPriceUseCase
import com.meal.domain.usecase.RemoveShoppingListItemUseCase
import com.meal.domain.usecase.RenameShoppingGroupUseCase
import com.meal.domain.usecase.ToggleShoppingListItemUseCase
import com.meal.domain.usecase.UpdateShoppingListItemAmountUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import com.meal.presentation.fakes.FakeIngredientRepository
import com.meal.presentation.fakes.FakeMealPlanRepository
import com.meal.presentation.fakes.FakeRecipeRepository
import com.meal.presentation.fakes.FakeSettingsRepository
import com.meal.presentation.fakes.FakeShoppingGroupRepository
import com.meal.presentation.fakes.FakeShoppingListRepository
import com.meal.presentation.fakes.FakeTtsSpeaker
import com.meal.presentation.fakes.ingredientLine
import com.meal.presentation.fakes.recipe
import com.meal.presentation.fakes.shoppingItem
import com.meal.presentation.fakes.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class ShoppingListViewModelTest {

    private val recipes = FakeRecipeRepository()
    private val mealPlan = FakeMealPlanRepository()
    private val shopping = FakeShoppingListRepository()
    private val groups = FakeShoppingGroupRepository()
    private val ingredients = FakeIngredientRepository(listOf(Ingredient(Uuid.random(), "Rice")))
    private val settings = FakeSettingsRepository()
    private val tts = FakeTtsSpeaker()

    private val weekStart = currentWeekSunday()
    private val weekEnd = weekEndFor(weekStart)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ShoppingListViewModel(
        GenerateShoppingListUseCase(GetWeeklyMealPlanUseCase(mealPlan, recipes), shopping),
        ToggleShoppingListItemUseCase(shopping),
        AddShoppingListItemUseCase(shopping),
        RemoveShoppingListItemUseCase(shopping),
        RecordItemPriceUseCase(shopping),
        GetAllIngredientsUseCase(ingredients),
        GetSettingsUseCase(settings),
        GetShoppingGroupsUseCase(groups),
        CreateShoppingGroupUseCase(groups),
        RenameShoppingGroupUseCase(groups),
        DeleteShoppingGroupUseCase(groups),
        AssignItemToGroupUseCase(groups),
        UpdateShoppingListItemAmountUseCase(shopping),
        tts
    )

    @Test
    fun startsOnTheCurrentWeek() {
        val state = viewModel().state.value

        assertEquals(ShoppingListRangeMode.WEEK, state.mode)
        assertEquals(weekStart, state.rangeStart)
        assertEquals(weekEnd, state.rangeEnd)
        assertFalse(state.isLoading)
        assertTrue(state.isEmpty)
    }

    @Test
    fun buildsTheListFromThePlannedMealsIngredients() {
        val adobo = recipe(ingredients = listOf(ingredientLine("Garlic", 3.0), ingredientLine("Soy sauce", 2.0, MeasurementUnit.TABLESPOON)))
        val sinangag = recipe(ingredients = listOf(ingredientLine("garlic ", 2.0)))
        recipes.recipes.value = listOf(adobo, sinangag)
        mealPlan.slots.value = listOf(
            slot(weekStart, MealType.DINNER, adobo),
            slot(weekStart.plus(1, DateTimeUnit.DAY), MealType.BREAKFAST, sinangag)
        )

        val items = viewModel().state.value.items

        assertEquals(listOf("Garlic" to 5.0, "Soy sauce" to 2.0), items.map { it.name to it.totalQuantity })
    }

    @Test
    fun followsSettingsIngredientsAndGroups() {
        val vm = viewModel()
        assertEquals("$", vm.state.value.currencySymbol)
        assertEquals(listOf("Rice"), vm.state.value.availableIngredients.map { it.name })

        settings.settings.value = AppSettings(currency = Currency.PHP, readAloudEnabled = true)
        groups.groups.value = listOf(ShoppingGroup("g", "Produce", 0))

        assertEquals("₱", vm.state.value.currencySymbol)
        assertTrue(vm.state.value.readAloudAvailable)
        assertEquals(listOf("Produce"), vm.state.value.groups.map { it.name })
    }

    @Test
    fun loadFailureShowsAnError() {
        shopping.loadFailure = IllegalStateException("offline")

        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertTrue(state.errorMessage!!.contains("offline"))
    }

    @Test
    fun itemActionsUseTheCurrentRange() {
        shopping.setItems(weekStart, weekEnd, listOf(shoppingItem("rice"), shoppingItem("salt")))
        val vm = viewModel()

        vm.toggleItem("rice", true)
        vm.recordItemPrice("rice", 4.5)
        vm.removeItem("salt")

        assertEquals(FakeShoppingListRepository.CheckedCall("rice", weekStart, weekEnd, true), shopping.checkedCalls.single())
        assertEquals("rice" to 4.5, shopping.priceCalls.single())
        assertEquals("salt" to (weekStart to weekEnd), shopping.removedItems.single())
        val rice = vm.state.value.items.single()
        assertTrue(rice.checked)
        assertEquals(4.5, rice.price)
    }

    @Test
    fun addingAnItemSavesItTrimmedAndKeepsThePickerOpenForTheNext() {
        val vm = viewModel()
        vm.openAddItemDialog()
        assertTrue(vm.state.value.showAddItemDialog)

        vm.addItem("  Eggs ", 12.0, MeasurementUnit.PIECE)

        val (item, range) = shopping.manualItems.single()
        assertEquals("Eggs", item.name)
        assertEquals(12.0, item.totalQuantity)
        assertEquals(weekStart to weekEnd, range)
        assertTrue(vm.state.value.showAddItemDialog)
        assertEquals(listOf("Eggs"), vm.state.value.items.map { it.name })

        vm.dismissAddItemDialog()
        assertFalse(vm.state.value.showAddItemDialog)
    }

    @Test
    fun deleteNeedsConfirmation() {
        val milk = shoppingItem("milk")
        shopping.setItems(weekStart, weekEnd, listOf(milk))
        val vm = viewModel()

        vm.requestDeleteItem(milk)
        assertEquals(milk, vm.state.value.itemPendingDeletion)
        vm.dismissDeleteConfirmation()
        assertNull(vm.state.value.itemPendingDeletion)
        assertTrue(shopping.removedItems.isEmpty())

        vm.requestDeleteItem(milk)
        vm.confirmDeleteItem()
        assertNull(vm.state.value.itemPendingDeletion)
        assertEquals("milk", shopping.removedItems.single().first)
    }

    @Test
    fun priceDialogOpensAndClosesAfterRecording() {
        val milk = shoppingItem("milk")
        shopping.setItems(weekStart, weekEnd, listOf(milk))
        val vm = viewModel()

        vm.openRecordPriceDialog(milk)
        assertEquals(milk, vm.state.value.itemBeingPriced)

        vm.recordItemPrice("milk", 2.0)
        assertNull(vm.state.value.itemBeingPriced)

        vm.openRecordPriceDialog(milk)
        vm.dismissRecordPriceDialog()
        assertNull(vm.state.value.itemBeingPriced)
    }

    @Test
    fun editingAnAmountUpdatesThatItemAndClosesTheEditor() {
        shopping.setItems(weekStart, weekEnd, listOf(shoppingItem("rice", quantity = 1.5, unit = MeasurementUnit.KILOGRAMS, price = 3.0)))
        val vm = viewModel()
        val rice = vm.state.value.items.single()

        vm.openEditAmount(rice)
        assertEquals(rice, vm.state.value.itemBeingEdited)
        vm.updateItemAmount(2.0, MeasurementUnit.KILOGRAMS)

        assertNull(vm.state.value.itemBeingEdited)
        val edited = vm.state.value.items.single()
        assertEquals(2.0, edited.totalQuantity)
        assertEquals(3.0, edited.price)
    }

    @Test
    fun selectingItemsThenConfirmingRemovesAllOfThemOnce() {
        shopping.setItems(weekStart, weekEnd, listOf(shoppingItem("rice"), shoppingItem("salt"), shoppingItem("oil")))
        val vm = viewModel()

        vm.startSelection("rice")
        vm.toggleSelection("salt")
        assertTrue(vm.state.value.isSelecting)
        assertEquals(setOf("rice", "salt"), vm.state.value.selectedItemKeys)

        vm.requestRemoveSelected()
        assertTrue(vm.state.value.confirmingRemoveSelected)
        assertTrue(shopping.removedItems.isEmpty())

        vm.confirmRemoveSelected()

        assertEquals(setOf("rice", "salt"), shopping.removedItems.map { it.first }.toSet())
        assertEquals(listOf("oil"), vm.state.value.items.map { it.itemKey })
        assertFalse(vm.state.value.isSelecting)
        assertFalse(vm.state.value.confirmingRemoveSelected)
    }

    @Test
    fun cancellingOrEmptyingTheSelectionLeavesSelectionModeWithoutRemoving() {
        shopping.setItems(weekStart, weekEnd, listOf(shoppingItem("rice"), shoppingItem("salt")))
        val vm = viewModel()

        vm.startSelection("rice")
        vm.toggleSelection("rice")
        assertFalse(vm.state.value.isSelecting)

        vm.startSelection("salt")
        vm.requestRemoveSelected()
        vm.dismissRemoveSelected()
        vm.clearSelection()

        assertFalse(vm.state.value.isSelecting)
        assertTrue(shopping.removedItems.isEmpty())
    }

    @Test
    fun changingTheRangeClearsTheSelection() {
        val vm = viewModel()
        vm.startSelection("rice")

        vm.nextWeek()

        assertFalse(vm.state.value.isSelecting)
    }

    @Test
    fun assigningAGroupUsesThePickedItem() {
        val milk = shoppingItem("milk")
        val vm = viewModel()

        vm.assignGroup("dairy")
        assertTrue(groups.assignments.isEmpty())

        vm.openGroupPicker(milk)
        vm.assignGroup("dairy")

        assertEquals("milk" to "dairy", groups.assignments.single())
        assertNull(vm.state.value.itemPendingGroupAssignment)
    }

    @Test
    fun removingAnItemFromItsGroupAssignsNull() {
        val vm = viewModel()
        vm.openGroupPicker(shoppingItem("milk", groupId = "dairy"))

        vm.assignGroup(null)

        assertEquals("milk" to null, groups.assignments.single())
    }

    @Test
    fun createGroupAndAssignCreatesTheGroupThenAssigns() {
        val vm = viewModel()
        vm.openGroupPicker(shoppingItem("milk"))

        vm.createGroupAndAssign("  Dairy ")

        val created = groups.groups.value.single()
        assertEquals("Dairy", created.name)
        assertEquals("milk" to created.id, groups.assignments.single())
        assertNull(vm.state.value.itemPendingGroupAssignment)
    }

    @Test
    fun groupPickerCanBeDismissed() {
        val vm = viewModel()
        vm.openGroupPicker(shoppingItem("milk"))

        vm.dismissGroupPicker()

        assertNull(vm.state.value.itemPendingGroupAssignment)
    }

    @Test
    fun groupManagementRenamesAndDeletesWithConfirmation() {
        val produce = ShoppingGroup("produce", "Produce", 0)
        groups.groups.value = listOf(produce)
        val vm = viewModel()

        vm.openGroupManagement()
        assertTrue(vm.state.value.groupManagementOpen)

        vm.renameGroup("produce", " Veg ")
        assertEquals("produce" to "Veg", groups.renamed.single())

        vm.requestDeleteGroup(produce)
        vm.dismissDeleteGroupConfirmation()
        assertTrue(groups.deleted.isEmpty())

        vm.requestDeleteGroup(produce)
        vm.confirmDeleteGroup()
        assertEquals(listOf("produce"), groups.deleted)
        assertNull(vm.state.value.groupPendingDeletion)

        vm.dismissGroupManagement()
        assertFalse(vm.state.value.groupManagementOpen)
    }

    @Test
    fun weekAndDayNavigation() {
        val vm = viewModel()

        vm.nextWeek()
        assertEquals(weekStart.plus(7, DateTimeUnit.DAY), vm.state.value.rangeStart)
        assertEquals(weekEnd.plus(7, DateTimeUnit.DAY), vm.state.value.rangeEnd)
        vm.previousWeek()
        assertEquals(weekStart, vm.state.value.rangeStart)

        vm.selectMode(ShoppingListRangeMode.DAY)
        assertEquals(today(), vm.state.value.rangeStart)
        assertEquals(today(), vm.state.value.rangeEnd)

        vm.nextDay()
        assertEquals(today().plus(1, DateTimeUnit.DAY), vm.state.value.rangeStart)
        assertEquals(vm.state.value.rangeStart, vm.state.value.rangeEnd)
        vm.previousDay()
        assertEquals(today(), vm.state.value.rangeStart)
        assertEquals(today() to today(), shopping.requestedRanges.last())
    }

    @Test
    fun selectingTheCurrentModeAgainDoesNothing() {
        val vm = viewModel()
        vm.nextWeek()
        val before = vm.state.value.rangeStart

        vm.selectMode(ShoppingListRangeMode.WEEK)

        assertEquals(before, vm.state.value.rangeStart)
    }

    @Test
    fun rangeModeStartsOnTheCurrentWeekAndAcceptsReversedDates() {
        val vm = viewModel()

        vm.selectMode(ShoppingListRangeMode.RANGE)
        assertEquals(ShoppingListRangeMode.RANGE, vm.state.value.mode)
        assertEquals(weekStart, vm.state.value.rangeStart)
        assertEquals(weekEnd, vm.state.value.rangeEnd)

        vm.selectRange(LocalDate(2030, 3, 20), LocalDate(2030, 3, 1))
        assertEquals(LocalDate(2030, 3, 1), vm.state.value.rangeStart)
        assertEquals(LocalDate(2030, 3, 20), vm.state.value.rangeEnd)
    }

    @Test
    fun goToDateAndTodayDependOnTheMode() {
        val vm = viewModel()
        val target = LocalDate(2030, 5, 15)

        vm.goToDate(target)
        assertEquals(LocalDate(2030, 5, 12) to LocalDate(2030, 5, 18), vm.state.value.rangeStart to vm.state.value.rangeEnd)
        vm.goToToday()
        assertEquals(weekStart, vm.state.value.rangeStart)

        vm.selectMode(ShoppingListRangeMode.DAY)
        vm.goToDate(target)
        assertEquals(target to target, vm.state.value.rangeStart to vm.state.value.rangeEnd)
        vm.goToToday()
        assertEquals(today() to today(), vm.state.value.rangeStart to vm.state.value.rangeEnd)

        vm.selectRange(LocalDate(2030, 1, 1), LocalDate(2030, 1, 10))
        vm.goToDate(target)
        vm.goToToday()
        assertEquals(LocalDate(2030, 1, 1) to LocalDate(2030, 1, 10), vm.state.value.rangeStart to vm.state.value.rangeEnd)
    }

    @Test
    fun readAloudSpeaksOnlyUncheckedItems() {
        shopping.setItems(
            weekStart,
            weekEnd,
            listOf(
                shoppingItem("rice", name = "Rice", quantity = 2.0, unit = MeasurementUnit.KILOGRAMS),
                shoppingItem("salt", name = "Salt", checked = true),
                shoppingItem("eggs", name = "Eggs", quantity = 12.0)
            )
        )
        val vm = viewModel()

        vm.toggleReadAloud()
        assertEquals("2 kg Rice. 12 pieces Eggs", tts.spoken.single())
        assertTrue(vm.state.value.isSpeaking)

        tts.finishCurrent()
        assertFalse(vm.state.value.isSpeaking)

        vm.toggleReadAloud()
        vm.toggleReadAloud()
        assertEquals(1, tts.stopCount)
        assertFalse(vm.state.value.isSpeaking)
    }

    @Test
    fun readAloudDoesNothingWhenEverythingIsChecked() {
        shopping.setItems(weekStart, weekEnd, listOf(shoppingItem("salt", checked = true)))
        val vm = viewModel()

        vm.toggleReadAloud()

        assertTrue(tts.spoken.isEmpty())
        assertFalse(vm.state.value.isSpeaking)
    }
}
