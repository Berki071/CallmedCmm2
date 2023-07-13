package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.images.recy

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ItemShowDateMediaBinding
import com.medhelp.callmed2.databinding.ItemShowImageMediaBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.DataForRecyFiles
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.ShowDateMediaTmHolder

class ShowImagesMediaTmAdapter(val context: Context, val list: MutableList<DataForRecyFiles>, val listener: ShowImagesMediaTmHolder.ShowImagesMediaTmListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_show_date_media, parent, false)
                val bindingItem = ItemShowDateMediaBinding.bind(view)
                return ShowDateMediaTmHolder(bindingItem)
            }

            1 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_show_image_media, parent, false)
                val bindingItem = ItemShowImageMediaBinding.bind(view)
                return ShowImagesMediaTmHolder(bindingItem, listener)
            }

            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_show_date_media, parent, false)
                val bindingItem = ItemShowDateMediaBinding.bind(view)
                return ShowDateMediaTmHolder(bindingItem)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(list[position].type){
            DataForRecyFiles.DataForRecyFilesType.DATE -> {
                (holder as ShowDateMediaTmHolder).onBind(list[position])
            }
            DataForRecyFiles.DataForRecyFilesType.FILE-> {
                (holder as ShowImagesMediaTmHolder).onBind(list[position])
            }
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].type) {
            DataForRecyFiles.DataForRecyFilesType.DATE -> 0
            DataForRecyFiles.DataForRecyFilesType.FILE-> 1
            else -> -1
        }
    }
}