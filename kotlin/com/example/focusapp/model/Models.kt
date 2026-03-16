package com.example.focusapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_schedules")
data class FocusTimeSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,           // e.g., "Morning Focus"
    val startTime: String,      // e.g., "09:00" (24-hour format)
    val endTime: String,        // e.g., "12:00"
    val isActive: Boolean = true,
    val daysOfWeek: String,     // Comma-separated: "0,1,2,3,4" (0=Sun, 6=Sat)
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "restricted_apps")
data class RestrictedApp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String,    // e.g., "com.facebook.katana"
    val appName: String,        // e.g., "Facebook"
    val appIcon: String = "",   // Base64 encoded icon
    val focusScheduleId: Int,   // Links to FocusTimeSchedule
    val blockType: String = "WARNING" // WARNING or GAME
)

@Entity(tableName = "app_usage_tracking")
data class AppUsageRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String,
    val appName: String,
    val usageTimeMs: Long,      // Time spent in app (milliseconds)
    val date: Long = System.currentTimeMillis(),
    val isFocusTime: Boolean = false
)

@Entity(tableName = "warning_history")
data class WarningRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String,
    val warningCount: Int = 1,  // How many times warned (max 6)
    val timestamp: Long = System.currentTimeMillis(),
    val focusSessionId: Int = -1
)
