package com.app.scheduler.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import com.google.common.primitives.Booleans

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Long.isScheduleExecuted() : Boolean {
    return  this<System.currentTimeMillis()
}