package com.example.focusapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.focusapp.database.FocusAppDatabase
import com.example.focusapp.model.FocusTimeSchedule
import com.example.focusapp.model.RestrictedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class FocusViewModel(private val database: FocusAppDatabase) : ViewModel() {
    
    // Expose database flows to UI
    val allSchedules: Flow<List<FocusTimeSchedule>> = 
        database.focusScheduleDao().getAllSchedules()
    
    val activeSchedules: Flow<List<FocusTimeSchedule>> = 
        database.focusScheduleDao().getActiveSchedules()

    /**
     * Add a new focus time schedule
     */
    fun addSchedule(name: String, startTime: String, endTime: String, daysOfWeek: String) {
        viewModelScope.launch {
            val schedule = FocusTimeSchedule(
                name = name,
                startTime = startTime,
                endTime = endTime,
                daysOfWeek = daysOfWeek,
                isActive = true
            )
            database.focusScheduleDao().insert(schedule)
        }
    }

    /**
     * Update an existing schedule
     */
    fun updateSchedule(schedule: FocusTimeSchedule) {
        viewModelScope.launch {
            database.focusScheduleDao().update(schedule)
        }
    }

    /**
     * Delete a schedule
     */
    fun deleteSchedule(schedule: FocusTimeSchedule) {
        viewModelScope.launch {
            database.focusScheduleDao().delete(schedule)
        }
    }

    /**
     * Add a restricted app to a focus schedule
     */
    fun addRestrictedApp(packageName: String, appName: String, scheduleId: Int) {
        viewModelScope.launch {
            val app = RestrictedApp(
                packageName = packageName,
                appName = appName,
                focusScheduleId = scheduleId
            )
            database.restrictedAppDao().insert(app)
        }
    }

    /**
     * Remove a restricted app
     */
    fun removeRestrictedApp(appId: Int) {
        viewModelScope.launch {
            // Need to get the app first to delete it
        }
    }

    /**
     * Get apps restricted for a specific schedule
     */
    fun getAppsForSchedule(scheduleId: Int): Flow<List<RestrictedApp>> {
        return database.restrictedAppDao().getAppsForSchedule(scheduleId)
    }
}

class FocusViewModelFactory(private val database: FocusAppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FocusViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
