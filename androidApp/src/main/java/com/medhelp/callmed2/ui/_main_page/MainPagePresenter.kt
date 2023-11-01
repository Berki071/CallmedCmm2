package com.medhelp.callmed2.ui._main_page

import com.medhelp.callmed2.data.model.timetable.DoctorList
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainPagePresenter(private val mainView: MainPageActivity) {
    var preferencesManager: PreferencesManager
    var networkManager: NetworkManager
    var networkManager2 = NetworkManagerCompatibleKMM()

    init {
        preferencesManager = PreferencesManager(mainView)
        networkManager = NetworkManager(preferencesManager)
    }

    fun removePassword() {
        preferencesManager.currentPassword = ""
        preferencesManager.currentUserId = 0
        preferencesManager.currentCenterId = 0
        preferencesManager.isShowPartCallCenter = false
        preferencesManager.isShowPartMessenger = false
        preferencesManager.isShowPartTimetable = false
        preferencesManager.isShowPartScanDoc = false
        preferencesManager.isShowPartRaspDoc = false
        preferencesManager.isShowPassportRecognize = false
    }

    fun getCurrentDocInfo() {
        if (preferencesManager.currentPassword != null && preferencesManager.currentPassword != "") {

            val tmp1 = preferencesManager.centerInfo!!.db_name
            val tmp2 = preferencesManager.accessToken
            val tmp3 = preferencesManager.currentUserId.toString()
            val tmp4 = preferencesManager.currentUserId.toString()

            val cd = CompositeDisposable()
            cd.add(networkManager
                .doctorById
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response: DoctorList ->
                        mainView.updateHiaderDocName(response.response[0])
                    }
                ) { throwable: Throwable? ->
                    Timber.e(
                        LoggingTree.getMessageForError(
                            throwable,
                            "MainPagePresenter/getCurrentDocInfo()"
                        )
                    )
                })
        }
    }

//    fun startCheckShowNotyRasp(){
//        val handler = Handler()
//        handler.postDelayed({
//            val currentDay = MDate.curentDate
//            val dayNotyRaspInPref = preferencesManager.dataShowRaspNoty
//            if((dayNotyRaspInPref!=null && currentDay==dayNotyRaspInPref) || !preferencesManager.isShowNotifications)
//                return@postDelayed
//
//            val currentTime = MDate.stringToLong(MDate.curentTime, MDate.DATE_FORMAT_HHmm)
//            val startPeriod = MDate.stringToLong("20:30", MDate.DATE_FORMAT_HHmm)
//            val endPeriod = MDate.stringToLong("21:00", MDate.DATE_FORMAT_HHmm)
//
//            if(currentTime in startPeriod..endPeriod){
//                requestServerDataForNoty()
//            }
//
//            startCheckShowNotyRasp()
//                            }, 1000*60)
//    }
//    fun requestServerDataForNoty(){
//        var dateTomottow =MDate.longToString(MDate.getCurrentDate()+(1000*60*60*24), MDate.DATE_FORMAT_ddMMyyyy)
//
//        mainView.lifecycleScope.launch {
//            kotlin.runCatching {
//                networkManager2.findRaspTomorrow(dateTomottow,
//                    preferencesManager.accessToken!!, preferencesManager.centerInfo!!.db_name!!, preferencesManager.currentUserId.toString())
//            }
//                .onSuccess {
//                    if(it.response.size>1 || it.response[0].data != null){
//                        preferencesManager.dataShowRaspNoty = MDate.curentDate
//
//                        var msg = ""
//                        for(i in it.response){
//                            msg += "${i.naim_filial}: с ${i.start} по ${i.end} (${i.kab})\n"
//                        }
//                        msg= msg.trim()
//
//                        val simpleNotificationForFCM = SimpleNotificationForFCM(mainView,
//                            mainView.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager)
//
//                        simpleNotificationForFCM.showDataRasp("Расписание на завтра!", msg)
//                    }
//                }.onFailure {
//                    Timber.e(LoggingTree.getMessageForError(it, "MainPagePresenter/requestServerDataForNoty"))
//                }
//        }
//    }
}