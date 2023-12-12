package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.db.RealmDb
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RecyChatPresenter {
    var mainView: RecyChatView? = null
    var networkManager: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    var prefManager: PreferencesManager? = null

    var lastIdMessage = 0
    fun onAttachView(mainView: RecyChatView){
       this.mainView = mainView
        prefManager = PreferencesManager(mainView.context)
    }
    fun onDetachView(){
        mainView = null
        prefManager = null
    }

    fun  getAllMessageFromRealm(idRoom: Int){
        val list = RealmDb.getAllMessageByIdRoom(idRoom.toString())
        if(list.isNotEmpty()){
            var newList = processingAddTariffMessages(list)
            newList = processingAddDateInMessages(newList)
            mainView?.initRecy(newList)
        }else{
            mainView?.initRecy(mutableListOf())
        }
    }

    fun getNewMessagesInLoopFromServer(idRoom: String) {
        // за счет повторения запроса в цикле должна вызываться только раз и крутится внутри while

        if(mainView == null)
            return

        //Different.showLoadingDialog(mainView)
        checkLastIdMessage()

        val lifecycle = (mainView!!.context as T3RoomActivity)

        //Log.wtf("sdfgetNewMessagesInLoopFromServer" ,"start")
        lifecycle.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    kotlin.runCatching {
                        networkManager.getMessagesRoom(idRoom, lastIdMessage.toString(), prefManager!!.accessToken!!, prefManager!!.centerInfo!!.db_name!!,
                            prefManager!!.currentUserId.toString()
                        )
                    }
                        .onSuccess {
                            //Log.wtf("sdfgetNewMessagesInLoopFromServer" ,"end")
                            if (it.response.size > 1 || it.response[0].idMessage != null) {

                                val newResponse: MutableList<MessageRoomItem> = mutableListOf()
                                for(i in it.response){
                                    if(i.type == T3RoomActivity.MsgRoomType.VIDEO.toString() && i.text != null && i.text!!.length > 100){
                                        deleteMessageFromServer(i)
                                    }else{
                                        newResponse.add(i)
                                    }
                                }

                                val listNewMFromRealm = addMessagesToRealm(newResponse, true)

                                if(listNewMFromRealm.isNotEmpty()){
                                    processingAndAddListItemsInRecy(listNewMFromRealm as MutableList<MessageRoomItem>)
                                    checkLastIdMessage()
                                }else{
                                    checkLastIdMessage()
                                }
                            }
                            Different.hideLoadingDialog()
                            mainView?.checkFirstHideLoading()

                        }.onFailure {
                            if(it.message==null || (it.message!=null  && !it.message!!.contains("connect_timeout=unknown ms")))
                                Timber.tag("my").w(LoggingTree.getMessageForError(it, "T3RoomPresenter(LoadAllMessagesPAC/)"))

                            if (mainView == null) {
                                return@onFailure
                            }

                            Different.hideLoadingDialog()
                        }

                    delay(1000)
                }
            }
        }
    }

    fun processingAndAddListItemsInRecy(list: MutableList<MessageRoomItem>){
        val lastItem = mainView?.adapter?.getLastMessage()

        var newList = processingAddTariffMessages(list, lastItem)
        newList = processingAddDateInMessages(newList, lastItem)
        mainView?.adapter?.addMessagesForShow(newList)
    }
    fun processingAddTariffMessages(list: List<MessageRoomItem>, lastMsg: MessageRoomItem? = null): MutableList<MessageRoomItem>{
        var nList = mutableListOf<MessageRoomItem>()

        if(lastMsg == null) {
            var tmp = MessageRoomItem()
            tmp.data = list[0].data!!
            tmp.type = T3RoomActivity.MsgRoomType.TARIFF.toString()
            tmp.idTm = list[0].idTm
            tmp.nameTm = list[0].nameTm!!
            nList.add(tmp)
            nList.add(list[0])
        }else{
            if(lastMsg!!.idTm != list[0].idTm){
                var tmp = MessageRoomItem()
                tmp.data = list[0].data
                tmp.type = T3RoomActivity.MsgRoomType.TARIFF.toString()
                tmp.idTm = list[0].idTm
                tmp.nameTm = list[0].nameTm!!
                nList.add(tmp)
            }
            nList.add(list[0])
        }

        for (i in 1 until list.size) {
            if (list[i].nameTm != list[i-1].nameTm) {
                var tmp = MessageRoomItem()
                tmp.data = list[i].data
                tmp.type = T3RoomActivity.MsgRoomType.TARIFF.toString()
                tmp.idTm = list[i].idTm
                tmp.nameTm = list[i].nameTm!!
                nList.add(tmp)
            }
            nList.add(list[i])
        }

        return nList
    }
    fun processingAddDateInMessages(list: MutableList<MessageRoomItem>, lastMsg: MessageRoomItem? = null) : MutableList<MessageRoomItem>{
        var nList = mutableListOf<MessageRoomItem>()

        if(lastMsg == null) {
            var tmp = MessageRoomItem()
            tmp.data = list[0].data
            tmp.type = T3RoomActivity.MsgRoomType.DATE.toString()
            nList.add(tmp)
            nList.add(list[0])
        }else{
            if(lastMsg.data!!.substring(0,10) != list[0].data!!.substring(0,10)){
                var tmp = MessageRoomItem()
                tmp.data = list[0].data
                tmp.type = T3RoomActivity.MsgRoomType.DATE.toString()
                nList.add(tmp)
            }
            nList.add(list[0])
        }

        for(i in 1 until  list.size){
            if(list[i].data!!.substring(0,10) != list[i-1].data!!.substring(0,10)){
                var tmp = MessageRoomItem()
                tmp.data = list[i].data
                tmp.type = T3RoomActivity.MsgRoomType.DATE.toString()
                nList.add(tmp)
            }
            nList.add(list[i])
        }

        return nList
    }

    fun addMessagesToRealm(list: List<MessageRoomItem>, isNeedProcessing: Boolean = false): List<MessageRoomItem>{
        // от вставки двух сообщений сразy
        if(RealmDb.latchWrite == false) {
            Log.wtf("fatttt", "processingAndAddListItemsInRecy id " + list[0]._id.toString())
            return RealmDb.addListMessages(list)
        }else{
            val handler = Handler()
            handler.postDelayed({
                Log.wtf("fatttt", "processingAndAddListItemsInRecy id "+ list[0]._id.toString() )
                val rList = RealmDb.addListMessages(list)

                if(isNeedProcessing) {
                    if(rList.size>0)
                        processingAndAddListItemsInRecy(rList as MutableList<MessageRoomItem>)
                    checkLastIdMessage()
                }
            },100L)

            return listOf()
        }
    }

    fun checkLastIdMessage(){
        mainView?.listener!!.getRecordItem()?.let{
            lastIdMessage = RealmDb.getMaxIdMessageByIdRoom(it.idRoom.toString())
        }
    }

    fun deleteMessageFromServer(item: MessageRoomItem){
        if(mainView == null)
            return

        Different.showLoadingDialog(mainView!!.context) //скроет показ загрузки getAllMessagesInLoo

        (mainView!!.context as T3RoomActivity).lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.deleteMessageFromServer(item.idMessage!!.toString(),
                    prefManager!!.accessToken!!, prefManager!!.centerInfo!!.db_name!!, prefManager!!.currentUserId.toString())
            }
                .onSuccess {
                    if(item.type == T3RoomActivity.MsgRoomType.IMG.toString() || item.type == T3RoomActivity.MsgRoomType.FILE.toString()){
                        val uriF: Uri = Uri.parse(item.text!!)
                        val contentResolver: ContentResolver = (mainView!!.context as T3RoomActivity).contentResolver
                        contentResolver.delete(uriF, null, null)
                    }

                    RealmDb.deleteMessage(item)
                    mainView?.adapter?.let {
                        it.deleteItem(item)
                    }
                }.onFailure {
                    RealmDb.deleteMessage(item)
                    mainView?.adapter?.let {
                        it.deleteItem(item)
                    }

                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T3RoomPresenter(ChatDeleteMessagePAC/)"))
                    if (mainView == null) {
                        return@onFailure
                    }

                }
        }
    }
}