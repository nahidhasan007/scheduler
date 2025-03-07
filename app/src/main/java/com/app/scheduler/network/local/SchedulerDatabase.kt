package com.app.scheduler.network.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.scheduler.datalayer.AppSchedule

@Database(entities = [AppSchedule::class], version = 2, exportSchema = false)
abstract class SchedulerDatabase : RoomDatabase() {
    abstract fun schedulerDao(): ScheduleDao

    companion object {
        @Volatile
        private var instance: SchedulerDatabase? = null

        fun getDatabase(context: Context): SchedulerDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SchedulerDatabase::class.java,
                    "scheduler_database"
                )
                    .fallbackToDestructiveMigration()  // Prevent crashes on schema change
                    .build().also { instance = it }
            }
        }
    }
}
