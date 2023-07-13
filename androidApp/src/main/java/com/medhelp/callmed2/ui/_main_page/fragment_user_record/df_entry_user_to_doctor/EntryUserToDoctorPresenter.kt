package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_entry_user_to_doctor

import android.widget.Toast
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.ResponseString
import com.medhelp.callmed2.data.model.SimpleResponseString
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.RecordData
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class EntryUserToDoctorPresenter(mainView: EntryUserToDoctorDialog) {
    var mainView: EntryUserToDoctorDialog?
    var networkManager: NetworkManager
    var prefManager: PreferencesManager

    init {
        this.mainView = mainView
        prefManager = PreferencesManager(mainView.requireContext())
        networkManager = NetworkManager(prefManager)
    }

    fun getUserIdInOtherBranch(recordData: RecordData) {
        Different.showLoadingDialog(mainView!!.context)
        val cd = CompositeDisposable()
        cd.add(networkManager
            .getUserIdInOtherBranch(
                recordData.user.id,
                recordData.user.idFilial,
                recordData.scheduleItem.filialItem.id
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: SimpleResponseString ->
                recordData.user.id = response.response
                recordData.user.idFilial = recordData.scheduleItem.filialItem.id
                sendEntryToServer(recordData)
                cd.dispose()
            }) { throwable: Throwable? ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "EntryUserToDoctorPresenter\$getUserIdInOtherBranch"
                    )
                )
                if (mainView == null) {
                    return@subscribe
                }
                Different.hideLoadingDialog()
                Toast.makeText(
                    mainView!!.context,
                    mainView!!.requireContext().getString(R.string.api_default_error),
                    Toast.LENGTH_LONG
                ).show()
                cd.dispose()
            }
        )
    }

    fun sendEntryToServer(recordData: RecordData) {
        Different.showLoadingDialog(mainView!!.context)
        val cd = CompositeDisposable()
        cd.add(networkManager
            .sendToDoctorVisit(recordData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: ResponseString ->
                Different.hideLoadingDialog()
                if (response.response == "true") mainView!!.recordingSuccessful() else Toast.makeText(
                    mainView!!.context,
                    mainView!!.requireContext().getString(R.string.api_default_error),
                    Toast.LENGTH_LONG
                ).show()
                cd.dispose()
            }) { throwable: Throwable? ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "EntryUserToDoctorPresenter\$sendEntryToServer"
                    )
                )
                if (mainView == null) {
                    return@subscribe
                }
                Different.hideLoadingDialog()
                Toast.makeText(
                    mainView!!.context,
                    mainView!!.requireContext().getString(R.string.api_default_error),
                    Toast.LENGTH_LONG
                ).show()
                cd.dispose()
            }
        )
    }
}