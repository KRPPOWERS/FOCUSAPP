package com.example.focusapp.database

import androidx.room.*
import com.example.focusapp.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: FocusTimeSchedule)

    @Update
    suspend fun update(schedule: FocusTimeSchedule)

    @Delete
    suspend fun delete(schedule: FocusTimeSchedule)

    @Query("SELECT * FROM focus_schedules WHERE isActive = 1 ORDER BY startTime ASC")
    fun getActiveSchedules(): Flow<List<FocusTimeSchedule>>

    @Query("SELECT * FROM focus_schedules ORDER BY startTime ASC")
    fun getAllSchedules(): Flow<List<FocusTimeSchedule>>

    @Query("SELECT * FROM focus_schedules WHERE id = :id")
    suspend fun getScheduleById(id: Int): FocusTimeSchedule?
}

@Dao
interface RestrictedAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: RestrictedApp)

    @Update
    suspend fun update(app: RestrictedApp)

    @Delete
    suspend fun delete(app: RestrictedApp)

    @Query("SELECT * FROM restricted_apps WHERE focusScheduleId = :scheduleId")
    fun getAppsForSchedule(scheduleId: Int): Flow<List<RestrictedApp>>

    @Query("SELECT * FROM restricted_apps WHERE packageName = :packageName")
    suspend fun getRestrictedApp(packageName: String): RestrictedApp?

    @Query("SELECT DISTINCT packageName FROM restricted_apps")
    suspend fun getAllRestrictedPackages(): List<String>

    @Query("SELECT * FROM restricted_apps WHERE focusScheduleId IN (SELECT id FROM focus_schedules WHERE isActive = 1)")
    suspend fun getActiveRestrictedApps(): List<RestrictedApp>
}

@Dao
interface AppUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AppUsageRecord)

    @Query("SELECT * FROM app_usage_tracking WHERE packageName = :packageName AND date > :since")
    suspend fun getUsageSince(packageName: String, since: Long): List<AppUsageRecord>

    @Query("SELECT SUM(usageTimeMs) FROM app_usage_tracking WHERE packageName = :packageName AND isFocusTime = 0")
    suspend fun getTotalNonFocusUsage(packageName: String): Long?

    @Query("SELECT SUM(usageTimeMs) FROM app_usage_tracking WHERE packageName = :packageName AND date > :since")
    suspend fun getRecentUsage(packageName: String, since: Long): Long?

    @Query("DELETE FROM app_usage_tracking WHERE date < :cutoff")
    suspend fun deleteOldRecords(cutoff: Long)
}

@Dao
interface WarningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: WarningRecord)

    @Query("SELECT warningCount FROM warning_history WHERE packageName = :packageName AND timestamp > :since LIMIT 1")
    suspend fun getRecentWarningCount(packageName: String, since: Long): Int?

    @Query("SELECT * FROM warning_history WHERE packageName = :packageName ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastWarning(packageName: String): WarningRecord?

    @Query("DELETE FROM warning_history WHERE timestamp < :cutoff")
    suspend fun deleteOldWarnings(cutoff: Long)
}
