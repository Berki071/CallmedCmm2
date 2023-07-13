package com.medhelp.callmed2.ui._main_page.fragment_scanner_doc

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.NewSyncItem
import com.medhelp.callmed2.data.model.timetable.SimpleBooleanList
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class ScannerDocPresenter(val mainView: ScannerDocFragment) {
    val FOLDER_SCANER = "scanner"
    val preferencesManager = PreferencesManager(mainView.requireActivity())
    val networkManager = NetworkManager(preferencesManager)

    var syncInfo: NewSyncItem? = null

    var onDestroyView = false


    fun getData() {
        if(!isTestAcc())
            showSynchronizationDialog()
        else
            hideSynchronizationDialog()

        val cd = CompositeDisposable()
        cd.add(networkManager
            .syncData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.response[0].id_kl == null) {
                        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                            override fun run() {
                                if (!onDestroyView) {
                                    getData()
                                }
                            }
                        },500)
                } else {
                    syncInfo = response.response[0]
                    Log.wtf(
                        "sdfsdfs",
                        "response.syncInfo.id_sync " + syncInfo?.id_sync + " syncInfo.id_centr " + syncInfo?.id_centr + " syncInfo.id_kl " + syncInfo?.id_kl
                    )
                    hideSynchronizationDialog()
                }
                cd.dispose()
            }, { throwable ->
                // hideSynchronizationDialog()
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "ScannerDocPresenter/getData"
                    )
                )
                if(mainView != null && mainView.isAdded) {
                    Different.showAlertInfo(
                        mainView.requireActivity(),
                        null,
                        mainView.resources.getString(R.string.some_error)
                    )
                }

                cd.dispose()
            }
            )
        )
    }

    fun isTestAcc(): Boolean{
        val tt = preferencesManager.currentUserName
        return tt == null || tt == "9130696928"
    }

    fun getNewUriForPhoto(): Uri? {
        val pathToCacheFolder: File = mainView.requireActivity().getCacheDir()
        val pathToFolderScanner: File = File(pathToCacheFolder, FOLDER_SCANER)
        if (!pathToFolderScanner.exists()) {
            pathToFolderScanner.mkdirs()
        }

        val nameNewFile = "/scanner_" + System.currentTimeMillis() + ".jpg"

        val newFile = File(pathToFolderScanner, nameNewFile)

        try {
            newFile.createNewFile()
            return FileProvider.getUriForFile(mainView.requireContext(), "com.medhelp.callmed2.fileprovider", newFile)

        } catch (e: IOException) {
            Timber.e(LoggingTree.getMessageForError(e, "ScannerDocPresenter/getNewUriForPhoto "))
        }

        return null
    }

    fun deleteAllFilesInCash() {
        mainView.clearAllImage()

        val pathToCacheFolder: File = mainView.requireActivity().getCacheDir()
        val pathToFolderScanner: File = File(pathToCacheFolder, FOLDER_SCANER)
        if (!pathToFolderScanner.exists()) {
            return
        }

        val listFiles = pathToFolderScanner.listFiles()
        if (listFiles.isNotEmpty()) {
            for (i in listFiles) {
                i.delete()
            }
        }
    }


    fun showSynchronizationDialog() {
        mainView.dialogSychronization.visibility = View.VISIBLE
        mainView.btnSend.visibility = View.GONE
    }

    fun hideSynchronizationDialog() {
        mainView.dialogSychronization.visibility = View.GONE
        mainView.btnSend.visibility = View.VISIBLE
    }

    var counterImageSync = 0
    fun sendImageToServer(list: MutableList<String>) {
        Different.showLoadingDialog(mainView.requireContext())

        counterImageSync = 0
        list.toObservable()
            .flatMap { itemmRes ->
                //Log.wtf("MyLog","перед bitmapToBase64 "+itemmRes)
                bitmapToBase64(itemmRes)
            }
            .flatMap { resBase64 ->
                //Log.wtf("MyLog","перед uploadImage")
                networkManager.uploadImage(syncInfo!!.id_sync, resBase64)
            }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleBooleanList> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: SimpleBooleanList) {
                    //Log.wtf("MyLog","выполнено uploadImage")
                    counterImageSync++
                    Toast.makeText(
                        mainView.requireContext(),
                        "Отправлено документов: " + counterImageSync,
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(e: Throwable) {
                    Different.hideLoadingDialog()
                    Timber.e(
                        LoggingTree.getMessageForError(
                            e,
                            "ScannerDocPresenter\$sendImageToServer"
                        )
                    )
                    Different.showAlertInfo(mainView.requireActivity(), null, "Ошибка")
                }

                override fun onComplete() {
                    //deleteAllFilesInCash()
                   // Log.wtf("MyLog","отправка закончена")
                    changeStatusSync()
                }
            })
    }

    fun bitmapToBase64(uriPath: String): Observable<String> {
        val cr: ContentResolver = mainView.requireActivity().getContentResolver()
        var bitmap: Bitmap
        cr.openInputStream(Uri.parse(uriPath))
            .use { input -> bitmap = BitmapFactory.decodeStream(input) }

//        //для исправления поворота при переводе в битмап
//        try {
//            mainView.requireContext().getContentResolver().openInputStream(Uri.parse(uriPath))
//                .use { inputStream ->
//                    val exif: ExifInterface = ExifInterface(inputStream!!)
//                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//
//                    val matrix = Matrix()
//
//                    when (orientation) {
//                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
//                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
//                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
//                    }
//
//                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//
//                }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        val sizeList =
//            SearchLoadFile.calculationOfProportionsBitmap(bitmap.width, bitmap.height, 1920, 1080)
//
//        bitmap = Bitmap.createScaledBitmap(bitmap, sizeList.get(0), sizeList.get(1), false)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Observable.just(Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT))
    }

    fun changeStatusSync() {

        val cd = CompositeDisposable()
        cd.add(networkManager
            .changeStatusSync(syncInfo!!.id_sync, true.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                deleteAllFilesInCash()
                Different.hideLoadingDialog()

                mainView.requireActivity()?.let {
                    Different.showAlertInfo(
                        it, null, "Документы успешно загружены",
                        object : Different.AlertInfoListener {
                            override fun clickOk() {
                                getData()
                            }

                            override fun clickNo() {
                                getData()
                            }
                        },
                        false
                    )
                }

                cd.dispose()
            }, { throwable ->
                Different.hideLoadingDialog()
                getData()
                Timber.e(
                    LoggingTree.getMessageForError(
                        throwable,
                        "ScannerDocPresenter/getData"
                    )
                )
                mainView.requireActivity()?.let {
                    Different.showAlertInfo(it, null, mainView.resources.getString(R.string.some_error))
                }
                cd.dispose()
            }
            )
        )
    }
}

