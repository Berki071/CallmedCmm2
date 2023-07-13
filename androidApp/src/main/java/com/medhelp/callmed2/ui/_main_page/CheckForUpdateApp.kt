package com.medhelp.callmed2.ui._main_page

import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.medhelp.callmed2.data.Constants

class CheckForUpdateApp(var context: Context) {
    init {
        testUpdateApp()
    }

    private fun testUpdateApp() {
        // Create instance of the IAUs manager.
        val appUpdateManager = AppUpdateManagerFactory.create(context)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                        (context as Activity),  // Include a request code to later monitor this update request.
                        Constants.REQUEST_CODE_FOR_UPDATE_APP
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }
}