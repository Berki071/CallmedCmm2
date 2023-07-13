package com.medhelp.callmed2.ui._main_page.fragment_doc_recognition

import android.Manifest
import android.R.attr.radius
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui.base.BaseFragment
import timber.log.Timber
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit

class DocRecognitionFragment : BaseFragment() {
    override fun setUp(view: View) {
    }

    override fun destroyFragment() {
    }

    override fun onStartSetStatusFragment(status: Int) {
    }

    //    lateinit var toolbar: Toolbar
//    lateinit var makePhoto: Button
//    lateinit var viewFinder: PreviewView
//    lateinit var imageHint: ImageView
//
//    lateinit var presenter: DocRecognitionPresenter
//
//    lateinit var cameraExecutor: ExecutorService
//    private var imageCapture: ImageCapture? = null
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        Timber.i("Распознавание документов")
//        val view = inflater.inflate(R.layout.fragment_doc_recognition, container, false)
//        toolbar = view.findViewById(R.id.toolbar)
//        makePhoto = view.findViewById(R.id.makePhoto)
//        viewFinder = view.findViewById(R.id.viewFinder)
//        imageHint = view.findViewById(R.id.imageHint)
//        presenter = DocRecognitionPresenter(this)
//
//        presenter.extractAssets(requireContext())
//
//        // Request camera permissions
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        return view
//    }
//
//    override fun setUp(view: View) {     //onViewCreated
//        toolbar.setNavigationOnClickListener{
//            (context as MainPageActivity?)!!.showNavigationMenu()
//        }
//
//        makePhoto.setOnClickListener {
//            takePhoto()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        imageHint.post {
//            drawPassportTip()
//        }
//
//    }
//
//    fun drawPassportTip (){
//        val height =  imageHint.height
//        val width = imageHint.width
//
//        if(height<=0 || width<=0)
//            return
//
//        val Rw = width/height
//        val minMargin = 50F
//
//
////        val marginHorizontal = 50F
////        val marginVertical = 252F
//
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//
//        val paint = Paint()
//        paint.style = Paint.Style.STROKE
//        paint.isAntiAlias = true
//        paint.color = Color.RED
//        paint.strokeWidth = 5f
//
//        val paint2 = Paint()
//        paint2.style = Paint.Style.STROKE
//        paint2.isAntiAlias = true
//        paint2.color = Color.GREEN
//        paint2.strokeWidth = 3f
//
////        //внешняя рамка паспорта
////        val rect = RectF()
////        rect.set(marginHorizontal, marginVertical, width-marginHorizontal, height-marginVertical)
////        canvas.drawRoundRect(rect, 20f, 20f, paint)
////        //разделитель страниц
////        canvas.drawLine(marginHorizontal, YMidLine, width-marginHorizontal, YMidLine, paint)
//
//        var widthPassport = 0f
//        var heightPassport = 0f
//        var marginHorizontal = 0F
//        var marginVertical = 0F
//        var heightOneList = 0F
//        val rf = 0.71
//        if(Rw<rf){
//            widthPassport = width - (minMargin * 2)
//            heightPassport = (widthPassport/rf).toFloat()
//            marginHorizontal = minMargin
//            marginVertical = ((height - heightPassport) / 2).toFloat()
//
//            val rectTMP = RectF()
//            rectTMP.set(marginHorizontal, marginVertical, marginHorizontal + widthPassport, marginVertical + heightPassport)
//            canvas.drawRoundRect(rectTMP, 20f, 20f, paint2)
//
//            //разделитель страниц
//            heightOneList=heightPassport/2
//            canvas.drawLine(marginHorizontal, marginVertical + (heightPassport/2), marginHorizontal + widthPassport, marginVertical + (heightPassport/2), paint2)
//        }else{
//            heightPassport = height - (minMargin * 2)
//            widthPassport = (heightPassport*rf).toFloat()
//            marginVertical = minMargin
//            marginHorizontal = ((width - widthPassport) / 2).toFloat()
//
//            val rectTMP = RectF()
//            rectTMP.set(marginHorizontal, marginVertical, marginHorizontal + widthPassport, marginVertical + heightPassport)
//            canvas.drawRoundRect(rectTMP, 20f, 20f, paint2)
//
//            //разделитель страниц
//            heightOneList=heightPassport/2
//            canvas.drawLine(marginHorizontal, marginVertical + (heightPassport/2), marginHorizontal + widthPassport, marginVertical + (heightPassport/2), paint2)
//        }
//
//        //паспорт выдан math
//        val passIssuedRF = 5
//        var passIssuedTopMargin = ((heightPassport * 7.45) / 100).toFloat()
//        var passIssuedRightMargin = widthPassport - ((widthPassport * 90) / 100).toFloat()
//        var passIssuedWidth = widthPassport - passIssuedRightMargin
//        var passIssuedHeight = passIssuedWidth/passIssuedRF
//        val rectPI = RectF()
//        rectPI.set(marginHorizontal, marginVertical+passIssuedTopMargin, marginHorizontal + passIssuedWidth, marginVertical + passIssuedTopMargin + passIssuedHeight)
//        canvas.drawRoundRect(rectPI, 10f, 10f, paint2)
//
//        //дата выдачи
//        val dateOfIssuedRF = 6.2
//        var dateOfIssuedTopMargin = ((heightPassport * 19.7) / 100).toFloat()
//        var dateOfIssuedRightMargin = widthPassport - ((widthPassport * 38.65) / 100).toFloat()
//        var dateOfIssuedWidth = widthPassport - dateOfIssuedRightMargin
//        var dateOfIssuedHeight = (dateOfIssuedWidth/dateOfIssuedRF).toFloat()
//        val rectDOI = RectF()
//        rectDOI.set(marginHorizontal, marginVertical+dateOfIssuedTopMargin, marginHorizontal + dateOfIssuedWidth, marginVertical + dateOfIssuedTopMargin + dateOfIssuedHeight)
//        canvas.drawRoundRect(rectDOI, 10f, 10f, paint2)
//
//
//        //код подразделения
//        val divisionCodeRF = 6.2
//        var divisionCodeTopMargin = ((heightPassport * 19.7) / 100).toFloat()
//        var divisionCodeLeftMargin = ((widthPassport * 54.6) / 100).toFloat()
//        var divisionCodeRightMargin = widthPassport - ((widthPassport * 90) / 100).toFloat()
//        var divisionCodeWidth = widthPassport - divisionCodeRightMargin - divisionCodeLeftMargin
//        var divisionCodeHeight = (divisionCodeWidth/divisionCodeRF).toFloat()
//        val rectDC = RectF()
//        rectDC.set(marginHorizontal+divisionCodeLeftMargin, marginVertical+divisionCodeTopMargin, marginHorizontal + divisionCodeLeftMargin + divisionCodeWidth, marginVertical + divisionCodeTopMargin + divisionCodeHeight)
//        canvas.drawRoundRect(rectDC, 10f, 10f, paint2)
//
//
//        // серия номер
//        val siriusAndNumberCodsRF = 0.12
//        var siriusAndNumberCodsTopMargin = 0f
//        var siriusAndNumberCodsLeftMargin = ((widthPassport * 91.8) / 100).toFloat()
//        var siriusAndNumberCodsWidth = widthPassport - siriusAndNumberCodsLeftMargin
//        var siriusAndNumberCodsHeight = (siriusAndNumberCodsWidth/siriusAndNumberCodsRF).toFloat()
//        val rectSANC = RectF()
//        rectSANC.set(marginHorizontal+siriusAndNumberCodsLeftMargin, marginVertical+siriusAndNumberCodsTopMargin, marginHorizontal + siriusAndNumberCodsLeftMargin + siriusAndNumberCodsWidth, marginVertical + siriusAndNumberCodsTopMargin + siriusAndNumberCodsHeight)
//        canvas.drawRoundRect(rectSANC, 10f, 10f, paint2)
//
//
//        //фамилия
//        val lastNameRF = 5.2
//        var lastNameTopMargin = ((heightPassport * 54.19) / 100).toFloat()
//        var lastNameLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var lastNameRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var lastNameWidth = widthPassport - lastNameLeftMargin - lastNameRightMargin
//        var lastNameHeight = (lastNameWidth/lastNameRF).toFloat()
//        val rectFM = RectF()
//        rectFM.set(marginHorizontal+lastNameLeftMargin, marginVertical+lastNameTopMargin, marginHorizontal + lastNameLeftMargin + lastNameWidth, marginVertical + lastNameTopMargin + lastNameHeight)
//        canvas.drawRoundRect(rectFM, 10f, 10f, paint2)
//
//        // Name
//        val firstNameRF = 9.6
//        var firstNameTopMargin = ((heightPassport * 62.04) / 100).toFloat()
//        var firstNameLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var firstNameRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var firstNameWidth = widthPassport - firstNameLeftMargin - firstNameRightMargin
//        var firstNameHeight = (firstNameWidth/firstNameRF).toFloat()
//        val rectFN = RectF()
//        rectFN.set(marginHorizontal+firstNameLeftMargin, marginVertical+firstNameTopMargin, marginHorizontal + firstNameLeftMargin + firstNameWidth, marginVertical + firstNameTopMargin + firstNameHeight)
//        canvas.drawRoundRect(rectFN, 10f, 10f, paint2)
//
//        // отчество
//        val surnameRF = 9.6
//        var surnameTopMargin = ((heightPassport * 66.0) / 100).toFloat()
//        var surnameLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var surnameRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var surnameWidth = widthPassport - surnameLeftMargin - surnameRightMargin
//        var surnameHeight = (surnameWidth/surnameRF).toFloat()
//        val rectS = RectF()
//        rectS.set(marginHorizontal+surnameLeftMargin, marginVertical+surnameTopMargin, marginHorizontal + surnameLeftMargin + surnameWidth, marginVertical + surnameTopMargin + surnameHeight)
//        canvas.drawRoundRect(rectS, 10f, 10f, paint2)
//
//        // пол
//        val genderRF = 2.2
//        var genderTopMargin = ((heightPassport * 70) / 100).toFloat()
//        var genderLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var genderRightMargin = widthPassport - ((widthPassport * 48.5) / 100).toFloat()
//        var genderWidth = widthPassport - genderLeftMargin - genderRightMargin
//        var genderHeight = (genderWidth/genderRF).toFloat()
//        val rectG = RectF()
//        rectG.set(marginHorizontal+genderLeftMargin, marginVertical+genderTopMargin, marginHorizontal + genderLeftMargin + genderWidth, marginVertical + genderTopMargin + genderHeight)
//        canvas.drawRoundRect(rectG, 10f, 10f, paint2)
//
//        // день рождения
//        val birthdayRF = 4.6
//        var birthdayTopMargin = ((heightPassport * 70) / 100).toFloat()
//        var birthdayLeftMargin = ((widthPassport * 56) / 100).toFloat()
//        var birthdayRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var birthdayWidth = widthPassport - birthdayLeftMargin - birthdayRightMargin
//        var birthdayHeight = (birthdayWidth/birthdayRF).toFloat()
//        val rectBD = RectF()
//        rectBD.set(marginHorizontal+birthdayLeftMargin, marginVertical+birthdayTopMargin, marginHorizontal + birthdayLeftMargin + birthdayWidth, marginVertical + birthdayTopMargin + birthdayHeight)
//        canvas.drawRoundRect(rectBD, 10f, 10f, paint2)
//
//        // место рождения
//        val placeOfBirthRF =3.5
//        var placeOfBirthTopMargin = ((heightPassport * 74.5) / 100).toFloat()
//        var placeOfBirthLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var placeOfBirthRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var placeOfBirthWidth = widthPassport - placeOfBirthLeftMargin - placeOfBirthRightMargin
//        var placeOfBirthHeight = (placeOfBirthWidth/placeOfBirthRF).toFloat()
//        val rectPOB = RectF()
//        rectPOB.set(marginHorizontal+placeOfBirthLeftMargin, marginVertical+placeOfBirthTopMargin, marginHorizontal + placeOfBirthLeftMargin + placeOfBirthWidth,
//            marginVertical + placeOfBirthTopMargin + placeOfBirthHeight)
//        canvas.drawRoundRect(rectPOB, 10f, 10f, paint2)
//
//        imageHint.setImageBitmap(bitmap)
//        Log.wtf("","")
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Constants.KEY_FOR_OPEN_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
//            if (data != null) {
//                val contentURI = data.data
//
//                contentURI?.let { it ->
//
//                    var bitmap: Bitmap? = null
//                    val contentResolver: ContentResolver = requireContext().contentResolver
//                    try {
//                        bitmap = if (Build.VERSION.SDK_INT < 28) {
//                            MediaStore.Images.Media.getBitmap(contentResolver, it)
//                        } else {
//                            val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, it)
//                            ImageDecoder.decodeBitmap(source)
//                        }
//
//
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//
//                    bitmap?.let {
//                        bitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true);
//                        presenter.extractText(bitmap!!)
//                    }
//
//                }
//            }
//        }
//    }
//
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//
//        cameraProviderFuture.addListener({
//            // Used to bind the lifecycle of cameras to the lifecycle owner
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(viewFinder.surfaceProvider)
//                }
//
//            imageCapture = ImageCapture.Builder().build()
//
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        Log.d("Camera", "Average luminosity: $luma")
//                    })
//                }
//
//            // Select back camera as a default
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll()
//
//                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalyzer)
//
//            } catch(exc: Exception) {
//                //Log.e(TAG, "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }
//
//    private fun takePhoto() {
//        // Get a stable reference of the modifiable image capture use case
//        val imageCapture = imageCapture ?: return
//
//        // Create time stamped name and MediaStore entry.
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//
//        // Create output options object which contains file + metadata
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(requireContext().contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues)
//            .build()
//
//        // Set up image capture listener, which is triggered after photo has
//        // been taken
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(requireContext()),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                }
//
//                override fun onImageSaved(output: ImageCapture.OutputFileResults){
//                    val msg = "Photo capture succeeded: ${output.savedUri}"
//                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//
//                    val dialog = ShowProcessingPhotoDF()
//                    output.savedUri?.let { dialog.uriImage = it }
//
//                    dialog.show(childFragmentManager, ShowProcessingPhotoDF::class.java.canonicalName)
//
//                    Log.d(TAG, msg)
//                }
//            }
//        )
//    }
//
//
//    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {
//
//        private fun ByteBuffer.toByteArray(): ByteArray {
//            rewind()    // Rewind the buffer to zero
//            val data = ByteArray(remaining())
//            get(data)   // Copy the buffer into a byte array
//            return data // Return the byte array
//        }
//
//        override fun analyze(image: ImageProxy) {
//
//            val buffer = image.planes[0].buffer
//            val data = buffer.toByteArray()
//            val pixels = data.map { it.toInt() and 0xFF }
//            val luma = pixels.average()
//
//            listener(luma)
//
//            image.close()
//        }
//    }
//
//    override fun destroyFragment() {}
//    override fun onStartSetStatusFragment(status: Int) {}
//
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
//                //finish()
//            }
//        }
//    }
//
//    companion object {
//        private const val TAG = "CameraXApp"
//        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS =
//            mutableListOf (Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).apply {
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                }
//            }.toTypedArray()
//    }

}