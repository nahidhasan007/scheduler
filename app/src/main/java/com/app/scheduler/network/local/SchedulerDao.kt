package com.app.scheduler.network.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.scheduler.datalayer.AppSchedule

@Dao
interface SchedulerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: AppSchedule)

    @Query("SELECT * FROM app_schedule ORDER BY scheduleTime ASC")
    suspend fun getAllSchedules(): List<AppSchedule>

    @Delete
    suspend fun delete(schedule: AppSchedule)

    @Query("DELETE FROM app_schedule WHERE id = :scheduleId")
    suspend fun deleteById(scheduleId: Int)
}