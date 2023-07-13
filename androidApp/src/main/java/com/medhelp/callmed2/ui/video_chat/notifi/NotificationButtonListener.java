package com.medhelp.callmed2.ui.video_chat.notifi;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.medhelp.callmed2.data.model.VisitItem2;
import com.medhelp.callmed2.ui.video_chat.VideoChatActivity;
import com.medhelp.callmed2.ui.video_chat.WebViewEasyRtc;


public class NotificationButtonListener extends BroadcastReceiver {
    public static final String ACTION_CLOSE = "com.example.medhelp2.CLOSE";
    public static final String ACTION_GO_TO_VIDEO="com.example.medhelp2.GOTOVIDEO";
    public static final String ACTION_CLOSE_NOTY_ONLY = "com.example.medhelp2.CLOSE_NOTY_ONLY";
    public static final String ACTION_GO_TO_VIDEO_WAIT = "com.example.medhelp2.ACTION_GO_TO_VIDEO_WAIT";



    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();

        Vibrator vibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(/*MyNotification.ID_NOTI_FOR_VIDEO_CALL*/ 1);
        WebViewEasyRtc instance=WebViewEasyRtc.getInstance();

        switch(action)
        {
            //входящий звонок
            case ACTION_CLOSE:
                instance.closeRtcPhoneDown(intent.getExtras().getString("companionid"));
                break;

            case ACTION_GO_TO_VIDEO:
                instance.replyRtcPhoneUp(intent.getExtras().getString("companionid"));
                break;

            // напоминание о приеме, надо зайти и ждать врача
            case ACTION_CLOSE_NOTY_ONLY:
                break;

            case ACTION_GO_TO_VIDEO_WAIT:
                Intent intBtnOpen = new Intent(context, VideoChatActivity.class);
                VisitItem2 rr=intent.getParcelableExtra(VisitItem2.class.getCanonicalName());
                intBtnOpen.putExtra(VisitItem2.class.getCanonicalName(),rr);
                context.startActivity(intBtnOpen);
                break;

            default:
                //Log.wtf("mLog","action: "+action);
        }

    }
}
