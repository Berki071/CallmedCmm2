package com.medhelp.callmed2.ui.splash

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.error.ANError
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.medhelp.callmed2.data.model.*
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.main.NetworkUtils
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashPresenter(private val mainView: SplashActivity) {
    private val preferencesManager: PreferencesManager
    private val networkManager: NetworkManager
    private val networkManager2 = NetworkManagerCompatibleKMM()

    init {
        preferencesManager = PreferencesManager(mainView)
        networkManager = NetworkManager(preferencesManager)
    }

    fun openNextActivity() {
        var username: String? = null
        var password: String? = null
        try {
            username = preferencesManager.currentUserName?.trim { it <= ' ' }
            password = preferencesManager.currentPassword?.trim { it <= ' ' }
        } catch (e: Exception) {
            // all right
        }
        if (username != null && password != null && password != "" && NetworkUtils.isNetworkConnected(mainView!!)) {
            verifyUser(username, password)
        } else {
            mainView!!.openLoginActivity()
        }
    }

    private fun verifyUser(username: String, password: String) {
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager2.doLoginApiCall(username, password)
            }
                .onSuccess {
                    if (it.response[0].username != null) {
                        Timber.v("Вход в приложение")
                        preferencesManager.isShowPartCallCenter = it.response[0].showPartCallCenter.toBoolean()
                        preferencesManager.isShowPartMessenger = it.response[0].showPartMessenger.toBoolean()
                        preferencesManager.isShowPartTimetable = it.response[0].timetable.toBoolean()
                        preferencesManager.isShowPartScanDoc = it.response[0].sync.toBoolean()
                        preferencesManager.isShowPassportRecognize = it.response[0].buttonRecognize.toBoolean()
                        preferencesManager.isShowPartRaspDoc = it.response[0].vrach.toBoolean()
                        preferencesManager.accessToken = it.response[0].apiKey
                        updateHeaderInfo()
                    } else {
                        preferencesManager.currentPassword = ""
                        preferencesManager.currentUserId = 0
                        preferencesManager.currentCenterId = 0
                        mainView.openLoginActivity()
                        Timber.e("SplashPresenter/verifyUser response.getUsername()!=null")
                    }
                }.onFailure {
                    Timber.e(LoggingTree.getMessageForError(it, "SplashPresenter/verifyUser "))

                    if (mainView != null)
                        mainView.openLoginActivity()
                }
        }

    }

    private fun updateHeaderInfo() {
        val cd = CompositeDisposable()
        cd.add(networkManager
            .centerApiCall
            .map { obj: CenterList -> obj.response }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: List<CenterResponse> ->
                preferencesManager.centerInfo = response[0]
                // getTestServer(getDataHelper().getCenterInfo().getIpAddressLan(),true);
                if (preferencesManager.isShowPartMessenger) {
                    mainView!!.startBGService()
                }
                fBToken
                cd.dispose()
            }) { throwable: Throwable? ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "SplashPresenter/updateHeaderInfo "
                    )
                )
                mainView!!.openLoginActivity()
                cd.dispose()
            })
    }

    private val fBToken: Unit
        private get() {
            if (preferencesManager.isShowNotifications) {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.e("SplashPresenter/getFBToken failed $task")
                            if (mainView != null) {
                                mainView.hideLoading()
                                mainView.openProfileActivity(preferencesManager.screenLock)
                            }
                            return@OnCompleteListener
                        }
                        val token = task.result
                        sendFcmToken(token)
                    })
            } else {
                if (mainView != null) {
                    mainView.hideLoading()
                    mainView.openProfileActivity(preferencesManager.screenLock)
                }
            }
        }

    private fun sendFcmToken(token: String) {
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager2.updateFcmToken(token,
                    preferencesManager.accessToken!!, preferencesManager.centerInfo!!.db_name!!, preferencesManager.currentUserId.toString())
            }
                .onSuccess {
                    mainView.hideLoading()
                    mainView.openProfileActivity(preferencesManager.screenLock)
                }.onFailure {
                    Timber.e(LoggingTree.getMessageForError(it, "SplashPresenter/sendFcmToken "))
                    mainView!!.hideLoading()
                    mainView.openProfileActivity(preferencesManager.screenLock)

                }
        }

//        val cd = CompositeDisposable()
//        cd.add(networkManager
//            .updateFcmToken(token)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ response: SimpleString? ->
//                if (mainView != null) {
//                    mainView.hideLoading()
//                    mainView.openProfileActivity(preferencesManager.screenLock)
//                }
//                cd.dispose()
//            }) { throwable: Throwable? ->
//                Timber.e(
//                    LoggingTree.getMessageForError(
//                        throwable,
//                        "SplashPresenter/sendFcmToken "
//                    )
//                )
//                mainView!!.hideLoading()
//                mainView.openProfileActivity(preferencesManager.screenLock)
//                cd.dispose()
//            })
    }

    val isFirstStart: Boolean
        get() = preferencesManager.isStartMode
    val showPartMessenger: Boolean
        get() = preferencesManager.isShowPartMessenger
}