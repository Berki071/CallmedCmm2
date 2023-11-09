//package com.medhelp.callmed2.ui.video_chat
//
//import android.app.AlertDialog
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.media.AudioManager
//import android.media.MediaPlayer
//import android.media.MediaPlayer.OnCompletionListener
//import android.media.SoundPool
//import android.os.Bundle
//import android.os.Vibrator
//import android.text.Html
//import android.util.Log
//import android.view.View
//import android.webkit.WebView
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.medhelp.callmed2.R
//import com.medhelp.callmed2.data.Constants
//import com.medhelp.callmed2.data.model.VisitItem2
//import com.medhelp.callmed2.ui._main_page.MainPageActivity
//import com.medhelp.callmed2.ui.video_chat.utils.CallReceiver
//import com.medhelp.callmed2.ui.video_chat.utils.WebInterface
//import java.util.*
//
////import com.medhelp.callmed2.ui.video_chat.notifi.MyNotification;
//class VideoChatActivity : AppCompatActivity() {
//    var context: Context? = null
//    var data: VisitItem2? = null
//
//    lateinit var webView: WebView
//
//    var mDecorView: View? = null
//    var callReceiver = CallReceiver()
//    var webViewEasyRtc: WebViewEasyRtc? = null
//    var listener: VideoChatListener? = null
//    private var sp: SoundPool? = null
//    private var endCollSp = 0
//    var mediaPlayer: MediaPlayer? = null
//    private var alert: AlertDialog? = null
//    private var timer: Timer? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_video_chat)
//        context = this
//        webView = findViewById(R.id.webView)
//        init()
//    }
//
//    private fun init() {
//        data = intent.extras!!.getParcelable(VisitItem2::class.java.canonicalName)
//        mDecorView = window.decorView
//        val filter = IntentFilter()
//        filter.addAction("android.intent.action.PHONE_STATE")
//        this.registerReceiver(callReceiver, filter)
//        callReceiver.setListener(object : CallReceiver.CallReceiverListener {
//            override fun beginCallPhone() {
//                if (webViewEasyRtc != null) {
//                    webViewEasyRtc!!.enableMicrophone(false)
//                    webViewEasyRtc!!.enableCamera(false)
//                }
//            }
//
//            override fun endCallPhone() {
//                if (webViewEasyRtc != null) {
//                    if (webViewEasyRtc!!.getWebInterface().isResume) {
//                        webViewEasyRtc!!.enableMicrophone(true)
//                        webViewEasyRtc!!.enableCamera(true)
//                    }
//                }
//            }
//        })
//        initEasyListener()
//        val webView = findViewById<WebView>(R.id.webView)
//        webViewEasyRtc = WebViewEasyRtc.getInstance()
//        webViewEasyRtc?.setWebView(context, webView)
//        webViewEasyRtc?.setListener(listener)
//        webViewEasyRtc?.setDataForVideoChat(
//            intent.getParcelableExtra(VisitItem2::class.java.canonicalName), intent.extras!!
//                .getString("companionid")
//        )
//        webViewEasyRtc?.setWebInterface(WebInterface.getInstance())
//        sp = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
//        webViewEasyRtc?.initWeb()
//        endCollSp = sp!!.load(this, R.raw.end_call, 1)
//    }
//
//    fun stopMusic() {
//        if (mediaPlayer != null) {
//            try {
//                mediaPlayer!!.release()
//                mediaPlayer = null
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    override fun onWindowFocusChanged(hasFocus: Boolean) {   // режим погружения
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            mDecorView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        }
//    }
//
//    fun startVibrations() {
//        val vibrator = applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
//        val pattern = longArrayOf(1000, 1000, 1000, 1000)
//        if (vibrator.hasVibrator()) {
//            vibrator.vibrate(pattern, 0)
//        }
//    }
//
//    fun stopVibrations() {
//        val vibrator = applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
//        vibrator.cancel()
//    }
//
//    var isClickBack = false
//    override fun onBackPressed() {
//        isClickBack = true
//        if (webViewEasyRtc!!.currentDivShow == WebViewEasyRtc.VIDEO) {
//            Log.wtf("timber", "onBackPressed")
//        }
//        if (webViewEasyRtc!!.currentDivShow == WebViewEasyRtc.CALL_INCOMING) {
//            webViewEasyRtc!!.incomingPhoneDown()
//        } else {
//            try {
//                webViewEasyRtc!!.stopVideoForBack()
//            } catch (e: Exception) {
//                Log.wtf("mLog", "catch " + e.message)
//            }
//        }
//        onBack()
//    }
//
//    fun onBack() {
//        stopAudioVibroTimer()
//        isClickBack = true
//        val intent = Intent(context, MainPageActivity::class.java)
//        intent.putExtra(
//            Constants.KEY_FOR_INTENT_POINTER_TO_PAGE,
//            MainPageActivity.MENU_ONLINE_CONSULTATION
//        )
//        startActivity(intent)
//        finish()
//    }
//
//    private fun stopAudioVibroTimer() {
//        stopMusic()
//        webViewEasyRtc!!.stopTimer()
//    }
//
//    override fun onPause() {
//        stopAudioVibroTimer()
//        if (!isClickBack) {
//            if (webViewEasyRtc!!.currentDivShow == WebViewEasyRtc.VIDEO) {
//                Log.wtf("timber", "onPause")
//                webViewEasyRtc!!.enableCamera(false)
//            }
//            if (webViewEasyRtc!!.currentDivShow == WebViewEasyRtc.VIDEO) {
//                //  webViewEasyRtc.stopOnPauseStateActivityForDoc();
//            } else {
//                webViewEasyRtc!!.stopCall()
//            }
//        }
//        if (webViewEasyRtc!!.getWebInterface() != null) webViewEasyRtc!!.getWebInterface()
//            .setStateActivity(false)
//        super.onPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        stopVibrations()
//        webViewEasyRtc!!.enableMicrophone(true)
//        webViewEasyRtc!!.enableCamera(true)
//        if (webViewEasyRtc!!.getWebInterface() != null) webViewEasyRtc!!.getWebInterface()
//            .setStateActivity(true)
//    }
//
//    override fun onDestroy() {
//        webViewEasyRtc!!.onDestroy()
//        finish()
//        unregisterReceiver(callReceiver)
//        super.onDestroy()
//    }
//
//    fun endOfResponseTime() {   //недозвон, пациент не ответил в установленное время
//        runOnUiThread {
//            webViewEasyRtc!!.stopCall()
//            stopMusic()
//            sp!!.play(endCollSp, 1f, 1f, 0, 0, 1f)
//            webViewEasyRtc!!.stopTimer()
//            timer = Timer()
//            timer!!.schedule(object : TimerTask() {
//                override fun run() {
//                    //выполняется втом же потоке что и таймер
//                    onBack()
//                }
//            }, 1000)
//        }
//    }
//
//    fun showAlert() {
//        val str = "Выйти?"
//        val inflater = layoutInflater
//        val view = inflater.inflate(R.layout.dialog_2textview_btn, null)
//        val title = view.findViewById<TextView>(R.id.title)
//        val text = view.findViewById<TextView>(R.id.text)
//        val btnYes = view.findViewById<Button>(R.id.btnYes)
//        val btnNo = view.findViewById<Button>(R.id.btnNo)
//        title.text = Html.fromHtml("<u>Подтвердите действие</u>")
//        text.text = str
//        btnYes.setOnClickListener { v: View? ->
//            if (context != null) (context as VideoChatActivity).isClickBack = true
//            if (webViewEasyRtc!!.currentDivShow == WebViewEasyRtc.VIDEO) {
//                webViewEasyRtc!!.stopVideoForBack()
//            }
//            alert!!.dismiss()
//            if (context != null) (context as VideoChatActivity).onBack()
//        }
//        btnNo.visibility = View.VISIBLE
//        btnNo.setOnClickListener { alert!!.dismiss() }
//        val builder = AlertDialog.Builder(context)
//        builder.setView(view)
//        alert = builder.create()
//        alert?.setCanceledOnTouchOutside(false)
//        alert?.show()
//    }
//
//    fun closeNotificationAboutCall() {
////        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////        notificationManager.cancel(MyNotification.ID_NOTI_FOR_VIDEO_CALL);
//    }
//
//    fun showNotification(
//        id: String?,
//        name: String?,
//        duration: String?,
//        timeStart: String?,
//        companionid: String?,
//        callerName: String?
//    ) {
//        startVibrations()
//
//        //new MyNotification(context, companionid,callerName);
//        data!!.executeTheScenario = Constants.SCENARIO_INCOMING_WAIT
//        data!!.durationSec = Integer.valueOf(duration)
//        val intOpen = Intent(context, VideoChatActivity::class.java)
//        intOpen.putExtra(VisitItem2::class.java.canonicalName, data)
//        intOpen.putExtra("companionid", companionid)
//        startActivity(intOpen)
//    }
//
//    fun playLongBell() {
//        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer.create(this, R.raw.long_beebs)
//            mediaPlayer?.setLooping(true)
//            mediaPlayer?.start()
//            mediaPlayer?.setOnCompletionListener(OnCompletionListener { })
//        }
//    }
//
//    private fun initEasyListener() {
//        listener = object : VideoChatListener {
//            override fun closeNotification() {
//                runOnUiThread { closeNotificationAboutCall() }
//            }
//
//            override fun showNotifications(
//                id: String?,
//                name: String?,
//                duration: String?,
//                timeStart: String?,
//                companionid: String?,
//                callerName: String?
//            ) {
//                runOnUiThread {
//                    showNotification(
//                        id,
//                        name,
//                        duration,
//                        timeStart,
//                        companionid,
//                        callerName
//                    )
//                }
//            }
//
//            override fun showToastMsg(msg: String?) {
//                runOnUiThread { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
//            }
//
//            override fun callOnBack() {
//                runOnUiThread { onBack() }
//            }
//
//            override fun startVibration() {
//                runOnUiThread { startVibrations() }
//            }
//
//            override fun stopVibration() {
//                runOnUiThread { stopVibrations() }
//            }
//
//            override fun stopMusics() {
//                runOnUiThread { stopMusic() }
//            }
//
//            override fun playLongBells() {
//                runOnUiThread { playLongBell() }
//            }
//
//            override fun showAlerts() {
//                runOnUiThread { showAlert() }
//            }
//
//            override fun setIsClickBack(boo: Boolean) {
//                runOnUiThread { isClickBack = boo }
//            }
//
//            override fun activityOnBack() {
//                runOnUiThread { onBack() }
//            }
//
//            override fun endOfResponseTimes() {
//                runOnUiThread { endOfResponseTime() }
//            }
//        }
//    }
//
//    interface VideoChatListener {
//        fun closeNotification()
//        fun showNotifications(
//            id: String?,
//            name: String?,
//            duration: String?,
//            timeStart: String?,
//            companionid: String?,
//            callerName: String?
//        )
//
//        fun showToastMsg(msg: String?)
//        fun callOnBack()
//        fun startVibration()
//        fun stopVibration()
//        fun stopMusics()
//        fun showAlerts()
//        fun setIsClickBack(boo: Boolean)
//        fun activityOnBack()
//        fun endOfResponseTimes()
//        fun playLongBells()
//    }
//}