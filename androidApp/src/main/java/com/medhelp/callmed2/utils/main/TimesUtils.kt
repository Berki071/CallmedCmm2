package com.medhelp.callmed2.utils.main

import com.medhelp.callmed2.utils.timber_log.LoggingTree.Companion.getMessageForError
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimesUtils {
    // спользовался до появления MDate.. перевести все на него
    const val DATE_FORMAT_HHmmss_ddMMyyyy = "HH:mm:ss dd.MM.yyyy"
    const val DATE_FORMAT_HHmm = "HH:mm"
    const val DATE_FORMAT_HHmm_ddMMyyyy = "HH:mm dd.MM.yyyy"
    const val DATE_FORMAT_ddMMyyyy = "dd.MM.yyyy"
    const val DATE_FORMAT_dd_MMMM_yyyy = "dd MMMM yyyy"
    const val DATE_FORMAT_HHmmss = "HH:mm:ss"
    fun getTime(data: String?): String {
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var date: Date? = null
        try {
            date = format.parse(data)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(date != null)
        val dataLong = date!!.time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(Date(dataLong))
    }

    fun getDateFromTime(time: String?): Date? {
        val format: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            return format.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    fun getMillisFromTime(time: String?): Long {
        val f = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            val d = f.parse(time)
            d.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }

    fun getMillisOfDateString(time: String?): Long {
        val f = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return try {
            val d = f.parse(time)
            d.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }

    fun getMillisOfDateString2(time: String?): Long {
        val f = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault())
        val f2 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return try {
            val d = f.parse(time)
            val s = f2.format(d)
            val d2 = f2.parse(s)
            d2.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }

    fun getMillisFromVisit(admDate: String?, time: String?): Long {
        val formatDay = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formatHours = SimpleDateFormat("hh:mm", Locale.getDefault())
        return try {
            val h = formatHours.parse(time)
            val calendar = GregorianCalendar.getInstance()
            calendar.time = h
            val hh = calendar[Calendar.HOUR_OF_DAY] * 60 * 60 * 1000 // gets hour in 24h format
            val m = calendar[Calendar.MINUTE] * 60 * 1000
            val d = formatDay.parse(admDate)
            d.time + hh + m
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }

    fun getDateSchedule(data: String?): Date? {
        val format: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        try {
            return format.parse(data)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getDateSchedule(data: Date?): String {
        val format: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.format(data)
    }

    fun getDateSchedule2(data: Date?): String {
        val format: DateFormat = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault())
        return format.format(data)
    }

    fun transformStringDate(admDate: String?): String {
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var date: Date? = null
        try {
            date = format.parse(admDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(date != null)
        val dataLong = date!!.time
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(Date(dataLong))
    }

    @JvmStatic
    val currrentDate: String
        get() {
            val mill = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = Date(mill)
            return dateFormat.format(date)
        }

    @JvmStatic
    fun localLongToUtcLong(local: Long): Long {
        val loc = TimeZone.getDefault()
        val formatter = SimpleDateFormat(DATE_FORMAT_HHmmss_ddMMyyyy)
        val lData = Date(local - loc.rawOffset)
        return lData.time
    }

    @JvmStatic
    fun clipHHmmss(date: Long): Long {
        val dateFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_ddMMyyyy)
        var d = Date(date)
        val newD = dateFormat.format(d)
        try {
            d = dateFormat.parse(newD)
            return d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    @JvmStatic
    fun clip_before_ddMMyyyyy(data: Long): String {
        val df: DateFormat = SimpleDateFormat(DATE_FORMAT_dd_MMMM_yyyy)
        val d = Date(data)
        return df.format(d)
    }

    fun UtcLongToStrLocal(date: Long): String {
        val loc = TimeZone.getDefault()
        val formatter = SimpleDateFormat(DATE_FORMAT_HHmmss_ddMMyyyy)
        val lDate = Date(date + loc.rawOffset)
        return formatter.format(lDate)
    }

    fun getNewFormatDateString(data: String?, newFormat: String?): String {
        val format: DateFormat = SimpleDateFormat(DATE_FORMAT_HHmmss_ddMMyyyy)
        var date: Date? = null
        try {
            date = format.parse(data)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(date != null)
        val dataLong = date!!.time
        val dateFormat = SimpleDateFormat(newFormat)
        return dateFormat.format(Date(dataLong))
    }

    fun getNewFormatString(data: String?, oldformat: String?, newFormat: String?): String {
        val format: DateFormat = SimpleDateFormat(oldformat)
        var date: Date? = null
        try {
            date = format.parse(data)
        } catch (e: ParseException) {
            Timber.e(getMessageForError(e, "TimesUtils"))
        }
        assert(date != null)
        val dataLong = date!!.time
        val dateFormat = SimpleDateFormat(newFormat)
        return dateFormat.format(Date(dataLong))
    }

    @JvmStatic
    fun stringToLong(date: String?, format: String?): Long? {
        val sdf = SimpleDateFormat(format)
        try {
            val data = sdf.parse(date)
            return data.time
        } catch (e: ParseException) {
            Timber.e(getMessageForError(e, "TimesUtils"))
        }
        return null
    }

    @JvmStatic
    fun longToString(date: Long, format: String?): String {
        val df: DateFormat = SimpleDateFormat(format)
        val d = Date(date)
        return df.format(d)
    }

    fun stringToDate(data: String?): Date? {
        val format: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        try {
            return format.parse(data)
        } catch (e: ParseException) {
            Timber.e(getMessageForError(e, "TimesUtils"))
        }
        return null
    }

    fun longToNewFormatLong(date: Long, format: String?): Long {
        val dateFormat: DateFormat = SimpleDateFormat(format)
        var d = Date(date)
        val newD = dateFormat.format(d)
        try {
            d = dateFormat.parse(newD)
            return d.time
        } catch (e: ParseException) {
            Timber.e(getMessageForError(e, "TimesUtils"))
        }
        return 0
    }

    fun dateToString(data: Date?, newFormat: String?): String {
        val format: DateFormat = SimpleDateFormat(newFormat, Locale.getDefault())
        return format.format(data)
    }

    fun getMillisFromDateString(time: String?): Long {
        val f = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault())
        val f2 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return try {
            val d = f.parse(time)
            val s = f2.format(d)
            val d2 = f2.parse(s)
            d2.time
        } catch (e: ParseException) {
            Timber.e(getMessageForError(e, "TimesUtils"))
            0L
        }
    }

    fun compareWhithCurentTimeByDate(data2: String?): Int {
        val mill = System.currentTimeMillis()
        val dateClear = longToNewFormatLong(mill, DATE_FORMAT_ddMMyyyy)
        val date2Clear = stringToLong(data2, DATE_FORMAT_ddMMyyyy)
        if(date2Clear == null)
            return 0

        return if (dateClear > date2Clear) -1 else if (dateClear < date2Clear) 1 else 0
    }
}