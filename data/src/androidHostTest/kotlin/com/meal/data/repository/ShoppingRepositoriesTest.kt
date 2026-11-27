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
    fun editedAmountStaysThroughASyncAndKeepsPriceCheckAndGroup() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice", quantity = 1.5)))
        list.setItemPrice("rice", weekStart, weekEnd, 4.0)
        val dry = groups.createGroup("Dry goods")
        groups.assignItemToGroup("rice", dry.id)

        list.updateItemAmount("rice", weekStart, weekEnd, 2.0, MeasurementUnit.KILOGRAMS)
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice", quantity = 1.5)))
        val rice = list.getItems(weekStart, weekEnd).first().single()

        assertEquals(2.0, rice.totalQuantity)
        assertEquals(MeasurementUnit.KILOGRAMS, rice.unit)
        assertEquals(4.0, rice.price)
        assertTrue(rice.checked)
        assertEquals(dry.id, rice.groupId)
    }

    @Test
    fun editedAmountOnlyChangesThatRange() = runTest {
        list.syncGeneratedItems(day, day, listOf(item("rice", quantity = 1.0)))
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice", quantity = 3.0)))

        list.updateItemAmount("rice", day, day, 2.0, MeasurementUnit.GRAMS)

        assertEquals(2.0, list.getItems(day, day).first().single().totalQuantity)
        assertEquals(3.0, list.getItems(weekStart, weekEnd).first().single().totalQuantity)
    }

    @Test
    fun pantryStaplesStartAtHomeAndOtherItemsDoNot() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("salt|GRAMS", "Salt"), item("rice|GRAMS", "Rice")))

        val atHome = list.getItems(weekStart, weekEnd).first().associate { it.name to it.atHome }

        assertEquals(mapOf("Rice" to false, "Salt" to true), atHome)
    }

    @Test
    fun movingAnItemIsRememberedForOtherListsByName() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("salt|GRAMS", "Salt"), item("rice|GRAMS", "Rice")))

        list.setItemsAtHome(listOf("Salt"), atHome = false)
        list.setItemsAtHome(listOf("Rice"), atHome = true)
        list.syncGeneratedItems(day, day, listOf(item("salt|PIECE", "salt "), item("rice|GRAMS", "Rice")))

        val week = list.getItems(weekStart, weekEnd).first().associate { it.name to it.atHome }
        val dayList = list.getItems(day, day).first().associate { it.name to it.atHome }
        assertEquals(mapOf("Rice" to true, "Salt" to false), week)
        assertEquals(mapOf("Rice" to true, "salt " to false), dayList)
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
    fun addingAnItemAlreadyOnTheListAddsToItsQuantity() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("taro|GRAMS", "Taro", 200.0)))

        val key = list.addManualItem(item("manual-1", "taro", 300.0), weekStart, weekEnd)

        assertEquals("taro|GRAMS", key)
        val taro = list.getItems(weekStart, weekEnd).first().single()
        assertEquals("taro|GRAMS", taro.itemKey)
        assertEquals(500.0, taro.totalQuantity)
    }

    @Test
    fun addingAnItemInAnotherUnitKeepsItsOwnRow() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("taro|GRAMS", "Taro", 200.0)))

        val key = list.addManualItem(item("manual-1", "Taro", 2.0).copy(unit = MeasurementUnit.PIECE), weekStart, weekEnd)

        assertEquals("manual-1", key)
        assertEquals(2, list.getItems(weekStart, weekEnd).first().size)
    }

    @Test
    fun itemsWithoutAMeasurementMergeByName() = runTest {
        val unmeasured = item("manual-1", "Bread").copy(totalQuantity = null, unit = null)
        list.addManualItem(unmeasured, weekStart, weekEnd)

        val key = list.addManualItem(unmeasured.copy(itemKey = "manual-2", name = "bread"), weekStart, weekEnd)

        assertEquals("manual-1", key)
        val bread = list.getItems(weekStart, weekEnd).first().single()
        assertNull(bread.totalQuantity)
        assertNull(bread.unit)
    }

    @Test
    fun clearingAnAmountRemovesTheMeasurement() = runTest {
        list.syncGeneratedItems(weekStart, weekEnd, listOf(item("rice")))

        list.updateItemAmount("rice", weekStart, weekEnd, null, null)

        val rice = list.getItems(weekStart, weekEnd).first().single()
        assertNull(rice.totalQuantity)
        assertNull(rice.unit)
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
    fun hiddenSectionsAreSavedAndForgottenWithTheirGroup() = runTest {
        val produce = groups.createGroup("Produce")

        groups.setSectionCollapsed(produce.id, collapsed = true)
        groups.setSectionCollapsed("ungrouped", collapsed = true)
        groups.setSectionCollapsed("ungrouped", collapsed = true)
        assertEquals(setOf(produce.id, "ungrouped"), groups.getCollapsedSections().first())

        groups.setSectionCollapsed("ungrouped", collapsed = false)
        groups.deleteGroup(produce.id)

        assertEquals(emptySet(), groups.getCollapsedSections().first())
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
