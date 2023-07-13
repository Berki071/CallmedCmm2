package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_add_user

import android.text.TextUtils
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.SimpleString
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_add_user.AddUserDialog.NewUserData
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AddUserPresenter(mainView: AddUserDialog) {
    var mainView: AddUserDialog?
    var networkManager: NetworkManager

    init {
        this.mainView = mainView
        val prefManager = PreferencesManager(mainView.requireContext())
        networkManager = NetworkManager(prefManager)
    }

    fun sendNewUser(newUserData: NewUserData) {
        Different.showLoadingDialog(mainView!!.context)
        val cd = CompositeDisposable()
        cd.add(networkManager
            .sendNewUser(newUserData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: SimpleString ->
                Different.hideLoadingDialog()
                if (TextUtils.isDigitsOnly(response.response)) mainView!!.createSuccessful(
                    response.response,
                    newUserData!!
                ) else mainView!!.showMsg(response.response)
                cd.dispose()
            }) { throwable: Throwable? ->
                Different.hideLoadingDialog()
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "AddUserPresenter\$sendNewUser"
                    )
                )
                if (mainView == null) {
                    return@subscribe
                }
                mainView!!.showMsg(mainView!!.getString(R.string.api_default_error))
                cd.dispose()
            })
    }
}