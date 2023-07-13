package com.medhelp.callmed2.utils.timber_log

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import com.androidnetworking.error.ANError
import com.medhelp.callmed2.data.model.LogData
import com.medhelp.callmed2.data.model.SimpleResponseBoolean
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.main.NetworkUtils
import com.medhelp.callmed2.utils.main.TimesUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoggingTree(val context: Context) : Timber.DebugTree() {
    private val pm: PreferencesManager
    private val networkManager: NetworkManager
    private var versionCode = 0

    init {
        pm = PreferencesManager(context)
        networkManager = NetworkManager(pm)
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val verCode = pInfo.versionCode
            versionCode = verCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    protected override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            2 -> addToListLog(VERBOSE_INFORMATION, message)
            3 -> addToListLog(TELEMEDICINE, message)
            4 -> addToListLog(CREATE_ACTIVITY, message)
            5 -> addToListLog(ERROR_TELEMEDICINE, message)
            6 -> { addToListLog(ERROR, message)
            }
            else -> addToListLog(DEFAULT_ERROR_TYPE, message)
        }
    }

    private fun addToListLog(type: String, message: String) {
        var message = message
        message = message.trim { it <= ' ' }
        message = MDate.longToString(MDate.getCurrentDate(), MDate.DATE_FORMAT_ddMMyyyy_HHmmss) + " " + message

        val latch: Boolean = checkErrorOnSave(message)
        if (!latch) return

        val logData = LogData()

        logData.idUser = if (pm.currentUserId == 0) "-1" else pm.currentUserId.toString()
        logData.idCenter = if (pm.currentCenterId == 0) "-1" else pm.currentCenterId.toString()
        logData.idBranch = "-1"
        logData.type = type
        logData.version = versionCode.toString()
        logData.log = message
        logData.time = MDate.getCurrentDate().toString()

        pm.addLogItem(logData)

        checkToSentListLog()
    }

    private fun checkToSentListLog() {
        val item = pm.getOneLogItem()
        if (item != null && lisOpenSendLogToServer) {
            sendLogToServer(item)
        }
    }

    private fun checkErrorOnSave(message: String): Boolean {
        return !(message.contains("Job was cancelled") || message.contains("StandaloneCoroutine was cancelled"))
    }

    var lisOpenSendLogToServer = true
    private fun sendLogToServer(item: LogData) {
        lisOpenSendLogToServer = false

        if (!NetworkUtils.isNetworkConnected(context)) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                return
            }
            val handler = Handler()
            handler.postDelayed({
                sendLogToServer(item)
                //return;
            }, 5000)
            return
        }

        val cd = CompositeDisposable()
        cd.add(networkManager
            .sendLogToServer(item)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: SimpleResponseBoolean? ->
                lisOpenSendLogToServer = true
                pm.removeLogItem(item)
                checkToSentListLog()
                cd.dispose()
            }
            ) { throwable: Throwable? ->
                lisOpenSendLogToServer = true
                checkToSentListLog()
                cd.dispose()
            }
        )
    }


    companion object {
        //Timber.e - for error (priority 6)
        //Timber.i - for onCreate Activity (priority 4)
        //Timber.v - verbose information about actions (priority 2)
        //Timber.d - ALERT_TYPE (priority 3) -> telemedicine
        //Timber.w - error telemedicine (priority 5)
//        const val VERBOSE_INFORMATION = "VERBOSE_INFORMATION"
//        const val CREATE_ACTIVITY = "CREATE_ACTIVITY"
//        const val ERROR = "ERROR"
//        const val DEFAULT_ERROR_TYPE = "DEFAULT_ERROR_TYPE"

        const val VERBOSE_INFORMATION = "VERBOSE_INFORMATION"
        const val CREATE_ACTIVITY = "CREATE_ACTIVITY"
        const val ERROR = "ERROR"
        const val DEFAULT_ERROR_TYPE = "DEFAULT_ERROR_TYPE"
        const val TELEMEDICINE = "TELEMEDICINE"   //Timber.tag("my").d
        const val ERROR_TELEMEDICINE = "ERROR_TELEMEDICINE"




        @JvmStatic
        fun getMessageForError(t: Throwable?, message: String): String {
            var err: String? = ""
            if (t != null) {
                if (t is ANError) {
                    val anError = t
                    err += "ANError ErrorDetail: "
                    err += anError.errorDetail
                    err += "\n"
                    err += "ANError ErrorBody: "
                    err += anError.errorBody
                    err += "\n"
                }
                err += "Throwable message: "
                err += t.message
            }
            return """
                   $message
                   $err
                   """.trimIndent()
        }
    }
}