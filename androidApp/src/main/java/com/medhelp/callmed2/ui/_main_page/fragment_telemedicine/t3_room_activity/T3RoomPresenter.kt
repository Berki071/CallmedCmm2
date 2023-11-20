package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.WorkTofFile.convert_Base64.ProcessingFileB64AndImg
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmed2.utils.timber_log.LoggingTree.Companion.getMessageForError
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
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

    fun sendMessageToServer(idUser: String, item: MessageRoomItem, idBranch: String){
        mainView.binding.recyCustom.presenter.processingAndAddListItemsInRecy(mutableListOf(item))

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
                            mainView.binding.recyCustom.adapter!!.deleteItem(item)
                            return@onSuccess
                        }

                        if(mainView.binding.recyCustom.presenter.lastIdMessage < response.response[0].idMessage!!)
                            mainView.binding.recyCustom.presenter.lastIdMessage = response.response[0].idMessage!!

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
                        mainView.binding.recyCustom.presenter.addMessagesToRealm(mutableListOf(item))

                        mainView.binding.recyCustom.adapter?.let{
                            mainView.binding.recyCustom.adapter!!.updateItemIdMessageById(item._id, response.response[0].idMessage!!, response.response[0].dataMessage!!, newUriFileStr)
                        }
                        sendMsgNotification()
                    }else{
                        mainView.binding.recyCustom.adapter!!.deleteItem(item)
                    }
                }
                .onFailure { error ->
                    Timber.tag("my").w(LoggingTree.getMessageForError(error, "T3RoomPresenter(SendNewMessageToChatPAC/)"))
                    if (mainView == null) {
                        return@onFailure
                    }else{
                        mainView.binding.recyCustom.adapter?.let {
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

            val newUri = mainView?.binding?.recyCustom?.presenterItems?.getNewUriForNewFile(item.idRoom!!, extension!!, idMessage, timeMillis)?.first

            newUri?.let{ur ->
                convertBase64.copyFileByUri(mainView,uriFile, ur)
                val contentResolver: ContentResolver = mainView.getContentResolver()
                contentResolver.delete(uriFile, null, null)

                return ur.toString()
            }
        }

        return null
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
            val currentTimeLong: Long = MDate.stringToLong(response.dataServer!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss)
            val dateStartLong: Long = MDate.stringToLong(response.dataStart!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss) ?: 0L
            val dateEndLong: Long = dateStartLong + (response.tmTimeForTm!!.toLong() * 60 * 1000)

            if (currentTimeLong >= dateEndLong) {
               Timber.tag("my").d("T3RoomPresenter closeTm " +
                        "item.dataServe:${response.dataServer!!}, item.dataStart:${response.dataStart!!}, item.tmTimeForTm:${response.tmTimeForTm!!}, tmId:${response.tmId}")

                closeRecordTelemedicine(response)
            }
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