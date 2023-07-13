package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop

import android.os.Build
import androidx.annotation.RequiresApi
import com.medhelp.callmed2.utils.main.MDate
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class DataForRecyFiles {
    var dateL : Long? = null
    var dateStr: String? = null
    var file: File? = null

    var type: DataForRecyFilesType? = null

    constructor(file: File?) {
        this.file = file
        type= DataForRecyFilesType.FILE
        initDate()
    }

    constructor(dateStr: String?) {
        this.dateStr = dateStr
        type= DataForRecyFilesType.DATE
        dateL = 0L
    }


    fun initDate(){
        file?.let{
            val timeL = getLastModifiedTimeInMillis(it)
            if (timeL == null){
                dateL = 0L
                dateStr = ""
            }else{
                this.dateL= timeL
                dateStr = MDate.longToString(timeL, MDate.DATE_FORMAT_ddMMyyyy)
            }
        }
    }

    fun getLastModifiedTimeInMillis(file: File): Long? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getLastModifiedTimeFromBasicFileAttrs(file)
            } else {
                file.lastModified()
            }
        } catch (x: Exception) {
            x.printStackTrace()
            null
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLastModifiedTimeFromBasicFileAttrs(file: File): Long {
        val basicFileAttributes = Files.readAttributes(
            file.toPath(),
            BasicFileAttributes::class.java
        )
        return basicFileAttributes.creationTime().toMillis()
    }

    enum class DataForRecyFilesType{
        FILE, DATE
    }
}