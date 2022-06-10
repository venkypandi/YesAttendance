package com.creatah.yesattendance

import dagger.hilt.android.HiltAndroidApp
import android.app.Application

@HiltAndroidApp
class YesAttendanceApplication: Application() {

    companion object {
        var instance: YesAttendanceApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}