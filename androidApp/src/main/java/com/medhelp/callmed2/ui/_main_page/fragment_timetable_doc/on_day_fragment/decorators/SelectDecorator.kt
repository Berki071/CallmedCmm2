package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.decorators

import android.app.Activity
import android.graphics.drawable.Drawable
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import com.medhelp.callmed2.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectDecorator(context: Activity?) : DayViewDecorator {
    private val drawable: Drawable?

    init {
        drawable = ContextCompat.getDrawable(context!!, R.drawable.date_selector)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(RelativeSizeSpan(1.5f))
        view.setSelectionDrawable(drawable!!)
    }
}