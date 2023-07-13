package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment

import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.AllRaspSotrResponse
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class TimetableOnMonthPresenter(val mainView: TimetableOnMonthFragment)  {
    var networkManager: NetworkManager
    var cd : CompositeDisposable? = null
    init {
        val prefManager = PreferencesManager(mainView.requireContext())
        networkManager = NetworkManager(prefManager)
    }

    fun findAllRaspSotr(date: String){
        mainView.showLoading()
        cd = CompositeDisposable()
        cd?.add(networkManager
            .findAllRaspSotr(date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: AllRaspSotrResponse ->
                try {
                    if (mainView == null || !mainView.isAdded || mainView.requireContext() == null) {
                        return@subscribe
                    }

                    response.response.sortBy {
                        MDate.stringToLong(it.data, MDate.DATE_FORMAT_ddMMyyyy)
                    }

                    mainView.initRecy(response.response)
                    mainView.hideLoading()

                } catch (e: Exception) {}
                cd?.dispose()
            }) { throwable: Throwable? ->
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "TimetableOnMonthPresenter/findAllRaspSotr"
                    )
                )
                try{
                    if (mainView != null && mainView.isAdded && mainView.requireContext() != null) {
                        mainView.hideLoading()
                        mainView.showError(mainView.context?.resources?.getString(R.string.api_default_error) ?: "")
                    }
                }catch(e: Exception){}
                cd?.dispose()
            })
    }


    fun processingDestroy(){
        cd?.dispose()
    }
}