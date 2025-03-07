package com.app.scheduler

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.scheduler.network.SchedulerDatabase
import com.app.scheduler.ui.theme.BaseDark050
import com.app.scheduler.ui.theme.BaseTextColorPrimary
import com.app.scheduler.ui.theme.BaseTextColorSecondary
import com.app.scheduler.ui.theme.BaseWhite
import com.app.scheduler.ui.theme.MyApplicationTheme
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
                    AppSchedulerScreen(modifier, viewModel)
                }
            }
        }
    }
}

@Composable
fun AppSchedulerScreen(
    modifier: Modifier = Modifier,
    viewModel: SchedulerMainViewModel
) {
    val context = LocalContext.current
    val schedules by viewModel.schedules.collectAsState()
    val packageManager = context.packageManager
    val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    var selectedApp by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf(System.currentTimeMillis()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseDark050)
            .padding(16.dp)
    ) {
        Text(
            text = "Schedule an App",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Adds vertical spacing
        ) {
            itemsIndexed(installedApps) { index, app ->
                val appName = app.loadLabel(packageManager).toString()
                AppItem(appName, selectedTime)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedApp.isNotEmpty()) {
                    viewModel.scheduleApp(context, selectedApp, selectedTime)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule App")
        }
    }
}

@Composable
fun AppItem(appName: String, selectedTime: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BaseWhite, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "App: $appName",
                fontWeight = FontWeight.Medium,
                color = BaseTextColorPrimary
            )
            Text(text = "Time: ${Date(selectedTime)}", color = BaseTextColorSecondary)
        }
    }
}
