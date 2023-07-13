package com.medhelp.callmed2.utils.main

import android.util.Log
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object MDate {
    const val DATE_FORMAT_HHmm = "HH:mm"
    const val DATE_FORMAT_LLLL_yyyyy = "LLLL yyyy"
    const val DATE_FORMAT_MMMM_yyyyy = "MMMM yyyy"
    const val DATE_FORMAT_EEE_d_MMMM = "EEE, d MMMM"
    const val DATE_FORMAT_EEE_d_MMM = "EEE, d MMM"
    const val DATE_FORMAT_ddMMyyyy = "dd.MM.yyyy"
    const val DATE_FORMAT_HHmmss_ddMMyyyy = "HH:mm:ss dd.MM.yyyy"
    const val DATE_FORMAT_WEEKDAY_EEEE = "EEEE"
    const val DATE_FORMAT_dd = "dd"
    const val DATE_FORMAT_mm = "mm"
    const val DATE_FORMAT_HHmm_ddMMyyyy = "HH:mm dd.MM.yyyy"
    const val DATE_FORMAT_MMyyyy = "MM.yyyy"
    const val DATE_FORMAT_ddMMyyyy_HHmm = "dd.MM.yyyy HH:mm"
    const val DATE_FORMAT_ddMMyyyy_HHmmss = "dd.MM.yyyy HH:mm:ss"

    const val DATE_FORMAT_ddMMyy = "dd.MM.yy"
    const val DATE_FORMAT_yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss"

    fun dateToString(date: Date?, newFormat: String?): String {
        val format: DateFormat = SimpleDateFormat(newFormat, Locale.getDefault())
        return format.format(date)
    }

    fun stringToString(date: String?, oldFormat: String?, newFormat: String?): String {
        var sdf = SimpleDateFormat(oldFormat)
        try {
            val dateN = sdf.parse(date)
            sdf = SimpleDateFormat(newFormat)
            return sdf.format(dateN)
        } catch (e: ParseException) {
            Log.wtf("", "")
        }
        return ""
    }

    fun stringToLondPlassDay(date: String?, formatDate: String?): Long {
        if (date == null) return 0L
        val sdf: DateFormat = SimpleDateFormat(formatDate)
        val dayLong = (1000 * 60 * 60 * 24).toLong()
        try {
            val dateD = sdf.parse(date)
            // long rr=dateD.getTime()+dayLong;
            return dateD.time + dayLong
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0L
    }

    @JvmStatic
    fun stringToLong(date: String?, formatDate: String?): Long {
        val sdf: DateFormat = SimpleDateFormat(formatDate)
        //long dayLong=1000*60*60*24;
        try {
            val dateD = sdf.parse(date)
            // long rr=dateD.getTime()+dayLong;
            return dateD.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0L
    }

    fun longToString(date: Long, amount: String?): String {
        val sdf = SimpleDateFormat(amount)
        val d = Date(date)
        return sdf.format(d)
    }

    val curentDate: String
        get() {
            val sdf: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMyyyy)
            val currentTime = Calendar.getInstance().time
            return sdf.format(currentTime)
        }

    val currenDateLong: Long
        get() = stringToLong(curentDate, DATE_FORMAT_ddMMyyyy)
    fun getCurrentDate(): Long {
        //return System.currentTimeMillis()
        val currentTime = Calendar.getInstance().time
        return currentTime.time
    }

    val curentTime: String
        get() {
            val sdf: DateFormat = SimpleDateFormat(DATE_FORMAT_HHmm)
            val currentTime = Calendar.getInstance().time
            return sdf.format(currentTime)
        }
    val currentTimeLongWithRounding: Long
        get() {
            val currentTime = Calendar.getInstance().time
            val currentMin = Integer.valueOf(dateToString(currentTime, DATE_FORMAT_mm))
            var tmp = currentMin % 5
            tmp = 5 - tmp
            val time = longToString(currentTime.time, DATE_FORMAT_HHmm)
            return stringToLondPlassDay(time, DATE_FORMAT_HHmm) + tmp * 1000 * 60
        }

    fun getThreeDay(dayStart: String): List<String> {
        val list: MutableList<String> = ArrayList()
        list.add(dayStart)
        val d2 = longToString(
            stringToLondPlassDay(dayStart, DATE_FORMAT_ddMMyyyy) + 1000 * 60 * 60,
            DATE_FORMAT_ddMMyyyy
        )
        val d3 = longToString(
            stringToLondPlassDay(dayStart, DATE_FORMAT_ddMMyyyy) + 1000 * 60 * 60 * 24,
            DATE_FORMAT_ddMMyyyy
        )
        list.add(d2)
        list.add(d3)
        return list
    }

    fun isEvenDay(date: String?): Boolean {
        var sdf: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMyyyy)
        try {
            val dateD = sdf.parse(date)
            sdf = SimpleDateFormat(DATE_FORMAT_dd)
            val s = sdf.format(dateD)
            return if (Integer.valueOf(s) % 2 == 0) true else false
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun isUnevenDay(date: String?): Boolean {
        var sdf: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMyyyy)
        try {
            val dateD = sdf.parse(date)
            sdf = SimpleDateFormat(DATE_FORMAT_dd)
            val s = sdf.format(dateD)
            return if (Integer.valueOf(s) % 2 != 0) true else false
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun isIncludedInProportion(
        dateStart: String?,
        searchDay: String?,
        proportionWork: Int,
        proportionDayOff: Int
    ): Boolean {
        var dayStartLong = stringToLong(dateStart, DATE_FORMAT_ddMMyyyy)
        val daySerchLong = stringToLong(searchDay, DATE_FORMAT_ddMMyyyy)
        val step = proportionWork + proportionDayOff
        if (daySerchLong < dayStartLong) return false
        if (proportionWork == 0 || proportionDayOff == 0) return false
        for (i in 1000 downTo 1) {
            val tmp = dayStartLong + 1000 * 60 * 60 * 24 * step - 1
            val s1 = longToString(daySerchLong, DATE_FORMAT_ddMMyyyy)
            val s2 = longToString(dayStartLong, DATE_FORMAT_ddMMyyyy)
            val s3 = longToString(tmp, DATE_FORMAT_ddMMyyyy)
            dayStartLong = if (daySerchLong >= dayStartLong && daySerchLong < tmp) {
                val endPropWork = dayStartLong + 1000 * 60 * 60 * 24 * proportionWork - 1
                return if (daySerchLong >= dayStartLong && daySerchLong < endPropWork) true else false
            } else {
                tmp + 1
            }
        }
        return false
    }

    fun getDayWeek(date: String?): String {
        var sdf: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMyyyy)
        try {
            val dateD = sdf.parse(date)
            sdf = SimpleDateFormat(DATE_FORMAT_WEEKDAY_EEEE)
            return sdf.format(dateD)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun threeDayForward(date: String?): String {
        return longToString(
            stringToLondPlassDay(
                date,
                DATE_FORMAT_ddMMyyyy
            ) + 1000 * 60 * 60 * 24 * 2, DATE_FORMAT_ddMMyyyy
        )
    }

    fun threeDayBack(date: String?): String {
        return longToString(
            stringToLondPlassDay(
                date,
                DATE_FORMAT_ddMMyyyy
            ) - 1000 * 60 * 60 * 24 * 4, DATE_FORMAT_ddMMyyyy
        )
    }

    fun getMondayLongByDate(date: String?): Long {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = stringToLong(date, DATE_FORMAT_ddMMyyyy)
        while (calendar[Calendar.DAY_OF_WEEK] > calendar.firstDayOfWeek) {
            calendar.add(Calendar.DATE, -1) // Substract 1 day until first day of week.
        }
        return calendar.timeInMillis
    }

    fun getMondayStringByDate(date: String?): String {
        return longToString(getMondayLongByDate(date), DATE_FORMAT_ddMMyyyy)
    }

    fun getFirstDayMonthInLong(date: String?): Long {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = stringToLong(date, DATE_FORMAT_ddMMyyyy)
        while (calendar[Calendar.DATE] > 1) {
            calendar.add(Calendar.DATE, -1) // Substract 1 day until first day of month.
        }
        return calendar.timeInMillis
    }

    fun getFirstDayMonthInString(date: String?): String {
        return longToString(getFirstDayMonthInLong(date), DATE_FORMAT_ddMMyyyy)
    }

    fun getFirstDayPastMonthInLong(date: String?): Long {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = stringToLong(date, DATE_FORMAT_ddMMyyyy)
        while (calendar[Calendar.DATE] > 1) {
            calendar.add(Calendar.DATE, -1) // Substract 1 day until first day of month.
        }
        calendar.add(Calendar.MONTH, -1)
        return calendar.timeInMillis
    }

    fun getFirstDayPastMonthInString(date: String?): String {
        return longToString(getFirstDayPastMonthInLong(date), DATE_FORMAT_ddMMyyyy)
    }

    fun getLastDayMonthInLong(date: String?): Long {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = stringToLong(date, DATE_FORMAT_ddMMyyyy)
        calendar[Calendar.DATE] = calendar.getActualMaximum(Calendar.DATE)
        return calendar.timeInMillis
    }

    fun getLastDayMonthInString(date: String?): String {
        return longToString(getLastDayMonthInLong(date), DATE_FORMAT_ddMMyyyy)
    }

    fun getNumberOfDaysBefore(dayTo: String): Int {
        val curDay = curentDate
        if (dayTo == curDay) return 0
        val dayToLong = stringToLong(dayTo, DATE_FORMAT_ddMMyyyy)
        val curDayLong = stringToLong(curDay, DATE_FORMAT_ddMMyyyy)
        val difference = dayToLong - curDayLong
        return if (difference < 0) -1 else (difference / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getStringWeek(date: Long?): List<String> {
        val listDate: MutableList<String> = ArrayList()
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = date!!
        while (calendar[Calendar.DAY_OF_WEEK] != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1) // Substract 1 day until first day of week.
        }
        listDate.add(longToString(calendar.timeInMillis, DATE_FORMAT_ddMMyyyy))
        for (i in 0..5) {
            calendar.add(Calendar.DATE, 1)
            listDate.add(longToString(calendar.timeInMillis, DATE_FORMAT_ddMMyyyy))
        }
        return listDate
    }

    fun getFirstDayWeek(date: Long): String {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = date
        while (calendar[Calendar.DAY_OF_WEEK] > calendar.firstDayOfWeek) {
            calendar.add(Calendar.DATE, -1) // Substract 1 day until first day of week.
        }
        return longToString(calendar.timeInMillis, DATE_FORMAT_ddMMyyyy)
    }

    fun isTheTimeConfirm(time2: String, date: String, intervalMin: Int): Boolean {
        val analysis = stringToLong("$time2 $date", DATE_FORMAT_HHmm_ddMMyyyy)
        // analysis=TimesUtils.localLongToUtcLong(analysis);
        val tDay = Calendar.getInstance().time.time
        val timeTo = 1000 * 60 * intervalMin

//        String d1 = longToString(analysis, DATE_FORMAT_HHmmss_ddMMyyyy);
//        String d11 = longToString(tDay, DATE_FORMAT_HHmmss_ddMMyyyy);
//
//        String d3 = longToString(analysis-timeTo, DATE_FORMAT_HHmmss_ddMMyyyy);
//        boolean boo = tDay<analysis && tDay>analysis-timeTo;

        //return analysis >= tDay && analysis <= (tDay + timeTo);
        return tDay < analysis && tDay > analysis - timeTo
    }
}