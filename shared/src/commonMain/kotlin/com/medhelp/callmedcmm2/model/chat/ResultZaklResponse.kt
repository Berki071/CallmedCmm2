package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ResultZaklResponse {
    @SerialName("error")
    var error = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: List<AnaliseResponse> = ArrayList<AnaliseResponse>()
}