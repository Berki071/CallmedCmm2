package com.medhelp.callmedcmm2.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserResponse {
    @SerialName("error")
    var error = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: List<UserItem> = ArrayList<UserItem>()

    @Serializable
    class UserItem{
        @SerialName("id_doctor")  //хз куда, пока ненужен
        val idDoctor:Int? = null

        @SerialName("id_doc_center")
        val idUser:Int? = null

        @SerialName("id_center")
        val idCenter:Int? = null

        @SerialName("username")
        val username: String? = null

        @SerialName("fb_key")     // хз куда
        val fbKey: String? = null

        @SerialName("token")
        val apiKey: String? = null

        @SerialName("callcenter")
        val showPartCallCenter: String? = null

        @SerialName("chat")
        val showPartMessenger: String? = null

        @SerialName("rasp") //запись
        val timetable: String? = null

        @SerialName("vrach") //расписание
        val vrach: String? = null

        @SerialName("sync") //сканер док
        val sync: String? = null

        @SerialName("recognize") //распознавание паспорта
        val buttonRecognize: String? = null

    }
}