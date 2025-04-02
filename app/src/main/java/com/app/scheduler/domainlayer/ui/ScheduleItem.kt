package com.app.scheduler.domainlayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.scheduler.R
import com.app.scheduler.datalayer.AppSchedule
import com.app.scheduler.ui.theme.BaseGreen500
import com.app.scheduler.ui.theme.BaseOrange500
import com.app.scheduler.ui.theme.BasePrimary100
import com.app.scheduler.ui.theme.BasePrimary500
import com.app.scheduler.ui.theme.BaseRed500
import com.app.scheduler.ui.theme.BaseTransparent
import com.app.scheduler.ui.theme.BaseWhite
import com.app.scheduler.utils.isScheduleExecuted
import java.util.Calendar
import java.util.Date

@Composable
fun ScheduleItem(schedule: AppSchedule, onCancel: () -> Unit, onReschedule: (Long) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("App: ${schedule.packageName}", fontWeight = FontWeight.Bold)
                    Text("Scheduled Time: ${Date(schedule.scheduleTime)}")
                }
                if (!schedule.scheduleTime.isScheduleExecuted()) {
                    IconButton(onClick = onCancel) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.scheduler_cancel_schedule),
                            tint = BaseRed500
                        )
                    }

                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!schedule.scheduleTime.isScheduleExecuted()) {
                    Button(
                        modifier = Modifier.height(40.dp),
                        onClick = { showTimePicker = true },
                        colors = ButtonColors(
                            containerColor = BasePrimary500,
                            contentColor = BaseWhite,
                            disabledContentColor = BasePrimary100,
                            disabledContainerColor = BasePrimary100
                        )
                    ) {
                        Text(stringResource(R.string.reschedule_app))
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = { showTimePicker = true },
                    colors = ButtonColors(
                        containerColor = if (schedule.scheduleTime.isScheduleExecuted()) {
                            BaseGreen500
                        } else {
                            BaseOrange500
                        },
                        contentColor = BaseWhite,
                        disabledContentColor = BaseTransparent,
                        disabledContainerColor = BaseTransparent
                    )
                ) {
                    Text(
                        text = if (schedule.scheduleTime.isScheduleExecuted()) stringResource(R.string.schedule_completed) else stringResource(
                            R.string.schedule_pending
                        )
                    )
                }
            }


            if (showTimePicker) {
                val timePickerDialog = android.app.TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        onReschedule(calendar.timeInMillis)
                        showTimePicker = false
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            }
        }
    }
}

@Preview
@Composable
fun PreviewScheduleItem() {
    val specificSchedule = AppSchedule(
        packageName = "com.example.anotherapp",
        scheduleTime = 1735689600000,
        executed = false
    )
    ScheduleItem(
        schedule = specificSchedule,
        onCancel = {},
        onReschedule = {}
    )
}
