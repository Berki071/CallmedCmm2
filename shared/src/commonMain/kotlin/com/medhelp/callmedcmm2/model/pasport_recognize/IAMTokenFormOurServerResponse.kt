package com.medhelp.callmedcmm2.model.pasport_recognize

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class IAMTokenFormOurServerResponse {
    @SerialName("error")
    var error = false
    @SerialName("message")
    var message: String? = null
    @SerialName("response")
    var response: List<IAMTokenFormOurServerItem> = ArrayList<IAMTokenFormOurServerItem>()

    @Serializable
    class IAMTokenFormOurServerItem{
        @SerialName("recognize_token")
        var recognize_token: String? = null
    }
}