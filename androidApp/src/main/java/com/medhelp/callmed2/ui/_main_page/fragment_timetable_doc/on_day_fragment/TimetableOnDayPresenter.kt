package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment

import com.medhelp.callmed2.data.model.DateList
import com.medhelp.callmed2.data.model.DateResponse
import com.medhelp.callmed2.data.model.timetable.SettingsAllBaranchHospitalList
import com.medhelp.callmed2.data.model.timetable.VisitList
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class TimetableOnDayPresenter(mainView: TimetableOnDayFragment) {
    var mainView: TimetableOnDayFragment?
    var networkManager: NetworkManager
    var prefManager: PreferencesManager

    init {
        this.mainView = mainView
        prefManager = PreferencesManager(mainView.requireContext())
        networkManager = NetworkManager(prefManager)
    }

    val allHospitalBranch: Unit
        get() {

            val t1 = prefManager.centerInfo!!.db_name
            val t2 = prefManager.accessToken
            val t3 = prefManager.currentUserId

            mainView?.showLoading()
            val cd = CompositeDisposable()
            cd.add(networkManager
                .allHospitalBranch
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: SettingsAllBaranchHospitalList ->
//                    if (mainView == null || mainView!!.context==null || mainView!!.isDetached) {
//                        cd.dispose()
//                        return@subscribe
//                    }
                    Collections.sort(response.response)
                    mainView?.setHospitalBranch(response.response)
                    mainView?.hideLoading()
                    cd.dispose()
                }) { throwable: Throwable? ->
                    Timber.e(LoggingTree.getMessageForError(throwable, "TimetableOnDayPresenter/getAllHospitalBranch "))

                    if (mainView == null) {
                        cd.dispose()
                        return@subscribe
                    }
                    mainView?.hideLoading()
                    mainView?.showErrorScreen()
                    cd.dispose()
                })
        }

    fun getDataFrom(branch: Int) {
        val date = arrayOfNulls<DateResponse>(1)
        mainView?.showLoading()
        val cd = CompositeDisposable()
        cd.add(networkManager
            .currentDateApiCall
            .concatMap<VisitList> { response: DateList ->
                date[0] = response.response
                networkManager.getAllReceptionApiCall(branch, response.response.today)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: VisitList ->
//                if (mainView == null || mainView!!.context==null || mainView!!.isDetached) {
//                    cd.dispose()
//                    return@subscribe
//                }
                mainView?.setupCalendar(date[0]!!.today, response.response)
                mainView?.hideLoading()
                cd.dispose()
            }) { throwable: Throwable? ->
                Timber.e(LoggingTree.getMessageForError(throwable, "TimetableOnDayPresenter/getDataFrom "))

                mainView?.hideLoading()
                mainView?.showErrorScreen()

//                if (mainView == null || mainView!!.context==null || mainView!!.isDetached) {
//                    cd.dispose()
//                    return@subscribe
//                }

                cd.dispose()
            })
    }

    fun getDataFrom(branch: Int, date: String) {
        mainView?.showLoading()
        val cd = CompositeDisposable()
        cd.add(networkManager
            .getAllReceptionApiCall(branch, date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: VisitList ->
//                if (mainView == null || mainView!!.context==null || mainView!!.isDetached) {
//                    cd.dispose()
//                    return@subscribe
//                }
                mainView?.setupCalendar(date, response.response)
                mainView?.hideLoading()
                cd.dispose()
            }) { throwable: Throwable? ->
                Timber.e(LoggingTree.getMessageForError(throwable, "TimetableOnDayPresenter/getDataFrom "))

//                if (mainView == null || mainView!!.context==null || mainView!!.isDetached) {
//                    cd.dispose()
//                    return@subscribe
//                }
                mainView?.hideLoading()
                mainView?.showErrorScreen()
                cd.dispose()
            })
    }
}