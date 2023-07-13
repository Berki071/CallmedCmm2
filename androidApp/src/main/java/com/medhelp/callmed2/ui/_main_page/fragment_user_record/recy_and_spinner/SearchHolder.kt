package com.medhelp.callmed2.ui._main_page.fragment_user_record.recy_and_spinner

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.ServiceItem
import com.medhelp.callmed2.databinding.ItemSearchBinding

class SearchHolder(val bindingItem: ItemSearchBinding, var listener: SearchHolderListener) : RecyclerView.ViewHolder(bindingItem.root) {

    var context: Context
    var repo: ServiceItem? = null

    init {
        context = itemView.context
        itemView.setOnClickListener { view: View? -> listener.clickRecord(repo) }
    }

    fun onBind(repo: ServiceItem) {
        this.repo = repo
        bindingItem.tvSearchItemName.text = repo.title
        bindingItem.tvSearchItemData.text = repo.value
        if (repo.hint != null && repo.hint != "null" && !repo.hint.isEmpty()) {
            bindingItem.hint!!.visibility = View.VISIBLE
            bindingItem.hint!!.text = repo.hint
        } else bindingItem.hint!!.visibility = View.GONE
    }

    interface SearchHolderListener {
        fun clickRecord(repo: ServiceItem?)
    }
}