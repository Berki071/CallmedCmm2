package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.WorkTofFile.convert_Base64.ProcessingFileB64AndImg
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmed2.utils.timber_log.LoggingTree.Companion.getMessageForError
import com.medhelp.callmedcmm2.db.RealmDb
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


class T3RoomPresenter(val mainView: T3RoomActivity) {
    var prefManager: PreferencesManager = PreferencesManager(mainView)
    var networkManager: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    var convertBase64: ProcessingFileB64AndImg = ProcessingFileB64AndImg()

    var lastIdMessage = 0


//    fun terxzt(){
//        val tmp = ResultZakl2Item()
//        tmp.idKl = "15"
//        tmp.idFilial = 2
//        tmp.nameSpec = "Гинекология 2"
//        tmp.dataPriema = "05.12.2021"
//
//        mainView.lifecycleScope.launch {
//            kotlin.runCatching {
//                networkManager.geDataResultZakl2(tmp, "ODU2UG7O1U4EK0CRNO2J", "vpraktikdemo", "5", "1")
//            }
//                .onSuccess {
//                   Log.wtf("","")
//                }.onFailure {
//                    Log.wtf("","")
//                }
//        }
//    }

    fun getNewMessagesInLoopFromServer(idRoom: String) {
        // за счет повторения запроса в цикле должна вызываться только раз и крутится внутри while

        //Different.showLoadingDialog(mainView)
        checkLastIdMessage()

        val lifecycle = mainView

        lifecycle.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    kotlin.runCatching {
                        networkManager.getMessagesRoom(idRoom, lastIdMessage.toString(), prefManager.accessToken!!, prefManager.centerInfo!!.db_name!!,
                            prefManager.currentUserId.toString()
                        )
                    }
                        .onSuccess {
                            if (it.response.size > 1 || it.response[0].idMessage != null) {

                                processingOnImageOrFile(it.response)
                                val listNewMFromRealm = addMessagesToRealm(it.response, true)

                                if(listNewMFromRealm.isNotEmpty()){
                                    processingAndAddListItemsInRecy(listNewMFromRealm as MutableList<MessageRoomItem>)
                                    checkLastIdMessage()
                                }else{
                                    checkLastIdMessage()
                                }
                            }
                            Different.hideLoadingDialog()
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

    fun  getAllMessageFromRealm(idRoom: Int){
        val list = RealmDb.getAllMessageByIdRoom(idRoom.toString())
        if(list.isNotEmpty()){
            var newList = processingAddTariffMessages(list)
            newList = processingAddDateInMessages(newList)
            mainView.initRecy(newList)
        }else{
            mainView.initRecy(mutableListOf())
        }
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


    fun processingOnImageOrFile(list: List<MessageRoomItem>){
        for (i in list){
            if(i.type == T3RoomActivity.MsgRoomType.IMG.toString() || i.type == T3RoomActivity.MsgRoomType.FILE.toString()
                || i.type == T3RoomActivity.MsgRoomType.REC_AUD.toString()){
                val ext = when(i.type){
                    T3RoomActivity.MsgRoomType.IMG.toString() -> "png"
                    T3RoomActivity.MsgRoomType.FILE.toString() -> "pdf"
                    T3RoomActivity.MsgRoomType.REC_AUD.toString() -> "wav"
                    else -> "pdf"
                }

                val newUri = getNewUriForNewFile(mainView.recordItem!!.idRoom.toString(),ext, i.idMessage!!.toString())?.first

                if(newUri==null){
                   Different.showAlertInfo(mainView, "Ошибка!", "Не удалось создать файл для сохранения")
                }else{
                   val tmp = convertBase64.base64ToFile(mainView, i.text!!, newUri)

                    if (!tmp)
                        Different.showAlertInfo(mainView, "Ошибка!", "Не удалось скопировать файл для сохранения")
                    else{
                        i.text = newUri.toString()
                    }
                }
            }
        }
    }
    fun processingAndAddListItemsInRecy(list: MutableList<MessageRoomItem>){
        val lastItem = mainView.adapter?.getLastMessage()

        var newList = processingAddTariffMessages(list, lastItem)
        newList = processingAddDateInMessages(newList, lastItem)
        mainView.adapter?.let{
            it.addMessagesForShow(newList)
        }
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

    fun sendMessageToServer(idUser: String, item: MessageRoomItem, idBranch: String){
        processingAndAddListItemsInRecy(mutableListOf(item))

        mainView.lifecycleScope.launch {
            // елсли фаил то в сообщении будет его бэйс64
            val valueText: String =  if(item.type == T3RoomActivity.MsgRoomType.TEXT.toString()) item.text!! else convertBase64.fileToBase64String(mainView, Uri.parse(item.text!!))

//            if(item.type != T3RoomActivity.MsgRoomType.TEXT.toString()){
//                val uriFile = Uri.parse(item.text!!)
//                var isExistFile = false    //проверка которая работает
//                if (null != uriFile) {
//                    try {
//                        val inputStream: InputStream? = mainView.getContentResolver().openInputStream(uriFile)
//                        isExistFile = inputStream != null
//                        inputStream?.close()
//                    } catch (e: Exception) {
//                    }
//                }
//                Log.wtf("dfdfd",""+isExistFile)
//            }

            kotlin.runCatching {
                networkManager.sendMessageFromRoom(item.idRoom!!, item.idTm!!.toString(), idUser, item.type!!, valueText,
                    prefManager.accessToken!!, prefManager.centerInfo!!.db_name!!, prefManager.currentUserId.toString(), idBranch)
            }
                .onSuccess { response ->
                    if(response.response[0].idMessage != null){
                        if(response.response[0].idMessage!! == -1){
                            getOneRecordInfo(item.idRoom!!, item.idTm!!.toString())
                            mainView.adapter!!.deleteItem(item)
                            return@onSuccess
                        }

                        if(lastIdMessage < response.response[0].idMessage!!)
                            lastIdMessage = response.response[0].idMessage!!

                        //если файл то надо пересохранить с idMessage
                        val newUriFileStr = if(item.type == T3RoomActivity.MsgRoomType.TEXT.toString())
                            null
                        else {
                            renameImageWithId(item, response.response[0].idMessage.toString())
                        }

                        item.idMessage = response.response[0].idMessage
                        item.data = response.response[0].dataMessage
                        if(newUriFileStr!=null) {
                            item.text = newUriFileStr

                            val uriFile = Uri.parse(newUriFileStr)
                            var isExistFile = false    //проверка которая работает
                            if (null != uriFile) {
                                try {
                                    val inputStream: InputStream? =
                                        mainView.getContentResolver().openInputStream(uriFile)
                                    isExistFile = inputStream != null
                                    inputStream?.close()
                                } catch (e: Exception) {
                                }
                            }
                            Log.wtf("dfdfd", "" + isExistFile)
                        }
                        addMessagesToRealm(mutableListOf(item))

                        mainView.adapter?.let{
                            mainView.adapter!!.updateItemIdMessageById(item._id, response.response[0].idMessage!!, response.response[0].dataMessage!!, newUriFileStr)
                        }
                        sendMsgNotification()
                    }else{
                        mainView.adapter!!.deleteItem(item)
                    }
                }
                .onFailure { error ->
                    Timber.tag("my").w(LoggingTree.getMessageForError(error, "T3RoomPresenter(SendNewMessageToChatPAC/)"))
                    if (mainView == null) {
                        return@onFailure
                    }else{
                        mainView.adapter?.let {
                            it.deleteItem(item)
                        }
                    }
                }
        }
    }

    //region notification msg
    fun sendMsgNotification() {
        mainView.recordItem?.let{
            val notiObj = creteJSONObjectNotificationForMsg(it.fcmKl!! , it.idRoom.toString(), it.tmId.toString(),  it.idKl.toString(), it.idFilial.toString()) ?: throw Exception("Не удалось создать объект для отправки нотификации")
            val servK = mainView.recordItem!!.serverKey
            sendMsgNotification2(notiObj, servK!!, Constants.SENDER_ID_FCM_PATIENT, true, it.tmId.toString())
        }
    }
    private fun creteJSONObjectNotificationForMsg(fcmKey: String, idRoom: String, idTm: String, idKl: String, idFilial: String): String? {
        try {
            val noti = JSONObject()
            noti.put("title", "Медицинский помощник.Пациент")
            noti.put("body", "Новое сообщение")
            noti.put("sound", "Enabled")
            val dopData = JSONObject()
            dopData.put("type_message", Constants.TelemedicineNotificationType.MESSAGE.fullName)
            dopData.put("idRoom", idRoom)
            dopData.put("idTm", idTm)
            dopData.put("id_kl", idKl)
            dopData.put("id_filial", idFilial)

            val obj = JSONObject()
            obj.put("to", fcmKey)
            obj.put("notification", noti)
            obj.put("data", dopData)

            return obj.toString()
        } catch (e: JSONException) {
            Timber.tag("my").w(getMessageForError(e, "T3RoomPresenter.creteJSONObjectNotification"))
        }
        return null
    }
    fun sendMsgNotification2(json: String, serverKey: String, senderId: String, isMsg: Boolean, tmId: String, status: String = "") {

        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.sendMsgFCM(json,serverKey,senderId)
            }
                .onSuccess {
                    if(!isMsg)
                        Timber.tag("my").d("Отправлено уведомление клиенту об изменении статусу приема tmId$tmId $status")
                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T3RoomPresenter.sendMsgNotification2"))
                }
        }
    }
    //endregion

    fun renameImageWithId(item: MessageRoomItem, idMessage: String ): String?{
        val uriFile = Uri.parse(item.text!!)
        val name2 = mainView.getFileName(uriFile)  //telemedicine_555_4_1680172330439.jpg

        name2?.let {
            val extension = convertBase64.getExtensionByUri(mainView, uriFile)
            val timeMillis= it.substring(it.lastIndexOf("_")+1, it.lastIndexOf("."))

            val newUri = getNewUriForNewFile(item.idRoom!!, extension!!, idMessage, timeMillis)?.first

            newUri?.let{ur ->
                convertBase64.copyFileByUri(mainView,uriFile, ur)
                val contentResolver: ContentResolver = mainView.getContentResolver()
                contentResolver.delete(uriFile, null, null)

                return ur.toString()
            }
        }

        return null
    }


    fun deleteMessageFromServer(item: MessageRoomItem){
        Different.showLoadingDialog(mainView) //скроет показ загрузки getAllMessagesInLoo

        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.deleteMessageFromServer(item.idMessage!!.toString(),
                    prefManager.accessToken!!, prefManager.centerInfo!!.db_name!!, prefManager.currentUserId.toString())
            }
                .onSuccess {
                    if(item.type == T3RoomActivity.MsgRoomType.IMG.toString() || item.type == T3RoomActivity.MsgRoomType.FILE.toString()){
                        val uriF: Uri = Uri.parse(item.text!!)
                        val contentResolver: ContentResolver = mainView.getContentResolver()
                        contentResolver.delete(uriF, null, null)
                    }

                    RealmDb.deleteMessage(item)
                    mainView.adapter?.let {
                        it.deleteItem(item)
                    }
                }.onFailure {
                    RealmDb.deleteMessage(item)
                    mainView.adapter?.let {
                        it.deleteItem(item)
                    }

                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T3RoomPresenter(ChatDeleteMessagePAC/)"))
                    if (mainView == null) {
                        return@onFailure
                    }

                }
        }
    }


    fun getNewUriForNewFile(idRoom: String, extensionF: String, idMessage: String? = null, timeMillis: String? = null): Pair<Uri,File>?  {
        if(extensionF.isEmpty())
            return null

        val idCenter: String = prefManager.centerInfo!!.idCenter.toString()

        val pathToCacheFolder: File = mainView.getCacheDir()
        val pathToFolderScanner = File(pathToCacheFolder, T3RoomActivity.FOLDER_TELEMEDICINE)

        if (!pathToFolderScanner.exists()) {
            pathToFolderScanner.mkdirs()
        }

        val nameNewFile = if(idMessage != null)
            "/"+T3RoomActivity.PREFIX_NAME_FILE + "_" + idCenter + "_" + idRoom + "_"+idMessage+"_" + (timeMillis ?: System.currentTimeMillis()) + "." + extensionF
        else
            "/"+T3RoomActivity.PREFIX_NAME_FILE + "_" + idCenter + "_" + idRoom +"_" + (timeMillis ?: System.currentTimeMillis()) + "." + extensionF

        val newFile = File(pathToFolderScanner, nameNewFile)

        try {
            newFile.createNewFile()
           // return FileProvider.getUriForFile(mainView, "com.medhelp.callmed2.fileprovider", newFile)
            return Pair(FileProvider.getUriForFile(mainView, "com.medhelp.callmed2.fileprovider", newFile),newFile)

        } catch (e: IOException) {
            Timber.e(LoggingTree.getMessageForError(e, "T3RoomPresenter/generateFileCamera "))
        }
        return null
    }


    fun checkLastIdMessage(){
        mainView.recordItem?.let{
           lastIdMessage = RealmDb.getMaxIdMessageByIdRoom(it.idRoom.toString())
        }
    }

    fun closeRecordTelemedicine(item: AllRecordsTelemedicineItem, isSendNoty:Boolean = false, isDoc: Boolean = false){
        //скрое загрузку цикличная функция

        Different.showLoadingDialog(mainView)
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.closeRecordTelemedicine(item.idRoom.toString(), item.tmId.toString()
                    ,prefManager.accessToken!!, prefManager.centerInfo!!.db_name!!, prefManager.currentUserId.toString())
            }
                .onSuccess {
                    if(!isDoc)
                        Timber.tag("my").d("Закрыт по истечению времени ${item.tmId}")
                    else
                        Timber.tag("my").d("Закрыт доктором ${item.tmId}")

                    mainView.recordItem?.let {
                        it.status = Constants.TelemedicineStatusRecord.complete.toString()
                    }
                    mainView.setVisibilityBottomBarChat(View.VISIBLE)
                    mainView.checkShowMenuItems()
                    mainView.checkTimer()

                    if(isSendNoty)
                        sendStatusNotification(Constants.TelemedicineChangeStatusAppointment.END.fullName , item, Constants.TelemedicineNotificationType.END_APPOINTMENT.fullName)

                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T3RoomPresenter.closeRecordTelemedicine(payTM) "))
                    if (mainView == null) {
                        return@onFailure
                    }
                    mainView.setVisibilityBottomBarChat(View.VISIBLE)
                }
        }
    }

    fun toActiveRecordTelemedicine(item: AllRecordsTelemedicineItem){
        Different.showLoadingDialog(mainView)
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.toActiveRecordTelemedicine(item.idRoom!!.toString(), item.tmId!!.toString()
                    ,prefManager.accessToken!!, prefManager.centerInfo!!.db_name!!, prefManager.currentUserId.toString())
            }
                .onSuccess {
                    Timber.tag("my").d("в Активные tmId ${item.tmId}")
                    mainView.recordItem?.let {
                        it.status = Constants.TelemedicineStatusRecord.active.toString()
                    }
                    mainView.setVisibilityBottomBarChat(View.INVISIBLE)
                    mainView.checkShowMenuItems()
                    getOneRecordInfo(item.idRoom.toString(), item.tmId.toString(), false)

                    sendStatusNotification(Constants.TelemedicineChangeStatusAppointment.START.fullName, item , Constants.TelemedicineNotificationType.START_APPOINTMENT.fullName)

                }.onFailure {
                    Different.hideLoadingDialog()
                    Different.showAlertInfo(mainView,"Ошибка", "Что-то пошло не так.")

                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T3RoomPresenter.toActiveRecordTelemedicine(ActiveTMbySotr) "))
                    if (mainView == null) {
                        return@onFailure
                    }
                }
        }
    }

    //region send status noty
    fun sendStatusNotification(status: String, item: AllRecordsTelemedicineItem, type: String) {
        mainView.recordItem?.let{
            val notiObj = creteJSONObjectNotificationForStatus(status, item, type) ?: throw Exception("Не удалось создать объект для отправки нотификации")
            val servK = mainView.recordItem!!.serverKey
            sendMsgNotification2(notiObj, servK!!, Constants.SENDER_ID_FCM_PATIENT, false, item.tmId.toString(),status)
        }
    }
    private fun creteJSONObjectNotificationForStatus(status: String, item: AllRecordsTelemedicineItem, type: String): String? {
        try {
            val noti = JSONObject()
            noti.put("title", "Медицинский помощник.Пациент")
            noti.put("body", status)
            noti.put("sound", "Enabled")
            val dopData = JSONObject()
            dopData.put("type_message", type)
            dopData.put("idRoom", item.idRoom.toString())
            dopData.put("idTm", item.tmId.toString())
            dopData.put("id_kl", item.idKl.toString())
            dopData.put("id_filial", item.idFilial.toString())

            val obj = JSONObject()
            obj.put("to", item.fcmKl!!)
            obj.put("notification", noti)
            obj.put("data", dopData)

            return obj.toString()
        } catch (e: JSONException) {
            Timber.tag("my").w(getMessageForError(e, "T3RoomPresenter.creteJSONObjectNotification"))
        }
        return null
    }
    //endregion

    fun getOneRecordInfo(idRoom:String, idTm: String, isRecordItemNull: Boolean = true){
        Different.showLoadingDialog(mainView)
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.getSelectRecordsTelemedicine(idRoom, idTm
                    ,prefManager.accessToken!!, prefManager.centerInfo!!.db_name!!, prefManager.currentUserId.toString())
            }
                .onSuccess {
                    if(it.response[0].idRoom != null) {
                        mainView.recordItem = it.response[0]

                        if(isRecordItemNull)
                            mainView.setUp()
                        else
                            mainView.checkTimer()

                        checkActiveItemOnComplete(it.response[0])
                    }else{
                        Timber.tag("my").w( "T3RoomPresenter.getOneRecordInfo(showTMbyIDtm/) response[0]==null")
                        Different.hideLoadingDialog()
                        Different.showAlertInfo(mainView,"Ошибка", "Что-то пошло не так.")
                    }
                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "T3RoomPresenter.getOneRecordInfo(showTMbyIDtm/)"))
                    if (mainView == null) {
                        return@onFailure
                    }
                    Different.hideLoadingDialog()
                    Different.showAlertInfo(mainView,"Ошибка", "Что-то пошло не так.")
                }
        }
    }
    fun checkActiveItemOnComplete(response: AllRecordsTelemedicineItem){
        if (response.status!! == Constants.TelemedicineStatusRecord.active.toString()) {
            val currentTimeLong: Long =
                MDate.stringToLong(response.dataServer!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            val dateStartLong: Long =
                MDate.stringToLong(response.dataStart!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss) ?: 0L
            val dateEndLong: Long = dateStartLong + (response.tmTimeForTm!!.toLong() * 60 * 1000)

            if (currentTimeLong >= dateEndLong)
                closeRecordTelemedicine(response)
        }
    }


    fun Context.getFileName(uri: Uri): String? = when(uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
        else -> uri.path?.let(::File)?.name
    }
    private fun Context.getContentFileName(uri: Uri): String? = runCatching {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
        }
    }.getOrNull()

}