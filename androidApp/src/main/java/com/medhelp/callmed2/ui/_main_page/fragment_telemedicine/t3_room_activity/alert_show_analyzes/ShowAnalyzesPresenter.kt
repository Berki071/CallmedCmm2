package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes

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
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.AnaliseResponseAndroid
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.WorkTofFile.show_file.LoadFile
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import com.medhelp.callmedcmm2.model.chat.AnaliseResponse
import com.medhelp.callmedcmm2.network.NetworkManagerCompatibleKMM
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.Calendar
import java.util.Collections
import java.util.Locale

class ShowAnalyzesPresenter(val mainView: ShowAnalyzesAlert) {
    var prefManager: PreferencesManager = PreferencesManager(mainView.requireContext())
    var networkManager: NetworkManagerCompatibleKMM = NetworkManagerCompatibleKMM()

    fun updateAnaliseList() {
        if(mainView.view == null)
            return

        //запрос данных с сервера
        showLoadingDialog(mainView.requireContext())

        //mainScope.launch {
        mainView.lifecycleScope.launch {
            kotlin.runCatching {
                networkManager.getResultAnalysis(mainView.recordItem.token_kl!!, prefManager.centerInfo!!.db_name!!, mainView.recordItem.idKl!!.toString(), mainView.recordItem.idFilial!!.toString())
            }
                .onSuccess {
                    if (mainView == null) {
                        return@onSuccess
                    }
                    var newResp = mutableListOf<AnaliseResponseAndroid>()
                    if(it.response.size>1 || it.response[0].date!=null){
                        for(i in it.response){
                            newResp.add(AnaliseResponseAndroid(i))
                        }
                    }

                    Collections.sort(newResp)
                    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                        mainView!!.showErrorScreen()
                        return@onSuccess
                    }
                    testDownloadIn(newResp)
                    mainView.updateAnaliseData(newResp)
                    hideLoadingDialog()
                }.onFailure {
                    Timber.tag("my").e(LoggingTree.getMessageForError(it, "AnaliseResPresenter\$updateAnaliseList "))
                    if (mainView == null) {
                        return@onFailure
                    }
                    hideLoadingDialog()
                    mainView.showErrorScreen()
                }
        }
    }

    private fun testDownloadIn(list: List<AnaliseResponse>) {
        try {
            if (list.size == 1 && list[0].date == null) return

            //File pathToDownloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
            if (nnn == null) {
                clearListPath(list)
                return
            }
            if (!pathToFolderAnalise.exists() && !pathToFolderImage.exists() || nnn.size <= 0) {
                clearListPath(list)
                return
            }
            val fff1 = pathToFolderAnalise.listFiles()
            val fff2 = pathToFolderImage.listFiles()
            val allFiles = concatArray(fff1, fff2)
            for (i in list.indices) {
                val linkToFile = truncatePathToAName(list[i].linkToPDF!!)
                var boo = false
                for (f in allFiles!!) {
                    val fileName = f!!.name
                    if (linkToFile == fileName) {
                        list[i].pathToFile=f.path
                        boo = true
                        break
                    }
                }
                if (!boo) {
                    list[i].pathToFile=""
                }
            }
        } catch (e: Exception) {
            Log.wtf("fat", e.message)
        }
    }

    private fun clearListPath(list: List<AnaliseResponse>) {
        for (i in list.indices) {
            list[i].pathToFile =""
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

    fun loadFile(num: Int, list: MutableList<AnaliseResponseAndroid>) {
        try {
            list[num].isHideDownload = false
            val link: String = list[num].linkToPDF!!
            val expansion = getExpansion(link)
            if (expansion == "pdf") {
                ShowFile2.BuilderPdf(mainView.requireContext())
                    .load(link)
                    .token(mainView.recordItem.token_kl)
                    .setListener(object : ShowFile2.ShowListener {
                        override fun complete(file: File) {
                            val dd = file.exists()
                            testDownloadIn(list)
                            list[num].pathToFile = file.path
                            list[num].isHideDownload = true
                            if (mainView == null) return
                            mainView!!.updateAnaliseData(list)
                        }

                        override fun error(error: String?) {
                            list[num].isHideDownload = true
                            (mainView.requireContext() as T3RoomActivity).runOnUiThread(Runnable {
                                mainView!!.updateAnaliseData(list)
                                mainView!!.showErrorDownload()
                            })
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
                            testDownloadIn(list)
                            list[num].pathToFile = img[0].absolutePath
                            list[num].isHideDownload = true
                            if (mainView == null) {
                                return
                            }
                            mainView!!.updateAnaliseData(list)
                        }

                        override fun error(err: String?) {
                            Timber.tag("my").e("ShowAnalyzesPresenter/loadFile_jpg " + err+ " "+link)

                            list[num].isHideDownload = true
                            (mainView.requireContext() as T3RoomActivity).runOnUiThread(Runnable {
                                mainView!!.updateAnaliseData(list)
                                mainView!!.showErrorDownload()
                            })
                        }
                    })
            } else {
                showAlertUnknown(expansion)
            }
        } catch (e: Exception) {
            Log.wtf("fat", e.message)
        }
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

    private fun getExpansion(path: String): String {
        val index = path.lastIndexOf(".")
        return path.substring(index + 1).lowercase(Locale.getDefault())
    }

    fun deleteFile(num: Int, list: MutableList<AnaliseResponseAndroid>) {
        val path: String = list[num].pathToFile
        if (path != "") {
            try {
                val f = File(path)
                f.delete()
                testDownloadIn(list)
                mainView!!.updateAnaliseData(list)
                //((MainActivity) context).runOnUiThread(() -> );
            } catch (e: Exception) {
                Log.wtf("fat", e.message)
            }
        } else {
            mainView!!.updateAnaliseData(list)
        }
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