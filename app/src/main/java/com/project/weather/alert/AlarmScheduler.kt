package com.project.weather.alert

import com.project.weather.model.AlertItem

interface AlarmScheduler {
    fun schedule(item: AlertItem)
    fun cancel(item: AlertItem)
}