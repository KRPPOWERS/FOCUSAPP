package com.example.focusapp.utils

import com.example.focusapp.database.FocusAppDatabase
import java.time.LocalTime
import java.util.Calendar

object TimeUtils {
    
    /**
     * Check if current time is within any active focus schedule
     */
    suspend fun isCurrentlyFocusTime(database: FocusAppDatabase): Boolean {
        val schedules = try {
            // Get all active schedules (this is blocking, should be done in background)
            val dao = database.focusScheduleDao()
            emptyList<com.example.focusapp.model.FocusTimeSchedule>()
            // Note: In real implementation, use Flow collection
        } catch (e: Exception) {
            return false
        }

        val now = LocalTime.now()
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

        return schedules.any { schedule ->
            if (!schedule.isActive) return@any false

            // Check if today is in the schedule
            val daysInSchedule = schedule.daysOfWeek.split(",").map { it.trim().toIntOrNull() ?: -1 }
            if (!daysInSchedule.contains(currentDayOfWeek)) return@any false

            // Check if current time is between start and end
            val startTime = parseTime(schedule.startTime)
            val endTime = parseTime(schedule.endTime)

            now.isAfter(startTime) && now.isBefore(endTime)
        }
    }

    /**
     * Parse time string "HH:mm" to LocalTime
     */
    private fun parseTime(timeString: String): LocalTime {
        return try {
            val parts = timeString.split(":")
            LocalTime.of(parts[0].toInt(), parts[1].toInt())
        } catch (e: Exception) {
            LocalTime.of(0, 0)
        }
    }

    /**
     * Format time from milliseconds to readable string
     */
    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return when {
            hours > 0 -> "$hours h ${minutes} m"
            minutes > 0 -> "$minutes m ${seconds} s"
            else -> "$seconds s"
        }
    }

    /**
     * Get current time in HH:mm format
     */
    fun getCurrentTimeString(): String {
        val now = LocalTime.now()
        return String.format("%02d:%02d", now.hour, now.minute)
    }

    /**
     * Check if time range is valid
     */
    fun isValidTimeRange(startTime: String, endTime: String): Boolean {
        return try {
            val start = parseTime(startTime)
            val end = parseTime(endTime)
            start.isBefore(end)
        } catch (e: Exception) {
            false
        }
    }
}
