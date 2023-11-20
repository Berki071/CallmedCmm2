package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.bottom_bar_chat

import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem

class BottomBarChatPresenter {

    fun createAudioMessage(recordItem: AllRecordsTelemedicineItem?, pathToFileRecord: String) : Triple<String,MessageRoomItem,String>?{
        recordItem?.let {
            var msgItem = MessageRoomItem()
            msgItem.idRoom = it.idRoom!!.toString()
            msgItem.data = MDate.longToString(MDate.getCurrentDate(), MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
            msgItem.type = T3RoomActivity.MsgRoomType.REC_AUD.toString()
            msgItem.text = pathToFileRecord
            msgItem.otpravitel = "sotr"
            msgItem.idTm = it.tmId!!
            msgItem.nameTm = it.tmName
            msgItem.viewKl = "false"
            msgItem.viewSotr = "true"

            return Triple(it.idKl!!.toString(), msgItem, it.idFilial!!.toString())
        }

        return null
    }

    fun createTextMessage(recordItem: AllRecordsTelemedicineItem?, msg: String) : Triple<String,MessageRoomItem,String>?{
        recordItem?.let {
            var msgItem = MessageRoomItem()
            msgItem.idRoom = it.idRoom!!.toString()
            msgItem.data = MDate.longToString(MDate.getCurrentDate(),MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
            msgItem.type = T3RoomActivity.MsgRoomType.TEXT.toString()
            msgItem.text = msg
            msgItem.otpravitel = "sotr"
            msgItem.idTm = it.tmId!!
            msgItem.nameTm = it.tmName
            msgItem.viewKl = "false"
            msgItem.viewSotr = "true"

            return Triple(it.idKl!!.toString(), msgItem, it.idFilial!!.toString())
        }

        return null
    }
}