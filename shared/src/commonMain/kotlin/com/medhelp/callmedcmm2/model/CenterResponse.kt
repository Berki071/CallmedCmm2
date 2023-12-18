package com.medhelp.callmedcmm2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CenterResponse {
    @SerialName("error")
    var error = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: List<CenterItem> = ArrayList<CenterItem>()

    @Serializable
    class CenterItem{
        @SerialName("id")
        var id:Int? = null
        @SerialName("id_center")
        var idCenter:Int? = null
        @SerialName("id_filial")
        var idFilial:Int? = null
        @SerialName("city")
        var city: String? = null
        @SerialName("time_zone")
        var timeZone:Int? = null
        @SerialName("title")
        var title: String? = null
        @SerialName("info")
        var info: String? = null
        @SerialName("logo")
        var logo: String? = null
        @SerialName("site")
        var site: String? = null
        @SerialName("phone")
        var phone: String? = null
        @SerialName("address")
        var address: String? = null
        @SerialName("time_otkaz")
        var timeForDenial:Int? = null
        @SerialName("time_podtvergd")
        var timeForConfirm:Int? = null
        @SerialName("komment_zapis")
        var komment_zapis: String? = null
        @SerialName("max_zapis")
        var max_zapis:Int? = null
        @SerialName("vers_kl")
        var vers_kl: String? = null
        @SerialName("vers_sotr")
        var vers_sotr: String? = null
        @SerialName("db_name")
        var db_name: String? = null
        @SerialName("ip_download")
        var ip_download: String? = null
    }
}