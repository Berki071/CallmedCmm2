package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class HasPacChatsResponse {
    @SerialName("error")
    var error = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: List<HasPacChatsItem> = ArrayList<HasPacChatsItem>()

    @Serializable
    class HasPacChatsItem {
        @SerialName("id_room")
        var idRoom: Int? = null
    }
}