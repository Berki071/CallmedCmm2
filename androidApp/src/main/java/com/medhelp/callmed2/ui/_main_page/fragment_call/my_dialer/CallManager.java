package com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;

import com.medhelp.callmed2.data.Constants;
import com.medhelp.callmed2.ui._main_page.MainPageActivity;
import com.medhelp.callmed2.ui._main_page.fragment_call.CallFragment;

@TargetApi(Build.VERSION_CODES.M)
public class CallManager {
    private static CallManager instance=new CallManager();
    private CallManager (){}
    public static CallManager getInstance()
    {
        return instance;
    }

    private final String LOG_TAG = "CallManager";
    CallManagerListener listener;

    private Call currentCall = null;

    private Call.Callback callCallback=new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            updateCall(call);
        }
    };

    public void updates(CallManagerListener listener)
    {
        this.listener=listener;
    }

    public GsmCall getCurentCallValue(){
        if(currentCall==null)
            return null;
        else
            return MappersKt.toGsmCall(currentCall);
    }

    public void updateCall(Call call) {
        currentCall = call;
        if (call != null) {
            if(listener!=null)
                listener.updateCall(MappersKt.toGsmCall(call));
        }
    }

    public void callRemoved(Call call)
    {
        if(currentCall==null)
            return;

        if(currentCall.getDetails().getHandle().getSchemeSpecificPart().equals(call.getDetails().getHandle().getSchemeSpecificPart()))
        {
            currentCall=null;
        }

    }

    public void unregisterCall(Call call)
    {
        call.unregisterCallback(callCallback);
    }

    public void cancelCall() {
        if(currentCall!=null) {
            switch(currentCall.getState())
            {
                case Call.STATE_RINGING:
                    rejectCall();
                    break;

                default:
                    disconnectCall();
            }
        }
    }


    public void acceptCall() {
        if(currentCall!=null)
        {
            currentCall.answer(currentCall.getDetails().getVideoState());
        }
    }

    public void rejectCall() {
        currentCall.reject(false, "");
    }

    public void disconnectCall() {
        currentCall.disconnect();
    }

    public void finishActivity()
    {
        if(listener!=null)
            listener.finishActivity();
    }

    public void onCallAdded(Call call, Context context) {
        if(call!=null && call.getState()==Call.STATE_RINGING && currentCall !=null  &&
                (currentCall.getState()==Call.STATE_ACTIVE || currentCall.getState()==Call.STATE_CONNECTING  || currentCall.getState()==Call.STATE_DIALING))
        {
            call.reject(false, "");
            return;
        }


        call.registerCallback(callCallback);
        currentCall = call;

        Log.wtf("mmmLog","onCallAdded CallFragment.statusFragment "+CallFragment.statusFragment);
        if (CallFragment.statusFragment != CallFragment.RESUMED) {

            finishActivity();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivity(context);

                }
            }, 1000);
        } else {
            updateCall(call);
        }
    }

    private void startActivity(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(context, MainPageActivity.class);
            intent.putExtra(Constants.KEY_FOR_INTENT_POINTER_TO_PAGE, MainPageActivity.MENU_CALL_SENTER);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public interface CallManagerListener{
        void updateCall(GsmCall itm);
        void finishActivity();
    }
}