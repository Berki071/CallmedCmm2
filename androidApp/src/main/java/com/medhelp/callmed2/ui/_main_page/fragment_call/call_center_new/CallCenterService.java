package com.medhelp.callmed2.ui._main_page.fragment_call.call_center_new;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.medhelp.callmed2.R;
import com.medhelp.callmed2.data.model.ClientBDResponse;
import com.medhelp.callmed2.ui._main_page.MainPageActivity;
import com.medhelp.callmed2.ui._main_page.fragment_call.CallFragment;
import com.medhelp.callmed2.ui._main_page.fragment_call.data_from_to_server_new.DataFromToTheServer;
import com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer.CallManager;
import com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer.GsmCall;
import com.medhelp.callmed2.ui._main_page.fragment_call.receiver_call.CallReceiver;
import com.medhelp.callmed2.ui._main_page.fragment_call.receiver_call.CallReceiverListener;

import java.lang.ref.WeakReference;

import static com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer.GsmCall.Status.DISCONNECTED;
import static com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer.GsmCall.Status.RINGING;
import static com.medhelp.callmed2.ui._main_page.fragment_call.my_dialer.GsmCall.Status.UNKNOWN;

public class CallCenterService extends Service {

    //region binder
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public CallCenterService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CallCenterService.this;
        }
    }
    //endregion

    private Context context;

    private static WeakReference textStatus;
    private static WeakReference textTimer;
    private static WeakReference txtFio;
    private static WeakReference txtPhone;
    private static WeakReference callBtn;
    private static WeakReference endCallBtn;
    private static WeakReference btnMic;

    public boolean IsProgramRunning=false;

    // region save activity value
    private static String saveStatus="";
    private static String saveFio="";
    private static String savePhone="";
    private static String saveTimer="";

    AudioManager audioManager;

    public String getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(String saveStatus) {
        if((CallFragment.statusFragment==CallFragment.RESUMED  || CallFragment.statusFragment==CallFragment.CREATE_VIEW) && textStatus.get()!=null)
        {
            try {
                ((TextView) textStatus.get()).setText(saveStatus);
            }catch (Exception e){}
        }
        this.saveStatus = saveStatus;
    }

    public String getSaveFio() {

        return saveFio;
    }

    public void setSaveFio(String saveFio) {
        try {
            int s = CallFragment.statusFragment;
            Object o = txtFio.get();
            Boolean dd = txtFio.get() != null;
            Boolean dd2 = CallFragment.statusFragment == CallFragment.RESUMED || CallFragment.statusFragment == CallFragment.CREATE_VIEW;

            Log.wtf("mmmLog", "statusFragment= " + s + "; txtFio.get()!=null: " + dd + "; statusFragment==RESUMED||CREATE_VIEW: " + dd2);

            if ((CallFragment.statusFragment == CallFragment.RESUMED || CallFragment.statusFragment == CallFragment.CREATE_VIEW) && txtFio.get() != null) {
                try {
                    ((EditText) txtFio.get()).setText(saveFio);
                } catch (Exception e) {
                }
            }
            this.saveFio = saveFio;
        }catch (Exception e){}
    }

    public String getSavePhone() {
        return savePhone;
    }

    public void setSavePhone(String savePhone) {
        if((CallFragment.statusFragment==CallFragment.RESUMED  || CallFragment.statusFragment==CallFragment.CREATE_VIEW) && txtPhone.get()!=null)
        {
            try {
                ((EditText) txtPhone.get()).setText(savePhone);
            }catch (Exception e){}
        }
        this.savePhone = savePhone;
    }

    public String getSaveTimer() {
        return saveTimer;
    }

    public void setSaveTimer(String saveTimer) {
        if((CallFragment.statusFragment==CallFragment.RESUMED  || CallFragment.statusFragment==CallFragment.CREATE_VIEW) && textTimer.get()!=null)
        {
            try {
                ((TextView) textTimer.get()).setText(saveTimer);
            }catch (Exception e){}
        }
        this.saveTimer = saveTimer;
    }
//endregion

    public Context getContextActivity()
    {
        if((textTimer.get())!=null)
            return ((TextView) textTimer.get()).getContext();
        else
            return null;
    }

    DataFromToTheServer dataFromToTheServer;
    private CallReceiver callReceiver ;  //before 23
    CallManager callManager; //after = 23

    CallCenterPresenter presenter;
    static ClientBDResponse currentCallData;

    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.callBtn) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String t = getSaveStatus();
                    if (t != null && t.equals("Входящий вызов…")) {
                        CallManager.getInstance().acceptCall();
                    } else {
                        if (presenter.testShowAlertNoIP())
                            return;

                        if (getSavePhone().equals(""))
                            return;

                        if (currentCallData == null || currentCallData.getNumberPhone().equals("") || !currentCallData.getStatus().equals(DataFromToTheServer.CALL_STATUS_WAIT))
                            return;


                        toCallNumber(currentCallData.getNumberPhone());
                    }
                } else {
                    if (presenter.testShowAlertNoIP())
                        return;

                    if (currentCallData == null || currentCallData.getNumberPhone().equals("") || !currentCallData.getStatus().equals(DataFromToTheServer.CALL_STATUS_WAIT))
                        return;

                    toCallNumber(currentCallData.getNumberPhone());
                }
            } else if (id == R.id.endCallBtn) {
                clickEndCall();
            } else if (id == R.id.btnMic) {//                    boolean booo=((ImageButton)btnMic.get()).isClickable();
//                    if(booo)
//                    {
//                        ((ImageButton)btnMic.get()).setClickable(false);
//                    }

                if (isTurnOnMicrophone) {
                    audioManager.setMicrophoneMute(true);
                    ((ImageButton) btnMic.get()).setImageResource(R.drawable.ic_mic_off_red_600_24dp);
                    isTurnOnMicrophone = false;
                } else {
                    turnOnMicrophone();
                }
            }
        }
    };


    DataFromToTheServer.DataFromTheServerListener dataFromTheServerListener=new DataFromToTheServer.DataFromTheServerListener() {
        @Override
        public void error(String str) {

        }

        @Override
        public void callMsg(ClientBDResponse data) {
            refreshDateCall(data);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();

        textStatus = new WeakReference<>(CallFragment.textStatus);
        textTimer = new WeakReference<>(CallFragment.textTimer);
        txtFio = new WeakReference<>(CallFragment.txtFio);
        txtPhone = new WeakReference<>(CallFragment.txtPhone);
        callBtn = new WeakReference<>(CallFragment.callBtn);
        endCallBtn = new WeakReference<>(CallFragment.endCallBtn);
        btnMic = new WeakReference<>(CallFragment.btnMic);


        presenter=new CallCenterPresenter(context, this);

        ImageButton callBtnIB = (ImageButton) callBtn.get();
        ImageButton endCallBtnIB = (ImageButton) endCallBtn.get();
        ImageButton btnMicB = (ImageButton) btnMic.get();

        if (callBtnIB != null)
            callBtnIB.setOnClickListener(clickListener);

        if (endCallBtnIB != null)
            endCallBtnIB.setOnClickListener(clickListener);

        if (btnMicB != null)
            btnMicB.setOnClickListener(clickListener);

        if (presenter.isCheckLoginAndPassword() && presenter.testExistIp()) {
            dataFromToTheServer=new DataFromToTheServer(context,dataFromTheServerListener);
            dataFromToTheServer.startTimer();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            registerPhoneStateReceiver();
        }else
        {
            callManager = CallManager.getInstance();

            GsmCall gsmCall=callManager.getCurentCallValue();
            if(gsmCall!=null)
            {
                updateView(gsmCall);
            }

            callManager.updates(new CallManager.CallManagerListener() {
                @Override
                public void updateCall(GsmCall itm) {
                    if(itm!=null)
                        updateView(itm);
                }

                @Override
                public void finishActivity() {
                    Context context=getContextActivity();
                    if(context!=null)
                        ((MainPageActivity)context).finish();
                }
            });
        }

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        refreshFioAndPhone();
        return super.onStartCommand(intent, flags, startId);
    }


    private void updateView(GsmCall gsmCall) {

        switch(gsmCall.getStatus())
        {
            case CONNECTING:
                //textStatusS = "Connecting…";
                setSaveStatus( "Набор номера…");
                if(!IsProgramRunning)
                {
                    setSaveFio(presenter.getNumberFromContacts(gsmCall.getDisplayName()));
                }
                break;

            case DIALING:
                //textStatusS = "Calling…";
                setVisibleBtnMic(true);
                setSaveStatus( "Набор номера…");
                if(!IsProgramRunning)
                {
                    setSaveFio(presenter.getNumberFromContacts(gsmCall.getDisplayName()));
                }
                break;

            case RINGING:
                //textStatusS = "Incoming call…";
                setSaveStatus( "Входящий вызов…");
                presenter.startVibrate();
                setVisibleBtnMic(true);
                if(dataFromToTheServer!=null) {
                    dataFromToTheServer.sendIncomingPhone(gsmCall.getDisplayName() != null ? gsmCall.getDisplayName() : "Unknown");
                }
                setSaveFio(presenter.getNumberFromContacts(gsmCall.getDisplayName()));
                break;

            case ACTIVE:
                turnOnMicrophone();
                setVisibleBtnMic(true);
                setSaveStatus( "Active");
                presenter.stopVibration();
                presenter.startTimerPhoneConversation();
                break;

            case DISCONNECTED:
                turnOnMicrophone();
                setVisibleBtnMic(false);
                //textStatusS = "Finished call…";
                setSaveStatus( "Звонок окончен…");
                presenter.stopVibration();
                callEndEvent();
                presenter.stopTimerPhoneConversation();
                break;

            case UNKNOWN:
                break;
        }

        //String s=gsmCall.getDisplayName()!=null ? gsmCall.getDisplayName() : "Unknown";  //phone

        if(!(gsmCall.getStatus().equals(DISCONNECTED) || gsmCall.getStatus().equals(UNKNOWN)))
            setSavePhone(gsmCall.getDisplayName()!=null ? gsmCall.getDisplayName() : "Unknown");

        if(callBtn.get()!=null)
            ((ImageButton)callBtn.get()).setVisibility((gsmCall.getStatus().equals(RINGING) || gsmCall.getStatus().equals(DISCONNECTED)) ? View.VISIBLE : View.GONE);
    }


    private void registerPhoneStateReceiver()
    {
        callReceiver = new CallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        this.registerReceiver(callReceiver, filter);

        callReceiver.setCallReceiverListener(new CallReceiverListener() {

            @Override
            public void ringing(String number) {
                setSaveStatus("Входящий вызов…");
                setSavePhone(number);
                if(dataFromToTheServer!=null) {
                    dataFromToTheServer.sendIncomingPhone(number);
                }
                setSaveFio(presenter.getNumberFromContacts(number));
            }

            @Override
            public void idle() {
                turnOnMicrophone();
                setVisibleBtnMic(false);
                setSaveStatus("Звонок окончен…");
                callEndEvent();
                presenter.stopTimerPhoneConversation();
            }

            @Override
            public void offHook(String number) {
                turnOnMicrophone();
                setVisibleBtnMic(true);
                setSaveStatus("Active");
                presenter.startTimerPhoneConversation();
                if(!IsProgramRunning)
                {
                    setSaveFio(presenter.getNumberFromContacts(number));
                }
            }

            @Override
            public void endCall() {

            }


        });
    }

    private void callEndEvent() {
        setSavePhone("");
        setSaveFio("");

        if (currentCallData != null && dataFromToTheServer!=null && !currentCallData.getStatus().equals(DataFromToTheServer.CALL_STATUS_INCALL) && currentCallData.getId() != 0  && IsProgramRunning) {
            dataFromToTheServer.postEndCall(String.valueOf(currentCallData.getId()));
        }
        IsProgramRunning = false;

        ((TextView)textStatus.get()).postDelayed(new Runnable() {
            @Override
            public void run() {
                String text=((TextView)textStatus.get()).getText().toString();
                if(text.equals("Звонок окончен…"))
                {
                    setSaveStatus("");
                    testDataBDOnShow();
                }
            }
        },1500);
    }

    private void testDataBDOnShow()
    {
        if(currentCallData!=null && currentCallData.getStatus().equals(DataFromToTheServer.CALL_STATUS_WAIT))
        {
            setSaveFio(currentCallData.getFio());
            setSavePhone(currentCallData.getNumberPhone());
        }
    }


    public void refreshDateCall(ClientBDResponse data) {
        if(data.getFio()==null || data.getFio().equals(""))
        {
            setSaveFio("");
            setSavePhone("");

            currentCallData = null;
            return;
        }

        if (data.getStatus().equals(DataFromToTheServer.CALL_STATUS_INCALL)) {
            return;
        }

        if(currentCallData!=null  && currentCallData.getStatus().equals(data.getStatus())  && currentCallData.getNumberPhone().equals(data.getNumberPhone())
                && currentCallData.getFio().equals(data.getFio()))
        {
            return;
        }

        if (currentCallData!=null && (!data.getFio().equals(currentCallData.getFio()) || !data.getNumberPhone().equals(currentCallData.getNumberPhone()))
                && currentCallData.getStatus().equals(DataFromToTheServer.CALL_STATUS_CALL)) {
            clickEndCall();
        }

        statusProcessing(data);
    }

    private void statusProcessing(ClientBDResponse data)
    {
        currentCallData=data;

        switch (data.getStatus()) {
            case DataFromToTheServer.CALL_STATUS_CALL:
                if (currentCallData != null && !currentCallData.getNumberPhone().equals("")) {
                    setSaveFio(data.getFio());
                    setSavePhone(data.getNumberPhone());
                    toCallNumber(currentCallData.getNumberPhone());
                }
                break;

            case DataFromToTheServer.CALL_STATUS_ENDCALL:
                if(txtPhone!=null) {
                    String showNumber = ((EditText) txtPhone.get()).getText().toString();
                    Log.wtf("phoneNumbers", "showNumber:" + showNumber + " ClientBDResponse:" + data.getNumberPhone());
                }
                //String tmp1=showNumber;
               // String tmp2=data.getNumberPhone();
               // if(showNumber.equals("") || showNumber.equals(data.getNumberPhone())) {
                    setSavePhone("");
                    setSaveFio("");
                    clickEndCall();
               // }
                break;

            case DataFromToTheServer.CALL_STATUS_WAIT:
                setSaveFio(data.getFio());
                setSavePhone(data.getNumberPhone());
                break;
        }
    }

    public void clickEndCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CallManager.getInstance().cancelCall();
        }else{
            callReceiver.endCall();
        }

        testEndCallAtWaitStatus();
    }

    private void testEndCallAtWaitStatus()
    {
        if(currentCallData!=null && dataFromToTheServer!=null && currentCallData.getStatus().equals(DataFromToTheServer.CALL_STATUS_WAIT) && currentCallData.getFio().equals(saveFio))
        {
            setSavePhone("");
            setSaveFio("");
            dataFromToTheServer.postEndCall(String.valueOf(currentCallData.getId()));
        }
    }


    private void toCallNumber(String number)
    {
        IsProgramRunning=true;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            return;

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + number));

        startActivity(intent);
    }



    public void refreshFioAndPhone() {
        setSaveFio(getSaveFio());
        setSavePhone(getSavePhone());
    }


    public void showAlertNoIp() {
        if (callBtn == null)
            return;

        Context context = ((ImageButton) callBtn.get()).getContext();
        if (context == null)
            return;

        if(((AppCompatActivity)context).isFinishing())
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.noIp)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog == null)
                            return;

                        dialog.cancel();
                    }
                });

        builder.show();
    }

    private boolean isTurnOnMicrophone=true;
    public void testMicrophone()
    {
        if(audioManager==null)
            return;

        if (audioManager.isMicrophoneMute()) {
            ((ImageButton)btnMic.get()).setImageResource(R.drawable.ic_mic_off_red_600_24dp);
            isTurnOnMicrophone=false; //is On
        }else
        {
            ((ImageButton)btnMic.get()).setImageResource(R.drawable.ic_mic_teal_a700_24dp);
            isTurnOnMicrophone=true;
        }
    }

    public void turnOnMicrophone()
    {
        if(audioManager!=null) {
            audioManager.setMicrophoneMute(false);
            isTurnOnMicrophone = true;
        }

        if ((CallFragment.statusFragment == CallFragment.RESUMED || CallFragment.statusFragment == CallFragment.CREATE_VIEW)) {
            try {
                ((ImageButton) btnMic.get()).setImageResource(R.drawable.ic_mic_teal_a700_24dp);
            } catch (Exception e) {}
        }
    }

    public void setVisibleBtnMic(boolean boo)
    {
       // if((CallFragment.statusFragment==CallFragment.RESUMED  || CallFragment.statusFragment==CallFragment.CREATE_VIEW)) {
        if(btnMic.get()!=null) {
            try {
                if (boo) {
                    ((ImageButton) btnMic.get()).setClickable(true);
                    ((ImageButton) btnMic.get()).setAlpha(1.f);
                } else {
                    ((ImageButton) btnMic.get()).setClickable(false);
                    ((ImageButton) btnMic.get()).setAlpha(0.3f);
                }
            } catch (Exception e) {
            }
        }
        //}
    }


    @Override
    public void onDestroy() {
        try {
            dataFromToTheServer.destroy();

            if(callReceiver!=null)
                this.unregisterReceiver(callReceiver);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                presenter.stopVibration();
            }

        } catch (Exception e) {}
    }
}
