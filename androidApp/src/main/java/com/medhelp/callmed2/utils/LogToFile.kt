package com.medhelp.callmed2.utils

import android.os.Environment
import com.medhelp.callmed2.utils.main.TimesUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class LogToFile {
    private val name = "LogMedApp.txt"
    private val fWrite: File

    init {
        fWrite = file
    }

    fun wtite(message: String) {
        val newString = """${currentTime} $message
"""
        val stream: FileOutputStream
        try {
            stream = FileOutputStream(fWrite, true)
            stream.write(newString.toByteArray())
            stream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private val file: File
        private get() {
            val f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(f, name)
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return file
        }
    private val currentTime: String
        private get() = TimesUtils.longToString(
            System.currentTimeMillis(),
            TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy
        )
}