//package com.medhelp.callmed2.ui.video_chat;
//
//import android.content.Context;
//import android.media.AudioManager;
//import android.media.SoundPool;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.widget.Toast;
//
//
//import com.medhelp.callmed2.R;
//import com.medhelp.callmed2.data.Constants;
//
//import com.medhelp.callmed2.ui.video_chat.utils.MyWebChromeClient;
//import com.medhelp.callmed2.ui.video_chat.utils.MyWebViewClient;
//import com.medhelp.callmed2.ui.video_chat.utils.WebInterface;
//import com.medhelp.callmed2.utils.main.TimesUtils;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import timber.log.Timber;
//
//public class WebViewEasyRtc {
//    private static final WebViewEasyRtc instance=new WebViewEasyRtc();
//    private WebViewEasyRtc(){ }
//
//    public static WebViewEasyRtc getInstance()
//    {
//        return instance;
//    }
//
//    WebView webView;
//
//    VisitItem2 dataForVideoChat;
//    String companionid;
//
//    WebInterface webInterface;
//    MyWebViewClient.WrbClientListener webViewListener;
//    WebInterface.WebInterfaceListener webInterfaceListener;
//
//    public static final String WAIT="wait";
//    public static final String CALL_OUTGOING ="CALL_OUTGOING";
//    public static final String CALL_INCOMING="call_incoming";
//    public static final String VIDEO="video";
//
//    public static final int  TIME_WAIT_UP_PHONE=1000*60;
//    public String currentDivShow="";
//
//    private Timer timer;
//    private MyTimeTask myTimeTask;
//
//    public VideoChatActivity.VideoChatListener listener;
//
//    private Boolean initForPhoneUp=false;
//    private boolean initWebView=false;
//
//    private long videoPlay=0;
//
//    private SoundPool sp;
//    private int endCollSp;
//
//    Context context;
//
//    public void onDestroy()
//    {
//        currentDivShow="";
//        stopTimer();
//        listener=null;
//        webView=null;
//    }
//
//
//    public WebInterface getWebInterface() {
//        return webInterface;
//    }
//
//    public void setWebInterface(WebInterface webInterface) {
//        this.webInterface = webInterface;
//    }
//
//    public void setWebView(Context context, WebView webView) {
//        this.context=context;
//        this.webView = webView;
//        initWebView=false;
//    }
//
//    public void setListener(VideoChatActivity.VideoChatListener listener)
//    {
//        this.listener=listener;
//    }
//
//    public void setDataForVideoChat(VisitItem2 dataForVideoChat, String companionid)
//    {
//        this.companionid=companionid;
//        if(dataForVideoChat!=null)
//            this.dataForVideoChat=dataForVideoChat;
//    }
//
//    public VisitItem2 getDataForVideoChat()
//    {
//        return dataForVideoChat;
//    }
//
//    public void initWeb()
//    {
//        initListeners();
//
//        webView.setWebViewClient(new MyWebViewClient(webViewListener));
//        webView.setWebChromeClient(new MyWebChromeClient());
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setAllowFileAccess(true);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setAllowFileAccessFromFileURLs(true);
//        webSettings.setAllowUniversalAccessFromFileURLs(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            //для звука
//            webSettings .setMediaPlaybackRequiresUserGesture(false);
//        }
//
//        webInterface.setListener( webInterfaceListener);
//        webView.addJavascriptInterface(webInterface, "AndroidListener");
//        webView.loadUrl("https://5.130.3.124:8443/");
//
//        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//        endCollSp =sp.load(context, R.raw.button_3, 1);
//    }
//
//
//    private void initListeners()
//    {
//        webViewListener =new MyWebViewClient.WrbClientListener() {
//            @Override
//            public void ready() {
//
//                webView.evaluateJavascript("setIdAndroid('"+dataForVideoChat.getIdDoc()+"')",null);  //myIdInAndroid
//                webView.evaluateJavascript("setTimeStart('"+  dataForVideoChat.getTime()+" "+dataForVideoChat.getDate()+"')",null);
//                webView.evaluateJavascript("setClearDuration('"+dataForVideoChat.getDurationSec()+"')",null);
//
//                webView.evaluateJavascript("setNameUser('"+dataForVideoChat.getDocAllName()+"')",null); //selfName
//                webView.evaluateJavascript("setAppName('ChatikMedHelp"+dataForVideoChat.getId_zapisi()+"')",null);
//
//                String sss=dataForVideoChat.getFullNameUser();
//                String sssd="ИВАНОВ Иван Борисович";
//                boolean boo=sss.equals(sssd);
//
//                webView.evaluateJavascript("setCompanionName('"+ dataForVideoChat.getFullNameUser()+"')",null); //nameCompanion
//                webView.evaluateJavascript("setTypeClient('doc')",null);  //isDoc
//
//                webView.evaluateJavascript("connect()",null);
//                initForPhoneUp=true;
//            }
//
//            @Override
//            public void error(String msg) {
//                Timber.e("WebViewEasyRtc initListeners: "+msg);
//                listener.showToastMsg(context.getResources().getString(R.string.api_default_error));
//                listener.callOnBack();
//            }
//        };
//
//
//
//        webInterfaceListener=new WebInterface.WebInterfaceListener() {
//            @Override
//            public void callCancel() {
//                if(WebInterface.getInstance().isResume()) {
//                    listener.callOnBack();
//                }
//
//                Timber.v("Звонок, окончен id_zapisi: "+dataForVideoChat.getId_zapisi());
//            }
//
//
//            @Override
//            public void showDiv(final String divName, boolean isResume) {
//                if(divName.equals(VIDEO))
//                {
//                    Handler mainHandler = new Handler(Looper.getMainLooper());
//                    Runnable myRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            listener.stopVibration();
//                            String tmp= String.valueOf(getTimerTime());
//
//                            webView.evaluateJavascript("setSessionDuration('" + tmp + "')", null);
//
//                            Timber.v("Звонок, начало видео id_zapisi: "+dataForVideoChat.getId_zapisi());
//
//                            if (tmp.equals("0")) {
//                                webView.evaluateJavascript("stopCall()", null);
//                                listener.callOnBack();
//                            }
//                        }
//                    };
//                    mainHandler.post(myRunnable);
//                }
//
//
//
//                if(currentDivShow.equals(WAIT) && !divName.equals(WAIT))
//                {
//                    listener.stopMusics();
//                }
//
//                if(divName.equals(CALL_OUTGOING))
//                {
//                   //Log.wtf("timber","иходящий звонок док");
//                    Timber.v("Звонок клиенту id_zapisi: "+dataForVideoChat.getId_zapisi());
//                    listener.playLongBells();
//                    startTimer();
//                }
//
//                if(currentDivShow.equals(CALL_OUTGOING) && !divName.equals(CALL_OUTGOING))
//                {
//                    listener.stopMusics();
//                    stopTimer();
//                }
//
//
//                if(divName.equals(CALL_INCOMING))
//                {
//                    if(WebInterface.getInstance().isResume())
//                        listener.startVibration();
//
//                }
//
//                currentDivShow=divName;
//            }
//
//            @Override
//            public void timeLeft(String time) {
//                try {
//                    //listener.showToastMsg("Time left = " + time);
//                }catch (Exception e){
//                    showError("WebViewEasyRtc showDiv: "+e.getMessage());
//                }
//            }
//
//            @Override
//            public void crossBtnClick( ) {
//                listener.showAlerts();
//            }
//
//            @Override
//            public void clickBtnBack() {
//                listener.setIsClickBack(true);
//
//                if(currentDivShow.equals(VIDEO))
//                {
//                    webView.evaluateJavascript("stopVideoForBack()",null);
//                }
//                listener.callOnBack();
//            }
//
//            @Override
//            public void showToast(String message) {
//                if(message.contains("Requested video size"))
//                    return;
//
//                Log.wtf("mmmd","message: "+message);
//                //listener.showToastMsg(message);
//            }
//
//            @Override
//            public void sendTimeTimer(String time) {
//                if(time.equals("05:00")) {
//                    Toast.makeText(context, "Осталось 5 минут", Toast.LENGTH_LONG).show();
//                    timeIsRunningOut();
//                }
//
//                if(time.equals("01:00")) {
//                    Toast.makeText(context, "Осталась 1 минута", Toast.LENGTH_LONG).show();
//                    timeIsRunningOut();
//                }
//            }
//
//            //!!!!!!!!!!!!!
//            @Override
//            public void showError(String message) {
//                Timber.e("WebViewEasyRtc webInterfaceListener: "+message);
//                //Log.wtf("mmmd","message: "+message);
//            }
//
//            @Override
//            public void showNotification(String id, String name, String duration, String timeStart,String companionid,String callerName) {
//                listener.showNotifications(id,name,duration,timeStart,companionid ,callerName);
//
//            }
//
//            @Override
//            public void logSuccess() {
//
//                Handler mainHandler = new Handler(Looper.getMainLooper());
//                Runnable myRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        if(initForPhoneUp)
//                        {
//                            initWebView=true;
//                        }
//
//                        if(perfomeCloseRtcPhoneDown)
//                        {
//                            perfomeCloseRtcPhoneDown=false;
//
//                            webView.evaluateJavascript("setCompanionId('" + companionid + "')", null);
//                            webView.evaluateJavascript("notifiIncomingPhoneDown()", null);
//                            listener.activityOnBack();
//                            return;
//                        }
//
//                        if(perfomeReplyRtcPhoneUp)
//                        {
//                            initForPhoneUp = false;
//                            perfomeReplyRtcPhoneUp=false;
//                            webView.evaluateJavascript("setCompanionId('" + companionid + "')", null);
//                            webView.evaluateJavascript("incomingPhoneUp()", null);
//                            return;
//                        }
//
//                        if (dataForVideoChat.getExecuteTheScenario().equals(Constants.SCENARIO_VIDEO) && initForPhoneUp == true) {
//                            // listener.closeNotification();
//
//                            if (companionid != null && !companionid.equals("") && webView!=null) {
//                                initForPhoneUp = false;
//                                webView.evaluateJavascript("setCompanionId('" + companionid + "')", null);
//                                webView.evaluateJavascript("incomingPhoneUp()", null);
//                            }
//                        }
//                        else if(dataForVideoChat.getExecuteTheScenario().equals(Constants.SCENARIO_INCOMING_WAIT)  && initForPhoneUp == true)
//                        {
//                            if (companionid != null && !companionid.equals("")  && webView!=null) {
//                                initForPhoneUp = false;
//                                webView.evaluateJavascript("setCompanionId('" + companionid + "')", null);
//                                webView.evaluateJavascript("showDiv(\"call_incoming\")", null);
//                            }
//                        }
//                    }
//                };
//                mainHandler.post(myRunnable);
//            }
//
//            @Override
//            public void clearNotification() {
//                listener.closeNotification();
//            }
//
//            @Override
//            public void clickAttachFile() {
//
//            }
//
//            @Override
//            public void callerVideoCanplay() {
//                videoPlay=System.currentTimeMillis();
//                String date=TimesUtils.longToString(videoPlay,TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
//                //Log.wtf("timber","video play "+date);
//            }
//
//            @Override
//            public void callerVideoSuspend() {
//                long currentTime =System.currentTimeMillis();
//                long difference= currentTime - videoPlay;
//                difference/=1000;
//                long min=difference/60;
//                long sec=difference%60;
//                videoPlay=0;
//
//                //listener.showToastMsg("Звонок продлился: "+min+" мин. "+sec+" c.");
//            }
//
//            @Override
//            public void clickBtnPhoneDown() {
//                Log.wtf("timber","трубку положил Doc");
//            }
//
//
//        };
//    }
//
//
//
//    private void timeIsRunningOut()
//    {
//        sp.play(endCollSp, 1, 1, 0, 0, 1);
//    }
//
//    private int getTimerTime()
//    {
//
//        long currentTimeLong=System.currentTimeMillis();
//        long timeOfReceiptStart=dataForVideoChat.getTimeMills();
//        long timeOfReceiptEnd=timeOfReceiptStart+(dataForVideoChat.getDurationSec()*1000);
//
//        if(((timeOfReceiptStart-Constants.MIN_TIME_BEFORE_VIDEO_CALL*60*1000)<currentTimeLong)  && currentTimeLong<timeOfReceiptEnd)
//        {
//            if (timeOfReceiptStart > currentTimeLong) {
//                return dataForVideoChat.getDurationSec();
//            } else {
//                long k1 =  timeOfReceiptEnd-currentTimeLong;
//                long k2 = (k1 / 1000);
//                return (int) k2;
//            }
//        }
//        else
//        {
//            return 0;
//        }
//    }
//
//
//
//    public void startTimer(){
//        stopTimer();
//
//        timer=new Timer();
//        myTimeTask=new MyTimeTask();
//
//        timer.schedule(myTimeTask,TIME_WAIT_UP_PHONE);
//    }
//
//    public void stopTimer(){
//        if(timer!=null)
//        {
//            timer.cancel();
//            timer=null;
//        }
//    }
//
//    class MyTimeTask extends TimerTask {   //таймер ожидания поднятия трубки
//        @Override
//        public void run() {
//            listener.endOfResponseTimes();
//            Timber.v("Не дозвонился, вышло время ожидания  id_zapisi: "+dataForVideoChat.getId_zapisi());
//        }
//
//    }
//
//
//    public void incomingPhoneDown()
//    {
//        webView.evaluateJavascript("incomingPhoneDown()",null);
//    }
//
//    public void stopVideoForBack()
//    {
//        webView.evaluateJavascript("stopVideoForBack()",null);
//    }
//
//    public void stopOnPauseStateActivityForDoc()
//    {
//        webView.evaluateJavascript("stopOnPauseStateActivityForDoc()", null);
//    }
//
//    public void stopCall()
//    {
//        webView.evaluateJavascript("stopCall()", null);
//    }
//
//    public void stopOnPauseStateActivityForPatient()
//    {
//        webView.evaluateJavascript("stopOnPauseStateActivityForPatient()", null);
//    }
//
//
//    boolean perfomeCloseRtcPhoneDown=false;
//    public void closeRtcPhoneDown(String companionId)
//    {
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (listener!=null)
//                    listener.closeNotification();
//
//                if(initWebView) {
//                    webView.evaluateJavascript("setCompanionId('" + companionId + "')", null);
//                    webView.evaluateJavascript("notifiIncomingPhoneDown()", null);
//                    listener.activityOnBack();
//                }
//                else
//                {
//                    companionid = companionId;
//                    perfomeCloseRtcPhoneDown=true;
//                }
//            }
//        };
//        mainHandler.post(myRunnable);
//    }
//
//
//    boolean perfomeReplyRtcPhoneUp = false;
//    public void replyRtcPhoneUp(String companionId) {
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//
//                if (initWebView) {
//                    initForPhoneUp = false;
//                    webView.evaluateJavascript("setCompanionId('" + companionid + "')", null);
//                    webView.evaluateJavascript("incomingPhoneUp()", null);
//                } else {
//                    companionid = companionId;
//                    perfomeReplyRtcPhoneUp=true;
//                }
//            }
//        };
//        mainHandler.post(myRunnable);
//    }
//
//    public void enableMicrophone(boolean boo)
//    {
//        if(boo)
//        {
//            webView.evaluateJavascript("microphoneOn()", null);
//        }
//        else
//        {
//            webView.evaluateJavascript("microphoneOff()", null);
//        }
//    }
//
//    public void enableCamera(boolean boo)
//    {
//        if(boo)
//        {
//            webView.evaluateJavascript("cameraOn()", null);
//        }
//        else
//        {
//            webView.evaluateJavascript("cameraOff()", null);
//        }
//    }
//
//}
