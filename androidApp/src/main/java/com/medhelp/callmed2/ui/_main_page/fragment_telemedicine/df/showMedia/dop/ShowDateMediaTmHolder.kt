package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop

import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.databinding.ItemShowDateMediaBinding

class ShowDateMediaTmHolder(val bindingItem: ItemShowDateMediaBinding) : RecyclerView.ViewHolder(bindingItem.root)  {
    var item: DataForRecyFiles? = null

    fun onBind(item: DataForRecyFiles){
        this.item = item

        bindingItem.title.text = item.dateStr
    }

}