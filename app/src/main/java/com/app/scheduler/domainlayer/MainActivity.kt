package com.app.scheduler.domainlayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.scheduler.navigation.AppNavigation
import com.app.scheduler.network.local.SchedulerDatabase
import com.app.scheduler.ui.theme.MyApplicationTheme
import com.app.scheduler.viewmodels.SchedulerMainViewModel
import com.app.scheduler.viewmodels.SchedulerMainViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = SchedulerDatabase.getDatabase(this)
        val dao = database.schedulerDao()

        val factory = SchedulerMainViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[SchedulerMainViewModel::class.java]

        enableEdgeToEdge()
        checkAndRequestPermissions()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)
                    AppNavigation(viewModel)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val queryPermission = Manifest.permission.QUERY_ALL_PACKAGES
            if (ContextCompat.checkSelfPermission(
                    this,
                    queryPermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(queryPermission)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isAlarmPermissionGranted()) {
                requestAlarmPermission()
                return
            }
        }
        if (permissionsToRequest.isEmpty()) {
            Log.e(TAG, "No permissions to request")
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PACKAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun isAlarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    companion object {
        const val PACKAGE_PERMISSION_REQUEST_CODE = 100
        const val TAG = "MAIN_ACTIVITY"
    }
}






