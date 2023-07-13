package com.medhelp.callmed2.ui._main_page.fragment_video_consultation

import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.model.VisitItem2
import com.medhelp.callmed2.data.model.VisitResponse2
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class VideoConsultationPresenter(view: VideoConsultationFragment) {
    private val mainView: VideoConsultationFragment?
    var prefManager: PreferencesManager
    var networkManager: NetworkManager

    init {
        prefManager = PreferencesManager(view.requireContext())
        networkManager = NetworkManager(prefManager)
        mainView = view
    }

    val userToken: String
        get() = prefManager.accessToken!!

    //String date="24.04.2020" ;
    val consultationList: Unit
        //        OnlineConsultationData t1=new OnlineConsultationData();
//        t1.setDuration(10*60);
//        t1.setExecuteTheScenario(OnlineConsultationData.SCENARIO_NON);
//        t1.setDocId("2");
//        t1.setDocName("Захарьин Григорий Анатольевич");
//        t1.setService("Бегать по воде");
//        t1.setTimeDateStart("11:00 20.04.2020");
//        t1.setPatientName("Иванов Иван Иванович");
        get() {
            mainView!!.showLoading()

            //String date="24.04.2020" ;
            val date = MDate.curentDate
            val cd = CompositeDisposable()
            cd.add(networkManager
                .getScheduleDocForVideoCal(prefManager.currentUserId.toString(), date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: VisitResponse2 ->
                    if (mainView == null) {
                        return@subscribe
                    }
                    processingScheduleData(response.response)
                    mainView.updateRecy(response.response)
                    mainView.hideLoading()
                    cd.dispose()
                }) { throwable: Throwable? ->
                    Timber.e(
                        LoggingTree.getMessageForError(
                            throwable,
                            "TimetableOnDayPresenter/getDataFrom "
                        )
                    )
                    if (mainView == null) {
                        return@subscribe
                    }
                    mainView.hideLoading()
                    mainView.showErrorScreen()
                    cd.dispose()
                })

//        OnlineConsultationData t1=new OnlineConsultationData();
//        t1.setDuration(10*60);
//        t1.setExecuteTheScenario(OnlineConsultationData.SCENARIO_NON);
//        t1.setDocId("2");
//        t1.setDocName("Захарьин Григорий Анатольевич");
//        t1.setService("Бегать по воде");
//        t1.setTimeDateStart("11:00 20.04.2020");
//        t1.setPatientName("Иванов Иван Иванович");
        }

    private fun processingScheduleData(list: List<VisitItem2>) {
        val idDoc = prefManager.currentUserId.toString()
        for (tmp in list) {
            tmp.executeTheScenario = Constants.SCENARIO_NON
            tmp.idDoc = idDoc
        }
    }
}