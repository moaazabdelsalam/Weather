package com.project.weather.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun <T> AppCompatActivity.collectLatestFlowOnLifecycle(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun getDateTime(s: Long): String? {
    val sdf = SimpleDateFormat("dd EEE MMM hh:mm,aa", Locale.ENGLISH)
    val netDate = Date(s * 1000)
    return sdf.format(netDate)
}