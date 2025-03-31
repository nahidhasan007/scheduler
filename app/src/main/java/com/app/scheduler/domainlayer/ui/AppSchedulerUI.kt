package com.app.scheduler.domainlayer.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.scheduler.R
import com.app.scheduler.domainlayer.AppSelector
import com.app.scheduler.domainlayer.TimePickerDialog
import com.app.scheduler.viewmodels.SchedulerMainViewModel

@Composable
fun AppSchedulerUI(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SchedulerMainViewModel
) {
    val schedules by viewModel.schedules.collectAsState(initial = emptyList())
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var scheduleTime by remember { mutableStateOf<Long?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            stringResource(R.string.scheduler_app_scheduler),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        AppSelector(onAppSelected = { selectedApp = it })

        TimePickerDialog(onTimeSelected = { scheduleTime = it })

        Button(
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
