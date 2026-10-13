package com.example.tracklayoff

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.tracklayoff.core.favorites.sync.FavoritesSyncCoordinator
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LayoffTrackerApp : Application(), Configuration.Provider {
    @Inject lateinit var startup: AppStartup
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var favoritesSync: FavoritesSyncCoordinator
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
    override fun onCreate() {
        super.onCreate()
        startup.start()
        favoritesSync.start()
    }
}
