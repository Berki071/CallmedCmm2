package com.medhelp.callmed2.utils.main

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.provider.Settings
import android.util.TypedValue
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.utils.Different
import java.util.regex.Matcher
import java.util.regex.Pattern

object MainUtils {
    const val IMAGE = "image"
    const val FILE = "file"
    const val TEXT = "text"
    private var dialog: ProgressDialog? = null
    fun showLoadingDialog(context: Context?): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.dialog_progres)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        dialog = progressDialog
        return progressDialog
    }

    fun hideLoadingDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    @SuppressLint("all")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isEmailValid(email: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

//    @Throws(IOException::class)
//    fun loadJsonFromAsset(context: Context, jsonFileName: String?): String {
//        val manager = context.assets
//        val `is` = manager.open(jsonFileName!!)
//        val size = `is`.available()
//        val buffer = ByteArray(size)
//        `is`.read(buffer)
//        `is`.close()
//        return String(buffer, "UTF-8")
//    }

    fun dpToPx(context: Context, num: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            num.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    fun creteImageIco(context: Context, text: String, status: String): Bitmap? {
        val width = Different.convertDpToPixel(50f, context).toInt()
        val height = Different.convertDpToPixel(50f, context).toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = 0
        canvas.drawPaint(paint)
        paint.color = colorByStatus(context, status)
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2).toFloat(),
            paint
        )

        val sp14Normal = Paint()
        sp14Normal.color = context.resources.getColor(R.color.white)
        sp14Normal.textSize = Different.spToPx(18f, context)
        sp14Normal.isAntiAlias = true
        val widthText = sp14Normal.measureText(text)
        val bounds = Rect()
        sp14Normal.getTextBounds(text, 0, text.length, bounds)
        val heightText = bounds.height()
        canvas.drawText(
            text,
            (bitmap.width - widthText) / 2,
            ((bitmap.height - heightText) / 2 + heightText).toFloat(),
            sp14Normal
        )
        return bitmap
    }
    private fun colorByStatus(context: Context, status: String): Int{
        return when(status){
            Constants.TelemedicineStatusRecord.active.toString() -> {context.resources.getColor(R.color.colorPrimary)}
            Constants.TelemedicineStatusRecord.wait.toString()  -> {context.resources.getColor(R.color.light_green_pressed)}
            Constants.TelemedicineStatusRecord.complete.toString()  -> {context.resources.getColor(R.color.red)}
            else -> context.resources.getColor(R.color.text_semi_dark)
        }
    }

}