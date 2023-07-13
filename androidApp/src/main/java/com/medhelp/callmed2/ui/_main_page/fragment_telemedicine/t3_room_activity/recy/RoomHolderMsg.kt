package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.recy

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import com.medhelp.callmed2.databinding.ItemChatMsgBinding
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate

class RoomHolderMsg(val bindingItem: ItemChatMsgBinding, val recyListener: RoomAdapter.RecyListener) : RecyclerView.ViewHolder(bindingItem.root) {
    private var message: MessageRoomItem? = null

    init {
        bindingItem.root.setOnLongClickListener {
            recyListener.clickLongClick(message)
        }
    }

    fun onBinView(msg: MessageRoomItem) {
        message = msg
        setData()
        tuningView()
    }

    private fun setData() {
        bindingItem.msg.text = (message?.text)
        if (message!!.data != null && !message!!.data!!.isEmpty() && message!!.idMessage != null) {

            val timeStr = MDate.stringToString(message!!.data!!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm)
            bindingItem.time.text = timeStr

        } else bindingItem.time.text = "..."
    }

    private fun tuningView() {
        if (message!!.otpravitel == "kl") {
            bindingItem.constraint.setBackgroundResource(R.drawable.msg_left2)
            val lp: ConstraintLayout.LayoutParams = bindingItem.constraint!!.getLayoutParams() as ConstraintLayout.LayoutParams
            lp.horizontalBias = 0F
            bindingItem.constraint!!.setLayoutParams(lp)

            val rootLp = bindingItem.rootBox.getLayoutParams() as RecyclerView.LayoutParams
            rootLp.setMargins(0, 0, Different.convertDpToPixel((60).toFloat(), itemView.context).toInt(), 0)
            bindingItem.rootBox.setLayoutParams(rootLp)

        } else {
            bindingItem.constraint.setBackgroundResource(R.drawable.msg_right2)
            val lp: ConstraintLayout.LayoutParams = bindingItem.constraint.getLayoutParams() as ConstraintLayout.LayoutParams
            lp.horizontalBias = 1F
            bindingItem.constraint.setLayoutParams(lp)

            val rootLp = bindingItem.rootBox.getLayoutParams() as RecyclerView.LayoutParams
            rootLp.setMargins(Different.convertDpToPixel((60).toFloat(), itemView.context).toInt(), 0, 0, 0)
            bindingItem.rootBox.setLayoutParams(rootLp)
        }
    }
}