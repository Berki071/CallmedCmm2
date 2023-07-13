package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SendMessageFromRoomItem {
    @SerialName("id_message")
    var idMessage: Int? = null
    @SerialName("data_message")
    var dataMessage: String? = null
}
