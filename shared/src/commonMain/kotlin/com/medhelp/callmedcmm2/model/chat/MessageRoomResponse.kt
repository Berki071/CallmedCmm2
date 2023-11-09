package com.medhelp.callmedcmm2.model.chat

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageRoomResponse {
    @SerialName("error")
    var error = false
    @SerialName("message")
    var message: String? = null
    @SerialName("response")
    var response: List<MessageRoomItem> = ArrayList<MessageRoomItem>()

    @Serializable
    open class MessageRoomItem : RealmObject {
        @PrimaryKey
        var _id: ObjectId = ObjectId.create()
        @SerialName("id_message")
        var idMessage: Int? = null
        @SerialName("id_room")
        var idRoom: String? = null
        @SerialName("name_tm")
        var nameTm: String? = null
        @SerialName("id_tm")
        var idTm: Int? = null
        @SerialName("data")
        var data: String? = null
        @SerialName("text")
        var text: String? = null
        @SerialName("otpravitel")
        var otpravitel: String? = null  //kl
        @SerialName("view_kl")
        var viewKl: String? = null
        @SerialName("view_sotr")
        var viewSotr: String? = null  //bool in String
        @SerialName("type")
        var type: String? = null

        @Ignore
        var isShowLoading = false


        //возможно для ios
        fun idAsString() : String {
            return _id.toString()
        }
    }
}