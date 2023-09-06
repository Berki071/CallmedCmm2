package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AnaliseResultResponse {
    @SerialName("imgError")
    var error = false

    @SerialName("Message")
    var message: String? = null

    @SerialName("response")
    var response: List<AnaliseResponse> = ArrayList()  //AnaliseResponseAndroid
}