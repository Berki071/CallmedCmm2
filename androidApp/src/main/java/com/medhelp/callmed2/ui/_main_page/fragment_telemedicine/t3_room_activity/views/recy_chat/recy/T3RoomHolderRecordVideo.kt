package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat.recy

import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.MediaController
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ItemChatRecordVideoBinding
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import java.io.InputStream


class T3RoomHolderRecordVideo(val bindingItem: ItemChatRecordVideoBinding, val recyListener: RoomAdapter.RecyListener) : RecyclerView.ViewHolder(bindingItem.root)  {
    private var message: MessageRoomItem? = null
    private var uriFile: Uri? = null

    init {
        bindingItem.videoView.visibility = View.GONE
        bindingItem.img.visibility = View.VISIBLE
        bindingItem.timeRecord.visibility = View.GONE

        bindingItem.root.setOnLongClickListener {
            if(message != null) {
                recyListener.clickLongClick(message!!)
            }else
                false
        }

        bindingItem.root.setOnClickListener {
            if(bindingItem.img.visibility == View.VISIBLE){
                bindingItem.videoView.visibility = View.VISIBLE
                bindingItem.img.visibility = View.GONE
                startShowVideo()
            }else{
                bindingItem.videoView.visibility = View.GONE
                bindingItem.img.visibility = View.VISIBLE
                stopShowVideo()
            }
        }
    }

    fun onBinView(message: MessageRoomItem) {
        this.uriFile = null

        this.message = message
        setTime()

        if(message.isShowLoading)
            showLoading()
        else
            hideLoading()


        if(message.text != null && message.text!!.isNotEmpty()){
            if(message.text!!.contains("http")){
                showLoading()
                recyListener.loadFile(message)
            }else{
                processingIncomingItem()
            }
        }

        tuningView()
    }

    private fun processingIncomingItem() {
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

        if(isExistFile) {
            this.uriFile = uriFile

            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(itemView.context, uriFile)
            try {
                val bitmap = mediaMetadataRetriever.getFrameAtTime(1)
                bindingItem.img.setImageBitmap(bitmap)
            } catch (outOfMemoryError: OutOfMemoryError) {}
        }else
            bindingItem.img.setImageResource(R.drawable.baseline_videocam_gray_24)
    }

    fun startShowVideo(){
        uriFile?.let {
            processingStartSizeWindowForShowVideo()

            bindingItem.videoView.setVideoURI(it)
            bindingItem.videoView.start()

            startShowTimePlayback()

            bindingItem.videoView.setOnCompletionListener {
                bindingItem.root.performClick()
            }
        }
    }

    fun stopShowVideo(){
        finishShowTimePlayback()
        bindingItem.videoView.stopPlayback()
        bindingItem.videoView.setVideoURI(null)
    }

    var showTimer = false
    fun startShowTimePlayback(){
        bindingItem.timeRecord.setText("00:00")
        bindingItem.timeRecord.visibility = View.VISIBLE

        showTimer = true
        repeatPartShowTimePlayback()
    }
    fun repeatPartShowTimePlayback(){
        if(!showTimer)
            return

        val durationAllSec = (bindingItem.videoView.duration / 1000)
        val currentTime = bindingItem.videoView.currentPosition / 1000
        val timeLost = durationAllSec - currentTime
        val timeLostStr = if(timeLost<10) "00:0${timeLost.toString()}" else "00:${timeLost.toString()}"
        bindingItem.timeRecord.text = timeLostStr

        val handler = Handler()
        val delay = 250 //milliseconds
        handler.postDelayed({
            repeatPartShowTimePlayback()
        }, delay.toLong())
    }
    fun finishShowTimePlayback(){
        showTimer = false
        bindingItem.timeRecord.visibility = View.GONE
    }


    fun processingStartSizeWindowForShowVideo(){
        val containerWidthAndHeight: Int = bindingItem.cardContainer.width

        val imgFrameVideo = bindingItem.img.drawable.toBitmap()
        val videoWidth = imgFrameVideo.width
        val videoHeight = imgFrameVideo.height

        //не подходит на мин качестверазмер кадра меньше чем базовый
        //если кадр меньше базового размера то установить для картинки базовый размер
//        if(videoWidth<=containerWidthAndHeight && videoHeight<=containerWidthAndHeight){
//            val lp = bindingItem.img.layoutParams
//            lp.width = containerWidthAndHeight
//            lp.height = containerWidthAndHeight
//            bindingItem.img.layoutParams = lp
//            return
//        }

        //если высота кадра больше базового размера то установить такую высоту вью изображения (и ширину)
        // что бы ширана вью изображения была равна базовой ширине при сохранении пропорции изображения
        //базовые размеры беруться y cardContainer

        if (videoHeight > videoWidth) {
            val newHeight = (containerWidthAndHeight * videoHeight) / videoWidth

            val lp = bindingItem.videoView.layoutParams
            lp.width = containerWidthAndHeight
            lp.height = newHeight
            bindingItem.videoView.layoutParams = lp
            return
        }

        if(videoWidth > videoHeight){
            val newWidth = (containerWidthAndHeight * videoWidth) / videoHeight

            val lp = bindingItem.videoView.layoutParams
            lp.width = newWidth
            lp.height = containerWidthAndHeight
            bindingItem.videoView.layoutParams = lp
            return
        }
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
            val lp: ConstraintLayout.LayoutParams = bindingItem.cardForConstraint.getLayoutParams() as ConstraintLayout.LayoutParams
            lp.horizontalBias = 0F
            bindingItem.cardForConstraint.setLayoutParams(lp)

            val rootLp = bindingItem.rootBox.getLayoutParams() as RecyclerView.LayoutParams
            rootLp.setMargins(0, 0, Different.convertDpToPixel((60).toFloat(), itemView.context).toInt(), 0)
            bindingItem.rootBox.setLayoutParams(rootLp)

        } else {
            bindingItem.constraint.setBackgroundResource(R.drawable.msg_right2)
            val lp: ConstraintLayout.LayoutParams = bindingItem.cardForConstraint.getLayoutParams() as ConstraintLayout.LayoutParams
            lp.horizontalBias = 1F
            bindingItem.cardForConstraint.setLayoutParams(lp)

            val rootLp = bindingItem.rootBox.getLayoutParams() as RecyclerView.LayoutParams
            rootLp.setMargins(Different.convertDpToPixel((60).toFloat(), itemView.context).toInt(), 0, 0, 0)
            bindingItem.rootBox.setLayoutParams(rootLp)
        }
    }
}