package com.medhelp.callmed2.ui.login

import android.os.Build
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.error.ANError
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.*
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import com.medhelp.callmed2.data.network.ProtectionData
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.IpUtils
import com.medhelp.callmed2.utils.main.NetworkUtils
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LoginPresenter (private val mainView: LoginActivity) {
    private val preferencesManager: PreferencesManager = PreferencesManager(mainView.context)
    private val networkManager: NetworkManager = NetworkManager(preferencesManager)
    private val networkManager2 = NetworkManagerCompatibleKMM()


    fun testConnections() {
        if (!NetworkUtils.isNetworkConnected(mainView!!)) {
            mainView!!.showAlert(R.string.connection_error, 0, null, null)
            return
        }

        //getMvpView().setEnable(true);
    }

    val showPartMessenger: Boolean
        get() = preferencesManager.isShowPartMessenger

    fun onLoginClick(username: String?, password: String?) {
        testConnections()
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            mainView!!.showError(R.string.empty_data)
            return
        }
        if (NetworkUtils.isNetworkConnected(mainView!!)) {
            verifyUser(username, password)
        } else {
            mainView!!.showError(R.string.connection_error)
        }
    }

    val username: Unit
        get() {
            try {
                mainView!!.updateUsernameAndPassHint(
                    preferencesManager.currentUserName,
                    preferencesManager.currentPassword
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun verifyUser(username: String, password: String) {
        mainView!!.showLoading()
        val cd = CompositeDisposable()
        cd.add(networkManager
            .doLoginApiCall(username, password, mainView.context)
            .subscribeOn(Schedulers.io())
            .map { response: UserList ->
                val userResponse: UserResponse
                val ar: List<*> = response.response
                userResponse = ar[0] as UserResponse
                userResponse
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: UserResponse ->
                if (response.username != null) {
                    saveUserPref(response)
                    Timber.v("Вход в приложение VERSION_SDK" + Build.VERSION.SDK_INT)
                    savePrivateData(username, password)

//                        if (mainView.isNeedSave()) {
//                            savePrivateData(username, password);
//                        } else {
//                            cleanPassword();
//                        }
                    centerInfo
                } else {
                    mainView.showError(R.string.err_authorize)
                }
                mainView.hideLoading()
                cd.dispose()
            }) { throwable: Throwable ->
                var str: String? = null
                if (throwable is ANError) {
                    str = throwable.errorBody
                    if (str != null && str.contains("vasdvasdasdvя")) {
                        val pd = ProtectionData()
                        Timber.e(
                            LoggingTree.getMessageForError(
                                Throwable(
                                    "Несовпадение hashkey " + pd.getSignature(
                                        mainView.context
                                    )
                                ), "LoginPresenter\$verifyUser1"
                            )
                        )
                    } else if (str != null) {
                        Timber.e(
                            LoggingTree.getMessageForError(
                                throwable,
                                "LoginPresenter\$verifyUser "
                            )
                        )
                    }
                }
                val str11 = throwable.message
                if (str == null && str11 != null) {
                    Timber.e(
                        LoggingTree.getMessageForError(
                            null,
                            "LoginPresenter\$verifyUser2 $str11; Response: null"
                        )
                    )
                }
                if (str11 != null && str != null && str.contains("Failed to connect to")) {
                    mainView.showError("Failed to connect")
                } else {
                    mainView.showError(R.string.api_default_error)
                }
                if (mainView == null) {
                    return@subscribe
                }
                mainView.hideLoading()
            })
    }

    private fun cleanPassword() {
        try {
            preferencesManager.deleteCurrentPassword()
        } catch (e: Exception) {
            Timber.e(
                LoggingTree.getMessageForError(
                    e,
                    "LoginPresenter/cleanPassword Ошибка удаления пароля: %s"
                )
            )
        }
    }

    private fun savePrivateData(username: String, password: String) {
        try {
            preferencesManager.currentPassword = password
            preferencesManager.currentUserName = username
        } catch (e: Exception) {
            Timber.e(
                LoggingTree.getMessageForError(
                    e,
                    "LoginPresenter/savePrivateData  Ошибка сохранения пароля в SharedPreference: %s"
                )
            )
        }
    }

    private fun saveUserPref(response: UserResponse) {
        try {
            preferencesManager.isShowPartCallCenter = response.isShowPartCallCenter
            preferencesManager.isShowPartMessenger = response.isShowPartMessenger
            preferencesManager.isShowPartTimetable = response.isTimetable
            preferencesManager.isShowPartScanDoc = response.isSync
            preferencesManager.isShowPartRaspDoc = response.isVrach
            preferencesManager.currentUserId = response.idUser
            preferencesManager.currentUserName = response.username
            preferencesManager.currentCenterId = response.idCenter
            preferencesManager.accessToken = response.apiKey
        } catch (e: Exception) {
            Timber.e(
                LoggingTree.getMessageForError(
                    e,
                    "LoginPresenter/saveUserPref Не удалось сохранить данные на устройстве: %s"
                )
            )
        }
    }
    // getTestServer(getDataHelper().getCenterInfo().getIpAddressLan(), true);

    //mainView.startBGService();
    private val centerInfo: Unit
        private get() {
            mainView!!.showLoading()
            val cd = CompositeDisposable()
            cd.add(networkManager
                .centerApiCall
                .map { obj: CenterList -> obj.response }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe({ centerResponses: List<CenterResponse> ->
                    preferencesManager.centerInfo = centerResponses[0]

                    fBToken

                   // mainView.hideLoading()
                    //mainView.openProfileActivity()
                    cd.dispose()
                }
                ) { throwable: Throwable? ->
                    Timber.e(
                        LoggingTree.getMessageForError(
                            throwable,
                            "LoginPresenter\$getCenterInfo "
                        )
                    )
                    mainView.hideLoading()
                    mainView.showError("Ошибка загрузки информации о центре")
                    cd.dispose()
                })
        }
    private val fBToken: Unit
        private get() {
            if (preferencesManager.isShowNotifications) {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Timber.e("LoginPresenter/getFBToken failed $task")
                            mainView!!.hideLoading()
                            mainView.openProfileActivity()
                            return@OnCompleteListener
                        }
                        val token = task.result
                        sendFcmToken(token)
                    })
            } else {
                if (mainView != null) {
                    mainView.hideLoading()
                    mainView.openProfileActivity()
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
                    mainView!!.hideLoading()
                    mainView.openProfileActivity()
                }.onFailure {
                    Timber.e(LoggingTree.getMessageForError(it, "LoginPresenter/sendFcmToken "))
                    mainView!!.hideLoading()
                    mainView.openProfileActivity()
                }
        }

//        val cd = CompositeDisposable()
//        cd.add(networkManager
//            .updateFcmToken(token)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ response: SimpleString? ->
//                mainView!!.hideLoading()
//                mainView.openProfileActivity()
//                cd.dispose()
//            }) { throwable: Throwable? ->
//                Timber.e(
//                    LoggingTree.getMessageForError(
//                        throwable,
//                        "LoginPresenter/sendFcmToken "
//                    )
//                )
//                mainView!!.hideLoading()
//                mainView.openProfileActivity()
//                cd.dispose()
//            })
    }

    private fun sendLogWith2IpAndMac(currentTestIp: String, msgErr: String) {
        val s1 = IpUtils.getMACAddress("wlan0")
        val s2 = IpUtils.getMACAddress("eth0")
        val cd = CompositeDisposable()
        cd.add(networkManager
            .externalIp
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ ipDataResponse: IPDataResponse ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        null,
                        "LoginPresenter/TestServer Проверка связи c IP:" + currentTestIp + " неуспешна;" +
                                " msgErr: " + msgErr + "; local IPv4:" + IpUtils.getIPAddress(
                            true
                        ) +
                                "; external ip: " + ipDataResponse.ip + "; mac wlan:" + s1 + "; mac eth:" + s2
                    )
                )
            }
            ) { throwable: Throwable ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "LoginPresenter/TestServer Проверка связи c IP:" + currentTestIp + " неуспешна," +
                                " msgErr: " + msgErr + "; local IPv4:" + IpUtils.getIPAddress(
                            true
                        ) + "; external ip err: " +
                                throwable.message + "; mac wlan:" + s1 + "; mac eth:" + s2
                    )
                )
            }
        )
    }
}