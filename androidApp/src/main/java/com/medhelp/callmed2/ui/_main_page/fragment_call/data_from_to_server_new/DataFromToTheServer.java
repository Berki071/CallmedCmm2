package com.medhelp.callmed2.ui._main_page.fragment_call.data_from_to_server_new;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.medhelp.callmed2.data.model.ClientBDList;
import com.medhelp.callmed2.data.model.ClientBDResponse;
import com.medhelp.callmed2.data.network.NetworkManager;
import com.medhelp.callmed2.data.pref.PreferencesManager;
import com.medhelp.callmed2.ui._main_page.fragment_call.CallFragment;
import com.medhelp.callmed2.utils.main.TimesUtils;
import com.medhelp.callmed2.utils.timber_log.LoggingTree;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DataFromToTheServer {
    public static final String CALL_STATUS_WAIT="wait";
    public static final String CALL_STATUS_INCALL="incall";
    public static final String CALL_STATUS_CALL="call";
    public static final String CALL_STATUS_ENDCALL="endcall";


    Context context;
    DataFromTheServerListener listener;

    private PreferencesManager preferencesHelper;
    private NetworkManager networkHelper;

    private CompositeDisposable disposableGetCall;
    private CompositeDisposable disposableEndCall;

    private Timer timer;
    private MyTimerTask myTimerTask;

    private Boolean isStopTimerOutside =false;
    private Boolean isStopTimerInside =false;


    public DataFromToTheServer(Context context, DataFromTheServerListener listener)
    {
        this.context=context;
        this.listener=listener;
        preferencesHelper=new PreferencesManager(context);
        disposableGetCall =new CompositeDisposable();
        disposableEndCall =new CompositeDisposable();
        networkHelper=new NetworkManager(preferencesHelper);


    }

    public void startTimer()
    {
        if(isStopTimerOutside || isStopTimerInside)
            return;

        if (timer != null) {
            timer.cancel();
        }

        timer=new Timer();
        myTimerTask=new MyTimerTask();

        timer.schedule(myTimerTask,1000);
    }

    public void stopTimer()
    {
        isStopTimerOutside =true;

        if(timer!=null)
        {
            timer.cancel();
            timer=null;
        }
    }


    private void getDataCall() {
        isStopTimerInside=true;

        String pas=preferencesHelper.getCurrentPassword();
        if(pas==null || pas.equals(""))
        {
            isStopTimerInside=false;
            startTimer();
            return;
        }

        if(disposableGetCall==null)
            return;

        disposableGetCall.add(networkHelper
                .getClientBD(getCurrentDate_ddMMyyyy(),preferencesHelper.getSelectedIp())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataList -> {

                    Gson gson=new Gson();
                    ClientBDList tmp=null;
                    try
                    {
                        tmp=gson.fromJson(String.valueOf(dataList),ClientBDList.class);
                    }catch (Exception e)
                    {
                        Timber.e(LoggingTree.getMessageForError(e,"MyServiceCallBefore23Presenter/getDataCall; json:"+dataList.toString()));
                        //listener.error("Ошибка!");
                        return;
                    }

                    listener.callMsg(tmp.getResponse().get(0));
                    isStopTimerInside=false;
                    startTimer();

                }, throwable -> {
                    if(pas!=null && !pas.equals(""))
                         Timber.e(LoggingTree.getMessageForError(throwable,"MyServiceCallPresenter/getDataCall2 "));
                    isStopTimerInside=false;
                    startTimer();
                }));
    }


    public void postEndCall(String id)
    {
        if(id.equals("-1"))
            return;

        if(networkHelper==null) {
            Timber.e(LoggingTree.getMessageForError(null,"MyServiceCallBefore23Presenter postEndCall networkHelper==null"));
            return;
        }

        if(disposableEndCall==null)
            disposableEndCall=new CompositeDisposable();

        disposableEndCall.add(networkHelper
                .setCallStatus(id,CALL_STATUS_ENDCALL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dateList -> {
                    Log.wtf("","");
                }, throwable -> {
                   // listener.error(throwable.getMessage());
                    Timber.e(LoggingTree.getMessageForError(throwable,"MyServiceCallPresenter/postEndCall "));
                }));
    }


    public void sendIncomingPhone(String phone) {

        CompositeDisposable cd = new CompositeDisposable();
        cd.add(networkHelper
                .sendIncommingNumber(getCurrenDate(), phone, getSelectIp())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response ->
                        {
                            cd.dispose();
                        }
                        , throwable -> {
                            if (throwable instanceof ANError)
                            {
                                ANError anError = (ANError) throwable;
                                String mes=anError.getErrorBody();

                                if(mes!=null && !mes.contains("The page you are looking for could not be found"))
                                {
                                    Timber.e(LoggingTree.getMessageForError(throwable,"CallFragmentPresenter/sendIncomingPhone; "));
                                }
                            }
                            else {
                                Timber.e(LoggingTree.getMessageForError(throwable, "CallFragmentPresenter/sendIncomingPhone; "));
                            }

                            cd.dispose();
                        }
                )
        );
    }


    private String getCurrenDate()
    {
        return TimesUtils.getCurrrentDate();
    }

    private String getSelectIp()
    {
        return preferencesHelper.getSelectedIp();
    }

    private String getCurrentDate_ddMMyyyy()
    {
        Date currentTime = Calendar.getInstance().getTime();
        return TimesUtils.getDateSchedule(currentTime);
    }


    public void destroy()
    {
        stopTimer();
        disposableGetCall =null;
        disposableEndCall=null;
    }


    public interface DataFromTheServerListener{
        void error(String str);
        void callMsg(ClientBDResponse data);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            getDataCall();
        }
    }
}
