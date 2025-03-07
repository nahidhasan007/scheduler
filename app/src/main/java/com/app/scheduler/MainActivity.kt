package com.app.scheduler

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.scheduler.datalayer.AppSchedule
import com.app.scheduler.network.local.SchedulerDatabase
import com.app.scheduler.ui.theme.MyApplicationTheme
import java.util.Calendar
import java.util.Date

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
    }
}


@Composable
fun ScheduleItem(schedule: AppSchedule, onCancel: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("App: ${schedule.packageName}", fontWeight = FontWeight.Bold)
                Text("Scheduled Time: ${Date(schedule.scheduleTime)}")
            }
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Delete, contentDescription = "Cancel Schedule")
            }
        }
    }
}

@Composable
fun TimePickerDialog(onTimeSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showDialog by remember { mutableStateOf(false) }

    Button(onClick = { showDialog = true }) {
        Text("Pick a Time")
    }

    if (showDialog) {
        val timePickerDialog = android.app.TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected(calendar.timeInMillis)
                showDialog = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }
}

@Composable
fun AppSelector(onAppSelected: (String) -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    var expanded by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<String?>(null) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedApp ?: "Select an App")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            installedApps.forEach { app ->
                val appName = app.loadLabel(packageManager).toString()
                DropdownMenuItem(text = { Text(appName) }, onClick = {
                    selectedApp = appName
                    expanded = false
                    onAppSelected(app.packageName)
                })
            }
        }
    }
}

@Composable
fun AppSchedulerUI(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchedulerMainViewModel
) {
    val schedules by viewModel.schedules.collectAsState(initial = emptyList())
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var scheduleTime by remember { mutableStateOf<Long?>(null) }

    Column(modifier = modifier) {
        Text("App Scheduler", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Select an App
        AppSelector(onAppSelected = { selectedApp = it })

        // Pick a Time
        TimePickerDialog(onTimeSelected = { scheduleTime = it })

        // Schedule Button
        Button(
            onClick = {
                if (selectedApp != null && scheduleTime != null) {
                    viewModel.scheduleApp(context, selectedApp!!, scheduleTime!!)
                }
            },
            enabled = selectedApp != null && scheduleTime != null
        ) {
            Text("Schedule App")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show Scheduled Apps
        LazyColumn {
            items(schedules) { schedule ->
                ScheduleItem(
                    schedule,
                    onCancel = { viewModel.cancelSchedule(context, schedule.id) })
            }
        }
    }
}




