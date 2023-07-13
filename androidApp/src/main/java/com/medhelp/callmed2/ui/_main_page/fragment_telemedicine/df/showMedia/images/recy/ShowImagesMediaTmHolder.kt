package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.images.recy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.databinding.ItemShowImageMediaBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.DataForRecyFiles

class ShowImagesMediaTmHolder(val bindingItem: ItemShowImageMediaBinding, val listener: ShowImagesMediaTmListener) : RecyclerView.ViewHolder(bindingItem.root)  {
    var data: DataForRecyFiles? = null

    init {
        bindingItem.root.setOnClickListener{
            data?.let{
                listener.onClickShow(it)
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
        bindingItem.title.text = data.file?.name
        bindingItem.date.text = data.dateStr

        showImage(bindingItem.image, data.file!!.absolutePath)

    }

    private fun showImage(img: ImageView, absolutePath: String) {
        val options = BitmapFactory.Options()
        options.inSampleSize = 8

        try {
            val bitmapPicture: Bitmap = BitmapFactory.decodeFile(absolutePath)
            img.setImageBitmap(bitmapPicture)
        }catch (e: Exception){}
    }



    interface ShowImagesMediaTmListener{
        fun onClickShow(file: DataForRecyFiles)
        fun delete(file: DataForRecyFiles)
    }
}