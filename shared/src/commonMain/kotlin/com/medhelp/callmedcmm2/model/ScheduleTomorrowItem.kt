package com.medhelp.callmedcmm2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ScheduleTomorrowItem {
    @SerialName("data")
    var data: String? = null
    @SerialName("start")
    var start: String? = null
    @SerialName("end")
    var end: String? = null
    @SerialName("kab")
    var kab: String? = null
    @SerialName("naim_filial")
    var naim_filial: String? = null
}