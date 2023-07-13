//package com.medhelp.callmed2.ui._main_page.fragment_doc_recognition.didalog
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import android.graphics.Matrix
//import android.graphics.drawable.ColorDrawable
//import android.net.Uri
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.exifinterface.media.ExifInterface
//import androidx.fragment.app.DialogFragment
//import com.medhelp.callmed2.R
//import com.medhelp.callmed2.databinding.ShowProcessingPhotoBinding
//import java.io.IOException
//import java.util.*
//
//class ShowProcessingPhotoDF: DialogFragment() {
//    var uriImage: Uri? = null
//
//    lateinit var binding: ShowProcessingPhotoBinding
//    lateinit var pathTesseract : String
//
//    //val tess = TessBaseAPI()
//
//    override fun onStart() {
//        super.onStart()
//        setHasOptionsMenu(true)
//
//        //костыль, по умолчанию окно показывается не во весь размер
//        val dialog = dialog
//        if (dialog != null) {
//            val width = ViewGroup.LayoutParams.MATCH_PARENT
//            val height = ViewGroup.LayoutParams.MATCH_PARENT
//            Objects.requireNonNull(dialog.window)?.setLayout(width, height)
//            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.GRAY))
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view: View = inflater.inflate(R.layout.show_processing_photo, null)
//        binding = ShowProcessingPhotoBinding.bind(view)
//
////        Assets.extractAssets(requireContext())
////        pathTesseract = Assets.getTessDataPath(requireContext())
//
////        if (!tess.init(pathTesseract, "rus", TessBaseAPI.OEM_TESSERACT_LSTM_COMBINED)) {
////            // Error initializing Tesseract (wrong/inaccessible data path or not existing language file)
////            tess.recycle()
////        }
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        if(uriImage == null)
//            return
//
//        var bitmap = fixBitmapRotation() ?: return
//
//        val height =  bitmap.height
//        val width = bitmap.width
//
//        if(height<=0 || width<=0)
//            return
//
//        val Rw = width.toFloat()/height.toFloat()
//        val minMargin = 50F
//        //val minMargin = 200F
//
//        var bitmapPassport : Bitmap? = null
//
//        val rf = 0.71
//        if(Rw<=rf){
//            val wF = width - (minMargin * 2)
//            val hF = wF/rf
//            // марджин по Ширене  minMargin
//            val margVert = ((height - hF) / 2).toFloat()
//
//            bitmapPassport = Bitmap.createBitmap(bitmap, minMargin.toInt(), margVert.toInt(), wF.toInt(), hF.toInt())
//            binding.imageAllPass.setImageBitmap(bitmapPassport)
//        }else{
//            val hF = height - (minMargin * 2)
//            val wF = hF*rf
//            // марджин по Высоте  minMargin
//            val margHoris = ((width - wF) / 2).toFloat()
//
//            bitmapPassport = Bitmap.createBitmap(bitmap, margHoris.toInt(), minMargin.toInt(), wF.toInt(), hF.toInt())
//            binding.imageAllPass.setImageBitmap(bitmapPassport)
//        }
//
//        bitmap.recycle()
//
//        val heightPassport =  bitmapPassport.height
//        val widthPassport  = bitmapPassport.width
//
//        //паспорт выдан
//        val passIssuedRF = 5
//        var passIssuedTopMargin = ((heightPassport * 7.45) / 100).toFloat()
//        var passIssuedRightMargin = widthPassport - ((widthPassport * 90) / 100).toFloat()
//        var passIssuedWidth = widthPassport - passIssuedRightMargin
//        var passIssuedHeight = passIssuedWidth/passIssuedRF
//
//        val bitmapTmp = Bitmap.createBitmap(bitmapPassport, 0, passIssuedTopMargin.toInt(), passIssuedWidth.toInt(), passIssuedHeight.toInt())
//        binding.imagePassIssued.setImageBitmap(bitmapTmp)
//        workTesseractbBitmapToString(bitmapTmp)?.let{
//            binding.textPassIssued.text = clearTessString(it)
//        }
//
//        //дата выдачи
//        val dateOfIssuedRF = 6.2
//        var dateOfIssuedTopMargin = ((heightPassport * 19.7) / 100).toFloat()
//        var dateOfIssuedRightMargin = widthPassport - ((widthPassport * 38.65) / 100).toFloat()
//        var dateOfIssuedWidth = widthPassport - dateOfIssuedRightMargin
//        var dateOfIssuedHeight = (dateOfIssuedWidth/dateOfIssuedRF).toFloat()
//
//        val bitmapTmp2 = Bitmap.createBitmap(bitmapPassport, 0, dateOfIssuedTopMargin.toInt(), dateOfIssuedWidth.toInt(), dateOfIssuedHeight.toInt())
//        binding.imageDateOfIssued.setImageBitmap(bitmapTmp2)
//        workTesseractbBitmapToString(bitmapTmp2)?.let{
//            binding.textDateOfIssued.text = clearTessString(it)
//        }
//
//
//        //код подразделения
//        val divisionCodeRF = 6.2
//        var divisionCodeTopMargin = ((heightPassport * 19.7) / 100).toFloat()
//        var divisionCodeLeftMargin = ((widthPassport * 54.6) / 100).toFloat()
//        var divisionCodeRightMargin = widthPassport - ((widthPassport * 90) / 100).toFloat()
//        var divisionCodeWidth = widthPassport - divisionCodeRightMargin - divisionCodeLeftMargin
//        var divisionCodeHeight = (divisionCodeWidth/divisionCodeRF).toFloat()
//
//        val bitmapTmp3 = Bitmap.createBitmap(bitmapPassport, divisionCodeLeftMargin.toInt(), divisionCodeTopMargin.toInt(), divisionCodeWidth.toInt(), divisionCodeHeight.toInt())
//        binding.imageDivisionCode.setImageBitmap(bitmapTmp3)
//        workTesseractbBitmapToString(bitmapTmp3)?.let{
//            binding.textDivisionCode.text = clearTessString(it)
//        }
//
//        // серия номер
//        val siriusAndNumberCodsRF = 0.12
//        var siriusAndNumberCodsTopMargin = 0
//        var siriusAndNumberCodsLeftMargin = ((widthPassport * 91.8) / 100).toFloat()
//        var siriusAndNumberCodsWidth = widthPassport - siriusAndNumberCodsLeftMargin
//        var siriusAndNumberCodsHeight = (siriusAndNumberCodsWidth/siriusAndNumberCodsRF).toFloat()
//
//        var bitmapTmp4 = Bitmap.createBitmap(bitmapPassport, siriusAndNumberCodsLeftMargin.toInt(), siriusAndNumberCodsTopMargin, siriusAndNumberCodsWidth.toInt(), siriusAndNumberCodsHeight.toInt())
//        bitmapTmp4 = bitmapTmp4.rotate(270F)
//        binding.imageSiriusAndNumberCods.setImageBitmap(bitmapTmp4)
//        workTesseractbBitmapToString(bitmapTmp4)?.let{
//            binding.textSiriusAndNumberCods.text = clearTessString(it)
//        }
//
//        //фамилия
//        val lastNameRF = 5.2
//        var lastNameTopMargin = ((heightPassport * 54.19) / 100).toFloat()
//        var lastNameLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var lastNameRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var lastNameWidth = widthPassport - lastNameLeftMargin - lastNameRightMargin
//        var lastNameHeight = (lastNameWidth/lastNameRF).toFloat()
//
//        var bitmapTmp5 = Bitmap.createBitmap(bitmapPassport, lastNameLeftMargin.toInt(), lastNameTopMargin.toInt(), lastNameWidth.toInt(), lastNameHeight.toInt())
//        binding.imageLastName.setImageBitmap(bitmapTmp5)
//        workTesseractbBitmapToString(bitmapTmp5)?.let{
//            binding.textLastName.text = clearTessString(it)
//        }
//
//        // Name
//        val firstNameRF = 9.6
//        var firstNameTopMargin = ((heightPassport * 62.04) / 100).toFloat()
//        var firstNameLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var firstNameRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var firstNameWidth = widthPassport - firstNameLeftMargin - firstNameRightMargin
//        var firstNameHeight = (firstNameWidth/firstNameRF).toFloat()
//
//        var bitmapTmp6 = Bitmap.createBitmap(bitmapPassport, firstNameLeftMargin.toInt(), firstNameTopMargin.toInt(), firstNameWidth.toInt(), firstNameHeight.toInt())
//        binding.imageFirstName.setImageBitmap(bitmapTmp6)
//        workTesseractbBitmapToString(bitmapTmp6)?.let{
//            binding.textFirstName.text = clearTessString(it)
//        }
//
//        // отчество
//        val surnameRF = 9.6
//        var surnameTopMargin = ((heightPassport * 66.0) / 100).toFloat()
//        var surnameLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var surnameRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var surnameWidth = widthPassport - surnameLeftMargin - surnameRightMargin
//        var surnameHeight = (surnameWidth/surnameRF).toFloat()
//
//        var bitmapTmp7 = Bitmap.createBitmap(bitmapPassport, surnameLeftMargin.toInt(), surnameTopMargin.toInt(), surnameWidth.toInt(), surnameHeight.toInt())
//        binding.imageSurname.setImageBitmap(bitmapTmp7)
//        workTesseractbBitmapToString(bitmapTmp7)?.let{
//            binding.textSurname.text = clearTessString(it)
//        }
//
//        // пол
//        val genderRF = 2.2
//        var genderTopMargin = ((heightPassport * 70) / 100).toFloat()
//        var genderLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var genderRightMargin = widthPassport - ((widthPassport * 48.5) / 100).toFloat()
//        var genderWidth = widthPassport - genderLeftMargin - genderRightMargin
//        var genderHeight = (genderWidth/genderRF).toFloat()
//
//        var bitmapTmp8 = Bitmap.createBitmap(bitmapPassport, genderLeftMargin.toInt(), genderTopMargin.toInt(), genderWidth.toInt(), genderHeight.toInt())
//        binding.imageGender.setImageBitmap(bitmapTmp8)
//        workTesseractbBitmapToString(bitmapTmp8)?.let{
//            binding.textGender.text = clearTessString(it)
//        }
//
//        // день рождения
//        val birthdayRF = 4.6
//        var birthdayTopMargin = ((heightPassport * 70) / 100).toFloat()
//        var birthdayLeftMargin = ((widthPassport * 56) / 100).toFloat()
//        var birthdayRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var birthdayWidth = widthPassport - birthdayLeftMargin - birthdayRightMargin
//        var birthdayHeight = (birthdayWidth/birthdayRF).toFloat()
//
//        var bitmapTmp9 = Bitmap.createBitmap(bitmapPassport, birthdayLeftMargin.toInt(), birthdayTopMargin.toInt(), birthdayWidth.toInt(), birthdayHeight.toInt())
//        binding.imageBirthday.setImageBitmap(bitmapTmp9)
//        workTesseractbBitmapToString(bitmapTmp9)?.let{
//            binding.textBirthday.text = clearTessString(it)
//        }
//
//        // место рождения
//        val placeOfBirthRF =3.5
//        var placeOfBirthTopMargin = ((heightPassport * 74.5) / 100).toFloat()
//        var placeOfBirthLeftMargin = ((widthPassport * 33.3) / 100).toFloat()
//        var placeOfBirthRightMargin = widthPassport - ((widthPassport * 91) / 100).toFloat()
//        var placeOfBirthWidth = widthPassport - placeOfBirthLeftMargin - placeOfBirthRightMargin
//        var placeOfBirthHeight = (placeOfBirthWidth/placeOfBirthRF).toFloat()
//        var bitmapTmp10 = Bitmap.createBitmap(bitmapPassport, placeOfBirthLeftMargin.toInt(), placeOfBirthTopMargin.toInt(), placeOfBirthWidth.toInt(), placeOfBirthHeight.toInt())
//        binding.imagePlaceOfBirth.setImageBitmap(bitmapTmp10)
//        workTesseractbBitmapToString(bitmapTmp10)?.let{
//            binding.textPlaceOfBirth.text = clearTessString(it)
//        }
//
//    }
//
////    fun workTesseractbBitmapToString(bitmap: Bitmap) : String? {
////        tess.setImage(bitmap)
////        val textTess: String? = tess.getUTF8Text()
////        return textTess
////    }
//
//    fun clearTessString(str: String) : String {
//
////        text = text.replace("=", "")
////        text = text.replace("+", "")
////        text = text.replace("_", "")
////        text = text.replace("|", "")
////        text = text.replace("$", "")
////        text = text.replace("&", "")
////        text = text.replace("©", "")
////        text = text.replace("!", "")
////        text = text.replace("{", "")
////        text = text.replace("}", "")
////        text = text.replace("°", "")
//
////        val s = "123456789.-йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ"
////        val text = s.replace("[^A-Za-zА-Яа-я0-9]".toRegex(), str) // удалится все кроме букв и цифр
//
//        return str
//    }
//
//    fun fixBitmapRotation() : Bitmap?{
//        //для исправления поворота при переводе в битмап
//        var bitmap: Bitmap? = null
//        var orientation: Int = ExifInterface.ORIENTATION_NORMAL
//        try {
//            requireContext().getContentResolver().openInputStream(uriImage!!)
//                .use { inputStream ->
//                    bitmap = BitmapFactory.decodeStream(inputStream)
//
//                    val exif: ExifInterface = ExifInterface(inputStream!!)
//                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//                }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        val matrix = Matrix()
//
//        when (orientation) {
//            ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
//            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
//            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
//        }
//
//        matrix.postRotate(90F)
//
//        bitmap = Bitmap.createBitmap(
//            bitmap!!,
//            0,
//            0,
//            bitmap!!.width,
//            bitmap!!.height,
//            matrix,
//            true
//        )
//
//
//        bitmap?.let{
//            return it
//        }
//
//        return null
//    }
//
//    override fun onDestroy() {
////        tess.stop();
////        tess.recycle()
//        super.onDestroy()
//    }
//
//    fun Bitmap.rotate(degrees: Float): Bitmap {
//        val matrix = Matrix().apply { postRotate(degrees) }
//        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
//    }
//}