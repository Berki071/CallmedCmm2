package com.medhelp.callmed2.ui.video_chat.utils;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.medhelp.callmed2.ui.video_chat.WebViewEasyRtc;

public class WebInterface {
    private static final WebInterface instance=new WebInterface();
    private WebInterface(){}

    public static WebInterface getInstance()
    {
        return instance;
    }

    public void setListener(WebInterfaceListener listener)
    {
        this.listener=listener;
    }



    WebInterfaceListener listener;

    public WebInterface(WebInterfaceListener listener) {
        this.listener = listener;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        listener.showToast(toast);
    }

    @JavascriptInterface
    public void loginSuccess(String easyrtcid, String cleaniId, String name) {
        Log.wtf("mLog", "easyrtcid " + easyrtcid + " cleaniId " + cleaniId + "; name " + name);
        listener.logSuccess();
    }

    @JavascriptInterface
    public void performCall(String otherEasyrtcid) {
        Log.wtf("mLog", "otherEasyrtcid " + otherEasyrtcid);
    }


    @JavascriptInterface
    public void occupantListener(String roomName, String isPrimary, String idUser, String name) {
        Log.wtf("mLogOc", "idUser " + idUser + "; name " + name + "; roomName " + roomName + "; isPrimary " + isPrimary);
    }

    @JavascriptInterface
    public void callCancel() {
        Log.wtf("mLogOc", "callCancel");
        if(listener!=null)
            listener.callCancel();
    }

    @JavascriptInterface
    public void showVideoCall() {
        Log.wtf("mLogOc", "showVideoCall");
        listener.showDiv(WebViewEasyRtc.VIDEO,isResume);
    }

    @JavascriptInterface
    public void showCallIncoming(String id, String name, String duration, String timeStart, String companionid, String callerName) {
        Log.wtf("mLog", "showCallIncoming");
        if (listener!=null) {
            if(!isResume)
            {
                listener.showNotification(id, name,duration,timeStart, companionid,callerName);
            }
            else
            {
                listener.showDiv(WebViewEasyRtc.CALL_INCOMING,isResume);
            }
        }
    }

    @JavascriptInterface
    public void showCallOutgoing() {
        if(listener!=null)
            listener.showDiv(WebViewEasyRtc.CALL_OUTGOING,isResume);
    }

    @JavascriptInterface
    public void showWait() {
        if(listener!=null)
            listener.showDiv(WebViewEasyRtc.WAIT,isResume);
    }

    @JavascriptInterface
    public void setTimeLeft(String time) {
        try {
            Log.wtf("mLogTime", "timeLeft" + time);
            int t = Integer.valueOf(time);

            int min = t / 60;
            int sec = t % 60;

            if (listener != null) {
                //listener.timeLeft(time);
                listener.timeLeft(min + " мин. " + sec + " c.");
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.showError("WebInterface setTimeLeft: "+e.getMessage());
            }
        }
    }

    @JavascriptInterface
    public void onStreamClosed(String name, String timeLeft) {
        Log.wtf("mLogOc", "onStreamClosed timeLeft" + timeLeft+",name: "+name);
        int t=Integer.valueOf(timeLeft);
        int min=t/60;
        int sec=t%60;
        if(listener!=null) {
            //listener.timeLeft(timeLeft);
            listener.timeLeft(min+" мин. "+sec+" c.");
            listener.callCancel();
        }
    }

    @JavascriptInterface
    public void crossBtnClick() {
        Log.wtf("mLogOc", "crossBtnClick");
        if(listener!=null)
            listener.crossBtnClick();
    }

    @JavascriptInterface
    public void clickBtnBack() {
        Log.wtf("mLogOc", "clickBtnBack");
        if(listener!=null)
            listener.clickBtnBack();
    }

    @JavascriptInterface
    public void clickIncomingPhoneUp()
    {
        listener.clearNotification();
    }

    @JavascriptInterface
    public void clickIncomingPhoneDown()
    {
        Log.wtf("mLogOc", "clickIncomingPhoneDown");
        listener.clearNotification();
    }

    @JavascriptInterface
    public void clickAttachFile()
    {
        listener.clickAttachFile();
    }

    @JavascriptInterface
    public void callerVideoCanplay()
    {
        listener.callerVideoCanplay();
    }

    @JavascriptInterface
    public void callerViedeoSuspend()
    {
        listener.callerVideoSuspend();
    }

    @JavascriptInterface
    public void clickBtnPhoneDown()
    {
        Log.wtf("mLogOc", "clickBtnPhoneDown");
        listener.clickBtnPhoneDown();
    }

    @JavascriptInterface
    public void sendTimeTimer(String time) {
        listener.sendTimeTimer(time);
    }




    @JavascriptInterface
    public void loginFailure(String errorCode, String message) {
        Log.wtf("mLogOc", "errorCode: " + errorCode + "; message: " + message);
        if (listener!=null) {
            listener.showError("loginFailure errorCode: " + errorCode + "; message: " + message);
        }
    }

    @JavascriptInterface
    public void errorSetName() {
        Log.wtf("mLogOc", "errorSetName");
        if (listener!=null) {
            listener.showError("ErrorSetName.");
        }
    }


    @JavascriptInterface
    public void error(String err) {
        Log.wtf("mLogOc", "error: " + err);
        if (listener!=null) {
            listener.showError( "error: " + err);
        }
    }


    public interface WebInterfaceListener {
        void callCancel();

        void showDiv(String divName, boolean isResume);

        void timeLeft(String time);

        void crossBtnClick();

        void clickBtnBack();

        void showToast(String message);

        void showError(String message);

        void showNotification(String id, String name, String duration, String timeStart, String companionid, String callerName);

        void logSuccess();

        void clearNotification();

        void clickAttachFile();

        void callerVideoCanplay();

        void callerVideoSuspend();

        void clickBtnPhoneDown();

        void sendTimeTimer(String time);
    }

    boolean isResume=true;

    public void setStateActivity(Boolean st)
    {
        isResume=st;
    }

    public boolean isResume() {
        return isResume;
    }
}
