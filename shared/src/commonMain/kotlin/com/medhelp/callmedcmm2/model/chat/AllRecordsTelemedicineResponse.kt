package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class AllRecordsTelemedicineResponse {
    @SerialName("error")
    var error = false
    @SerialName("message")
    var message: String? = null
    @SerialName("response")
    var response: List<AllRecordsTelemedicineItem> = ArrayList()

    @Serializable
    open class AllRecordsTelemedicineItem() {
        @SerialName("server_key")
        var serverKey: String? = null
        @SerialName("data_server")
        var dataServer: String? = null
        @SerialName("id_room")
        var idRoom: Int? = null
        @SerialName("status")
        var status: String? = null
        @SerialName("id_kl")
        var idKl: Int? = null
        @SerialName("id_filial")
        var idFilial: Int? = null
        @SerialName("token_kl")
        var token_kl: String? = null

        @SerialName("specialty")
        var specialty: String? = null
        @SerialName("full_name_kl")
        var fullNameKl: String? = null
        @SerialName("dr_kl")
        var drKl: String? = null
        @SerialName("komment_kl")
        var kommentKl: String? = null
        @SerialName("fcm_kl")
        var fcmKl: String? = null
        @SerialName("tm_id")
        var tmId: Int? = null
        @SerialName("tm_name")
        var tmName: String? = null
        @SerialName("tm_type")
        var tmType: String? = null
        @SerialName("tm_price")
        var tmPrice: Int? = null
        @SerialName("tm_time_for_tm")
        var tmTimeForTm: Int? = null
        @SerialName("time_start_after_pay")
        var timeStartAfterPay: Int? = null
        @SerialName("data_start")
        var dataStart: String? = null
        @SerialName("data_end")
        var dataEnd: String? = null
        @SerialName("data_pay")
        var dataPay: String? = null
        @SerialName("status_pay")
        var statusPay: String? = null
        @SerialName("about")
        var aboutShort: String? = null
        @SerialName("about_full")
        var aboutfull: String? = null
        @SerialName("notif_24")
        var notif_24: String? = null
        @SerialName("notif_12")
        var notif_12: String? = null
        @SerialName("notif_4")
        var notif_4: String? = null
        @SerialName("notif_1")
        var notif_1: String? = null

        //для айос
        var isShowNewMsgIco= false

        constructor(item: AllRecordsTelemedicineItem): this() {
            this.serverKey = item.serverKey
            this.dataServer = item.dataServer
            this.idRoom = item.idRoom
            this.status = item.status
            this.idKl = item.idKl
            this.idFilial = item.idFilial
            this.token_kl = token_kl

            this.specialty = item.specialty
            this.fullNameKl = item.fullNameKl
            this.drKl = item.drKl
            this.kommentKl = item.kommentKl
            this.fcmKl = item.fcmKl
            this.tmId = item.tmId
            this.tmName = item.tmName
            this.tmType = item.tmType
            this.tmPrice = item.tmPrice
            this.tmTimeForTm = item.tmTimeForTm
            this.timeStartAfterPay = item.timeStartAfterPay
            this.dataStart = item.dataStart
            this.dataEnd = item.dataEnd
            this.dataPay = item.dataPay
            this.statusPay = item.statusPay
            this.aboutShort = item.aboutShort
            this.aboutfull = item.aboutfull

            this.notif_24 = item.notif_24
            this.notif_12 = item.notif_12
            this.notif_4 = item.notif_4
            this.notif_1 = item.notif_1
            //this.isShowNewMsgIco = item.isShowNewMsgIco
        }
    }
}