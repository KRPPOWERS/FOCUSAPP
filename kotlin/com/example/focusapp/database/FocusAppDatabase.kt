package com.example.focusapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.focusapp.model.*

@Database(
    entities = [
        FocusTimeSchedule::class,
        RestrictedApp::class,
        AppUsageRecord::class,
        WarningRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FocusAppDatabase : RoomDatabase() {
    abstract fun focusScheduleDao(): FocusScheduleDao
    abstract fun restrictedAppDao(): RestrictedAppDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun warningDao(): WarningDao

    companion object {
        @Volatile
        private var Instance: FocusAppDatabase? = null

        fun getDatabase(context: Context): FocusAppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FocusAppDatabase::class.java,
                    "focus_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
