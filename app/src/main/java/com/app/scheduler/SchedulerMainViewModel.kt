package com.app.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.scheduler.backgroundservice.AppScheduleLauncher
import com.app.scheduler.datalayer.AppSchedule
import com.app.scheduler.network.local.SchedulerDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SchedulerMainViewModel (private val dao: SchedulerDao) : ViewModel() {

    val schedules = MutableStateFlow<List<AppSchedule>>(emptyList())

    init {
        fetchSchedules()
    }

    private fun fetchSchedules() {
        viewModelScope.launch {
            schedules.value = dao.getAllSchedules()
        }
    }

    fun scheduleApp(context: Context, packageName: String, scheduleTime: Long) {
        viewModelScope.launch {
            val schedule = AppSchedule(packageName = packageName, scheduleTime = scheduleTime)
            dao.insert(schedule)
            setAlarm(context, packageName, scheduleTime)
            fetchSchedules()
        }
    }

    fun cancelSchedule(context: Context, scheduleId: Int) {
        viewModelScope.launch {
            dao.deleteById(scheduleId)
            cancelAlarm(context, scheduleId)
            fetchSchedules()
        }
    }

    private fun setAlarm(context: Context, packageName: String, scheduleTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AppScheduleLauncher::class.java).apply {
            putExtra("packageName", packageName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, packageName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, scheduleTime, pendingIntent)
    }

    private fun cancelAlarm(context: Context, scheduleId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AppScheduleLauncher::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, scheduleId, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }
}

class SchedulerMainViewModelFactory(private val dao: SchedulerDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchedulerMainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SchedulerMainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
