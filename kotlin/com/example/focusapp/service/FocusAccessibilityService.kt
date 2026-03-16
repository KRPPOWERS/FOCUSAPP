package com.example.focusapp.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.focusapp.database.FocusAppDatabase
import com.example.focusapp.model.AppUsageRecord
import com.example.focusapp.model.WarningRecord
import com.example.focusapp.utils.TimeUtils
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * Accessibility Service monitors:
 * 1. Current foreground app
 * 2. If it's in a focus time window
 * 3. If it's a restricted app during focus time
 * 4. Displays warnings and games accordingly
 */
class FocusAccessibilityService : AccessibilityService() {
    
    private lateinit var database: FocusAppDatabase
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // Track last foreground app to detect changes
    private var lastForegroundPackage = ""
    private var lastFocusTime = 0L
    
    // Current warning count for the session
    private var warningCountMap = mutableMapOf<String, Int>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        database = FocusAppDatabase.getDatabase(this)
        
        // Configure this accessibility service
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
        this.serviceInfo = info

        // Schedule periodic cleanup work
        scheduleCleanupWork()
        
        // Start monitoring loop
        startMonitoring()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // This is called frequently - use it for real-time detection
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            checkCurrentForegroundApp()
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    /**
     * Main monitoring loop - runs every 2 seconds to check app usage
     */
    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                try {
                    checkCurrentForegroundApp()
                    delay(2000) // Check every 2 seconds - low battery impact
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Gets current foreground app and handles restrictions
     */
    private fun checkCurrentForegroundApp() {
        val currentPackage = getForegroundPackage()
        if (currentPackage.isEmpty()) return

        // Only process if app changed or every 30 seconds
        val now = System.currentTimeMillis()
        if (currentPackage == lastForegroundPackage && (now - lastFocusTime) < 30000) {
            return
        }

        lastForegroundPackage = currentPackage
        lastFocusTime = now

        scope.launch {
            try {
                // Check if we're in a focus time
                val isFocusTime = TimeUtils.isCurrentlyFocusTime(database)
                
                // Check if this app is restricted
                val restrictedApp = database.restrictedAppDao()
                    .getRestrictedApp(currentPackage)

                when {
                    // During focus time + restricted app clicked
                    isFocusTime && restrictedApp != null -> {
                        handleRestrictedAppDuringFocus(currentPackage, restrictedApp)
                    }
                    
                    // Outside focus time but restricted app + high usage
                    !isFocusTime && restrictedApp != null -> {
                        checkAndWarnHighUsage(currentPackage)
                    }
                }

                // Track usage
                recordAppUsage(currentPackage, isFocusTime)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Handles when user clicks a restricted app during focus time
     * Shows 6 warning screens, then mini-game, then motivational message
     */
    private suspend fun handleRestrictedAppDuringFocus(
        packageName: String,
        restrictedApp: com.example.focusapp.model.RestrictedApp
    ) {
        // Get warning count for this session
        val warningCount = warningCountMap.getOrDefault(packageName, 0) + 1
        warningCountMap[packageName] = warningCount

        when {
            warningCount <= 6 -> {
                // Show motivational warning (different message for each)
                showWarningOverlay(packageName, warningCount)
            }
            warningCount == 7 -> {
                // After 6 warnings, show mini-game
                showMiniGame(packageName)
            }
            else -> {
                // Show motivational message to go back to work
                showMotivationalMessage()
            }
        }

        // Record warning
        database.warningDao().insert(
            WarningRecord(
                packageName = packageName,
                warningCount = warningCount
            )
        )
    }

    /**
     * Check if app usage (outside focus time) is too high and warn
     */
    private suspend fun checkAndWarnHighUsage(packageName: String) {
        val dailyUsageMs = database.appUsageDao()
            .getTotalNonFocusUsage(packageName) ?: 0

        // If more than 2 hours, show warning
        if (dailyUsageMs > 2 * 60 * 60 * 1000) {
            showHighUsageWarning(packageName, dailyUsageMs)
        }
    }

    /**
     * Get the currently focused app's package name
     */
    private fun getForegroundPackage(): String {
        return try {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                time - 1000, time
            )
            
            if (stats.isEmpty()) return ""
            
            stats.sortByDescending { it.lastTimeUsed }
            stats[0].packageName
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Record app usage to database
     */
    private suspend fun recordAppUsage(packageName: String, isFocusTime: Boolean) {
        database.appUsageDao().insert(
            AppUsageRecord(
                packageName = packageName,
                appName = getAppName(packageName),
                usageTimeMs = 1000, // 1 second increments
                isFocusTime = isFocusTime
            )
        )
    }

    /**
     * Get human-readable app name from package name
     */
    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    /**
     * Show warning overlay (should be shown via Activity, not here)
     * In real implementation, this would start an Activity with FLAG_KEEP_SCREEN_ON
     */
    private fun showWarningOverlay(packageName: String, warningNumber: Int) {
        val intent = Intent(this, WarningActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("warning_number", warningNumber)
            putExtra("package_name", packageName)
        }
        startActivity(intent)
    }

    /**
     * Show mini-game (1-2 minutes puzzle/game before returning)
     */
    private fun showMiniGame(packageName: String) {
        val intent = Intent(this, MiniGameActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("package_name", packageName)
        }
        startActivity(intent)
    }

    /**
     * Show motivational message to return to work
     */
    private fun showMotivationalMessage() {
        mainHandler.post {
            Toast.makeText(
                this,
                "Keep focus! You're doing great! 💪",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * High usage warning (used app too much outside focus time)
     */
    private fun showHighUsageWarning(packageName: String, usageMs: Long) {
        val hours = usageMs / (60 * 60 * 1000)
        mainHandler.post {
            Toast.makeText(
                this,
                "⏱️ You've spent ${hours}h on ${getAppName(packageName)} today",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Schedule periodic cleanup of old data
     */
    private fun scheduleCleanupWork() {
        val cleanupWork = PeriodicWorkRequestBuilder<CleanupWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "cleanup_work",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWork
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

// Placeholder Activities - we'll create full implementations
class WarningActivity
class MiniGameActivity
