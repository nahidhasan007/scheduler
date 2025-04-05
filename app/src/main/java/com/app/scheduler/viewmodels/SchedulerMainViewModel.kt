package com.app.scheduler.viewmodels

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.scheduler.backgroundservice.AppScheduleLauncher
import com.app.scheduler.datalayer.AppSchedule
import com.app.scheduler.network.local.ScheduleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SchedulerMainViewModel(private val dao: ScheduleDao) : ViewModel() {

    val scheduleList = MutableStateFlow<List<AppSchedule>>(emptyList())

    private val _installedApps = MutableStateFlow<List<ApplicationInfo>>(emptyList())
    val installedApps = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps = _isLoadingApps.asStateFlow()

    val selectedApp = MutableStateFlow<String?>(null)

    init {
        fetchSchedules()
    }

    private fun fetchSchedules() {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleList.value = dao.getAllSchedules()
        }
    }

    fun setSelectedApp(packageName: String) {
        selectedApp.value = packageName
    }

    fun scheduleApp(context: Context, packageName: String, time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = AppSchedule(packageName = packageName, scheduleTime = time)
            dao.insertSchedule(schedule)
            setAlarm(context, schedule)
            scheduleList.value = dao.getAllSchedules()
        }
    }

    fun cancelSchedule(context: Context, appSchedule: AppSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteSchedule(appSchedule.id)
            cancelAlarm(context, appSchedule)
            scheduleList.value = dao.getAllSchedules()
        }
    }

    fun rescheduleApp(context: Context, appSchedule: AppSchedule, newTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            cancelSchedule(context, appSchedule)
            val newSchedule = AppSchedule(packageName = appSchedule.packageName, scheduleTime = newTime)
            dao.insertSchedule(newSchedule)
            setAlarm(context, newSchedule)
            scheduleList.value = dao.getAllSchedules()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(context: Context, schedule: AppSchedule) {
        Log.e(TAG, "Sending intent for package: ${schedule.packageName}")
        val intent = Intent(context, AppScheduleLauncher::class.java).apply {
            putExtra(PACKAGENAME, schedule.packageName)
            putExtra(SCHEDULEID, schedule.id)
            data = Uri.parse("appschedule://${schedule.id}/${schedule.packageName}")
        }
        val requestCode = schedule.id
            //(schedule.id.toString() + System.currentTimeMillis().toString()).hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, schedule.scheduleTime, pendingIntent
        )

    }


    private fun cancelAlarm(context: Context, appSchedule: AppSchedule) {
        Log.e(TAG, "Deleting intent")
        val intent = Intent(context, AppScheduleLauncher::class.java).apply {
            putExtra(PACKAGENAME, appSchedule.packageName)
            putExtra(SCHEDULEID, appSchedule.id)
            data = Uri.parse("appschedule://${appSchedule.id}/${appSchedule.packageName}")
        }
        val requestCode = appSchedule.id
          //  (appSchedule.id.toString() + System.currentTimeMillis().toString()).hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun updateAlarm(context: Context, appSchedule: AppSchedule, newTime: Long) {
        val newSchedule = appSchedule.copy(scheduleTime = newTime)
        setAlarm(context, newSchedule)
    }

    fun loadInstalledApps(context: Context) {
        if (_installedApps.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoadingApps.value = true
            withContext(Dispatchers.IO) {
                val packageManager = context.packageManager
                val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
                    .sortedBy { it.loadLabel(packageManager).toString().lowercase() }
                _installedApps.value = apps
                _isLoadingApps.value = false
            }
        }
    }

    suspend fun getScheduleById(id: Int): AppSchedule? {
        return dao.getScheduleById(id)
    }

    companion object {
        private const val TAG = "SchedulerMainViewModel"
        const val PACKAGENAME = "packageName"
        const val SCHEDULEID = "scheduleId"
    }
}


