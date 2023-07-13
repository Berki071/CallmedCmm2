package com.medhelp.callmed2.ui._main_page.fragment_call.receiver_call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import java.lang.reflect.Method;

public class CallReceiver extends BroadcastReceiver {
    TelecomManager telecomManager;
    ITelephony telephonyService;
    CallReceiverListener callReceiverListener;

    Context context;

    public void setCallReceiverListener(CallReceiverListener callReceiverListener) {
        this.callReceiverListener = callReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                //телефон звонит, получаем входящий номер
               // String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                Log.wtf("LOGincomingNumber", number);

                if (callReceiverListener != null) {
                    callReceiverListener.ringing(number);
                }
            }

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
                //телефон находиться в ждущем режиме. Это событие наступает по окончанию разговора, когда мы уже знаем номер и факт звонка
                if (callReceiverListener != null) {
                    callReceiverListener.idle();
                }
            }

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //телефон находится в режиме звонка (набор номера / разговор)
                if (callReceiverListener != null) {
                    callReceiverListener.offHook(number);
                }


                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Method m = tm.getClass().getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                telephonyService = (ITelephony) m.invoke(tm);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void endCall()
    {
        if(telephonyService!=null)
        {
            if(callReceiverListener!=null)
            {
                callReceiverListener.endCall();
            }

            telephonyService.endCall();
        }
    }
}
