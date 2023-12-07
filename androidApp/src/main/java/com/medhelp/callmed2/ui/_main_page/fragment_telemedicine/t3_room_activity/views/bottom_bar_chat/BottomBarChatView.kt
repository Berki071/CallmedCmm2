package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.bottom_bar_chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.medhelp.callmed2.R
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import java.io.File

class BottomBarChatView : RelativeLayout {
    val MAX_DURATION_OF_ONE_AUDIO_MSG = 180 // в секундах
    val MAX_DURATION_OF_ONE_VIDEO_MSG = 30 // в секундах

    //region constructors
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }
    //endregion

    //region инициализировать перед использованием View
    var listener: BottomBarChatViewListener? = null
    //endregion

    lateinit var editT: EditText
    lateinit var takeAPhoto: ImageButton
    lateinit var openLibraryPhoto: ImageButton
    lateinit var btnAction: BtnActionForChatView
    lateinit var disableChat: LinearLayout
    lateinit var cardBox: ConstraintLayout
    lateinit var forDeleteMsg: TextView

    lateinit var presenter: BottomBarChatPresenter

    val recAudio: BottomBarChatViewRecAudio by lazy {
        BottomBarChatViewRecAudio(context)
    }
    val lazyRecVide = lazy { BottomBarChatViewRecVideo(context, (context as T3RoomActivity).findViewById(R.id.videoPreviewContainer)) }
    val recVideo: BottomBarChatViewRecVideo by lazyRecVide

    var ocl = View.OnClickListener { v: View ->
        when (v.id) {
            R.id.takeAPhoto ->  takeAPhoto()
            R.id.openLibraryPhoto -> openLibraryPhoto()
            R.id.disableChat ->{
                Different.showAlertInfo((context as AppCompatActivity), "Внимание!", "Нажмите начать прием в меню(вверху справа три точки) для разблокировки чата")
            }
        }
    }

    var pointTimerStarted: Long? = null  //время старта аудиозаписи
        set(value){
            field = value
            if(value == null){
                forDeleteMsg.visibility = View.GONE
            }else{
                forDeleteMsg.visibility = View.VISIBLE
            }
        }
    var swipePath: MutableList<Pair<Int,Int>>? = null //координаты движения при записи аудио
    var cardBoxLastTouchState: Pair<Int, Long>? = null



    @SuppressLint("ClickableViewAccessibility")
    private fun init(context: Context) {
        inflate(context, R.layout.view_bottom_bar_chat, this)
        editT = findViewById(R.id.editT)
        takeAPhoto = findViewById(R.id.takeAPhoto)
        openLibraryPhoto = findViewById(R.id.openLibraryPhoto)
        btnAction = findViewById(R.id.btnAction)
        disableChat = findViewById(R.id.disableChat)
        cardBox = findViewById(R.id.cardBox)
        forDeleteMsg = findViewById(R.id.forDeleteMsg)

        takeAPhoto.setOnClickListener(ocl)
        openLibraryPhoto.setOnClickListener(ocl)
        disableChat.setOnClickListener(ocl)

        disableChat.visibility = View.GONE
        forDeleteMsg.visibility = View.GONE

        presenter = BottomBarChatPresenter()

        editT.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                testShowPhotoButtons(s.toString())
            }
        })

        cardBox.setOnTouchListener(object : OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                val eX : Int? = p1?.x?.toInt()
                val eY: Int? = p1?.y?.toInt()

                val offsetViewBounds = Rect()
                btnAction.getDrawingRect(offsetViewBounds)   //returns the visible bounds
                cardBox.offsetDescendantRectToMyCoords(btnAction, offsetViewBounds)   // calculates the relative coordinates to the parent

                val xTop : Int = offsetViewBounds.left
                val yTop : Int = offsetViewBounds.top
                val xBottom : Int = offsetViewBounds.right
                val yBottom : Int = offsetViewBounds.bottom

                if(eX ==null || eY == null){
                    return false
                }

                when (p1?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (eX>=xTop && eX<=xBottom && eY>=yTop && eY <= yBottom){

                            cardBoxLastTouchState = Pair(MotionEvent.ACTION_DOWN, MDate.getCurrentDate())

                            val handler = Handler()
                            val delay = 100 //milliseconds
                            handler.postDelayed({
                                //что бы запустить запись нажатие должно быть более 100 мс
                                if(btnAction.isForRecordState() && cardBoxLastTouchState!!.first == MotionEvent.ACTION_DOWN){
                                    if (isEmptyEditText() ) {
                                        if (btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.AUDIO) {
                                            if (!permissionGrantedRecordAudio()) {
                                                ActivityCompat.requestPermissions((context as AppCompatActivity), arrayOf(android.Manifest.permission.RECORD_AUDIO), 777)
                                                cardBoxLastTouchState = null
                                                return@postDelayed
                                            }
                                        }else if(btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.VIDEO){
                                            if(!permissionGrantedRecordVideo()){
                                                ActivityCompat.requestPermissions((context as AppCompatActivity), REQUIRED_PERMISSIONS, 778)
                                                cardBoxLastTouchState = null
                                                return@postDelayed
                                            }
                                        }

                                        startRecord()
                                    }
                                }
                            }, delay.toLong())

                            return true
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if(swipePath == null)
                            return false

                        swipePath?.add(Pair(eX,eY))
                        checkSwipePathOnCancelAudioMsg(xTop, yTop, xBottom, yBottom)
                    }
                    MotionEvent.ACTION_UP -> {
                        if(pointTimerStarted != null){   //if pointTimerStarted != null идет запись и первод в null ее остановит
                            pointTimerStarted = null
                        }else{
                            if(btnAction.isForRecordState()){

                                //если нажатие менее 100мс то сменить значек

                                val currentTime = MDate.getCurrentDate()
                                if(cardBoxLastTouchState!=null && (currentTime-cardBoxLastTouchState!!.second)<100){
                                    btnAction.clickChangeToNextState()
                                }
                            }else{
                                sendTextMessage()
                            }
                        }

                        cardBoxLastTouchState = Pair(MotionEvent.ACTION_UP, MDate.getCurrentDate())
                        return true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        cardBoxLastTouchState = Pair(MotionEvent.ACTION_CANCEL, MDate.getCurrentDate())
                        pointTimerStarted = null
                    }
                }
                return true
            }
        })

    }

    fun showRecordBtnAnim(){

        btnAction.animate()
            .scaleX(0.8F)
            .scaleY(0.8F)
            .setDuration(500).setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction( Runnable() {
                btnAction.animate()
                    .scaleX(1.2F)
                    .scaleY(1.2F)
                    .setDuration(500).setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction(Runnable() {

                        btnAction.animate()
                            .scaleX(0.8F)
                            .scaleY(0.8F)
                            .setDuration(500).setInterpolator(AccelerateDecelerateInterpolator())
                            .withEndAction(Runnable() {

                                btnAction.clickChangeToNextState()

                                btnAction.animate()
                                    .scaleX(1.2F)
                                    .scaleY(1.2F)
                                    .setDuration(500)
                                    .setInterpolator(AccelerateDecelerateInterpolator())
                                    .withEndAction(Runnable() {

                                        btnAction.animate()
                                            .scaleX(0.8F)
                                            .scaleY(0.8F)
                                            .setDuration(500)
                                            .setInterpolator(AccelerateDecelerateInterpolator())
                                            .withEndAction(Runnable() {

                                                btnAction.clickChangeToNextState()

                                                btnAction.animate()
                                                    .scaleX(1F)
                                                    .scaleY(1F)
                                                    .setDuration(500).setInterpolator(
                                                        AccelerateDecelerateInterpolator()
                                                    )
                                                    .withEndAction(Runnable() {
                                                        btnAction.isShowHint = true
                                                    })
                                            })
                                    })
                            })
                    })
            })
    }

    fun startRecord(){
        if (btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.AUDIO) {

            pointTimerStarted = MDate.getCurrentDate()
            swipePath = mutableListOf()
            listener?.getNewUriForNewFile(listener!!.getRecordItem()!!.idRoom.toString(), "wav")
                ?.let {
                    recAudio.startRecord(it)
                }
            startRepeatShowTimer()

        }else if(btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.VIDEO){


            listener?.getNewUriForNewFile(listener!!.getRecordItem()!!.idRoom.toString(), "mp4")
                ?.let {
                    recVideo?.startRecord(it, object: BottomBarChatViewRecVideo.BottomBarChatViewRecVideoListener {
                        override fun videoRecordStart() {
                            pointTimerStarted = MDate.getCurrentDate()
                            swipePath = mutableListOf()
                            startRepeatShowTimer()
                        }

                        override fun videoRecordFinalizeWithoutError(pathToFileRecord: Pair<Uri, File>) {
                            presenter.createVideoMessage(listener!!.getRecordItem(), pathToFileRecord.first.toString())?.let {
                                listener!!.sendMessageToServer(it.first, it.second, it.third)
                            }
                        }
                    })
                }
        }

    }
    fun stopRecord(){
        if (btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.AUDIO) {
            recAudio.stopRecord()

            if(recAudio.pathToFileRecord != null) {
                presenter.createAudioMessage(listener!!.getRecordItem(), recAudio.pathToFileRecord!!.first.toString())?.let {
                    listener!!.sendMessageToServer(it.first, it.second, it.third)
                }
            }
        }else if(btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.VIDEO){
            recVideo?.stopRecord()
        }
    }
    fun canselRecord(){
        if (btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.AUDIO) {
            recAudio.cancelRecord()
        }else if(btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.VIDEO){
            recVideo?.cancelRecord()
        }
    }


    fun startRepeatShowTimer(){
        if(swipePath == null){
            editT.setText("")
            pointTimerStarted = null
            return
        }

        if(pointTimerStarted == null) {
            swipePath=null
            editT.setText("")

            stopRecord()
            return
        }

        val currentTime: Long = MDate.getCurrentDate()
        val timeHasPassedL: Long = currentTime - pointTimerStarted!!
        val timeHasPassedAllSec: Int = if(timeHasPassedL == 0L) 0 else (timeHasPassedL/1000).toInt()

        if(btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.AUDIO && timeHasPassedAllSec >= MAX_DURATION_OF_ONE_AUDIO_MSG){
            pointTimerStarted = null
            startRepeatShowTimer()
            return
        }else if(btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.VIDEO && timeHasPassedAllSec >= MAX_DURATION_OF_ONE_VIDEO_MSG){
            pointTimerStarted = null
            startRepeatShowTimer()
            return
        }

        val min = (timeHasPassedAllSec / 60).toString()
        val secInt = timeHasPassedAllSec % 60
        val sec = if(secInt<10) "0$secInt" else secInt

        editT.setText("$min:$sec")

        val handler = Handler()
        val delay = 1000 //milliseconds
        handler.postDelayed({
            startRepeatShowTimer()
        }, delay.toLong())
    }

    private fun checkSwipePathOnCancelAudioMsg(xTopBtn : Int, yTopBtn : Int, xBottomBtn : Int, yBottomBtn : Int) {
        if(swipePath == null)
            return

        val offsetViewBoundsForDeleteMsg = Rect()
        forDeleteMsg.getDrawingRect(offsetViewBoundsForDeleteMsg)   //returns the visible bounds
        cardBox.offsetDescendantRectToMyCoords(forDeleteMsg, offsetViewBoundsForDeleteMsg)   // calculates the relative coordinates to the parent

        //верхняя граница вью =0, -20 единиц на погрешность пальца
        if(swipePath!!.size < 2 && swipePath!!.last().second < -20){
            return
        }

        if(swipePath!!.last().first <= forDeleteMsg.left){
            for(i in swipePath!!.size-1 downTo 1){
                if(swipePath!![i].second < -20 || swipePath!![i].first > swipePath!![i-1].first)
                    return

                if(swipePath!![i].first>=xTopBtn && swipePath!![i].first<=xBottomBtn && swipePath!![i].second>=yTopBtn && swipePath!![i].second <= yBottomBtn){
                    //свайп на отмену аудиозаписи нормальный, можно отменять
                    swipePath=null
                    canselRecord()

                    return
                }
            }
        }
    }

    private fun testShowPhotoButtons(s: String) {
        if (s.trim { it <= ' ' }.length > 0) {
            takeAPhoto.visibility = View.GONE
            openLibraryPhoto.visibility = View.GONE
            if(pointTimerStarted == null) {
                btnAction.setState(BtnActionForChatView.BtnActionForChatState.TEXT)
            }else {
                if (btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.TEXT && btnAction.isAllowStateAudioRecord) {
                    btnAction.setState(BtnActionForChatView.BtnActionForChatState.AUDIO)
                }
            }
        } else {
            takeAPhoto.visibility = View.VISIBLE
            openLibraryPhoto.visibility = View.VISIBLE

            if (btnAction.stateBtn == BtnActionForChatView.BtnActionForChatState.TEXT && btnAction.isAllowStateAudioRecord) {
                btnAction.setState(BtnActionForChatView.BtnActionForChatState.AUDIO)
            }
        }
    }
    fun isEmptyEditText(): Boolean {
        val str = editT.text.toString()
        return str.trim { it <= ' ' }.length <= 0
    }

    private fun sendTextMessage() {
        //hideKeyboard();
        val msg = editT.getText().toString()
        editT.setText("")
        val trimMsg = msg.trim { it <= ' ' }.length > 0
        if(trimMsg) {
            presenter.createTextMessage(listener!!.getRecordItem(), msg)?.let {
                listener!!.sendMessageToServer(it.first, it.second, it.third)
            }
        }
    }
    fun hideKeyboard() {
        val view: View? = (context as AppCompatActivity).currentFocus
        if (view != null) {
            val imm = (context as AppCompatActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun takeAPhoto() {
        if (permissionGranted()) {
            val isCamera = (context as AppCompatActivity).packageManager.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)
            if (isCamera == null || !isCamera) {
                Toast.makeText(context, "Нет камеры", Toast.LENGTH_LONG).show()
                return
            }

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity((context as AppCompatActivity).packageManager) != null) {
                val photoURI = listener!!.getNewUriForNewFile(listener!!.getRecordItem()!!.idRoom.toString(), "jpg")?.first

                photoURI?.let {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                    (context as AppCompatActivity).startActivityForResult(takePictureIntent, T3RoomActivity.KEY_FOR_CAMERA_PHOTO)

                    listener!!.setMCurrentFilePath(photoURI)
                }
            }
        }else{
            ActivityCompat.requestPermissions((context as AppCompatActivity),
                T3RoomActivity.REQUIRED_PERMISSIONS,
                T3RoomActivity.REQUEST_CODE_PERMISSIONS
            )
        }
    }
    private fun permissionGranted() = T3RoomActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun permissionGrantedRecordAudio() =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    private fun permissionGrantedRecordVideo() = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun openLibraryPhoto() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)

        galleryIntent.type = "*/*"
        val mimetypes = arrayOf("image/jpeg", "image/png", "application/pdf")
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

        (context as AppCompatActivity).startActivityForResult(galleryIntent, T3RoomActivity.KEY_FOR_SELECT_DOC)
    }

    fun onDestroyMainView(){
        if(lazyRecVide.isInitialized()){
            recVideo.cancelRecord()
            recVideo.context = null
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
    interface BottomBarChatViewListener {
        fun sendMessageToServer(idSotr: String, item: MessageRoomItem, idFilial: String)
        fun getNewUriForNewFile(idRoom: String, extensionF: String, idMessage: String? = null, timeMillis: String? = null) : Pair<Uri, File>?
        fun setMCurrentFilePath(uri: Uri?)
        fun getRecordItem(): AllRecordsTelemedicineItem?
    }
}