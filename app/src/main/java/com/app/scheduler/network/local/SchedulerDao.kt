package com.app.scheduler.network.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.scheduler.datalayer.AppSchedule

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: AppSchedule)

    @Query("DELETE FROM app_schedule WHERE id = :id")
    suspend fun deleteSchedule(id: Int)

    @Query("UPDATE app_schedule SET scheduleTime = :newTime WHERE id = :id")
    suspend fun updateSchedule(id: Int, newTime: Long)

    @Query("SELECT * FROM app_schedule WHERE executed = 0 ORDER BY scheduleTime ASC")
    suspend fun getPendingSchedules(): List<AppSchedule>

    @Query("SELECT * FROM app_schedule WHERE scheduleTime ORDER BY scheduleTime DESC")
    suspend fun getAllSchedules(): List<AppSchedule>

    @Query("UPDATE app_schedule SET executed = 1 WHERE id = :id")
    suspend fun markAsExecuted(id: Int)

    @Query("SELECT * FROM app_schedule WHERE id = :id")
    suspend fun getScheduleById(id: Int): AppSchedule?
}