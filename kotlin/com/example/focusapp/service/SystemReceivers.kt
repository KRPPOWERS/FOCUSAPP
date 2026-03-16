package com.example.focusapp.service

import android.app.admin.DeviceAdminReceiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Device Admin Receiver - allows more control over device features
 * Used for advanced blocking mechanisms
 */
class DeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        // Called when device admin is enabled
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        // Called when device admin is disabled
    }

    override fun onPasswordChanged(context: Context, intent: Intent) {
        super.onPasswordChanged(context, intent)
    }
}

/**
 * Boot Receiver - starts the focus service when device boots
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context?.let {
                // Start accessibility service on boot
                val startIntent = Intent(it, FocusAccessibilityService::class.java)
                it.startService(startIntent)
            }
        }
    }
}
