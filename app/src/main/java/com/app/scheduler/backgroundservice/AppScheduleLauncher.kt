package com.app.scheduler.backgroundservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.scheduler.network.local.SchedulerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppScheduleLauncher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val packageName = intent?.getStringExtra("packageName")
        val scheduleId = intent?.getIntExtra("scheduleId", -1)

        if (!packageName.isNullOrEmpty() && scheduleId != -1) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)

            // Mark as executed in the database
            CoroutineScope(Dispatchers.IO).launch {
                val db = SchedulerDatabase.getDatabase(context)
                db.schedulerDao().markAsExecuted(scheduleId!!)
            }
        }
    }
}