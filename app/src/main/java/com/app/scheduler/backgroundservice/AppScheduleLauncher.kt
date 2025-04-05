package com.app.scheduler.backgroundservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.scheduler.network.local.SchedulerDatabase
import com.app.scheduler.viewmodels.SchedulerMainViewModel.Companion.PACKAGENAME
import com.app.scheduler.viewmodels.SchedulerMainViewModel.Companion.SCHEDULEID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppScheduleLauncher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.e(TAG, "in receiver")
        val packageName = intent?.getStringExtra(PACKAGENAME)
        val scheduleId = intent?.getIntExtra(SCHEDULEID, -1)

        Log.e(TAG, "Intent: $intent")

        if (!packageName.isNullOrEmpty() && scheduleId != -1) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                Log.e(TAG, "Launching app: $packageName")
            } else {
                Log.e(TAG, "Launch intent is null for: $packageName")
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = SchedulerDatabase.getDatabase(context)
                Log.e(TAG, "Marking schedule as executed id: $scheduleId")
                db.schedulerDao().markAsExecuted(scheduleId!!)
            }
        }
    }

    companion object {
        const val TAG = "scheduler"
    }
}