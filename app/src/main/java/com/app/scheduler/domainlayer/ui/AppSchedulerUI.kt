package com.app.scheduler.domainlayer.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scheduler.R
import com.app.scheduler.viewmodels.SchedulerMainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSchedulerUI(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchedulerMainViewModel
) {
    val schedules by viewModel.schedules.collectAsState(initial = emptyList())
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var scheduleTime by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.scheduler_app_scheduler),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            Text(
                stringResource(R.string.scheduler_app_Text),
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            )

            AppSelector(onAppSelected = { selectedApp = it },
                onLoadApps = {
                    viewModel.loadInstalledApps(context)
                },
                viewModel = viewModel
            )

            TimePickerDialog(onTimeSelected = { scheduleTime = it })

            Button(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                onClick = {
                    if (selectedApp != null && scheduleTime != null) {
                        viewModel.scheduleApp(context, selectedApp!!, scheduleTime!!)
                    }
                },
                enabled = selectedApp != null && scheduleTime != null
            ) {
                Text(stringResource(R.string.scheduler_schedule_app))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(schedules) { schedule ->
                    ScheduleItem(
                        schedule,
                        onCancel = { viewModel.cancelSchedule(context, schedule.id) },
                        onReschedule = { newTime ->
                            viewModel.rescheduleApp(
                                context,
                                schedule.id,
                                newTime
                            )
                        }
                    )
                }
            }
        }
    }
}
