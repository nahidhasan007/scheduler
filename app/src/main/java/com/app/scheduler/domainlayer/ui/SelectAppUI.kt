package com.app.scheduler.domainlayer.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppSelector(onAppSelected: (String) -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    var expanded by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var installedApps by remember { mutableStateOf<List<ApplicationInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        if (expanded && installedApps.isEmpty()) {
            withContext(Dispatchers.IO) {
                val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
                    .sortedBy { it.loadLabel(packageManager).toString().lowercase() }
                installedApps = apps
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.padding(top = 4.dp)) {
        Button(onClick = {
            expanded = true
        }) {
            Text(selectedApp ?: stringResource(R.string.scheduler_select_app))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            if (isLoading) {
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
                installedApps.forEach { app ->
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