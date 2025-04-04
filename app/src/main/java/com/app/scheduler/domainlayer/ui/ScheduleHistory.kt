package com.app.scheduler.domainlayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.scheduler.R
import com.app.scheduler.network.local.SchedulerDatabase
import com.app.scheduler.ui.theme.BaseDark300
import com.app.scheduler.viewmodels.SchedulerMainViewModel
import com.app.scheduler.viewmodels.SchedulerMainViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleHistoryList(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current

    val database = SchedulerDatabase.getDatabase(context)
    val dao = database.schedulerDao()

    val viewModel: SchedulerMainViewModel =
        viewModel(factory = SchedulerMainViewModelFactory(dao))

    val schedules by viewModel.scheduleList.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(color = BaseDark300),
                title = {
                    Text(
                        stringResource(R.string.scheduler_history),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            LazyColumn {
                items(schedules) { schedule ->
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
