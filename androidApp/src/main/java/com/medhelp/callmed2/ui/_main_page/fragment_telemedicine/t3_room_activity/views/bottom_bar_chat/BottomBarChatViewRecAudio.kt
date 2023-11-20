package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.bottom_bar_chat

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import java.io.File

class BottomBarChatViewRecAudio(val context: Context) {
    var audioRecorder: MediaRecorder? = null
    var pathToFileRecord: Pair<Uri, File>? = null

    fun startRecord(pathToFileRecord: Pair<Uri, File>){
        this.pathToFileRecord = pathToFileRecord

        audioRecorder = MediaRecorder().apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(pathToFileRecord!!.second.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecord(){
        audioRecorder?.let {
            Log.wtf("myLogdd", "stopRecord")
            it.stop()
            it.release()
        }
        audioRecorder = null

        val duration = getDuration(pathToFileRecord!!.first)
        if(duration < 1000){
            pathToFileRecord?.let{
                it.second.delete()
            }
            pathToFileRecord = null
            return
        }
    }

    fun cancelRecord(){
        audioRecorder?.let {
            Log.wtf("myLogdd", "cancelRecord")
            it.stop()
            it.release()
        }
        audioRecorder = null

        pathToFileRecord?.let{
            it.second.delete()
        }
        pathToFileRecord = null

        Toast.makeText(context,"Отменено", Toast.LENGTH_SHORT).show()
    }

    fun getDuration(uri: Uri): Long{ //millsec
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }catch (e: Exception){}

        return 0L
    }
}