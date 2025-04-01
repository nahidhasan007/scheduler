package com.app.scheduler.domainlayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.scheduler.viewmodels.SchedulerMainViewModel
import com.app.scheduler.viewmodels.SchedulerMainViewModelFactory
import com.app.scheduler.domainlayer.ui.AppSchedulerUI
import com.app.scheduler.network.local.SchedulerDatabase
import com.app.scheduler.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = SchedulerDatabase.getDatabase(this)
        val dao = database.schedulerDao()

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)
                    val viewModel: SchedulerMainViewModel =
                        viewModel(factory = SchedulerMainViewModelFactory(dao))
                    AppSchedulerUI(modifier = modifier, this, viewModel)
                }
            }
        }
        checkAndRequestPermissions()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        val packagePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.QUERY_ALL_PACKAGES
        ) == PackageManager.PERMISSION_GRANTED
        val permissionsToRequest = mutableListOf<String>()
        if (!packagePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.QUERY_ALL_PACKAGES)
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PACKAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        const val PACKAGE_PERMISSION_REQUEST_CODE = 100
    }
}






