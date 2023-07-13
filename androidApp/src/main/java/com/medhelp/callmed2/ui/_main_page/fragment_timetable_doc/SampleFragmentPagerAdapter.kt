package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.TimetableOnDayFragment
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment.TimetableOnMonthFragment
import timber.log.Timber

class SampleFragmentPagerAdapter(fm: FragmentManager,context: Context): FragmentPagerAdapter(fm) {
    val PAGE_COUNT = 2
    private val tabTitles = arrayOf("Расписание по дням", "Расписание на месяц")


    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        if(position == 0) {
            return TimetableOnDayFragment()
        }else {
            return TimetableOnMonthFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        // генерируем заголовок в зависимости от позиции
        return tabTitles[position]
    }
}