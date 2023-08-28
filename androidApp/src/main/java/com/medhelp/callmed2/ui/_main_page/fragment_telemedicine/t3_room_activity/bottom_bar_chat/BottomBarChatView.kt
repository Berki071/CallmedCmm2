package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.bottom_bar_chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomItem
import java.io.File
import java.io.InputStream

class BottomBarChatView : RelativeLayout {

    var isAudioRecordingAllowed = true  // флаг для разрешения аудио записи

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

    lateinit var editT: EditText
    lateinit var takeAPhoto: ImageButton
    lateinit var openLibraryPhoto: ImageButton
    lateinit var sendMsg: ImageButton
    lateinit var disableChat: LinearLayout
    lateinit var cardBox: ConstraintLayout
    lateinit var forDeleteMsg: TextView

    var ocl = View.OnClickListener { v: View ->
        when (v.id) {
            //R.id.sendMsg -> sendMessage()
            R.id.takeAPhoto ->  takeAPhoto()
            R.id.openLibraryPhoto -> openLibraryPhoto()
            R.id.disableChat ->{
                Different.showAlertInfo((context as AppCompatActivity), "Внимание!", "Нажмите начать прием в меню(вверху справа три точки) для разблокировки чата")
            }
        }
    }

    //region инициализировать перед использованием
    var listener: BottomBarChatViewListener? = null
    //endregion

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
    var audioRecorder: MediaRecorder? = null
    var pathToFileRecord: Pair<Uri, File>? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun init(context: Context) {
        inflate(context, R.layout.view_bottom_bar_chat, this)
        editT = findViewById(R.id.editT)
        takeAPhoto = findViewById(R.id.takeAPhoto)
        openLibraryPhoto = findViewById(R.id.openLibraryPhoto)
        sendMsg = findViewById(R.id.sendMsg)
        disableChat = findViewById(R.id.disableChat)
        cardBox = findViewById(R.id.cardBox)
        forDeleteMsg = findViewById(R.id.forDeleteMsg)

        takeAPhoto.setOnClickListener(ocl)
        openLibraryPhoto.setOnClickListener(ocl)
        disableChat.setOnClickListener(ocl)

        disableChat.visibility = View.GONE
        forDeleteMsg.visibility = View.GONE

        if (isAudioRecordingAllowed){
            sendMsg.setImageResource(R.drawable.baseline_mic_24_white)
        }else{
            sendMsg.setImageResource(R.drawable.ic_send_white_24dp)
        }

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
                sendMsg.getDrawingRect(offsetViewBounds)   //returns the visible bounds
                cardBox.offsetDescendantRectToMyCoords(sendMsg, offsetViewBounds)   // calculates the relative coordinates to the parent

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
                            if(isEmptyEditText() && isAudioRecordingAllowed){
                                if(!permissionGrantedRecordAudio()) {
                                    ActivityCompat.requestPermissions((context as AppCompatActivity), arrayOf(android.Manifest.permission.RECORD_AUDIO), 777)
                                    return false
                                }

                                pointTimerStarted = MDate.getCurrentDate()
                                swipePath = mutableListOf()

                                startRecord()
                                startRepeatShowTimer()
                            }

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
                        if(pointTimerStarted != null){
                            pointTimerStarted = null
                        }else{
                            sendMessage()
                        }

                        return true

                    }
                    MotionEvent.ACTION_CANCEL -> {
                        pointTimerStarted = null
                    }
                }
                return true
            }
        })
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

        if(timeHasPassedAllSec >= Constants.MAX_DURATION_OF_ONE_AUDIO_MSG){
            pointTimerStarted = null  //????
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
                    cancelRecord()
                    return
                }
            }
        }
    }

    fun startRecord(){
        Log.wtf("myLogdd", "startRecord")

        pathToFileRecord = listener?.getNewUriForNewFile(listener!!.getRecordItem()!!.idRoom.toString(), "wav") ?: return


        // val p3: String = pathToFileRecord!!.second.absolutePath


        //Log.wtf("","")

        audioRecorder = MediaRecorder().apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            //setOutputFile(file!!.canonicalPath)
            setOutputFile(pathToFileRecord!!.second.absolutePath)
            prepare()
            start()
        }
    }
    private fun getAudioPath(): String {
        return "${context.cacheDir.absolutePath}${File.pathSeparator}${System.currentTimeMillis()}.wav"
    }

    fun stopRecord(){
        audioRecorder?.let {
            Log.wtf("myLogdd", "stopRecord")
            it.stop()
            it.release()
        }
        audioRecorder = null

        val duration = getDuration(pathToFileRecord!!.first)
        if(duration < 1000){
            pathToFileRecord?.let{
                it.second.delete()
            }
            pathToFileRecord = null
            return
        }

        val recordItem = listener!!.getRecordItem()
        recordItem?.let {
            var msgItem = MessageRoomItem()
            msgItem.idRoom = it.idRoom!!.toString()
            msgItem.data = MDate.longToString(MDate.getCurrentDate(),MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
            msgItem.type = T3RoomActivity.MsgRoomType.REC_AUD.toString()
            msgItem.text = pathToFileRecord!!.first.toString()
            msgItem.otpravitel = "sotr"
            msgItem.idTm = it.tmId!!
            msgItem.nameTm = it.tmName
            msgItem.viewKl = "false"
            msgItem.viewSotr = "true"

            listener!!.sendMessageToServer(it.idKl!!.toString(), msgItem, it.idFilial!!.toString())
        }
    }

    fun getDuration(uri: Uri): Long{ //millsec
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }catch (e: Exception){}

        return 0L
    }
    fun cancelRecord(){
        audioRecorder?.let {
            Log.wtf("myLogdd", "cancelRecord")
            it.stop()
            it.release()
        }
        audioRecorder = null

        pathToFileRecord?.let{
            it.second.delete()
        }
        pathToFileRecord = null

        Toast.makeText(context,"Отменено", Toast.LENGTH_SHORT).show()
    }


    private fun testShowPhotoButtons(s: String) {
        if (s.trim { it <= ' ' }.length > 0) {
            takeAPhoto.visibility = View.GONE
            openLibraryPhoto.visibility = View.GONE
            if(pointTimerStarted == null)
                sendMsg.setImageResource(R.drawable.ic_send_white_24dp)
            else {
                if (isAudioRecordingAllowed) {
                    sendMsg.setImageResource(R.drawable.baseline_mic_24_white)
                }
            }
        } else {
            takeAPhoto.visibility = View.VISIBLE
            openLibraryPhoto.visibility = View.VISIBLE

            if (isAudioRecordingAllowed) {
                sendMsg.setImageResource(R.drawable.baseline_mic_24_white)
            }
        }
    }
    fun isEmptyEditText(): Boolean {
        val str = editT.text.toString()
        return str.trim { it <= ' ' }.length <= 0
    }

    private fun sendMessage() {
        //hideKeyboard();
        val msg = editT.getText().toString();
        editT.setText("");

        val trimMsg = msg.trim { it <= ' ' }.length > 0

        if(trimMsg) {
            val recordItem = listener!!.getRecordItem()

            recordItem?.let {
                var msgItem = MessageRoomItem()
                msgItem.idRoom = it.idRoom!!.toString()
                msgItem.data = MDate.longToString(MDate.getCurrentDate(),MDate.DATE_FORMAT_yyyyMMdd_HHmmss)
                msgItem.type = T3RoomActivity.MsgRoomType.TEXT.toString()
                msgItem.text = msg
                msgItem.otpravitel = "sotr"
                msgItem.idTm = it.tmId!!
                msgItem.nameTm = it.tmName
                msgItem.viewKl = "false"
                msgItem.viewSotr = "true"

                listener!!.sendMessageToServer(it.idKl!!.toString(), msgItem, it.idFilial!!.toString())
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

    private fun openLibraryPhoto() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)

        galleryIntent.type = "*/*"
        val mimetypes = arrayOf("image/jpeg", "image/png", "application/pdf")
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

        (context as AppCompatActivity).startActivityForResult(galleryIntent, T3RoomActivity.KEY_FOR_SELECT_DOC)
    }


    interface BottomBarChatViewListener {
        fun sendMessageToServer(idSotr: String, item: MessageRoomItem, idFilial: String)
        fun getNewUriForNewFile(idRoom: String, extensionF: String, idMessage: String? = null, timeMillis: String? = null) : Pair<Uri, File>?
        fun setMCurrentFilePath(uri: Uri?)
        fun getRecordItem(): AllRecordsTelemedicineItem?
    }
}