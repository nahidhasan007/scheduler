package com.app.scheduler

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scheduler.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)

                    AppSchedulerScreenV2()
                }
            }
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

@Composable
fun AppSchedulerScreenV2() {
    val context = LocalContext.current
//    val schedules by viewModel.schedules.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Schedule an App", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val packages: List<PackageInfo> = packageManager.getInstalledPackages(0)

        var selectedApp by remember { mutableStateOf("") }
        var selectedTime by remember { mutableStateOf(System.currentTimeMillis()) }

        DropdownMenu(
            expanded = true,
            onDismissRequest = { /* Dismiss Dropdown */ }
        ) {
            apps.forEach { app ->
                val appName = app.loadLabel(packageManager).toString()
                if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0 ||
                    app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0)  {
                    DropdownMenuItem(
                        text = { Text(appName) },
                        onClick = { selectedApp = app.packageName }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (selectedApp.isNotEmpty()) {
//                    viewModel.scheduleApp(context, selectedApp, selectedTime)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule App")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Scheduled Apps", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//        LazyColumn {
//            items(schedules.size) { schedule ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
////                        Column {
//                            Text("App: ${schedule.packageName}")
//                            Text("Time: ${Date(schedule.scheduleTime)}")
//                        }
//                        Button(onClick = {
//                            viewModel.cancelSchedule(context, schedule.id)
//                        }) {
//                            Text("Cancel")
//                        }
    }
//            }
//        }
//    }
}


//@Composable
//fun AppSchedulerScreen(viewModel: SchedulerMainViewModel) {
//    val context = LocalContext.current
//    val schedules by viewModel.schedules.collectAsState()
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text("Schedule an App", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//        Spacer(modifier = Modifier.height(8.dp))
//
//        val packageManager = context.packageManager
//        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//
//        var selectedApp by remember { mutableStateOf("") }
//        var selectedTime by remember { mutableStateOf(System.currentTimeMillis()) }
//
//        DropdownMenu(
//            expanded = true,
//            onDismissRequest = { /* Dismiss Dropdown */ }
//        ) {
//            installedApps.forEach { app ->
//                DropdownMenuItem(
//                    text = { Text(app.packageName) },
//                    onClick = { selectedApp = app.packageName }
//                )
//            }
//        }
//
//        Button(
//            onClick = {
//                if (selectedApp.isNotEmpty()) {
//                    viewModel.scheduleApp(context, selectedApp, selectedTime)
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Schedule App")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text("Scheduled Apps", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//        LazyColumn {
//            items(schedules.size) { schedule ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
////                        Column {
////                            Text("App: ${schedule.packageName}")
////                            Text("Time: ${Date(schedule.scheduleTime)}")
////                        }
////                        Button(onClick = {
////                            viewModel.cancelSchedule(context, schedule.id)
////                        }) {
////                            Text("Cancel")
////                        }
//                }
//            }
//        }
//    }
//}