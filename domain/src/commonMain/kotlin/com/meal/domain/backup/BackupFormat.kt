package com.meal.domain.backup

/**
 * Layout of a backup file: a zip holding a manifest, the database and the user's photos.
 * Entry names are checked before anything is written, so a crafted zip cannot write
 * outside the restore folder.
 */
object BackupFormat {
    const val FORMAT_VERSION = 1
    const val MANIFEST = "backup.properties"
    const val DATABASE = "mealplanner.db"
    const val PHOTOS_DIR = "photos/"

    const val KEY_FORMAT = "format"
    const val KEY_SCHEMA_VERSION = "schemaVersion"
    const val KEY_APP_VERSION = "appVersion"
    const val KEY_CREATED_AT = "createdAt"

    /** The photo's file name for a `photos/<name>` entry, or null for any other entry. */
    fun photoFileName(entryName: String): String? {
        if (!entryName.startsWith(PHOTOS_DIR)) return null
        val name = entryName.removePrefix(PHOTOS_DIR)
        val safe = name.isNotEmpty() && name != "." && name != ".." &&
            '/' !in name && '\\' !in name && ':' !in name
        return name.takeIf { safe }
    }

    fun isAllowedEntry(entryName: String): Boolean =
        entryName == MANIFEST || entryName == DATABASE || photoFileName(entryName) != null

    /** True when the manifest describes a backup that this app's database schema can open. */
    fun isReadableManifest(manifest: Map<String, String>, currentSchemaVersion: Long): Boolean {
        val format = manifest[KEY_FORMAT]?.toIntOrNull() ?: return false
        val schemaVersion = manifest[KEY_SCHEMA_VERSION]?.toLongOrNull() ?: return false
        return format == FORMAT_VERSION && schemaVersion in 1..currentSchemaVersion
    }
}
