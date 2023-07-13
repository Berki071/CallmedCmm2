package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineItem
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.ShowMediaTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.files.ShowFilesMediaTmFragment
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.images.ShowImagesMediaTmFragment


class SampleFragmentPagerAdapter(fm: FragmentManager, val recItem: AllRecordsTelemedicineItem, val listener: ShowMediaTelemedicineDf.ShowMediaTelemedicineListener): FragmentStatePagerAdapter(fm) {
    val PAGE_COUNT = 2
    private val tabTitles = arrayOf("Изображения", "Документы")


    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        if(position == 0) {
            val fragment = ShowImagesMediaTmFragment()
            fragment.recItem= recItem
            fragment.listener = listener
            return fragment
        }else {
            val fragment = ShowFilesMediaTmFragment()
            fragment.recItem= recItem
            fragment.listener = listener
            return fragment
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

}