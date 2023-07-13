package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.recy

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.UserForRecordItem
import com.medhelp.callmed2.databinding.ItemSelectUserForRecordBinding
import com.medhelp.callmed2.utils.Different

class SelectUserForRecordHolder(val bindingItem: ItemSelectUserForRecordBinding, listener: SelectUserForRecordHolderListener) : RecyclerView.ViewHolder(bindingItem.root) {
    var context: Context
    var data: UserForRecordItem? = null
    var listener: SelectUserForRecordHolderListener

    init {
        context = itemView.context
        this.listener = listener
        itemView.setOnClickListener { listener.selectUser(data) }
    }

    fun onBind(data: UserForRecordItem) {
        this.data = data
        if (data.patronymic != null && data.patronymic != "" && data.patronymic != "null") bindingItem.fio!!.text =
            data.surname + " " + data.name + " " + data.patronymic else bindingItem.fio!!.text =
            data.surname + " " + data.name
        if (data.dr != null && data.dr != "" && data.dr != "null") {
            bindingItem.birthday!!.text = data.dr
            bindingItem.birthday!!.visibility = View.VISIBLE
        } else bindingItem.birthday!!.visibility = View.GONE
        if (data.phoneEncoded != null) {
            bindingItem.phone!!.text = Different.getFormattedPhone(data.phone)
            bindingItem.phone!!.visibility = View.VISIBLE
        } else bindingItem.phone!!.visibility = View.GONE
    }

    interface SelectUserForRecordHolderListener {
        fun selectUser(data: UserForRecordItem?)
    }
}