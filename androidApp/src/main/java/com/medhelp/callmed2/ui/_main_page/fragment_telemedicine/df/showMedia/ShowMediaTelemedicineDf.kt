package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DfShowMediaTelemedicineBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.SampleFragmentPagerAdapter
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import java.util.*

class ShowMediaTelemedicineDf: DialogFragment() {
    lateinit var recItem: AllRecordsTelemedicineItem
    lateinit var listener: ShowMediaTelemedicineListener   // при создании диалога

    lateinit var binding: DfShowMediaTelemedicineBinding

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
        val view: View = inflater.inflate(R.layout.df_show_media_telemedicine, null)
        binding = DfShowMediaTelemedicineBinding.bind(view)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.toolbar.title = recItem.fullNameKl
        binding.toolbar.title = "Медиа"
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }
        binding.tabLayou.setupWithViewPager(binding.viewpager)
        binding.viewpager.adapter = SampleFragmentPagerAdapter(childFragmentManager, recItem, listener)
    }

    interface ShowMediaTelemedicineListener{
        fun deleteFile(uriStringFile : String)
    }
}