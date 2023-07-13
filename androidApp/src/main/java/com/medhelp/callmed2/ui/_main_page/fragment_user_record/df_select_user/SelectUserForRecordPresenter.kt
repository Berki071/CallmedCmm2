package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user

import android.content.Context
import android.widget.Toast
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.UserForRecordItem
import com.medhelp.callmed2.data.model.UserForRecordResponse
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.RecordData
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class SelectUserForRecordPresenter(mainVew: SelectUserForRecordDialog) {
    var context: Context?
    var mainView: SelectUserForRecordDialog?
    var prefManager: PreferencesManager
    var networkManager: NetworkManager

    init {
        mainView = mainVew
        context = mainVew.requireContext()
        prefManager = PreferencesManager(mainVew.requireContext())
        networkManager = NetworkManager(prefManager)
    }

    fun findUserBySurname(surname: String, recordData: RecordData) {
        Different.showLoadingDialog(context)
        val cd = CompositeDisposable()
        cd.add(networkManager
            .findUserBySurname(surname, recordData.scheduleItem.filialItem.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: UserForRecordResponse ->
                searchBySurname(response.response, surname)
                Collections.sort(response.response)
                mainView!!.initRecy(response.response)
                Different.hideLoadingDialog()
            }) { throwable: Throwable? ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "SelectUserForRecordPresenter\$findUserBySurname"
                    )
                )
                if (mainView == null) {
                    return@subscribe
                }
                Different.hideLoadingDialog()
                Toast.makeText(
                    context,
                    context!!.getString(R.string.api_default_error),
                    Toast.LENGTH_LONG
                ).show()
                cd.dispose()
            }
        )
    }

    fun findUserByPhone(phone: String, recordData: RecordData) {
        Different.showLoadingDialog(context)
        val cd = CompositeDisposable()
        cd.add(networkManager
            .findUserByPhone(phone, recordData.scheduleItem.filialItem.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: UserForRecordResponse ->
                searchByPhone(response.response, phone)
                Collections.sort(response.response)
                mainView!!.initRecy(response.response)
                Different.hideLoadingDialog()
            }) { throwable: Throwable? ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "SelectUserForRecordPresenter\$findUserByPhone"
                    )
                )
                if (mainView == null) {
                    return@subscribe
                }
                Different.hideLoadingDialog()
                Toast.makeText(
                    context,
                    context!!.getString(R.string.api_default_error),
                    Toast.LENGTH_LONG
                ).show()
                cd.dispose()
            }
        )
    }

    private fun searchBySurname(list: MutableList<UserForRecordItem>, surname: String) {
        if (list.size == 1 && list[0].name == null) return
        var i = 0
        while (i < list.size) {
            if (!list[i].surname.uppercase(Locale.getDefault()).contains(surname)) {
                list.removeAt(i)
                i--
            }
            i++
        }
    }

    private fun searchByPhone(list: MutableList<UserForRecordItem>, phone: String) {
        if (list.size == 1 && list[0].name == null) return
        var i = 0
        while (i < list.size) {
            if (!list[i].phone.contains(phone)) {
                list.removeAt(i)
                i--
            }
            i++
        }
    }
}