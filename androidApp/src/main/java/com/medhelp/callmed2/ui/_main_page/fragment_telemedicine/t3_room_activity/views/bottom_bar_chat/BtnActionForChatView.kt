package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.bottom_bar_chat

import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import com.medhelp.callmed2.R
import it.sephiroth.android.library.xtooltip.Tooltip

class BtnActionForChatView: AppCompatImageButton {
    //region constructors
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }
    //endregion

    var isAllowStateAudioRecord = true  // флаг для разрешения аудио записи
    var isAllowStateVideoRecord = true

    var isShowHint = false
    
    var stateBtn: BtnActionForChatState = BtnActionForChatState.TEXT

    private fun init(context: Context) {
        checkExistStateVideo()
    }
    private fun checkExistStateVideo(){
        val packageManager = context.packageManager
        val isCamera = packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

        if(isCamera==null || !isCamera) {
            isAllowStateVideoRecord = false
        }

        if (isAllowStateAudioRecord){
            setState(BtnActionForChatState.AUDIO)
        }else{
            setState(BtnActionForChatState.TEXT)
        }
    }
    
    fun setState(state: BtnActionForChatState){
        if(state == BtnActionForChatState.AUDIO && !isAllowStateAudioRecord)
            return
        if(state == BtnActionForChatState.VIDEO && !isAllowStateVideoRecord)
            return
        
        stateBtn = state 
        
        when(state){
            BtnActionForChatState.TEXT -> setImageResource(R.drawable.ic_send_white_24dp)
            BtnActionForChatState.AUDIO -> setImageResource(R.drawable.baseline_mic_24_white)
            BtnActionForChatState.VIDEO -> setImageResource(R.drawable.baseline_videocam_white_24)
        }
    }

    fun clickChangeToNextState(){
        if(isForRecordState()){
            if(stateBtn == BtnActionForChatState.AUDIO){
                setState(BtnActionForChatState.VIDEO)

                if(isShowHint)
                    TooltipRecordBtn().showTooltip(context,context.getString(R.string.hintBtnRecVideo), this)

            }else{
                setState(BtnActionForChatState.AUDIO)

                if(isShowHint)
                    TooltipRecordBtn().showTooltip(context,context.getString(R.string.hintBtnRecAudio), this)
            }
        }
    }



    fun isForRecordState() : Boolean{
        return stateBtn == BtnActionForChatState.AUDIO || stateBtn == BtnActionForChatState.VIDEO
    }
    
    enum class BtnActionForChatState{
        TEXT, AUDIO, VIDEO
    } 
}