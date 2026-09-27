package com.example.tracklayoff.core.common.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleTracker @Inject constructor() : DefaultLifecycleObserver {
    private val _isAppInForeground = MutableStateFlow(false)
    val isAppInForeground : StateFlow<Boolean> = _isAppInForeground.asStateFlow()

    var lastBackgroundTimeStamp : Long = System.currentTimeMillis()
        private set

    fun init () {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        _isAppInForeground.value = true
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        _isAppInForeground.value = true
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        _isAppInForeground.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        lastBackgroundTimeStamp = System.currentTimeMillis()
        _isAppInForeground.value = false
    }
}