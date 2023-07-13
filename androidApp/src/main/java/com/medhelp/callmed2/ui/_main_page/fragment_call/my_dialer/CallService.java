package com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import com.medhelp.callmed2.ui._main_page.MainPageActivity;

@TargetApi(Build.VERSION_CODES.M)
public class CallService extends InCallService {

    private final String LOG_TAG = "CallService";
    CallManager callManager=CallManager.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        callManager.onCallAdded(call,getBaseContext());
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        //Log.i(LOG_TAG, "onCallRemoved: "+call);
        callManager.unregisterCall(call);
        //callManager.updateCall(null);
        callManager.callRemoved(call);
    }
}