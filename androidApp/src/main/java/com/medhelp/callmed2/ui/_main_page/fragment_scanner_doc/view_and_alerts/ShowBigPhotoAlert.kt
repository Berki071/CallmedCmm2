package com.medhelp.callmed2.ui._main_page.fragment_scanner_doc.view_and_alerts

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.medhelp.callmed2.R
import java.util.*

class ShowBigPhotoAlert: DialogFragment() {
    lateinit var photoURI: String
    fun setData(photoURI: String){
        this.photoURI = photoURI
    }

    lateinit var image: ImageView

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)

        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            Objects.requireNonNull(dialog.window)?.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.GRAY))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.df_show_big_photo, null)
        image=view.findViewById<ImageView>(R.id.image)

        image.setOnClickListener{
            dialog?.dismiss()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgUri = Uri.parse(photoURI)
        image.setImageURI(imgUri)
    }
}