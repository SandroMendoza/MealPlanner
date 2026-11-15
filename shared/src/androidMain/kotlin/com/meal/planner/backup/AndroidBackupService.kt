package com.meal.planner.backup

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.net.Uri
import com.meal.data.datasource.DatabaseBackupDataSource
import com.meal.domain.backup.BackupFormat
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.service.BackupService
import com.meal.domain.service.InvalidBackupException
import com.meal.domain.service.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Properties
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val DATABASE_NAME = "mealplanner.db"
private const val PHOTO_DIR = "recipe_images"

class AndroidBackupService(
    private val context: Context,
    private val database: DatabaseBackupDataSource,
    private val reminderRepository: MealReminderRepository,
    private val reminderScheduler: ReminderScheduler
) : BackupService {

    private val photoDir get() = File(context.filesDir, PHOTO_DIR)
    private val databaseFile get() = context.getDatabasePath(DATABASE_NAME)
    private val restorePendingMarker get() = File(context.filesDir, "restore_pending")

    override suspend fun export(destinationUri: String): Unit = withContext(Dispatchers.IO) {
        database.checkpoint()
        val output = context.contentResolver.openOutputStream(Uri.parse(destinationUri), "wt")
            ?: throw IOException("Cannot write to the chosen file")
        ZipOutputStream(output.buffered()).use { zip ->
            zip.putNextEntry(ZipEntry(BackupFormat.MANIFEST))
            manifest().store(zip, null)
            zip.closeEntry()

            zip.putNextEntry(ZipEntry(BackupFormat.DATABASE))
            databaseFile.inputStream().use { it.copyTo(zip) }
            zip.closeEntry()

            photoDir.listFiles().orEmpty().filter { it.isFile && it.extension == "jpg" }.forEach { photo ->
                zip.putNextEntry(ZipEntry(BackupFormat.PHOTOS_DIR + photo.name))
                photo.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }
    }

    override suspend fun import(sourceUri: String) {
        withContext(Dispatchers.IO) {
            val staging = File(context.cacheDir, "restore").apply { deleteRecursively(); mkdirs() }
            try {
                unzip(Uri.parse(sourceUri), staging)
                validate(staging)
                replaceData(staging)
            } finally {
                staging.deleteRecursively()
            }
        }
        restartApp()
    }

    /**
     * Finishes a restore after the restart that follows it: points photo paths at this
     * device's folder and re-arms the restored reminders. Call once at app start.
     */
    fun completePendingRestoreInBackground() {
        if (!restorePendingMarker.exists()) return
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            database.rewritePhotoPaths(Uri.fromFile(photoDir).toString().trimEnd('/') + "/")
            reminderScheduler.rescheduleAll(reminderRepository.getReminders().first())
            restorePendingMarker.delete()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun manifest() = Properties().apply {
        setProperty(BackupFormat.KEY_FORMAT, BackupFormat.FORMAT_VERSION.toString())
        setProperty(BackupFormat.KEY_SCHEMA_VERSION, database.schemaVersion.toString())
        setProperty(BackupFormat.KEY_APP_VERSION, appVersionName())
        setProperty(BackupFormat.KEY_CREATED_AT, Clock.System.now().toString())
    }

    private fun appVersionName(): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()

    private fun unzip(source: Uri, staging: File) {
        val input = context.contentResolver.openInputStream(source)
            ?: throw IOException("Cannot read the chosen file")
        val photos = File(staging, "photos").apply { mkdirs() }
        ZipInputStream(input.buffered()).use { zip ->
            generateSequence { zip.nextEntry }.forEach { entry ->
                if (entry.isDirectory) return@forEach
                val name = entry.name
                if (!BackupFormat.isAllowedEntry(name)) throw InvalidBackupException("Unexpected entry $name")
                val target = BackupFormat.photoFileName(name)?.let { File(photos, it) } ?: File(staging, name)
                target.outputStream().use { zip.copyTo(it) }
            }
        }
    }

    private fun validate(staging: File) {
        val manifestFile = File(staging, BackupFormat.MANIFEST)
        val stagedDatabase = File(staging, BackupFormat.DATABASE)
        if (!manifestFile.isFile || !stagedDatabase.isFile) {
            throw InvalidBackupException("Missing manifest or database")
        }

        val properties = Properties().apply { manifestFile.inputStream().use { load(it) } }
        val manifest = properties.stringPropertyNames().associateWith { properties.getProperty(it) }
        if (!BackupFormat.isReadableManifest(manifest, database.schemaVersion)) {
            throw InvalidBackupException("Unsupported backup version")
        }

        val isMealPlannerDatabase = try {
            SQLiteDatabase.openDatabase(stagedDatabase.path, null, SQLiteDatabase.OPEN_READONLY).use { db ->
                val hasSettings = db.rawQuery(
                    "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = 'SettingsEntity'",
                    null
                ).use { it.moveToFirst() && it.getInt(0) == 1 }
                hasSettings && db.version.toLong() in 1..database.schemaVersion
            }
        } catch (e: SQLiteException) {
            false
        }
        if (!isMealPlannerDatabase) throw InvalidBackupException("Not a Meal Planner database")
    }

    /**
     * Copies the backup next to the live data first, so a failure while copying leaves the
     * current data in place; the switch itself is a delete and a same-folder rename.
     */
    private fun replaceData(staging: File) {
        val incomingDatabase = File(databaseFile.parentFile, "$DATABASE_NAME.restore")
        File(staging, BackupFormat.DATABASE).copyTo(incomingDatabase, overwrite = true)
        val incomingPhotos = File(context.filesDir, "$PHOTO_DIR.restore").apply { deleteRecursively() }
        File(staging, "photos").copyRecursively(incomingPhotos, overwrite = true)

        database.close()
        context.deleteDatabase(DATABASE_NAME)
        if (!incomingDatabase.renameTo(databaseFile)) {
            throw IOException("Could not move the restored database into place")
        }
        photoDir.deleteRecursively()
        if (!incomingPhotos.renameTo(photoDir)) {
            throw IOException("Could not move the restored photos into place")
        }
        restorePendingMarker.createNewFile()
    }

    /**
     * Every repository and screen holds the closed database, so the process is restarted
     * rather than reopened in place.
     */
    private fun restartApp(): Nothing {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (launch != null) context.startActivity(launch)
        Runtime.getRuntime().exit(0)
        throw IllegalStateException("Process did not exit")
    }
}
