package com.medhelp.callmed2.ui._main_page.fragment_doc_recognition

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import java.io.*
import java.util.*

class DocRecognitionPresenter(val mainView: DocRecognitionFragment) {
    var prefManager: PreferencesManager
    var networkManager: NetworkManager

    init {
        prefManager = PreferencesManager(mainView.requireContext())
        networkManager = NetworkManager(prefManager)
    }

    private fun generateTwo_digitNumber(): Int {
        val rand = Random()
        return rand.nextInt(100)
    }

//    fun generateFileFoto(file: File?): File {
//        val pathToDownloadFolder = requireContext().cacheDir
//
//
//        val str = "/image_" + System.currentTimeMillis() + generateTwo_digitNumber() + ".jpg"
//        val newFile = File(pathToDownloadFolder, str)
//        try {
//            newFile.createNewFile()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        try {
//            BufferedInputStream(
//                FileInputStream(file)
//            ).use { `in` ->
//                BufferedOutputStream(
//                    FileOutputStream(newFile)
//                ).use { out ->
//                    val buffer = ByteArray(1024)
//                    var lengthRead: Int
//                    while (`in`.read(buffer).also { lengthRead = it } > 0) {
//                        out.write(buffer, 0, lengthRead)
//                        out.flush()
//                    }
//                }
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return newFile
//    }

    private fun copyFile(am: AssetManager, assetName: String, outFile: File) {
        try {
            am.open(assetName).use { `in` -> FileOutputStream(outFile).use { out ->
                val buffer = ByteArray(1024)
                var read: Int
                while (`in`.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
            }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun extractText(bitmap: Bitmap) {
//        image.setImageBitmap(bitmap)
//
//        val tess = TessBaseAPI()
//        if (!tess.init(getTessDataPath(requireContext()), "rus", TessBaseAPI.OEM_TESSERACT_LSTM_COMBINED)) {
//            // Error initializing Tesseract (wrong data path or language)
//            tess.recycle();
//            return;
//        }
//
//         //Specify image and then recognize it and get result (can be called multiple times during Tesseract lifetime)
//        tess.setImage(bitmap)
//        var text = tess.utF8Text
//
//        tess.recycle()
//
//        text = text.replace("=","")
//        text = text.replace("+","")
//        text = text.replace("_","")
//        text = text.replace("|","")
//        text = text.replace("$","")
//        text = text.replace("&","")
//        text = text.replace("©","")
//        text = text.replace("!","")
//        text = text.replace("{","")
//        text = text.replace("}","")
//        text = text.replace("°","")
//
//        this.text.text = text
//        Log.wtf("Poznanie",">>> "+text)

    }

    fun extractAssets(context: Context) {
        val am: AssetManager = context.getAssets()

        val tessDir: File = File(getTessDataPath(context), "tessdata")
        if (!tessDir.exists()) {
            tessDir.mkdir()
        }
        val engFile = File(tessDir, "rus.traineddata")
        if (!engFile.exists()) {
            //copyFile(am, "rus.traineddata", engFile)
        }
    }
    fun getTessDataPath(context: Context): String? {
        // We need to return folder that contains the "tessdata" folder,
        // which is in this sample directly the app's files dir
        return context.getFilesDir().getAbsolutePath()
    }
}