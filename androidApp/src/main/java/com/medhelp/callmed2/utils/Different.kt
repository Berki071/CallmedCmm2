package com.medhelp.callmed2.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.medhelp.callmed2.R
import java.util.*

object Different {
    fun isValidEmail(target: String?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun getClearPhone(ph: String): String {
        var ph = ph
        if (ph.length < 5) return ph
        ph = ph.substring(3)
        ph = ph.replace("\\)".toRegex(), "")
        ph = ph.replace("-".toRegex(), "")
        ph = ph.replace("_".toRegex(), "")
        return ph
    }

    fun getFormattedPhone(clearPhone: String): String {
        if (clearPhone.length != 10) return clearPhone
        var str = "+7("
        str += clearPhone.substring(0, 3)
        str += ")"
        str += clearPhone.substring(3, 6)
        str += "-"
        str += clearPhone.substring(6, 8)
        str += "-"
        str += clearPhone.substring(8, 10)
        return str
    }

    fun getRealPathFromURI(contentURI: Uri, context: Context): String? {
        val result: String?
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun spToPx(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }

    fun generateCornerShapeDrawable(
        color: Int,
        topLeftCorner: Int,
        topRightCorner: Int,
        bottomRightCorner: Int,
        bottomLeftCorner: Int
    ): ShapeDrawable {
        val shape: Shape = RoundRectShape(
            floatArrayOf(
                topLeftCorner.toFloat(),
                topLeftCorner.toFloat(),
                topRightCorner.toFloat(),
                topRightCorner.toFloat(),
                bottomRightCorner.toFloat(),
                bottomRightCorner.toFloat(),
                bottomLeftCorner.toFloat(),
                bottomLeftCorner.toFloat()
            ), null, null
        )
        val sd = ShapeDrawable(shape)
        sd.paint.color = color
        sd.paint.style = Paint.Style.FILL
        return sd
    }

    //region Load alert
    var progressDialog: AlertDialog? = null
    var timeStartProgressDialog: Long? = null
    fun showLoadingDialog(context: Context?): Boolean {
        if (context!=null && (progressDialog == null || (progressDialog != null && !progressDialog!!.isShowing))) {
            timeStartProgressDialog = Calendar.getInstance().timeInMillis
            val view = (context as Activity).layoutInflater.inflate(R.layout.dialog_progres, null)
            val builder = AlertDialog.Builder(context /*, R.style.DialogTheme*/)
            builder.setView(view)
            progressDialog = builder.create()
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            return true
        }
        return false
    }

    var handler: Handler? = null
    var runnable: Runnable? = null
    fun hideLoadingDialog() {
        if (progressDialog == null) return
        if (timeStartProgressDialog == null) timeStartProgressDialog = 0L
        val tmp = Calendar.getInstance().timeInMillis
        val tmp2 = tmp - timeStartProgressDialog!!
        if (tmp2 < 800 && tmp2 > 80) {
            handler = Handler()
            runnable = Runnable {
                try {
                    progressDialog!!.dismiss()
                } catch (e: Exception) {
                }
            }
            handler!!.postDelayed(runnable!!, tmp2)
        } else {
            progressDialog!!.dismiss()
        }
    }

    fun hideLoadingDialog(deley: Int) {
        val tmp = Calendar.getInstance().timeInMillis
        val tmp2 = tmp - timeStartProgressDialog!!
        if (tmp2 < deley) {
            handler = Handler()
            runnable = Runnable {
                try {
                    progressDialog!!.dismiss()
                } catch (e: Exception) {
                }
            }
            handler!!.postDelayed(runnable!!, tmp2)
        } else {
            progressDialog!!.dismiss()
        }
    }

    //endregion
    var alertInfo: AlertDialog? = null

    //    public static void showAlertInfo(Activity activity, String titleToolText, String msgText)
    //    {
    //        showAlertInfo(activity,titleToolText,msgText,null);
    //    }
    //
    //    public static void showAlertInfo(Activity activity, String titleToolText, String msgText,AlertInfoListener listener)
    //    {
    //        if(alertInfo!=null){
    //            alertInfo.dismiss();
    //            alertInfo=null;
    //        }
    //
    //        LayoutInflater inflater= activity.getLayoutInflater();
    //        View view=inflater.inflate(R.layout.dialog_2textview_btn2,null);
    //
    //        Toolbar toolbar=view.findViewById(R.id.toolbarD);
    //        TextView titleTool=view.findViewById(R.id.titleToolbar);
    //
    //
    //        TextView text=view.findViewById(R.id.text);
    //        Button btnYes =view.findViewById(R.id.btnYes);
    //        // Button btnNo =view.findViewById(R.id.btnNo);
    //        //btnNo.setVisibility(View.GONE);
    //        btnYes.setText("Ok");
    //
    //        if(titleToolText==null)
    //            titleTool.setVisibility(View.GONE);
    //        else {
    //            titleTool.setVisibility(View.VISIBLE);
    //            titleTool.setText(titleToolText);
    //        }
    //
    //
    //
    //        if(msgText==null)
    //            text.setVisibility(View.GONE);
    //        else {
    //            text.setVisibility(View.VISIBLE);
    //            text.setText(msgText);
    //        }
    //
    //        btnYes.setOnClickListener(v -> {
    //            alertInfo.dismiss();
    //        });
    //
    //
    //        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
    //        builder.setView(view);
    //
    //        alertInfo=builder.create();
    //        alertInfo.getWindow().setBackgroundDrawableResource(R.color.transparent);
    //        alertInfo.show();
    //
    //        // обработчик закрытия
    //        alertInfo.setOnDismissListener(new DialogInterface.OnDismissListener() {
    //            public void onDismiss(DialogInterface dialog) {
    //                if(listener!=null)
    //                    listener.clickOk();
    //            }
    //        });
    //    }
    @JvmOverloads
    fun showAlertInfo(
        activity: Activity?,
        titleToolText: String?,
        msgText: String?,
        listener: AlertInfoListener? = null,
        showCloseBtn: Boolean = false,
        btnNoName: String? = activity!!.getString(R.string.close),
        btnYesName: String? = activity!!.getString(R.string.understandably)
    ) {
        if (activity == null) return
        if (alertInfo != null) {
            alertInfo!!.dismiss()
            alertInfo = null
        }
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_2textview_btn2, null)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarD)
        val titleTool = view.findViewById<TextView>(R.id.titleToolbar)

        // ConstraintLayout mainBox=view.findViewById(R.id.mainBox);
        val text = view.findViewById<TextView>(R.id.text)
        val btnYes = view.findViewById<Button>(R.id.btnYes)
        val btnNo = view.findViewById<Button>(R.id.btnNo)
        if (showCloseBtn) {
            btnNo.visibility = View.VISIBLE
            btnNo.setOnClickListener { v: View? ->
                listener?.clickNo()
                alertInfo!!.dismiss()
            }
        } else {
            btnNo.visibility = View.GONE
        }

        //btnYes.setText("Ok");
        btnYes.text = btnYesName
        btnNo.text = btnNoName
        if (titleToolText == null) titleTool.visibility = View.GONE else {
            titleTool.visibility = View.VISIBLE
            titleTool.text = titleToolText
        }
        if (msgText == null) text.visibility = View.GONE else {
            text.visibility = View.VISIBLE
            text.text = msgText
        }
        btnYes.setOnClickListener { v: View? ->
            alertInfo!!.dismiss()
            listener?.clickOk()
        }
        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
        alertInfo = builder.create()
        alertInfo!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        val forWidthPix =
            activity.window.decorView.width - convertDpToPixel(16f, activity).toInt() * 2
        if (activity == null) return
        alertInfo!!.show()
        if (activity.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) alertInfo!!.window!!
            .setLayout(forWidthPix, ViewGroup.LayoutParams.WRAP_CONTENT) else alertInfo!!.window!!
            .setLayout(
                convertDpToPixel(400f, activity).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        alertInfo!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        alertInfo!!.setCanceledOnTouchOutside(true)
        alertInfo!!.setOnCancelListener { listener?.clickNo() }
    }

    //    public interface AlertInfoListener{
    //        void clickOk();
    //    }
    fun getEncodeKey(word: String): String {
        var tmp = ""
        val generator = Random()
        for (i in 0 until word.length) {
            tmp += if (word[i].code > 255) generator.nextInt(4095).toChar() else generator.nextInt(
                255
            )
                .toChar()
        }
        return tmp
    }

    @JvmStatic
    fun encodeDecodeWord(word: String, key: String): String {
        var ecodeStr = ""
        for (i in 0 until word.length) ecodeStr += (word[i].code xor key[i].code).toChar()
        return ecodeStr
    }

    interface AlertInfoListener {
        fun clickOk()
        fun clickNo()
    }
}