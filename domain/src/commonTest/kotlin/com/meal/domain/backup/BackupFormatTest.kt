package com.meal.domain.backup

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BackupFormatTest {

    @Test
    fun acceptsTheManifestDatabaseAndPhotos() {
        assertTrue(BackupFormat.isAllowedEntry("backup.properties"))
        assertTrue(BackupFormat.isAllowedEntry("mealplanner.db"))
        assertTrue(BackupFormat.isAllowedEntry("photos/3f2a.jpg"))
        assertEquals("3f2a.jpg", BackupFormat.photoFileName("photos/3f2a.jpg"))
    }

    @Test
    fun rejectsEntriesThatCouldEscapeTheRestoreFolder() {
        listOf(
            "../mealplanner.db",
            "photos/../../shared_prefs/x.xml",
            "photos/..",
            "photos/.",
            "photos/",
            "photos/nested/a.jpg",
            "photos/a\\..\\b.jpg",
            "/data/data/x/databases/mealplanner.db",
            "photos/C:a.jpg",
        ).forEach { name ->
            assertFalse(BackupFormat.isAllowedEntry(name), "accepted $name")
            assertNull(BackupFormat.photoFileName(name), "photo name for $name")
        }
    }

    @Test
    fun rejectsUnknownEntries() {
        assertFalse(BackupFormat.isAllowedEntry("mealplanner.db-wal"))
        assertFalse(BackupFormat.isAllowedEntry("readme.txt"))
    }

    @Test
    fun readsManifestsUpToTheCurrentSchemaVersion() {
        val manifest = mapOf("format" to "1", "schemaVersion" to "1")
        assertTrue(BackupFormat.isReadableManifest(manifest, currentSchemaVersion = 1))
        assertTrue(BackupFormat.isReadableManifest(manifest, currentSchemaVersion = 3))
    }

    @Test
    fun rejectsNewerSchemasOtherFormatsAndMissingValues() {
        assertFalse(BackupFormat.isReadableManifest(mapOf("format" to "1", "schemaVersion" to "2"), 1))
        assertFalse(BackupFormat.isReadableManifest(mapOf("format" to "2", "schemaVersion" to "1"), 1))
        assertFalse(BackupFormat.isReadableManifest(mapOf("format" to "1", "schemaVersion" to "0"), 1))
        assertFalse(BackupFormat.isReadableManifest(mapOf("format" to "1"), 1))
        assertFalse(BackupFormat.isReadableManifest(mapOf("schemaVersion" to "1"), 1))
        assertFalse(BackupFormat.isReadableManifest(mapOf("format" to "x", "schemaVersion" to "1"), 1))
        assertFalse(BackupFormat.isReadableManifest(emptyMap(), 1))
    }
}
