package com.app.scheduler.domainlayer.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.scheduler.R
import com.app.scheduler.ui.theme.BaseBlack
import com.app.scheduler.ui.theme.BaseDark300
import com.app.scheduler.ui.theme.BaseTransparent
import java.util.Calendar

@Composable
fun TimePickerDialog(onTimeSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showDialog by remember { mutableStateOf(false) }

    Button(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        onClick = { showDialog = true },
        colors = ButtonColors(
            containerColor = BaseDark300,
            contentColor = BaseBlack,
            disabledContentColor = BaseTransparent,
            disabledContainerColor = BaseTransparent
        )
    ) {
        Text(stringResource(R.string.scheduler_pick_time))
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