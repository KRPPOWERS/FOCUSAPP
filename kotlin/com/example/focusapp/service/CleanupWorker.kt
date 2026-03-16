package com.example.focusapp.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.focusapp.database.FocusAppDatabase

/**
 * Periodic worker that cleans up old database records
 * Runs daily to remove entries older than 30 days
 */
class CleanupWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = FocusAppDatabase.getDatabase(applicationContext)
            val thirtyDaysAgoMs = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)

            // Clean old app usage records
            database.appUsageDao().deleteOldRecords(thirtyDaysAgoMs)

            // Clean old warning records
            database.warningDao().deleteOldWarnings(thirtyDaysAgoMs)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
