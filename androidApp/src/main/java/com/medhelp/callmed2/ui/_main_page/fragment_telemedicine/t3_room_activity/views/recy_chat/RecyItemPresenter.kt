package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat

import android.net.Uri
import android.os.Handler
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.WorkTofFile.convert_Base64.ProcessingFileB64AndImg
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.PrintWriter

class RecyItemPresenter() {
    var mainView: RecyChatView? = null
    var prefManager: PreferencesManager? = null
    var convertBase64: ProcessingFileB64AndImg? = ProcessingFileB64AndImg()
    var networkManager: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    var networkManagerOld: NetworkManager? = null

    fun attachView(mainView: RecyChatView){
        this.mainView = mainView
        prefManager = PreferencesManager(mainView.context)
        networkManagerOld = NetworkManager(prefManager!!)
    }
    fun detachView(){
        this.mainView = null
        prefManager = null
        networkManagerOld = null
    }

    var tryLoadCounter = 0
    fun loadFile(item: MessageRoomItem, listener: RoomHolderProcessingFileListener){
        if(item.type == T3RoomActivity.MsgRoomType.VIDEO.toString()){
            if(networkManagerOld == null)
                return

            if(mainView == null ||  mainView!!.context == null || prefManager == null){
                listener.error(item,"Ошибка!","Не удалось создать фаил")
                return
            }

            val ext = getExt(item)
            val pathToFolderScanner = getPathToFolderTelemedicine().absolutePath
            var fileName = createNameFile(mainView!!.listener!!.getRecordItem()?.idRoom.toString(), ext, item.idMessage!!.toString())
            //fileName = fileName + "&token=" + prefManager!!.accessToken

            val cd = CompositeDisposable()
            cd.add(networkManagerOld!!
                .loadVideoFile(item.text!!+ "&token=" + prefManager!!.accessToken, pathToFolderScanner, fileName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    processingConvertingToFileVideo(item, pathToFolderScanner, fileName, listener)

                    cd.dispose()
                }) { throwable: Throwable? ->
                    Timber.e(LoggingTree.getMessageForError(throwable, "RecyItemPresenter/loadFile"))
                    listener.error(item, "Ошибка!", "Не удалось загрузить файл")
                    cd.dispose()
                })

        }else {
            (mainView!!.context as T3RoomActivity).lifecycleScope.launch {
                kotlin.runCatching {
                    networkManager.getFileForMessageRoom(
                        item.idMessage!!.toString(),
                        prefManager!!.accessToken!!,
                        prefManager!!.centerInfo!!.db_name!!,
                        prefManager!!.currentUserId.toString()
                    )
                }
                    .onSuccess {
                        if(it.response[0].data_file == null){
                            val handler = Handler()
                            handler.postDelayed({
                                if (tryLoadCounter < 10) {
                                    loadFile(item, listener)
                                    tryLoadCounter = tryLoadCounter + 1
                                } else {
                                    Timber.tag("my").w("LoaderFileForChat/load количество попыток загрузки исчерпано")
                                    listener.error(
                                        item,
                                        "Ошибка!",
                                        "Не удалось создать файл для сохранения"
                                    )
                                }
                            },2000L)
                            return@onSuccess
                        }
                        tryLoadCounter = 0

                        item.text = it.response[0].data_file
                        processingOnImageOrFile(item, listener)
                    }.onFailure {
                        Timber.tag("my").w(LoggingTree.getMessageForError(it, "RecyItemPresenter/loadFile(LoadAllMessagesSOTR_v2_fulldata/)"))
                        listener.error(item, "Ошибка!", "Не удалось загрузить файл")
                    }
            }
        }
    }
    private fun processingOnImageOrFile(item: MessageRoomItem, listener: RoomHolderProcessingFileListener) {
        if(convertBase64 == null || mainView == null ||  mainView!!.context == null || prefManager == null){
            listener.error(item,"Ошибка!","Не удалось скопировать")
            return
        }

        val ext = getExt(item)
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

    fun getExt(item: MessageRoomItem) : String{
       return when (item.type) {
            T3RoomActivity.MsgRoomType.IMG.toString() -> "png"
            T3RoomActivity.MsgRoomType.FILE.toString() -> "pdf"
            T3RoomActivity.MsgRoomType.REC_AUD.toString() -> "wav"
            T3RoomActivity.MsgRoomType.VIDEO.toString() -> "mp4"
            else -> "pdf"
        }
    }
    fun getNewUriForNewFile(idRoom: String, extensionF: String, idMessage: String? = null, timeMillis: String? = null): Pair<Uri, File>?  {
        if(extensionF.isEmpty())
            return null

        val pathToFolderScanner = getPathToFolderTelemedicine()
        val nameNewFile = "/"+createNameFile(idRoom,extensionF, idMessage, timeMillis)

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
    private fun getPathToFolderTelemedicine(): File{
        val pathToCacheFolder: File = mainView!!.context.cacheDir
        val pathToFolderScanner = File(pathToCacheFolder, T3RoomActivity.FOLDER_TELEMEDICINE)

        if (!pathToFolderScanner.exists()) {
            pathToFolderScanner.mkdirs()
        }

        return pathToFolderScanner
    }
    private fun createNameFile(idRoom: String, extensionF: String, idMessage: String? = null, timeMillis: String? = null): String{
        val idCenter: String = prefManager!!.centerInfo!!.idCenter.toString()

        return if(idMessage != null)
            T3RoomActivity.PREFIX_NAME_FILE + "_" + idCenter + "_" + idRoom + "_"+idMessage+"_" + (timeMillis ?: System.currentTimeMillis()) + "." + extensionF
        else
            T3RoomActivity.PREFIX_NAME_FILE + "_" + idCenter + "_" + idRoom +"_" + (timeMillis ?: System.currentTimeMillis()) + "." + extensionF
    }

    fun processingConvertingToFileVideo(item: MessageRoomItem, pathToFolderScanner: String, fileName: String, listener: RoomHolderProcessingFileListener){
        (mainView!!.context as T3RoomActivity).lifecycleScope.launch {
            kotlin.runCatching {
                convertingToFileVideo(pathToFolderScanner, fileName)
            }
                .onSuccess {
                    val newFile = File(pathToFolderScanner, "/"+fileName)
                    val newUri = FileProvider.getUriForFile(mainView!!.context, "com.medhelp.callmed2.fileprovider", newFile)
                    item.text = newUri.toString()
                    listener.processingFileComplete(item)
                }.onFailure {
                    Timber.tag("my").w(LoggingTree.getMessageForError(it, "RecyItemPresenter/processingConvertingToFileVideo)"))
                    listener.error(item, "Ошибка!", "Не удалось загрузить файл")
                }
        }
    }
    suspend fun convertingToFileVideo(dirPath: String, fileName: String): File? {
        val nFile = File(dirPath, fileName)
        if (!nFile.exists()) {
            return error(Throwable("convertingToPdf: Файла не существует $dirPath/$fileName"))
        }
        val str: String? = gerStringFromFile(nFile.absolutePath)

        //очистка файла
        val writer: PrintWriter
        try {
            writer = PrintWriter(nFile)
            writer.print("")
            writer.close()
        } catch (e: FileNotFoundException) {
            Timber.tag("my").e(LoggingTree.getMessageForError(null, fileName + " convertingToPdf.1 " + e.message))
            return error(e)
        }

        //write the bytes in file
        val fos: FileOutputStream
        try {
            val pdfAsBytes = Base64.decode(str, 0)
            fos = FileOutputStream(nFile, false)
            fos.write(pdfAsBytes)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            Timber.tag("my").e(LoggingTree.getMessageForError(null, fileName + " RecyItemPresenter/convertingToPdf.2 " + e.message))
            return error(e)
        } catch (e: IOException) {
            Timber.tag("my").e(LoggingTree.getMessageForError(null, fileName + " RecyItemPresenter/convertingToPdf.3 " + e.message))
            return error(e)
        } catch (e: IllegalArgumentException) {
            Timber.tag("my").e(LoggingTree.getMessageForError(null, fileName + " RecyItemPresenter/convertingToPdf.4 " + e.message))
            return error(e)
        } catch (e: Exception) {
            Timber.tag("my").e(LoggingTree.getMessageForError(null, fileName + " RecyItemPresenter/convertingToPdf.5 " + e.message))
            return error(e)
        }
        return nFile
    }
    private fun gerStringFromFile(filePath: String): String? {
        val contentBuilder = StringBuilder()
        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var sCurrentLine: String?
                while (br.readLine().also { sCurrentLine = it } != null) {
                    contentBuilder.append(sCurrentLine).append("\n")
                }
            }
        } catch (e: IOException) {
            Timber.tag("my").e(LoggingTree.getMessageForError(e, "RecyItemPresenter/gerStringFromFile $filePath"))
            return null
        }
        return contentBuilder.toString()
    }



   interface RoomHolderProcessingFileListener{
       fun error(item: MessageRoomItem, title: String, msg: String)
       fun processingFileComplete(item: MessageRoomItem)
   }
}