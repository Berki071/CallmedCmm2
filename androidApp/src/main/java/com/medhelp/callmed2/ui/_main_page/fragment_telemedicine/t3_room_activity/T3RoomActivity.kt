package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.bg.service.MyFirebaseMessagingService
import com.medhelp.callmed2.data.bg.service.PadForMyFirebaseMessagingService
import com.medhelp.callmed2.databinding.ActivityT31RoomBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowFileTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowImageTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.ShowMediaTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.bottom_bar_chat.BottomBarChatView
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat.recy.RoomAdapter
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.main.MainUtils
import timber.log.Timber
import java.io.File
import android.os.PowerManager
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes.ShowAnalyzesAlert
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_conclusions.ShowConclusionsAlert
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.recy_chat.RecyChatView
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem


class T3RoomActivity: AppCompatActivity(){

    lateinit var binding: ActivityT31RoomBinding
    lateinit var presenter: T3RoomPresenter

    var recordItem: AllRecordsTelemedicineItem? = null
    var whatDataShow: String? = null  // для Т1 что бы знать что показывать


    var mCurrentFilePath: Uri? = null

    var sensorManager: SensorManager? = null
    var proximitySensor: Sensor? = null
    var proximitySensorEventListener: SensorEventListener? = null
    var stateProximity: ProximitySensorState = ProximitySensorState.AWAY
        set(value) {
            field = value
            if(listenerStateProximity != null){
                listenerStateProximity?.changeState(field)
            }

            if(field == ProximitySensorState.NEAR){
                // Enable : Acquire the lock if it was not already acquired
                if(!lock.isHeld) lock.acquire()
            }else{
                // Disable : Release the lock if it was not already released
                if(lock.isHeld) lock.release()
            }
        }
    var listenerStateProximity: RoomAdapter.ListenerStateProximity? = null

    private lateinit var powerManager: PowerManager
    private lateinit var lock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("my").i("Комната с доктором")

        //setVolumeControlStream(AudioManager.STREAM_MUSIC)

        binding = ActivityT31RoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseInit()

        val intent = intent
        if (intent != null) {
            val gson = Gson()
            whatDataShow = intent.getStringExtra("whatDataShow")

            val str = intent.getStringExtra("recItem")
            if(str!=null) {
                recordItem = gson.fromJson(str, AllRecordsTelemedicineItem::class.java)
                presenter.getOneRecordInfo(recordItem!!.idRoom.toString(), recordItem!!.tmId.toString())
                //setUp()
            }else{
                val idRoom = intent.getStringExtra("idRoom")
                val idTm = intent.getStringExtra("idTm")
                if (idRoom != null && idTm != null) {
                    presenter.getOneRecordInfo(idRoom, idTm)
                }
            }
        }

        initView()
        initProximitySensor()

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        lock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,"callmed2:tag")

        binding.recyCustom.onCreateMainView()
    }
    fun baseInit(){
        binding.timer.visibility  = View.GONE
        binding.videoPreviewContainer.visibility = View.GONE

        presenter = T3RoomPresenter(this)

        val kvl = object : KeyboardVisibilityListener {
            override fun onKeyboardVisibilityChanged(keyboardVisible: Boolean) {
                if (keyboardVisible) {
                    binding.recyCustom.recyScrollToStart()
                }
            }
        }
        setKeyboardVisibilityListener(this, kvl)
    }
    fun initView(){
        binding.bottomBarChat.listener = object : BottomBarChatView.BottomBarChatViewListener {
            override fun sendMessageToServer(idSotr: String, item: MessageRoomItem, idFilial: String) {
                if(item.type != MsgRoomType.VIDEO.toString() )
                    presenter.sendMessageToServer(idSotr, item, idFilial)
                else{
                    presenter.videoToJsonObjWithBase64(idSotr, item, idFilial)
                }
            }

            override fun getNewUriForNewFile(idRoom: String, extensionF: String, idMessage: String?, timeMillis: String?): Pair<Uri, File>? {
                return binding.recyCustom.presenterItems.getNewUriForNewFile(idRoom, extensionF, idMessage, timeMillis)
            }

            override fun setMCurrentFilePath(uri: Uri?) {
                mCurrentFilePath = uri
            }

            override fun getRecordItem(): AllRecordsTelemedicineItem? {
                return recordItem
            }

        }

        binding.recyCustom.setData(object: RecyChatView.RecyChatViewListener{
            override fun clickedShowBigImage(item: MessageRoomItem, list : MutableList<MessageRoomResponse.MessageRoomItem>) {
                val dialog = ShowImageTelemedicineDf()
                dialog.setData(item, list)
                dialog.show(supportFragmentManager, ShowImageTelemedicineDf::class.java.canonicalName)
            }

            override fun clickedShowFile(item: MessageRoomItem) {
                val dialog = ShowFileTelemedicineDf()
                dialog.uriFile = Uri.parse(item.text)
                dialog.show(supportFragmentManager, ShowFileTelemedicineDf::class.java.canonicalName)
            }

            override fun getRecordItem(): AllRecordsTelemedicineItem? {
                return recordItem
            }

            override fun getStateProximity(): ProximitySensorState {
                return stateProximity
            }

            override fun setListenerStateProximity(item: RoomAdapter.ListenerStateProximity?) {
                listenerStateProximity = item
            }

            override fun getListenerStateProximity(): RoomAdapter.ListenerStateProximity? {
                return listenerStateProximity
            }
        })
    }
    fun initProximitySensor(){
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor != null) {
            proximitySensorEventListener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // method to check accuracy changed in sensor.
                    //Log.wtf("adfasdasdf",">>> other")
                }

                override fun onSensorChanged(event: SensorEvent) {
                    // check if the sensor type is proximity sensor.
                    if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                        if (event.values[0] == 0f) {
                            //("Near")
                            stateProximity = ProximitySensorState.NEAR
                        } else {
                            //("Away")
                            stateProximity = ProximitySensorState.AWAY
                        }
                    }
                }
            }
            sensorManager?.registerListener(proximitySensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

    }

    //region audio volume click key
    //AudioManager.MODE_IN_COMMUNICATION звук во время прослушивания регулируется только так
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        super.onKeyDown(keyCode, event)
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val am = getSystemService(AUDIO_SERVICE)  as AudioManager
            am?.let{
                if(it.mode == AudioManager.MODE_IN_COMMUNICATION){
                    val volumeLevel: Int = am?.getStreamVolume(AudioManager.MODE_IN_COMMUNICATION) ?: 0
                    val max: Int = am?.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION) ?: 0

                    val step: Int = if(max == 0) 0 else (max * 0.2).toInt()
                    val tmpNewV = volumeLevel+step
                    val newValue = if(tmpNewV>max) max else tmpNewV

                    am?.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, newValue, 0)
                    Log.wtf("adfasdasdf", "KEYCODE_VOLUME_UP $newValue")
                    return true
                }
            }
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            val am = getSystemService(AUDIO_SERVICE)  as AudioManager
            am?.let{
                if(it.mode == AudioManager.MODE_IN_COMMUNICATION){
                    val volumeLevel: Int = am?.getStreamVolume(AudioManager.MODE_IN_COMMUNICATION) ?: 0
                    val max: Int = am?.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION) ?: 0

                    val step: Int = if(max == 0) 0 else (max * 0.2).toInt()
                    val tmpNewV = volumeLevel-step
                    val newValue = if(tmpNewV<=0) 0 else tmpNewV

                    am?.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, newValue, 0)
                    Log.wtf("adfasdasdf", "KEYCODE_VOLUME_DOWN $newValue")
                    return true
                }
            }
        }

        return false
    }
//endregion

    override fun onPause() {
        super.onPause()

        // Disable : Release the lock if it was not already released
        if(lock.isHeld) lock.release()
    }

    fun setUp() {
        setSupportActionBar(binding.toolbar)

        if(recordItem!=null && recordItem!!.status == Constants.TelemedicineStatusRecord.active.toString()){
            setVisibilityBottomBarChat(View.GONE)
        }else
            setVisibilityBottomBarChat(View.VISIBLE)

        setInfoToolbar()
        recordItem?.let{
            binding.recyCustom.setUp(it.idRoom!!)
            PadForMyFirebaseMessagingService.showIdRoom = it.idRoom.toString()
            PadForMyFirebaseMessagingService.listener = object: MyFirebaseMessagingService.MyFirebaseMessagingServiceListener{
                override fun updateRecordInfo() {
                    presenter.getOneRecordInfo(it.idRoom.toString(), it.tmId.toString())
                }
            }
        }
        clearChatNotification()

        checkTimer()
    }

    fun initRecy(listMsg: MutableList<MessageRoomItem>){
        binding.recyCustom.initRecy(listMsg)
    }

    var timerTimeStop = 0L
    var currentTimePhoneForProcessing = 0L
    fun checkTimer(){
        if(recordItem!=null && recordItem!!.dataStart!=null && recordItem!!.status == Constants.TelemedicineStatusRecord.active.toString()) {

            currentTimePhoneForProcessing = MDate.getCurrentDate()
            val currentTimeServerLong: Long = MDate.stringToLong(recordItem!!.dataServer!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss) ?: 0L
            var differenceCurrentTime = currentTimeServerLong - currentTimePhoneForProcessing

            val dateStartLong: Long = MDate.stringToLong(recordItem!!.dataStart!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss) ?: 0L
            val dateEndLong: Long = dateStartLong + (recordItem!!.tmTimeForTm!!.toLong()*60*1000)

            if(dateEndLong<currentTimeServerLong)
                binding.timer.visibility = View.GONE
            else{
                binding.timer.visibility = View.VISIBLE
                timerTimeStop = dateEndLong-differenceCurrentTime  //differenceCurrentTime для приведения к времени устройства
                startTimer()
            }
        }else{
            binding.timer.visibility = View.GONE
        }
    }

    fun setVisibilityBottomBarChat(boo: Int){ //View.VISIBLE...
        binding.bottomBarChat.disableChat.visibility = boo
    }
    fun startTimer(){
        val currentTimePhone = MDate.getCurrentDate()
        if(currentTimePhone >= timerTimeStop){
            Timber.tag("my").d("T3RoomActivity closeTm startTimer currentTimePhone:${MDate.longToString(currentTimePhone,MDate.DATE_FORMAT_ddMMyyyy_HHmmss)}, " +
                    "timerTimeStop:${MDate.longToString(timerTimeStop,MDate.DATE_FORMAT_ddMMyyyy_HHmmss)}, " +
                    "item.dataServe:${recordItem?.dataServer}, item.dataStart:${recordItem?.dataStart!!}, item.tmTimeForTm:${recordItem?.tmTimeForTm}, " +
                    "currentTimePhoneForProcessing:${currentTimePhoneForProcessing}, tmId:${recordItem?.tmId}")

            binding.timer.visibility = View.GONE
            presenter.closeRecordTelemedicine(recordItem!!)
            return
        }

        val timeLeft = timerTimeStop-currentTimePhone

        val timeLeftSec = timeLeft/1000
        val sec = timeLeftSec % 60
        val minAll = timeLeftSec / 60
        val min = minAll % 60
        val hour = minAll / 60

        var str = if (hour==0L) "" else hour.toString()+":"
        var minStr = min.toString()
        if(minStr.length==1)
            minStr = "0"+minStr
        str += if(minStr.isEmpty()) minStr else minStr+":"

        str += if(sec<10 ) "0"+sec.toString() else sec.toString()
        binding.timer.text = str

        val handler = Handler()
        val delay = 1000 //milliseconds
        handler.postDelayed({
            startTimer()
        }, delay.toLong())
    }

    //region toolbar

    var startReception: MenuItem?=null
    var completeReception: MenuItem?=null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = getMenuInflater ()
        inflater.inflate(R.menu.menu_info_telemedicine, menu)

        menu?.let{
            startReception = it.findItem(R.id.startReception)
            completeReception = it.findItem(R.id.completeReception)
            checkShowMenuItems()
        }
        return true
    }
    private fun setInfoToolbar() {
        recordItem?.let{
            binding.title.text = it.fullNameKl
            var idSpace = it.fullNameKl!!.indexOf(" ")
            idSpace = if(idSpace>0) idSpace else 1
            val tmpStr = it.fullNameKl!!.substring(0,1)+it.fullNameKl!!.substring(idSpace+1,idSpace+2)
            binding.ico.setImageBitmap(MainUtils.creteImageIco(this, tmpStr, "Telemedicine"))
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.media -> {
                showMedia()
                true
            }

            R.id.startReception -> {
                Different.showAlertInfo(this, "Внимание!", "Вы действительно хотите начать прием?",object: Different.AlertInfoListener{
                    override fun clickOk() {
                        recordItem?.let {
                            if(it.status == Constants.TelemedicineStatusRecord.wait.toString() && it.statusPay != "true"){
                                Different.showAlertInfo(this as AppCompatActivity, "Внимание!", "Прием не оплачен")
                                return
                            }
                            presenter.toActiveRecordTelemedicine(it)
                        }
                    }
                    override fun clickNo() {}
                },true,"нет","да")

                true
            }
            R.id.completeReception -> {
                Different.showAlertInfo(this, "Внимание!", "Вы действительно хотите завершить прием? ",object: Different.AlertInfoListener{
                    override fun clickOk() {
                        Timber.tag("my").d("T3RoomActivity closeTm OptionsItemSelected" +
                                "item.dataServe:${recordItem?.dataServer!!}, item.dataStart:${recordItem?.dataStart!!}, item.tmTimeForTm:${recordItem?.tmTimeForTm!!}, tmId:${recordItem?.tmId}")

                        presenter.closeRecordTelemedicine(recordItem!!, true, isDoc=true)
                    }
                    override fun clickNo() {}
                },true,"нет","да")

                true
            }

            R.id.menuShowAnalise -> {
                showAnalyzes()
                true
            }
            R.id.menuShowConclusions -> {
                showConclusions()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showConclusions() {
        val alert = ShowConclusionsAlert()
        alert.recordItem = recordItem!!
        alert.show(supportFragmentManager, ShowConclusionsAlert::class.java.canonicalName!!.toString())
    }
    private fun showAnalyzes() {
        val alert = ShowAnalyzesAlert()
        alert.recordItem = recordItem!!
        alert.show(supportFragmentManager, ShowAnalyzesAlert::class.java.canonicalName!!.toString())
    }

    fun showMedia(){
        recordItem?.let {
            val dialogDoc = ShowMediaTelemedicineDf()
            dialogDoc.recItem = it
            dialogDoc.listener = object : ShowMediaTelemedicineDf.ShowMediaTelemedicineListener{
                override fun deleteFile(uriStringFile: String) {
                    binding.recyCustom.adapter?.let{
                        it.updateItemByPathFileUri(uriStringFile)
                    }
                }
            }
            dialogDoc.show(supportFragmentManager, ShowMediaTelemedicineDf::class.java.canonicalName)
        }
    }

    fun checkShowMenuItems(){
        recordItem?.let{
            if(it.status == Constants.TelemedicineStatusRecord.active.toString()){
                startReception?.isVisible = false
                completeReception?.isVisible = true
            }else{
                startReception?.isVisible = true
                completeReception?.isVisible = false
            }
        }
    }
    //endregion

    private fun takeAPhoto() {
        if (permissionGranted()) {
            val isCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
            if (isCamera == null || !isCamera) {
                Toast.makeText(this, "Нет камеры", Toast.LENGTH_LONG).show()
                return
            }

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val photoURI = binding.recyCustom.presenterItems.getNewUriForNewFile(recordItem!!.idRoom.toString(), "jpg")?.first

                photoURI?.let {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                    startActivityForResult(takePictureIntent, KEY_FOR_CAMERA_PHOTO)

                    mCurrentFilePath = photoURI
                }
            }
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == KEY_FOR_SELECT_DOC && resultCode == RESULT_OK) {
            if (data != null) {
                val contentURI: Uri? = data.data

                contentURI?.let{
                    val extF = presenter.convertBase64.getExtensionByUri(this, it)

                    if(extF == null){
                        Different.showAlertInfo(this, "Ошибка!", "Не удалось определить расширение файла")
                        return
                    }else if(extF!="jpeg" && extF!="jpg" && extF!="png" && extF!="pdf"){
                        Different.showAlertInfo(this, "Ошибка!", "Извините, такой тип файла не разрешен")
                        return
                    }else {
                        val newFileInCacheUri = binding.recyCustom.presenterItems.getNewUriForNewFile(recordItem?.idRoom.toString(), extF)?.first
                        if (newFileInCacheUri != null) {
                            presenter.convertBase64.copyFileByUri(this, contentURI, newFileInCacheUri)
                            mCurrentFilePath = newFileInCacheUri

                            val typeFile = if (extF == "pdf") MsgRoomType.FILE.toString() else MsgRoomType.IMG.toString()
                            if(typeFile == MsgRoomType.IMG.toString()){
                                presenter.convertBase64.compressImage(this, mCurrentFilePath!!)
                            }

                            recordItem?.let {
                                var msgItem = MessageRoomItem()
                                msgItem.idRoom = it.idRoom!!.toString()
                                msgItem.data = MDate.longToString(MDate.getCurrentDate(), MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
                                msgItem.type = typeFile
                                msgItem.text = mCurrentFilePath!!.toString()
                                msgItem.idTm = it.tmId!!
                                msgItem.otpravitel = "sotr"
                                msgItem.nameTm = it.tmName
                                msgItem.viewKl = "false"
                                msgItem.viewSotr = "true"


                                presenter.sendMessageToServer(it.idKl!!.toString(), msgItem, it.idFilial!!.toString())
                            }
                        }
                    }
                }
            }
        }
        if (requestCode == KEY_FOR_CAMERA_PHOTO) {
            if (resultCode == RESULT_OK) {

                presenter.convertBase64.compressImage(this, mCurrentFilePath!!)

                recordItem?.let {
                    var msgItem = MessageRoomItem()
                    msgItem.idRoom = it.idRoom!!.toString()
                    msgItem.data = MDate.longToString(MDate.getCurrentDate(), MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
                    msgItem.type = MsgRoomType.IMG.toString()
                    msgItem.text = mCurrentFilePath!!.toString()
                    msgItem.idTm = it.tmId!!
                    msgItem.otpravitel = "sotr"
                    msgItem.nameTm = it.tmName
                    msgItem.viewKl = "false"
                    msgItem.viewSotr = "true"

                    presenter.sendMessageToServer(it.idKl!!.toString(), msgItem, it.idFilial!!.toString())
                }

            }else{
                mCurrentFilePath?.let {
                    val contentResolver = contentResolver
                    contentResolver.delete(it, null, null)
                }
            }
        }

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissionGranted()) {
                takeAPhoto()
            } else {
                Different.showAlertInfo(this, "Важно!", "Для использования запрошенных функций необходимо Ваше разрешение", object: Different.AlertInfoListener {
                    override fun clickOk() {
                        ActivityCompat.requestPermissions(this as T3RoomActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                    }

                    override fun clickNo() {}
                }, true)
            }
        }
    }

    private fun permissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun clearChatNotification() {
        if(recordItem!=null && recordItem!!.idRoom!=null && recordItem!!.tmId!=null){
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel((recordItem!!.idRoom!!.toString()+recordItem!!.tmId!!.toString()).toInt())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainPageActivity::class.java)
        intent.putExtra(Constants.KEY_FOR_INTENT_POINTER_TO_PAGE, MainPageActivity.MENU_CHAT_WITH_DOC)
        intent.putExtra("whatDataShow", whatDataShow)
        startActivity(intent)
        finish()
    }
    override fun onDestroy() {
        binding.recyCustom.onDestroyMainView()
        binding.bottomBarChat.onDestroyMainView()

        PadForMyFirebaseMessagingService.showIdRoom = null
        PadForMyFirebaseMessagingService.listener = null

        proximitySensorEventListener?.let {
            sensorManager?.unregisterListener(it)
        }

        super.onDestroy()
    }

    //region show/hide keyboard
    var mAppHeight = 0
    var currentOrientation = -1
    fun setKeyboardVisibilityListener(activity: Activity, keyboardVisibilityListener: KeyboardVisibilityListener) {
        val contentView = activity.findViewById<View>(android.R.id.content)
        contentView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            private var mPreviousHeight = 0
            override fun onGlobalLayout() {
                val newHeight = contentView.height
                if (newHeight == mPreviousHeight) return
                mPreviousHeight = newHeight
                if (activity.resources.configuration.orientation != currentOrientation) {
                    currentOrientation = activity.resources.configuration.orientation
                    mAppHeight = 0
                }
                if (newHeight >= mAppHeight) {
                    mAppHeight = newHeight
                }
                if (newHeight != 0) {
                    if (mAppHeight > newHeight) {
                        // Height decreased: keyboard was shown
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(true)
                    } else {
                        // Height increased: keyboard was hidden
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(false)
                    }
                }
            }
        })
    }

    interface KeyboardVisibilityListener {
        fun onKeyboardVisibilityChanged(keyboardVisible: Boolean)
    }
    //endregion

    enum class MsgRoomType (val id: Int){
        DATE(0),TARIFF(1), TEXT(2), IMG(3), FILE(4), REC_AUD(5), VIDEO(6)
    }

    enum class ProximitySensorState{
        NEAR,AWAY
    }
    companion object {
        const val FOLDER_TELEMEDICINE = "TELEMEDICINE"
        const val PREFIX_NAME_FILE = "telemedicine"

        const val KEY_FOR_SELECT_DOC = 108
        const val KEY_FOR_CAMERA_PHOTO = 107

        const val REQUEST_CODE_PERMISSIONS = 121
        val REQUIRED_PERMISSIONS =
            mutableListOf (Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}