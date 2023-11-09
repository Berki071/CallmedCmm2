package com.medhelp.callmed2.ui._main_page.fragment_chat_with_doctor.recycler.archive

import android.content.Context
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.databinding.ItemChatWithDoctorBinding
import com.medhelp.callmed2.utils.main.MainUtils
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem

class T1ListOfEntriesArchiveHolder(private val context: Context, val bindingItem: ItemChatWithDoctorBinding, val listener: T1ListOfEntriesArchiveHolderListener) :
    RecyclerView.ViewHolder(bindingItem.root) {

    private var item: AllRecordsTelemedicineItem? = null

    init {
        bindingItem.root.setOnClickListener { click: View? ->
            item?.let{ listener.enterTheRoom(it)}
        }
    }

    fun onBindView(item: AllRecordsTelemedicineItem) {
        this.item = item

        bindingItem.title.text = item.fullNameKl
        bindingItem.tariffName.visibility = View.GONE
        bindingItem.redDot.visibility = View.GONE
        bindingItem.timeLeft.visibility = View.GONE
        bindingItem.newIcoBox.visibility = View.GONE
        bindingItem.statusPaid.visibility = View.GONE

        bindingItem.tariffSmallInfo.text = Html.fromHtml("<b>Специальность: </b>" + item.specialty)

        var idSpace = item.fullNameKl!!.indexOf(" ")
        idSpace = if(idSpace>0) idSpace else 1
        val tmpStr = item.fullNameKl!!.substring(0,1)+item.fullNameKl!!.substring(idSpace+1,idSpace+2)
        bindingItem.ico.setImageBitmap(MainUtils.creteImageIco(context, tmpStr,item!!.status!!))
    }


    interface T1ListOfEntriesArchiveHolderListener{
        fun enterTheRoom(inf: AllRecordsTelemedicineItem)
    }
}