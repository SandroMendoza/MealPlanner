package com.meal.data.repository

import com.meal.data.datasource.ShoppingGroupLocalDataSource
import com.meal.data.datasource.ShoppingListLocalDataSource
import com.meal.data.inMemoryDatabase
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShoppingRepositoriesTest {

    private val database = inMemoryDatabase()
    private val groupSource = ShoppingGroupLocalDataSource(database, Dispatchers.Unconfined)
    private val groups = ShoppingGroupRepositoryImpl(groupSource)
    private val list = ShoppingListRepositoryImpl(ShoppingListLocalDataSource(database, groupSource, Dispatchers.Unconfined))

    private val weekStart = LocalDate(2026, 11, 8)
    private val weekEnd = LocalDate(2026, 11, 14)
    private val day = LocalDate(2026, 11, 10)

    private fun item(key: String, name: String = key.replaceFirstChar { it.uppercase() }, quantity: Double = 1.0) =
        ShoppingListItem(key, name, quantity, MeasurementUnit.GRAMS, checked = false)

    @Test
    fun syncInsertsRequiredItemsSortedByName() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice"), item("garlic")))

        val items = list.getItems(weekStart, weekEnd).first()

        assertEquals(listOf("Garlic", "Rice"), items.map { it.name })
        assertTrue(items.none { it.checked })
    }

    @Test
    fun syncKeepsExistingRowsUntouched() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice", quantity = 1.0)))
        list.setItemChecked("rice", weekStart, weekEnd, true)

        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice", quantity = 5.0), item("egg")))
        val items = list.getItems(weekStart, weekEnd).first().associateBy { it.itemKey }

        assertEquals(1.0, items.getValue("rice").totalQuantity)
        assertTrue(items.getValue("rice").checked)
        assertFalse(items.getValue("egg").checked)
    }

    @Test
    fun syncDoesNotBringBackRemovedItems() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice")))
        list.removeItem("rice", weekStart, weekEnd)

        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice")))

        assertTrue(list.getItems(weekStart, weekEnd).first().isEmpty())
    }

    @Test
    fun syncCarriesCheckedAndPriceOverFromAnOverlappingRange() = runTest {
        list.syncGeneratedItems(day, day, listOf(item("rice"), item("egg")))
        list.setItemChecked("egg", day, day, true)
        list.setItemPrice("rice", day, day, 3.5)

        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice"), item("egg"), item("salt")))
        val week = list.getItems(weekStart, weekEnd).first().associateBy { it.itemKey }

        assertTrue(week.getValue("egg").checked)
        assertTrue(week.getValue("rice").checked)
        assertEquals(3.5, week.getValue("rice").price)
        assertFalse(week.getValue("salt").checked)
        assertNull(week.getValue("salt").price)
    }

    @Test
    fun checkingAnItemAppliesToOverlappingRanges() = runTest {
        list.syncGeneratedItems(day, day, listOf(item("rice")))
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice")))

        list.setItemChecked("rice", day, day, true)

        assertTrue(list.getItems(weekStart, weekEnd).first().single().checked)
        assertTrue(list.getItems(day, day).first().single().checked)
    }

    @Test
    fun uncheckingClearsTheCheck() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice")))
        list.setItemChecked("rice", weekStart, weekEnd, true)

        list.setItemChecked("rice", weekStart, weekEnd, false)

        assertFalse(list.getItems(weekStart, weekEnd).first().single().checked)
    }

    @Test
    fun manualItemIsAddedUnchecked() = runTest {
        list.addManualItem(item("soap", "Soap", 2.0).copy(checked = true, unit = MeasurementUnit.PIECE), weekStart, weekEnd)

        val soap = list.getItems(weekStart, weekEnd).first().single()

        assertEquals("Soap", soap.name)
        assertEquals(2.0, soap.totalQuantity)
        assertEquals(MeasurementUnit.PIECE, soap.unit)
        assertFalse(soap.checked)
    }

    @Test
    fun removeHidesOnlyThatRangesRow() = runTest {
        list.syncGeneratedItems(day, day, listOf(item("rice")))
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice")))

        list.removeItem("rice", weekStart, weekEnd)

        assertTrue(list.getItems(weekStart, weekEnd).first().isEmpty())
        assertEquals(1, list.getItems(day, day).first().size)
    }

    @Test
    fun recordingAPriceAlsoChecksTheItem() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice")))

        list.setItemPrice("rice", weekStart, weekEnd, 4.25)
        val rice = list.getItems(weekStart, weekEnd).first().single()

        assertEquals(4.25, rice.price)
        assertTrue(rice.checked)
    }

    @Test
    fun pricedItemsCoverOverlappingRangesAndSkipUnpricedAndRemoved() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice"), item("egg"), item("salt")))
        list.setItemPrice("rice", weekStart, weekEnd, 4.0)
        list.setItemPrice("salt", weekStart, weekEnd, 1.0)
        list.removeItem("salt", weekStart, weekEnd)
        list.syncGeneratedItems(LocalDate(2026, 12, 1), LocalDate(2026, 12, 7), listOf(item("milk")))
        list.setItemPrice("milk", LocalDate(2026, 12, 1), LocalDate(2026, 12, 7), 2.0)

        val priced = list.getPricedItems(LocalDate(2026, 11, 1), LocalDate(2026, 11, 30)).first()

        assertEquals(listOf("rice"), priced.map { it.itemKey })
        assertEquals(4.0, priced.single().price)
        assertEquals(weekStart, priced.single().rangeStart)
        assertEquals(weekEnd, priced.single().rangeEnd)
    }

    @Test
    fun groupsGetIncreasingSortOrderAndListInThatOrder() = runTest {
        val produce = groups.createGroup("Produce")
        val dairy = groups.createGroup("Dairy")

        assertEquals(0, produce.sortOrder)
        assertEquals(1, dairy.sortOrder)
        assertEquals(listOf("Produce", "Dairy"), groups.getGroups().first().map { it.name })
    }

    @Test
    fun renamingAGroupKeepsItsPlace() = runTest {
        val produce = groups.createGroup("Produce")
        groups.createGroup("Dairy")

        groups.renameGroup(produce.id, "Fruit & Veg")

        assertEquals(listOf("Fruit & Veg", "Dairy"), groups.getGroups().first().map { it.name })
    }

    @Test
    fun assignedItemsShowTheirGroup() = runTest {
        val produce = groups.createGroup("Produce")
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("carrot"), item("rice")))

        groups.assignItemToGroup("carrot", produce.id)
        val items = list.getItems(weekStart, weekEnd).first().associateBy { it.itemKey }

        assertEquals(produce.id, items.getValue("carrot").groupId)
        assertNull(items.getValue("rice").groupId)
    }

    @Test
    fun assigningNullUngroupsTheItem() = runTest {
        val produce = groups.createGroup("Produce")
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("carrot")))
        groups.assignItemToGroup("carrot", produce.id)

        groups.assignItemToGroup("carrot", null)

        assertNull(list.getItems(weekStart, weekEnd).first().single().groupId)
    }

    @Test
    fun deletingAGroupUngroupsItsItems() = runTest {
        val produce = groups.createGroup("Produce")
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("carrot")))
        groups.assignItemToGroup("carrot", produce.id)

        groups.deleteGroup(produce.id)

        assertTrue(groups.getGroups().first().isEmpty())
        assertNull(list.getItems(weekStart, weekEnd).first().single().groupId)
    }

    @Test
    fun groupAssignmentFollowsTheItemAcrossRanges() = runTest {
        val produce = groups.createGroup("Produce")
        list.syncGeneratedItems(day, day, listOf(item("carrot")))
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("carrot")))

        groups.assignItemToGroup("carrot", produce.id)

        assertEquals(produce.id, list.getItems(day, day).first().single().groupId)
        assertEquals(produce.id, list.getItems(weekStart, weekEnd).first().single().groupId)
    }
}
