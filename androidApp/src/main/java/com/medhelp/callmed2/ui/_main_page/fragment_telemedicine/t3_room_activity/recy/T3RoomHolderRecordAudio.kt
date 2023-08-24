package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.recy

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ItemChatRecordAudioBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.main.MainUtils
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import java.io.InputStream

class T3RoomHolderRecordAudio(val itemBinding: ItemChatRecordAudioBinding, val recyListener: RoomAdapter.RecyListener) : RecyclerView.ViewHolder(itemBinding.root)  {

    private var message: MessageRoomItem? = null
    var mPlayer: MediaPlayer? = null
    var mAudioManager: AudioManager? = null

    init {
        itemBinding.btnControl.tag = BtnControlStay.STOP.name  //current stay

        itemBinding.root.setOnLongClickListener {
            recyListener.clickLongClick(message)
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
                itemBinding.duration.text = ""
                itemBinding.btnControl.setOnClickListener(null)
                return
            }

            itemBinding.btnControl.setOnClickListener{
                if(itemBinding.btnControl.tag == BtnControlStay.STOP.name){
                    itemBinding.btnControl.tag = BtnControlStay.PLAY.name
                    itemBinding.btnControl.setImageResource(R.drawable.baseline_stop_dark_gray_24)
                    clickPlay(uriFile)
                }else{
                    itemBinding.btnControl.tag = BtnControlStay.STOP.name
                    itemBinding.btnControl.setImageResource(R.drawable.baseline_play_arrow_dark_gray_24)
                    clickStop()
                }
            }

            val durationMillSec = getDuration(uriFile)
            itemBinding.progressBar.max = durationMillSec.toInt()
            itemBinding.duration.text = durationMillSecToStringFormatted(durationMillSec.toInt())

            if (it.data != null && it.idMessage != null) {
                val timeStr = MDate.getNewFormatString(message!!.data!!, MDate.DATE_FORMAT_yyyyMMdd_HHmmss, MDate.DATE_FORMAT_HHmm)
                itemBinding.time.text = timeStr

            } else
                itemBinding.time.text = "..."
        }
    }

    var isStartCheckPlayback = false
    fun clickPlay(uri: Uri){
        mAudioManager = itemView.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max: Int = mAudioManager?.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION) ?: 0
        mAudioManager?.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, max, 0)

        val stateProximity = recyListener.getStateProximity()
        if(stateProximity == T3RoomActivity.ProximitySensorState.NEAR){
            mAudioManager?.mode=AudioManager.MODE_IN_COMMUNICATION
            //Log.wtf("adfasdasdf", "NEAR volumeLevel $volumeLevel max $max")
        }else{
            mAudioManager?.mode=AudioManager.MODE_NORMAL
            //Log.wtf("adfasdasdf", "AWAY volumeLevel $volumeLevel max $max")
        }
        recyListener.addListenerProximityState(object : RoomAdapter.ListenerStateProximity {
            override fun changeState(state: T3RoomActivity.ProximitySensorState?) {
                if(state == null){
                    clickStop()
                }else{
                    if(state == T3RoomActivity.ProximitySensorState.NEAR){
                        //Log.wtf("adfasdasdf", "NEAR")
                        mAudioManager?.mode=AudioManager.MODE_IN_COMMUNICATION
                        //Log.wtf("adfasdasdf", "NEAR volumeLevel $volumeLevel max $max")
                    }else if(state == T3RoomActivity.ProximitySensorState.AWAY){
                        //Log.wtf("adfasdasdf", "AWAY")
                        mAudioManager?.mode=AudioManager.MODE_NORMAL
                        //Log.wtf("adfasdasdf", "AWAY volumeLevel $volumeLevel max $max")
                    }
                }
            }
        })

        mPlayer = MediaPlayer.create(itemView.context, uri)
        mPlayer?.setOnCompletionListener {
            clickStop()
        }

        mPlayer?.setAudioAttributes(AudioAttributes.Builder()
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .build())

        mPlayer?.start()

        isStartCheckPlayback=true
        startRepeatCheckPlayback()
    }
    fun clickStop(){
        recyListener.addListenerProximityState(null)

        itemBinding.btnControl.tag = BtnControlStay.STOP.name
        itemBinding.btnControl.setImageResource(R.drawable.baseline_play_arrow_dark_gray_24)
        mPlayer?.stop()

        mPlayer?.let {
            itemBinding.duration.text = durationMillSecToStringFormatted(it.duration)
        }

        stopRepeatCheckPlayback()
    }

    fun startRepeatCheckPlayback(){
        if(!isStartCheckPlayback || mPlayer == null)
            return

        val currentPosInMillSec = mPlayer!!.currentPosition
        itemBinding.duration.text = durationMillSecToStringFormatted(currentPosInMillSec)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemBinding.progressBar.setProgress(currentPosInMillSec,true)
        }else{
            itemBinding.progressBar.setProgress(currentPosInMillSec)
        }

        val handler = Handler()
        val delay = 100 //milliseconds
        handler.postDelayed({
            startRepeatCheckPlayback()
        }, delay.toLong())
    }
    fun stopRepeatCheckPlayback(){
        isStartCheckPlayback = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemBinding.progressBar.setProgress(0,true)
        }else{
            itemBinding.progressBar.setProgress(0)
        }
    }


    fun getDuration(uri: Uri): Long{ //millsec
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(itemView.context, uri)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }catch (e: Exception){}

        return 0L
    }
    private fun durationMillSecToStringFormatted(millSec: Int): String {
        val durationSecAll = (millSec/1000).toInt()

        val min = (durationSecAll / 60).toString()
        val secInt = durationSecAll % 60
        val sec = if(secInt<10) "0$secInt" else secInt

        return "$min:$sec"
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

    enum class BtnControlStay{
        PLAY,STOP
    }
}