package com.medhelp.callmed2.ui._main_page.fragment_video_consultation.recy

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.model.VisitItem2
import com.medhelp.callmed2.databinding.ItemOnlineConsultationBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_video_consultation.recy.ConsultationAdapter.ConsultationListener
import com.medhelp.callmed2.utils.main.TimesUtils
import java.util.*

class ConsultationHolder(val bindingitem: ItemOnlineConsultationBinding, private val token: String, var listener: ConsultationListener) : RecyclerView.ViewHolder(bindingitem.root) {

    private var data: VisitItem2? = null
    private var timer: Timer? = null
    private var myTimer: MyTimerClass? = null

    init {
        itemView.setOnClickListener {
            if (bindingitem.timerTitle.getVisibility() === View.VISIBLE) {
                data?.let{listener.onClickItemRecy(it)}
            }
        }
    }

    fun onBindView(data: VisitItem2) {
        this.data = data
        bindingitem.name.setText(data.allNameUser)
        bindingitem.service.setText(data.serviceName)
        bindingitem.dateStart.setText(data.date)
        bindingitem.timeStartAndEnd.setText(data.timeStartAndEnd)
        if (isShowTimer) {
            bindingitem.timerTitle.setVisibility(View.VISIBLE)
            bindingitem.timer.setVisibility(View.VISIBLE)
            startTimer()
        } else {
            bindingitem.timerTitle.setVisibility(View.GONE)
            bindingitem.timer.setVisibility(View.GONE)
        }
    }

    //        String d1 = TimesUtils.longToString(currentTimeAndDate, TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
//        String d21 = TimesUtils.longToString(dateTimeLongStart,TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
//        String d22 = TimesUtils.longToString(dateTimeLongStartMinusBeforeAdmission,TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
//        String d3 = TimesUtils.longToString(timeDataEnd,TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
    private val isShowTimer: Boolean
        private get() {
            val currentTimeAndDate = System.currentTimeMillis()
            val dateTimeLongStart = data!!.timeMills
            val dateTimeLongStartMinusBeforeAdmission =
                dateTimeLongStart - Constants.MIN_TIME_BEFORE_VIDEO_CALL * 60 * 1000
            val timeDataEnd = dateTimeLongStart + data!!.durationSec * 1000

//        String d1 = TimesUtils.longToString(currentTimeAndDate, TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
//        String d21 = TimesUtils.longToString(dateTimeLongStart,TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
//        String d22 = TimesUtils.longToString(dateTimeLongStartMinusBeforeAdmission,TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
//        String d3 = TimesUtils.longToString(timeDataEnd,TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy);
            return currentTimeAndDate > dateTimeLongStartMinusBeforeAdmission && currentTimeAndDate < timeDataEnd
        }

    private fun startTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
            myTimer = null
        }
        timer = Timer()
        myTimer = MyTimerClass()
        timer!!.schedule(myTimer, 0, 1000)
    }

    private fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
            myTimer = null
        }
    }

    internal inner class MyTimerClass : TimerTask() {
        override fun run() {
            if (!isShowTimer) {
                bindingitem.timerTitle.setVisibility(View.GONE)
                bindingitem.timer.setVisibility(View.GONE)
                stopTimer()
                return
            }
            val currentTimeAndDate = System.currentTimeMillis()
            val m1 =
                TimesUtils.longToString(currentTimeAndDate, TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy)
            val receptionTimeAndDate = data!!.timeMills
            val m2 = TimesUtils.longToString(
                receptionTimeAndDate,
                TimesUtils.DATE_FORMAT_HHmmss_ddMMyyyy
            )
            var timeLeft = receptionTimeAndDate - currentTimeAndDate
            timeLeft = timeLeft / 1000
            var isNegative = false
            if (timeLeft < 0) {
                isNegative = true
                timeLeft *= -1
            }
            val finalTimeLeft = timeLeft
            val finalIsNegative = isNegative
            (bindingitem.root.context as MainPageActivity?)!!.runOnUiThread {
                val hour = (finalTimeLeft / 3600).toString() + ""
                val min =
                    if (finalTimeLeft / 60 % 60 > 9) (finalTimeLeft / 60 % 60).toString() + "" else "0" + finalTimeLeft / 60 % 60
                val sec =
                    if (finalTimeLeft % 60 > 9) (finalTimeLeft % 60).toString() + "" else "0" + finalTimeLeft % 60
                var time = ""
                if (finalIsNegative) {
                    bindingitem.timer.setTextColor(Color.RED)
                    time += "- "
                } else {
                    bindingitem.timer.setTextColor(Color.BLACK)
                }
                time += (if (hour == "0") "" else "$hour:") + (if (hour == "0" && min == "00") "" else "$min:") + sec
                bindingitem.timer.setText(time)
            }
        }
    }

    fun onDestroy() {
        stopTimer()
    }
}