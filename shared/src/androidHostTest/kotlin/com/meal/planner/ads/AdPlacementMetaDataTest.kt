package com.meal.planner.ads

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdPlacementMetaDataTest {

    // Host tests run with the module directory as the working directory.
    private val appManifest = File("../androidApp/src/main/AndroidManifest.xml").readText()

    @Test
    fun everyPlacementHasItsOwnAdUnitKey() {
        val keys = AdPlacement.entries.map { it.metaDataKey() }
        assertEquals(keys.size, keys.toSet().size, "Placements share an ad unit key: $keys")
    }

    @Test
    fun everyPlacementKeyIsDeclaredInTheAppManifest() {
        AdPlacement.entries.forEach { placement ->
            val key = placement.metaDataKey()
            assertTrue(
                "android:name=\"$key\"" in appManifest,
                "$placement reads ad unit ID from $key, which androidApp's manifest does not declare"
            )
        }
    }
}
