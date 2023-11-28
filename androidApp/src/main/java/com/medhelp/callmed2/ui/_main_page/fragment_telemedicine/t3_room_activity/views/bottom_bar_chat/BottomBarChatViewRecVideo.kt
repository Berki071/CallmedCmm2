package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.views.bottom_bar_chat

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.medhelp.callmed2.R
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class BottomBarChatViewRecVideo(var context: Context?,val videoPreviewContainer: ConstraintLayout) {
    var viewFinder: PreviewView

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var pathToFileRecord: Pair<Uri, File>? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private var typeLastStop: TypeLastStop? = null

    init{
        viewFinder = videoPreviewContainer.findViewById(R.id.viewFinder)
        //initCamera()
    }


    fun startRecord(pathToFileRecord: Pair<Uri, File>, listener: BottomBarChatViewRecVideoListener){
        initCamera(pathToFileRecord, listener)
    }
    private fun initCamera(pathToFileRecord: Pair<Uri, File>, listener: BottomBarChatViewRecVideoListener){
        if(context == null)
            return

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context!!)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.LOWEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)


            // Select back camera as a default
            //val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()
                // Bind use cases to camera
                if(context != null)
                    cameraProvider?.bindToLifecycle((context as T3RoomActivity), cameraSelector, preview, videoCapture)

            } catch(exc: Exception) {
                //Log.e(TAG, "Use case binding failed", exc)
                Timber.tag("my").w("BottomBarChatViewRecVideo/initCamera ${exc}")
            }

            captureVideo(pathToFileRecord, listener)

        }, ContextCompat.getMainExecutor(context!!))
    }

    fun captureVideo(pathToFileRecord: Pair<Uri, File>, listener: BottomBarChatViewRecVideoListener){
        val videoCapture = this.videoCapture ?: return

        this.pathToFileRecord = pathToFileRecord

        videoPreviewContainer.visibility = View.VISIBLE

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            stopRecord()
            return
        }

        val fileOutputOptions = FileOutputOptions.Builder(pathToFileRecord.second).build()

        if(context == null)
            return

        recording = videoCapture.output
            .prepareRecording(context!!, fileOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(context!!,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(context!!)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        listener.videoRecordStart()
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            //Log.e(TAG, "Video capture Finalize")
                            videoPreviewContainer.visibility = View.GONE

                            if(typeLastStop != null){
                                if(typeLastStop == TypeLastStop.STOP){
                                    //val msg = "Video capture succeeded: " + "${recordEvent.outputResults.outputUri}"
                                    //Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    //Log.wtf("CameraXApp", msg)
                                    listener.videoRecordFinalizeWithoutError(pathToFileRecord)
                                }else{
                                    deleteFile()
                                    Toast.makeText(context,"Отменено", Toast.LENGTH_SHORT).show()
                                }
                            }

                            recording?.close()
                            recording = null
                            this.videoCapture = null
                            cameraProvider?.unbindAll()
                            cameraProvider = null

                        } else {
                            recording?.close()
                            recording = null
                            this.videoCapture = null
                            cameraProvider?.unbindAll()
                            cameraProvider = null
                            deleteFile()
                            videoPreviewContainer.visibility = View.GONE
                            Timber.tag("my").w("BottomBarChatViewRecVideo/startRecord ${recordEvent.error}")
                            //Log.e(TAG, "Video capture ends with error: ${recordEvent.error}")
                        }

                    }
                }
            }
    }

    fun stopRecord(){
        typeLastStop = TypeLastStop.STOP

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
        }

        //не забыть очистить переменную в родителе
    }

    fun cancelRecord(){
        //фаил удалиться в VideoRecordEvent.Finalize по типу окончания записи
        typeLastStop = TypeLastStop.CANSEL

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
        }

        //не забыть очистить переменную в родителе
    }

    fun deleteFile(){
        pathToFileRecord?.let{
            it.second.delete()
        }
        pathToFileRecord = null
    }


    enum class TypeLastStop{
        STOP,CANSEL
    }

    interface BottomBarChatViewRecVideoListener{
        fun videoRecordStart()
        fun videoRecordFinalizeWithoutError(pathToFileRecord: Pair<Uri, File>)
    }
}