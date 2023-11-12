package com.project.weather.utils

import android.app.Application
import com.yariksoffice.lingver.Lingver

class LingverInit: Application() {
    override fun onCreate() {
        super.onCreate()
        Lingver.init(this)
    }
}