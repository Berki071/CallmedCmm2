package com.medhelp.callmedcmm2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class VisitResponse {
    @SerialName("error")
    var isError = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: List<VisitItem> = ArrayList()

    @Serializable
    class VisitItem {
        @SerialName("kab")
        var kab: String? = null

        @SerialName("date")
        var date: String? = null

        @SerialName("time")
        var time: String? = null

        @SerialName("fam_kl")
        var fam_kl: String? = null

        @SerialName("name_kl")
        var name_kl: String? = null

        @SerialName("otch_kl")
        var otch_kl: String? = null

        @SerialName("komment")
        var komment: String? = null

        @SerialName("id_client")
        var id_client: Int? = null

        @SerialName("naim")
        var naim: String? = null

        @SerialName("start")
        var start: String? = null

        @SerialName("end")
        var end: String? = null
        val fullName: String
            get() = "$fam_kl $name_kl $otch_kl"

        constructor(time: String, am_kl: String, name_kl: String, otch_kl: String, naim: String, komment: String){
            this.time = time
            this.fam_kl=fam_kl
            this.name_kl=name_kl
            this.otch_kl=otch_kl
            this.naim=naim
            this.komment=komment
        }

//        override operator fun compareTo(o: Any): Int {
//            val dateCurrent = stringToLong(date, TimesUtils.DATE_FORMAT_ddMMyyyy)!!
//            val dateObj = stringToLong((o as VisitResponse).date, TimesUtils.DATE_FORMAT_ddMMyyyy)!!
//            return if (dateCurrent > dateObj) 1 else if (dateCurrent < dateObj) -1 else {
//                val timeCurrent = stringToLong(time, TimesUtils.DATE_FORMAT_HHmm)!!
//                val timeObj =
//                    stringToLong(o.time, TimesUtils.DATE_FORMAT_HHmm)!!
//                java.lang.Long.compare(timeCurrent, timeObj)
//            }
//        }
    }
}