package com.medhelp.callmedcmm2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ScheduleTomorrowResponse {
    @SerialName("error")
    var error = false
    @SerialName("message")
    var message: String? = null
    @SerialName("response")
    var response: List<ScheduleTomorrowItem> = ArrayList<ScheduleTomorrowItem>()
}