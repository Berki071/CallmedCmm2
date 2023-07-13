package com.medhelp.callmed2.ui._main_page.fragment_call.call_center_new;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Vibrator;
import android.provider.ContactsContract;
import com.medhelp.callmed2.data.pref.PreferencesManager;
import com.medhelp.callmed2.ui._main_page.MainPageActivity;
import com.medhelp.callmed2.ui._main_page.fragment_call.data_from_to_server_new.DataFromToTheServer;

import java.util.Timer;
import java.util.TimerTask;

public class CallCenterPresenter {
    PreferencesManager preferencesHelper;
    CallCenterService main;
    Context context;

    public  CallCenterPresenter (Context context,CallCenterService main)
    {
        this.context=context;
        preferencesHelper=new PreferencesManager(context);
        this.main=main;
    }

    public Boolean testShowAlertNoIP()
    {
        if (!testExistIp()) {
            main.showAlertNoIp();
            return true;
        }
        else
            return false;
    }

    public Boolean testExistIp() {
        String ip = preferencesHelper.getSelectedIp();
        if (ip.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isCheckLoginAndPassword()
    {
        int idUser=preferencesHelper.getCurrentUserId();
        String pass=preferencesHelper.getCurrentPassword();

        if(idUser==0  || pass==null  || pass.equals(""))
            return false;
        else
            return true;
    }

    public void startVibrate()
    {
        if(context!=null)
        {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1000, 500, 1000, 500};
            v.vibrate(pattern, 0);
        }
    }

    public void stopVibration()
    {
        if(context!=null)
        {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.cancel();
        }
    }

    private boolean isPermissionGrantedReadContacts()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if ( context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                return true;
            else
                return false;
        }else
            return true;
    }

    public String getNumberFromContacts(String number)
    {
        if(number==null)
            return "";

        if(!isPermissionGrantedReadContacts())
            return "";

        String numberNew="";

        for(int i=0;i<number.length();i++)
        {
            numberNew+="%";
            numberNew+=number.substring(i,i+1);
        }

        String name = null;
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER+" like'%" + numberNew +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, null);

        if (c.moveToFirst()) {
            name = c.getString(0);
        }
        c.close();
        if(name==null)
            name = "";

        return name;
    }

    //region timer for call
    private Timer timer;
    private MyTimerTask myTimerTask;
    int valueTimer=0;

    public void startTimerPhoneConversation()
    {
        valueTimer=0;
        if (timer != null) {
            timer.cancel();
            timer=null;
        }

        timer=new Timer();
        myTimerTask=new MyTimerTask();

        timer.schedule(myTimerTask,1000,1000);
    }

    public void stopTimerPhoneConversation()
    {
        if(timer!=null)
        {
            timer.cancel();
            timer=null;
        }
        main.setSaveTimer("");
    }

    private String getTimeTimer()
    {
        String time="";

        int min=valueTimer/60;
        int sec=valueTimer%60;

        if(min<10)
        {
            time+="0";
        }
        time+=min+":";

        if(sec<10)
        {
            time+="0";
        }
        time+=sec+"";

        return time;
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if(main.getContextActivity()!=null) {
                ((MainPageActivity) main.getContextActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        main.setSaveTimer(getTimeTimer());
                    }
                });
            }
            valueTimer++;
        }
    }
    //endregion
}
