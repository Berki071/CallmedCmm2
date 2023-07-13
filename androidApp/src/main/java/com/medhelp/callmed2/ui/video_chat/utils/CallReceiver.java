package com.medhelp.callmed2.ui.video_chat.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    CallReceiverListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {

            Log.wtf("logRecordingService","CallReceiver CALL_STATE_OFFHOOK");
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        }

        if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
            Log.wtf("logRecordingService","CallReceiver CALL_STATE_IDLE");
        }

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Log.wtf("logRecordingService","CallReceiver EXTRA_STATE_RINGING");
                //Трубка не поднята, телефон звонит
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //Log.i("mlogTel","EXTRA_STATE_RINGING ");
                listener.beginCallPhone();

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
               // Log.i("mlogTel","EXTRA_STATE_OFFHOOK");
                Log.wtf("logRecordingService","CallReceiver EXTRA_STATE_OFFHOOK");
                listener.beginCallPhone();

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок".
               // Log.i("mlogTel","EXTRA_STATE_IDLE");
                Log.wtf("logRecordingService","CallReceiver EXTRA_STATE_IDLE");
                listener.endCallPhone();
            }
        }
    }

    public void setListener(CallReceiverListener listener)
    {
        this.listener=listener;
    }

    public interface CallReceiverListener{
        void beginCallPhone();
        void endCallPhone();
    }
}
