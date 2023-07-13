package com.medhelp.callmed2.ui._main_page.fragment_settings

import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.medhelp.callmed2.data.model.IPDataList
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class SettingsPresenter(val mainView: SettingsFragment) {
    var preferencesManager: PreferencesManager = PreferencesManager(mainView.requireContext())
    var networkManager: NetworkManager = NetworkManager(preferencesManager)
    val networkManager2 = NetworkManagerCompatibleKMM()

    val isCheckLoginAndPassword: Boolean
        get() {
            val idUser = preferencesManager.currentUserId
            val pass = preferencesManager.currentPassword
            return if (idUser == 0 || pass == "") false else true
        }

    //у некоторых были невидимые символы в начале строки
    val allIp: Unit
        get() {
            mainView!!.showLoading()
            if (!isCheckLoginAndPassword) return
            val cd = CompositeDisposable()
            cd.add(networkManager
                .allIPCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ dataList: String ->
                    val withoutInvisible = dataList.substring(
                        dataList.indexOf("{"),
                        dataList.length
                    ) //у некоторых были невидимые символы в начале строки
                    val gson = Gson()
                    var tmp: IPDataList? = null
                    try {
                        tmp = gson.fromJson(withoutInvisible, IPDataList::class.java)
                        if (tmp.response.size > 1) Collections.sort(tmp.response)
                    } catch (e: Exception) {
                        Timber.e(
                            LoggingTree.getMessageForError(
                                e,
                                "SettingsPresenter/getAllIp2; json:$dataList"
                            )
                        )
                        mainView!!.hideLoading()
                        return@subscribe
                    }
                    if (mainView == null) {
                        return@subscribe
                    }
                    mainView!!.refreshSpinner(tmp.response)
                    mainView!!.hideLoading()
                    cd.dispose()
                }) { throwable: Throwable ->
                    Timber.e(
                        LoggingTree.getMessageForError(
                            throwable,
                            "SettingsPresenter/getAllIp"
                        )
                    )
                    if (mainView == null) {
                        return@subscribe
                    }
                    mainView!!.showErrorM(throwable.message)
                    mainView!!.hideLoading()
                    cd.dispose()
                })
        }
    val fBToken: Unit
        get() {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Timber.e("SettingsPresenter/getFBToken failed $task")
                        if (mainView != null) {
                            mainView!!.hideLoading()
                            mainView!!.binding.switchNoty!!.isChecked = false
                            mainView!!.showErrorM("Не удалось получить токен для уведомлений")
                        }
                        return@OnCompleteListener
                    }
                    val token = task.result
                    sendFcmToken(token)
                })
        }

    fun sendFcmToken(token: String) {
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager2.updateFcmToken(token,
                    preferencesManager.accessToken!!, preferencesManager.centerInfo!!.db_name!!, preferencesManager.currentUserId.toString())
            }
                .onSuccess {
                    if (mainView != null) {
                        mainView.hideLoading()
                    }
                }.onFailure {
                    Timber.e(LoggingTree.getMessageForError(it, "SettingsPresenter/sendFcmToken "))
                    mainView!!.showErrorM("Не удалось обновить")
                    mainView!!.hideLoading()
                }
        }


//        val cd = CompositeDisposable()
//        cd.add(networkManager
//            .updateFcmToken(token)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ response: SimpleString? ->
//                mainView!!.hideLoading()
//                cd.dispose()
//            }) { throwable: Throwable? ->
//                Timber.e(
//                    LoggingTree.getMessageForError(
//                        throwable,
//                        "SettingsPresenter/sendFcmToken "
//                    )
//                )
//                mainView!!.showErrorM("Не удалось обновить")
//                mainView!!.hideLoading()
//                cd.dispose()
//            })
    }

    var ip: String
        get() = preferencesManager.selectedIp!!
        set(ip) {
            Timber.v("Выбрана новый IP: $ip")
            preferencesManager.selectedIp = ip
        }
    var needShowLockScreen: Boolean
        get() = preferencesManager.screenLock
        set(boo) {
            preferencesManager.screenLock = boo
        }
    val isShowCallCenter: Boolean
        get() = preferencesManager.isShowPartCallCenter
    var lockScreenCallCenter: Boolean
        get() = preferencesManager.lockScreenCallCenter
        set(boo) {
            preferencesManager.lockScreenCallCenter = boo
        }
}