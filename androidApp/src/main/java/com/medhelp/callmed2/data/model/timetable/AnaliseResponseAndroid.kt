package com.medhelp.callmed2.data.model.timetable

import android.os.Parcel
import android.os.Parcelable
import com.medhelp.callmed2.utils.main.MDate
import com.medhelp.callmedcmm2.model.chat.AnaliseResponse

class AnaliseResponseAndroid() : AnaliseResponse(), Parcelable, Comparable<AnaliseResponseAndroid>{

    fun AnaliseResponse(date: String?, linkToPDF: String?) {
        this.date = date
        this.linkToPDF = linkToPDF
    }

    constructor(item: AnaliseResponse): this() {
        this.date = item.date
        this.linkToPDF = item.linkToPDF
    }

    constructor(parcel: Parcel) : this() {
        date=parcel.readString()
        linkToPDF=parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(linkToPDF)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnaliseResponseAndroid> {
        override fun createFromParcel(parcel: Parcel): AnaliseResponseAndroid {
            return AnaliseResponseAndroid(parcel)
        }

        override fun newArray(size: Int): Array<AnaliseResponseAndroid?> {
            return arrayOfNulls(size)
        }
    }

//    //для электронных заключений DataClassForElectronicRecy
//    fun getDateForZakl(): String? {
//        return date?.substring(date!!.length - 10)
//    }
//
//    fun getTitleItem(): String? {
//        return date?.substring(date!!.length - 10) + " " + date!!.substring(0, date!!.length - 14)
//    }

    override fun compareTo(o: AnaliseResponseAndroid): Int {
        val name1 = getDateForZakl()
        val name2 = o.getDateForZakl()
        if (name1 == null || name2 == null || name1 == "" || name2 == "" || name1 == name2) return 0
        val d1 = MDate.stringToLong(name1, MDate.DATE_FORMAT_ddMMyyyy)!!
        val d2 = MDate.stringToLong(name2, MDate.DATE_FORMAT_ddMMyyyy)!!
        return if (d2 > d1) 1 else if (d2 < d1) -1 else 0
    }
}