package com.medhelp.callmed2.ui._main_page.fragment_scanner_doc.view_and_alerts

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.exifinterface.media.ExifInterface
import com.medhelp.callmed2.R
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.WorkTofFile.show_file.SearchLoadFile
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class PreviewImageView : ConstraintLayout {

    //region constructor
    constructor(context: Context?) : super(context!!) {
        context?.let { init() }
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        context?.let { init() }
        initAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        context?.let { init() }
        initAttrs(attrs)
    }
    //endregion

    var activity: Activity? = null
    var listener: PreviewImageViewListener? = null
    fun setData(activity: Activity, listener: PreviewImageViewListener) {
        this.activity = activity
        this.listener = listener
    }

    lateinit var image: ImageView
    lateinit var rotateLeft: ImageButton
    lateinit var delete: ImageButton
    lateinit var rotateRight: ImageButton

    var photoURI: String? = null


    fun init() {
        val mainView = inflate(context, R.layout.view_previrew_image, this)
        image = mainView.findViewById(R.id.image)
        rotateLeft = mainView.findViewById(R.id.rotateLeft)
        delete = mainView.findViewById(R.id.delete)
        rotateRight = mainView.findViewById(R.id.rotateRight)

        isHideBtn(true)

        image.setOnClickListener {
            if (photoURI == null)
                listener?.startPhoto()
            else
             activity?.let{ showBigPhoto()}
        }

        rotateLeft.setOnClickListener {
            photoURI?.let { rotateLeft() }
        }

        delete.setOnClickListener {
            activity?.let { alertDeletePhoto(it) }
            //image.setImageDrawable(null)
        }

        rotateRight.setOnClickListener {
            photoURI?.let { rotateRight() }
        }

    }

    private fun initAttrs(attrs: AttributeSet?) {
//        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.BasicBtn, 0, 0)
//        text.text = typedArray.getString(R.styleable.BasicBtn_text)
    }


    private fun rotateLeft() {
        Different.showLoadingDialog(activity)

        val cd = CompositeDisposable()
        cd.add(rotate(270F)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                image.setImageDrawable(null)
                setImageURI()
                Different.hideLoadingDialog()
                cd.dispose()
            }, { throwable ->
                Different.hideLoadingDialog()
                cd.dispose()
            }
            )
        )
    }

    private fun rotateRight() {
        Different.showLoadingDialog(activity)
        val cd = CompositeDisposable()
        cd.add(rotate(90F)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                image.setImageDrawable(null)
                setImageURI()
                Different.hideLoadingDialog()
                cd.dispose()
            }, { throwable ->
                Different.hideLoadingDialog()
                cd.dispose()
            }
            )
        )
    }

    fun setImageURIWithProcessing() {
        Different.showLoadingDialog(activity)
        val cd = CompositeDisposable()
        cd.add(processingNewImage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setImageURI()
                Different.hideLoadingDialog()
                cd.dispose()
            }, { throwable ->
                Different.hideLoadingDialog()
                cd.dispose()
            }
            )
        )
    }

    private fun rotate(degree: Float): Completable {
        return Completable.fromAction(object : Action {
            override fun run() {
                var bitmap: Bitmap

                activity!!.getContentResolver().openInputStream(Uri.parse(photoURI))
                    .use { inputStream ->
                        bitmap = BitmapFactory.decodeStream(inputStream)
                    }
                val matrix = Matrix()
                matrix.postRotate(degree)

                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                activity!!.getContentResolver().openOutputStream(Uri.parse(photoURI))
                    .use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)
                        outputStream?.flush()
                    }
            }
        })
    }

    fun processingNewImage(): Completable {
        return Completable.fromAction(object : Action {
            override fun run() {
                //для исправления поворота при переводе в битмап
                var bitmap: Bitmap? = null
                var orientation: Int = ExifInterface.ORIENTATION_NORMAL
                try {
                    activity!!.getContentResolver().openInputStream(Uri.parse(photoURI))
                        .use { inputStream ->
                            bitmap = BitmapFactory.decodeStream(inputStream)

                            val exif: ExifInterface = ExifInterface(inputStream!!)
                            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                        }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val matrix = Matrix()

                when (orientation) {
                    ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
                }

                // К каsтыль
                if (bitmap!!.getWidth() >= bitmap!!.getHeight()) {
                    matrix.postRotate(90F)
                }

                //matrix.postRotate(90F)


                bitmap = Bitmap.createBitmap(
                    bitmap!!,
                    0,
                    0,
                    bitmap!!.width,
                    bitmap!!.height,
                    matrix,
                    true
                )

                val sizeList = SearchLoadFile.calculationOfProportionsBitmap(
                        bitmap!!.width,
                        bitmap!!.height,
                        1080,
                        1920
                    )

                bitmap = Bitmap.createScaledBitmap(bitmap!!, sizeList.get(0), sizeList.get(1), false)
                activity!!.getContentResolver().openOutputStream(Uri.parse(photoURI))
                    .use { outputStream ->
                        bitmap!!.compress(Bitmap.CompressFormat.PNG, 30, outputStream!!)
                        outputStream?.flush()
                    }
            }
        })
    }

    fun setImageURI() {
        photoURI?.let {
            val imgUri = Uri.parse(photoURI)
            image.setImageURI(imgUri)
            isHideBtn(false)
        }
    }

    fun isHideBtn(boo: Boolean) {
        if (boo) {
            rotateLeft.visibility = GONE
            delete.visibility = GONE
            rotateRight.visibility = GONE
        } else {
            rotateLeft.visibility = VISIBLE
            delete.visibility = VISIBLE
            rotateRight.visibility = VISIBLE
        }
    }

    fun alertDeletePhoto(activity: Activity) {
        Different.showAlertInfo(
            activity,
            null,
            "Удалить фотографию?",
            object : Different.AlertInfoListener {
                override fun clickOk() {
                    clearImage()
                }

                override fun clickNo() {}
            },
            true,
            "Отмена",
            "Да"
        )
    }

    fun clearImage() {
        photoURI = null
        isHideBtn(true)
        image.setImageDrawable(resources.getDrawable(R.drawable.ic_add_a_photo_white_24dp))
    }

    fun showBigPhoto(){
        photoURI?.let{
            val dia = ShowBigPhotoAlert()
            dia.setData(it)
            dia.show((activity!! as MainPageActivity).supportFragmentManager, ShowBigPhotoAlert :: class.java.canonicalName)
        }
    }

    interface PreviewImageViewListener {
        fun startPhoto()
    }

}