package com.project.weather.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.project.weather.constants.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun <T> AppCompatActivity.collectLatestFlowOnLifecycle(
    flow: Flow<T>,
    collect: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun getDateAndTime(s: Long): Map<String, String> {
    val sdf = SimpleDateFormat("EEE MMM dd yyyy hh:mm aa", Locale.ENGLISH)
    val netDate = Date(s * 1000)
    val result = (sdf.format(netDate)).split(" ")
    return mapOf(
        Constants.DAY_OF_WEEK_KEY to result[0],
        Constants.MONTH_KEY to result[1],
        Constants.DAY_OF_MONTH_KEY to result[2],
        Constants.YEAR_KEY to result[3],
        Constants.TIME_KEY to result[4],
        Constants.AM_PM_KEY to result[5]
    )
}