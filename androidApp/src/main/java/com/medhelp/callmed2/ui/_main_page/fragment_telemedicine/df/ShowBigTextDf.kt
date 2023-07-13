package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DfShowBigTextBinding
import java.util.*

class ShowBigTextDf : DialogFragment() {
    var title: String? = null
    var text: String? = null
    fun setData(title: String?, text: String){
        this.title = title
        this.text = text
    }

    lateinit var binding: DfShowBigTextBinding

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)

        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            Objects.requireNonNull(dialog.window)?.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.df_show_big_text, null)
        binding = DfShowBigTextBinding.bind(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title?.let{
            binding.titleToolbar.text = it
        }

        text?.let{
            binding.text.text = it
        }

        binding.btnYes.setOnClickListener {
            dialog?.dismiss()
        }
    }
}