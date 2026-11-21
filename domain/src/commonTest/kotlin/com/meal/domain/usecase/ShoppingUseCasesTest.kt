package com.meal.domain.usecase

import com.meal.domain.fakes.FakeShoppingGroupRepository
import com.meal.domain.fakes.FakeShoppingListRepository
import com.meal.domain.fakes.ItemCall
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShoppingUseCasesTest {

    private val start = LocalDate(2026, 11, 8)
    private val end = LocalDate(2026, 11, 14)
    private val list = FakeShoppingListRepository()
    private val groups = FakeShoppingGroupRepository()

    @Test
    fun addingAnItemTidiesItsNameAndStartsUnchecked() = runTest {
        AddShoppingListItemUseCase(list).execute("  fish sauce ", 2.0, MeasurementUnit.TABLESPOON, start, end)

        val (item, rangeStart, rangeEnd) = list.addedItems.single()
        assertEquals("Fish Sauce", item.name)
        assertEquals(2.0, item.totalQuantity)
        assertEquals(MeasurementUnit.TABLESPOON, item.unit)
        assertFalse(item.checked)
        assertNull(item.price)
        assertNull(item.groupId)
        assertTrue(item.itemKey.isNotBlank())
        assertEquals(start, rangeStart)
        assertEquals(end, rangeEnd)
    }

    @Test
    fun addedItemsGetDistinctKeysEvenWithTheSameName() = runTest {
        val useCase = AddShoppingListItemUseCase(list)

        useCase.execute("Rice", 1.0, MeasurementUnit.CUP, start, end)
        useCase.execute("Rice", 1.0, MeasurementUnit.CUP, start, end)

        assertNotEquals(list.addedItems[0].first.itemKey, list.addedItems[1].first.itemKey)
    }

    @Test
    fun togglingAnItemPassesItsKeyRangeAndState() = runTest {
        ToggleShoppingListItemUseCase(list).execute("garlic|PIECE", start, end, checked = true)

        assertEquals(ItemCall("garlic|PIECE", start, end, true), list.checkedCalls.single())
    }

    @Test
    fun removingAnItemPassesItsKeyAndRange() = runTest {
        RemoveShoppingListItemUseCase(list).execute("garlic|PIECE", start, end)

        assertEquals(ItemCall("garlic|PIECE", start, end), list.removedCalls.single())
    }

    @Test
    fun recordingAPricePassesItsKeyRangeAndAmount() = runTest {
        RecordItemPriceUseCase(list).execute("garlic|PIECE", start, end, 3.75)

        assertEquals(ItemCall("garlic|PIECE", start, end, 3.75), list.priceCalls.single())
    }

    @Test
    fun creatingAGroupTrimsItsName() = runTest {
        val group = CreateShoppingGroupUseCase(groups).execute("  Produce  ")

        assertEquals("Produce", group.name)
        assertEquals(listOf(group), groups.groups.value)
    }

    @Test
    fun renamingAGroupTrimsTheNewName() = runTest {
        val group = groups.createGroup("Produce")

        RenameShoppingGroupUseCase(groups).execute(group.id, " Vegetables ")

        assertEquals("Vegetables", groups.groups.value.single().name)
    }

    @Test
    fun deletingAGroupRemovesIt() = runTest {
        val produce = groups.createGroup("Produce")
        val dairy = groups.createGroup("Dairy")

        DeleteShoppingGroupUseCase(groups).execute(produce.id)

        assertEquals(listOf(dairy), groups.groups.value)
    }

    @Test
    fun assigningAndUnassigningAnItemToAGroup() = runTest {
        val useCase = AssignItemToGroupUseCase(groups)

        useCase.execute("garlic|PIECE", "group-1")
        assertEquals("group-1", groups.assignments["garlic|PIECE"])

        useCase.execute("garlic|PIECE", null)
        assertTrue(groups.assignments.containsKey("garlic|PIECE"))
        assertNull(groups.assignments["garlic|PIECE"])
    }

    @Test
    fun gettingGroupsReturnsTheStoredGroups() = runTest {
        val stored = listOf(ShoppingGroup("group-1", "Produce", 0))
        groups.groups.value = stored

        assertEquals(stored, GetShoppingGroupsUseCase(groups).execute().first())
    }
}
