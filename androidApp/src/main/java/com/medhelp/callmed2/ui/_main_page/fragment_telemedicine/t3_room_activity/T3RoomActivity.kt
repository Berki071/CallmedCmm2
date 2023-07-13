package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.bg.service.MyFirebaseMessagingService
import com.medhelp.callmed2.data.bg.service.PadForMyFirebaseMessagingService
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import com.medhelp.callmed2.databinding.ActivityT31RoomBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowFileTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowImageTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.ShowMediaTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.T1ListOfEntriesFragment
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.recy.RoomAdapter
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmed2.utils.main.MainUtils
import com.medhelp.callmedcmm2.db.RealmDb
import timber.log.Timber


class T3RoomActivity: AppCompatActivity() {

    lateinit var binding: ActivityT31RoomBinding
    lateinit var presenter: T3RoomPresenter

    var recordItem: AllRecordsTelemedicineItem? = null
    var whatDataShow: String? = null  // для Т1 что бы знать что показывать
    var adapter: RoomAdapter? = null

    private val scroll: Parcelable? = null

    var ocl = View.OnClickListener { v: View ->
        when (v.id) {
            R.id.sendMsg -> sendMessage()
            R.id.takeAPhoto ->  takeAPhoto()
            R.id.openLibraryPhoto -> openLibraryPhoto()
            R.id.disableChat ->{
                Different.showAlertInfo(this, "Внимание!", "Нажмите начать прием в меню(вверху справа три точки) для разблокировки чата")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("my").i("Комната с доктором")

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

    }

    fun baseInit(){
        binding.disableChat.visibility = View.GONE
        binding.timer.visibility  = View.GONE

        presenter = T3RoomPresenter(this)

        binding.sendMsg.setOnClickListener(ocl)
        binding.takeAPhoto.setOnClickListener(ocl)
        binding.openLibraryPhoto.setOnClickListener(ocl)
        binding.disableChat.setOnClickListener(ocl)

        val kvl = object : KeyboardVisibilityListener {
            override fun onKeyboardVisibilityChanged(keyboardVisible: Boolean) {
                if (keyboardVisible) {
                    recyScrollToStart()
                }
            }
        }
        setKeyboardVisibilityListener(this, kvl)

        binding.editT.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                testShowPhotoButtons(s.toString())
            }
        })
    }

    fun setUp() {
        setSupportActionBar(binding.toolbar)

        if(recordItem!=null && recordItem!!.status == Constants.TelemedicineStatusRecord.active.toString()){
            binding.disableChat.visibility = View.GONE
        }else
            binding.disableChat.visibility = View.VISIBLE

        setInfoToolbar()
        recordItem?.let{
            presenter.getAllMessageFromRealm(it.idRoom!!)
            presenter.getNewMessagesInLoopFromServer(it.idRoom!!.toString())
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

    public override fun onResume() {
        super.onResume()
        //checkTimer()
    }

    var timerTimeStop = 0L
    fun checkTimer(){
        if(recordItem!=null && recordItem!!.dataStart!=null && recordItem!!.status == Constants.TelemedicineStatusRecord.active.toString()) {

            val currentTimePhone = MDate.getCurrentDate()
            val currentTimeServerLong: Long = MDate.stringToLong(recordItem!!.dataServer!!, MDate.DATE_FORMAT_ddMMyyyy_HHmmss) ?: 0L
            var differenceCurrentTime = currentTimeServerLong - currentTimePhone

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
    fun startTimer(){
        val currentTimePhone = MDate.getCurrentDate()
        if(currentTimePhone >= timerTimeStop){
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
                        presenter.closeRecordTelemedicine(recordItem!!, true, isDoc=true)
                    }
                    override fun clickNo() {}
                },true,"нет","да")

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showMedia(){
        recordItem?.let {
            val dialogDoc = ShowMediaTelemedicineDf()
            dialogDoc.recItem = it
            dialogDoc.listener = object : ShowMediaTelemedicineDf.ShowMediaTelemedicineListener{
                override fun deleteFile(uriStringFile: String) {
                    adapter?.let{
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


    var latchScrollToEnd = true
    fun initRecy(listMsg: MutableList<MessageRoomItem>) {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        linearLayoutManager.setReverseLayout(true)


        adapter = RoomAdapter(this, listMsg, object : RoomAdapter.RecyListener {
            override fun finishLoading() {
                if (latchScrollToEnd) {
                    if (scroll == null)
                        recyScrollToStart();
                    else
                        binding.recy.getLayoutManager()?.onRestoreInstanceState(scroll);
                }
            }

            override fun clickedShowBigImage(item: MessageRoomItem) {
                val dialog = ShowImageTelemedicineDf()
                dialog.setData(item, adapter!!.getAllMessageWithImage())
                dialog.show(
                    supportFragmentManager,
                    ShowImageTelemedicineDf::class.java.canonicalName
                )
            }

            override fun clickedShowFile(item: MessageRoomItem) {
                val dialog = ShowFileTelemedicineDf()
                dialog.uriFile = Uri.parse(item.text)
                dialog.show(
                    supportFragmentManager,
                    ShowFileTelemedicineDf::class.java.canonicalName
                )
            }

            override fun clickLongClick(item: MessageRoomItem?): Boolean {
                item?.let {

                    val isPossibleDeleteCheckMsgUserAfterSelect = RealmDb.isPossibleDeleteCheckMsgUserAfterSelect(item)

                    if(isPossibleDeleteCheckMsgUserAfterSelect) {
                        if (it.otpravitel == "sotr" && recordItem!!.status != Constants.TelemedicineStatusRecord.complete.toString() /*&& it.viewKl == "false"*/) {
                            val msg =
                                if (it.type == MsgRoomType.TEXT.toString()) "Удалить собщение \"" + it.text + "\"?" else "Удалить файл?"

                            Different.showAlertInfo(
                                this@T3RoomActivity, "Удаление!", msg,
                                object : Different.AlertInfoListener {
                                    override fun clickOk() {
                                        presenter.deleteMessageFromServer(it)
                                    }

                                    override fun clickNo() {}
                                }, true
                            )
                            return true
                        }
                    }
                }
                return false
            }

        }, binding.recy)

        binding.recy.setLayoutManager(linearLayoutManager)
        binding.recy.setAdapter(adapter);

        recyScrollToStart()

    }

    private fun recyScrollToStart() {
        if (adapter == null)
            return

        binding.recy.scrollToPosition(0)
    }


    private fun sendMessage() {
        hideKeyboard();
        val msg = binding.editT.getText().toString();
        binding.editT.setText("");

        val trimMsg = msg.trim { it <= ' ' }.length > 0

        if(trimMsg) {
            recordItem?.let {
                var msgItem = MessageRoomItem()
                msgItem.idRoom = it.idRoom!!.toString()
                msgItem.data = MDate.longToString(MDate.getCurrentDate(),MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
                msgItem.type = MsgRoomType.TEXT.toString()
                msgItem.text = msg
                msgItem.otpravitel = "sotr"
                msgItem.idTm = it.tmId!!
                msgItem.nameTm = it.tmName
                msgItem.viewKl = "false"
                msgItem.viewSotr = "true"

                presenter.sendMessageToServer(it.idKl!!.toString(), msgItem, it.idFilial!!.toString())
            }
        }
    }

    private fun openLibraryPhoto() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)

        galleryIntent.type = "*/*"
        val mimetypes = arrayOf("image/jpeg", "image/png", "application/pdf")
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

        startActivityForResult(galleryIntent, KEY_FOR_SELECT_DOC)
    }

    var mCurrentFilePath: Uri? = null
    private fun takeAPhoto() {
        if (permissionGranted()) {
            val isCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
            if (isCamera == null || !isCamera) {
                Toast.makeText(this, "Нет камеры", Toast.LENGTH_LONG).show()
                return
            }

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val photoURI = presenter!!.getNewUriForNewFile(recordItem!!.idRoom.toString(), "jpg")

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
                        val newFileInCacheUri = presenter.getNewUriForNewFile(recordItem?.idRoom.toString(), extF)
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
                                msgItem.viewKl = "true"
                                msgItem.viewSotr = "false"


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
                    msgItem.viewKl = "true"
                    msgItem.viewSotr = "false"

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

    private fun testShowPhotoButtons(s: String) {
        if (s.trim { it <= ' ' }.length > 0) {
            binding.takeAPhoto.visibility = View.GONE
            binding.openLibraryPhoto.visibility = View.GONE
        } else {
            binding.takeAPhoto.visibility = View.VISIBLE
            binding.openLibraryPhoto.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainPageActivity::class.java)
        intent.putExtra(Constants.KEY_FOR_INTENT_POINTER_TO_PAGE, MainPageActivity.MENU_CHAT_WITH_DOC)
        intent.putExtra("whatDataShow", whatDataShow)
        startActivity(intent)
        finish()
    }
    override fun onDestroy() {
        PadForMyFirebaseMessagingService.showIdRoom = null
        PadForMyFirebaseMessagingService.listener = null
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

    fun hideKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    interface KeyboardVisibilityListener {
        fun onKeyboardVisibilityChanged(keyboardVisible: Boolean)
    }
    //endregion

    public enum class MsgRoomType (val id: Int){
        DATE(0),TARIFF(1), TEXT(2), IMG(3), FILE(4)
    }
    companion object {
        const val FOLDER_TELEMEDICINE = "TELEMEDICINE"
        const val PREFIX_NAME_FILE = "telemedicine"

        private const val KEY_FOR_SELECT_DOC = 108
        private const val KEY_FOR_CAMERA_PHOTO = 107

        private const val REQUEST_CODE_PERMISSIONS = 121
        private val REQUIRED_PERMISSIONS =
            mutableListOf (Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}