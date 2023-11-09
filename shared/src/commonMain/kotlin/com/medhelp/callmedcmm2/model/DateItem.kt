package com.medhelp.callmedcmm2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DateResponse {
    @SerialName("error")
    var isError = false

    @SerialName("message")
    var message: String? = null

    @SerialName("response")
    var response: DateItem? = null

    @Serializable
    class DateItem {
        @SerialName("today")
        var today: String? = null

        @SerialName("week_day")
        var weekDay: String? = null

        @SerialName("last_monday")
        var lastMonday: String? = null

        constructor()
        constructor(lastMonday: String?) {
            this.lastMonday = lastMonday
        }
    }
}