package com.medhelp.callmed2.utils.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isOnline: NetworkInfo? = null
        if (cm != null) {
            isOnline = cm.activeNetworkInfo
        }
        return isOnline != null && isOnline.isConnectedOrConnecting
    }
}