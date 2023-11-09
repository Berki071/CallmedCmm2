package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment

import androidx.lifecycle.lifecycleScope
import com.medhelp.callmed2.data.model.timetable.SettingsAllBaranchHospitalList
import com.medhelp.callmedcmm2.model.VisitResponse
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.T1ListOfEntriesFragment
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.model.DateResponse
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class TimetableOnDayPresenter() {
    var mainView: TimetableOnDayFragment? = null
    var networkManager: NetworkManager? = null
    var prefManager: PreferencesManager? = null

    val networkManagerKMM: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()

    fun onAttachView(mainView: TimetableOnDayFragment){
        this.mainView = mainView
        prefManager = PreferencesManager(mainView.requireContext())
        networkManager = NetworkManager(prefManager!!)
    }
    fun onDetachView(){
        this.mainView = null
        prefManager = null
        networkManager = null
    }

    fun allHospitalBranch() {
        if(mainView == null)
            return

        mainView?.showLoading()
        val cd = CompositeDisposable()
        cd.add(networkManager!!
            .allHospitalBranch
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: SettingsAllBaranchHospitalList ->
                Collections.sort(response.response)
                mainView?.setHospitalBranch(response.response)
                //mainView?.hideLoading()
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
        if(mainView == null)
            return

//        if(mainView?.isLoading() == false)
//            mainView?.showLoading()

        mainView?.viewLifecycleOwner?.lifecycleScope?.launch {
            kotlin.runCatching {
                networkManagerKMM.currentDateApiCall()
            }
                .onSuccess {
                    getAllReceptionApiCall(branch, it.response!!.today!!, isShowLoading = false)
                }.onFailure {
                    Timber.e(
                        LoggingTree.getMessageForError(
                            it,
                            "TimetableOnDayPresenter/getDataFrom (/date)"
                        )
                    )
                    mainView?.hideLoading()
                    mainView?.showErrorScreen()
                }
        }
    }
    fun getAllReceptionApiCall(branch: Int, dateToday: String, isShowLoading: Boolean = true){
        if(mainView == null)
            return

        if(isShowLoading)
            mainView?.showLoading()

        mainView?.viewLifecycleOwner?.lifecycleScope?.launch {

            val idDoc = prefManager!!.currentUserId.toString()
            val dbName = prefManager!!.centerInfo!!.db_name
            val token = prefManager!!.accessToken!!

            kotlin.runCatching {
                networkManagerKMM.getAllReceptionApiCall(branch.toString(), dateToday, token,dbName, idDoc)
            }
                .onSuccess {
                    mainView?.setupCalendar(dateToday, it.response.toMutableList())
                    mainView?.hideLoading()

                }.onFailure {
                    Timber.e(LoggingTree.getMessageForError(it, "TimetableOnDayPresenter/getAllReceptionApiCall (/scheduleFull/doctor/)"))
                    mainView?.hideLoading()
                    mainView?.showErrorScreen()
                }
        }
    }
}