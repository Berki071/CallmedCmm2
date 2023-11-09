package com.medhelp.callmedcmm2.model.chat

import com.medhelp.callmedcmm2.model.ScheduleTomorrowResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FileForMsgResponse {
    @SerialName("error")
    var error = false
    @SerialName("message")
    var message: String? = null
    @SerialName("response")
    var response: List<Data> = ArrayList<Data>()

    @Serializable
    class Data{
        @SerialName("data_file")
        var data_file: String? = null
    }
}