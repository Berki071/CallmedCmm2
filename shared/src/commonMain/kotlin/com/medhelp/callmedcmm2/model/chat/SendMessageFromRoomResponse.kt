package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SendMessageFromRoomResponse {
    @SerialName("error")
    var error = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: List<SendMessageFromRoomItem> = ArrayList<SendMessageFromRoomItem>()

    @Serializable
    class SendMessageFromRoomItem {
        @SerialName("id_message")
        var idMessage: Int? = null
        @SerialName("data_message")
        var dataMessage: String? = null
    }
}