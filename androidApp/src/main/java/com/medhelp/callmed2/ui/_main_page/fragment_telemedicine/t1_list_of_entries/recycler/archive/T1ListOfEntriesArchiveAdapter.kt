package com.medhelp.callmed2.ui._main_page.fragment_chat_with_doctor.recycler.archive

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ItemChatWithDoctorBinding
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem

class T1ListOfEntriesArchiveAdapter(private val context: Context, private var list: List<AllRecordsTelemedicineItem>, val listener: T1ListOfEntriesArchiveHolder.T1ListOfEntriesArchiveHolderListener) : RecyclerView.Adapter<T1ListOfEntriesArchiveHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T1ListOfEntriesArchiveHolder {
        val inf = LayoutInflater.from(context)
        val view = inf.inflate(R.layout.item_chat_with_doctor, parent, false)
        val bindingItem = ItemChatWithDoctorBinding.bind(view)
        return T1ListOfEntriesArchiveHolder(context, bindingItem, listener)
    }

    override fun onBindViewHolder(holder: T1ListOfEntriesArchiveHolder, position: Int) {
        holder.onBindView(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}