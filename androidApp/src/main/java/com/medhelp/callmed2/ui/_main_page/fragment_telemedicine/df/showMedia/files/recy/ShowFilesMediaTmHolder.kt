package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.files.recy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.databinding.ItemShowFilesMediaBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.DataForRecyFiles
import com.medhelp.callmed2.utils.main.MDate
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class ShowFilesMediaTmHolder(val bindingItem: ItemShowFilesMediaBinding, val listener: ShowFilesMediaTmHolderListener) : RecyclerView.ViewHolder(bindingItem.root) {
    var data: DataForRecyFiles? = null

    init {
        bindingItem.root.setOnClickListener{
            data?.let{
                listener.onClick(it)
            }
        }

        bindingItem.root.setOnLongClickListener {
            data?.let{
                listener.delete(it)
            }
            true
        }
    }

    fun onBind(data: DataForRecyFiles){
        this.data=data
        bindingItem.title.text = data.file!!.name
        val dateLong = getLastModifiedTimeInMillis(data.file!!)
        bindingItem.date.text = if(dateLong == null) "" else MDate.longToString(dateLong, MDate.DATE_FORMAT_ddMMyy)
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

    interface ShowFilesMediaTmHolderListener{
        fun onClick(file: DataForRecyFiles)
        fun delete(file: DataForRecyFiles)
    }
}