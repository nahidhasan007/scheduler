package com.app.scheduler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.scheduler.backgroundservice.AppScheduleLauncher
import com.app.scheduler.datalayer.AppSchedule
import com.app.scheduler.network.local.ScheduleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SchedulerMainViewModel(private val dao: ScheduleDao) : ViewModel() {

    val schedules = MutableStateFlow<List<AppSchedule>>(emptyList())

    init {
        fetchSchedules()
    }

    private fun fetchSchedules() {
        viewModelScope.launch(Dispatchers.IO) {
            schedules.value = dao.getPendingSchedules()
        }
    }

    fun scheduleApp(context: Context, packageName: String, time: Long) {
        viewModelScope.launch {
            val schedule = AppSchedule(packageName = packageName, scheduleTime = time)
            dao.insertSchedule(schedule)
            setAlarm(context, schedule)
        }
    }

    fun cancelSchedule(context: Context, id: Int) {
        viewModelScope.launch {
            dao.deleteSchedule(id)
            cancelAlarm(context, id)
        }
    }

    fun rescheduleApp(context: Context, id: Int, newTime: Long) {
        viewModelScope.launch {
            dao.updateSchedule(id, newTime)
            updateAlarm(context, id, newTime)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(context: Context, schedule: AppSchedule) {
        Log.e("Scheduler", "Sending intent")
        val intent = Intent(context, AppScheduleLauncher::class.java).apply {
            putExtra("packageName", schedule.packageName)
            putExtra("scheduleId", schedule.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, schedule.id, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, schedule.scheduleTime, pendingIntent
        )
    }

    private fun cancelAlarm(context: Context, id: Int) {
        Log.e("Scheduler", "Deleting intent")
        val intent = Intent(context, AppScheduleLauncher::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, id, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun updateAlarm(context: Context, id: Int, newTime: Long) {
        cancelAlarm(context, id)
        val schedule = AppSchedule(id = id, scheduleTime = newTime, packageName = "")
        setAlarm(context, schedule)
    }
}

class SchedulerMainViewModelFactory(private val dao: ScheduleDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchedulerMainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SchedulerMainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
