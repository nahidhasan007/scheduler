package com.app.scheduler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.scheduler.network.local.ScheduleDao

class SchedulerMainViewModelFactory(private val dao: ScheduleDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchedulerMainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SchedulerMainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}