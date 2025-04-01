package com.app.scheduler.domainlayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.scheduler.R
import com.app.scheduler.viewmodels.SchedulerMainViewModel

@Composable
fun AppSelector(
    onAppSelected: (String) -> Unit,
    onLoadApps: () -> Unit,
    viewModel: SchedulerMainViewModel
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    var expanded by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<String?>(null) }
    val installedApps = viewModel.installedApps.collectAsState()
    val isLoading = viewModel.isLoadingApps.collectAsState()


    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Button(onClick = {
            expanded = true
            onLoadApps.invoke()
        }) {
            Text(selectedApp ?: stringResource(R.string.scheduler_select_app))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            if (isLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                installedApps.value.forEach { app ->
                    val appName = app.loadLabel(packageManager).toString()
                    DropdownMenuItem(
                        text = { Text(appName) },
                        onClick = {
                            selectedApp = appName
                            expanded = false
                            onAppSelected(app.packageName)
                        }
                    )
                }
            }
        }
    }
}