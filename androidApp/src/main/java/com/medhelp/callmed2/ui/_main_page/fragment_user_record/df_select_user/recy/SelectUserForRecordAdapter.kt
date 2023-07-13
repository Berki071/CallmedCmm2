package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.recy

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.UserForRecordItem
import com.medhelp.callmed2.databinding.ItemSelectUserForRecordBinding
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_user.recy.SelectUserForRecordHolder.SelectUserForRecordHolderListener
import java.util.*

class SelectUserForRecordAdapter(var context: Context, var mainList: MutableList<UserForRecordItem>, listener: SelectUserForRecordHolderListener) : RecyclerView.Adapter<SelectUserForRecordHolder>() {
    var list: MutableList<UserForRecordItem> = mutableListOf()
    var listener: SelectUserForRecordHolderListener
    var filter = ""
    set(value) {
        if (field == value.lowercase(Locale.getDefault())) return
        field = value.lowercase(Locale.getDefault())
        processingList()
    }

    init {
        mainList = mainList
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectUserForRecordHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_select_user_for_record, parent, false)
        val bindingItem = ItemSelectUserForRecordBinding.bind(view)
        return SelectUserForRecordHolder(bindingItem, listener)
    }

    override fun onBindViewHolder(holder: SelectUserForRecordHolder, position: Int) {
        holder.onBind(mainList[position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    fun setNewList(list: MutableList<UserForRecordItem>) {
        mainList = list
        processingList()
        // notifyDataSetChanged();
    }


    private fun processingList() {
        mainList = ArrayList()
        if (filter == "") {
            mainList = mainList
            notifyDataSetChanged()
            return
        }
        val isNumber = TextUtils.isDigitsOnly(filter)
        for (tmp in mainList) {
            if (isNumber) {
                if (tmp.phone.contains(filter)) mainList.add(tmp)
            } else {
                if (tmp.name.lowercase(Locale.getDefault()).contains(filter)) mainList.add(tmp)
            }
        }
        notifyDataSetChanged()
    }
}