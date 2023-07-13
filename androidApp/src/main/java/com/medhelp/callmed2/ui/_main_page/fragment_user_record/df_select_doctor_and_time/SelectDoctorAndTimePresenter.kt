package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time

import android.content.Context
import android.util.Pair
import com.medhelp.callmed2.data.model.*
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.DataRecyServiceRecord
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class SelectDoctorAndTimePresenter(var mainView: SelectDoctorAndTimeDialog?, var context: Context) {
    var mainList: MutableList<ScheduleItem>? = null
    var prefManager: PreferencesManager
    var networkManager: NetworkManager

    init {
        prefManager = PreferencesManager(context)
        networkManager = NetworkManager(prefManager)
    }

    fun getFreeTileAll(dateMonday: String, serviceItem: ServiceItem) {
        Different.showLoadingDialog(context)
        mainList = ArrayList()
        networkManager.getFilialByIdServices(serviceItem.id.toString())
            .flatMap(Function { filialResponse: FilialResponse ->
                Observable.fromIterable(
                    filialResponse.response
                )
            } as Function<FilialResponse, Observable<FilialItem>>)
            .flatMap(Function { filialItemResponse: FilialItem ->
                Observable.zip(networkManager.getScheduleByServiceApiCall(
                    serviceItem.id.toString(), dateMonday, serviceItem.admission.toString(), filialItemResponse.id),
                    Observable.just(filialItemResponse),
                    BiFunction { scheduleResponce: ScheduleResponce, filialItemResponse1: FilialItem ->
                        Pair(
                            scheduleResponce,
                            filialItemResponse1
                        )
                    } as BiFunction<ScheduleResponce, FilialItem, Pair<ScheduleResponce, FilialItem>>)
            } as Function<FilialItem, Observable<Pair<ScheduleResponce, FilialItem>>>)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Pair<ScheduleResponce, FilialItem>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(scheduleResponseFilialItemPair: Pair<ScheduleResponce, FilialItem>) {
                    for (tmp in scheduleResponseFilialItemPair.first.response) {
                        tmp.filialItem = scheduleResponseFilialItemPair.second
                        mainList?.add(tmp)
                    }
                }

                override fun onError(e: Throwable) {
                    Different.hideLoadingDialog()
                    Timber.e(
                        LoggingTree.getMessageForError(
                            e,
                            "RecordUserPresenter\$getFreeTileAll"
                        )
                    )
                    if (mainView == null) {
                        return
                    }
                    //Different.hideLoadingDialog(context);
                    // mainView.showErrorScreen(true);
                }

                override fun onComplete() {
                    mainList?.let{
                        processingDataForRecy(it)
                    }
                }
            })
    }

    private fun processingDataForRecy(mainList: MutableList<ScheduleItem>) {
        deleteOldDateAndTime(mainList)
        if (mainList.size == 0) {
            mainView!!.initRecy(ArrayList())
            Different.hideLoadingDialog()
        } else {
            Collections.sort(mainList)
            val listRecy: MutableList<DataRecyServiceRecord> = ArrayList()
            for (tmp in mainList) {
                if (listRecy.size == 0 || listRecy[listRecy.size - 1].title != tmp.admDay) {
                    val tmpListSchedule: MutableList<ScheduleItem> = ArrayList()
                    tmpListSchedule.add(tmp)
                    val tmpDrsr = DataRecyServiceRecord(tmp.admDay, tmpListSchedule)
                    listRecy.add(tmpDrsr)
                } else {
                    listRecy[listRecy.size - 1].items.add(tmp)
                }
            }
            for (tmp in listRecy) {
                if (tmp.itemCount == 1) tmp.items[0].openListTime = true
            }
            mainView!!.initRecy(listRecy)
            Different.hideLoadingDialog()
        }
    }

    private fun deleteOldDateAndTime(mainList: MutableList<ScheduleItem>) {
        if (mainList.size == 0) return
        val currentDayLong = MDate.currenDateLong
        val currentTimeLong =
            MDate.stringToLong(MDate.curentTime, MDate.DATE_FORMAT_HHmm) + 1000 * 60 * 5
        var i = 0
        while (i < mainList.size) {
            if (mainList[i].dateLong < currentDayLong) {
                mainList.removeAt(i)
                i--
            } else if (mainList[i].dateLong == currentDayLong) {
                if (mainList[i].admTime.size == 0) {
                    i++
                    continue
                }
                var j = 0
                while (j < mainList[i].admTime.size) {
                    if (MDate.stringToLong(
                            mainList[i].admTime[j],
                            MDate.DATE_FORMAT_HHmm
                        ) < currentTimeLong
                    ) {
                        mainList[i].admTime.removeAt(j)
                        j--
                    }
                    j++
                }
            }
            i++
        }
    }
}