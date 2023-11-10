package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.WorkTofFile.convert_Base64.ProcessingFileB64AndImg
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.db.RealmDb
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.IOException

class RecyItemPresenter() {
    var mainView: RecyChatView? = null
    var prefManager: PreferencesManager? = null
    var convertBase64: ProcessingFileB64AndImg? = ProcessingFileB64AndImg()
    var networkManager: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()

    fun attachView(mainView: RecyChatView){
        this.mainView = mainView
        prefManager = PreferencesManager(mainView.context)
    }
    fun detachView(){
        this.mainView = null
        prefManager = null
    }

    fun loadFile(item: MessageRoomItem, listener: RoomHolderProcessingFileListener){
        (mainView!!.context as T3RoomActivity).lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.getFileForMessageRoom(item.idMessage!!.toString(),
                    prefManager!!.accessToken!!, prefManager!!.centerInfo!!.db_name!!, prefManager!!.currentUserId.toString())
            }
                .onSuccess {
                    item.text = it.response[0].data_file
                    processingOnImageOrFile(item, listener)
                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "RecyItemPresenter/loadFile(LoadAllMessagesSOTR_v2_fulldata/)"))
                    listener.error(item,"Ошибка!","Не удалось загрузить файл")
                }
        }
    }
    private fun processingOnImageOrFile(item: MessageRoomItem, listener: RoomHolderProcessingFileListener) {
        if(convertBase64 == null || mainView == null ||  mainView!!.context == null || prefManager == null){
            listener.error(item,"Ошибка!","Не удалось скопировать")
            return
        }

        val ext = when (item.type) {
            T3RoomActivity.MsgRoomType.IMG.toString() -> "png"
            T3RoomActivity.MsgRoomType.FILE.toString() -> "pdf"
            T3RoomActivity.MsgRoomType.REC_AUD.toString() -> "wav"
            else -> "pdf"
        }

        val newUri = getNewUriForNewFile(mainView!!.listener!!.getRecordItem()?.idRoom.toString(), ext, item.idMessage!!.toString())?.first

        if (newUri == null || item.text == null) {
            item.text = "null"
            listener.error(item,"Ошибка!","Не удалось создать файл для сохранения")
        } else {
            val tmp = convertBase64!!.base64ToFile(mainView!!.context, item.text!! , newUri)

            if (!tmp)
                listener.error(item,"Ошибка!","Не удалось скопировать файл для сохранения")
            else {
                item.text = newUri.toString()
                listener.processingFileComplete(item)
            }
        }
    }

    fun getNewUriForNewFile(idRoom: String, extensionF: String, idMessage: String? = null, timeMillis: String? = null): Pair<Uri, File>?  {
        if(extensionF.isEmpty())
            return null

        val idCenter: String = prefManager!!.centerInfo!!.idCenter.toString()

        val pathToCacheFolder: File = mainView!!.context.getCacheDir()
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
            return Pair(FileProvider.getUriForFile(mainView!!.context, "com.medhelp.callmed2.fileprovider", newFile),newFile)

        } catch (e: IOException) {
            Timber.e(LoggingTree.getMessageForError(e, "T3RoomPresenter/generateFileCamera "))
        }
        return null
    }

   interface RoomHolderProcessingFileListener{
       fun error(item: MessageRoomItem, title: String, msg: String)
       fun processingFileComplete(item: MessageRoomItem)
   }
}