package com.medhelp.callmedcmm2.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class HasPacChatsItem {
    @SerialName("id_room")
    var idRoom: Int? = null
}