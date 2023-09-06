package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_conclusions

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.error.ANError
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.WorkTofFile.show_file.LoadFile
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.model.chat.AnaliseResponse
import com.medhelp.callmedcmm2.model.chat.DataClassForElectronicRecy
import com.medhelp.callmedcmm2.model.chat.LoadDataZaklAmbItem
import com.medhelp.callmedcmm2.model.chat.ResultZakl2Item
import com.medhelp.callmedcmm2.model.chat.ResultZakl2Response
import com.medhelp.callmedcmm2.model.chat.ResultZaklResponse
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.Locale

class ShowConclusionsPresenter(val mainView: ShowConclusionsAlert) {
    var prefManager: PreferencesManager = PreferencesManager(mainView.requireContext())
    var networkManager: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()
    var networkManagerLocal: NetworkManager = NetworkManager(prefManager)

    var res1: ResultZaklResponse? = null

    fun getData() {
        //запрос данных с сервера
        showLoadingDialog(mainView.requireContext())

        //mainScope.launch {
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.getResultZakl(mainView.recordItem.token_kl!!, prefManager.centerInfo!!.db_name!!, mainView.recordItem.idKl!!.toString(), mainView.recordItem.idFilial!!.toString())
            }
                .onSuccess {
                    res1 = it
                    getData2()
                }.onFailure {
                    Timber.tag("my").e(LoggingTree.getMessageForError(it, "ShowConclusionsPresenter/getFilesForRecy1"))
                    if (mainView == null) {
                        return@onFailure
                    }
                    hideLoadingDialog()
                    mainView!!.initRecy(null)
                }
        }
    }
    fun getData2(){
        mainView.viewLifecycleOwner.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.getResultZakl2(mainView.recordItem.token_kl!!, prefManager.centerInfo!!.db_name!!, mainView.recordItem.idKl!!.toString(), mainView.recordItem.idFilial!!.toString())
            }
                .onSuccess {
                    processingGetData(it)
                }.onFailure {
                    Timber.tag("my").e(LoggingTree.getMessageForError(it, "ElectronicConclusionsPresenter\$getFilesForRecy2"))
                    if (mainView == null) {
                        return@onFailure
                    }
                    hideLoadingDialog()
                    mainView!!.initRecy(null)
                }
        }
    }

    fun processingGetData(dateList: ResultZakl2Response){
        if (mainView == null) {
            return
        }
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            mainView!!.showErrorScreen()
            return
        }
        val combinedList: MutableList<DataClassForElectronicRecy> =
            ArrayList<DataClassForElectronicRecy>()

        if (res1 != null && (res1!!.response.size > 1 || res1!!.response[0].linkToPDF != null )) {
            for( i in res1!!.response){
                //val tmp  = i.datePer
                if(MDate.stringToLong(i.datePer, MDate.DATE_FORMAT_ddMMyyyy) != null) {
                    combinedList.add(i)
                }
            }
        }

        if (dateList != null && (dateList.response.size > 1 || dateList.response.get(0).nameSpec != null)) {
            for( i in dateList!!.response){
                //val tmp  = i.datePer
                if(MDate.stringToLong(i.dataPriema, MDate.DATE_FORMAT_ddMMyyyy) != null) {
                    combinedList.add(i)
                }
            }
        }

        if (combinedList.size == 0) mainView!!.initRecy(null) else {
            testDownloadIn(combinedList)

            val comparator =
                Comparator { o1: DataClassForElectronicRecy, o2: DataClassForElectronicRecy ->
                    val name1 = o1.datePer
                    val name2 = o2.datePer
                    if (name1 == null || name2 == null || name1 == "" || name2 == "" || name1 == name2) return@Comparator 0
                    val d1 = MDate.stringToLong(
                        name1,
                        MDate.DATE_FORMAT_ddMMyyyy
                    )
                    val d2 = MDate.stringToLong(
                        name2,
                        MDate.DATE_FORMAT_ddMMyyyy
                    )

                    if(d1==null || d2==null)
                        return@Comparator 0

                    return@Comparator if (d2 > d1) 1 else if (d2 < d1) -1 else 0
                }
            combinedList.sortWith(comparator)


            mainView!!.initRecy(combinedList)
        }
        hideLoadingDialog()
    }

    private fun testDownloadIn(list: List<DataClassForElectronicRecy>) {
        try {
            val pathToDownloadFolder = mainView.requireContext().cacheDir
            val pathToFolder = File(pathToDownloadFolder, LoadFile.NAME_FOLDER_APP)
            if (!pathToFolder.exists()) {
                clearListPath(list)
                return
            }
            val pathToFolderAnalise = File(pathToFolder, LoadFile.NAME_FOLDER_ANALISE)
            val pathToFolderImage = File(pathToFolder, LoadFile.NAME_FOLDER_CHAT)
            val mmm = pathToFolderAnalise.list()
            val mmm2 = pathToFolderImage.list()
            val nnn = concatArray(mmm, mmm2)
            if (nnn == null || nnn.size <= 0 || !pathToFolderAnalise.exists() && !pathToFolderImage.exists()) {
                clearListPath(list)
                return
            }
            val fff1 = pathToFolderAnalise.listFiles()
            val file_size: Int = java.lang.String.valueOf(fff1[0].length()).toInt()

            val fff2 = pathToFolderImage.listFiles()
            val allFiles = concatArray(fff1, fff2)
            for (i in list.indices) {
                var fileNameList: String
                fileNameList =
                    if (list[i] is AnaliseResponse)
                        truncatePathToAName((list[i] as AnaliseResponse).linkToPDF!!)
                    else {
                        (list[i] as ResultZakl2Item).getNameFileWithoutExtension(mainView.recordItem.fullNameKl!!)
                    }

                var boo = false
                for (f in allFiles!!) {
                    val fileName = f!!.name
                    if (fileName.contains(fileNameList)) {
                        list[i].pathToFile = f.path
                        boo = true
                        break
                    }
                }
                if (!boo) {
                    list[i].pathToFile = ""
                }
            }
        } catch (e: Exception) {
            Log.wtf("fat", e.message)
        }
    }

    private fun clearListPath(list: List<DataClassForElectronicRecy>) {
        for (i in list.indices) {
            list[i].pathToFile = ""
        }
    }
    private fun concatArray(a: Array<String?>?, b: Array<String?>?): Array<String?>? {
        if (a == null && b == null) return null
        if (a == null) return b
        if (b == null) return a
        val r = arrayOfNulls<String>(a.size + b.size)
        System.arraycopy(a, 0, r, 0, a.size)
        System.arraycopy(b, 0, r, a.size, b.size)
        return r
    }
    private fun concatArray(a: Array<File?>?, b: Array<File?>?): Array<File?>? {
        if (a == null && b == null) return null
        if (a == null) return b
        if (b == null) return a
        val r = arrayOfNulls<File>(a.size + b.size)
        System.arraycopy(a, 0, r, 0, a.size)
        System.arraycopy(b, 0, r, a.size, b.size)
        return r
    }

    private fun truncatePathToAName(name: String): String {
        val startTruncate = name.indexOf("path=") + 5
        val entTruncate = name.length
        return name.substring(startTruncate, entTruncate)
    }

    fun deleteFile(item: DataClassForElectronicRecy) {
        val path: String = item.pathToFile
        if (path != "") {
            try {
                val f = File(path)
                f.delete()
                testDownloadIn(Arrays.asList<DataClassForElectronicRecy?>(item))
                mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
            } catch (e: Exception) {
                Log.wtf("fat", e.message)
            }
        } else {
            mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
        }
    }

    fun loadFile(item: AnaliseResponse) {
        try {
            item.isHideDownload = false
            val link: String = item.linkToPDF!!
            val expansion = getExpansion(link)
            //val ddd: String = prefManager.currentUserInfo!!.apiKey!!
            if (expansion == "pdf") {
                ShowFile2.BuilderPdf(mainView.requireContext())
                    .load(link)
                    .token(mainView.recordItem.token_kl)
                    .setListener(object : ShowFile2.ShowListener {
                        override fun complete(file: File) {
                            val dd = file.exists()
                            testDownloadIn(Arrays.asList<DataClassForElectronicRecy?>(item))
                            item.pathToFile = file.path
                            item.isHideDownload = true
                            // main.electronicConclusionsAdapter.updateItemInRecy(item);
                            if (mainView == null) return
                            mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
                        }

                        override fun error(error: String?) {
                            item.isHideDownload = true
                            mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
                            (mainView.requireContext() as T3RoomActivity).runOnUiThread(Runnable { mainView!!.showErrorDownload() })
                        }
                    })
                    .build()
            } else if (expansion == "jpg" || expansion == "jpeg") {
                LoadFile(
                    mainView.requireContext(),
                    ShowFile2.TYPE_IMAGE,
                    truncatePathToAName(link),
                    link,
                    mainView.recordItem.token_kl,
                    null,
                    object : LoadFile.LoadFileListener {
                        override fun success(img: List<File>) {
                            val dd = img[0].exists()
                            testDownloadIn(Arrays.asList<DataClassForElectronicRecy?>(item))
                            item.pathToFile = img[0].absolutePath
                            item.isHideDownload = true
                            mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
                            if (mainView == null) {
                                return
                            }
                            mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
                        }

                        override fun error(err: String) {
                            item.isHideDownload = true
                            mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
                            (mainView.requireContext() as T3RoomActivity).runOnUiThread(Runnable { mainView!!.showErrorDownload() })
                        }
                    })
            } else {
                showAlertUnknown(expansion)
            }
        } catch (e: Exception) {
            Log.wtf("fat", e.message)
        }
    }

    private fun getExpansion(path: String): String {
        val index = path.lastIndexOf(".")
        return path.substring(index + 1).lowercase(Locale.getDefault())
    }

    var dialogMsg: AlertDialog? = null
    private fun showAlertUnknown(expansion: String) {
        val str = "Неизвестное расширение файла \"$expansion\""
        val inflater: LayoutInflater = mainView.requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_2textview_btn, null)
        val title: TextView = view.findViewById<TextView>(R.id.title)
        val text: TextView = view.findViewById<TextView>(R.id.text)
        val btnYes = view.findViewById<Button>(R.id.btnYes)
        val btnNo = view.findViewById<Button>(R.id.btnNo)
        btnNo.visibility = View.GONE
        title.setText(Html.fromHtml("<u>Ошибка!</u>"))
        text.setText(str)
        btnYes.setOnClickListener { v: View? -> dialogMsg!!.cancel() }
        val builder = AlertDialog.Builder(view.context)
        builder.setView(view)
        dialogMsg = builder.create()
        dialogMsg!!.show()
    }

    fun loadFile2(item: ResultZakl2Item) {
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.geDataResultZakl2(item, mainView.recordItem.token_kl!!, prefManager.centerInfo!!.db_name!!, mainView.recordItem.idKl!!.toString(), mainView.recordItem.idFilial!!.toString())
            }
                .onSuccess {
                    val dir = dir

                    val extension = if(prefManager.centerInfo!!.db_name == "tomograd_podolsk" && it.response.get(0).sotrSpec!=null && it.response.get(0).sotrSpec!! == "мрт") ".html" else ".pdf"
                    val fName: String = item.getNameFileWithExtension(mainView.recordItem.fullNameKl!!, extension)

                    if(extension == ".pdf") {
                        downloadPdfFromInternet(it.response.get(0), dir, fName, item)
                    }else{
                        saveHtmlStringToFile(it.response.get(0), dir, fName, item)
                    }
                }.onFailure {
                    Timber.tag("my").e(LoggingTree.getMessageForError(it, "ElectronicConclusionsPresenter\$loadFile2"))
                    if (mainView == null) {
                        return@onFailure
                    }
                    item.isHideDownload=true
                    mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
                    mainView!!.showErrorScreen()
                }
        }
    }
    private val dir: String
        private get() {
            val pathToDownloadFolder = mainView.requireContext().cacheDir
            val pathToFolder = File(pathToDownloadFolder, LoadFile.NAME_FOLDER_APP)
            val pathToFolderAnalise = File(pathToFolder, LoadFile.NAME_FOLDER_ANALISE)
            if (!pathToFolderAnalise.exists()) {
                pathToFolderAnalise.mkdirs()
            }
            return pathToFolderAnalise.absolutePath
        }

    private fun saveHtmlStringToFile(data: LoadDataZaklAmbItem, dirPath: String, fileName: String, item: ResultZakl2Item){
        val f = File(dirPath)
        val f2 = File(f, "/$fileName")
        if (f2.exists()) f2.delete()
        try {
            f2.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        f2.writeText(data.textHtml!!)

        testDownloadIn(Arrays.asList<DataClassForElectronicRecy?>(item))
        item.pathToFile = f2.path
        item.isHideDownload = true
        updateItemInMainThread(item)
    }

    private fun downloadPdfFromInternet(data: LoadDataZaklAmbItem, dirPath: String, fileName: String, item: ResultZakl2Item) {
        val cd = CompositeDisposable()
        cd.add(networkManagerLocal
            .loadFileZakl(data, dirPath, fileName, load2FileListener, item, mainView.recordItem.fullNameKl!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ cd.dispose() }) { throwable: Throwable? ->
                item.isHideDownload = true
                mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item)
                Timber.tag("my").e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "ElectronicConclusionsPresenter/downloadPdfFromInternet"
                    )
                )
                cd.dispose()
            })
    }

    var load2FileListener: NetworkManager.Load2FileListener = object : NetworkManager.Load2FileListener {
        override fun onResponse(response: Response, dirPath: String, fileName: String, item: ResultZakl2Item) {
            byteToFile(response, dirPath, fileName, item)
        }


        override fun onError(anError: ANError, item: ResultZakl2Item) {
            item.isHideDownload = true
            mainView.electronicConclusionsAdapter!!.updateItemInRecy(item)
            Timber.tag("my").e(
                LoggingTree.getMessageForError(
                    anError,
                    "ElectronicConclusionsPresenter/load2FileListener"
                )
            )
        }
    }

    fun byteToFile(response: Response, dirPath: String?, fileName: String, item: ResultZakl2Item) {
        if (response.code != 200) {
            item.isHideDownload = true
            updateItemInMainThread(item)
            Timber.tag("my").e("ElectronicConclusionsPresenter/load2FileListener $response")
        }
        val thread = Thread {
            val head = response.headers
            val body: ResponseBody? = response.body
            val f = File(dirPath)
            val f2 = File(f, "/$fileName")
            if (f2.exists()) f2.delete()
            try {
                f2.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (f2.exists()) {
                try {
                    var inputStream: InputStream? = null
                    var outputStream: OutputStream? = null
                    try {
                        val fileReader = ByteArray(4096)
                        val fileSize: Long = body!!.contentLength()
                        var fileSizeDownloaded: Long = 0
                        inputStream = body.byteStream()
                        outputStream = FileOutputStream(f2)
                        while (true) {
                            val read = inputStream.read(fileReader)
                            if (read == -1) {
                                break
                            }
                            outputStream.write(fileReader, 0, read)
                            fileSizeDownloaded += read.toLong()
                        }
                        outputStream.flush()
                        testDownloadIn(Arrays.asList<DataClassForElectronicRecy?>(item))
                        item.pathToFile = f2.path
                        item.isHideDownload = true
                        updateItemInMainThread(item)
                    } catch (e: IOException) {
                        item.isHideDownload = true
                        updateItemInMainThread(item)
                        Timber.tag("my").e(
                            LoggingTree.getMessageForError(
                                e,
                                "ElectronicConclusionsPresenter/byteToFile"
                            )
                        )
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                } catch (e: IOException) {
                    item.isHideDownload = true
                    updateItemInMainThread(item)
                    Timber.tag("my").e(
                        LoggingTree.getMessageForError(
                            e,
                            "ElectronicConclusionsPresenter/byteToFile"
                        )
                    )
                }
            }
            item.isHideDownload = true
            updateItemInMainThread(item)
        }
        thread.start()
    }

    fun updateItemInMainThread(item: ResultZakl2Item) {
        if(mainView.context!=null && !mainView.isDetached)
            mainView!!.requireActivity().runOnUiThread { mainView!!.electronicConclusionsAdapter!!.updateItemInRecy(item) }
    }

    var progressDialog: AlertDialog? = null
    var timeStartProgressDialog: Long? = null
    fun showLoadingDialog(context: Context?): Boolean {
        if (context!=null && (progressDialog == null || (progressDialog != null && !progressDialog!!.isShowing))) {
            timeStartProgressDialog = Calendar.getInstance().timeInMillis
            val view = (context as Activity).layoutInflater.inflate(R.layout.dialog_progres, null)
            val builder = AlertDialog.Builder(context /*, R.style.DialogTheme*/)
            builder.setView(view)
            progressDialog = builder.create()
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            return true
        }
        return false
    }

    var handler: Handler? = null
    var runnable: Runnable? = null
    fun hideLoadingDialog() {
        if (progressDialog == null) return
        if (timeStartProgressDialog == null) timeStartProgressDialog = 0L
        val tmp = Calendar.getInstance().timeInMillis
        val tmp2 = tmp - timeStartProgressDialog!!
        if (tmp2 < 800 && tmp2 > 80) {
            handler = Handler()
            runnable = Runnable {
                try {
                    progressDialog!!.dismiss()
                } catch (e: Exception) {
                }
            }
            handler!!.postDelayed(runnable!!, tmp2)
        } else {
            progressDialog!!.dismiss()
        }
    }
}