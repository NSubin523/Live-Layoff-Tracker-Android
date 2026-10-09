package com.example.tracklayoff

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LayoffTrackerApp : Application() {
    @Inject lateinit var startup: AppStartup
    override fun onCreate() {
        super.onCreate()
        startup.start()
    }
}
