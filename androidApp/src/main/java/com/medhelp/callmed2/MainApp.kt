package com.medhelp.callmed2

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import timber.log.Timber

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
        Timber.plant(LoggingTree(applicationContext))
    }
}