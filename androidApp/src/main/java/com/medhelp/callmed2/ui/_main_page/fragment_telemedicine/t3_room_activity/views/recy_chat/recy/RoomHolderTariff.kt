package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat.recy

import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.databinding.ItemChatMsgTariffBinding
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem

class RoomHolderTariff(val bindingItem: ItemChatMsgTariffBinding) : RecyclerView.ViewHolder(bindingItem.root)  {
    fun onBinView(msg: MessageRoomItem) {
        bindingItem.date.text = msg.nameTm
    }
}