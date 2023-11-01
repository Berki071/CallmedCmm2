package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.recy

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import com.medhelp.callmed2.databinding.ItemChatFileBinding
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.main.MainUtils
import java.io.InputStream


class T3RoomHolderFile(val itemBinding: ItemChatFileBinding, val recyListener: RoomAdapter.RecyListener) : RecyclerView.ViewHolder(itemBinding.root){
    private var message: MessageRoomItem? = null

    init {
        itemBinding.root.setOnLongClickListener {
            recyListener.clickLongClick(message)
        }

        itemBinding.root.setOnClickListener {
            message?.let{
                recyListener.clickedShowFile(it)
            }
        }
    }


    fun onBinView(msg: MessageRoomItem) {
        message = msg
        setData()
        tuningView()
    }

    private fun setData() {
        message?.let{
            val uriFile = Uri.parse(it.text)

            var isExistFile = false    //проверка которая работает
            if (null != uriFile) {
                try {
                    val inputStream: InputStream? = itemBinding.root.context.getContentResolver().openInputStream(uriFile)
                    isExistFile = inputStream != null
                    inputStream?.close()
                } catch (e: Exception) { }
            }

            if(!isExistFile){
                itemBinding.nameFile.text = ""
                itemBinding.nameFile.visibility = View.GONE
                return
            }

            itemBinding.nameFile.visibility = View.VISIBLE
            itemBinding.nameFile.text = uriFile.getOriginalFileName(itemBinding.root.context) ?: ""

            if (it.data != null && it.idMessage != null) {
                val timeStr = MDate.stringToString(message!!.data!!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm)
                itemBinding.time.text = timeStr

            } else
                itemBinding.time.text = "..."
        }
    }

    fun showLoading(){
        itemBinding.loadingView.visibility = View.VISIBLE
    }
    fun hideLoading(){
        itemBinding.loadingView.visibility = View.GONE
    }

    private fun tuningView() {
        if (message!!.otpravitel == "kl") {
            itemBinding.constraint.setBackgroundResource(R.drawable.msg_left2)
            val lp: ConstraintLayout.LayoutParams = itemBinding.constraint.getLayoutParams() as ConstraintLayout.LayoutParams
            lp.horizontalBias = 0F
            itemBinding.constraint.setLayoutParams(lp)

            val rootLp = itemBinding.rootBox.getLayoutParams() as RecyclerView.LayoutParams
            rootLp.setMargins(0, 0, MainUtils.dpToPx(itemBinding.root.context, 60), 0)
            itemBinding.rootBox.setLayoutParams(rootLp)
        } else {
            itemBinding.constraint.setBackgroundResource(R.drawable.msg_right2)
            val lp: ConstraintLayout.LayoutParams = itemBinding.constraint.getLayoutParams() as ConstraintLayout.LayoutParams
            lp.horizontalBias = 1F
            itemBinding.constraint.setLayoutParams(lp)

            val rootLp = itemBinding.rootBox.getLayoutParams() as RecyclerView.LayoutParams
            rootLp.setMargins(MainUtils.dpToPx(itemBinding.root.context, 60), 0, 0, 0)
            itemBinding.rootBox.setLayoutParams(rootLp)
        }
    }

    fun Uri.getOriginalFileName(context: Context): String? {
        return context.contentResolver.query(this, null, null, null, null)?.use {
            val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameColumnIndex)
        }
    }
}