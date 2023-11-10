package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat.recy

import android.net.Uri
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ItemChatImgBinding
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import java.io.InputStream

class RoomHolderImg(val bindingItem: ItemChatImgBinding, val recyListener: RoomAdapter.RecyListener) : RecyclerView.ViewHolder(bindingItem.root) {
    private var message: MessageRoomItem? = null

    init {
        bindingItem.img.setOnLongClickListener {
            if(message != null) {
                recyListener.clickLongClick(message!!)
                 true
            }else
                 false
        }

        bindingItem.img.setOnClickListener{
            message?.let{
                if(it.text != "null" && it.text != null && it.text!!.isNotEmpty())
                    recyListener.clickedShowBigImage(it)
            }
        }
    }

    fun onBinView(message: MessageRoomItem) {
        this.message = message
        setTime()

        if(message.isShowLoading)
            showLoading()
        else
            hideLoading()

        if(message.text != null && !message.text!!.isEmpty()){
            setImage()
        }else{
            showLoading()
            recyListener.loadFile(message)
        }

        tuningView()
    }


    private fun setImage() {
        if (message == null || message!!.text == null || message!!.text.equals("")) return

        val uriFile = Uri.parse(message!!.text)
        var isExistFile = false    //проверка которая работает
        if (null != uriFile) {
            try {
                val inputStream: InputStream? = itemView.context.getContentResolver().openInputStream(uriFile)
                isExistFile = inputStream != null
                inputStream?.close()
            } catch (e: Exception) { }
        }

        if(isExistFile)
            bindingItem.img.setImageURI(uriFile)
        else
            bindingItem.img.setImageResource(R.drawable.ic_photo_camera_grey_200_36dp)
    }

    private fun setTime() {
        if (message!!.data != null && message!!.idMessage != null)  {
            val timeStr = MDate.stringToString(message!!.data!!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm)
            bindingItem.time.text = timeStr
        } else bindingItem.time.text = "..."
    }

    fun showLoading(){
        bindingItem.loadingView.visibility = View.VISIBLE
    }
    fun hideLoading(){
        bindingItem.loadingView.visibility = View.GONE
    }

    private fun tuningView() {
        if (message!!.otpravitel == "kl") {
            bindingItem.constraint.setBackgroundResource(R.drawable.msg_left2)
            val lp: ConstraintLayout.LayoutParams = bindingItem.constraint.getLayoutParams() as ConstraintLayout.LayoutParams
            lp.horizontalBias = 0F
            bindingItem.constraint.setLayoutParams(lp)

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