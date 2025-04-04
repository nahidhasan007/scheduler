package com.app.scheduler.domainlayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.scheduler.R
import com.app.scheduler.navigation.Routes
import com.app.scheduler.network.local.SchedulerDatabase
import com.app.scheduler.ui.theme.BaseBlack
import com.app.scheduler.ui.theme.BaseDark300
import com.app.scheduler.ui.theme.BaseDark600
import com.app.scheduler.ui.theme.BaseTransparent
import com.app.scheduler.viewmodels.SchedulerMainViewModel
import com.app.scheduler.viewmodels.SchedulerMainViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSchedulerUI(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current

    val database = SchedulerDatabase.getDatabase(context)
    val dao = database.schedulerDao()

    val viewModel: SchedulerMainViewModel =
        viewModel(factory = SchedulerMainViewModelFactory(dao))

    val schedules by viewModel.scheduleList.collectAsState(initial = emptyList())
    val selectedApp by viewModel.selectedApp.collectAsState()
    var scheduleTime by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(color = BaseDark300),
                title = {
                    Text(
                        stringResource(R.string.scheduler_app_scheduler),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            )

            AppSelector(
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
                colors = ButtonColors(
                    containerColor = BaseDark300,
                    contentColor = BaseBlack,
                    disabledContentColor = BaseTransparent,
                    disabledContainerColor = BaseTransparent
                ),
                enabled = selectedApp != null && scheduleTime != null
            ) {
                Text(stringResource(R.string.scheduler_schedule_app))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(modifier = Modifier
                .padding(16.dp)
                .clickable {
                    navController.navigate(Routes.SCHEDULE_HISTORY.name)
                }, text = "See All Schedules"
            )
            LazyColumn {
                itemsIndexed(schedules) { index, schedule ->
                    if (index < 3) {
                        ScheduleItem(
                            schedule,
                            onCancel = { viewModel.cancelSchedule(context, schedule) },
                            onReschedule = { newTime ->
                                viewModel.rescheduleApp(
                                    context,
                                    schedule,
                                    newTime
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
