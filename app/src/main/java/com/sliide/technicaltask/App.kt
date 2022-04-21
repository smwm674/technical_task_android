package com.sliide.technicaltask

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}