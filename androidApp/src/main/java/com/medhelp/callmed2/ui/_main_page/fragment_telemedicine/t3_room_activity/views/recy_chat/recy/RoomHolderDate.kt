package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat.recy

import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.databinding.ItemChatMsgDateBinding
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem

class RoomHolderDate(val bindingItem: ItemChatMsgDateBinding) : RecyclerView.ViewHolder(bindingItem.root) {
    fun onBinView(msg: MessageRoomItem) {
        val textD = MDate.stringToString(msg.data!!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss,MDate.DATE_FORMAT_ddMMyyyy)
        bindingItem.date.text = textD
    }
}