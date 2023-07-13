package com.medhelp.callmed2.ui._main_page.fragment_doc_recognition.didalog

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class Assets {
    companion object {
        fun getTessDataPath(context: Context): String {
            // We need to return folder that contains the "tessdata" folder,
            // which is in this sample directly the app's files dir
            return context.getFilesDir().getAbsolutePath()
        }

        fun extractAssets(context: Context) {
            val am: AssetManager = context.getAssets()

            val tessDir = File(getTessDataPath(context), "tessdata")
            if (!tessDir.exists()) {
                tessDir.mkdir()
            }
            val rusFile = File(tessDir, "rus.traineddata")
            if (!rusFile.exists()) {
                copyFile(am, "rus.traineddata", rusFile)
            }
        }

        private fun copyFile(am: AssetManager, assetName: String, outFile: File) {
            try {
                am.open(assetName).use { `in` ->
                    FileOutputStream(outFile).use { out ->
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
    }
}