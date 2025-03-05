package com.app.scheduler.datalayer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_schedule")
data class AppSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val scheduleTime: Long,
    val status: String = "Scheduled"
)
