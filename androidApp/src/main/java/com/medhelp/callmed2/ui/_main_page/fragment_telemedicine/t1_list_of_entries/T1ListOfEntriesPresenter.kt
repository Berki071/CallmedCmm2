package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries

import android.app.NotificationManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessagingService
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.bg.Notification.SimpleNotificationForFCM
import com.medhelp.callmed2.data.model.AllRecordsTelemedicineItemAndroid
import com.medhelp.callmed2.data.model.ServiceItem
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active.T1ListOfEntriesDataModel
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.ArrayList
import java.util.Locale

class T1ListOfEntriesPresenter{
    var mainView: T1ListOfEntriesFragment? = null
    var prefManager: PreferencesManager? = null
    val networkManagerKMM: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()

    var mainList: MutableList<AllRecordsTelemedicineItem>? = null
    //var searchList: MutableList<T1ListOfEntriesDataModel>? = null
    var query = ""

    fun onAttachView(mainView: T1ListOfEntriesFragment){
        this.mainView = mainView
        prefManager = PreferencesManager(mainView.requireContext())
    }
    fun onDetachView(){
        mainView = null
        prefManager = null
    }


    fun getData(type: String) {  //type = "old" or any string
        if(mainView == null || prefManager == null)
            return

        Different.showLoadingDialog(mainView?.requireContext())
        val idDoc = prefManager!!.currentUserId.toString()
        val dbName = prefManager!!.centerInfo!!.db_name
        val token = prefManager!!.accessToken!!


        mainView?.viewLifecycleOwner?.lifecycleScope?.launch {
            kotlin.runCatching {
                networkManagerKMM.getAllRecordsTelemedicine(type, token, dbName, idDoc)
            }
                .onSuccess {
                    if(mainView == null)
                        return@onSuccess

                    if(it.response.size>1 || it.response[0].idRoom != null) {
                        if (mainView!!.whatDataShow == T1ListOfEntriesFragment.WhatDataShow.ACTIVE.toString()) {
                           // it.response[0].status = Constants.TelemedicineStatusRecord.wait.toString()
                            //it.response[0].dataServer = "26.04.2023 02:30:26"
                           //it.response[0].tmTimeForTm = 2

                            checkActiveItemOnComplete(it.response)
                            mainList = removeWaitItemWhichNoPay(it.response)
                            val tmpList = processingDataRecordsForRecy(mainList!!)

                            mainView?.updateRecyActive(tmpList)

                        } else {
                            mainList = it.response!!.toMutableList()
                            val tmp = filtrationList()
                            mainView?.updateRecyArchive(tmp)
                        }
                    }else{
                        mainView?.let { itMV ->
                            if(itMV.whatDataShow == T1ListOfEntriesFragment.WhatDataShow.ACTIVE.toString())
                                itMV.updateRecyActive(mutableListOf())
                            else
                                itMV.updateRecyArchive(mutableListOf())
                        }
                    }

                    mainView?.binding?.swipeProfile?.isRefreshing = false
                    Different.hideLoadingDialog()
                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T1ListOfEntriesPresenter.getData"))
                    if (mainView == null) {
                        return@onFailure
                    }
                    mainView?.let { itMV ->
                        if (itMV.whatDataShow == T1ListOfEntriesFragment.WhatDataShow.ACTIVE.toString())
                            itMV.updateRecyActive(mutableListOf())
                        else
                            itMV.updateRecyArchive(mutableListOf())
                    }

                    mainView?.binding?.swipeProfile?.isRefreshing = false
                    Different.hideLoadingDialog()
                }
        }
    }
    fun setFilterService(query: String) {
        var query = query
        query = query.lowercase(Locale.getDefault())
        if (this.query == query) return
        this.query = query

        val tmpL = filtrationList()
        mainView?.updateRecyArchive(tmpL)
    }
    private fun filtrationList() : MutableList<AllRecordsTelemedicineItem> {
        if(mainList == null)
            return mutableListOf()

        if(query.isEmpty())
            return mainList!!

        val filteredModelList: MutableList<AllRecordsTelemedicineItem> = ArrayList()
        for (item in mainList!!) {
            val text = item.fullNameKl!!.lowercase(Locale.getDefault())
            if (text.contains(query)) {
                filteredModelList.add(item)
            }
        }

        return if (filteredModelList.size == 0)
            mutableListOf()
        else
            filteredModelList
    }

    fun checkActiveItemOnComplete(response: List<AllRecordsTelemedicineItem>){
        //закрыть активный прием если у него вышло время

        for(i in response){
            if(i.status!! == Constants.TelemedicineStatusRecord.active.toString()){
                val currentTimeLong: Long = MDate.stringToLong(i.dataServer!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
                val dateStartLong: Long = MDate.stringToLong(i.dataStart!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss) ?: 0L
                val dateEndLong: Long = dateStartLong + (i.tmTimeForTm!!.toLong()*60*1000)

                if(currentTimeLong>=dateEndLong) {
                    Timber.tag("my").d("T1ListOfEntriesPresenter closeTm " +
                            "item.dataServe:${i.dataServer!!}, item.dataStart:${i.dataStart!!}, item.tmTimeForTm:${i.tmTimeForTm!!}, tmId:${i.tmId}")

                    closeRecordTelemedicine(i)
                }
            }
        }
    }
    fun removeWaitItemWhichNoPay(response: List<AllRecordsTelemedicineItem>) : MutableList<AllRecordsTelemedicineItem> {
        // удалить из списка не оплаченный wait
        var newList = mutableListOf<AllRecordsTelemedicineItem>()
        for(i in response){
            if(i.status != Constants.TelemedicineStatusRecord.wait.toString() || i.statusPay == "true"){
                newList.add(i)
            }
        }
        return newList
    }
    fun processingDataRecordsForRecy(response: MutableList<AllRecordsTelemedicineItem>) : MutableList<T1ListOfEntriesDataModel>{
        var mainList = mutableListOf<T1ListOfEntriesDataModel>()

        var masActive = mutableListOf<AllRecordsTelemedicineItemAndroid>()
        var masWait = mutableListOf<AllRecordsTelemedicineItemAndroid>()

        for(i in response){
            val itm = AllRecordsTelemedicineItemAndroid(i)

            when(itm.status){
                Constants.TelemedicineStatusRecord.wait.toString() ->{
                    masWait.add(itm)
                }
                Constants.TelemedicineStatusRecord.active.toString() ->{
                    masActive.add(itm)
                }
            }
        }

        if(masActive.size>0){
            mainList.add(T1ListOfEntriesDataModel("Активные",masActive))
        }
        if(masWait.size>0){
            mainList.add(T1ListOfEntriesDataModel("Ожидают начала",masWait))
        }

        return mainList
    }

    val isCheckLoginAndPassword: Boolean
        get() {
            prefManager?.let{
                val idUser = it.currentUserId
                val pass = it.currentPassword
                return if (idUser == 0 || pass == "") false else true
            }
            return false
        }

    fun closeRecordTelemedicine(item: AllRecordsTelemedicineItem){
        if(mainView == null || prefManager == null)
            return


        Different.showLoadingDialog(mainView?.requireContext())

        mainView?.lifecycleScope?.launch {
            kotlin.runCatching {
                networkManagerKMM.closeRecordTelemedicine(item.idRoom!!.toString(), item.tmId!!.toString()
                    ,prefManager!!.accessToken!!, prefManager!!.centerInfo!!.db_name!!, prefManager!!.currentUserId.toString())
            }
                .onSuccess {
                    Timber.tag("my").d("Закрыт по истечению времени tmId ${item.tmId}")
                    getData("new")
                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T1ListOfEntriesPresenter.closeRecordTelemedicine(payTM) "))
                    if (mainView == null) {
                        return@onFailure
                    }
                    getData("new")
                }
        }
    }


    fun areThereAnyNewTelemedicineMsg(){
        //крутится в процессе жизненного цикла

        if(mainView == null || prefManager == null)
            return

        val lifecycle = mainView?.viewLifecycleOwner

        lifecycle?.lifecycleScope?.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    kotlin.runCatching {
                        networkManagerKMM.areThereAnyNewTelemedicineMsg(prefManager!!.accessToken!!, prefManager!!.centerInfo!!.db_name!!, prefManager!!.currentUserId.toString())
                    }
                        .onSuccess {
                            if(mainView == null)
                                return@onSuccess

                            if(it.response.size>1 || it.response[0].idRoom != null)
                                mainView?.adapterActive?.processingHasNewMsg(it.response)
                            else
                                mainView?.adapterActive?.processingHasNewMsg(listOf())


//                                var itmTest = HasPacChatsItem()
//                                itmTest.idRoom = 14
//                                val listTest = listOf<HasPacChatsItem>(itmTest)
//                                mainView.adapterActive?.processingHasNewMsg(listTest)

                        }.onFailure {
                            if(it.message==null || (it.message!=null  && !it.message!!.contains("connect_timeout=unknown ms")))
                                Timber.tag("my").w(LoggingTree.getMessageForError(it, "T1ListOfEntriesPresenter.areThereAnyNewTelemedicineMsg(Has_pac_chats/)"))
                            if (mainView == null) {
                                return@onFailure
                            }
                        }

                    delay(1000)
                }
            }

        }
    }


    //region notification time reminder
    fun sendMsgNotificationTimeReminder(item: AllRecordsTelemedicineItemAndroid, msg: String) {
        mainView?.let{
            val simpleNotificationForFCM = SimpleNotificationForFCM(it.requireContext(), it.requireContext().getSystemService(
                FirebaseMessagingService.NOTIFICATION_SERVICE)
                    as NotificationManager
            )
            //TelemedicineNotificationType все что не равны сообщению переводят на всеКомнаты
            simpleNotificationForFCM.showDataTelemedicine("Внимание!", msg, item.idRoom.toString(), item.tmId.toString(), Constants.TelemedicineNotificationType.START_APPOINTMENT.fullName)
        }
    }

    fun updateTelemedicineReminderDocAboutRecord(item: AllRecordsTelemedicineItemAndroid, type: String){
        if(mainView == null || prefManager == null)
            return

        mainView?.lifecycleScope?.launch {
            kotlin.runCatching {
                networkManagerKMM.updateTelemedicineReminderDocAboutRecord(type, item.tmId!!.toString()
                    , prefManager!!.accessToken!!, prefManager!!.centerInfo!!.db_name!!, prefManager!!.currentUserId.toString())
            }
                .onSuccess {
                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T1ListOfEntriesPresenter.updateTelemedicineReminderDocAboutRecord(UpdateNotifDoctorForTMbyDoc/) "))
                }
        }
    }

    //endregion
}