package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.decorators

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.medhelp.callmed2.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DayDecorator(private val context: Context, private val day: CalendarDay, val dayMode: Int) :
    DayViewDecorator {
    private val bgDrawableGreen: Drawable?
    private val bgDrawableClear: Drawable?

    init {
        bgDrawableGreen = ContextCompat.getDrawable(context, R.drawable.date_item_default)
        bgDrawableClear = ContextCompat.getDrawable(context, R.drawable.date_clear)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return this.day == day
    }

    override fun decorate(view: DayViewFacade) {
        when (dayMode) {
            DAY_MODE_MANY -> view.setBackgroundDrawable(
                bgDrawableGreen!!
            )
            DAY_MODE_CLEAR -> view.setBackgroundDrawable(bgDrawableClear!!)
        }
    }

    companion object {
        const val DAY_MODE_MANY = 1
        const val DAY_MODE_CLEAR = 2
    }
}